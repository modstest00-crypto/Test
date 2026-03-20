package com.snapsort.app.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import java.util.Date;

/**
 * Data Access Object for Screenshot entity
 */
@Dao
public interface ScreenshotDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Screenshot screenshot);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Screenshot> screenshots);
    
    @Update
    void update(Screenshot screenshot);
    
    @Delete
    void delete(Screenshot screenshot);
    
    @Query("DELETE FROM screenshots WHERE id = :id")
    void deleteById(long id);
    
    @Query("DELETE FROM screenshots WHERE path = :path")
    void deleteByPath(String path);
    
    @Query("SELECT * FROM screenshots ORDER BY dateAdded DESC")
    List<Screenshot> getAllScreenshots();
    
    @Query("SELECT * FROM screenshots WHERE id = :id")
    Screenshot getScreenshotById(long id);
    
    @Query("SELECT * FROM screenshots WHERE path = :path")
    Screenshot getScreenshotByPath(String path);
    
    @Query("SELECT * FROM screenshots WHERE category = :category ORDER BY dateAdded DESC")
    List<Screenshot> getScreenshotsByCategory(String category);
    
    @Query("SELECT * FROM screenshots WHERE autoAlbum = :albumName ORDER BY dateAdded DESC")
    List<Screenshot> getScreenshotsByAutoAlbum(String albumName);
    
    @Query("SELECT * FROM screenshots WHERE isOrganized = 0 ORDER BY dateAdded DESC")
    List<Screenshot> getUnorganizedScreenshots();
    
    @Query("SELECT * FROM screenshots WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    List<Screenshot> getFavoriteScreenshots();
    
    @Query("SELECT COUNT(*) FROM screenshots")
    int getTotalCount();
    
    @Query("SELECT COUNT(*) FROM screenshots WHERE category = :category")
    int getCountByCategory(String category);
    
    @Query("SELECT COUNT(*) FROM screenshots WHERE isOrganized = 1")
    int getOrganizedCount();
    
    @Query("SELECT COUNT(*) FROM screenshots WHERE isOrganized = 0")
    int getUnorganizedCount();
    
    @Query("SELECT * FROM screenshots WHERE dateAdded >= :date ORDER BY dateAdded DESC")
    List<Screenshot> getScreenshotsSince(Date date);
    
    @Query("SELECT * FROM screenshots WHERE name LIKE '%' || :query || '%' OR extractedText LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY dateAdded DESC")
    List<Screenshot> searchScreenshots(String query);
    
    // Natural language search queries
    @Query("SELECT * FROM screenshots WHERE extractedText LIKE '%receipt%' OR extractedText LIKE '%total%' OR extractedText LIKE '%$%' ORDER BY dateAdded DESC")
    List<Screenshot> searchReceipts();
    
    @Query("SELECT * FROM screenshots WHERE extractedText LIKE '%password%' OR extractedText LIKE '%wifi%' OR extractedText LIKE '%WiFi%' ORDER BY dateAdded DESC")
    List<Screenshot> searchPasswords();
    
    @Query("SELECT * FROM screenshots WHERE category = 'shopping' OR extractedText LIKE '%shopping%' OR extractedText LIKE '%cart%' ORDER BY dateAdded DESC")
    List<Screenshot> searchShopping();
    
    @Query("SELECT * FROM screenshots WHERE autoAlbum = 'shopping_list' OR extractedText LIKE '%list%' ORDER BY dateAdded DESC")
    List<Screenshot> searchShoppingLists();
    
    @Query("SELECT * FROM screenshots WHERE autoAlbum = 'ticket' OR extractedText LIKE '%ticket%' OR extractedText LIKE '%booking%' OR extractedText LIKE '%reservation%' ORDER BY dateAdded DESC")
    List<Screenshot> searchTickets();
    
    @Query("SELECT * FROM screenshots WHERE autoAlbum = 'todo' OR extractedText LIKE '%todo%' OR extractedText LIKE '%to-do%' OR extractedText LIKE '%task%' ORDER BY dateAdded DESC")
    List<Screenshot> searchTodoLists();
    
    @Query("SELECT DISTINCT category FROM screenshots")
    List<String> getAllCategories();
    
    @Query("UPDATE screenshots SET category = :newCategory WHERE id IN (:ids)")
    void updateCategories(List<Long> ids, String newCategory);
    
    @Query("UPDATE screenshots SET autoAlbum = :albumName WHERE id IN (:ids)")
    void updateAutoAlbums(List<Long> ids, String albumName);
    
    @Query("UPDATE screenshots SET isFavorite = :isFavorite WHERE id IN (:ids)")
    void updateFavorites(List<Long> ids, boolean isFavorite);
    
    @Query("UPDATE screenshots SET isOrganized = :isOrganized WHERE id IN (:ids)")
    void updateOrganizedStatus(List<Long> ids, boolean isOrganized);
    
    @Query("UPDATE screenshots SET tags = :tags WHERE id = :id")
    void updateTags(long id, String tags);
    
    @Query("UPDATE screenshots SET extractedText = :text WHERE id = :id")
    void updateExtractedText(long id, String text);
}
