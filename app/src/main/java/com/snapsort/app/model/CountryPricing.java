package com.snapsort.app.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Country-specific pricing for Pro features
 * Prices are localized for different countries/regions
 */
public class CountryPricing {
    
    // Pro One-Time Purchase Prices by Country Code
    public static final Map<String, ProPrice> PRO_ONE_TIME_PRICES = new HashMap<>();
    
    // Cloud Backup Subscription Prices by Country Code
    public static final Map<String, ProPrice> CLOUD_SUBSCRIPTION_PRICES = new HashMap<>();
    
    static {
        // North America
        PRO_ONE_TIME_PRICES.put("US", new ProPrice("USD", "$2.99", 2.99));
        PRO_ONE_TIME_PRICES.put("CA", new ProPrice("CAD", "$3.99", 3.99));
        PRO_ONE_TIME_PRICES.put("MX", new ProPrice("MXN", "$59.00", 59.00));
        
        // Europe
        PRO_ONE_TIME_PRICES.put("GB", new ProPrice("GBP", "£2.49", 2.49));
        PRO_ONE_TIME_PRICES.put("DE", new ProPrice("EUR", "€2.99", 2.99));
        PRO_ONE_TIME_PRICES.put("FR", new ProPrice("EUR", "€2.99", 2.99));
        PRO_ONE_TIME_PRICES.put("IT", new ProPrice("EUR", "€2.99", 2.99));
        PRO_ONE_TIME_PRICES.put("ES", new ProPrice("EUR", "€2.99", 2.99));
        PRO_ONE_TIME_PRICES.put("NL", new ProPrice("EUR", "€2.99", 2.99));
        PRO_ONE_TIME_PRICES.put("SE", new ProPrice("SEK", "29.00 kr", 29.00));
        PRO_ONE_TIME_PRICES.put("NO", new ProPrice("NOK", "29.00 kr", 29.00));
        PRO_ONE_TIME_PRICES.put("DK", new ProPrice("DKK", "22.00 kr", 22.00));
        PRO_ONE_TIME_PRICES.put("PL", new ProPrice("PLN", "12.99 zł", 12.99));
        
        // Asia
        PRO_ONE_TIME_PRICES.put("IN", new ProPrice("INR", "₹249", 249.00));
        PRO_ONE_TIME_PRICES.put("CN", new ProPrice("CNY", "¥22", 22.00));
        PRO_ONE_TIME_PRICES.put("JP", new ProPrice("JPY", "¥380", 380.00));
        PRO_ONE_TIME_PRICES.put("KR", new ProPrice("KRW", "₩3,900", 3900.00));
        PRO_ONE_TIME_PRICES.put("SG", new ProPrice("SGD", "$3.98", 3.98));
        PRO_ONE_TIME_PRICES.put("MY", new ProPrice("MYR", "RM12.90", 12.90));
        PRO_ONE_TIME_PRICES.put("TH", new ProPrice("THB", "฿99", 99.00));
        PRO_ONE_TIME_PRICES.put("VN", new ProPrice("VND", "69,000₫", 69000.00));
        PRO_ONE_TIME_PRICES.put("PH", new ProPrice("PHP", "₱169", 169.00));
        PRO_ONE_TIME_PRICES.put("ID", new ProPrice("IDR", "Rp45,000", 45000.00));
        
        // Oceania
        PRO_ONE_TIME_PRICES.put("AU", new ProPrice("AUD", "$4.49", 4.49));
        PRO_ONE_TIME_PRICES.put("NZ", new ProPrice("NZD", "$4.99", 4.99));
        
        // South America
        PRO_ONE_TIME_PRICES.put("BR", new ProPrice("BRL", "R$14.90", 14.90));
        PRO_ONE_TIME_PRICES.put("AR", new ProPrice("ARS", "$2,999", 2999.00));
        PRO_ONE_TIME_PRICES.put("CL", new ProPrice("CLP", "$2,990", 2990.00));
        PRO_ONE_TIME_PRICES.put("CO", new ProPrice("COP", "$12,900", 12900.00));
        
        // Middle East
        PRO_ONE_TIME_PRICES.put("AE", new ProPrice("AED", "10.99 د.إ", 10.99));
        PRO_ONE_TIME_PRICES.put("SA", new ProPrice("SAR", "11.99 ﷼", 11.99));
        PRO_ONE_TIME_PRICES.put("IL", new ProPrice("ILS", "₪10.90", 10.90));
        PRO_ONE_TIME_PRICES.put("TR", new ProPrice("TRY", "₺89.90", 89.90));
        
        // Africa
        PRO_ONE_TIME_PRICES.put("ZA", new ProPrice("ZAR", "R54.99", 54.99));
        PRO_ONE_TIME_PRICES.put("NG", new ProPrice("NGN", "₦4,490", 4490.00));
        PRO_ONE_TIME_PRICES.put("KE", new ProPrice("KES", "KSh449", 449.00));
        PRO_ONE_TIME_PRICES.put("EG", new ProPrice("EGP", "E£139", 139.00));
        
        // Add Nigeria to cloud subscription
        CLOUD_SUBSCRIPTION_PRICES.put("NG", new ProPrice("NGN", "₦1,490/mo", 1490.00));
        
        // Default (USD)
        PRO_ONE_TIME_PRICES.put("DEFAULT", new ProPrice("USD", "$2.99", 2.99));
        
        // Cloud Subscription Prices (Monthly)
        CLOUD_SUBSCRIPTION_PRICES.put("US", new ProPrice("USD", "$1.99/mo", 1.99));
        CLOUD_SUBSCRIPTION_PRICES.put("GB", new ProPrice("GBP", "£1.69/mo", 1.69));
        CLOUD_SUBSCRIPTION_PRICES.put("DE", new ProPrice("EUR", "€1.99/mo", 1.99));
        CLOUD_SUBSCRIPTION_PRICES.put("IN", new ProPrice("INR", "₹169/mo", 169.00));
        CLOUD_SUBSCRIPTION_PRICES.put("JP", new ProPrice("JPY", "¥250/mo", 250.00));
        CLOUD_SUBSCRIPTION_PRICES.put("CN", new ProPrice("CNY", "¥15/mo", 15.00));
        CLOUD_SUBSCRIPTION_PRICES.put("BR", new ProPrice("BRL", "R$9.90/mo", 9.90));
        CLOUD_SUBSCRIPTION_PRICES.put("AU", new ProPrice("AUD", "$2.99/mo", 2.99));
        CLOUD_SUBSCRIPTION_PRICES.put("CA", new ProPrice("CAD", "$2.69/mo", 2.69));
        CLOUD_SUBSCRIPTION_PRICES.put("DEFAULT", new ProPrice("USD", "$1.99/mo", 1.99));
    }
    
