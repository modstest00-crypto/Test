package com.snapsort.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TensorFlow Lite Image Classifier for screenshot categorization
 * Uses MobileNetV2 or similar lightweight model for on-device inference
 */
public class ImageClassifier {
    
    private static final String MODEL_FILE = "model.tflite";
    private static final String LABELS_FILE = "labels.txt";
    private static final int INPUT_SIZE = 224;
    private static final int NUM_THREADS = 4;
    private static final float PROBABILITY_THRESHOLD = 0.5f;
    
    private Interpreter interpreter;
    private List<String> labels;
    private boolean isModelLoaded;
    
    // Image processing operations
    private ImageProcessor imageProcessor;
    
    // For MobileNetV2: mean=127.0, std=128.0
    private static final float IMAGE_MEAN = 127.0f;
    private static final float IMAGE_STD = 128.0f;
    
    public ImageClassifier(Context context) {
        loadModel(context);
    }
    
    /**
     * Load the TensorFlow Lite model and labels
     */
    private void loadModel(Context context) {
        try {
            // Load model
            interpreter = new Interpreter(FileUtil.loadMappedFile(context, MODEL_FILE));
            interpreter.setNumThreads(NUM_THREADS);
            
            // Load labels
            labels = loadLabels(context);
            
            // Initialize image processor
            imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                    .add(new NormalizeOp(IMAGE_MEAN, IMAGE_STD))
                    .build();
            
            isModelLoaded = true;
        } catch (IOException e) {
            e.printStackTrace();
            isModelLoaded = false;
        }
    }
    
    /**
     * Load labels from file
     */
    private List<String> loadLabels(Context context) throws IOException {
        List<String> labels = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(LABELS_FILE)));
        String line;
        while ((line = reader.readLine()) != null) {
            labels.add(line.trim());
        }
        reader.close();
        return labels;
    }
    
    /**
     * Classify an image and return the result
     * @param bitmap The input bitmap image
     * @return ClassificationResult containing category and confidence
     */
    public ClassificationResult classify(Bitmap bitmap) {
        if (!isModelLoaded || interpreter == null) {
            return new ClassificationResult("other", 0.0f, Collections.emptyList());
        }
        
        try {
            // Preprocess the image
            TensorImage tensorImage = preprocessImage(bitmap);
            
            // Create output tensor buffer
            int numCategories = labels.size();
            TensorBuffer outputBuffer = TensorBuffer.createFixedSize(
                    new int[]{1, numCategories},
                    org.tensorflow.lite.DataType.FLOAT32
            );
            outputBuffer.getBuffer().order(ByteOrder.nativeOrder());
            
            // Run inference
            interpreter.run(tensorImage.getBuffer(), outputBuffer.getBuffer());
            
            // Process results
            float[] probabilities = outputBuffer.getFloatArray();
            List<AbstractMap.SimpleEntry<String, Float>> results = new ArrayList<>();
            
            for (int i = 0; i < probabilities.length; i++) {
                if (i < labels.size()) {
                    results.add(new AbstractMap.SimpleEntry<>(labels.get(i), probabilities[i]));
                }
            }
            
            // Sort by confidence
            results.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));
            
            // Get top result
            String topCategory = "other";
            float topConfidence = 0.0f;
            
            if (!results.isEmpty()) {
                AbstractMap.SimpleEntry<String, Float> topResult = results.get(0);
                topCategory = mapCategoryToAppCategory(topResult.getKey());
                topConfidence = topResult.getValue();
            }
            
            return new ClassificationResult(topCategory, topConfidence, results);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ClassificationResult("other", 0.0f, Collections.emptyList());
        }
    }
    
    /**
     * Preprocess the image for the model
     */
    private TensorImage preprocessImage(Bitmap bitmap) {
        // Convert bitmap to TensorImage
        TensorImage tensorImage = TensorImage.fromBitmap(bitmap);
        
        // Apply preprocessing operations
        return imageProcessor.process(tensorImage);
    }
    
    /**
     * Map model category to app category
     */
    private String mapCategoryToAppCategory(String modelCategory) {
        // Map model output categories to our app categories
        // This depends on your trained model's labels
        String category = modelCategory.toLowerCase();
        
        // Social Media
        if (category.contains("facebook") || category.contains("instagram") || 
            category.contains("twitter") || category.contains("social")) {
            return "social";
        }
        
        // Chat & Messages
        if (category.contains("whatsapp") || category.contains("message") || 
            category.contains("chat") || category.contains("telegram") ||
            category.contains("sms")) {
            return "chat";
        }
        
        // Gaming
        if (category.contains("game") || category.contains("gaming") || 
            category.contains("score") || category.contains("play")) {
            return "gaming";
        }
        
        // Shopping
        if (category.contains("shop") || category.contains("cart") || 
            category.contains("product") || category.contains("price") ||
            category.contains("amazon") || category.contains("ebay")) {
            return "shopping";
        }
        
        // News & Articles
        if (category.contains("news") || category.contains("article") || 
            category.contains("read") || category.contains("web")) {
            return "news";
        }
        
        // Music & Audio
        if (category.contains("music") || category.contains("audio") || 
            category.contains("spotify") || category.contains("sound")) {
            return "music";
        }
        
        // Video & Streaming
        if (category.contains("video") || category.contains("youtube") || 
            category.contains("stream") || category.contains("movie") ||
            category.contains("netflix")) {
            return "video";
        }
        
        // Maps & Navigation
        if (category.contains("map") || category.contains("navigation") || 
            category.contains("location") || category.contains("gps") ||
            category.contains("google maps")) {
            return "maps";
        }
        
        // Finance & Banking
        if (category.contains("bank") || category.contains("finance") || 
            category.contains("money") || category.contains("payment") ||
            category.contains("wallet") || category.contains("receipt")) {
            return "finance";
        }
        
        // Productivity
        if (category.contains("calendar") || category.contains("note") || 
            category.contains("task") || category.contains("email") ||
            category.contains("document") || category.contains("productivity")) {
            return "productivity";
        }
        
        // Settings & System
        if (category.contains("setting") || category.contains("system") || 
            category.contains("menu") || category.contains("config")) {
            return "settings";
        }
        
        // Default to other
        return "other";
    }
    
    /**
     * Close the classifier and release resources
     */
    public void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
        isModelLoaded = false;
    }
    
    /**
     * Check if the model is loaded
     */
    public boolean isModelLoaded() {
        return isModelLoaded;
    }
    
    /**
     * Get all available labels
     */
    public List<String> getLabels() {
        return labels;
    }
    
    /**
     * Classification result holder
     */
    public static class ClassificationResult {
        private final String category;
        private final float confidence;
        private final List<AbstractMap.SimpleEntry<String, Float>> allResults;
        
        public ClassificationResult(String category, float confidence, 
                                   List<AbstractMap.SimpleEntry<String, Float>> allResults) {
            this.category = category;
            this.confidence = confidence;
            this.allResults = allResults;
        }
        
        public String getCategory() {
            return category;
        }
        
        public float getConfidence() {
            return confidence;
        }
        
        public List<AbstractMap.SimpleEntry<String, Float>> getAllResults() {
            return allResults;
        }
        
        public boolean isConfident() {
            return confidence >= PROBABILITY_THRESHOLD;
        }
    }
}
