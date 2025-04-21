package com.example.projectakhir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private Context context;
    private List<Restaurant> restaurantList;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
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
        Restaurant restaurant = restaurantList.get(position);

        holder.imageView.setImageResource(restaurant.getImageResId());
        holder.nameTextView.setText(restaurant.getName());
        holder.descriptionTextView.setText(restaurant.getDescription());
        // Jika ada ratingBar, tambahkan di XML dan di sini juga

        // Tombol bisa ditambahkan fungsionalitas di sini kalau diperlukan
        holder.likeIcon.setOnClickListener(v -> {
            // contoh: Toast.makeText(context, "Liked " + restaurant.getName(), Toast.LENGTH_SHORT).show();
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
        ImageView likeIcon, dislikeIcon, shareIcon;
        ImageButton bookmarkButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewRestaurant);
            nameTextView = itemView.findViewById(R.id.textRestaurantName);
            descriptionTextView = itemView.findViewById(R.id.foodDesc);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            dislikeIcon = itemView.findViewById(R.id.dislikeIcon);
            shareIcon = itemView.findViewById(R.id.shareIcon);
            bookmarkButton = itemView.findViewById(R.id.btnBookmark);
        }
    }
}
