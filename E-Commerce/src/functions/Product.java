package functions;

public class Product {
    private int productId;
    private String name;
    private double price;
    private int stock;
    private String category;
    private double averageRating;

    public Product(int productId, String name, double price, int stock, String category, double averageRating) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.averageRating = averageRating;
    }

    public Product() {
        this.productId = 0;
        this.name = "";
        this.price = 0.0;
        this.stock = 0;
        this.category = "";
        this.averageRating = 0.0;
    }

    public int getProductId() { return productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public boolean reduceStock(int quantity) {
        if (this.stock >= quantity) {
            this.stock -= quantity;
            return true;
        }
        return false;
    }
    
    public void addStock(int quantity) {
        this.stock += quantity;
    }

    // serialization format: productId,name,price,stock,category,averageRating
    public String toCSV() {
        return productId + "," + name + "," + price + "," + stock + "," + category + "," + averageRating;
    }

    public static Product fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length == 6) {
            return new Product(
                Integer.parseInt(parts[0]), 
                parts[1], 
                Double.parseDouble(parts[2]), 
                Integer.parseInt(parts[3]), 
                parts[4],
                Double.parseDouble(parts[5])
            );
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%-18s | ID: %-4d | Cat: %-12s | Price: ₱%.2f | Stock: %d | Rating: %.1f", 
                name, productId, category, price, stock, averageRating);
    }
}
