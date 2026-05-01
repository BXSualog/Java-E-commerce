package Menu;

import managers.*;
import functions.*;
import utilities.InputValidator;
import utilities.FileHandler;
import java.util.ArrayList;
import java.util.List;

public class MenuUI {
    private UserManager userManager;
    private ProductManager productManager;
    private OrderManager orderManager;
    private VoucherManager voucherManager;
    private CartManager cartManager;

    public MenuUI() {
        this.productManager = new ProductManager();
        this.userManager = new UserManager();
        this.orderManager = new OrderManager(productManager);
        this.voucherManager = new VoucherManager();
        this.cartManager = new CartManager(productManager);
    }

    public void start() {
        while (true) {
            System.out.println("\n==================================================");
            System.out.println("        WELCOME TO XM XUPERMALL");
            System.out.println("==================================================");
            System.out.println("           [1] Customer Login");
            System.out.println("           [2] Admin Login");
            System.out.println("           [3] Register New User");
            System.out.println("           [4] Exit System");
            System.out.println("==================================================");

            int choice = InputValidator.readInt("Enter your choice: ", 1, 4);

            switch (choice) {
                case 1:
                    handleLogin("CUSTOMER");
                    break;
                case 2:
                    handleLogin("ADMIN");
                    break;
                case 3:
                    handleRegister();
                    break;
                case 4:
                    System.out.println("Thank you for visiting XM XuperMall. Goodbye!");
                    return;
            }
        }
    }

    private void handleRegister() {
        System.out.println("\n--- REGISTRATION ---");
        String user = InputValidator.readString("Username: ", false);
        String pass = InputValidator.readString("Password: ", false);
        // Fixed to CUSTOMER role as requested
        String role = "CUSTOMER";

        if (userManager.register(user, pass, role)) {
            System.out.println("[SUCCESS] Registration successful!");
        } else {
            System.out.println("[ERROR] Username already exists.");
        }
    }

    private void handleLogin(String requiredRole) {
        System.out.println("\n--- " + requiredRole + " LOGIN ---");
        String user = InputValidator.readString("Username: ", false);
        String pass = InputValidator.readString("Password: ", false);

        // Fixed Admin Credentials Check
        if (requiredRole.equals("ADMIN") && user.equals("admin")) {
            if (pass.equals("admin123")) {
                System.out.println("[SUCCESS] Admin Authentication successful!");
                adminMenu();
                return;
            } else {
                System.out.println("[ERROR] Incorrect password.");
                return;
            }
        }

        // Granular Error Handling for Customers/Users
        User foundUser = null;
        for (User u : userManager.getAllUsers()) {
            if (u.getUsername().equalsIgnoreCase(user)) {
                foundUser = u;
                break;
            }
        }

        if (foundUser == null) {
            System.out.println("[ERROR] Account does not exist.");
            return;
        }

        if (!foundUser.getPassword().equals(pass)) {
            System.out.println("[ERROR] Incorrect password.");
            return;
        }

        if (requiredRole != null && !foundUser.getRole().equals(requiredRole)) {
            System.out.println("[ERROR] Invalid role for this account.");
            return;
        }

        // If we reach here, login is successful
        User u = foundUser;
        // Personalized Welcome logic
        if (u.getLoginCount() == 0) {
            System.out.println("[SUCCESS] Welcome new customer, " + u.getUsername() + "!");
        } else {
            System.out.println("[SUCCESS] Welcome back, " + u.getUsername() + "!");
        }
        userManager.incrementLoginCount(u);
        userManager.login(u.getUsername(), u.getPassword(), u.getRole()); // Set as current user in manager

        if (u.isAdmin())
            adminMenu();
        else
            customerMenu();
    }