    /**
     * Get the country code from locale
     */
    public static String getCountryCode(String locale) {
        if (locale == null || locale.isEmpty()) {
            return "DEFAULT";
        }
        String[] parts = locale.split("_");
        if (parts.length >= 2) {
            String countryCode = parts[1].toUpperCase();
            if (PRO_ONE_TIME_PRICES.containsKey(countryCode)) {
                return countryCode;
            }
        }
        // Try to match by language
        String lang = parts[0].toLowerCase();
        switch (lang) {
            case "en": return "US";
            case "zh": return "CN";
            case "es": return "ES";
            case "fr": return "FR";
            case "de": return "DE";
            case "ja": return "JP";
            case "ko": return "KR";
            case "pt": return "BR";
            case "ru": return "RU";
            case "ar": return "AE";
            case "hi": return "IN";
            default: return "DEFAULT";
        }
    }
    
    /**
     * Get Pro one-time purchase price for a country
     */
    public static ProPrice getProOneTimePrice(String countryCode) {
        ProPrice price = PRO_ONE_TIME_PRICES.get(countryCode);
        return price != null ? price : PRO_ONE_TIME_PRICES.get("DEFAULT");
    }
    
    /**
     * Get cloud subscription price for a country
     */
    public static ProPrice getCloudSubscriptionPrice(String countryCode) {
        ProPrice price = CLOUD_SUBSCRIPTION_PRICES.get(countryCode);
        return price != null ? price : CLOUD_SUBSCRIPTION_PRICES.get("DEFAULT");
    }
    
    /**
     * Get all available countries for selection
     */
    public static CountryInfo[] getAvailableCountries() {
        return new CountryInfo[] {
            new CountryInfo("US", "🇺🇸", "United States"),
            new CountryInfo("GB", "🇬🇧", "United Kingdom"),
            new CountryInfo("DE", "🇩🇪", "Germany"),
            new CountryInfo("FR", "🇫🇷", "France"),
            new CountryInfo("IT", "🇮🇹", "Italy"),
            new CountryInfo("ES", "🇪🇸", "Spain"),
            new CountryInfo("IN", "🇮🇳", "India"),
            new CountryInfo("CN", "🇨🇳", "China"),
            new CountryInfo("JP", "🇯🇵", "Japan"),
            new CountryInfo("KR", "🇰🇷", "South Korea"),
            new CountryInfo("BR", "🇧🇷", "Brazil"),
            new CountryInfo("AU", "🇦🇺", "Australia"),
            new CountryInfo("CA", "🇨🇦", "Canada"),
            new CountryInfo("MX", "🇲🇽", "Mexico"),
            new CountryInfo("SG", "🇸🇬", "Singapore"),
            new CountryInfo("AE", "🇦🇪", "United Arab Emirates"),
            new CountryInfo("ZA", "🇿🇦", "South Africa"),
            new CountryInfo("NG", "🇳🇬", "Nigeria"),
            new CountryInfo("OTHER", "🌍", "Other Countries")
        };
    }
    
    /**
     * Price information holder
     */
    public static class ProPrice {
        public final String currency;
        public final String displayPrice;
        public final double amount;
        
        public ProPrice(String currency, String displayPrice, double amount) {
            this.currency = currency;
            this.displayPrice = displayPrice;
            this.amount = amount;
        }
        
        @Override
        public String toString() {
            return displayPrice;
        }
    }
    
    /**
     * Country information holder
     */
    public static class CountryInfo {
        public final String code;
        public final String flag;
        public final String name;
        
        public CountryInfo(String code, String flag, String name) {
            this.code = code;
            this.flag = flag;
            this.name = name;
        }
        
        @Override
        public String toString() {
            return flag + " " + name;
        }
    }
}
