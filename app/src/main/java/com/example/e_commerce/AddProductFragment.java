package com.example.e_commerce;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class AddProductFragment extends Fragment {

    private EditText product_name, product_imageUrl, product_categoryId,  product_description, product_price, product_stock, product_barcodeUrl;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);

        // Initialize UI components
        product_name = rootView.findViewById(R.id.product_name_input);
        product_imageUrl = rootView.findViewById(R.id.product_image_url_input);
        product_categoryId = rootView.findViewById(R.id.category_id_input);
        product_description = rootView.findViewById(R.id.product_description_input);
        product_price = rootView.findViewById(R.id.product_price_input);
        product_stock = rootView.findViewById(R.id.product_stock_input);
        //product_barcodeUrl = rootView.findViewById(R.id.product_barcode_url_input);
        Button addProduct_btn = rootView.findViewById(R.id.add_product_button);


        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Set click listener for adding a category
        addProduct_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*String price_string = product_price.getText().toString().trim();
            String stock_string = product_stock.getText().toString().trim();*/

                String name = product_name.getText().toString().trim();
                String imageUrl = product_imageUrl.getText().toString().trim();
                String categoryId = product_categoryId.getText().toString().trim();
                String description = product_description.getText().toString().trim();
                double price = Double.parseDouble(product_price.getText().toString().trim());
                int stock = Integer.parseInt(product_stock.getText().toString().trim());
                //String barcodeUrl = product_barcodeUrl.getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    product_name.setError("Category name is required");
                    return;
                }

                if (TextUtils.isEmpty(imageUrl)) {
                    product_imageUrl.setError("Image URL is required");
                    return;
                }

                if (TextUtils.isEmpty(categoryId)) {
                    product_categoryId.setError("Category name is required");
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    product_description.setError("Image URL is required");
                    return;
                }

                if (price <= 0) {
                    product_price.setError("Category name is required");
                    return;
                }

                if (stock <= 0) {
                    product_stock.setError("Image URL is required");
                    return;
                }

                /*if (TextUtils.isEmpty(barcodeUrl)) {
                    product_barcodeUrl.setError("Category name is required");
                    return;
                }*/

                // Add the category to Firestore
                addProductToFirestore(name, description, price, imageUrl, categoryId, stock);

                goHome();
            }
        });

        return rootView;
    }

    // Method to add a product to Firestore
    private void addProductToFirestore(String name, String description, double price, String imageUrl, String categoryName, int stock) {
        // Create a new Category object
        Product product = new Product(name, description, price, imageUrl, categoryName, stock);

        db.collection("Products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    String productId = documentReference.getId(); // Get the auto-generated document ID
                    product.setId(productId); // Update the product object with the ID

                    // Update Firestore document with the ID
                    db.collection("Products")
                            .document(productId)
                            .set(product)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();

                                // Clear the input fields
                                product_name.setText("");
                                product_imageUrl.setText("");
                                product_categoryId.setText("");
                                product_description.setText("");
                                product_price.setText("");
                                product_stock.setText("");
                                //product_barcodeUrl.setText("");
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed to update product ID: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to add product: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

    }


    private void goHome() {

        HomeFragment homeFragment = new HomeFragment();
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .addToBackStack(null)
                .commit();
    }
}