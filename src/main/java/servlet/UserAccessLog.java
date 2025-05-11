package servlet;

public class UserAccessLog {
    private int logId;
    private int userId;
    private String username;
    private String ipAddress;
    private java.sql.Timestamp loginTime;
    private java.sql.Timestamp logoutTime;

    // Getters and Setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public java.sql.Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(java.sql.Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public java.sql.Timestamp getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(java.sql.Timestamp logoutTime) {
        this.logoutTime = logoutTime;
    }
}
