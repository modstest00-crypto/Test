package com.snapsort.app.model;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room Database for SnapSort app
 */
@Database(
    entities = {
        Screenshot.class,
        Category.class,
        AutoAlbum.class,
        AppSettings.class,
        ProFeature.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class SnapSortDatabase extends RoomDatabase {
    
    private static volatile SnapSortDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    private static final String DATABASE_NAME = "snapsort_db";
    
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    public abstract ScreenshotDao screenshotDao();
    public abstract CategoryDao categoryDao();
    public abstract AutoAlbumDao autoAlbumDao();
    public abstract SettingsDao settingsDao();
    public abstract ProFeatureDao proFeatureDao();
    
    public static SnapSortDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (SnapSortDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            SnapSortDatabase.class,
                            DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * Destroy the database instance (for testing)
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
