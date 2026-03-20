package com.snapsort.app.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import java.util.Date;

/**
 * Entity class representing a screenshot/image in the app
 */
@Entity(tableName = "screenshots",
        indices = {@Index("category"), @Index("dateAdded"), @Index("path")})
public class Screenshot {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String path;
    private String name;
    private long size;
    private String mimeType;
    private int width;
    private int height;
    private Date dateAdded;
    private Date dateModified;
    private String category;
    private float confidence;  // AI confidence score for categorization
    private boolean isOrganized;
    private boolean isFavorite;
    private String autoAlbum;  // For auto-albums like "Shopping Lists", "Tickets", etc.
    private String tags;  // Comma-separated tags for search
    private String extractedText;  // OCR extracted text for search
    
    public Screenshot() {
        this.dateAdded = new Date();
        this.dateModified = new Date();
        this.isOrganized = false;
        this.isFavorite = false;
        this.confidence = 0.0f;
        this.category = "other";
    }
    
    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    
    public Date getDateAdded() { return dateAdded; }
    public void setDateAdded(Date dateAdded) { this.dateAdded = dateAdded; }
    
    public Date getDateModified() { return dateModified; }
    public void setDateModified(Date dateModified) { this.dateModified = dateModified; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public float getConfidence() { return confidence; }
    public void setConfidence(float confidence) { this.confidence = confidence; }
    
    public boolean isOrganized() { return isOrganized; }
    public void setOrganized(boolean organized) { isOrganized = organized; }
    
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    
    public String getAutoAlbum() { return autoAlbum; }
    public void setAutoAlbum(String autoAlbum) { this.autoAlbum = autoAlbum; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }
}
