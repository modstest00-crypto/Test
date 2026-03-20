package com.snapsort.app.util;

import android.util.Log;
import com.snapsort.app.model.Screenshot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Natural Language Search Engine for SnapSort
 * Parses natural language queries and filters screenshots accordingly
 * 
 * Examples:
 * - "Show me the Wi-Fi password from last week"
 * - "Find the receipt over $50"
 * - "Shopping screenshots from yesterday"
 * - "Tickets for my upcoming flight"
 */
public class NaturalLanguageSearch {
    
    private static final String TAG = "NLSearch";
    
    // Pattern for extracting amounts (e.g., "$50", "over 50 dollars")
    private static final Pattern AMOUNT_PATTERN = Pattern.compile(
            "(?:\\$\\s*([\\d,]+\\.?\\d*)|([\\d,]+\\.?\\d*)\\s*(?:dollars?|bucks?|usd))",
            Pattern.CASE_INSENSITIVE
    );
    
    // Pattern for extracting dates
    private static final Pattern DATE_PATTERN = Pattern.compile(
            "(?i)(today|yesterday|last\\s*(week|month)|this\\s*(week|month)|" +
            "(\\d{1,2})\\s*(days?|weeks?|months?)\\s*ago|" +
            "(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}))",
            Pattern.CASE_INSENSITIVE
    );
    
    // Pattern for category keywords
    private static final Pattern CATEGORY_PATTERN = Pattern.compile(
            "(?i)(receipt|shopping|cart|product|price|ticket|booking|flight|" +
            "password|wifi|login|credential|chat|message|whatsapp|" +
            "game|score|music|song|video|youtube|news|article|" +
            "map|location|note|todo|task|list|email)",
            Pattern.CASE_INSENSITIVE
    );
    
    // Pattern for auto-album keywords
    private static final Pattern ALBUM_PATTERN = Pattern.compile(
            "(?i)(shopping\\s*list|to-?do|todo|task\\s*list|checklist|" +
            "ticket|boarding\\s*pass|booking|confirmation|e-?ticket)",
            Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Parse a natural language query and return search parameters
     */
    public SearchQuery parseQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new SearchQuery();
        }
        
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.originalQuery = query;
        
        // Extract amount filters (e.g., "over $50")
        searchQuery.minAmount = extractAmount(query);
        
        // Extract date filters
        searchQuery.dateRange = extractDateRange(query);
        
        // Extract category
        searchQuery.category = extractCategory(query);
        
        // Extract auto-album type
        searchQuery.albumType = extractAlbumType(query);
        
        // Extract text search terms
        searchQuery.textSearch = extractTextSearch(query);
        
        // Check for specific keywords
        searchQuery.isReceiptSearch = query.toLowerCase().contains("receipt");
        searchQuery.isPasswordSearch = query.toLowerCase().contains("password") || 
                                        query.toLowerCase().contains("wifi");
        searchQuery.isShoppingSearch = query.toLowerCase().contains("shopping") ||
                                        query.toLowerCase().contains("cart");
        
