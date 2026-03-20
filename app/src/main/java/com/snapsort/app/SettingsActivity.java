package com.snapsort.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Settings Activity for SnapSort
 */
public class SettingsActivity extends AppCompatActivity {
    
    private MaterialToolbar toolbar;
    private SwitchMaterial autoScanSwitch;
    private SwitchMaterial notificationsSwitch;
    private SwitchMaterial darkModeSwitch;
    private Button upgradeProButton;
    private TextView versionText;
    
    private SharedPreferences preferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        setupToolbar();
        setupViews();
        loadSettings();
        setupListeners();
    }
    
    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupViews() {
        autoScanSwitch = findViewById(R.id.autoScanSwitch);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        upgradeProButton = findViewById(R.id.upgradeProButton);
        versionText = findViewById(R.id.versionText);
        
        // Set version
        try {
            String versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
            versionText.setText("Version " + versionName);
        } catch (Exception e) {
            versionText.setText("Version 1.0");
        }
    }
    
    private void loadSettings() {
        autoScanSwitch.setChecked(preferences.getBoolean("auto_scan", true));
        notificationsSwitch.setChecked(preferences.getBoolean("notifications", true));
        darkModeSwitch.setChecked(preferences.getBoolean("dark_mode", false));
    }
    
    private void setupListeners() {
        autoScanSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("auto_scan", isChecked).apply();
        });
        
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("notifications", isChecked).apply();
        });
        
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("dark_mode", isChecked).apply();
            // Apply theme change
            applyDarkMode(isChecked);
        });
        
        upgradeProButton.setOnClickListener(v -> {
            // Show Pro upgrade dialog
            showProUpgradeDialog();
        });
    }
    
    private void applyDarkMode(boolean enabled) {
        // This would apply the dark theme
        // In a real implementation, you'd use AppCompatDelegate.setDefaultNightMode()
        if (enabled) {
            // Apply dark theme
        } else {
            // Apply light theme
        }
    }
    
    private void showProUpgradeDialog() {
        // In a real implementation, this would show a purchase dialog
        // using Google Play Billing Library
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Upgrade to SnapSort Pro")
                .setMessage("Unlock premium features:\n\n" +
                        "• Encrypted cloud backup ($1.99/month)\n" +
                        "• Advanced AI models\n" +
                        "• Priority support\n\n" +
                        "One-time purchase: $2.99")
                .setPositiveButton("Purchase", (dialog, which) -> {
                    // Initiate purchase flow
                    // This would use Google Play Billing Library
                })
                .setNegativeButton("Maybe Later", null)
                .show();
    }
}
