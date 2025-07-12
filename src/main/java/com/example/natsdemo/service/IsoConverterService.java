package com.example.natsdemo.service;

import com.example.natsdemo.model.Iso8583Message;
import com.example.natsdemo.model.Iso20022Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service for converting ISO 8583 messages to ISO 20022 format.
 * This is a simplified implementation for demonstration purposes.
 */
@Service
public class IsoConverterService {

    private static final Logger logger = LoggerFactory.getLogger(IsoConverterService.class);
    
    private final NatsService natsService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public IsoConverterService(NatsService natsService) {
        this.natsService = natsService;
        this.objectMapper = new ObjectMapper();
        
        // Subscribe to ISO 8583 messages for conversion
        subscribeToIso8583Messages();
    }
    
    /**
     * Subscribes to ISO 8583 messages and converts them to ISO 20022.
     */
    private void subscribeToIso8583Messages() {
        natsService.subscribeToSubject("iso8583.messages", (subject, message) -> {
            logger.info("Received ISO 8583 message on subject '{}': {}", subject, message);
            
            try {
                // Parse the ISO 8583 message
                Iso8583Message iso8583Message = objectMapper.readValue(message, Iso8583Message.class);
                
                // Convert to ISO 20022
                Iso20022Message iso20022Message = convertIso8583ToIso20022(iso8583Message);
                
                // Convert to JSON and publish
                String iso20022Json = objectMapper.writeValueAsString(iso20022Message);
                natsService.publishMessage("iso20022.messages", iso20022Json);
                
                logger.info("Successfully converted ISO 8583 to ISO 20022 and published to 'iso20022.messages'");
                
            } catch (Exception e) {
                logger.error("Error converting ISO 8583 to ISO 20022: {}", message, e);
            }
        });
        
        logger.info("Subscribed to ISO 8583 messages for conversion");
    }
    
