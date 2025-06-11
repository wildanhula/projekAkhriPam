package com.example.projectakhir;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private Context context;
    private List<FoodModel> foodList;

    public FoodAdapter(Context context, List<FoodModel> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodModel food = foodList.get(position);

        Glide.with(context)
                .load(food.getImageUri()) // Gunakan imageUri dari Firebase
                .placeholder(R.drawable.restoran)
                .into(holder.imageViewRestaurant);

        holder.textRestaurantName.setText(food.getTitle());
        holder.foodDesc.setText(food.getDescription());

        // Hanya gambar yang bisa diklik untuk menuju ke RestaurantActivity
        holder.imageViewRestaurant.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReviewActivity.class);
            intent.putExtra("title", food.getTitle());
            intent.putExtra("description", food.getDescription());
            intent.putExtra("imageUri", food.getImageUri());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // agar tidak crash jika context bukan activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewRestaurant;
        TextView textRestaurantName, foodDesc;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewRestaurant = itemView.findViewById(R.id.imageViewRestaurant);
            textRestaurantName = itemView.findViewById(R.id.textRestaurantName);
            foodDesc = itemView.findViewById(R.id.foodDesc);
        }
    }
}
