package managers;

import functions.Product;
import functions.Review;
import utilities.FileHandler;
import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    private List<Product> products;
    private List<Review> reviews;
    private static final String PRODUCT_FILE = "products.csv";
    private static final String REVIEW_FILE = "reviews.csv";

    public ProductManager() {
        this.products = new ArrayList<>();
        this.reviews = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        List<String> pLines = FileHandler.readCSV(PRODUCT_FILE);
        for (int i = 0; i < pLines.size(); i++) {
            Product p = Product.fromCSV(pLines.get(i));
            if (p != null) products.add(p);
        }
        sortById(); // Ensure initial load is organized
        
        List<String> rLines = FileHandler.readCSV(REVIEW_FILE);
        for (int i = 0; i < rLines.size(); i++) {
            Review r = Review.fromCSV(rLines.get(i));
            if (r != null) reviews.add(r);
        }
    }

    public void saveData() {
        List<String> pLines = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            pLines.add(products.get(i).toCSV());
        }
        FileHandler.saveListToCSV(PRODUCT_FILE, pLines);

        List<String> rLines = new ArrayList<>();
        for (int i = 0; i < reviews.size(); i++) {
            rLines.add(reviews.get(i).toCSV());
        }
        FileHandler.saveListToCSV(REVIEW_FILE, rLines);
    }

    public List<Product> getAllProducts() { return products; }

    public void addProduct(String name, double price, int stock, String category) {
        int id = products.size() > 0 ? products.get(products.size() - 1).getProductId() + 1 : 101;
        products.add(new Product(id, name, price, stock, category, 0.0));
        sortById(); // Keep main list organized by ID
        saveData();
    }

    public void updateProduct(int id, String name, double price, String category) {
        Product p = getProductById(id);
        if (p != null) {
            p.setName(name);
            p.setPrice(price);
            p.setCategory(category);
            saveData();
        }
    }

    public Product getProductById(int id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId() == id) {
                return products.get(i);
            }
        }
        return null;
    }

    public boolean deleteProduct(int id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId() == id) {
                products.remove(i);
                saveData();
                return true;
            }
        }
        return false;
    }

    // Manual Search (Linear Search)
    public List<Product> searchByName(String name) {
        List<Product> results = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }

    // Manual Filter by Category
    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        for (Product p : products) {
            boolean exists = false;
            for (String cat : categories) {
                if (cat.equalsIgnoreCase(p.getCategory())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                categories.add(p.getCategory());
            }
        }
        return categories;
    }

    public List<Product> filterByCategory(String category) {
        List<Product> results = new ArrayList<>();
        for (Product p : products) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                results.add(p);
            }
        }
        return results;
    }

    // Manual Filter (Keep for internal use, though replaced in UI)
    public List<Product> filterByPrice(double min, double max) {
        List<Product> results = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.getPrice() >= min && p.getPrice() <= max) {
                results.add(p);
            }
        }
        return results;
    }

    // Manual Sort Algorithm (Insertion Sort)
    public List<Product> sortProducts(int choice) {
        List<Product> sorted = new ArrayList<>(products);
        int n = sorted.size();

        for (int i = 1; i < n; i++) {
            Product key = sorted.get(i);
            int j = i - 1;

            while (j >= 0 && shouldSwap(sorted.get(j), key, choice)) {
                sorted.set(j + 1, sorted.get(j));
                j--;
            }
            sorted.set(j + 1, key);
        }
        return sorted;
    }

    private boolean shouldSwap(Product a, Product b, int choice) {
        if (choice == 1) return a.getPrice() > b.getPrice(); // Price Low-High
        if (choice == 2) return a.getStock() > b.getStock(); // Stock Low-High
        if (choice == 3) return a.getAverageRating() < b.getAverageRating(); // Rating High-Low
        return false;
    }

    private void sortById() {
        int n = products.size();
        for (int i = 1; i < n; i++) {
            Product key = products.get(i);
            int j = i - 1;
            while (j >= 0 && products.get(j).getProductId() > key.getProductId()) {
                products.set(j + 1, products.get(j));
                j--;
            }
            products.set(j + 1, key);
        }
    }

    // Manual Recommendation logic
    public List<Product> getRecommendations(String lastBoughtCategory) {
        List<Product> results = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.getCategory().equalsIgnoreCase(lastBoughtCategory)) {
                results.add(p);
                if (results.size() == 3) break; // Limit to top 3
            }
        }
        return results;
    }

    public void addReview(int productId, int customerId, int rating, String comment) {
        int id = reviews.size() > 0 ? reviews.get(reviews.size() - 1).getReviewId() + 1 : 1;
        reviews.add(new Review(id, productId, customerId, rating, comment));
        
        // Update product average rating manually
        Product p = getProductById(productId);
        if (p != null) {
            double sum = 0;
            int count = 0;
            for (int i = 0; i < reviews.size(); i++) {
                if (reviews.get(i).getProductId() == productId) {
                    sum += reviews.get(i).getRating();
                    count++;
                }
            }
            if (count > 0) p.setAverageRating(sum / count);
        }
        saveData();
    }
    public String getLatestComment(int productId) {
        String comment = "No comments yet";
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getProductId() == productId) {
                comment = reviews.get(i).getComment();
            }
        }
        return comment;
    }
}
