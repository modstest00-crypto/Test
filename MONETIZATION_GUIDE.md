# 💰 SnapSort Monetization Guide

## How to Earn Money from Pro Upgrades

### Revenue Streams

| Product | Price (USD) | Type | Your Earnings (85%) |
|---------|-------------|------|---------------------|
| Pro One-Time | $2.99 | One-time purchase | ~$2.54 per sale |
| Cloud Backup | $1.99/month | Subscription | ~$1.69 per month |

**Google Play Fee:** 15% for first $1M USD per year (30% after)

---

## 📦 Step 1: Add Google Play Billing Library

### Update `app/build.gradle`

```gradle
dependencies {
    // ... existing dependencies ...
    
    // Google Play Billing Library
    implementation 'com.android.billingclient:billing:6.2.1'
    implementation 'com.android.billingclient:billing-ktx:6.2.1'
}
```

---

## 🎯 Step 2: Create Products in Google Play Console

### 2.1 Create In-App Products

1. Go to **Google Play Console** → Your App → **Monetize** → **In-app products**
2. Click **Create in-app product**

### One-Time Purchase (Pro)
```
Product ID: pro_one_time
Name: SnapSort Pro - Lifetime
Description: Unlock all premium features forever
Default Price: $2.99
Type: One-time purchase
```

### Subscription (Cloud Backup)
```
Product ID: cloud_backup_monthly
Name: Cloud Backup - Monthly
Description: 100GB encrypted cloud storage
Default Price: $1.99/month
Type: Subscription
Subscription period: Monthly
Auto-renewing: Yes
```

### 2.2 Set Up Country-Specific Pricing

In Play Console, for each product:
1. Click on the product
2. Scroll to **Country-specific prices**
3. Click **Add country/region**
4. Set local prices (suggested prices auto-filled)

**Example Pricing:**
| Country | Pro One-Time | Cloud Monthly |
|---------|--------------|---------------|
| 🇺🇸 USA | $2.99 | $1.99 |
| 🇬🇧 UK | £2.49 | £1.69 |
| 🇪🇺 EU | €2.99 | €1.99 |
| 🇮🇳 India | ₹249 | ₹169 |
| 🇳🇬 Nigeria | ₦4,490 | ₦1,490 |
| 🇯🇵 Japan | ¥380 | ¥250 |
| 🇧🇷 Brazil | R$14.90 | R$9.90 |

---

## 💻 Step 3: Implement Billing in Code

### Create BillingManager.java

