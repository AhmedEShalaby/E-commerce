package com.example.e_commerce;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;
import java.util.UUID;

public class AddCategoryFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText categoryNameInput;
    private Button addCategoryButton;

    //private Uri imageUri;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);

        categoryNameInput = view.findViewById(R.id.category_name_input);
        addCategoryButton = view.findViewById(R.id.add_category_button);

        db = FirebaseFirestore.getInstance();

        addCategoryButton.setOnClickListener(v -> {
            String name = categoryNameInput.getText().toString();

            if (TextUtils.isEmpty(name)) {
                categoryNameInput.setError("Category name is required");
                return;
            }

            addCategoryToFirestore(name);
        });

        return view;
    }

    private void addCategoryToFirestore(String name) {
        Category category = new Category(name);

        db.collection("categories")
                .add(category)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Category added successfully!", Toast.LENGTH_SHORT).show();
                    categoryNameInput.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add category: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

