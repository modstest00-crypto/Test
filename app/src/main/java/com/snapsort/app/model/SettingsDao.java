package com.snapsort.app.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

/**
 * Data Access Object for AppSettings entity
 */
@Dao
public interface SettingsDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppSettings setting);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AppSettings> settings);
    
    @Update
    void update(AppSettings setting);
    
    @Delete
    void delete(AppSettings setting);
    
    @Query("SELECT * FROM settings ORDER BY key")
    List<AppSettings> getAllSettings();
    
    @Query("SELECT * FROM settings WHERE key = :key")
    AppSettings getSettingByKey(String key);
    
    @Query("SELECT value FROM settings WHERE key = :key")
    String getSettingValue(String key);
    
    @Query("UPDATE settings SET value = :value, lastModified = :timestamp WHERE key = :key")
    void updateSetting(String key, String value, long timestamp);
    
    @Query("DELETE FROM settings")
    void deleteAll();
}
