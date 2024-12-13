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
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class EditCategoryFragment extends Fragment {

    private EditText categoryNameInput, categoryImageUrlInput;
    private Button saveButton;
    private FirebaseFirestore db;
    private String categoryId;
    Button deleteButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_category, container, false);

        // Initialize UI components
        categoryNameInput = rootView.findViewById(R.id.category_name_input);
        categoryImageUrlInput = rootView.findViewById(R.id.category_image_url_input);
        saveButton = rootView.findViewById(R.id.save_category_button);
        deleteButton = rootView.findViewById(R.id.delete_category_button);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            Log.d("EditCategoryFragment", "Category ID: " + categoryId);
        } else {
            Log.e("EditCategoryFragment", "Arguments are null!");
        }

        // Get the category ID from arguments
        categoryId = getArguments().getString("categoryId");

        // Fetch current category data
        fetchCategoryData();

        // Save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            HomeFragment homeFragment = new HomeFragment();
            @Override
            public void onClick(View v) {
                String name = categoryNameInput.getText().toString().trim();
                String imageUrl = categoryImageUrlInput.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    categoryNameInput.setError("Category name is required");
                    return;
                }

                if (TextUtils.isEmpty(imageUrl)) {
                    categoryImageUrlInput.setError("Image URL is required");
                    return;
                }

                // Update the category in Firestore
                updateCategoryInFirestore(name, imageUrl);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {

            HomeFragment homeFragment = new HomeFragment();

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you really want to delete the category?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCategory(categoryId);
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, homeFragment)
                                .addToBackStack(null)
                                .commit();
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

    private void fetchCategoryData() {
        db.collection("Categories")
                .document(categoryId) // Use the document ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Category category = documentSnapshot.toObject(Category.class);

                        // Populate input fields
                        categoryNameInput.setText(category.getName());
                        categoryImageUrlInput.setText(category.getImageUrl());
                    } else {
                        Toast.makeText(getContext(), "No matching category found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch category: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void updateCategoryInFirestore(String name, String imageUrl) {
        db.collection("Categories")
                .document(categoryId) // Use the document ID
                .update("name", name, "imageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Category updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to update category: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void deleteCategory(String categoryId) {
        // Delete associated products first
        db.collection("Products")
                .whereEqualTo("categoryId", categoryId)  // Query products associated with the category
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Loop through and delete each product
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        db.collection("Products")
                                .document(document.getId())  // Delete product by its document ID
                                .delete()
                                .addOnFailureListener(e -> Log.e("DeleteCategory", "Failed to delete product: " + e.getMessage()));
                    }

                    // After deleting products, delete the category
                    db.collection("Categories")
                            .document(categoryId)
                            .delete()
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(getContext(), "Category and associated products deleted successfully!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed to delete category", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch associated products", Toast.LENGTH_SHORT).show());
    }

}
