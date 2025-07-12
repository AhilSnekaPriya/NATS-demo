package com.example.natsdemo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simplified ISO 8583 message model for testing purposes.
 * In a real implementation, you would use a proper ISO 8583 library like jPOS.
 */
public class Iso8583Message {
    
    @JsonProperty("MTI")
    private String mti; // Message Type Indicator
    
    @JsonProperty("PAN")
    private String pan; // Primary Account Number (Field 2)
    
    @JsonProperty("PROC_CODE")
    private String processingCode; // Processing Code (Field 3)
    
    @JsonProperty("AMOUNT")
    private String amount; // Transaction Amount (Field 4)
    
    @JsonProperty("STAN")
    private String stan; // System Trace Audit Number (Field 11)
    
    @JsonProperty("TIME")
    private String time; // Transaction Time (Field 7)
    
    @JsonProperty("DATE")
    private String date; // Transaction Date (Field 13)
    
    @JsonProperty("MERCHANT_TYPE")
    private String merchantType; // Merchant Type (Field 18)
    
    @JsonProperty("POS_ENTRY_MODE")
    private String posEntryMode; // POS Entry Mode (Field 22)
    
    @JsonProperty("CARD_SEQUENCE")
    private String cardSequence; // Card Sequence Number (Field 23)
    
    @JsonProperty("ACQUIRER_ID")
    private String acquirerId; // Acquirer Institution ID (Field 32)
    
    @JsonProperty("FORWARDER_ID")
    private String forwarderId; // Forwarding Institution ID (Field 33)
    
    @JsonProperty("TRACK2")
    private String track2; // Track 2 Data (Field 35)
    
    @JsonProperty("RETRIEVAL_REF")
    private String retrievalRef; // Retrieval Reference Number (Field 37)
    
    @JsonProperty("AUTH_CODE")
    private String authCode; // Authorization ID Response (Field 38)
    
    @JsonProperty("RESPONSE_CODE")
    private String responseCode; // Response Code (Field 39)
    
    @JsonProperty("CARD_ACCEPTOR")
    private String cardAcceptor; // Card Acceptor Terminal ID (Field 41)
    
    @JsonProperty("CARD_ACCEPTOR_ID")
    private String cardAcceptorId; // Card Acceptor ID Code (Field 42)
    
    @JsonProperty("CARD_ACCEPTOR_NAME")
    private String cardAcceptorName; // Card Acceptor Name/Location (Field 43)
    
    @JsonProperty("CURRENCY_CODE")
    private String currencyCode; // Currency Code (Field 49)
    
    @JsonProperty("PIN_DATA")
    private String pinData; // PIN Data (Field 52)
    
    @JsonProperty("SECURITY_INFO")
    private String securityInfo; // Security Related Control Information (Field 53)
    
    @JsonProperty("ADDITIONAL_AMOUNTS")
    private String additionalAmounts; // Additional Amounts (Field 54)
    
    @JsonProperty("ICC_DATA")
    private String iccData; // ICC System Related Data (Field 55)
    
    @JsonProperty("ORIGINAL_DATA")
    private String originalData; // Original Data Elements (Field 90)
    
    @JsonProperty("REPLACEMENT_AMOUNTS")
    private String replacementAmounts; // Replacement Amounts (Field 95)
    
    @JsonProperty("MESSAGE_SECURITY")
    private String messageSecurity; // Message Security Code (Field 128)
    
    // Constructors
    public Iso8583Message() {}
    
    public Iso8583Message(String mti, String pan, String amount, String stan) {
        this.mti = mti;
        this.pan = pan;
        this.amount = amount;
        this.stan = stan;
    }
    
    // Getters and Setters
    public String getMti() { return mti; }
    public void setMti(String mti) { this.mti = mti; }
    
    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    
    public String getProcessingCode() { return processingCode; }
    public void setProcessingCode(String processingCode) { this.processingCode = processingCode; }
    
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    
    public String getStan() { return stan; }
    public void setStan(String stan) { this.stan = stan; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getMerchantType() { return merchantType; }
    public void setMerchantType(String merchantType) { this.merchantType = merchantType; }
    
    public String getPosEntryMode() { return posEntryMode; }
    public void setPosEntryMode(String posEntryMode) { this.posEntryMode = posEntryMode; }
    
    public String getCardSequence() { return cardSequence; }
    public void setCardSequence(String cardSequence) { this.cardSequence = cardSequence; }
    
    public String getAcquirerId() { return acquirerId; }
    public void setAcquirerId(String acquirerId) { this.acquirerId = acquirerId; }
    
    public String getForwarderId() { return forwarderId; }
    public void setForwarderId(String forwarderId) { this.forwarderId = forwarderId; }
    
    public String getTrack2() { return track2; }
    public void setTrack2(String track2) { this.track2 = track2; }
    
    public String getRetrievalRef() { return retrievalRef; }
    public void setRetrievalRef(String retrievalRef) { this.retrievalRef = retrievalRef; }
    
    public String getAuthCode() { return authCode; }
    public void setAuthCode(String authCode) { this.authCode = authCode; }
    
    public String getResponseCode() { return responseCode; }
    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }
    
    public String getCardAcceptor() { return cardAcceptor; }
    public void setCardAcceptor(String cardAcceptor) { this.cardAcceptor = cardAcceptor; }
    
    public String getCardAcceptorId() { return cardAcceptorId; }
    public void setCardAcceptorId(String cardAcceptorId) { this.cardAcceptorId = cardAcceptorId; }
    
    public String getCardAcceptorName() { return cardAcceptorName; }
    public void setCardAcceptorName(String cardAcceptorName) { this.cardAcceptorName = cardAcceptorName; }
    
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    
    public String getPinData() { return pinData; }
    public void setPinData(String pinData) { this.pinData = pinData; }
    
    public String getSecurityInfo() { return securityInfo; }
    public void setSecurityInfo(String securityInfo) { this.securityInfo = securityInfo; }
    
    public String getAdditionalAmounts() { return additionalAmounts; }
    public void setAdditionalAmounts(String additionalAmounts) { this.additionalAmounts = additionalAmounts; }
    
    public String getIccData() { return iccData; }
    public void setIccData(String iccData) { this.iccData = iccData; }
    
    public String getOriginalData() { return originalData; }
    public void setOriginalData(String originalData) { this.originalData = originalData; }
    
    public String getReplacementAmounts() { return replacementAmounts; }
    public void setReplacementAmounts(String replacementAmounts) { this.replacementAmounts = replacementAmounts; }
    
    public String getMessageSecurity() { return messageSecurity; }
    public void setMessageSecurity(String messageSecurity) { this.messageSecurity = messageSecurity; }
    
    @Override
    public String toString() {
        return "Iso8583Message{" +
                "mti='" + mti + '\'' +
                ", pan='" + pan + '\'' +
                ", amount='" + amount + '\'' +
                ", stan='" + stan + '\'' +
                '}';
    }
} 