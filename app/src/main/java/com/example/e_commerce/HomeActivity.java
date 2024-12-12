package com.example.e_commerce;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    RecyclerView recyclerView;
    TextView name_txt;
    ImageButton cart;
    ImageButton addCategory_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.category_rc);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        List <Category> categories = new ArrayList<>();
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(this, categories);

        ImageButton cart = findViewById(R.id.cart_btn);
        ImageButton addCategory_btn = findViewById(R.id.addCategory_btn);


        // Variables for Firebase instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        welcomeCurrentUser();


        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert document to Category object
                                Category category = document.toObject(Category.class);
                                categories.add(category); // Add to the list
                            }
                            categoriesAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("CategoriesFragment", "Error getting documents.", task.getException());
                        }
                    }
                });

        addCategory_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AddCategoryFragment());
            }
        });

    }

    private void welcomeCurrentUser() {

        if (auth.getCurrentUser() != null) {
            String userEmail = auth.getCurrentUser().getEmail();

            db.collection("Users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Extract name and admin status
                            String name = documentSnapshot.getString("name");
                            Boolean isAdmin = documentSnapshot.getBoolean("admin");
                            String displayName = name;

                            if (name != null) {
                                if(isAdmin != null && isAdmin){
                                    displayName = "Admin " + name;
                                    cart.setVisibility(View.GONE);
                                    addCategory_btn.setVisibility(View.VISIBLE);
                                }
                                TextView userNameTextView = findViewById(R.id.name_txt);
                                userNameTextView.setText(displayName);
                            } else {
                                Log.e("Firestore", "Name field is missing in user document");
                            }
                        } else {
                            Log.e("Firestore", "No user document found for email: " + userEmail);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error retrieving user document", e));


        }

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // Ensure `fragment_container` exists in your layout
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
