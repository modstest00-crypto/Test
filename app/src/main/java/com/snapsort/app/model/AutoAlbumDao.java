package com.snapsort.app.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

/**
 * Data Access Object for AutoAlbum entity
 */
@Dao
public interface AutoAlbumDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AutoAlbum album);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AutoAlbum> albums);
    
    @Update
    void update(AutoAlbum album);
    
    @Delete
    void delete(AutoAlbum album);
    
    @Query("SELECT * FROM auto_albums ORDER BY imageCount DESC")
    List<AutoAlbum> getAllAlbums();
    
    @Query("SELECT * FROM auto_albums WHERE id = :id")
    AutoAlbum getAlbumById(String id);
    
    @Query("SELECT * FROM auto_albums WHERE albumType = :type")
    AutoAlbum getAlbumByType(String type);
    
    @Query("SELECT * FROM auto_albums WHERE imageCount > 0 ORDER BY lastUpdated DESC")
    List<AutoAlbum> getAlbumsWithImages();
    
    @Query("SELECT COUNT(*) FROM auto_albums")
    int getTotalCount();
    
    @Query("UPDATE auto_albums SET imageCount = :count WHERE id = :id")
    void updateImageCount(String id, int count);
    
    @Query("UPDATE auto_albums SET imageCount = imageCount + 1, lastUpdated = :timestamp WHERE id = :id")
    void incrementImageCount(String id, long timestamp);
    
    @Query("UPDATE auto_albums SET imageCount = imageCount - 1 WHERE id = :id")
    void decrementImageCount(String id);
}
