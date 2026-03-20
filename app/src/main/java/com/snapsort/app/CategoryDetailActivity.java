package com.snapsort.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.snapsort.app.adapter.ScreenshotAdapter;
import com.snapsort.app.model.Screenshot;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying screenshots in a specific category or auto-album
 */
public class CategoryDetailActivity extends AppCompatActivity implements
        ScreenshotAdapter.OnScreenshotClickListener {
    
    private MaterialToolbar toolbar;
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private ScreenshotAdapter adapter;
    private FloatingActionButton shareFab;
    
    private String categoryId;
    private String albumId;
    private String title;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);
        
        // Get intent data
        categoryId = getIntent().getStringExtra("category_id");
        albumId = getIntent().getStringExtra("album_id");
        title = getIntent().getStringExtra("category_name");
        
        if (title == null) {
            title = getIntent().getStringExtra("album_name");
        }
        
        setupToolbar();
        setupRecyclerView();
        setupFab();
        loadScreenshots();
    }
    
    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title != null ? title : "Category");
        setSupportActionBar(toolbar);
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.categoryImagesRecyclerView);
        adapter = new ScreenshotAdapter(this, this);
        
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    
    private void setupFab() {
        shareFab = findViewById(R.id.shareFab);
        shareFab.setOnClickListener(v -> {
            // Share category/album
            shareCategory();
        });
    }
    
    private void loadScreenshots() {
        // In a real implementation, this would use a ViewModel
        List<Screenshot> screenshots = new ArrayList<>();
        
        if (categoryId != null) {
            // Load screenshots by category
            // This would query the database
        } else if (albumId != null) {
            // Load screenshots by auto-album
            // This would query the database
        }
        
        adapter.setScreenshots(screenshots);
    }
    
    private void shareCategory() {
        String shareText = "Check out my " + title + " collection in SnapSort!";
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    // Screenshot click listener
    @Override
    public void onScreenshotClick(Screenshot screenshot) {
        // Open image viewer
    }
    
    @Override
    public void onScreenshotLongClick(Screenshot screenshot) {
        // Show context menu
    }
    
    @Override
    public void onSelectionChanged(int count) {
        // Update selection UI
    }
}
