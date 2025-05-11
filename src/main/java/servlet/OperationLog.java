package servlet;

import java.sql.Timestamp;

public class OperationLog {
    private int logId;
    private int accountId;
    private String accountType;
    private String operationContent;
    private java.sql.Timestamp operationTime;
    private String ipAddress;
    private String operatorName;

    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public String getOperationContent() { return operationContent; }
    public void setOperationContent(String operationContent) { this.operationContent = operationContent; }
    public java.sql.Timestamp getOperationTime() { return operationTime; }
    public void setOperationTime(java.sql.Timestamp operationTime) { this.operationTime = operationTime; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
}