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
import com.snapsort.app.model.AutoAlbum;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying auto-albums in a RecyclerView
 */
public class AutoAlbumAdapter extends RecyclerView.Adapter<AutoAlbumAdapter.AutoAlbumViewHolder> {
    
    private final Context context;
    private List<AutoAlbum> albums;
    private final OnAlbumClickListener listener;
    
    public interface OnAlbumClickListener {
        void onAlbumClick(AutoAlbum album);
    }
    
    public AutoAlbumAdapter(Context context, OnAlbumClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.albums = new ArrayList<>();
    }
    
    public void setAlbums(List<AutoAlbum> albums) {
        this.albums = albums != null ? albums : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void updateAlbum(AutoAlbum album) {
        for (int i = 0; i < albums.size(); i++) {
            if (albums.get(i).getId().equals(album.getId())) {
                albums.set(i, album);
                notifyItemChanged(i);
                break;
            }
        }
    }
    
    @NonNull
    @Override
    public AutoAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_auto_album, parent, false);
        return new AutoAlbumViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AutoAlbumViewHolder holder, int position) {
        AutoAlbum album = albums.get(position);
        holder.bind(album);
    }
    
    @Override
    public int getItemCount() {
        return albums.size();
    }
    
    class AutoAlbumViewHolder extends RecyclerView.ViewHolder {
        private final ImageView autoAlbumIcon;
        private final TextView autoAlbumName;
        private final TextView autoAlbumCount;
        
        public AutoAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            autoAlbumIcon = itemView.findViewById(R.id.autoAlbumIcon);
            autoAlbumName = itemView.findViewById(R.id.autoAlbumName);
            autoAlbumCount = itemView.findViewById(R.id.autoAlbumCount);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAlbumClick(albums.get(position));
                }
            });
        }
        
        public void bind(AutoAlbum album) {
            autoAlbumName.setText(album.getDisplayName());
            autoAlbumCount.setText(album.getImageCount() + " items");
            
            // Set icon
            if (album.getIconResId() != 0) {
                autoAlbumIcon.setImageResource(album.getIconResId());
            }
            
            // Set tint color based on album type
            int color = getAlbumColor(album.getAlbumType());
            autoAlbumIcon.setColorFilter(color);
        }
        
        private int getAlbumColor(String albumType) {
            switch (albumType) {
                case "shopping_list":
                    return context.getResources().getColor(R.color.category_shopping);
                case "ticket":
                    return context.getResources().getColor(R.color.category_video);
                case "todo":
                    return context.getResources().getColor(R.color.category_productivity);
                default:
                    return context.getResources().getColor(R.color.primary);
            }
        }
    }
}
