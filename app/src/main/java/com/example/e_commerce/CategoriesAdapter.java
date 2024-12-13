package com.example.e_commerce;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    Context context;
    private final List<Category> categories;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);

        void onProductClick(Product product);
    }

    private OnCategoryLongClickListener longClickListener;

    // Interface for handling long click actions
    public interface OnCategoryLongClickListener {
        void onCategoryLongClick(Category category);
    }

    // Constructor
    public CategoriesAdapter(Context context, List<Category> categories,
                             OnCategoryClickListener listener,
                             OnCategoryLongClickListener longClickListener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        // Set category name
        holder.category_name.setText(category.getName());

        // Use Glide to load the image from the URL
        Glide.with(context)
                .load(category.getImageUrl()) // The URL of the image
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .into(holder.category_image); // Set the image to the ImageView

        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onCategoryLongClick(category);
            }
            return true; // Return true to indicate the long-click was handled
        });

        /*holder.itemView.setOnLongClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("categoryId", category.getId()); // Pass document ID
            EditCategoryFragment editCategoryFragment = new EditCategoryFragment();
            editCategoryFragment.setArguments(args);

            // Assuming you're using a FragmentManager to replace fragments
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, editCategoryFragment)
                    .addToBackStack(null)
                    .commit();

            return true;
        });*/
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView category_name;
        ImageView category_image;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            category_name = itemView.findViewById(R.id.category_name);
            category_image = itemView.findViewById(R.id.category_image); // ImageView to display the category image
        }
    }
}
