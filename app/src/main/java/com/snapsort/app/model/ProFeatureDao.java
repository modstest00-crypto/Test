package com.snapsort.app.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

/**
 * Data Access Object for ProFeature entity
 */
@Dao
public interface ProFeatureDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProFeature feature);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ProFeature> features);
    
    @Update
    void update(ProFeature feature);
    
    @Delete
    void delete(ProFeature feature);
    
    @Query("SELECT * FROM pro_features ORDER BY id")
    List<ProFeature> getAllFeatures();
    
    @Query("SELECT * FROM pro_features WHERE id = :id")
    ProFeature getFeatureById(String id);
    
    @Query("SELECT * FROM pro_features WHERE isPurchased = 1")
    List<ProFeature> getPurchasedFeatures();
    
    @Query("SELECT COUNT(*) FROM pro_features WHERE isPurchased = 1")
    int getPurchasedCount();
    
    @Query("UPDATE pro_features SET isPurchased = :purchased, purchaseDate = :date WHERE id = :id")
    void updatePurchaseStatus(String id, boolean purchased, long date);
    
    @Query("UPDATE pro_features SET expiryDate = :date WHERE id = :id")
    void updateExpiryDate(String id, long date);
}