    /**
     * Converts an ISO 8583 message to ISO 20022 format.
     * This is a simplified conversion for demonstration purposes.
     * 
     * @param iso8583Message The ISO 8583 message to convert
     * @return The converted ISO 20022 message
     */
    public Iso20022Message convertIso8583ToIso20022(Iso8583Message iso8583Message) {
        logger.debug("Converting ISO 8583 message: {}", iso8583Message);
        
        Iso20022Message iso20022Message = new Iso20022Message();
        
        // Set basic message information
        iso20022Message.setMsgId(generateMessageId());
        iso20022Message.setCreDtTm(getCurrentDateTime());
        iso20022Message.setNbOfTxs("1");
        iso20022Message.setCtrlSum(iso8583Message.getAmount());
        
        // Set Initiating Party
        Iso20022Message.InitiatingParty initgPty = new Iso20022Message.InitiatingParty();
        initgPty.setNm("Demo Bank");
        
        Iso20022Message.PartyIdentification partyId = new Iso20022Message.PartyIdentification();
        Iso20022Message.OrganisationIdentification orgId = new Iso20022Message.OrganisationIdentification();
        Iso20022Message.GenericIdentification genId = new Iso20022Message.GenericIdentification();
        genId.setId("DEMO001");
        
        Iso20022Message.SchemeName schemeName = new Iso20022Message.SchemeName();
        schemeName.setCd("BANK");
        genId.setSchmeNm(schemeName);
        
        orgId.setOthr(genId);
        partyId.setOrgId(orgId);
        initgPty.setId(partyId);
        iso20022Message.setInitgPty(initgPty);
        
        // Set Payment Information
        Iso20022Message.PaymentInformation pmtInf = new Iso20022Message.PaymentInformation();
        pmtInf.setPmtInfId(generatePaymentInfoId());
        pmtInf.setPmtMtd("TRF");
        pmtInf.setBtchBookg("true");
        pmtInf.setNbOfTxs("1");
        pmtInf.setCtrlSum(iso8583Message.getAmount());
        pmtInf.setReqdExctnDt(getCurrentDate());
        
        // Set Payment Type Information
        Iso20022Message.PaymentTypeInformation pmtTpInf = new Iso20022Message.PaymentTypeInformation();
        Iso20022Message.ServiceLevel svcLvl = new Iso20022Message.ServiceLevel();
        svcLvl.setCd("SEPA");
        pmtTpInf.setSvcLvl(svcLvl);
        pmtInf.setPmtTpInf(pmtTpInf);
        
        // Set Debtor (from PAN)
        Iso20022Message.PartyIdentification dbtr = new Iso20022Message.PartyIdentification();
        Iso20022Message.OrganisationIdentification dbtrOrgId = new Iso20022Message.OrganisationIdentification();
        Iso20022Message.GenericIdentification dbtrGenId = new Iso20022Message.GenericIdentification();
        dbtrGenId.setId(iso8583Message.getPan());
        dbtrGenId.setSchmeNm(schemeName);
        dbtrOrgId.setOthr(dbtrGenId);
        dbtr.setOrgId(dbtrOrgId);
        pmtInf.setDbtr(dbtr);
        
        // Set Debtor Account
        Iso20022Message.CashAccount dbtrAcct = new Iso20022Message.CashAccount();
        Iso20022Message.AccountIdentification dbtrAcctId = new Iso20022Message.AccountIdentification();
        Iso20022Message.GenericIdentification dbtrAcctGenId = new Iso20022Message.GenericIdentification();
        dbtrAcctGenId.setId(iso8583Message.getPan());
        dbtrAcctId.setOthr(dbtrAcctGenId);
        dbtrAcct.setId(dbtrAcctId);
        pmtInf.setDbtrAcct(dbtrAcct);
        
        // Set Debtor Agent
        Iso20022Message.BranchAndFinancialInstitutionIdentification dbtrAgt = 
            new Iso20022Message.BranchAndFinancialInstitutionIdentification();
        Iso20022Message.FinancialInstitutionIdentification dbtrFinInstnId = 
            new Iso20022Message.FinancialInstitutionIdentification();
        dbtrFinInstnId.setBicfi("DEMOBANK");
        dbtrAgt.setFinInstnId(dbtrFinInstnId);
        pmtInf.setDbtrAgt(dbtrAgt);
        
        // Set Credit Transfer Transaction Information
        Iso20022Message.CreditTransferTransactionInformation cdtTrfTxInf = 
            new Iso20022Message.CreditTransferTransactionInformation();
        
        // Set Payment Identification
        Iso20022Message.PaymentIdentification pmtId = new Iso20022Message.PaymentIdentification();
        pmtId.setInstrId(generateInstructionId());
        pmtId.setEndToEndId(iso8583Message.getStan());
        cdtTrfTxInf.setPmtId(pmtId);
        
        // Set Amount
        Iso20022Message.Amount amt = new Iso20022Message.Amount();
        amt.setInstdAmt(iso8583Message.getAmount());
        cdtTrfTxInf.setAmt(amt);
        
        // Set Creditor Agent
        Iso20022Message.BranchAndFinancialInstitutionIdentification cdtrAgt = 
            new Iso20022Message.BranchAndFinancialInstitutionIdentification();
        Iso20022Message.FinancialInstitutionIdentification cdtrFinInstnId = 
            new Iso20022Message.FinancialInstitutionIdentification();
        cdtrFinInstnId.setBicfi("TARGETBANK");
        cdtrAgt.setFinInstnId(cdtrFinInstnId);
        cdtTrfTxInf.setCdtrAgt(cdtrAgt);
        
        // Set Creditor
        Iso20022Message.PartyIdentification cdtr = new Iso20022Message.PartyIdentification();
        Iso20022Message.OrganisationIdentification cdtrOrgId = new Iso20022Message.OrganisationIdentification();
        Iso20022Message.GenericIdentification cdtrGenId = new Iso20022Message.GenericIdentification();
        cdtrGenId.setId("TARGET001");
        cdtrGenId.setSchmeNm(schemeName);
        cdtrOrgId.setOthr(cdtrGenId);
        cdtr.setOrgId(cdtrOrgId);
        cdtTrfTxInf.setCdtr(cdtr);
        
        // Set Creditor Account
        Iso20022Message.CashAccount cdtrAcct = new Iso20022Message.CashAccount();
        Iso20022Message.AccountIdentification cdtrAcctId = new Iso20022Message.AccountIdentification();
        Iso20022Message.GenericIdentification cdtrAcctGenId = new Iso20022Message.GenericIdentification();
        cdtrAcctGenId.setId("TARGETACCOUNT");
        cdtrAcctId.setOthr(cdtrAcctGenId);
        cdtrAcct.setId(cdtrAcctId);
        cdtTrfTxInf.setCdtrAcct(cdtrAcct);
        
        pmtInf.setCdtTrfTxInf(cdtTrfTxInf);
        iso20022Message.setPmtInf(pmtInf);
        
        logger.debug("Converted to ISO 20022 message: {}", iso20022Message);
        return iso20022Message;
    }
    
    /**
     * Generates a unique message ID.
     */
    private String generateMessageId() {
        return "MSG" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Generates a unique payment information ID.
     */
    private String generatePaymentInfoId() {
        return "PMT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Generates a unique instruction ID.
     */
    private String generateInstructionId() {
        return "INS" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Gets the current date time in ISO format.
     */
    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }
    
    /**
     * Gets the current date in ISO format.
     */
    private String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    /**
     * Publishes a test ISO 8583 message for conversion testing.
     */
    public void publishTestIso8583Message() {
        try {
            Iso8583Message testMessage = new Iso8583Message();
            testMessage.setMti("0200");
            testMessage.setPan("4111111111111111");
            testMessage.setProcessingCode("000000");
            testMessage.setAmount("100.00");
            testMessage.setStan("123456");
            testMessage.setTime("143022");
            testMessage.setDate("0710");
            testMessage.setMerchantType("5411");
            testMessage.setPosEntryMode("021");
            testMessage.setCardSequence("001");
            testMessage.setAcquirerId("123456");
            testMessage.setForwarderId("654321");
            testMessage.setTrack2("4111111111111111=123456789012345");
            testMessage.setRetrievalRef("123456789012");
            testMessage.setAuthCode("123456");
            testMessage.setResponseCode("00");
            testMessage.setCardAcceptor("12345678");
            testMessage.setCardAcceptorId("123456789012345");
            testMessage.setCardAcceptorName("DEMO MERCHANT");
            testMessage.setCurrencyCode("840");
            
            String testMessageJson = objectMapper.writeValueAsString(testMessage);
            natsService.publishMessage("iso8583.messages", testMessageJson);
            
            logger.info("Published test ISO 8583 message for conversion");
            
        } catch (Exception e) {
            logger.error("Error publishing test ISO 8583 message", e);
        }
    }
} 