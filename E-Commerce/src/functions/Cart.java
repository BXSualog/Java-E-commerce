package functions;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;
    private double discountAmount = 0.0;

    public Cart() {
        this.items = new ArrayList<>();
        this.discountAmount = 0.0;
    }

    public void addItem(Product product, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getProductId() == product.getProductId()) {
                item.addQuantity(quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    public boolean removeItem(int productId) {
        return items.removeIf(item -> item.getProduct().getProductId() == productId);
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getSubTotal() {
        double amount = 0;
        for (CartItem item : items) {
            amount += item.getSubTotal();
        }
        return amount;
    }

    public double getTotalAmount() {
        return Math.max(0, getSubTotal() - discountAmount);
    }

    public void setDiscountAmount(double discount) {
        this.discountAmount = discount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void clear() {
        this.items.clear();
        this.discountAmount = 0.0;
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int getItemCount() {
        int count = 0;
        for(CartItem item : items) count += item.getQuantity();
        return count;
    }
}
