package com.snapsort.app.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.snapsort.app.R;
import com.snapsort.app.model.AutoAlbum;
import com.snapsort.app.model.Category;
import com.snapsort.app.model.Screenshot;
import com.snapsort.app.model.SnapSortDatabase;
import com.snapsort.app.util.AutoAlbumDetector;
import com.snapsort.app.util.ImageClassifier;
import com.snapsort.app.util.NaturalLanguageSearch;
import com.snapsort.app.util.ScreenshotScanner;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for MainActivity
 * Handles business logic and data operations
 */
public class MainViewModel extends AndroidViewModel {
    
    private static final String TAG = "MainViewModel";
    
    // Database DAOs
    private final SnapSortDatabase database;
    
    // Utilities
    private final ScreenshotScanner scanner;
    private final ImageClassifier classifier;
    private final AutoAlbumDetector albumDetector;
    private final NaturalLanguageSearch nlSearch;
    
    // LiveData
    private final MutableLiveData<List<Screenshot>> screenshots = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<AutoAlbum>> autoAlbums = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isScanning = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isOrganizing = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> organizeProgress = new MutableLiveData<>(0);
    private final MutableLiveData<String> message = new MutableLiveData<>();
    
    // Executor for background tasks
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    
    // Settings
    private boolean autoScanEnabled = true;
    private int organizedCount = 0;
    
    public MainViewModel(@NonNull Application application) {
        super(application);
        
        database = SnapSortDatabase.getInstance(application);
        scanner = new ScreenshotScanner(application);
        classifier = new ImageClassifier(application);
        albumDetector = new AutoAlbumDetector();
        nlSearch = new NaturalLanguageSearch();
        
        // Initialize default categories
        initializeCategories();
        
        // Load initial data
        loadScreenshots();
        loadCategories();
        loadAutoAlbums();
    }
    
    /**
     * Initialize default categories in the database
     */
    private void initializeCategories() {
        executor.execute(() -> {
            int count = database.categoryDao().getTotalCount();
            if (count == 0) {
                List<Category> defaultCategories = createDefaultCategories();
                database.categoryDao().insertAll(defaultCategories);
            }
        });
    }
    
    private List<Category> createDefaultCategories() {
        List<Category> categories = new ArrayList<>();
        
        categories.add(createCategory("all", "All", 0));
        categories.add(createCategory("social", "Social Media", R.drawable.ic_category_social));
        categories.add(createCategory("chat", "Chat & Messages", R.drawable.ic_category_chat));
        categories.add(createCategory("gaming", "Gaming", R.drawable.ic_category_gaming));
        categories.add(createCategory("shopping", "Shopping", R.drawable.ic_category_shopping));
        categories.add(createCategory("news", "News & Articles", R.drawable.ic_category_news));
        categories.add(createCategory("music", "Music & Audio", R.drawable.ic_category_music));
        categories.add(createCategory("video", "Video & Streaming", R.drawable.ic_category_video));
        categories.add(createCategory("maps", "Maps & Navigation", R.drawable.ic_category_maps));
        categories.add(createCategory("finance", "Finance & Banking", R.drawable.ic_category_finance));
        categories.add(createCategory("productivity", "Productivity", R.drawable.ic_category_productivity));
        categories.add(createCategory("settings", "Settings & System", R.drawable.ic_category_settings));
        categories.add(createCategory("other", "Other", R.drawable.ic_category_other));
        
        return categories;
    }
    
    private Category createCategory(String id, String displayName, int iconResId) {
        Category category = new Category();
        category.setId(id);
        category.setName(id);
        category.setDisplayName(displayName);
        category.setIconResId(iconResId);
        category.setImageCount(0);
        return category;
    }
    
    /**
     * Scan for screenshots on the device
     */
    public void scanScreenshots() {
        if (isScanning.getValue()) return;
        
        isScanning.setValue(true);
        message.setValue("Scanning for screenshots...");
        
        executor.execute(() -> {
            try {
                List<Screenshot> foundScreenshots = scanner.scanScreenshots();
                
                // Filter out already processed screenshots
                List<Screenshot> newScreenshots = filterNewScreenshots(foundScreenshots);
                
                // Save to database
                for (Screenshot screenshot : newScreenshots) {
                    database.screenshotDao().insert(screenshot);
                }
                
                // Update UI
                screenshots.postValue(foundScreenshots);
                
                String msg = "Found " + foundScreenshots.size() + " screenshots";
                if (!newScreenshots.isEmpty()) {
                    msg += " (" + newScreenshots.size() + " new)";
                }
                message.postValue(msg);
                
            } catch (Exception e) {
                Log.e(TAG, "Error scanning screenshots", e);
                message.postValue("Error scanning: " + e.getMessage());
            } finally {
                isScanning.postValue(false);
            }
        });
    }
    
