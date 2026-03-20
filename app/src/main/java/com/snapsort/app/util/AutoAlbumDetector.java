package com.snapsort.app.util;

import android.graphics.Bitmap;
import android.util.Log;
import com.snapsort.app.model.Screenshot;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Auto-album detector for identifying Shopping Lists, Tickets, and To-Do Lists
 * Uses pattern matching and heuristics to categorize screenshots into auto-albums
 */
public class AutoAlbumDetector {
    
    private static final String TAG = "AutoAlbumDetector";
    
    // Album types
    public static final String ALBUM_TYPE_SHOPPING_LIST = "shopping_list";
    public static final String ALBUM_TYPE_TICKET = "ticket";
    public static final String ALBUM_TYPE_TODO = "todo";
    
    // Patterns for shopping lists
    private static final Pattern[] SHOPPING_PATTERNS = {
            Pattern.compile("(?i)\\b(shopping|cart|basket|wishlist|buy|purchase|order)\\b"),
            Pattern.compile("(?i)\\b(add to cart|add to basket|checkout)\\b"),
            Pattern.compile("(?i)\\b(total|subtotal|shipping|tax)\\s*[:\\$]?\\s*[\\d,]+\\.?\\d*"),
            Pattern.compile("(?i)\\b(item|qty|quantity|price)\\b.*\\d+"),
            Pattern.compile("(?i)amazon|ebay|shopify|aliexpress|walmart|target")
    };
    
    // Patterns for tickets
    private static final Pattern[] TICKET_PATTERNS = {
            Pattern.compile("(?i)\\b(ticket|boarding pass|e-ticket|confirmation|booking)\\b"),
            Pattern.compile("(?i)\\b(flight|train|bus|movie|concert|event)\\b"),
            Pattern.compile("(?i)\\b(seat|gate|PNR|PNR No|booking ref)\\b"),
            Pattern.compile("(?i)\\b(departure|arrival|from:|to:)\\b"),
            Pattern.compile("(?i)\\b(date:|time:|passenger)\\b"),
            Pattern.compile("(?i)airlines|irctc|bookmyshow|eventbrite|ticketmaster")
    };
    
    // Patterns for to-do lists
    private static final Pattern[] TODO_PATTERNS = {
            Pattern.compile("(?i)\\b(to-do|todo|task|checklist|remind)\\b"),
            Pattern.compile("(?i)\\b(\\[\\s*\\]|\\[x\\]|\\[✓\\]|•|\\-)\\s*\\w+"),
            Pattern.compile("(?i)\\b(due|deadline|priority|important)\\b"),
            Pattern.compile("(?i)\\b(today|tomorrow|this week|next week)\\b"),
            Pattern.compile("(?i)google keep|notion|evernote|todoist|any.do|microsoft to do")
    };
    
    // Text patterns that indicate specific album types
    private static final Pattern[] TEXT_ANALYSIS_PATTERNS = {
            Pattern.compile("(?i)\\$[\\d,]+\\.?\\d*"),  // Prices
            Pattern.compile("(?i)\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}"),  // Dates
            Pattern.compile("(?i)\\d{1,2}:\\d{2}\\s*(AM|PM)?"),  // Times
            Pattern.compile("(?i)@[\\w.]+"),  // Email addresses
            Pattern.compile("(?i)\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}"),  // Card numbers
    };
    
    /**
     * Detect auto-album type for a screenshot
     * @param screenshot The screenshot to analyze
     * @param extractedText Text extracted from the image (OCR)
     * @return Album type or null if no match
     */
    public String detectAlbumType(Screenshot screenshot, String extractedText) {
        // First check extracted text
        if (extractedText != null && !extractedText.isEmpty()) {
            String albumType = analyzeText(extractedText);
            if (albumType != null) {
                return albumType;
            }
        }
        
        // Check tags
        if (screenshot.getTags() != null && !screenshot.getTags().isEmpty()) {
            String albumType = analyzeText(screenshot.getTags());
            if (albumType != null) {
                return albumType;
            }
        }
        
        // Check category as fallback
        String category = screenshot.getCategory();
        if ("shopping".equals(category)) {
            return ALBUM_TYPE_SHOPPING_LIST;
        } else if ("productivity".equals(category)) {
            return ALBUM_TYPE_TODO;
        }
        
        return null;
    }
    
