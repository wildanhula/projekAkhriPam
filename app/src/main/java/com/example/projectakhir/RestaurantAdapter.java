package com.example.projectakhir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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

        holder.nameTextView.setText(restaurant.getName());
        holder.descriptionTextView.setText(restaurant.getDescription());
        holder.locationTextView.setText(restaurant.getLocation()); // Disembunyikan
        holder.priceTextView.setText(restaurant.getPrice());       // Disembunyikan

        // Load image dari URL menggunakan Glide
        if (restaurant.getImageUrl() != null && !restaurant.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(restaurant.getImageUrl())
                    .placeholder(R.drawable.restoran) // gambar default sementara loading
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.restoran); // default jika tidak ada gambar
        }

        // Contoh: Tambahkan onClick untuk like button
        holder.likeIcon.setOnClickListener(v -> {
            // Implementasikan fungsi like
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
        }
    }
}
