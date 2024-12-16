package com.example.e_commerce;

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

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>{

    Context context;
    private List<Product> products;

    public void setSearchList(List<Product> productSearchList)
    {
        this.products = productSearchList;
        notifyDataSetChanged();
    }

    private OnProductClickListener ClickListener;
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private OnProductLongClickListener longClickListener;

    // Interface for handling long click actions
    public interface OnProductLongClickListener {
        void onProductLongClick(Product product);
    }

    // Constructor
    public ProductsAdapter(Context context, List<Product> products, OnProductClickListener ClickListener,
                           OnProductLongClickListener longClickListener) {
        this.context = context;
        this.products = products;
        this.ClickListener = ClickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ProductsAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductsAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        Product product = products.get(position);

        holder.product_name.setText(product.getName());
        holder.product_price.setText(String.valueOf(product.getPrice()));
        holder.product_availability.setText(String.valueOf(product.getStock()));

        // Use Glide to load the image from the URL
        Glide.with(context)
                .load(product.getImageUrl()) // The URL of the image
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .into(holder.product_image); // Set the image to the ImageVie

        Glide.with(context)
                .load(product.getBarcodeUrl()) // The URL of the image
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .into(holder.product_barcode_image); // Set the image to the ImageVie// w

        holder.itemView.setOnClickListener(v -> ClickListener.onProductClick(product));

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onProductLongClick(product);
            }
            return true; // Return true to indicate the long-click was handled
        });



    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView product_name, product_price, product_availability;
        ImageView product_image, product_barcode_image;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_availability = itemView.findViewById(R.id.product_availability);
            product_image = itemView.findViewById(R.id.product_image);
            product_barcode_image = itemView.findViewById(R.id.product_barcode_image);

        }
    }

}
