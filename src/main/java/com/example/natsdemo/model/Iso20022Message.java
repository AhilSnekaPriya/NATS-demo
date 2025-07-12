package com.example.natsdemo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simplified ISO 20022 message model for testing purposes.
 * This represents a Payment Initiation message (pain.001) structure.
 */
public class Iso20022Message {
    
    @JsonProperty("MsgId")
    private String msgId; // Message Identification
    
    @JsonProperty("CreDtTm")
    private String creDtTm; // Creation Date Time
    
    @JsonProperty("NbOfTxs")
    private String nbOfTxs; // Number Of Transactions
    
    @JsonProperty("CtrlSum")
    private String ctrlSum; // Control Sum
    
    @JsonProperty("InitgPty")
    private InitiatingParty initgPty; // Initiating Party
    
    @JsonProperty("PmtInf")
    private PaymentInformation pmtInf; // Payment Information
    
    // Nested classes for ISO 20022 structure
    public static class InitiatingParty {
        @JsonProperty("Nm")
        private String nm; // Name
        
        @JsonProperty("Id")
        private PartyIdentification id; // Identification
        
        public String getNm() { return nm; }
        public void setNm(String nm) { this.nm = nm; }
        
        public PartyIdentification getId() { return id; }
        public void setId(PartyIdentification id) { this.id = id; }
    }
    
    public static class PartyIdentification {
        @JsonProperty("OrgId")
        private OrganisationIdentification orgId; // Organisation Identification
        
        public OrganisationIdentification getOrgId() { return orgId; }
        public void setOrgId(OrganisationIdentification orgId) { this.orgId = orgId; }
    }
    
    public static class OrganisationIdentification {
        @JsonProperty("Othr")
        private GenericIdentification othr; // Other
        
        public GenericIdentification getOthr() { return othr; }
        public void setOthr(GenericIdentification othr) { this.othr = othr; }
    }
    
    public static class GenericIdentification {
        @JsonProperty("Id")
        private String id; // Identification
        
        @JsonProperty("SchmeNm")
        private SchemeName schmeNm; // Scheme Name
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public SchemeName getSchmeNm() { return schmeNm; }
        public void setSchmeNm(SchemeName schmeNm) { this.schmeNm = schmeNm; }
    }
    
    public static class SchemeName {
        @JsonProperty("Cd")
        private String cd; // Code
        
        public String getCd() { return cd; }
        public void setCd(String cd) { this.cd = cd; }
    }
    
    public static class PaymentInformation {
        @JsonProperty("PmtInfId")
        private String pmtInfId; // Payment Information Identification
        
        @JsonProperty("PmtMtd")
        private String pmtMtd; // Payment Method
        
        @JsonProperty("BtchBookg")
        private String btchBookg; // Batch Booking
        
        @JsonProperty("NbOfTxs")
        private String nbOfTxs; // Number Of Transactions
        
        @JsonProperty("CtrlSum")
        private String ctrlSum; // Control Sum
        
        @JsonProperty("PmtTpInf")
        private PaymentTypeInformation pmtTpInf; // Payment Type Information
        
        @JsonProperty("ReqdExctnDt")
        private String reqdExctnDt; // Requested Execution Date
        
        @JsonProperty("Dbtr")
        private PartyIdentification dbtr; // Debtor
        
        @JsonProperty("DbtrAcct")
        private CashAccount dbtrAcct; // Debtor Account
        
        @JsonProperty("DbtrAgt")
        private BranchAndFinancialInstitutionIdentification dbtrAgt; // Debtor Agent
        
        @JsonProperty("CdtTrfTxInf")
        private CreditTransferTransactionInformation cdtTrfTxInf; // Credit Transfer Transaction Information
        
        public String getPmtInfId() { return pmtInfId; }
        public void setPmtInfId(String pmtInfId) { this.pmtInfId = pmtInfId; }
        
        public String getPmtMtd() { return pmtMtd; }
        public void setPmtMtd(String pmtMtd) { this.pmtMtd = pmtMtd; }
        
        public String getBtchBookg() { return btchBookg; }
        public void setBtchBookg(String btchBookg) { this.btchBookg = btchBookg; }
        
        public String getNbOfTxs() { return nbOfTxs; }
        public void setNbOfTxs(String nbOfTxs) { this.nbOfTxs = nbOfTxs; }
        
        public String getCtrlSum() { return ctrlSum; }
        public void setCtrlSum(String ctrlSum) { this.ctrlSum = ctrlSum; }
        
        public PaymentTypeInformation getPmtTpInf() { return pmtTpInf; }
        public void setPmtTpInf(PaymentTypeInformation pmtTpInf) { this.pmtTpInf = pmtTpInf; }
        
        public String getReqdExctnDt() { return reqdExctnDt; }
        public void setReqdExctnDt(String reqdExctnDt) { this.reqdExctnDt = reqdExctnDt; }
        
        public PartyIdentification getDbtr() { return dbtr; }
        public void setDbtr(PartyIdentification dbtr) { this.dbtr = dbtr; }
        
        public CashAccount getDbtrAcct() { return dbtrAcct; }
        public void setDbtrAcct(CashAccount dbtrAcct) { this.dbtrAcct = dbtrAcct; }
        
        public BranchAndFinancialInstitutionIdentification getDbtrAgt() { return dbtrAgt; }
        public void setDbtrAgt(BranchAndFinancialInstitutionIdentification dbtrAgt) { this.dbtrAgt = dbtrAgt; }
        
