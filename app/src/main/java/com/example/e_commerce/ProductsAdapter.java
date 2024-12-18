package com.example.e_commerce;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>{

    Context context;
    private List<Product> products;

    int quantity;

    public void setSearchList(List<Product> productSearchList)
    {
        this.products = productSearchList;
        notifyDataSetChanged();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    /*public void setQuantity(int quantity) {
        this.quantity = quantity;
        notifyDataSetChanged();
    }*/


    private final OnProductClickListener ClickListener;
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final OnProductLongClickListener longClickListener;

    // Interface for handling long click actions
    public interface OnProductLongClickListener {
        void onProductLongClick(Product product);
    }

    /*private final OnCartActionListener cartActionListener;
    public interface OnCartActionListener {
        void onAddToCart(Product product);
        void onRemoveFromCart(Product product);
    }*/

    // Constructor
    //private final CartViewModel cartViewModel;

    public ProductsAdapter(Context context, List<Product> products, OnProductClickListener clickListener,
                           OnProductLongClickListener longClickListener) {
        this.context = context;
        this.products = products;
        this.ClickListener = clickListener;
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
        //holder.cartItemCount.setText(String.valueOf(quantity));

        if(product.getStock() > 0){
            holder.product_availability.setText("In stock");
        }
        else{
            holder.product_availability.setText("Out of stock");
        }

        // Use Glide to load the image from the URL
        Glide.with(context)
                .load(product.getImageUrl()) // The URL of the image
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .into(holder.product_image); // Set the image to the ImageVie

       /* Glide.with(context)
                .load(product.getBarcodeUrl()) // The URL of the image
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .into(holder.product_barcode_image); // Set the image to the ImageVie// w*/

        holder.product_barcode_image.setImageBitmap(generateBarcode(product.getName()));

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

        TextView product_name, product_price, product_availability, cartItemCount;
        ImageView product_image, product_barcode_image;
        ImageButton addToCartBtn, removeFromCartBtn;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_availability = itemView.findViewById(R.id.product_availability);
            product_image = itemView.findViewById(R.id.product_image);
            product_barcode_image = itemView.findViewById(R.id.product_barcode_image);

            /*cartItemCount = itemView.findViewById(R.id.cart_item_count);
            addToCartBtn = itemView.findViewById(R.id.add_to_cart_btn);
            removeFromCartBtn = itemView.findViewById(R.id.remove_from_cart_btn);*/
        }
    }

    public Bitmap generateBarcode(String productName) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = new com.google.zxing.MultiFormatWriter()
                    .encode(productName, BarcodeFormat.QR_CODE, 100, 100);

            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

}
