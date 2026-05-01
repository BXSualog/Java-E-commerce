package functions;

public class Voucher {
    private int voucherId;
    private int userId;
    private String name;
    private String type; // FIXED or PERCENTAGE
    private double value;
    private double minSpend;
    private boolean isUsed;

    public Voucher(int voucherId, int userId, String name, String type, double value, double minSpend, boolean isUsed) {
        this.voucherId = voucherId;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.value = value;
        this.minSpend = minSpend;
        this.isUsed = isUsed;
    }

    public Voucher() {}

    public int getVoucherId() { return voucherId; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getValue() { return value; }
    public double getMinSpend() { return minSpend; }
    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { isUsed = used; }

    // serialization format: voucherId,userId,name,type,value,minSpend,isUsed
    public String toCSV() {
        return voucherId + "," + userId + "," + name + "," + type + "," + value + "," + minSpend + "," + isUsed;
    }

    public static Voucher fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length == 7) {
            return new Voucher(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                parts[2],
                parts[3],
                Double.parseDouble(parts[4]),
                Double.parseDouble(parts[5]),
                Boolean.parseBoolean(parts[6])
            );
        }
        return null;
    }

    @Override
    public String toString() {
        String discountDesc = type.equals("FIXED") ? "₱" + value + " OFF" : (int)value + "% OFF";
        return String.format("%-15s | %-12s | Min Spend: ₱%-6.2f", name, discountDesc, minSpend);
    }
}