    // --- CUSTOMER SECTION ---
    private void customerMenu() {
        while (true) {
            User current = userManager.getCurrentUser();
            Cart cart = cartManager.getUserCart(current.getId());
            System.out.println("\n==================================================");
            System.out.println("        XM Shopper's Panel | XM Points: " + current.getPoints());
            System.out.println("==================================================");
            System.out.println("[1] Browse Products");
            System.out.println("[2] Top rated & Trending");

            String cartInfo = "";
            if (!cart.isEmpty()) {
                cartInfo = " (" + cart.getItems().size() + " items inside!)";
            }
            System.out.println("[3] My Cart" + cartInfo);
            System.out.println("[4] Order History");
            System.out.println("[5] XM Voucher");
            System.out.println("[0] Logout");
            System.out.println("==================================================");

            int choice = InputValidator.readInt("Choice: ", 0, 5);
            if (choice == 0) {
                userManager.logout();
                break;
            }

            switch (choice) {
                case 1:
                    browseMenu();
                    break;
                case 2:
                    recommendationMenu();
                    break;
                case 3:
                    cartMenu();
                    break;
                case 4:
                    historyMenu();
                    break;
                case 5:
                    handleVoucherMenu();
                    break;
            }
        }
    }

    private void browseMenu() {
        System.out.println("\n--- BROWSE PRODUCTS ---");
        System.out.println("[1] View All [2] Search Name [3] Filter Category [4] Sort By");
        int c = InputValidator.readInt("Choice: ", 1, 4);

        List<Product> list = null;
        switch (c) {
            case 1:
                list = productManager.getAllProducts();
                break;
            case 2:
                list = productManager.searchByName(InputValidator.readString("Search: ", false));
                break;
            case 3:
                List<String> categories = productManager.getCategories();
                if (categories.isEmpty()) {
                    System.out.println("No categories found.");
                    return;
                }
                System.out.println("\n--- SELECT CATEGORY ---");
                for (int i = 0; i < categories.size(); i++) {
                    System.out.println("[" + (i + 1) + "] " + categories.get(i));
                }
                int catChoice = InputValidator.readInt("Choice: ", 1, categories.size());
                list = productManager.filterByCategory(categories.get(catChoice - 1));
                break;
            case 4:
                System.out.println("Sort: [1] Price Lowest to Highest [2] Stock [3] Rating");
                int sortChoice = InputValidator.readInt("Choice: ", 1, 3);
                list = productManager.sortProducts(sortChoice);
                break;
            default:
                list = productManager.getAllProducts();
                break;
        }

        if (list == null || list.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + list.get(i).toString());
        }

