package com.snapsort.app.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

/**
 * Data Access Object for Category entity
 */
@Dao
public interface CategoryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Category> categories);
    
    @Update
    void update(Category category);
    
    @Delete
    void delete(Category category);
    
    @Query("SELECT * FROM categories ORDER BY imageCount DESC")
    List<Category> getAllCategories();
    
    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryById(String id);
    
    @Query("SELECT * FROM categories WHERE imageCount > 0 ORDER BY imageCount DESC")
    List<Category> getCategoriesWithImages();
    
    @Query("SELECT COUNT(*) FROM categories")
    int getTotalCount();
    
    @Query("UPDATE categories SET imageCount = :count WHERE id = :id")
    void updateImageCount(String id, int count);
    
    @Query("UPDATE categories SET imageCount = imageCount + 1 WHERE id = :id")
    void incrementImageCount(String id);
    
    @Query("UPDATE categories SET imageCount = imageCount - 1 WHERE id = :id")
    void decrementImageCount(String id);
}
