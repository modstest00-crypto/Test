package com.snapsort.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.snapsort.app.R;
import com.snapsort.app.model.Screenshot;
import com.snapsort.app.util.ScreenshotScanner;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adapter for displaying screenshots in a RecyclerView
 */
public class ScreenshotAdapter extends RecyclerView.Adapter<ScreenshotAdapter.ScreenshotViewHolder> {
    
    private final Context context;
    private List<Screenshot> screenshots;
    private final OnScreenshotClickListener listener;
    private boolean selectMode = false;
    private final Set<Long> selectedItems;
    private ScreenshotScanner scanner;
    
    public interface OnScreenshotClickListener {
        void onScreenshotClick(Screenshot screenshot);
        void onScreenshotLongClick(Screenshot screenshot);
        void onSelectionChanged(int count);
    }
    
    public ScreenshotAdapter(Context context, OnScreenshotClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.screenshots = new ArrayList<>();
        this.selectedItems = new HashSet<>();
    }
    
    public void setScreenshots(List<Screenshot> screenshots) {
        this.screenshots = screenshots != null ? screenshots : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void addScreenshot(Screenshot screenshot) {
        screenshots.add(0, screenshot);
        notifyItemInserted(0);
    }
    
    public void removeScreenshot(long id) {
        for (int i = 0; i < screenshots.size(); i++) {
            if (screenshots.get(i).getId() == id) {
                screenshots.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
    
    public void setSelectMode(boolean enabled) {
        this.selectMode = enabled;
        if (!enabled) {
            selectedItems.clear();
        }
        notifyDataSetChanged();
    }
    
    public void toggleSelection(long id) {
        if (selectedItems.contains(id)) {
            selectedItems.remove(id);
        } else {
            selectedItems.add(id);
        }
        notifyItemChanged(getItemPosition(id));
        
        if (listener != null) {
            listener.onSelectionChanged(selectedItems.size());
        }
    }
    
    public void selectAll() {
        selectedItems.clear();
        for (Screenshot screenshot : screenshots) {
            selectedItems.add(screenshot.getId());
        }
        notifyDataSetChanged();
        
        if (listener != null) {
            listener.onSelectionChanged(selectedItems.size());
        }
    }
    
    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
        
        if (listener != null) {
            listener.onSelectionChanged(0);
        }
    }
    
    public Set<Long> getSelectedItems() {
        return selectedItems;
    }
    
    public List<Screenshot> getSelectedScreenshots() {
        List<Screenshot> selected = new ArrayList<>();
        for (Screenshot screenshot : screenshots) {
            if (selectedItems.contains(screenshot.getId())) {
                selected.add(screenshot);
            }
        }
        return selected;
    }
    
    private int getItemPosition(long id) {
        for (int i = 0; i < screenshots.size(); i++) {
            if (screenshots.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }
    
    @NonNull
    @Override
    public ScreenshotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_screenshot, parent, false);
        return new ScreenshotViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ScreenshotViewHolder holder, int position) {
        Screenshot screenshot = screenshots.get(position);
        holder.bind(screenshot);
    }
    
    @Override
    public int getItemCount() {
        return screenshots.size();
    }
    
    class ScreenshotViewHolder extends RecyclerView.ViewHolder {
        private final ImageView screenshotImage;
        private final ImageView selectionOverlay;
        private final CheckBox selectionCheckbox;
        private final TextView categoryBadge;
        private final TextView imageDate;
        
        public ScreenshotViewHolder(@NonNull View itemView) {
            super(itemView);
            screenshotImage = itemView.findViewById(R.id.screenshotImage);
            selectionOverlay = itemView.findViewById(R.id.selectionOverlay);
            selectionCheckbox = itemView.findViewById(R.id.selectionCheckbox);
            categoryBadge = itemView.findViewById(R.id.categoryBadge);
            imageDate = itemView.findViewById(R.id.imageDate);
            
            if (scanner == null) {
                scanner = new ScreenshotScanner(context);
            }
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Screenshot screenshot = screenshots.get(position);
                    if (selectMode) {
                        toggleSelection(screenshot.getId());
                    } else if (listener != null) {
                        listener.onScreenshotClick(screenshot);
                    }
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onScreenshotLongClick(screenshots.get(position));
                    return true;
                }
                return false;
            });
        }
        
        public void bind(Screenshot screenshot) {
            // Load image
            Bitmap bitmap = loadBitmap(screenshot.getPath());
            if (bitmap != null) {
                screenshotImage.setImageBitmap(bitmap);
            } else {
                screenshotImage.setImageResource(R.drawable.ic_category_other);
            }
            
            // Show/hide selection UI
            if (selectMode) {
                selectionCheckbox.setVisibility(View.VISIBLE);
                selectionCheckbox.setChecked(selectedItems.contains(screenshot.getId()));
                selectionOverlay.setVisibility(selectedItems.contains(screenshot.getId()) ? View.VISIBLE : View.GONE);
            } else {
                selectionCheckbox.setVisibility(View.GONE);
                selectionOverlay.setVisibility(View.GONE);
            }
            
            // Set category badge
            if (screenshot.getCategory() != null && !screenshot.getCategory().isEmpty()) {
                categoryBadge.setText(getCategoryDisplayName(screenshot.getCategory()));
                categoryBadge.setVisibility(View.VISIBLE);
            } else {
                categoryBadge.setVisibility(View.GONE);
            }
            
            // Set date
            if (screenshot.getDateAdded() != null) {
                imageDate.setText(scanner.getRelativeTime(screenshot.getDateAdded()));
            }
        }
        
        private Bitmap loadBitmap(String path) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    return BitmapFactory.decodeFile(path, options);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        
        private String getCategoryDisplayName(String categoryId) {
            switch (categoryId) {
                case "social":
                    return "Social";
                case "chat":
                    return "Chat";
                case "gaming":
                    return "Gaming";
                case "shopping":
                    return "Shopping";
                case "news":
                    return "News";
                case "music":
                    return "Music";
                case "video":
                    return "Video";
                case "maps":
                    return "Maps";
                case "finance":
                    return "Finance";
                case "productivity":
                    return "Productivity";
                case "settings":
                    return "Settings";
                default:
                    return "Other";
            }
        }
    }
}
