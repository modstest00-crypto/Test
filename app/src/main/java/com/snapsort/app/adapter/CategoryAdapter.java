package com.snapsort.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.snapsort.app.R;
import com.snapsort.app.model.Category;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying categories in a RecyclerView
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    
    private final Context context;
    private List<Category> categories;
    private final OnCategoryClickListener listener;
    
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
    
    public CategoryAdapter(Context context, OnCategoryClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.categories = new ArrayList<>();
    }
    
    public void setCategories(List<Category> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void updateCategory(Category category) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(category.getId())) {
                categories.set(i, category);
                notifyItemChanged(i);
                break;
            }
        }
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }
    
    @Override
    public int getItemCount() {
        return categories.size();
    }
    
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView categoryIcon;
        private final TextView categoryName;
        private final TextView categoryCount;
        
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryCount = itemView.findViewById(R.id.categoryCount);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCategoryClick(categories.get(position));
                }
            });
        }
        
        public void bind(Category category) {
            categoryName.setText(category.getDisplayName());
            categoryCount.setText(String.valueOf(category.getImageCount()));
            
            // Set icon
            if (category.getIconResId() != 0) {
                categoryIcon.setImageResource(category.getIconResId());
            }
            
            // Set card background color based on category
            int color = getCategoryColor(category.getId());
            itemView.setCardBackgroundColor(color);
        }
        
        private int getCategoryColor(String categoryId) {
            // Return a light version of the category color for card background
            switch (categoryId) {
                case "social":
                    return context.getResources().getColor(R.color.category_social);
                case "chat":
                    return context.getResources().getColor(R.color.category_chat);
                case "gaming":
                    return context.getResources().getColor(R.color.category_gaming);
                case "shopping":
                    return context.getResources().getColor(R.color.category_shopping);
                case "news":
                    return context.getResources().getColor(R.color.category_news);
                case "music":
                    return context.getResources().getColor(R.color.category_music);
                case "video":
                    return context.getResources().getColor(R.color.category_video);
                case "maps":
                    return context.getResources().getColor(R.color.category_maps);
                case "finance":
                    return context.getResources().getColor(R.color.category_finance);
                case "productivity":
                    return context.getResources().getColor(R.color.category_productivity);
                case "settings":
                    return context.getResources().getColor(R.color.category_settings);
                default:
                    return context.getResources().getColor(R.color.category_other);
            }
        }
    }
}
