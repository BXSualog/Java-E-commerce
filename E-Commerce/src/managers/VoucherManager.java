package managers;

import functions.Voucher;
import utilities.FileHandler;
import java.time.LocalDate;
import java.util.*;

public class VoucherManager {
    private List<Voucher> vouchers;
    private Map<Integer, String> dailyClaims; // userId -> lastClaimDate (YYYY-MM-DD)
    private static final String VOUCHER_FILE = "vouchers.csv";
    private static final String CLAIMS_FILE = "daily_claims.csv";
    private Random random = new Random();

    public VoucherManager() {
        this.vouchers = new ArrayList<>();
        this.dailyClaims = new HashMap<>();
        loadData();
    }

    private void loadData() {
        List<String> vLines = FileHandler.readCSV(VOUCHER_FILE);
        for (String line : vLines) {
            Voucher v = Voucher.fromCSV(line);
            if (v != null)
                vouchers.add(v);
        }

        List<String> cLines = FileHandler.readCSV(CLAIMS_FILE);
        for (String line : cLines) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                dailyClaims.put(Integer.parseInt(parts[0]), parts[1]);
            }
        }
    }

    private void saveData() {
        List<String> vLines = new ArrayList<>();
        for (Voucher v : vouchers) {
            vLines.add(v.toCSV());
        }
        FileHandler.saveListToCSV(VOUCHER_FILE, vLines);

        List<String> cLines = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : dailyClaims.entrySet()) {
            cLines.add(entry.getKey() + "," + entry.getValue());
        }
        FileHandler.saveListToCSV(CLAIMS_FILE, cLines);
    }

    public boolean canClaimToday(int userId) {
        String today = LocalDate.now().toString();
        return !today.equals(dailyClaims.get(userId));
    }

    public Voucher claimDailyVoucher(int userId) {
        if (!canClaimToday(userId))
            return null;

        Voucher reward = generateRandomVoucher(userId);
        vouchers.add(reward);
        dailyClaims.put(userId, LocalDate.now().toString());
        saveData();
        return reward;
    }

    private Voucher generateRandomVoucher(int userId) {
        int id = vouchers.size() > 0 ? vouchers.get(vouchers.size() - 1).getVoucherId() + 1 : 5001;
        int roll = random.nextInt(100) + 1; // 1-100

        if (roll <= 5) { // 5% Legendary
            return new Voucher(id, userId, "Deserve Mo ‘To", "PERCENTAGE", 50.0, 1000.0, false);
        } else if (roll <= 30) { // 25% Rare
            if (random.nextBoolean())
                return new Voucher(id, userId, "Budol Approved", "PERCENTAGE", 20.0, 500.0, false);
            else
                return new Voucher(id, userId, "Suki Discount", "FIXED", 100.0, 500.0, false);
        } else { // 70% Common
            if (random.nextBoolean())
                return new Voucher(id, userId, "Elite Saver", "PERCENTAGE", 10.0, 100.0, false);
            else
                return new Voucher(id, userId, "Reward Rush", "FIXED", 30.0, 100.0, false);
        }
    }

    public List<Voucher> getAvailableVouchers(int userId) {
        List<Voucher> userVouchers = new ArrayList<>();
        for (Voucher v : vouchers) {
            if (v.getUserId() == userId && !v.isUsed()) {
                userVouchers.add(v);
            }
        }
        return userVouchers;
    }

    public void useVoucher(int voucherId) {
        for (Voucher v : vouchers) {
            if (v.getVoucherId() == voucherId) {
                v.setUsed(true);
                saveData();
                return;
            }
        }
    }
}
