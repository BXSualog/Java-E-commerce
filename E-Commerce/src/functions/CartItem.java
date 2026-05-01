package functions;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public CartItem() {
        this.product = new Product();
        this.quantity = 0;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }
    
    public double getSubTotal() {
        return product.getPrice() * quantity;
    }

    // serialization for order_items: productId:quantity
    public String toCSV() {
        return product.getProductId() + ":" + quantity;
    }

    @Override
    public String toString() {
        return String.format("%-18s x %d = ₱%.2f", product.getName(), quantity, getSubTotal());
    }
}
