package managers;

import functions.Cart;
import functions.CartItem;
import functions.Product;
import utilities.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String CART_FILE = "cart.csv";
    private ProductManager productManager;

    public CartManager(ProductManager productManager) {
        this.productManager = productManager;
    }

    public Cart getUserCart(int userId) {
        Cart cart = new Cart();
        List<String> lines = FileHandler.readCSV(CART_FILE);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                try {
                    int uid = Integer.parseInt(parts[0]);
                    if (uid == userId) {
                        int pid = Integer.parseInt(parts[1]);
                        int qty = Integer.parseInt(parts[2]);
                        Product p = productManager.getProductById(pid);
                        if (p != null) {
                            cart.addItem(p, qty);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore malformed lines
                }
            }
        }
        return cart;
    }

    public void saveUserCart(int userId, Cart cart) {
        List<String> allLines = FileHandler.readCSV(CART_FILE);
        List<String> newLines = new ArrayList<>();
        
        // Keep lines that don't belong to this user
        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                try {
                    int uid = Integer.parseInt(parts[0]);
                    if (uid != userId) {
                        newLines.add(line);
                    }
                } catch (NumberFormatException e) {
                    // Ignore malformed lines
                }
            }
        }
        
        // Append this user's current cart items
        List<CartItem> items = cart.getItems();
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            newLines.add(userId + "," + item.getProduct().getProductId() + "," + item.getQuantity());
        }
        
        FileHandler.saveListToCSV(CART_FILE, newLines);
    }
    
    public void clearUserCart(int userId) {
        saveUserCart(userId, new Cart());
    }
}
