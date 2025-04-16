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
        holder.imageViewRestaurant.setImageResource(food.getImageResource());
        holder.textRestaurantName.setText(food.getTitle());
        holder.foodDesc.setText(food.getDescription());

        // Navigasi ke RestoranActivity saat item diklik
        View.OnClickListener listener = v -> {
            Intent intent = new Intent(context, RestoranActivity.class);
            intent.putExtra("foodName", food.getTitle());
            context.startActivity(intent);
        };

//        holder.itemView.setOnClickListener(listener);  // Klik seluruh item
        holder.imageViewRestaurant.setOnClickListener(listener); // Klik gambar saja
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