        public CreditTransferTransactionInformation getCdtTrfTxInf() { return cdtTrfTxInf; }
        public void setCdtTrfTxInf(CreditTransferTransactionInformation cdtTrfTxInf) { this.cdtTrfTxInf = cdtTrfTxInf; }
    }
    
    public static class PaymentTypeInformation {
        @JsonProperty("SvcLvl")
        private ServiceLevel svcLvl; // Service Level
        
        public ServiceLevel getSvcLvl() { return svcLvl; }
        public void setSvcLvl(ServiceLevel svcLvl) { this.svcLvl = svcLvl; }
    }
    
    public static class ServiceLevel {
        @JsonProperty("Cd")
        private String cd; // Code
        
        public String getCd() { return cd; }
        public void setCd(String cd) { this.cd = cd; }
    }
    
    public static class CashAccount {
        @JsonProperty("Id")
        private AccountIdentification id; // Identification
        
        public AccountIdentification getId() { return id; }
        public void setId(AccountIdentification id) { this.id = id; }
    }
    
    public static class AccountIdentification {
        @JsonProperty("Othr")
        private GenericIdentification othr; // Other
        
        public GenericIdentification getOthr() { return othr; }
        public void setOthr(GenericIdentification othr) { this.othr = othr; }
    }
    
    public static class BranchAndFinancialInstitutionIdentification {
        @JsonProperty("FinInstnId")
        private FinancialInstitutionIdentification finInstnId; // Financial Institution Identification
        
        public FinancialInstitutionIdentification getFinInstnId() { return finInstnId; }
        public void setFinInstnId(FinancialInstitutionIdentification finInstnId) { this.finInstnId = finInstnId; }
    }
    
    public static class FinancialInstitutionIdentification {
        @JsonProperty("BICFI")
        private String bicfi; // BICFI
        
        public String getBicfi() { return bicfi; }
        public void setBicfi(String bicfi) { this.bicfi = bicfi; }
    }
    
    public static class CreditTransferTransactionInformation {
        @JsonProperty("PmtId")
        private PaymentIdentification pmtId; // Payment Identification
        
        @JsonProperty("Amt")
        private Amount amt; // Amount
        
        @JsonProperty("CdtrAgt")
        private BranchAndFinancialInstitutionIdentification cdtrAgt; // Creditor Agent
        
        @JsonProperty("Cdtr")
        private PartyIdentification cdtr; // Creditor
        
        @JsonProperty("CdtrAcct")
        private CashAccount cdtrAcct; // Creditor Account
        
        public PaymentIdentification getPmtId() { return pmtId; }
        public void setPmtId(PaymentIdentification pmtId) { this.pmtId = pmtId; }
        
        public Amount getAmt() { return amt; }
        public void setAmt(Amount amt) { this.amt = amt; }
        
        public BranchAndFinancialInstitutionIdentification getCdtrAgt() { return cdtrAgt; }
        public void setCdtrAgt(BranchAndFinancialInstitutionIdentification cdtrAgt) { this.cdtrAgt = cdtrAgt; }
        
        public PartyIdentification getCdtr() { return cdtr; }
        public void setCdtr(PartyIdentification cdtr) { this.cdtr = cdtr; }
        
        public CashAccount getCdtrAcct() { return cdtrAcct; }
        public void setCdtrAcct(CashAccount cdtrAcct) { this.cdtrAcct = cdtrAcct; }
    }
    
    public static class PaymentIdentification {
        @JsonProperty("InstrId")
        private String instrId; // Instruction Identification
        
        @JsonProperty("EndToEndId")
        private String endToEndId; // End To End Identification
        
        public String getInstrId() { return instrId; }
        public void setInstrId(String instrId) { this.instrId = instrId; }
        
        public String getEndToEndId() { return endToEndId; }
        public void setEndToEndId(String endToEndId) { this.endToEndId = endToEndId; }
    }
    
    public static class Amount {
        @JsonProperty("InstdAmt")
        private String instdAmt; // Instructed Amount
        
        public String getInstdAmt() { return instdAmt; }
        public void setInstdAmt(String instdAmt) { this.instdAmt = instdAmt; }
    }
    
    // Constructors
    public Iso20022Message() {}
    
    public Iso20022Message(String msgId, String creDtTm) {
        this.msgId = msgId;
        this.creDtTm = creDtTm;
    }
    
    // Getters and Setters
    public String getMsgId() { return msgId; }
    public void setMsgId(String msgId) { this.msgId = msgId; }
    
    public String getCreDtTm() { return creDtTm; }
    public void setCreDtTm(String creDtTm) { this.creDtTm = creDtTm; }
    
    public String getNbOfTxs() { return nbOfTxs; }
    public void setNbOfTxs(String nbOfTxs) { this.nbOfTxs = nbOfTxs; }
    
    public String getCtrlSum() { return ctrlSum; }
    public void setCtrlSum(String ctrlSum) { this.ctrlSum = ctrlSum; }
    
    public InitiatingParty getInitgPty() { return initgPty; }
    public void setInitgPty(InitiatingParty initgPty) { this.initgPty = initgPty; }
    
    public PaymentInformation getPmtInf() { return pmtInf; }
    public void setPmtInf(PaymentInformation pmtInf) { this.pmtInf = pmtInf; }
    
    @Override
    public String toString() {
        return "Iso20022Message{" +
                "msgId='" + msgId + '\'' +
                ", creDtTm='" + creDtTm + '\'' +
                '}';
    }
} 