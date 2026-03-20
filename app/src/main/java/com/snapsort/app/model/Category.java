package com.snapsort.app.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class representing a category
 */
@Entity(tableName = "categories")
public class Category {
    
    @PrimaryKey
    private String id;
    
    private String name;
    private String displayName;
    private int color;
    private int iconResId;
    private int imageCount;
    private String description;
    
    public Category() {
        this.imageCount = 0;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    
    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }
    
    public int getImageCount() { return imageCount; }
    public void setImageCount(int imageCount) { this.imageCount = imageCount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