    /**
     * Analyze text to determine album type
     */
    private String analyzeText(String text) {
        int shoppingScore = 0;
        int ticketScore = 0;
        int todoScore = 0;
        
        // Check shopping patterns
        for (Pattern pattern : SHOPPING_PATTERNS) {
            if (pattern.matcher(text).find()) {
                shoppingScore++;
            }
        }
        
        // Check ticket patterns
        for (Pattern pattern : TICKET_PATTERNS) {
            if (pattern.matcher(text).find()) {
                ticketScore++;
            }
        }
        
        // Check todo patterns
        for (Pattern pattern : TODO_PATTERNS) {
            if (pattern.matcher(text).find()) {
                todoScore++;
            }
        }
        
        // Determine the best match
        int maxScore = Math.max(shoppingScore, Math.max(ticketScore, todoScore));
        
        if (maxScore >= 2) {  // Require at least 2 matches for confidence
            if (shoppingScore == maxScore) {
                return ALBUM_TYPE_SHOPPING_LIST;
            } else if (ticketScore == maxScore) {
                return ALBUM_TYPE_TICKET;
            } else if (todoScore == maxScore) {
                return ALBUM_TYPE_TODO;
            }
        }
        
        return null;
    }
    
    /**
     * Extract relevant information from ticket screenshots
     */
    public TicketInfo extractTicketInfo(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        TicketInfo info = new TicketInfo();
        
        // Extract date
        Pattern datePattern = Pattern.compile("(?i)(?:date|depart|arrive)[:\\s]*(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})");
        Matcher dateMatcher = datePattern.matcher(text);
        if (dateMatcher.find()) {
            info.date = dateMatcher.group(1);
        }
        
        // Extract time
        Pattern timePattern = Pattern.compile("(?i)(?:time|depart|arrive)[:\\s]*(\\d{1,2}:\\d{2}\\s*(?:AM|PM)?)");
        Matcher timeMatcher = timePattern.matcher(text);
        if (timeMatcher.find()) {
            info.time = timeMatcher.group(1);
        }
        
        // Extract seat/gate
        Pattern seatPattern = Pattern.compile("(?i)(?:seat|gate)[:\\s]*([A-Z]?\\d+[A-Z]?)");
        Matcher seatMatcher = seatPattern.matcher(text);
        if (seatMatcher.find()) {
            info.seat = seatMatcher.group(1);
        }
        
        // Extract PNR/Booking reference
        Pattern pnrPattern = Pattern.compile("(?i)(?:PNR|booking|ref)[:\\s]*([A-Z0-9]{6,10})");
        Matcher pnrMatcher = pnrPattern.matcher(text);
        if (pnrMatcher.find()) {
            info.bookingRef = pnrMatcher.group(1);
        }
        
        return info;
    }
    
    /**
     * Extract shopping list items from text
     */
    public String[] extractShoppingItems(String text) {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }
        
        // Simple extraction - look for lines with quantities or prices
        String[] lines = text.split("\\n");
        java.util.List<String> items = new java.util.ArrayList<>();
        
        for (String line : lines) {
            if (line.matches(".*\\d+.*") || line.matches(".*\\$.*")) {
                items.add(line.trim());
            }
        }
        
        return items.toArray(new String[0]);
    }
    
    /**
     * Extract todo items from text
     */
    public String[] extractTodoItems(String text) {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }
        
        // Look for checkbox patterns or bullet points
        Pattern itemPattern = Pattern.compile("(?m)^[\\s]*([\\[\\-•]\\s*.+)$");
        Matcher matcher = itemPattern.matcher(text);
        
        java.util.List<String> items = new java.util.ArrayList<>();
        while (matcher.find()) {
            items.add(matcher.group(1).trim());
        }
        
        return items.toArray(new String[0]);
    }
    
    /**
     * Ticket information holder
     */
    public static class TicketInfo {
        public String date;
        public String time;
        public String seat;
        public String bookingRef;
        public String from;
        public String to;
        public String carrier;
        
        @Override
        public String toString() {
            return "TicketInfo{" +
                    "date='" + date + '\'' +
                    ", time='" + time + '\'' +
                    ", seat='" + seat + '\'' +
                    ", bookingRef='" + bookingRef + '\'' +
                    '}';
        }
    }
}