    /**
     * Filter out screenshots that are already in the database
     */
    private List<Screenshot> filterNewScreenshots(List<Screenshot> allScreenshots) {
        List<Screenshot> newScreenshots = new ArrayList<>();
        for (Screenshot screenshot : allScreenshots) {
            Screenshot existing = database.screenshotDao().getScreenshotByPath(screenshot.getPath());
            if (existing == null) {
                newScreenshots.add(screenshot);
            }
        }
        return newScreenshots;
    }
    
    /**
     * Organize screenshots using AI classification
     */
    public void organizeScreenshots() {
        if (isOrganizing.getValue()) return;
        
        isOrganizing.setValue(true);
        organizeProgress.setValue(0);
        message.setValue("Organizing screenshots with AI...");
        
        executor.execute(() -> {
            try {
                List<Screenshot> unorganized = database.screenshotDao().getUnorganizedScreenshots();
                int total = unorganized.size();
                int processed = 0;
                int organized = 0;
                
                for (Screenshot screenshot : unorganized) {
                    // Classify image
                    Bitmap bitmap = scanner.loadBitmap(screenshot);
                    if (bitmap != null) {
                        ImageClassifier.ClassificationResult result = classifier.classify(bitmap);
                        
                        screenshot.setCategory(result.getCategory());
                        screenshot.setConfidence(result.getConfidence());
                        screenshot.setOrganized(true);
                        
                        // Detect auto-album type
                        String albumType = albumDetector.detectAlbumType(
                                screenshot, 
                                screenshot.getExtractedText()
                        );
                        if (albumType != null) {
                            screenshot.setAutoAlbum(albumType);
                            updateAutoAlbumCount(albumType, 1);
                        }
                        
                        database.screenshotDao().update(screenshot);
                        database.categoryDao().incrementImageCount(result.getCategory());
                        organized++;
                    }
                    
                    processed++;
                    organizeProgress.postValue((processed * 100) / total);
                }
                
                organizedCount += organized;
                message.postValue("Organized " + organized + " screenshots");
                
                // Refresh data
                loadScreenshots();
                loadCategories();
                loadAutoAlbums();
                
            } catch (Exception e) {
                Log.e(TAG, "Error organizing screenshots", e);
                message.postValue("Error organizing: " + e.getMessage());
            } finally {
                isOrganizing.postValue(false);
            }
        });
    }
    
    /**
     * Update auto-album image count
     */
    private void updateAutoAlbumCount(String albumType, int increment) {
        AutoAlbum album = database.autoAlbumDao().getAlbumByType(albumType);
        if (album == null) {
            album = createAutoAlbum(albumType);
            database.autoAlbumDao().insert(album);
        }
        database.autoAlbumDao().incrementImageCount(album.getId(), System.currentTimeMillis());
    }
    
    private AutoAlbum createAutoAlbum(String type) {
        AutoAlbum album = new AutoAlbum();
        album.setId(type);
        album.setName(type);
        album.setAlbumType(type);
        album.setImageCount(0);
        
        switch (type) {
            case "shopping_list":
                album.setDisplayName("Shopping Lists");
                album.setIconResId(R.drawable.ic_auto_shopping_list);
                break;
            case "ticket":
                album.setDisplayName("Tickets");
                album.setIconResId(R.drawable.ic_auto_ticket);
                break;
            case "todo":
                album.setDisplayName("To-Do Lists");
                album.setIconResId(R.drawable.ic_auto_todo);
                break;
            default:
                album.setDisplayName(type);
                album.setIconResId(R.drawable.ic_category_other);
        }
        
        return album;
    }
    
    /**
     * Search screenshots with natural language query
     */
    public void searchWithNaturalLanguage(String query) {
        NaturalLanguageSearch.SearchQuery searchQuery = nlSearch.parseQuery(query);
        
        executor.execute(() -> {
            List<Screenshot> allScreenshots = database.screenshotDao().getAllScreenshots();
            List<Screenshot> results = nlSearch.filterScreenshots(allScreenshots, searchQuery);
            screenshots.postValue(results);
        });
    }
    
    /**
     * Search screenshots by text
     */
    public void searchScreenshots(String query) {
        executor.execute(() -> {
            List<Screenshot> results = database.screenshotDao().searchScreenshots(query);
            screenshots.postValue(results);
        });
    }
    
    /**
     * Load all screenshots
     */
    public void loadScreenshots() {
        executor.execute(() -> {
            List<Screenshot> list = database.screenshotDao().getAllScreenshots();
            screenshots.postValue(list);
        });
    }
    
