package com.example.e_commerce;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.appcompat.widget.SearchView;
import android.app.SearchManager;
import android.widget.SearchView.OnQueryTextListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements CategoriesAdapter.OnCategoryClickListener,  ProductsAdapter.OnProductClickListener{

    private FirebaseFirestore db;
    private User currentUser;
    private RecyclerView recyclerViewCategories, recyclerViewProducts;
    private CategoriesAdapter categoriesAdapter;
    private List<Category> categories;
    private ProductsAdapter productsAdapter;
    private List<Product> allProducts, filteredProducts;
    private List<CartItem> cartItems;
    SearchView searchView;
    //ProductDetailsFragment productDetailsFragment;
    //private CartViewModel cartViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("currentUser");
        }

        searchView = rootView.findViewById(R.id.search);
        searchView.setIconifiedByDefault(false);
        TextView searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchText != null) {
            searchText.setHintTextColor(Color.parseColor("#EED3B1")); // Change to your desired color
            searchText.setTextColor(Color.parseColor("#EED3B1")); // Change to your desired text color
        }
        /*ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchIcon.setImageResource(R.drawable.search); // Replace 'new_search_icon' with your icon resource*/
        searchView.setQueryHint("Search here...");

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView for Categories
        recyclerViewCategories = rootView.findViewById(R.id.category_rc);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        // Initialize categories list and adapter
        categories = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(getContext(), categories, this, this::triggerEditCategory);
        recyclerViewCategories.setAdapter(categoriesAdapter);

        // Fetch categories data from Firestore
        fetchCategoriesFromFirestore();

        // Initialize RecyclerView for Products
        recyclerViewProducts = rootView.findViewById(R.id.products_rc);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize products list and adapter
        allProducts = new ArrayList<>();
        productsAdapter = new ProductsAdapter(getContext(), allProducts, this, this::triggerEditProduct);

        recyclerViewProducts.setAdapter(productsAdapter);

        fetchProductsFromFirestore();

        searchView.clearFocus();
        filteredProducts  = new ArrayList<>();

        /*boolean search = true;
        if (getArguments() != null) {
            search = getArguments().getBoolean("search",true);
        }
        else {
            search = true;
        }*/

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //filterProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                //Toast.makeText(getContext(), "Whhhyyyyyyy", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //productDetailsFragment = new ProductDetailsFragment();


        cartItems = new ArrayList<>();

        return rootView;
    }

    private void addToCart(Product product) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId().equals(product.getId())) {
                cartItem.setQuantity(cartItem.getQuantity() + 1); // Increment quantity
                Toast.makeText(getContext(), "Increased quantity of " + product.getName() + " to " + cartItem.getQuantity(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Add product to cart with initial quantity 1
        cartItems.add(new CartItem(product, 1));
        Toast.makeText(getContext(), product.getName() + " added to cart with quantity 1", Toast.LENGTH_SHORT).show();
    }



    /*private void removeFromCart(Product product) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId().equals(product.getId())) {
                int newQuantity = cartItem.getQuantity() - 1;
                if (newQuantity <= 0) {
                    cartItems.remove(cartItem); // Remove product from cart
                    Toast.makeText(getContext(), product.getName() + " removed from cart!", Toast.LENGTH_SHORT).show();
                } else {
                    cartItem.setQuantity(newQuantity); // Update quantity
                    Toast.makeText(getContext(), "Decreased quantity of " + product.getName() + " to " + newQuantity, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

        // If the product is not in the cart, show a message
        Toast.makeText(getContext(), product.getName() + " is not in the cart!", Toast.LENGTH_SHORT).show();
    }

    private int getProductQuantityFromCart(Product product) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId().equals(product.getId())) {
                return cartItem.getQuantity(); // Return existing quantity
            }
        }
        return 0; // Product not in cart
    }*/


    private void showCart() {
        // Logic to navigate to a cart screen or show cart details
        Toast.makeText(getContext(), "Cart contains " + cartItems.size() + " items.", Toast.LENGTH_SHORT).show();
    }

    public void filterProducts(String query) {
        filteredProducts.clear();

        //Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
        if (query == null || query.trim().isEmpty()) {
            // If the query is empty, restore the full list of products
            productsAdapter.setProducts(allProducts);
            productsAdapter.notifyDataSetChanged();
            return;
        }

        // Filter products based on the query
        for (Product product : allProducts) {
            boolean matchesName = product.getName().toLowerCase().contains(query.toLowerCase());

            if (matchesName) {
                filteredProducts.add(product);
            }
        }

        if (filteredProducts.isEmpty()) {
            Toast.makeText(getContext(), "No products found", Toast.LENGTH_SHORT).show();
        }

        getActivity().runOnUiThread(() -> {
            productsAdapter.setProducts(filteredProducts);
            productsAdapter.notifyDataSetChanged();
        });


        // Update the adapter with filtered products
       /* productsAdapter.setProducts(filteredProducts);
        productsAdapter.notifyDataSetChanged();*/
    }


    private void triggerEditProduct(Product product) {

        EditProductFragment editProductFragment = new EditProductFragment();
        Bundle args = new Bundle();
        args.putSerializable("currentUser", currentUser);
        args.putString("productId", product.getId()); // Pass the document ID
        editProductFragment.setArguments(args);

        loadFragment(editProductFragment);

    }

    private void triggerEditCategory(Category category) {

        EditCategoryFragment editCategoryFragment = new EditCategoryFragment();
        Bundle args = new Bundle();
        args.putString("categoryId", category.getId()); // Pass the document ID
        editCategoryFragment.setArguments(args);

        loadFragment(editCategoryFragment);
    }

    private void fetchCategoriesFromFirestore() {
        db.collection("Categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear previous data before adding new data
                        categories.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("CategoryData", "Fetched: " + document.getData());

                            // Retrieve the document ID
                            String documentId = document.getId();

                            // Retrieve the category data
                            Category category = document.toObject(Category.class);
                            category.setId(documentId); // Assuming your Category model has a `setDocumentId` method

                            categories.add(category);
                        }
                        // Notify the adapter to update the RecyclerView
                        categoriesAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("CategoriesFragment", "Error getting documents.", task.getException());
                    }
                });
    }

    private void fetchProductsForCategory(String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) {
            Toast.makeText(getContext(), "Invalid category ID", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Products")
                .whereEqualTo("categoryId", categoryId) // Ensure the field name matches your Firestore field
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear previous products
                        allProducts.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            allProducts.add(product);
                        }
                        productsAdapter.setProducts(allProducts);
                        // Notify the adapter to refresh the list
                        productsAdapter.notifyDataSetChanged();

                    } else {
                        Log.w("fetchProducts", "Error getting products.", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("fetchProducts", "Failed to fetch products: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchProductsFromFirestore() {

        db.collection("Products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear previous products
                        allProducts.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            allProducts.add(product);
                        }

                        // Update the adapter with the full product list
                        productsAdapter.setProducts(allProducts);
                        productsAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("fetchProducts", "Error getting products.", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("fetchProducts", "Failed to fetch products: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    @Override
    public void onCategoryClick(Category category) {
        // Fetch products for the clicked category
        fetchProductsForCategory(category.getId());
    }

    @Override
    public void onProductClick(Product product) {

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("currentUser");
        }
        else {
            Toast.makeText(getContext(), "Not in argument", Toast.LENGTH_SHORT).show();
        }

        if (currentUser == null) {
            Toast.makeText(getContext(), "User data not available!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        Bundle args = new Bundle();

        args.putSerializable("currentUser", currentUser);
        args.putSerializable("product", product);
        productDetailsFragment.setArguments(args);

        loadFragment(productDetailsFragment);

    }

    private void loadFragment(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}

