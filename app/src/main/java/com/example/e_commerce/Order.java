package com.example.e_commerce;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String Id; // Unique ID for the order
    private String userEmail;  // ID of the user who placed the order
    private List<CartItem> items; // List of items in the order
    private double totalPrice; // Total price of the order
    //private String status; // Order status (e.g., "Pending", "Shipped", "Delivered")
    private String orderDate; // Order date (timestamp)
    private String shippingAddress; // Shipping address for the order
    private String feedback;
    private String rating;

    // Default constructor (required for Firebase or other ORMs)
    public Order() {}

    // Parameterized constructor
    public Order(String orderId, String userEmail, List<CartItem> items, double totalPrice, String orderDate, String shippingAddress) {
        this.Id = orderId;
        this.userEmail = userEmail;
        this.items = items;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.shippingAddress = shippingAddress;
    }

    // Getters and Setters
    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getuserEmail() {
        return userEmail;
    }

    public void setuserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    /*@Override
    public String toString() {
        return "Order{" +
                "orderId='" + Id + '\'' +
                ", userId='" + userId + '\'' +
                ", items=" + items +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", orderDate=" + orderDate +
                ", shippingAddress='" + shippingAddress + '\'' +
                '}';
    }*/
}