```java
package com.snapsort.app.util;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import com.android.billingclient.api.*;
import java.util.List;

public class BillingManager implements PurchasesUpdatedListener {
    
    private BillingClient billingClient;
    private Context context;
    private BillingListener listener;
    
    // Product IDs (must match Play Console)
    public static final String PRODUCT_PRO_ONE_TIME = "pro_one_time";
    public static final String PRODUCT_CLOUD_MONTHLY = "cloud_backup_monthly";
    
    public interface BillingListener {
        void onPurchaseSuccess(String productId);
        void onPurchaseFailed(int errorCode, String message);
        void onPurchasesUpdated(List<Purchase> purchases);
    }
    
    public BillingManager(Context context, BillingListener listener) {
        this.context = context;
        this.listener = listener;
        
        billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build();
    }
    
    public void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Billing client ready
                }
            }
            
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart connection
            }
        });
    }
    
    public void queryProductDetails(String... productIds) {
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    List.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(PRODUCT_PRO_ONE_TIME)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build(),
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(PRODUCT_CLOUD_MONTHLY)
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build()
                    )
                )
                .build();
        
        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (listener != null && !productDetailsList.isEmpty()) {
                // Product details retrieved (includes localized prices)
                for (ProductDetails details : productDetailsList) {
                    String localizedPrice = details.getOneTimePurchaseOfferDetails() != null ? 
                        details.getOneTimePurchaseOfferDetails().getFormattedPrice() :
                        details.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
                }
            }
        });
    }
    
    public void launchBillingFlow(Activity activity, String productId) {
        // Get product details first, then launch billing flow
        queryProductDetailsAndLaunch(activity, productId);
    }
    
    private void queryProductDetailsAndLaunch(Activity activity, String productId) {
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(List.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(productId.contains("monthly") ? 
                            BillingClient.ProductType.SUBS : BillingClient.ProductType.INAPP)
                        .build()
                ))
                .build();
        
        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (!productDetailsList.isEmpty()) {
                ProductDetails productDetails = productDetailsList.get(0);
                
                BillingFlowParams billingFlowParams;
                if (productId.contains("monthly")) {
                    // Subscription
                    String offerToken = productDetails.getSubscriptionOfferDetails().get(0).getOfferToken();
                    billingFlowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(
                                List.of(BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .setOfferToken(offerToken)
                                    .build())
                            )
                            .build();
                } else {
                    // One-time purchase
                    String offerToken = productDetails.getOneTimePurchaseOfferDetails().getOfferToken();
                    billingFlowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(
                                List.of(BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .setOfferToken(offerToken)
                                    .build())
                            )
                            .build();
                }
                
                billingClient.launchBillingFlow(activity, billingFlowParams);
            }
        });
    }
    
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            if (listener != null) {
                listener.onPurchaseFailed(BillingClient.BillingResponseCode.USER_CANCELED, "User canceled");
            }
        }
    }
    
    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to user
            if (listener != null) {
                listener.onPurchaseSuccess(purchase.getProducts().get(0));
            }
            
            // Acknowledge purchase (required within 3 days)
            acknowledgePurchase(purchase);
        }
    }
    
    private void acknowledgePurchase(Purchase purchase) {
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        
        billingClient.acknowledgePurchase(params, billingResult -> {
            // Purchase acknowledged
        });
    }
    
    public void queryPurchases() {
        // Check for existing purchases
        PurchasesQueryParams params = PurchasesQueryParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build();
        
        billingClient.queryPurchasesAsync(params, (billingResult, list) -> {
            if (listener != null && list != null) {
                listener.onPurchasesUpdated(list);
            }
        });
    }
    
    public void endConnection() {
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }
}
```

---

## 📱 Step 4: Update ProUpgradeActivity

```java
private BillingManager billingManager;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pro_upgrade);
    
    // Initialize billing
    billingManager = new BillingManager(this, new BillingManager.BillingListener() {
        @Override
        public void onPurchaseSuccess(String productId) {
            showPurchaseSuccess("Purchase successful!");
            // Unlock features
            if (productId.equals(BillingManager.PRODUCT_PRO_ONE_TIME)) {
                // Unlock Pro features
            } else if (productId.equals(BillingManager.PRODUCT_CLOUD_MONTHLY)) {
                // Unlock cloud backup
            }
        }
        
        @Override
        public void onPurchaseFailed(int errorCode, String message) {
            Toast.makeText(ProUpgradeActivity.this, 
                "Purchase failed: " + message, Toast.LENGTH_SHORT).show();
        }
        
        @Override
        public void onPurchasesUpdated(List<Purchase> purchases) {
            // Restore purchases
        }
    });
    
    billingManager.startConnection();
    
    // Update buttons with real prices from Play Store
    updatePricesFromPlayStore();
}

private void updatePricesFromPlayStore() {
    billingManager.queryProductDetails(
        BillingManager.PRODUCT_PRO_ONE_TIME,
        BillingManager.PRODUCT_CLOUD_MONTHLY
    );
}

private void initiateOneTimePurchase() {
    billingManager.launchBillingFlow(this, BillingManager.PRODUCT_PRO_ONE_TIME);
}

private void initiateSubscriptionPurchase() {
    billingManager.launchBillingFlow(this, BillingManager.PRODUCT_CLOUD_MONTHLY);
}

private void restorePurchase() {
    billingManager.queryPurchases();
}

@Override
protected void onDestroy() {
    super.onDestroy();
    if (billingManager != null) {
        billingManager.endConnection();
    }
}
```

---

## 💵 Step 5: How Money Flows

### Payment Flow