        int choiceIndex = InputValidator.readInt("\nEnter Choice # to add to cart (or 0 to go back): ", 0, list.size());
        if (choiceIndex != 0) {
            Product p = list.get(choiceIndex - 1);
            if (p.getStock() <= 0) {
                System.out.println("[WARNING] Sorry, " + p.getName() + " is currently OUT OF STOCK!");
            } else {
                int qty = InputValidator.readInt("Quantity (Available: " + p.getStock() + "): ", 1, p.getStock());
                User current = userManager.getCurrentUser();
                Cart cart = cartManager.getUserCart(current.getId());
                cart.addItem(p, qty);
                cartManager.saveUserCart(current.getId(), cart);
                System.out.println("[SUCCESS] " + qty + " item(s) added to cart!");
            }
        }
    }

    private void cartMenu() {
        User current = userManager.getCurrentUser();
        Cart cart = cartManager.getUserCart(current.getId());

        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }
        System.out.println("\n--- YOUR CART ---");
        List<CartItem> items = cart.getItems();
        for (int i = 0; i < items.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + items.get(i).toString());
        }
        System.out.println("Subtotal: ₱" + cart.getSubTotal());

        System.out.println("[1] Checkout Selected Items [2] Checkout All [3] Remove Item [4] Clear Cart [0] Back");
        int c = InputValidator.readInt("Choice: ", 0, 4);
        if (c == 1) {
            Cart selectedCart = new Cart();
            System.out.println("\n--- SELECT ITEMS TO CHECKOUT ---");
            while (true) {
                int pick = InputValidator.readInt("Enter Choice No. to checkout (or 0 to finish selection): ", 0, items.size());
                if (pick == 0) break;
                
                CartItem selectedItem = items.get(pick - 1);
                boolean exists = false;
                for (int i = 0; i < selectedCart.getItems().size(); i++) {
                    if (selectedCart.getItems().get(i).getProduct().getProductId() == selectedItem.getProduct().getProductId()) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    selectedCart.addItem(selectedItem.getProduct(), selectedItem.getQuantity());
                    System.out.println("[ADDED] " + selectedItem.getProduct().getName() + " to checkout list.");
                } else {
                    System.out.println("[INFO] Item already selected.");
                }
            }
            if (!selectedCart.isEmpty()) {
                checkoutFlow(selectedCart);
            } else {
                System.out.println("[INFO] No items selected. Checkout cancelled.");
            }
        } else if (c == 2) {
            checkoutFlow(cart);
        } else if (c == 3) {
            int removeChoice = InputValidator.readInt("Enter Choice No. to remove: ", 1, items.size());
            functions.CartItem itemToRemove = items.get(removeChoice - 1);
            cart.removeItem(itemToRemove.getProduct().getProductId());
            cartManager.saveUserCart(current.getId(), cart);
            System.out.println("[SUCCESS] Item removed.");
        } else if (c == 4) {
            cartManager.clearUserCart(current.getId());
            System.out.println("[SUCCESS] Cart cleared.");
        }
    }

    private void checkoutFlow(Cart checkoutCart) {
        while (true) {
            User current = userManager.getCurrentUser();
            System.out.println("\n--- CHECKOUT OPTIONS ---");
            System.out.println("Total Amount: ₱" + checkoutCart.getTotalAmount());
            System.out.println("Available XM Reward Points: " + current.getPoints());
            System.out.println("[1] Buy Now");
            System.out.println("[2] Apply Voucher");
            System.out.println("[3] Apply XM Points");
            System.out.println("[0] Cancel Checkout");
            System.out.println("------------------------");

            int choice = InputValidator.readInt("Choice: ", 0, 3);
            if (choice == 0)
                break;

            if (choice == 1) {
                List<CartItem> itemsSnapshot = new ArrayList<>(checkoutCart.getItems()); // Save items for review
                Order o = orderManager.processCheckout(current, checkoutCart);
                if (o != null) {
                    System.out.println("[SUCCESS] Purchase successful! Order #" + o.getOrderId());
                    // Add points
                    userManager.updatePoints(current, current.getPoints() + (int) (o.getTotalAmount() / 100));

                    // Post-purchase review
                    promptPostPurchaseReview(itemsSnapshot);

                    // Update the user's persistent cart
                    Cart mainCart = cartManager.getUserCart(current.getId());
                    for (int i = 0; i < itemsSnapshot.size(); i++) {
                        mainCart.removeItem(itemsSnapshot.get(i).getProduct().getProductId());
                    }
                    cartManager.saveUserCart(current.getId(), mainCart);
                    break;
                }
            } else if (choice == 2) {
                List<Voucher> available = voucherManager.getAvailableVouchers(current.getId());

                if (available.isEmpty()) {
                    System.out.println("\n[INFO] You have no available vouchers. Claim your daily reward first!");
                    continue;
                }

                System.out.println("\n--- SELECT VOUCHER ---");
                for (int i = 0; i < available.size(); i++) {
                    System.out.println("[" + (i + 1) + "] " + available.get(i).toString());
                }
                System.out.println("[0] Back");

                int vChoice = InputValidator.readInt("Select Voucher index: ", 0, available.size());
                if (vChoice == 0)
                    continue;

                Voucher selected = available.get(vChoice - 1);
                if (checkoutCart.getSubTotal() < selected.getMinSpend()) {
                    System.out.println(
                            "[ERROR] Minimum spend of ₱" + selected.getMinSpend() + " required for this voucher.");
                } else {
                    double disc = 0;
                    if (selected.getType().equals("FIXED")) {
                        disc = selected.getValue();
                    } else {
                        disc = checkoutCart.getSubTotal() * (selected.getValue() / 100.0);
                    }
                    checkoutCart.setDiscountAmount(disc);
                    voucherManager.useVoucher(selected.getVoucherId());
                    System.out.println("[SUCCESS] Voucher applied! Saved ₱" + String.format("%.2f", disc));
                    // Removed break; to allow the user to continue checking out
                }
            } else if (choice == 3) {
                if (current.getPoints() <= 0) {
                    System.out.println("\n[INFO] You do not have any XM Reward Points to use.");
                    continue;
                }

                int maxPoints = (int) Math.min(current.getPoints(), checkoutCart.getTotalAmount());
                if (maxPoints <= 0) {
                    System.out.println("\n[INFO] Checkout total is already ₱0.00.");
                    continue;
                }

                System.out.println("\nYou can use up to " + maxPoints + " points (1 Point = ₱1.00).");
                int pointsToUse = InputValidator.readInt("Enter points to use (0 to cancel): ", 0, maxPoints);

                if (pointsToUse > 0) {
                    checkoutCart.setDiscountAmount(checkoutCart.getDiscountAmount() + pointsToUse);
                    userManager.updatePoints(current, current.getPoints() - pointsToUse);
                    System.out.println("[SUCCESS] " + pointsToUse + " XM Reward Points applied! Saved ₱" + pointsToUse);
                }
            }
        }
    }

    private void promptPostPurchaseReview(List<CartItem> items) {
        System.out.println("\n--- RATE YOUR PURCHASE ---");
        if (InputValidator.confirm("Would you like to rate the products you just bought?")) {
            for (int i = 0; i < items.size(); i++) {
                Product p = items.get(i).getProduct();
                System.out.println("\nProduct: " + p.getName());
                if (InputValidator.confirm("Rate this product?")) {
                    int rating = InputValidator.readInt("Rating (1-5 stars): ", 1, 5);
                    String comment = InputValidator.readString("Comment: ", false);
                    productManager.addReview(p.getProductId(), userManager.getCurrentUser().getId(), rating, comment);
                    System.out.println("[SUCCESS] Thank you for your review!");
                }
            }
            System.out.println("\nAll reviews submitted. Thank you for your feedback!");
        }
    }

    private void historyMenu() {
        List<Order> history = orderManager.getHistory(userManager.getCurrentUser().getId());
        if (history.isEmpty()) {
            System.out.println("No past orders.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            Order o = history.get(i);
            System.out.println("Order #" + o.getOrderId() + " | Date: " + o.getOrderDate() + " | ₱" + o.getTotalAmount());
        }
    }

    private void recommendationMenu() {
        System.out.println("\n--- TOP RATED PRODUCTS ---");
        List<Product> top = productManager.sortProducts(3);
        int displayed = 0;
        for (int i = 0; i < top.size() && displayed < 5; i++) {
            Product p = top.get(i);
            if (p.getAverageRating() >= 3.0) {
                String comment = productManager.getLatestComment(p.getProductId());
                System.out.println(String.format("[%d] %-18s | Price: ₱%.2f | Rating: %.1f | Comment: %s", 
                    (displayed + 1), p.getName(), p.getPrice(), p.getAverageRating(), comment));
                displayed++;
            }
        }
        if (displayed == 0) {
            System.out.println("No top-rated items at the moment.");
        }

        System.out.println("\n--- PERSONALIZED FOR YOU ---");
        List<Order> h = orderManager.getHistory(userManager.getCurrentUser().getId());
        if (!h.isEmpty()) {
            String cat = h.get(0).getItems().get(0).getProduct().getCategory();
            List<Product> recs = productManager.getRecommendations(cat);
            for (int i = 0; i < recs.size(); i++) {
                Product p = recs.get(i);
                String comment = productManager.getLatestComment(p.getProductId());
                System.out.println(String.format("[%d] %-18s | Price: ₱%.2f | Rating: %.1f | Comment: %s", 
                    (i + 1), p.getName(), p.getPrice(), p.getAverageRating(), comment));
            }
        } else {
            System.out.println("Buy something first for personalized tips!");
        }
    }

    // --- ADMIN SECTION ---
    private void adminMenu() {
        while (true) {
            checkLowStock();
            System.out.println("\n==================================================");
            System.out.println("           ADMIN CONTROL PANEL");
            System.out.println("==================================================");
            System.out.println("[1] View Products  [2] Sales Report  [3] User Management");
            System.out.println("[4] View Receipts [0] Logout");
            System.out.println("==================================================");

            int choice = InputValidator.readInt("Choice: ", 0, 4);
            if (choice == 0)
                break;

            if (choice == 1) {
                handleViewProducts();
            } else if (choice == 2) {
                orderManager.printSalesReport();
            } else if (choice == 3) {
                handleUserManagement();
            } else if (choice == 4) {
                handleViewReceipts();
            }
        }
    }

    private void handleUserManagement() {
        System.out.println("\n--- USER MANAGEMENT ---");
        List<User> users = userManager.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.printf("%-5s | %-15s | %-10s\n", "ID", "Username", "Role");
        System.out.println("---------------------------------------");
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            System.out.printf("%-5d | %-15s | %-10s\n", u.getId(), u.getUsername(), u.getRole());
        }

        int id = InputValidator.readInt("\nEnter User ID to view details (or 0 to back): ", 0, 9999);
        if (id == 0)
            return;

        User selectedUser = null;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == id) {
                selectedUser = users.get(i);
                break;
            }
        }

        if (selectedUser != null) {
            System.out.println("\n--- USER DETAILS ---");
            System.out.println("Username: " + selectedUser.getUsername());
            System.out.println("Password: " + selectedUser.getPassword());
            System.out.println("Role:     " + selectedUser.getRole());
            System.out.println("Points:   " + selectedUser.getPoints());
        } else {
            System.out.println("[ERROR] User not found.");
        }
    }

    private void handleViewProducts() {
        while (true) {
            System.out.println("\n--- PRODUCT INVENTORY ---");
            List<Product> products = productManager.getAllProducts();
            if (products.isEmpty()) {
                System.out.println("No products in inventory.");
            } else {
                System.out.println(String.format("%-5s | %-5s | %-20s | %-15s | %-12s | %-5s", "No.", "ID", "Name",
                        "Category", "Price", "Stock"));
                System.out
                        .println("----------------------------------------------------------------------------------");
                for (int i = 0; i < products.size(); i++) {
                    Product p = products.get(i);
                    System.out.println(String.format("[%2d]  | %-5d | %-20s | %-15s | ₱%-11.2f | %-5d",
                            (i + 1), p.getProductId(), p.getName(), p.getCategory(), p.getPrice(), p.getStock()));
                }
            }

            System.out.println("\n[1] Add Product  [2] Delete Product  [3] Edit Product  [0] Back to Dashboard");
            int choice = InputValidator.readInt("Choice: ", 0, 3);
            if (choice == 0)
                break;

            if (choice == 1) {
                String name = InputValidator.readString("Name: ", false);
                double price = InputValidator.readDouble("Price: ", 0);
                int stock = InputValidator.readInt("Initial Stock: ", 1, 9999);
                String cat = InputValidator.readString("Category: ", false);
                productManager.addProduct(name, price, stock, cat);
                System.out.println("[SUCCESS] Product added!");
            } else if (choice == 2) {
                int cNum = InputValidator.readInt("Enter Choice No. to delete: ", 1, products.size());
                Product p = products.get(cNum - 1);
                productManager.deleteProduct(p.getProductId());
                System.out.println("[SUCCESS] Product deleted!");
            } else if (choice == 3) {
                int cNum = InputValidator.readInt("Enter Choice No. to edit: ", 1, products.size());
                editProductFlow(products.get(cNum - 1));
            }
        }
    }

    private void handleViewReceipts() {
        System.out.println("\n--- VIEW RECEIPTS ---");
        List<String> receipts = FileHandler.listReceipts();
        if (receipts.isEmpty()) {
            System.out.println("No receipts found.");
            return;
        }

        for (int i = 0; i < receipts.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + receipts.get(i));
        }

        int choice = InputValidator.readInt("\nSelect Receipt to view (or 0 to back): ", 0, receipts.size());
        if (choice == 0)
            return;

        String selected = receipts.get(choice - 1);
        System.out.println("\n--- RECEIPT CONTENT ---");
        System.out.println(FileHandler.readReceipt(selected));
    }

    private void checkLowStock() {
        List<Product> products = productManager.getAllProducts();
        boolean found = false;
        StringBuilder sb = new StringBuilder("\n[LOW STOCK ALERT] The following products are running out:\n");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.getStock() < 3) {
                sb.append("- ").append(p.getName()).append(" (Stock: ").append(p.getStock()).append(")\n");
                found = true;
            }
        }
        if (found) {
            System.out.println(sb.toString());
        }
    }

    private void editProductFlow(Product p) {
        while (true) {
            System.out.println("\n--- EDITING: " + p.getName() + " (ID: " + p.getProductId() + ") ---");
            System.out.println("[1] Name     : " + p.getName());
            System.out.println("[2] Price    : ₱" + String.format("%.2f", p.getPrice()));
            System.out.println("[3] Category : " + p.getCategory());
            System.out.println("[4] Stock    : " + p.getStock());
            System.out.println("[0] Back to Inventory");

            int choice = InputValidator.readInt("Select field to edit: ", 0, 4);
            if (choice == 0)
                break;

            switch (choice) {
                case 1:
                    p.setName(InputValidator.readString("Enter New Name: ", false));
                    break;
                case 2:
                    p.setPrice(InputValidator.readDouble("Enter New Price: ", 0));
                    break;
                case 3:
                    p.setCategory(InputValidator.readString("Enter New Category: ", false));
                    break;
                case 4:
                    p.setStock(InputValidator.readInt("Enter New Stock Quantity: ", 0, 9999));
                    break;
            }
            productManager.saveData();
            System.out.println("[SUCCESS] Product details updated!");
        }
    }

    private void handleVoucherMenu() {
        while (true) {
            User current = userManager.getCurrentUser();
            System.out.println("\n--- XM VOUCHER DASHBOARD ---");
            System.out.println("[1] Claim Daily Reward");
            System.out.println("[2] View My Vouchers");
            System.out.println("[0] Back");

            int choice = InputValidator.readInt("Choice: ", 0, 2);
            if (choice == 0)
                break;

            if (choice == 1) {
                System.out.println("\n[Starting the XM Voucher Rewards.....]");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                Voucher reward = voucherManager.claimDailyVoucher(current.getId());
                if (reward != null) {
                    System.out.println("\n******************************************");
                    System.out.println("   CONGRATULATIONS! YOU RECEIVED: ");
                    System.out.println("   >>> " + reward.getName() + " <<<");
                    System.out.println("   Value: " + (reward.getType().equals("FIXED") ? "₱" + reward.getValue()
                            : (int) reward.getValue() + "% OFF"));
                    System.out.println("******************************************");
                } else {
                    System.out.println("[ERROR] Already claimed today! Come back tomorrow for more luck.");
                }
            } else if (choice == 2) {
                List<Voucher> myVouchers = voucherManager.getAvailableVouchers(current.getId());
                System.out.println("\n--- MY UNUSED VOUCHERS ---");
                if (myVouchers.isEmpty()) {
                    System.out.println("You don't have any vouchers yet.");
                } else {
                    for (int i = 0; i < myVouchers.size(); i++) {
                        System.out.println(myVouchers.get(i).toString());
                    }
                }
            }
        }
    }
}
