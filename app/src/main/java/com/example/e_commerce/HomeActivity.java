package com.example.e_commerce;

import static com.example.e_commerce.LoginActivity.SHARED_PREFS;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.material.search.SearchView;
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
    ImageButton cart;
    ImageButton addCategory_btn;
    ImageButton home_btn;
    TextView context_title;

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

        cart = findViewById(R.id.cart_btn);
        addCategory_btn = findViewById(R.id.addCategory_btn);
        home_btn = findViewById(R.id.home_btn);
        context_title = findViewById(R.id.context_title);
        ImageButton logout_btn = findViewById(R.id.logout_btn);

        // Variables for Firebase instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        welcomeCurrentUser();

        loadFragment(new HomeFragment());

        registerForContextMenu(addCategory_btn);


        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setMessage("Do you really want to Logout").setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
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

        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeFragment());
                home_btn.setVisibility(View.GONE);
                addCategory_btn.setVisibility(View.GONE);
                context_title.setText(R.string.home);
            }
        });

    }

    private void logout() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Removes all stored data
        editor.apply();  // Save changes

        // Navigate to LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Optional: Finish current activity
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.floating_menu, menu);

    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_category:  // Use the ID from your menu resource
                // Handle add category action
                cart.setVisibility(View.GONE);
                addCategory_btn.setVisibility(View.GONE);
                home_btn.setVisibility(View.VISIBLE);
                context_title.setText(R.string.add_category);
                loadFragment(new AddCategoryFragment());
                return true;

            case R.id.add_product:  // Use the ID from your menu resource
                cart.setVisibility(View.GONE);
                addCategory_btn.setVisibility(View.GONE);
                home_btn.setVisibility(View.VISIBLE);
                context_title.setText(R.string.add_product);
                loadFragment(new AddProductFragment());
                return true;

            default:
                return super.onContextItemSelected(item);
        }
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

                            if (name != null) {
                                if(isAdmin != null && isAdmin){
                                    cart.setVisibility(View.GONE);
                                    addCategory_btn.setVisibility(View.GONE);
                                }
                                TextView userNameTextView = findViewById(R.id.name_txt);
                                userNameTextView.setText(name);
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
