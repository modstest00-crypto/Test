package com.snapsort.app.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import com.snapsort.app.model.Screenshot;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility class for scanning and managing screenshots
 */
public class ScreenshotScanner {
    
    private static final String TAG = "ScreenshotScanner";
    
    private final Context context;
    private final ContentResolver contentResolver;
    
    // Common screenshot directories
    private static final String[] SCREENSHOT_DIRECTORIES = {
            MediaStore.Images.Media.DATA,
            "Pictures/Screenshots",
            "DCIM/Screenshots",
            "Pictures",
            "DCIM",
            "Downloads"
    };
    
    public ScreenshotScanner(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }
    
    /**
     * Scan for all screenshots on the device
     */
    public List<Screenshot> scanScreenshots() {
        List<Screenshot> screenshots = new ArrayList<>();
        
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_MODIFIED
        };
        
        // Filter for screenshots
        String selection = MediaStore.Images.Media.DATA + " LIKE ?" +
                          " OR " + MediaStore.Images.Media.DATA + " LIKE ?" +
                          " OR " + MediaStore.Images.Media.DATA + " LIKE ?";
        String[] selectionArgs = {
                "%screenshot%",
                "%Screenshot%",
                "%/Pictures/%"
        };
        
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
        
        try (Cursor cursor = contentResolver.query(
                externalUri,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
                int widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
                int heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
                int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
                int dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
                
                while (cursor.moveToNext()) {
                    try {
                        Screenshot screenshot = new Screenshot();
                        screenshot.setId(cursor.getLong(idColumn));
                        screenshot.setPath(cursor.getString(dataColumn));
                        screenshot.setName(cursor.getString(nameColumn));
                        screenshot.setSize(cursor.getLong(sizeColumn));
                        screenshot.setMimeType(cursor.getString(mimeTypeColumn));
                        screenshot.setWidth(cursor.getInt(widthColumn));
                        screenshot.setHeight(cursor.getInt(heightColumn));
                        
                        long dateAdded = cursor.getLong(dateAddedColumn);
                        long dateModified = cursor.getLong(dateModifiedColumn);
                        screenshot.setDateAdded(new Date(dateAdded * 1000));
                        screenshot.setDateModified(new Date(dateModified * 1000));
                        
                        screenshots.add(screenshot);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading cursor", e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error scanning screenshots", e);
        }
        
        // Also scan specific directories
        screenshots.addAll(scanDirectories());
        
        return removeDuplicates(screenshots);
    }
    
    /**
     * Scan specific directories for images
     */
    private List<Screenshot> scanDirectories() {
        List<Screenshot> screenshots = new ArrayList<>();
        
        for (String dirPath : SCREENSHOT_DIRECTORIES) {
            File directory = new File(dirPath);
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles((dir, name) -> 
                        name.toLowerCase().endsWith(".png") || 
                        name.toLowerCase().endsWith(".jpg") || 
                        name.toLowerCase().endsWith(".jpeg"));
                
                if (files != null) {
                    for (File file : files) {
                        try {
                            Screenshot screenshot = new Screenshot();
                            screenshot.setPath(file.getAbsolutePath());
                            screenshot.setName(file.getName());
                            screenshot.setSize(file.length());
                            screenshot.setMimeType(getMimeType(file.getName()));
                            screenshot.setDateAdded(new Date(file.lastModified()));
                            screenshot.setDateModified(new Date(file.lastModified()));
                            
                            // Get dimensions
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                            screenshot.setWidth(options.outWidth);
                            screenshot.setHeight(options.outHeight);
                            
                            screenshots.add(screenshot);
                        } catch (Exception e) {
                            Log.e(TAG, "Error scanning file: " + file.getName(), e);
                        }
                    }
                }
            }
        }
        
        return screenshots;
    }
    
    /**
     * Get MIME type from file name
     */
    private String getMimeType(String fileName) {
        if (fileName.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (fileName.toLowerCase().endsWith(".jpg") || 
                   fileName.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.toLowerCase().endsWith(".webp")) {
            return "image/webp";
        }
        return "image/*";
    }
    
    /**
     * Remove duplicate screenshots based on path
     */
    private List<Screenshot> removeDuplicates(List<Screenshot> screenshots) {
        List<Screenshot> unique = new ArrayList<>();
        List<String> paths = new ArrayList<>();
        
        for (Screenshot screenshot : screenshots) {
            if (!paths.contains(screenshot.getPath())) {
                paths.add(screenshot.getPath());
                unique.add(screenshot);
            }
        }
        
        return unique;
    }
    
    /**
     * Load a bitmap from a screenshot
     */
    public Bitmap loadBitmap(Screenshot screenshot) {
        return loadBitmap(screenshot.getPath());
    }
    
    /**
     * Load a bitmap from a file path
     */
    public Bitmap loadBitmap(String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = calculateSampleSize(path, 224, 224);
            return BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            Log.e(TAG, "Error loading bitmap: " + path, e);
            return null;
        }
    }
    
    /**
     * Calculate sample size for loading bitmap efficiently
     */
    private int calculateSampleSize(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        
        int height = options.outHeight;
        int width = options.outWidth;
        int sampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            
            while ((halfHeight / sampleSize) >= reqHeight && 
                   (halfWidth / sampleSize) >= reqWidth) {
                sampleSize *= 2;
            }
        }
        
        return sampleSize;
    }
    
    /**
     * Check if a file exists
     */
    public boolean fileExists(String path) {
        return new File(path).exists();
    }
    
    /**
     * Delete a screenshot file
     */
    public boolean deleteFile(Screenshot screenshot) {
        try {
            File file = new File(screenshot.getPath());
            return file.delete();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting file", e);
            return false;
        }
    }
    
    /**
     * Get file size in human readable format
     */
    public String getReadableFileSize(long size) {
        if (size <= 0) return "0 B";
        String[] units = new String[] { "B", "KB", "MB", "GB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }
    
    /**
     * Get relative time string
     */
    public String getRelativeTime(Date date) {
        long now = System.currentTimeMillis();
        long diff = now - date.getTime();
        
        if (diff < 60000) {
            return "Just now";
        } else if (diff < 3600000) {
            return (diff / 60000) + " min ago";
        } else if (diff < 86400000) {
            return (diff / 3600000) + " hours ago";
        } else if (diff < 604800000) {
            return (diff / 86400000) + " days ago";
        } else if (diff < 2592000000L) {
            return (diff / 604800000) + " weeks ago";
        } else {
            return (diff / 2592000000L) + " months ago";
        }
    }
}