    /**
     * Load categories
     */
    public void loadCategories() {
        executor.execute(() -> {
            List<Category> list = database.categoryDao().getAllCategories();
            categories.postValue(list);
        });
    }
    
    /**
     * Load auto albums
     */
    public void loadAutoAlbums() {
        executor.execute(() -> {
            List<AutoAlbum> list = database.autoAlbumDao().getAlbumsWithImages();
            autoAlbums.postValue(list);
        });
    }
    
    /**
     * Delete a screenshot
     */
    public void deleteScreenshot(Screenshot screenshot) {
        executor.execute(() -> {
            scanner.deleteFile(screenshot);
            database.screenshotDao().delete(screenshot);
            database.categoryDao().decrementImageCount(screenshot.getCategory());
            loadScreenshots();
            message.postValue("Screenshot deleted");
        });
    }
    
    /**
     * View a screenshot
     */
    public void viewScreenshot(Screenshot screenshot) {
        // This would open a detail view or image viewer
        message.postValue("Viewing: " + screenshot.getName());
    }
    
    /**
     * Create organization video/GIF
     */
    public void createOrganizationVideo() {
        executor.execute(() -> {
            try {
                // Create a simple summary image
                Bitmap summaryBitmap = createSummaryImage();
                
                // Save to file
                File outputDir = new File(Environment.getExternalStorageDirectory(), "SnapSort");
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }
                
                File outputFile = new File(outputDir, "organization_summary.png");
                FileOutputStream fos = new FileOutputStream(outputFile);
                summaryBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                
                message.postValue("Summary saved to: " + outputFile.getAbsolutePath());
                
            } catch (IOException e) {
                Log.e(TAG, "Error creating summary", e);
                message.postValue("Error creating summary");
            }
        });
    }
    
    private Bitmap createSummaryImage() {
        // Create a simple summary bitmap
        Bitmap bitmap = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        // Background
        canvas.drawColor(Color.WHITE);
        
        // Title
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#6366F1"));
        paint.setTextSize(24);
        paint.setAntiAlias(true);
        canvas.drawText("SnapSort Summary", 20, 40, paint);
        
        // Stats
        paint.setColor(Color.BLACK);
        paint.setTextSize(18);
        canvas.drawText("Total: " + screenshots.getValue().size(), 20, 80, paint);
        canvas.drawText("Organized: " + organizedCount, 20, 110, paint);
        
        return bitmap;
    }
    
    /**
     * Export organization report as PDF
     */
    public void exportOrganizationReport() {
        executor.execute(() -> {
            try {
                PdfDocument pdfDocument = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(400, 600, 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                
                Canvas canvas = page.getCanvas();
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                paint.setTextSize(16);
                
                canvas.drawText("SnapSort Organization Report", 20, 30, paint);
                canvas.drawText("Generated: " + new java.util.Date(), 20, 50, paint);
                canvas.drawText("Total Screenshots: " + screenshots.getValue().size(), 20, 80, paint);
                canvas.drawText("Organized: " + organizedCount, 20, 100, paint);
                
                pdfDocument.finishPage(page);
                
                // Save PDF
                File outputDir = new File(Environment.getExternalStorageDirectory(), "SnapSort");
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }
                
                File outputFile = new File(outputDir, "organization_report.pdf");
                FileOutputStream fos = new FileOutputStream(outputFile);
                pdfDocument.writeTo(fos);
                pdfDocument.close();
                fos.close();
                
                message.postValue("Report saved to: " + outputFile.getAbsolutePath());
                
            } catch (IOException e) {
                Log.e(TAG, "Error exporting report", e);
                message.postValue("Error exporting report");
            }
        });
    }
    
    /**
     * Check if auto-scan is enabled
     */
    public boolean isAutoScanEnabled() {
        return autoScanEnabled;
    }
    
    /**
     * Set auto-scan preference
     */
    public void setAutoScanEnabled(boolean enabled) {
        this.autoScanEnabled = enabled;
    }
    
    // Getters for LiveData
    public LiveData<List<Screenshot>> getScreenshots() { return screenshots; }
    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<List<AutoAlbum>> getAutoAlbums() { return autoAlbums; }
    public LiveData<Boolean> isScanning() { return isScanning; }
    public LiveData<Boolean> isOrganizing() { return isOrganizing; }
    public LiveData<Integer> getOrganizeProgress() { return organizeProgress; }
    public LiveData<String> getMessage() { return message; }
    public int getOrganizedCount() { return organizedCount; }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        classifier.close();
        executor.shutdown();
    }
}
