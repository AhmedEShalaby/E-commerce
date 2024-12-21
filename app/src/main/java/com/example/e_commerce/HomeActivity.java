package com.example.e_commerce;

import static com.example.e_commerce.LoginActivity.SHARED_PREFS;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.search.SearchView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements onScannedResult {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private User currentUser;
    private ImageButton cart_btn;
    private ImageButton addCategoryProduct_btn;
    private ImageButton home_btn;
    private TextView context_title;
    private HomeFragment homeFragment;
    ProgressBar progressBar;
    private static final int RQ_SPEECH_REC = 109;

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

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setTint(getResources().getColor(R.color.Desert_Blush));
        }

        progressBar = findViewById(R.id.progressBar_home);
        cart_btn = findViewById(R.id.cart_btn);
        addCategoryProduct_btn = findViewById(R.id.addCategory_btn);
        //home_btn = findViewById(R.id.home_btn);
        context_title = findViewById(R.id.context_title);
        ImageButton logout_btn = findViewById(R.id.logout_btn);

        // Variables for Firebase instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        homeFragment = new HomeFragment();

        welcomeCurrentUser();


        //loadFragment(homeFragment);

        registerForContextMenu(addCategoryProduct_btn);



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

        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example: Pass a list of cart items
                //List<CartItem> cartItems = currentUser.getCartItems(); // Retrieve your cart items list

                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                intent.putExtra("currentUser", currentUser); // Pass the data
                startActivity(intent);
            }
        });


        /*cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });*/
    }

    @Override
    public void onScannedResult(String result) {
        // Pass the scanned result to HomeFragment's function
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).filterProducts(result); // Call the search function with the scanned result
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                /*Bundle args = new Bundle();
                args.putBoolean("search", false);
                homeFragment.setArguments(args);*/
                //HomeFragment homeFragment1 = new HomeFragment();
                loadFragment(homeFragment);
                context_title.setText("Home");
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
                return true;


            case R.id.scan_barcode:
                // Handle the barcode scanner item
                //loadFragment(homeFragment);
                startBarcodeScanner();
                Toast.makeText(this, "Scan Barcode clicked", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.search_voice:
                // Handle the voice search item

                loadFragment(homeFragment);
                startVoiceRecognition();

                /*if (homeFragment != null && homeFragment.isAdded() && homeFragment.isVisible()) {
                    // The HomeFragment is running in the fragment container
                    startVoiceRecognition();
                } else {
                    // The HomeFragment is not running
                    Toast.makeText(this, "Return to home", Toast.LENGTH_SHORT).show();
                }*/
                return true;

            case R.id.orders:
                Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
                intent.putExtra("currentUser", currentUser); // Pass the data
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startBarcodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Align the barcode within the frame");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        barcodeLauncher.launch(options);
    }
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                String scannedResult = result.getContents();
                if (result.getContents() != null) {
                    Toast.makeText(this, scannedResult, Toast.LENGTH_SHORT).show();
                    //HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (homeFragment != null) {
                        homeFragment.filterProducts(scannedResult); // Update the search query in the fragment
                    }
                    //homeFragment.filterProducts(scannedResult);
                } else {
                    Toast.makeText(this, "No barcode found", Toast.LENGTH_SHORT).show();
                }
            }
    );


    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now to search for products");
        startActivityForResult(intent, RQ_SPEECH_REC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_SPEECH_REC && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String query = results.get(0); // Get the first recognized result

                // Pass the recognized query to HomeFragment's search
                HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (homeFragment != null) {
                    homeFragment.filterProducts(query); // Update the search query in the fragment
                }
            }
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
        editor.clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
                //cart.setVisibility(View.GONE);
                //addCategoryProduct_btn.setVisibility(View.GONE);
                context_title.setText(R.string.add_category);
                loadFragment(new AddCategoryFragment());
                return true;

            case R.id.add_product:  // Use the ID from your menu resource
                //cart.setVisibility(View.GONE);
                //addCategoryProduct_btn.setVisibility(View.GONE);
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
                            currentUser = documentSnapshot.toObject(User.class);
                            // Extract name and admin status
                            String name = currentUser.getName();
                            Boolean isAdmin = currentUser.isAdmin();

                            if (name != null) {
                                if(isAdmin != null && isAdmin){
                                    cart_btn.setVisibility(View.GONE);
                                    addCategoryProduct_btn.setVisibility(View.VISIBLE);
                                }
                                else {
                                    cart_btn.setVisibility(View.VISIBLE);
                                    addCategoryProduct_btn.setVisibility(View.GONE);
                                }
                                TextView userNameTextView = findViewById(R.id.name_txt);
                                userNameTextView.setText(name);
                                loadFragment(homeFragment);
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
        Bundle args = new Bundle();
        args.putSerializable("currentUser", currentUser);
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        progressBar.setVisibility(View.GONE);
    }

}
