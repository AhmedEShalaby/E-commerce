package com.example.e_commerce;

import java.util.List;

public class Order {
    private String orderId; // Unique ID for the order
    private String userId;  // ID of the user who placed the order
    private List<CartItem> items; // List of items in the order
    private double totalPrice; // Total price of the order
    private String status; // Order status (e.g., "Pending", "Shipped", "Delivered")
    private long orderDate; // Order date (timestamp)
    private String shippingAddress; // Shipping address for the order

    // Default constructor (required for Firebase or other ORMs)
    public Order() {}

    // Parameterized constructor
    public Order(String orderId, String userId, List<CartItem> items, double totalPrice, String status, long orderDate, String shippingAddress) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderDate = orderDate;
        this.shippingAddress = shippingAddress;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", items=" + items +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", orderDate=" + orderDate +
                ", shippingAddress='" + shippingAddress + '\'' +
                '}';
    }
}
