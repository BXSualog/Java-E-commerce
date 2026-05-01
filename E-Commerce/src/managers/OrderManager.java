package managers;

import functions.*;
import utilities.FileHandler;
import java.util.*;
import java.text.SimpleDateFormat;

public class OrderManager {
    private List<Order> orders;
    private ProductManager productManager;
    private static final String ORDER_FILE = "orders.csv";
    private static final String ITEM_FILE = "order_items.csv";

    public OrderManager(ProductManager productManager) {
        this.orders = new ArrayList<>();
        this.productManager = productManager;
        loadOrders();
    }

    private void loadOrders() {
        List<String> lines = FileHandler.readCSV(ORDER_FILE);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] p = line.split(",");
            if (p.length >= 4) {
                String date = p.length >= 5 ? p[4] : p[3];
                Order o = new Order(Integer.parseInt(p[0]), Integer.parseInt(p[1]), new ArrayList<>(), 
                        Double.parseDouble(p[2]), date);
                orders.add(o);
            }
        }
        
        List<String> itemLines = FileHandler.readCSV(ITEM_FILE);
        for (int i = 0; i < itemLines.size(); i++) {
            String line = itemLines.get(i);
            String[] p = line.split(","); // orderId,productId,qty,priceAtPurchase
            if (p.length == 4) {
                int orderId = Integer.parseInt(p[0]);
                Order o = getOrderById(orderId);
                if (o != null) {
                    Product prod = productManager.getProductById(Integer.parseInt(p[1]));
                    if (prod != null) {
                        o.getItems().add(new CartItem(prod, Integer.parseInt(p[2])));
                    }
                }
            }
        }
    }

    public Order getOrderById(int id) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId() == id) {
                return orders.get(i);
            }
        }
        return null;
    }

    public void saveOrders() {
        List<String> oLines = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            oLines.add(orders.get(i).toCSV());
        }
        
        List<String> iLines = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            for (int k = 0; k < o.getItems().size(); k++) {
                CartItem item = o.getItems().get(k);
                iLines.add(o.getOrderId() + "," + item.getProduct().getProductId() + "," + item.getQuantity() + "," + item.getProduct().getPrice());
            }
        }
        FileHandler.saveListToCSV(ORDER_FILE, oLines);
        FileHandler.saveListToCSV(ITEM_FILE, iLines);
    }

    public Order processCheckout(User user, Cart cart) {
        // Stock Validation
        for (int i = 0; i < cart.getItems().size(); i++) {
            CartItem item = cart.getItems().get(i);
            if (item.getProduct().getStock() < item.getQuantity()) {
                System.out.println("[ERROR] Insufficient stock for " + item.getProduct().getName());
                return null;
            }
        }

        // Reduce Stock
        for (int i = 0; i < cart.getItems().size(); i++) {
            cart.getItems().get(i).getProduct().reduceStock(cart.getItems().get(i).getQuantity());
        }
        productManager.saveData();

        // Create Order
        int orderId = orders.size() > 0 ? orders.get(orders.size() - 1).getOrderId() + 1 : 1001;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Order newOrder = new Order(orderId, user.getId(), new ArrayList<>(cart.getItems()), cart.getTotalAmount(), date);
        orders.add(newOrder);
        saveOrders();

        // Generate Receipt
        generateReceipt(newOrder, user, cart);
        
        return newOrder;
    }

    private void generateReceipt(Order o, User u, Cart c) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================\n");
        sb.append("       XM XuperMall RECEIPT\n");
        sb.append("==============================\n");
        sb.append("Order ID: ").append(o.getOrderId()).append("\n");
        sb.append("Customer: ").append(u.getUsername()).append("\n");
        sb.append("Date: ").append(o.getOrderDate()).append("\n");
        sb.append("------------------------------\n");
        for (int i = 0; i < o.getItems().size(); i++) {
            sb.append(o.getItems().get(i).toString()).append("\n");
        }
        sb.append("------------------------------\n");
        sb.append("Subtotal: ₱").append(String.format("%.2f", c.getSubTotal())).append("\n");
        sb.append("Discount: ₱").append(String.format("%.2f", c.getSubTotal() - c.getTotalAmount())).append("\n");
        sb.append("TOTAL PAID: ₱").append(String.format("%.2f", o.getTotalAmount())).append("\n");
        sb.append("==============================\n");
        sb.append("   Thank you for shopping!\n");
        
        FileHandler.saveReceipt(o.getOrderId(), sb.toString());
    }

    // Admin Reports - Refactored to manual loops
    public void printSalesReport() {
        double totalRevenue = 0;
        HashMap<String, Double> dailyRevenue = new HashMap<>();
        HashMap<String, Double> monthlyRevenue = new HashMap<>();
        HashMap<String, Double> categoryRevenue = new HashMap<>();
        HashMap<Integer, Integer> productCounts = new HashMap<>();

        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            totalRevenue += o.getTotalAmount();

            // Daily Revenue breakdown
            String date = o.getOrderDate().substring(0, 10);
            dailyRevenue.put(date, dailyRevenue.getOrDefault(date, 0.0) + o.getTotalAmount());

            // Monthly Revenue breakdown
            String month = o.getOrderDate().substring(0, 7); // YYYY-MM
            monthlyRevenue.put(month, monthlyRevenue.getOrDefault(month, 0.0) + o.getTotalAmount());

            for (int j = 0; j < o.getItems().size(); j++) {
                CartItem item = o.getItems().get(j);
                int pid = item.getProduct().getProductId();
                String cat = item.getProduct().getCategory();
                double itemRev = item.getQuantity() * item.getProduct().getPrice();

                // Category Performance
                categoryRevenue.put(cat, categoryRevenue.getOrDefault(cat, 0.0) + itemRev);

                // Top Selling Product mapping
                productCounts.put(pid, productCounts.getOrDefault(pid, 0) + item.getQuantity());
            }
        }

        StringBuilder reportSb = new StringBuilder();
        reportSb.append("==================================================\n");
        reportSb.append("           XM XUPERMALL SALES REPORT\n");
        reportSb.append("==================================================\n");
        reportSb.append("Generated on: ").append(new java.util.Date()).append("\n\n");
        reportSb.append("Total Orders:  ").append(orders.size()).append("\n");
        reportSb.append("Total Revenue: ₱").append(String.format("%.2f", totalRevenue)).append("\n");

        reportSb.append("\n--- MONTHLY REVENUE BREAKDOWN ---\n");
        for (String month : sortedKeys(monthlyRevenue)) {
            reportSb.append(month).append(": ₱").append(String.format("%.2f", monthlyRevenue.get(month))).append("\n");
        }

        reportSb.append("\n--- DAILY REVENUE BREAKDOWN ---\n");
        for (String date : sortedKeys(dailyRevenue)) {
            reportSb.append(date).append(": ₱").append(String.format("%.2f", dailyRevenue.get(date))).append("\n");
        }

        reportSb.append("\n--- CATEGORY PERFORMANCE ---\n");
        for (String cat : categoryRevenue.keySet()) {
            reportSb.append(cat).append(": ₱").append(String.format("%.2f", categoryRevenue.get(cat))).append("\n");
        }

        // Find top selling product
        int topId = -1;
        int maxSold = -1;
        for (Integer pid : productCounts.keySet()) {
            if (productCounts.get(pid) > maxSold) {
                maxSold = productCounts.get(pid);
                topId = pid;
            }
        }

        if (topId != -1) {
            Product p = productManager.getProductById(topId);
            if (p != null) {
                reportSb.append("\nOverall Top Seller: ").append(p.getName()).append(" (").append(maxSold).append(" sold)\n");
            }
        }

        String reportContent = reportSb.toString();
        System.out.println(reportContent);
        FileHandler.saveTextFile("sales_report.txt", reportContent);
        System.out.println("[INFO] Detailed sales report has been saved to data/sales_report.txt");
    }

    private List<String> sortedKeys(Map<String, Double> map) {
        List<String> keys = new ArrayList<>(map.keySet());
        // Simple selection sort for keys (since we can't use Collections.sort if restricted to basic)
        int n = keys.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (keys.get(j).compareTo(keys.get(minIdx)) < 0) minIdx = j;
            }
            String temp = keys.get(minIdx);
            keys.set(minIdx, keys.get(i));
            keys.set(i, temp);
        }
        return keys;
    }

    public List<Order> getHistory(int customerId) {
        List<Order> history = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getCustomerId() == customerId) {
                history.add(orders.get(i));
            }
        }
        return history;
    }
}
