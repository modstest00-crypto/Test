package com.snapsort.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.billingclient.api.*;
import java.util.List;

/**
 * Google Play Billing Manager for SnapSort Pro
 * Handles in-app purchases and subscriptions
 */
public class BillingManager implements PurchasesUpdatedListener {
    
    private static final String TAG = "BillingManager";
    
    // Product IDs (MUST match what you create in Google Play Console)
    public static final String PRODUCT_PRO_ONE_TIME = "snapsort_pro_lifetime";
    public static final String PRODUCT_CLOUD_MONTHLY = "snapsort_cloud_monthly";
    
    // Shared Preferences key for purchased status
    private static final String PREFS_NAME = "snapsort_billing";
    private static final String KEY_PRO_PURCHASED = "pro_purchased";
    private static final String KEY_CLOUD_SUBSCRIBED = "cloud_subscribed";
    
    private BillingClient billingClient;
    private Context context;
    private BillingListener listener;
    private boolean isServiceConnected = false;
    
    /**
     * Listener interface for billing events
     */
    public interface BillingListener {
        void onBillingInitialized();
        void onPurchaseSuccess(@NonNull Purchase purchase);
        void onPurchaseFailed(int errorCode, @Nullable String message);
        void onPurchasesRestored(@NonNull List<Purchase> purchases);
    }
    
    public BillingManager(Context context, BillingListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
        
        billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build();
    }
    
