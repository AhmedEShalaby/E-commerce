package com.example.e_commerce;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private User currentUser;
    private FirebaseFirestore db;
    private List<Integer> soldQuantities = new ArrayList<>();
    private List<String> productNames = new ArrayList<>();
    private BarChart barChart;
    private PieChart pieChart;
    private int[] chartColors = {
            Color.parseColor("#A02334"), // Original
            Color.parseColor("#B3404D"), // 10% Lighter
            Color.parseColor("#C66167"), // 20% Lighter
            Color.parseColor("#D98181"), // 30% Lighter
            Color.parseColor("#EB999A"), // 40% Lighter
            Color.parseColor("#8E1D2E"), // 10% Darker
            Color.parseColor("#7A1928"), // 20% Darker
            Color.parseColor("#661522"), // 30% Darker
            Color.parseColor("#52121B")  // 40% Darker
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = findViewById(R.id.charts_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.green_back_arrow);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.charts), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        assert (getIntent().getSerializableExtra("currentUser")!=null);
        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        db = FirebaseFirestore.getInstance();

        fetchProductsFromFirestore();

        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pie_chart);
        fetchProductsFromFirestore();
    }

    private void fetchProductsFromFirestore() {
        db.collection("Products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear previous products
                        soldQuantities.clear();
                        productNames.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            soldQuantities.add(product.getSoldQuantity());
                            productNames.add(product.getName());
                        }
                        updateBarChart();
                        updatePieChart();
                    } else {
                        Log.w("fetchProducts", "Error getting products.", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("fetchProducts", "Failed to fetch products: " + e.getMessage());
                    Toast.makeText(ChartActivity.this, "Failed to fetch products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < soldQuantities.size(); i++) {
            entries.add(new BarEntry(i, soldQuantities.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Sold Quantities");
        barDataSet.setColors(chartColors);
        barDataSet.setValueTextColor(Color.parseColor("#A02334"));
        barDataSet.setValueTextSize(30f);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(true);
        barChart.animateY(2000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(productNames));
        xAxis.setTextSize(30f);

        barChart.invalidate(); // Refresh the chart
    }

    private void updatePieChart() {
        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < soldQuantities.size(); i++) {
            entries.add(new PieEntry(soldQuantities.get(i), productNames.get(i)));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Sold Quantities");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(2000);
        pieChart.setCenterTextSize(100f);

        pieChart.invalidate(); // Refresh the chart
    }






   /* private void fetchProductsFromFirestore() {

        db.collection("Products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear previous products
                        //allProducts.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            soldQuantities.add(product.getSoldQuantity());
                        }
                    } else {
                        Log.w("fetchProducts", "Error getting products.", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("fetchProducts", "Failed to fetch products: " + e.getMessage());
                    Toast.makeText(ChartActivity.this, "Failed to fetch products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Prepare data to send back
            Intent intent = new Intent(ChartActivity.this, OrdersActivity.class);
            intent.putExtra("currentUser", currentUser); // Add your data here
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}