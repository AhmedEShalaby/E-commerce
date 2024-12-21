package com.example.e_commerce;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;
    private User currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OrdersAdapter(Context context, List<Order> orders, User currentUser) {
        this.context = context;
        this.orders = orders;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.order_number.setText(String.valueOf(position+1));

        holder.order_date.setText(order.getOrderDate());
        List<CartItem> items = order.getItems();
        int itemsCount = 0;
        for (CartItem item : items) {
            itemsCount += item.getQuantity();
        }
        holder.order_items_count.setText(String.valueOf(itemsCount));
        holder.order_total_price.setText("$ " + order.getTotalPrice());

        if(order.getFeedback() != null && !currentUser.isAdmin()){
            holder.submitFeedbackButton.setVisibility(View.GONE);
            holder.ratingBar.setVisibility(View.GONE);
            holder.feedbackInput.setVisibility(View.GONE);
            holder.rating_label.setText("Thank You For Your Purchase");
        }
        else if (!currentUser.isAdmin() && order.getFeedback() == null){
            holder.submitFeedbackButton.setVisibility(View.VISIBLE);
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.feedbackInput.setVisibility(View.VISIBLE);
            holder.rating_label.setText("Rate your order");
        }
        else if (currentUser.isAdmin() && order.getFeedback() == null){
            holder.submitFeedbackButton.setVisibility(View.GONE);
            holder.ratingBar.setVisibility(View.GONE);
            holder.feedbackInput.setVisibility(View.GONE);
            holder.userEmail.setVisibility(View.VISIBLE);
            holder.userEmail.setText(order.getuserEmail());
            holder.rating_label.setText("No feedback or rating yet...");
        }
        else if (currentUser.isAdmin() && order.getFeedback() != null){
            holder.ratingBar.setRating(Float.valueOf(order.getRating()));
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setIsIndicator(true);
            holder.feedback_text.setVisibility(View.VISIBLE);
            holder.feedback_text.setText(order.getFeedback());
            holder.userEmail.setVisibility(View.VISIBLE);
            holder.userEmail.setText(order.getuserEmail());
            holder.rating_label.setText("Customer's feedback");
        }


        holder.submitFeedbackButton.setOnClickListener(v -> {
            String rating = String.valueOf(holder.ratingBar.getRating());
            String feedback = holder.feedbackInput.getText().toString().trim();
            addFeedback(order, position, rating, feedback); });

    }

    private void addFeedback(Order order, int position, String rating, String feedback) {
        if (feedback.isEmpty() || rating.isEmpty()) {
            Toast.makeText(context, "Please provide your rating & feedback", Toast.LENGTH_SHORT).show();
        } else {
            saveFeedback(order, position, rating, feedback);
            Toast.makeText(context, "Thank you for your feedback! Rating: " + rating, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFeedback(Order order, int position, String rating, String feedback) {

        order.setRating(rating);
        order.setFeedback(feedback);

        db.collection("Orders")
                .document(order.getId())
                .set(order)
                .addOnSuccessListener(aVoid -> {
                    notifyItemChanged(position);
                    Toast.makeText(context, "Thank you for your feedback!  " + rating, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to submit feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void setOrders(List<Order> orders){
        this.orders = orders;
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView order_number, order_date, order_items_count, order_total_price, rating_label, feedback_text, userEmail;
        RatingBar ratingBar;
        EditText feedbackInput;
        Button submitFeedbackButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            order_number = itemView.findViewById(R.id.order_number);
            order_date = itemView.findViewById(R.id.order_date);
            order_items_count = itemView.findViewById(R.id.order_items_count);
            order_total_price = itemView.findViewById(R.id.order_total_price);
            submitFeedbackButton = itemView.findViewById(R.id.btn_submit_feedback);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            feedbackInput = itemView.findViewById(R.id.feedback_input);
            rating_label = itemView.findViewById(R.id.rating_label);
            feedback_text = itemView.findViewById(R.id.feedback_text);
            userEmail = itemView.findViewById(R.id.user_email);
        }
    }
}
