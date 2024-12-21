package com.example.e_commerce;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private User currentUser;
    private FirebaseFirestore db;
    private RecyclerView recyclerViewOrders;
    private OrdersAdapter ordersAdapter;
    private List<Order> orders, filteredOrders;
    private ImageButton chartsBtn;
    private SearchView searchView;
    ImageButton calenderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orders);
        Toolbar toolbar = findViewById(R.id.orders_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.green_back_arrow);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.orders), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        assert (getIntent().getSerializableExtra("currentUser")!=null);
        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView for Categories
        recyclerViewOrders = findViewById(R.id.orders_rc);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(OrdersActivity.this));

        // Initialize categories list and adapter
        orders = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(OrdersActivity.this, orders, currentUser);
        recyclerViewOrders.setAdapter(ordersAdapter);

        // Fetch categories data from Firestore
        fetchOrdersFromFirestore();

        chartsBtn = findViewById(R.id.chart_btn);
        chartsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrdersActivity.this, ChartActivity.class);
            intent.putExtra("currentUser", currentUser); // Pass the data
            startActivity(intent);
        });

        searchView = findViewById(R.id.search);
        searchView.setIconifiedByDefault(false);
        TextView searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchText != null) {
            searchText.setHintTextColor(Color.parseColor("#47663B"));
            searchText.setTextColor(Color.parseColor("#47663B"));
        }
        searchView.setQueryHint("Search here...");

        filteredOrders  = new ArrayList<>();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterOrders(newText);
                return false;
            }
        });

        calenderBtn = findViewById(R.id.calendar_btn);
        calenderBtn.setOnClickListener(v -> {
            openDialog();
            //Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
        });

        if(!currentUser.isAdmin()){
            calenderBtn.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
        }

    }

    private void fetchOrdersFromFirestore() {

        if(currentUser.isAdmin()){

            db.collection("Orders")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Clear previous data before adding new data
                            orders.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("OrdersData", "Fetched: " + document.getData());

                                Order order = document.toObject(Order.class);

                                orders.add(order);
                            }
                            // Notify the adapter to update the RecyclerView
                            ordersAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("OrdersActivity", "Error getting documents.", task.getException());
                        }
                    });

        }
        else {

            db.collection("Orders")
                    .whereEqualTo("userEmail", currentUser.getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Clear previous data before adding new data
                            orders.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("OrdersData", "Fetched: " + document.getData());

                                Order order = document.toObject(Order.class);

                                orders.add(order);
                            }
                            // Notify the adapter to update the RecyclerView
                            ordersAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("OrdersError", "Error getting documents.", task.getException());
                        }
                    });

        }

    }

    public void filterOrders(String query) {

        filteredOrders.clear();
        //Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
        if (query == null || query.trim().isEmpty()) {
            // If the query is empty, restore the full list of products
            ordersAdapter.setOrders(orders);
            ordersAdapter.notifyDataSetChanged();
            return;
        }

        // Filter products based on the query
        for (Order order : orders) {
            boolean matchesName = order.getuserEmail().contains(query);

            if (matchesName) {
                filteredOrders.add(order);
            }
        }

        if (filteredOrders.isEmpty()) {
            Toast.makeText(OrdersActivity.this, "No Order found", Toast.LENGTH_SHORT).show();
        }


        ordersAdapter.setOrders(filteredOrders);
        ordersAdapter.notifyDataSetChanged();

    }

    private void openDialog(){

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

// Create the DatePickerDialog with today's date as the initial date
        DatePickerDialog dialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Format the date and store it in the variable
                        String selectedDate = year + "-" + (month + 1) + "-" + day;
                        searchByDate(selectedDate);
                        Log.d("SelectedDate", "Selected date: " + selectedDate); // Debug log
                    }
                }, currentYear, currentMonth, currentDay // Set today's date as initial values
        );

        dialog.show();
    }

    private void searchByDate(String searchDate) {
        List<Order> userDateFiltered = new ArrayList<>();
        List<Order> dateFiltered = new ArrayList<>();
        if (!filteredOrders.isEmpty()) {
            for (Order order : filteredOrders) {
                boolean matchesDate = order.getOrderDate().equals(searchDate);
                if (matchesDate) {
                    userDateFiltered.add(order);
                }
            }
            ordersAdapter.setOrders(userDateFiltered);
            ordersAdapter.notifyDataSetChanged();
        }
        else {
            dateFiltered.clear();
            for (Order order : orders) {
                boolean matchesDate = order.getOrderDate().equals(searchDate);
                if (matchesDate) {
                    dateFiltered.add(order);
                }
            }
            ordersAdapter.setOrders(dateFiltered);
            ordersAdapter.notifyDataSetChanged();
        }
    }
}