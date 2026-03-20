package com.snapsort.app;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.snapsort.app.model.CountryPricing;
import com.snapsort.app.model.CountryPricing.CountryInfo;
import com.snapsort.app.model.CountryPricing.ProPrice;
import com.snapsort.app.util.BillingManager;
import java.util.List;

/**
 * Pro Upgrade Activity with country-specific pricing
 */
public class ProUpgradeActivity extends AppCompatActivity {
    
    private Spinner countrySpinner;
    private TextView priceText;
    private MaterialButton purchaseOneTimeButton;
    private MaterialButton purchaseSubscriptionButton;
    private TextView restorePurchaseText;
    
    private BillingManager billingManager;
    private CountryInfo[] countries;
    private String selectedCountryCode = "DEFAULT";
    private ProductDetails proProductDetails;
    private ProductDetails cloudProductDetails;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_upgrade);
        
        setupToolbar();
        setupViews();
        setupBillingManager();
        setupCountrySpinner();
        setupListeners();
        updatePriceDisplay();
    }
    
    private void setupBillingManager() {
        billingManager = new BillingManager(this, new BillingManager.BillingListener() {
            @Override
            public void onBillingInitialized() {
                // Billing ready, query product details
                queryProductDetails();
            }
            
            @Override
            public void onPurchaseSuccess(@NonNull Purchase purchase) {
                showPurchaseSuccess("🎉 Purchase successful! Pro features unlocked.");
            }
            
            @Override
            public void onPurchaseFailed(int errorCode, String message) {
                if (errorCode != com.android.billingclient.api.BillingClient.BillingResponseCode.USER_CANCELED) {
                    Toast.makeText(ProUpgradeActivity.this, 
                        "Purchase failed: " + message, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onPurchasesRestored(@NonNull List<Purchase> purchases) {
                if (!purchases.isEmpty()) {
                    showPurchaseSuccess("🎉 Purchases restored! Welcome back to Pro.");
                }
            }
        });
        
        billingManager.startConnection();
    }
    
    private void queryProductDetails() {
        billingManager.queryProductDetails(productDetailsList -> {
            runOnUiThread(() -> {
                for (ProductDetails details : productDetailsList) {
                    if (details.getProductId().equals(BillingManager.PRODUCT_PRO_ONE_TIME)) {
                        proProductDetails = details;
                    } else if (details.getProductId().equals(BillingManager.PRODUCT_CLOUD_MONTHLY)) {
                        cloudProductDetails = details;
                    }
                }
                updatePricesFromPlayStore();
            });
        });
    }
    
    private void updatePricesFromPlayStore() {
        // Update button text with real prices from Play Store
        if (proProductDetails != null && proProductDetails.getOneTimePurchaseOfferDetails() != null) {
            String price = proProductDetails.getOneTimePurchaseOfferDetails().getFormattedPrice();
            purchaseOneTimeButton.setText("Purchase Pro - " + price);
            priceText.setText(price);
        }
        
        if (cloudProductDetails != null && 
            cloudProductDetails.getSubscriptionOfferDetails() != null &&
            !cloudProductDetails.getSubscriptionOfferDetails().isEmpty()) {
            String price = cloudProductDetails.getSubscriptionOfferDetails()
                    .get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
            purchaseSubscriptionButton.setText("Cloud Backup - " + price);
        }
    }
    
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupViews() {
        countrySpinner = findViewById(R.id.countrySpinner);
        priceText = findViewById(R.id.priceText);
        purchaseOneTimeButton = findViewById(R.id.purchaseOneTimeButton);
        purchaseSubscriptionButton = findViewById(R.id.purchaseSubscriptionButton);
        restorePurchaseText = findViewById(R.id.restorePurchaseText);
        
        countries = CountryPricing.getAvailableCountries();
    }
    
    private void setupCountrySpinner() {
        ArrayAdapter<CountryInfo> adapter = new ArrayAdapter<CountryInfo>(
                this,
                android.R.layout.simple_spinner_item,
                countries
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextSize(16);
                textView.setTextColor(getResources().getColor(R.color.on_surface));
                return textView;
            }
            
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setTextSize(16);
                textView.setTextColor(getResources().getColor(R.color.on_surface));
                textView.setPadding(16, 16, 16, 16);
                return textView;
            }
        };
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);
        
        // Set default selection based on device locale
        String deviceCountry = CountryPricing.getCountryCode(getResources().getConfiguration().locale.toString());
        for (int i = 0; i < countries.length; i++) {
            if (countries[i].code.equals(deviceCountry)) {
                countrySpinner.setSelection(i);
                selectedCountryCode = deviceCountry;
                break;
            }
        }
    }
    
    private void setupListeners() {
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountryCode = countries[position].code;
                if ("OTHER".equals(selectedCountryCode)) {
                    selectedCountryCode = "DEFAULT";
                }
                // Note: Real prices come from Play Store, not CountryPricing
                // This is just for display fallback
                updatePriceDisplay();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        purchaseOneTimeButton.setOnClickListener(v -> {
            if (billingManager.isProPurchased()) {
                Toast.makeText(this, "You already own Pro!", Toast.LENGTH_SHORT).show();
            } else {
                billingManager.launchBillingFlow(this, BillingManager.PRODUCT_PRO_ONE_TIME);
            }
        });
        
        purchaseSubscriptionButton.setOnClickListener(v -> {
            if (billingManager.isCloudSubscribed()) {
                Toast.makeText(this, "You already have Cloud Backup!", Toast.LENGTH_SHORT).show();
            } else {
                billingManager.launchBillingFlow(this, BillingManager.PRODUCT_CLOUD_MONTHLY);
            }
        });
        
        restorePurchaseText.setOnClickListener(v -> {
            billingManager.queryPurchases();
        });
    }
    
    private void updatePriceDisplay() {
        // Fallback prices if Play Store not connected
        if (proProductDetails == null) {
            ProPrice oneTimePrice = CountryPricing.getProOneTimePrice(selectedCountryCode);
            priceText.setText(oneTimePrice.displayPrice);
        }
    }
    
    private void showPurchaseSuccess(String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("🎉 Success!")
                .setMessage(message)
                .setPositiveButton("Awesome!", (dialog, which) -> {
                    finish();
                })
                .show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingManager != null) {
            billingManager.endConnection();
        }
    }
}
