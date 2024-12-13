package com.example.e_commerce;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;


public class EditProductFragment extends Fragment {


    private EditText productName, productImageUrl, productDescription, productPrice, productBarcodeUrl, productCategoryId, productStock;
    private Button saveButton;
    private FirebaseFirestore db;
    private String productId;
    Button deleteButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_product, container, false);

        // Initialize UI components
        productName = rootView.findViewById(R.id.product_name_input);
        productDescription = rootView.findViewById(R.id.product_description_input);
        productImageUrl = rootView.findViewById(R.id.product_image_url_input);
        productPrice = rootView.findViewById(R.id.product_price_input);
        productBarcodeUrl = rootView.findViewById(R.id.product_barcode_url_input);
        productCategoryId = rootView.findViewById(R.id.product_categoryId_input);
        productStock = rootView.findViewById(R.id.product_stock_input);

        saveButton = rootView.findViewById(R.id.save_product_button);
        deleteButton = rootView.findViewById(R.id.delete_product_button);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            productId = getArguments().getString("productId");
            Log.d("EditCategoryFragment", "Product ID: " + productId);
        } else {
            Log.e("EditCategoryFragment", "Arguments are null!");
        }

        // Get the category ID from arguments
        productId = getArguments().getString("productId");

        // Fetch current category data
        fetchProductData();

        // Save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            HomeFragment homeFragment = new HomeFragment();
            @Override
            public void onClick(View v) {
                String name = productName.getText().toString().trim();
                String imageUrl = productImageUrl.getText().toString().trim();
                String description = productDescription.getText().toString().trim();
                double price = Double.parseDouble(productPrice.getText().toString().trim());
                String barcode = productBarcodeUrl.getText().toString().trim();
                String categoryId = productCategoryId.getText().toString().trim();
                int stock = Integer.parseInt(productStock.getText().toString().trim());

                if (TextUtils.isEmpty(name)) {
                    productName.setError("Product name is required");
                    return;
                }
                if (TextUtils.isEmpty(imageUrl)) {
                    productImageUrl.setError("Image URL is required");
                    return;
                }
                if (TextUtils.isEmpty(description)) {
                    productDescription.setError("Product description is required");
                    return;
                }
                if (price <= 0) {
                    productPrice.setError("Price > 0 is required");
                    return;
                }
                if (TextUtils.isEmpty(barcode)) {
                    productBarcodeUrl.setError("Product barcode is required");
                    return;
                }
                if (TextUtils.isEmpty(categoryId)) {
                    productCategoryId.setError("Category Id is required");
                    return;
                }
                if (stock <= 0) {
                    productStock.setError("Stock > 0 is required");
                    return;
                }

                // Update the category in Firestore
                updateProductInFirestore(name,description, imageUrl, price, barcode, categoryId, stock);

                goHome();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you really want to delete the product?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProduct(productId);
                        goHome();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        return rootView;
    }

    private void goHome() {

        HomeFragment homeFragment = new HomeFragment();
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchProductData() {
        db.collection("Products")
                .document(productId) // Use the document ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);

                        productName.setText(product.getName());
                        productImageUrl.setText(product.getImageUrl());
                        productDescription.setText(product.getDescription());
                        productPrice.setText(String.valueOf(product.getPrice()));
                        productBarcodeUrl.setText(product.getBarcodeUrl());
                        productCategoryId.setText(product.getCategoryId());
                        productStock.setText(String.valueOf(product.getStock()));

                    } else {
                        Toast.makeText(getContext(), "No matching product found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch product: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void updateProductInFirestore(String name,String description, String imageUrl, double price,
                                           String barcode, String categoryId, int stock) {
        db.collection("Products")
                .document(productId) // Use the document ID
                .update("name", name, "description", description,"imageUrl", imageUrl,
                        "price", price, "barcode", barcode, "categoryId", categoryId, "stock", stock)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Product updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to update product: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void deleteProduct(String productId) {
        db.collection("Products")
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Product deleted successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete product", Toast.LENGTH_SHORT).show());
    }


}
