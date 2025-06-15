package com.example.projectakhir;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private Context context;
    private List<RestaurantModel> restaurantList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RestaurantModel restaurant);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RestaurantAdapter(Context context, List<RestaurantModel> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RestaurantModel restaurant = restaurantList.get(position);

        holder.nameTextView.setText(restaurant.getName());
        holder.descriptionTextView.setText(restaurant.getDescription());
        holder.locationTextView.setText(restaurant.getLocation());
        holder.priceTextView.setText(restaurant.getPrice());

        // Load image
        if (restaurant.getImageUrl() != null && !restaurant.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(restaurant.getImageUrl())
                    .placeholder(R.drawable.restoran)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.restoran);
        }

        // Klik seluruh item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(restaurant);
            }
        });

        // Klik tombol edit
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditRestaurantActivity.class);
            intent.putExtra("restaurantId", restaurant.getId());
            intent.putExtra("restaurantName", restaurant.getName());
            intent.putExtra("restaurantDesc", restaurant.getDescription());
            intent.putExtra("restaurantLocation", restaurant.getLocation());
            intent.putExtra("restaurantPrice", restaurant.getPrice());
            intent.putExtra("restaurantHours", restaurant.getOpeningHours());
            intent.putExtra("restaurantImageUrl", restaurant.getImageUrl());
            context.startActivity(intent);
        });

        // Placeholder aksi ikon
        holder.likeIcon.setOnClickListener(v -> {
            // Tambahkan aksi like
        });

        holder.dislikeIcon.setOnClickListener(v -> {
            // Tambahkan aksi dislike
        });

        holder.shareIcon.setOnClickListener(v -> {
            // Tambahkan aksi share
        });

        holder.bookmarkIcon.setOnClickListener(v -> {
            // Tambahkan aksi bookmark
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView descriptionTextView;
        TextView locationTextView;
        TextView priceTextView;
        ImageView likeIcon, dislikeIcon, shareIcon, bookmarkIcon;
        ImageButton editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewRestaurant);
            nameTextView = itemView.findViewById(R.id.textRestaurantName);
            descriptionTextView = itemView.findViewById(R.id.foodDesc);
            locationTextView = itemView.findViewById(R.id.textLocation);
            priceTextView = itemView.findViewById(R.id.textPrice);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            dislikeIcon = itemView.findViewById(R.id.dislikeIcon);
            shareIcon = itemView.findViewById(R.id.shareIcon);
            bookmarkIcon = itemView.findViewById(R.id.bookmarkIcon);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}
