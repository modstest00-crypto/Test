package com.snapsort.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.snapsort.app.adapter.AutoAlbumAdapter;
import com.snapsort.app.adapter.CategoryAdapter;
import com.snapsort.app.adapter.ScreenshotAdapter;
import com.snapsort.app.model.AutoAlbum;
import com.snapsort.app.model.Category;
import com.snapsort.app.model.Screenshot;
import com.snapsort.app.util.NaturalLanguageSearch;
import com.snapsort.app.viewmodel.MainViewModel;
import java.util.List;

/**
 * Main Activity for SnapSort app
 * Displays screenshots, categories, and provides organization features
 */
public class MainActivity extends AppCompatActivity implements
        CategoryAdapter.OnCategoryClickListener,
        ScreenshotAdapter.OnScreenshotClickListener,
        AutoAlbumAdapter.OnAlbumClickListener {
    
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    // UI Components
    private TextInputEditText searchEditText;
    private View clearSearchButton;
    private MaterialButton scanButton;
    private MaterialButton organizeButton;
    private FloatingActionButton shareFab;
    private View progressContainer;
    private LinearProgressIndicator progressIndicator;
    
    // RecyclerViews
    private androidx.recyclerview.widget.RecyclerView categoriesRecyclerView;
    private androidx.recyclerview.widget.RecyclerView recentImagesRecyclerView;
    
    // Adapters
    private CategoryAdapter categoryAdapter;
    private ScreenshotAdapter screenshotAdapter;
    private AutoAlbumAdapter autoAlbumAdapter;
    
    // ViewModel
    private MainViewModel viewModel;
    
    // Natural Language Search
    private NaturalLanguageSearch nlSearch;
    
    // Permission launcher
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Intent> settingsLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        nlSearch = new NaturalLanguageSearch();
        
        // Initialize permission launchers
        setupPermissionLaunchers();
        
        // Setup UI
        setupToolbar();
        setupViews();
        setupRecyclerViews();
        setupObservers();
        setupListeners();
        
        // Check permissions
        checkPermissions();
    }
    
    private void setupPermissionLaunchers() {
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        viewModel.scanScreenshots();
                    } else {
                        showPermissionDeniedDialog();
                    }
                }
        );
        
        settingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            viewModel.scanScreenshots();
                        }
                    }
                }
        );
    }
    
    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }
    
    private void setupViews() {
        searchEditText = findViewById(R.id.searchEditText);
        clearSearchButton = findViewById(R.id.clearSearchButton);
        scanButton = findViewById(R.id.scanButton);
        organizeButton = findViewById(R.id.organizeButton);
        shareFab = findViewById(R.id.shareFab);
        progressContainer = findViewById(R.id.progressContainer);
        progressIndicator = findViewById(R.id.progressIndicator);
    }
    
    private void setupRecyclerViews() {
        // Categories RecyclerView
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoryAdapter = new CategoryAdapter(this, this);
        categoriesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoriesRecyclerView.setAdapter(categoryAdapter);
        
        // Recent Images RecyclerView
        recentImagesRecyclerView = findViewById(R.id.recentImagesRecyclerView);
        screenshotAdapter = new ScreenshotAdapter(this, this);
        recentImagesRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 3));
        recentImagesRecyclerView.setAdapter(screenshotAdapter);
    }
    
    private void setupObservers() {
        // Observe screenshots
        viewModel.getScreenshots().observe(this, screenshots -> {
            screenshotAdapter.setScreenshots(screenshots);
            updateStats(screenshots.size(), viewModel.getOrganizedCount());
        });
        
        // Observe categories
        viewModel.getCategories().observe(this, categories -> {
            categoryAdapter.setCategories(categories);
        });
        
        // Observe auto albums
        viewModel.getAutoAlbums().observe(this, albums -> {
            updateAutoAlbums(albums);
        });
        
        // Observe scanning state
        viewModel.isScanning().observe(this, isScanning -> {
            progressContainer.setVisibility(isScanning ? View.VISIBLE : View.GONE);
            if (isScanning) {
                progressIndicator.setIndeterminate(true);
            }
        });
        
        // Observe organizing state
        viewModel.isOrganizing().observe(this, isOrganizing -> {
            if (isOrganizing) {
                progressContainer.setVisibility(View.VISIBLE);
                progressIndicator.setIndeterminate(false);
                progressIndicator.setProgress(viewModel.getOrganizeProgress());
            }
        });
        
        // Observe messages
        viewModel.getMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupListeners() {
        // Search text watcher
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    clearSearchButton.setVisibility(View.VISIBLE);
                    performSearch(query);
                } else {
                    clearSearchButton.setVisibility(View.GONE);
                    viewModel.loadScreenshots();
                }
            }
        });
        
        // Clear search button
        clearSearchButton.setOnClickListener(v -> {
            searchEditText.setText("");
            clearSearchButton.setVisibility(View.GONE);
        });
        
        // Scan button
        scanButton.setOnClickListener(v -> {
            if (checkPermissions()) {
                viewModel.scanScreenshots();
            }
        });
        
        // Organize button
        organizeButton.setOnClickListener(v -> {
            if (checkPermissions()) {
                viewModel.organizeScreenshots();
            }
        });
        
        // Share FAB
        shareFab.setOnClickListener(v -> {
            showShareOptionsDialog();
        });
    }
    
    private void updateStats(int total, int organized) {
        findViewById(R.id.totalImagesText).findViewById(android.R.id.text1);
        ((android.widget.TextView) findViewById(R.id.totalImagesText)).setText(String.valueOf(total));
        ((android.widget.TextView) findViewById(R.id.organizedImagesText)).setText(String.valueOf(organized));
    }
    
    private void updateAutoAlbums(List<AutoAlbum> albums) {
        // Auto albums are displayed in a horizontal scroll view
        // This would be implemented with dynamic view inflation
    }
    
    private void performSearch(String query) {
        NaturalLanguageSearch.SearchQuery searchQuery = nlSearch.parseQuery(query);
        
        if (searchQuery.isReceiptSearch || searchQuery.isPasswordSearch || 
            searchQuery.category != null || searchQuery.albumType != null) {
            viewModel.searchWithNaturalLanguage(query);
        } else {
            viewModel.searchScreenshots(query);
        }
    }
    
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                requestManageStoragePermission();
                return false;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                return false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                return false;
            }
        }
        return true;
    }
    
    private void requestManageStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                settingsLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                settingsLauncher.launch(intent);
            }
        }
    }
    
    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_required)
                .setMessage(R.string.permission_storage_message)
                .setPositiveButton(R.string.go_to_settings, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }
    
    private void showShareOptionsDialog() {
        String[] options = {"Create Organization Video", "Share Stats", "Export Report"};
        
        new AlertDialog.Builder(this)
                .setTitle("Share")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            viewModel.createOrganizationVideo();
                            break;
                        case 1:
                            shareStats();
                            break;
                        case 2:
                            exportReport();
                            break;
                    }
                })
                .show();
    }
    
    private void shareStats() {
        int total = viewModel.getScreenshots().getValue() != null ? 
                viewModel.getScreenshots().getValue().size() : 0;
        int organized = viewModel.getOrganizedCount();
        
        String shareText = "SnapSort Stats:\n" +
                "Total Screenshots: " + total + "\n" +
                "Organized: " + organized + "\n" +
                "Pending: " + (total - organized);
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
    
    private void exportReport() {
        viewModel.exportOrganizationReport();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.action_refresh) {
            viewModel.scanScreenshots();
            return true;
        } else if (itemId == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    // Category click listener
    @Override
    public void onCategoryClick(Category category) {
        Intent intent = new Intent(this, CategoryDetailActivity.class);
        intent.putExtra("category_id", category.getId());
        intent.putExtra("category_name", category.getDisplayName());
        startActivity(intent);
    }
    
    // Screenshot click listener
    @Override
    public void onScreenshotClick(Screenshot screenshot) {
        // Open image viewer or detail view
        viewModel.viewScreenshot(screenshot);
    }
    
    @Override
    public void onScreenshotLongClick(Screenshot screenshot) {
        // Show context menu
        showScreenshotContextMenu(screenshot);
    }
    
    @Override
    public void onSelectionChanged(int count) {
        // Update action mode or show selection toolbar
    }
    
    // Auto album click listener
    @Override
    public void onAlbumClick(AutoAlbum album) {
        Intent intent = new Intent(this, CategoryDetailActivity.class);
        intent.putExtra("album_id", album.getId());
        intent.putExtra("album_name", album.getDisplayName());
        startActivity(intent);
    }
    
    private void showScreenshotContextMenu(Screenshot screenshot) {
        String[] options = {"View", "Move to Category", "Add to Album", "Share", "Delete"};
        
        new AlertDialog.Builder(this)
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            viewModel.viewScreenshot(screenshot);
                            break;
                        case 1:
                            showMoveToCategoryDialog(screenshot);
                            break;
                        case 2:
                            showAddToAlbumDialog(screenshot);
                            break;
                        case 3:
                            shareScreenshot(screenshot);
                            break;
                        case 4:
                            confirmDelete(screenshot);
                            break;
                    }
                })
                .show();
    }
    
    private void showMoveToCategoryDialog(Screenshot screenshot) {
        // Implementation for moving to category
    }
    
    private void showAddToAlbumDialog(Screenshot screenshot) {
        // Implementation for adding to album
    }
    
    private void shareScreenshot(Screenshot screenshot) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            
            Uri imageUri = Uri.parse(screenshot.getPath());
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            
            startActivity(Intent.createChooser(shareIntent, "Share image"));
        } catch (Exception e) {
            Toast.makeText(this, "Error sharing image", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void confirmDelete(Screenshot screenshot) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_confirm)
                .setMessage(R.string.dialog_confirm_delete)
                .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                    viewModel.deleteScreenshot(screenshot);
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data if auto-scan is enabled
        if (viewModel.isAutoScanEnabled()) {
            viewModel.scanScreenshots();
        }
    }
}
