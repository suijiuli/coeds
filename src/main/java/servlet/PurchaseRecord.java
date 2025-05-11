package servlet;

import java.sql.Timestamp;

public class PurchaseRecord {
    private int purchaseId;
    private int userId;
    private String username;
    private int categoryId;
    private String categoryName;
    private java.sql.Timestamp purchaseDate;
    private double price;
    private int quantity;

    // Getters and Setters
    public int getPurchaseId() { return purchaseId; }
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public java.sql.Timestamp getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(java.sql.Timestamp purchaseDate) { this.purchaseDate = purchaseDate; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}