package managers;

import functions.User;
import utilities.FileHandler;
import java.util.*;

public class UserManager {
    private List<User> users;
    private User currentUser;
    private static final String FILE_NAME = "users.csv";

    public UserManager() {
        this.users = new ArrayList<>();
        loadUsers();
    }

    private void loadUsers() {
        List<String> lines = FileHandler.readCSV(FILE_NAME);
        for (int i = 0; i < lines.size(); i++) {
            User user = User.fromCSV(lines.get(i));
            if (user != null) users.add(user);
        }
    }

    private void saveUsers() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            lines.add(users.get(i).toCSV());
        }
        FileHandler.saveListToCSV(FILE_NAME, lines);
    }

    public boolean register(String username, String password, String role) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equalsIgnoreCase(username)) return false;
        }
        int newId = users.size() > 0 ? users.get(users.size() - 1).getId() + 1 : 1;
        User newUser = new User(newId, username, password, role, 0, 0); // initial loginCount is 0
        users.add(newUser);
        saveUsers();
        return true;
    }

    public User login(String username, String password, String requiredRole) {
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(password)) {
                if (requiredRole == null || u.getRole().equals(requiredRole)) {
                    this.currentUser = u;
                    return u;
                }
            }
        }
        return null;
    }

    public User getCurrentUser() { return currentUser; }
    public void logout() { this.currentUser = null; }
    
    public void updatePoints(User user, int newPoints) {
        user.setPoints(newPoints);
        saveUsers();
    }

    public void incrementLoginCount(User user) {
        user.setLoginCount(user.getLoginCount() + 1);
        saveUsers();
    }

    public List<User> getAllUsers() {
        return users;
    }
}
