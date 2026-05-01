package functions;

public class User {
    private int id;
    private String username;
    private String password;
    private String role; // ADMIN or CUSTOMER
    private int points;
    private int loginCount;

    public User(int id, String username, String password, String role, int points, int loginCount) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.points = points;
        this.loginCount = loginCount;
    }

    public User() {
        this.id = 0;
        this.username = "";
        this.password = "";
        this.role = "CUSTOMER";
        this.points = 0;
        this.loginCount = 0;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public String toCSV() {
        return id + "," + username + "," + password + "," + role + "," + points + "," + loginCount;
    }

    public static User fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length >= 5) {
            int id = Integer.parseInt(parts[0]);
            String user = parts[1];
            String pass = parts[2];
            String role = parts[3];
            int pts = Integer.parseInt(parts[4]);
            int login = (parts.length > 5) ? Integer.parseInt(parts[5]) : 0; // default for existing
            return new User(id, user, pass, role, pts, login);
        }
        return null;
    }
}
