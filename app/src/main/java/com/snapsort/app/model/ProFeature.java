package com.snapsort.app.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

/**
 * Entity class for Pro features / purchases
 */
@Entity(tableName = "pro_features")
public class ProFeature {
    
    @PrimaryKey
    private String id;
    
    private String name;
    private String description;
    private String price;
    private String type;  // one_time, subscription
    private boolean isPurchased;
    private Date purchaseDate;
    private Date expiryDate;  // For subscriptions
    
    public ProFeature() {
        this.isPurchased = false;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public boolean isPurchased() { return isPurchased; }
    public void setPurchased(boolean purchased) { isPurchased = purchased; }
    
    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
}