    /**
     * Start connection to Google Play Billing service
     */
    public void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing client connected");
                    isServiceConnected = true;
                    if (listener != null) {
                        listener.onBillingInitialized();
                    }
                    // Query existing purchases
                    queryPurchases();
                } else {
                    Log.e(TAG, "Billing setup failed: " + billingResult.getDebugMessage());
                    isServiceConnected = false;
                }
            }
            
            @Override
            public void onBillingServiceDisconnected() {
                Log.d(TAG, "Billing client disconnected");
                isServiceConnected = false;
                // Try to reconnect after a delay
                reconnectWithDelay(5000);
            }
        });
    }
    
    /**
     * Reconnect to billing service after delay
     */
    private void reconnectWithDelay(long delayMillis) {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (billingClient != null && !billingClient.isReady()) {
                startConnection();
            }
        }, delayMillis);
    }
    
    /**
     * Get product details including localized prices
     */
    public void queryProductDetails(@NonNull ProductDetailsListener productListener) {
        if (!isServiceConnected) {
            Log.w(TAG, "Billing client not connected");
            return;
        }
        
        List<QueryProductDetailsParams.Product> productList = List.of(
            QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_PRO_ONE_TIME)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),
            QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_CLOUD_MONTHLY)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
        );
        
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();
        
        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (listener != null && productListener != null) {
                    productListener.onProductDetailsRetrieved(productDetailsList);
                }
            } else {
                Log.e(TAG, "Query product details failed: " + billingResult.getDebugMessage());
            }
        });
    }
    
    /**
     * Launch billing flow for a specific product
     */
    public void launchBillingFlow(@NonNull Activity activity, @NonNull String productId) {
        if (!isServiceConnected) {
            Log.w(TAG, "Billing client not connected");
            return;
        }
        
        // Determine product type
        String productType = productId.contains("cloud") ? 
                BillingClient.ProductType.SUBS : BillingClient.ProductType.INAPP;
        
        // Query product details first
        List<QueryProductDetailsParams.Product> productList = List.of(
            QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(productType)
                    .build()
        );
        
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();
        
        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && 
                !productDetailsList.isEmpty()) {
                
                ProductDetails productDetails = productDetailsList.get(0);
                BillingFlowParams billingFlowParams;
                
                if (productType.equals(BillingClient.ProductType.SUBS)) {
                    // Subscription product
                    if (productDetails.getSubscriptionOfferDetails() != null && 
                        !productDetails.getSubscriptionOfferDetails().isEmpty()) {
                        String offerToken = productDetails.getSubscriptionOfferDetails()
                                .get(0).getOfferToken();
                        
                        billingFlowParams = BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(List.of(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                            .setProductDetails(productDetails)
                                            .setOfferToken(offerToken)
                                            .build()
                                ))
                                .build();
                    } else {
                        Log.e(TAG, "No subscription offer details found");
                        return;
                    }
                } else {
                    // One-time purchase
                    if (productDetails.getOneTimePurchaseOfferDetails() != null) {
                        String offerToken = productDetails.getOneTimePurchaseOfferDetails()
                                .getOfferToken();
                        
                        billingFlowParams = BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(List.of(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                            .setProductDetails(productDetails)
                                            .setOfferToken(offerToken)
                                            .build()
                                ))
                                .build();
                    } else {
                        Log.e(TAG, "No one-time purchase offer details found");
                        return;
                    }
                }
                
                // Launch the billing flow
                BillingResult billingResult1 = billingClient.launchBillingFlow(activity, billingFlowParams);
                
                if (billingResult1.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "Launch billing flow failed: " + billingResult1.getDebugMessage());
                }
            } else {
                Log.e(TAG, "Product details query failed: " + billingResult.getDebugMessage());
            }
        });
    }
    
    /**
     * Handle purchase updates from Google Play
     */
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        int responseCode = billingResult.getResponseCode();
        
        if (responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            // Purchase successful
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // User canceled the purchase
            Log.d(TAG, "Purchase canceled by user");
            if (listener != null) {
                listener.onPurchaseFailed(BillingClient.BillingResponseCode.USER_CANCELED, "Purchase canceled");
            }
        } else if (responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            // Item already owned
            Log.d(TAG, "Item already owned");
            queryPurchases();
        } else {
            // Other error
            Log.e(TAG, "Purchase failed: " + billingResult.getDebugMessage());
            if (listener != null) {
                listener.onPurchaseFailed(responseCode, billingResult.getDebugMessage());
            }
        }
    }
    
    /**
     * Handle a successful purchase
     */
    private void handlePurchase(@NonNull Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            Log.d(TAG, "Purchase successful: " + purchase.getProducts());
            
            // Grant entitlement to user
            grantEntitlement(purchase);
            
            // Acknowledge purchase (REQUIRED within 3 days or Google will refund)
            acknowledgePurchase(purchase);
            
            // Notify listener
            if (listener != null) {
                listener.onPurchaseSuccess(purchase);
            }
        }
    }
    
    /**
     * Grant features to user based on purchase
     */
    private void grantEntitlement(@NonNull Purchase purchase) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        for (String productId : purchase.getProducts()) {
            if (productId.equals(PRODUCT_PRO_ONE_TIME)) {
                // Unlock Pro features
                editor.putBoolean(KEY_PRO_PURCHASED, true);
                Log.d(TAG, "Pro features unlocked");
            } else if (productId.equals(PRODUCT_CLOUD_MONTHLY)) {
                // Unlock cloud backup
                editor.putBoolean(KEY_CLOUD_SUBSCRIBED, true);
                Log.d(TAG, "Cloud backup unlocked");
            }
        }
        
        editor.apply();
    }
    
    /**
     * Acknowledge purchase (required for non-consumable items)
     */
    private void acknowledgePurchase(@NonNull Purchase purchase) {
        if (purchase.isAcknowledged()) {
            Log.d(TAG, "Purchase already acknowledged");
            return;
        }
        
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        
        billingClient.acknowledgePurchase(params, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged successfully");
            } else {
                Log.e(TAG, "Acknowledge purchase failed: " + billingResult.getDebugMessage());
            }
        });
    }
    
    /**
     * Query existing purchases (for restoring)
     */
    public void queryPurchases() {
        if (!isServiceConnected) {
            Log.w(TAG, "Billing client not connected");
            return;
        }
        
        // Query in-app purchases
        PurchasesQueryParams inAppParams = PurchasesQueryParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build();
        
        billingClient.queryPurchasesAsync(inAppParams, (billingResult, inAppPurchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && 
                inAppPurchases != null && !inAppPurchases.isEmpty()) {
                
                if (listener != null) {
                    listener.onPurchasesRestored(inAppPurchases);
                }
                
                // Grant entitlements for restored purchases
                for (Purchase purchase : inAppPurchases) {
                    grantEntitlement(purchase);
                }
            }
        });
        
        // Query subscriptions
        PurchasesQueryParams subsParams = PurchasesQueryParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build();
        
        billingClient.queryPurchasesAsync(subsParams, (billingResult, subPurchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && 
                subPurchases != null && !subPurchases.isEmpty()) {
                
                if (listener != null) {
                    listener.onPurchasesRestored(subPurchases);
                }
                
                // Grant entitlements for restored subscriptions
                for (Purchase purchase : subPurchases) {
                    grantEntitlement(purchase);
                }
            }
        });
    }
    
    /**
     * Check if Pro features are purchased
     */
    public boolean isProPurchased() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_PRO_PURCHASED, false);
    }
    
    /**
     * Check if cloud backup is subscribed
     */
    public boolean isCloudSubscribed() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_CLOUD_SUBSCRIBED, false);
    }
    
    /**
     * Get localized price for a product
     */
    public interface ProductDetailsListener {
        void onProductDetailsRetrieved(@NonNull List<ProductDetails> productDetailsList);
    }
    
    /**
     * Clean up resources
     */
    public void endConnection() {
        if (billingClient != null && billingClient.isReady()) {
            billingClient.endConnection();
            isServiceConnected = false;
            Log.d(TAG, "Billing client connection ended");
        }
    }
}
