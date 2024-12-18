package com.example.e_commerce;

import android.content.Context;
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

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.CartItemsViewHolder>{

    Context context;
    private List<CartItem> cartItems;

    private final CartItemsAdapter.onAddClickLitener addClickLitener;
    public interface onAddClickLitener {
        void onAddClickLitener(int position);
    }

    private final CartItemsAdapter.onRemoveClickListener removeClickListener;
    public interface onRemoveClickListener {
        void onRemoveClickListener(int position);
    }

    public CartItemsAdapter(Context context, List<CartItem> cartItems, onAddClickLitener addClickLitener,
                            onRemoveClickListener removeClickListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.addClickLitener = addClickLitener;
        this.removeClickListener = removeClickListener;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartItemsAdapter.CartItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartItemsAdapter.CartItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemsViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        holder.item_name.setText(cartItem.getProduct().getName());
        holder.item_price.setText(String.valueOf(cartItem.getProduct().getPrice()));
        holder.item_quantity_text.setText(String.valueOf(cartItem.getQuantity()));
        holder.total_item_price.setText(String.valueOf(cartItem.getTotalPrice()));

        // Use Glide to load the image from the URL
        Glide.with(context)
                .load(cartItem.getProduct().getImageUrl()) // The URL of the image
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .into(holder.item_image); // Set the image to the ImageVie

        holder.increase_quantity.setOnClickListener(v -> addClickLitener.onAddClickLitener(position));

        holder.decrease_quantity.setOnClickListener(v -> removeClickListener.onRemoveClickListener(position));


    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartItemsViewHolder extends RecyclerView.ViewHolder {

        TextView item_name, item_price, item_quantity_text, total_item_price;
        ImageView item_image;
        ImageButton decrease_quantity, increase_quantity;
        public CartItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_price = itemView.findViewById(R.id.item_price);
            item_quantity_text = itemView.findViewById(R.id.item_quantity_text);
            total_item_price = itemView.findViewById(R.id.total_item_price);
            item_image = itemView.findViewById(R.id.item_image);
            decrease_quantity = itemView.findViewById(R.id.decrease_quantity);
            increase_quantity = itemView.findViewById(R.id.increase_quantity);

        }
    }
}
