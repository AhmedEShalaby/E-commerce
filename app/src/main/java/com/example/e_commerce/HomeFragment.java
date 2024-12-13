package com.example.e_commerce;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.graphics.Color;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import java.util.List;

public class HomeFragment extends Fragment implements CategoriesAdapter.OnCategoryClickListener,  ProductsAdapter.OnProductClickListener{

    private FirebaseFirestore db;
    private RecyclerView recyclerViewCategories, recyclerViewProducts;
    private CategoriesAdapter categoriesAdapter;
    private List<Category> categories;
    private ProductsAdapter productsAdapter;
    private List<Product> products;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        searchView = rootView.findViewById(R.id.search);

// Set hint text color programmatically (as shown earlier)
        TextView searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchText != null) {
            searchText.setHintTextColor(Color.GRAY); // Change to your desired color
            searchText.setTextColor(Color.BLACK); // Change to your desired text color
        }

// Set other customizations programmatically if needed
        searchView.setQueryHint("Search here...");


        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);

                return true;
            }
        });



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
        products = new ArrayList<>();
        productsAdapter = new ProductsAdapter(getContext(), products, this, this::triggerEditProduct);
        recyclerViewProducts.setAdapter(productsAdapter);


        return rootView;
    }

    private void searchList(String newText) {
        List<Product> productSearchList = new ArrayList<>();

        // Loop through the existing products and filter by name
        for (Product product: products) {
            if (product.getName().toLowerCase().contains(newText.toLowerCase())) {
                productSearchList.add(product);
            }
        }

        // If no products are found, show a message
        if (productSearchList.isEmpty()) {
            Toast.makeText(getContext(), "Product not found", Toast.LENGTH_SHORT).show();
        } else {
            // Set the filtered list to the adapter
            productsAdapter.setSearchList(productSearchList);
        }
    }


    private void triggerEditProduct(Product product) {

        EditProductFragment editProductFragment = new EditProductFragment();
        Bundle args = new Bundle();
        args.putString("productId", product.getId()); // Pass the document ID
        editProductFragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, editProductFragment)
                .addToBackStack(null)
                .commit();

        //Toast.makeText(getContext(), "YYYYYEEEEEESSSSSSSSSS", Toast.LENGTH_SHORT).show();
    }


    private void triggerEditCategory(Category category) {


        EditCategoryFragment editCategoryFragment = new EditCategoryFragment();
        Bundle args = new Bundle();
        args.putString("categoryId", category.getId()); // Pass the document ID
        editCategoryFragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, editCategoryFragment)
                .addToBackStack(null)
                .commit();


        // Pass category data to EditCategoryFragment
        /*EditCategoryFragment fragment = new EditCategoryFragment();
        Bundle args = new Bundle();
        args.putString("categoryName", category.getName());
        args.putString("categoryImageUrl", category.getImageUrl());
        fragment.setArguments(args);

        // Navigate to EditCategoryFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment) // Replace with your fragment container ID
                .addToBackStack(null)
                .commit();*/
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
                        products.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            products.add(product);
                        }

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



    // On category click, fetch products related to that category
    @Override
    public void onCategoryClick(Category category) {
        // Fetch products for the clicked category
        fetchProductsForCategory(category.getId());
    }

    @Override
    public void onProductClick(Product product) {
        Toast.makeText(getContext(), "HAHAHAHAHAHA", Toast.LENGTH_SHORT).show();
    }

}

