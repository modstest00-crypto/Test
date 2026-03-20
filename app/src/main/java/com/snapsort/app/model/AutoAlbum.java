package com.snapsort.app.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

/**
 * Entity class representing an auto-album (Shopping Lists, Tickets, To-Do Lists)
 */
@Entity(tableName = "auto_albums")
public class AutoAlbum {
    
    @PrimaryKey
    private String id;
    
    private String name;
    private String displayName;
    private int iconResId;
    private int color;
    private int imageCount;
    private String albumType;  // shopping_list, ticket, todo, etc.
    private Date lastUpdated;
    private String description;
    
    public AutoAlbum() {
        this.imageCount = 0;
        this.lastUpdated = new Date();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }
    
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    
    public int getImageCount() { return imageCount; }
    public void setImageCount(int imageCount) { this.imageCount = imageCount; }
    
    public String getAlbumType() { return albumType; }
    public void setAlbumType(String albumType) { this.albumType = albumType; }
    
    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
