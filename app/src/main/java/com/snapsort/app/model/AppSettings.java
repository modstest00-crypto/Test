package com.snapsort.app.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

/**
 * Entity class representing user settings
 */
@Entity(tableName = "settings")
public class AppSettings {
    
    @PrimaryKey
    private String key;
    
    private String value;
    private String type;  // boolean, string, int, etc.
    private String description;
    private Date lastModified;
    
    public AppSettings() {
        this.lastModified = new Date();
        this.type = "string";
    }
    
    // Getters and Setters
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getLastModified() { return lastModified; }
    public void setLastModified(Date lastModified) { this.lastModified = lastModified; }
}
