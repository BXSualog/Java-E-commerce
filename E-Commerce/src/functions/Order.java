package functions;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private int customerId;
    private List<CartItem> items;
    private double totalAmount;
    private String orderDate;

    public Order(int orderId, int customerId, List<CartItem> items, double totalAmount, String orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
    }

    public Order() {
        this.orderId = 0;
        this.customerId = 0;
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
        this.orderDate = "";
    }

    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public List<CartItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public String getOrderDate() { return orderDate; }

    // serialization format: orderId,customerId,totalAmount,orderDate
    public String toCSV() {
        return orderId + "," + customerId + "," + totalAmount + "," + orderDate;
    }
}
