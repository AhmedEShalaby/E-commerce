package com.example.e_commerce;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private User currentUser;
    private List<CartItem> cartItems;
    private TextView subtotal_price, total_price, delivery_fee, item_quantity_text;
    private FirebaseFirestore db;
    private RecyclerView cartRecyclerView;
    private CartItemsAdapter cartItemsAdapter;
    private Button place_order_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = findViewById(R.id.cart_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cart), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        if(currentUser != null && currentUser.getCartItems() != null){
            cartItems = currentUser.getCartItems();
        }
        else {
            cartItems = new ArrayList<>();
        }

        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView for Categories
        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));

        cartItemsAdapter = new CartItemsAdapter(CartActivity.this, cartItems, this::increaseQuantity, this::removeFromCart);
        cartRecyclerView.setAdapter(cartItemsAdapter);

        //item_quantity_text = findViewById(R.id.item_quantity_text);

        subtotal_price = findViewById(R.id.subtotal_price);
        total_price = findViewById(R.id.total_price);
        delivery_fee = findViewById(R.id.delivery_fee);

        countPrices();

        place_order_button = findViewById(R.id.place_order_button);
        place_order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cartItems.isEmpty()){
                    Toast.makeText(CartActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CartActivity.this, "Add Products", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void increaseQuantity(int position) {

        //CartItem cartItem = cartItems.get(position);

        if (cartItems.get(position).getProduct().getStock() == cartItems.get(position).getQuantity()) {
            Toast.makeText(this, "Cannot exceed stock limit!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the quantity locally
        cartItems.get(position).setQuantity(cartItems.get(position).getQuantity() + 1);
        cartRecyclerView.getAdapter().notifyItemChanged(position);
        countPrices();

        currentUser.setCartItems(cartItems);

        // Update the user document in Firestore
        db.collection("Users")
                .document(currentUser.getId())
                .set(currentUser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cart updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void removeFromCart(int position) {

        //cartItems.get(position) = currentUser.getCartItems();

        //CartItem cartItem = findCartItemInList();
        if (cartItems.get(position).getQuantity() > 1) {
            // Reduce the quantity of the cart item
            cartItems.get(position).setQuantity(cartItems.get(position).getQuantity() - 1);
            cartItemsAdapter.notifyItemChanged(position);
        } else {
            // Remove the cart item completely if quantity is 1
            cartItems.remove(cartItems.get(position));
            cartItemsAdapter.notifyItemRemoved(position);
        }

        countPrices();

        // Update the user document in Firestore
        currentUser.setCartItems(cartItems);

        db.collection("Users")
                .document(currentUser.getId())
                .set(currentUser)
                .addOnSuccessListener(aVoid -> {
                    //updateCartCount();
                    Toast.makeText(CartActivity.this, "Removed from cart!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CartActivity.this, "Failed to update cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void countPrices() {
        double subTotalPrice = 0, totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            subTotalPrice += cartItem.getTotalPrice();
        }

        double deliveryFee = 0;
        if(subTotalPrice != 0)
            deliveryFee = Double.parseDouble(delivery_fee.getText().toString().substring(2));

        totalPrice = subTotalPrice + deliveryFee;

        subtotal_price.setText("$ " + String.valueOf(subTotalPrice));
        delivery_fee.setText("$ " + String.valueOf(deliveryFee));
        total_price.setText("$ " + String.valueOf(totalPrice));
    }


    /*private CartItem findCartItemInList() {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(currentProduct.getId())) {
                return item;
            }
        }
        return null;
    }*/

   /* private void updateCartCount() {
        CartItem cartItem = findCartItemInList();
        int quantity = (cartItem != null) ? cartItem.getQuantity() : 0;
        cartItemCount.setText(String.valueOf(quantity));
    }*/


   /* private void fetchCartItemsFromFirestore() {



        *//*String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Fetch the user document based on the email
        db.collection("Users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        //cartItems.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User user = document.toObject(User.class);
                            cartItems = user.getCartItems();

                            //cartItems.add(cartItem);
                        }
                        // Notify the adapter to update the RecyclerView
                        cartItemsAdapter.setCartItems(cartItems);
                        cartItemsAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(CartActivity.this, "User with this email not found in Firestore!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CartActivity.this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });*//*
    }*/

}