```
User → Google Play → Your Developer Account
  ↓         ↓              ↓
$2.99   $0.45 (15%)    $2.54 (85%)
```

### Payout Schedule

1. **Google collects** payment from user
2. **Google holds** for ~60 days (first payment)
3. **Monthly payouts** to your bank account
4. **Minimum threshold:** $100 USD (varies by country)

### Setting Up Payouts

1. Go to **Play Console** → **Setup** → **Payments profile**
2. Add your **business information**
3. Add **bank account** for deposits
4. Complete **tax information** (W-8BEN for non-US)

---

## 📊 Step 6: Track Earnings

### In Play Console

**Monetize** → **Reports** → **Financial reports**

See:
- Total revenue
- Number of purchases
- Refunds
- Subscriptions active/canceled
- Revenue by country

### Example Earnings Calculation

| Scenario | Monthly Users | Conversion | Revenue/Month |
|----------|---------------|------------|---------------|
| Small | 1,000 | 2% | ~$50 |
| Medium | 10,000 | 3% | ~$750 |
| Large | 100,000 | 5% | ~$12,500 |

**Formula:** `Users × Conversion% × Price × 85% = Your Earnings`

---

## 🚀 Step 7: Increase Conversions

### Best Practices

1. **Show value first** - Let users use free features
2. **Timing** - Show upgrade prompt after organizing 10+ screenshots
3. **Clear benefits** - List exactly what they get
4. **Social proof** - Show number of Pro users
5. **Limited offers** - "50% off first month"
6. **Free trial** - Offer 7-day free trial for subscription

### Update ProUpgradeActivity with Trial

```java
// In Play Console, create subscription with free trial
// Product ID: cloud_backup_monthly_trial
// Trial period: 7 days

// In code:
if (productId.contains("trial")) {
    // Launch trial subscription flow
}
```

---

## ⚠️ Important Requirements

### Must Have

1. **Google Play Developer Account** ($25 one-time fee)
2. **Merchant account** in Play Console
3. **Privacy Policy** URL
4. **Terms of Service** URL
5. **Refund policy** (Google handles refunds automatically)

### Compliance

- ✅ Clearly state what Pro includes
- ✅ Honor refunds (Google policy)
- ✅ Restore purchases option
- ✅ Cancel subscription anytime (user can cancel in Play Store)

---

## 🔄 Alternative: Other Payment Methods

If you don't want to use Google Play Billing:

### 1. Stripe (for web-based)
- Lower fees (2.9% + $0.30)
- But: Requires web payment flow
- Not recommended for in-app

### 2. PayPal
- Similar to Stripe
- Requires web redirect

### 3. RevenueCat (Recommended for simplicity)
- Wrapper around Play Billing + App Store
- Handles subscriptions, receipts, analytics
- Free up to $10k/month revenue
- Takes 1% after that

**RevenueCat Setup:**
```gradle
implementation 'com.revenuecat.purchases:purchases:6.+'
```

---

## 📈 Expected Revenue

### Realistic Projections

**Month 1-3:**
- 500 downloads
- 2% conversion = 10 Pro sales
- **Revenue: ~$25/month**

**Month 4-6:**
- 5,000 downloads
- 3% conversion = 150 Pro sales
- **Revenue: ~$380/month**

**Year 1:**
- 50,000 downloads
- 3% conversion = 1,500 Pro sales
- **Revenue: ~$3,800/month**

---

## ✅ Checklist to Start Earning

- [ ] Create Google Play Developer Account ($25)
- [ ] Set up merchant profile
- [ ] Create in-app products in Play Console
- [ ] Add Billing Library to app
- [ ] Implement BillingManager
- [ ] Test with license testing (Play Console → Setup → License testing)
- [ ] Submit app for review
- [ ] Add bank account for payouts
- [ ] Launch and monitor earnings!

---

## 🎯 Quick Start (Minimum Viable)

1. **Today:** Create Play Console account ($25)
2. **Day 2:** Create products, add billing code
3. **Day 3:** Test with license testers
4. **Day 4:** Submit for review
5. **Day 7:** App approved, start earning!

---

**Good luck with your monetization! 💰**