        return searchQuery;
    }
    
    /**
     * Extract minimum amount from query
     */
    private Float extractAmount(String query) {
        Matcher matcher = AMOUNT_PATTERN.matcher(query);
        if (matcher.find()) {
            String amount = matcher.group(1);
            if (amount == null) {
                amount = matcher.group(2);
            }
            if (amount != null) {
                try {
                    return Float.parseFloat(amount.replace(",", ""));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error parsing amount", e);
                }
            }
        }
        return null;
    }
    
    /**
     * Extract date range from query
     */
    private DateRange extractDateRange(String query) {
        Matcher matcher = DATE_PATTERN.matcher(query);
        if (matcher.find()) {
            String dateStr = matcher.group(1);
            if (dateStr != null) {
                return parseDateExpression(dateStr.toLowerCase());
            }
        }
        return null;
    }
    
    /**
     * Parse date expression like "last week", "yesterday", etc.
     */
    private DateRange parseDateExpression(String expression) {
        long now = System.currentTimeMillis();
        long startTime = 0;
        long endTime = now;
        
        switch (expression) {
            case "today":
                startTime = getStartOfDay(now);
                break;
            case "yesterday":
                startTime = getStartOfDay(now - 86400000);
                endTime = getEndOfDay(now - 86400000);
                break;
            case "last week":
                startTime = getStartOfWeek(now - 604800000);
                endTime = getEndOfWeek(now - 604800000);
                break;
            case "this week":
                startTime = getStartOfWeek(now);
                break;
            case "last month":
                startTime = getStartOfMonth(now - 2592000000L);
                endTime = getEndOfMonth(now - 2592000000L);
                break;
            case "this month":
                startTime = getStartOfMonth(now);
                break;
            default:
                // Try to parse "X days ago" format
                Pattern daysPattern = Pattern.compile("(\\d+)\\s*days?\\s*ago");
                Matcher matcher = daysPattern.matcher(expression);
                if (matcher.matches()) {
                    int days = Integer.parseInt(matcher.group(1));
                    startTime = now - (days * 86400000L);
                }
                break;
        }
        
        return new DateRange(new Date(startTime), new Date(endTime));
    }
    
    private long getStartOfDay(long timestamp) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    
    private long getEndOfDay(long timestamp) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);
        cal.set(java.util.Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }
    
    private long getStartOfWeek(long timestamp) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(java.util.Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    
    private long getEndOfWeek(long timestamp) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(java.util.Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + 6);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);
        cal.set(java.util.Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }
    
    private long getStartOfMonth(long timestamp) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    
    private long getEndOfMonth(long timestamp) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(java.util.Calendar.DAY_OF_MONTH, cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);
        cal.set(java.util.Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }
    
    /**
     * Extract category from query
     */
    private String extractCategory(String query) {
        Matcher matcher = CATEGORY_PATTERN.matcher(query);
        if (matcher.find()) {
            String keyword = matcher.group(1).toLowerCase();
            return mapKeywordToCategory(keyword);
        }
        return null;
    }
    
    /**
     * Map keyword to app category
     */
    private String mapKeywordToCategory(String keyword) {
        if (keyword.contains("receipt") || keyword.contains("shopping") || 
            keyword.contains("cart") || keyword.contains("product") || keyword.contains("price")) {
            return "shopping";
        }
        if (keyword.contains("ticket") || keyword.contains("booking") || keyword.contains("flight")) {
            return "maps";  // Travel related
        }
        if (keyword.contains("password") || keyword.contains("wifi") || keyword.contains("login")) {
            return "settings";
        }
        if (keyword.contains("chat") || keyword.contains("message") || keyword.contains("whatsapp")) {
            return "chat";
        }
        if (keyword.contains("game") || keyword.contains("score")) {
            return "gaming";
        }
        if (keyword.contains("music") || keyword.contains("song")) {
            return "music";
        }
        if (keyword.contains("video") || keyword.contains("youtube")) {
            return "video";
        }
        if (keyword.contains("news") || keyword.contains("article")) {
            return "news";
        }
        if (keyword.contains("map") || keyword.contains("location")) {
            return "maps";
        }
        if (keyword.contains("note") || keyword.contains("todo") || 
            keyword.contains("task") || keyword.contains("list")) {
            return "productivity";
        }
        if (keyword.contains("email")) {
            return "productivity";
        }
        return null;
    }
    
    /**
     * Extract auto-album type from query
     */
    private String extractAlbumType(String query) {
        Matcher matcher = ALBUM_PATTERN.matcher(query);
        if (matcher.find()) {
            String albumKeyword = matcher.group(1).toLowerCase();
            if (albumKeyword.contains("shopping") || albumKeyword.contains("list")) {
                return "shopping_list";
            }
            if (albumKeyword.contains("todo") || albumKeyword.contains("to-do") || 
                albumKeyword.contains("task") || albumKeyword.contains("checklist")) {
                return "todo";
            }
            if (albumKeyword.contains("ticket") || albumKeyword.contains("boarding") || 
                albumKeyword.contains("booking") || albumKeyword.contains("confirmation")) {
                return "ticket";
            }
        }
        return null;
    }
    
    /**
     * Extract text search terms from query
     */
    private String extractTextSearch(String query) {
        // Remove known patterns and keep remaining text
        String text = query.toLowerCase();
        text = text.replaceAll("(show|me|the|from|find|over|under|more|less|than)", " ");
        text = text.replaceAll("\\$", " ");
        text = text.replaceAll("\\d+", " ");
        text = text.replaceAll("(today|yesterday|last|this|week|month)", " ");
        text = text.trim();
        
        // Keep only meaningful words
        if (text.length() > 2) {
            return text;
        }
        return null;
    }
    
    /**
     * Filter screenshots based on parsed query
     */
    public List<Screenshot> filterScreenshots(List<Screenshot> screenshots, SearchQuery query) {
        List<Screenshot> results = new ArrayList<>();
        
        for (Screenshot screenshot : screenshots) {
            if (matchesQuery(screenshot, query)) {
                results.add(screenshot);
            }
        }
        
        return results;
    }
    
    /**
     * Check if a screenshot matches the search query
     */
    private boolean matchesQuery(Screenshot screenshot, SearchQuery query) {
        // Check category
        if (query.category != null && !query.category.equals(screenshot.getCategory())) {
            return false;
        }
        
        // Check album type
        if (query.albumType != null && !query.albumType.equals(screenshot.getAutoAlbum())) {
            return false;
        }
        
        // Check date range
        if (query.dateRange != null) {
            Date dateAdded = screenshot.getDateAdded();
            if (dateAdded.before(query.dateRange.startDate) || 
                dateAdded.after(query.dateRange.endDate)) {
                return false;
            }
        }
        
        // Check text search
        if (query.textSearch != null && !query.textSearch.isEmpty()) {
            String searchText = query.textSearch.toLowerCase();
            String name = screenshot.getName().toLowerCase();
            String extractedText = screenshot.getExtractedText() != null ? 
                    screenshot.getExtractedText().toLowerCase() : "";
            String tags = screenshot.getTags() != null ? 
                    screenshot.getTags().toLowerCase() : "";
            
            if (!name.contains(searchText) && 
                !extractedText.contains(searchText) && 
                !tags.contains(searchText)) {
                return false;
            }
        }
        
        // Check specific searches
        if (query.isReceiptSearch) {
            String text = screenshot.getExtractedText() != null ? 
                    screenshot.getExtractedText().toLowerCase() : "";
            if (!text.contains("receipt") && !text.contains("total") && 
                !text.contains("purchase") && !text.contains("payment")) {
                return false;
            }
        }
        
        if (query.isPasswordSearch) {
            String text = screenshot.getExtractedText() != null ? 
                    screenshot.getExtractedText().toLowerCase() : "";
            if (!text.contains("password") && !text.contains("wifi") && 
                !text.contains("login") && !text.contains("credential")) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Search query parameters holder
     */
    public static class SearchQuery {
        public String originalQuery;
        public String category;
        public String albumType;
        public Float minAmount;
        public DateRange dateRange;
        public String textSearch;
        public boolean isReceiptSearch;
        public boolean isPasswordSearch;
        public boolean isShoppingSearch;
        
        public SearchQuery() {
            this.originalQuery = "";
        }
        
        public boolean isEmpty() {
            return category == null && albumType == null && 
                   minAmount == null && dateRange == null &&
                   textSearch == null && !isReceiptSearch && 
                   !isPasswordSearch && !isShoppingSearch;
        }
    }
    
    /**
     * Date range holder
     */
    public static class DateRange {
        public Date startDate;
        public Date endDate;
        
        public DateRange(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}
