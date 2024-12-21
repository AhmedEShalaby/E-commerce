package com.example.e_commerce;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProductDetailsFragment extends Fragment {

    private ImageView productImage;
    private TextView productName, productPrice, productAvailability, productDescription, cartItemCount;
    private ImageButton addToCartBtn, removeFromCartBtn;

    private List<CartItem> cart = new ArrayList<>(); // Initialize the cart list
    private String productId;
    private Product currentProduct;
    private User currentUser;
    CartItem cartItem;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_details, container, false);

        // Initialize UI components
        initializeViews(rootView);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Handle arguments passed to the fragment
        handleArguments();

        // Set button click listeners
        setButtonListeners();

        return rootView;
    }

    private void initializeViews(View rootView) {
        productImage = rootView.findViewById(R.id.product_image);
        productName = rootView.findViewById(R.id.product_name);
        productPrice = rootView.findViewById(R.id.product_price);
        productAvailability = rootView.findViewById(R.id.product_availability);
        productDescription = rootView.findViewById(R.id.product_description);
        cartItemCount = rootView.findViewById(R.id.cart_item_count);
        addToCartBtn = rootView.findViewById(R.id.add_to_cart_btn);
        removeFromCartBtn = rootView.findViewById(R.id.remove_from_cart_btn);
    }

    private void handleArguments() {
        if (getArguments() != null) {
            currentProduct = (Product) getArguments().getSerializable("product");
            currentUser = (User) getArguments().getSerializable("currentUser");
        }

        if (currentProduct == null) {
            Toast.makeText(getContext(), "Product ID not found in arguments!", Toast.LENGTH_SHORT).show();
            return;
        }

        fetchCartItems();
    }

    private void fetchCartItems() {


        cart = currentUser.getCartItems();
        displayProductDetails();

    }

    private void displayProductDetails() {
        productName.setText(currentProduct.getName());
        productPrice.setText(String.format("$%.2f", currentProduct.getPrice()));
        productDescription.setText(currentProduct.getDescription());

        if (currentProduct.getStock() > 0) {
            productAvailability.setText("In Stock");
            productAvailability.setTextColor(Color.parseColor("#47663B"));
        } else {
            productAvailability.setText("Out of Stock");
            productAvailability.setTextColor(Color.parseColor("#A02334"));
        }

        Glide.with(requireContext())
                .load(currentProduct.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(productImage);

        updateCartCount();
    }

    private void setButtonListeners() {
        addToCartBtn.setOnClickListener(v -> {
            if (currentProduct != null && !currentUser.isAdmin()) {
                addToCart();
            }
        });

        removeFromCartBtn.setOnClickListener(v -> {
            if (currentProduct != null && !currentUser.isAdmin()) {
                removeFromCart();
            }
        });
    }

    private void addToCart() {

        CartItem cartItem = findCartItemInList();


        if (cartItem != null) {
            if (currentProduct.getStock() == 0 || cartItem.getQuantity() == currentProduct.getStock()) {
                Toast.makeText(getContext(), "Product is out of stock!", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                cartItem.setQuantity(cartItem.getQuantity() + 1);
            }
        } else {
            if (currentProduct.getStock() == 0) {
                Toast.makeText(getContext(), "Product is out of stock!", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                cartItem = new CartItem(currentProduct, 1);
                cart.add(cartItem);
            }
        }

        // Update the user document in Firestore
        currentUser.setCartItems(cart);

        db.collection("Users")
                .document(currentUser.getId())
                .set(currentUser)
                .addOnSuccessListener(aVoid -> {
                    updateCartCount();
                    Toast.makeText(getContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void removeFromCart() {

        cart = currentUser.getCartItems();

        CartItem cartItem = findCartItemInList();
        if (cartItem == null) {
            Toast.makeText(getContext(), "Product not in cart!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (cartItem.getQuantity() > 1) {
            // Reduce the quantity of the cart item
            cartItem.setQuantity(cartItem.getQuantity() - 1);
        } else {
            // Remove the cart item completely if quantity is 1
            cart.remove(cartItem);
        }

        // Update the user document in Firestore
        currentUser.setCartItems(cart);

        db.collection("Users")
                .document(currentUser.getId())
                .set(currentUser)
                .addOnSuccessListener(aVoid -> {
                    updateCartCount();
                    Toast.makeText(getContext(), "Removed from cart!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private CartItem findCartItemInList() {
        for (CartItem item : cart) {
            if (item.getProduct().getId().equals(currentProduct.getId())) {
                return item;
            }
        }
        return null;
    }

    private void updateCartCount() {
        CartItem cartItem = findCartItemInList();
        int quantity = (cartItem != null) ? cartItem.getQuantity() : 0;
        cartItemCount.setText(String.valueOf(quantity));
    }
}
