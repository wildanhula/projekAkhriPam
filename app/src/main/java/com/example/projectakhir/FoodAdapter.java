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
                .load(food.getImageUri())
                .placeholder(R.drawable.restoran)
                .into(holder.imageViewRestaurant);

        holder.textRestaurantName.setText(food.getTitle());
        holder.foodDesc.setText(food.getDescription());

        // Klik gambar → buka ReviewActivity
        holder.imageViewRestaurant.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReviewActivity.class);
            intent.putExtra("title", food.getTitle());
            intent.putExtra("description", food.getDescription());
            intent.putExtra("imageUri", food.getImageUri());
            // 🔽 Tambahkan ini:
            intent.putExtra("location", food.getLocation());
            intent.putExtra("price", food.getPrice());
            intent.putExtra("openingHours", food.getOpeningHours());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });


        // Klik tombol edit → buka EditFoodActivity
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditFoodActivity.class);
            intent.putExtra("food_id", food.getId()); // Pastikan FoodModel punya ID unik
            intent.putExtra("title", food.getTitle());
            intent.putExtra("description", food.getDescription());
            intent.putExtra("imageUri", food.getImageUri());
            intent.putExtra("location", food.getLocation());
            intent.putExtra("price", food.getPrice());
            intent.putExtra("openingHours", food.getOpeningHours());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        ImageButton editButton;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewRestaurant = itemView.findViewById(R.id.imageViewRestaurant);
            textRestaurantName = itemView.findViewById(R.id.textRestaurantName);
            foodDesc = itemView.findViewById(R.id.foodDesc);
            editButton = itemView.findViewById(R.id.editButton); // tombol edit kecil di samping deskripsi
        }
    }
}
