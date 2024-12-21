package com.example.e_commerce;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class AddCategoryFragment extends Fragment {

    private User currentUser;
    private EditText categoryNameInput, categoryImageUrlInput;
    private Button addCategoryButton;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_category, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("currentUser");
        } else {
            Toast.makeText(getContext(), "Add category error", Toast.LENGTH_SHORT).show();
            return rootView;
        }

        // Initialize UI components
        categoryNameInput = rootView.findViewById(R.id.category_name_input);
        categoryImageUrlInput = rootView.findViewById(R.id.category_image_url_input);
        addCategoryButton = rootView.findViewById(R.id.add_category_button);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Set click listener for adding a category
        addCategoryButton.setOnClickListener(v -> {
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

            // Add the category to Firestore
            addCategoryToFirestore(name, imageUrl);

        });

        return rootView;
    }

    // Method to add a category to Firestore
    private void addCategoryToFirestore(String name, String imageUrl) {
        // Create a new Category object
        Category category = new Category(name, imageUrl);

        // Add the category object to the Firestore collection
        db.collection("Categories")
                .add(category)
                .addOnSuccessListener(documentReference -> {
                    String categoryId = documentReference.getId(); // Get the auto-generated document ID
                    category.setId(categoryId); // Update the category object with the ID

                    // Update Firestore document with the ID
                    db.collection("Categories")
                            .document(categoryId)
                            .set(category)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Category added successfully!", Toast.LENGTH_SHORT).show();

                                goHome();
                                // Clear the input fields
                                /*categoryNameInput.setText("");
                                categoryImageUrlInput.setText("");*/
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed to update category ID: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to add category: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

    }

    private void goHome() {

        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable("currentUser", currentUser);
        homeFragment.setArguments(args);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .addToBackStack(null)
                .commit();
    }

}
