package ustitchapp_db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDataAccess {
    //ADD DATA------------------------------------------------------------------
    //Insert a new user --------------------------------------------------------
    public static boolean insertUser(String email, String password) {
    String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
    
    try (Connection conn = DBUtility.getConnection();
         // Return the new user's ID (important!)
         PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
        
        pstmt.setString(1, email);
        pstmt.setString(2, password);
        pstmt.executeUpdate();

        // Get new user ID
        ResultSet rs = pstmt.getGeneratedKeys();
        int newUserId = -1;
        if (rs.next()) {
            newUserId = rs.getInt(1);
        }

        System.out.println("User added successfully: " + email + " (ID: " + newUserId + ")");

        // INSERT USER PROGRESS ROW
        String progressSql = """
            INSERT INTO UserProgress 
            (user_id, images_uploaded, videos_watched, quiz_answered, patterns_generated)
            VALUES (?, 0, 0, 0, 0)
        """;

        try (PreparedStatement progressStmt = conn.prepareStatement(progressSql)) {
            progressStmt.setInt(1, newUserId);
            progressStmt.executeUpdate();
            System.out.println("Progress row created for user " + newUserId);
        }

        return true;
        
    } catch (SQLException e) {
        System.out.println("Error inserting user: " + e.getMessage());
        return false;
    }
}

    
    //Get User -----------------------------------------------------------------
    public static Integer getUserId(String email, String password) {
    String sql = "SELECT id FROM users WHERE email = ? AND password = ?";
    try (Connection conn = DBUtility.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, email);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id"); // actual user ID
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null; // user not found
}

    //Validate data-------------------------------------------------------------
    public static Integer validateUser(String email, String password) {
    String sql = "SELECT id FROM users WHERE email = ? AND password = ?";
    
    try (Connection conn = DBUtility.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, email);
        pstmt.setString(2, password);

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int id = rs.getInt("id");
            System.out.println("Login successful! User ID = " + id);

            // 🟡 SAVE SESSION INFO (IMPORTANT!)
            Session.setCurrentUserId(id);
            Session.setCurrentUserEmail(email);

            return id;  // return the real user ID
        } else {
            System.out.println("Invalid login data.");
            return null;
        }
    } catch (SQLException e) {
        System.out.println("Error validating user: " + e.getMessage());
        return null;
    }
}


   
    
    //METHOD HELPER ------------------------------------------------------------
    //Print all the user to check the database is working-----------------------
    public static void printAllUsers() {
        String sql = "SELECT id, email, password FROM users";
        try (Connection conn = DBUtility.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            //String email and password stored
            System.out.println("Current users in DB:");
            while (rs.next()) {
                System.out.println(" - " + rs.getInt("id") + ": " +
                                   rs.getString("email") + " / " +
                                   rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println("Error reading users: " + e.getMessage());
        }
    }
    
    //REMEMBER ME FEATURE-------------------------------------------------------
    private static final String REMEMBER_FILE = "remember.txt";
    //Save the remembered email to a file
    public static void saveRememberedEmail(String email) {
        try (java.io.FileWriter writer = new java.io.FileWriter(REMEMBER_FILE)) {
            writer.write(email);
            System.out.println("Remembered email saved: " + email);
        }catch (Exception e) {
        System.out.println("Error saving remembered email: " + e.getMessage());
        }
    }

    //Load the remembered email from the file-----------------------------------
    public static String getRememberedEmail() {
        try (java.util.Scanner scanner = new java.util.Scanner(new java.io.File(REMEMBER_FILE))) {
            if (scanner.hasNextLine()) {
                String email = scanner.nextLine().trim();
                System.out.println("Remembered email loaded: " + email);
                return email;
            }
        }catch (Exception e) {
            System.out.println("No remembered email found.");
        }
        return "";
    }
    
    //Search for user progress--------------------------------------------------
    public static ResultSet getUserProgress(int userId) {
        try (Connection conn = DBUtility.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
             "SELECT * FROM UserProgress WHERE user_id = ?")) {
             stmt.setInt(1, userId);
             return stmt.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    //Update a single progress field--------------------------------------------
    public static void updateUserProgress(int userId, String column, int newValue) {
        String sql = "UPDATE UserProgress SET " + column + " = ? WHERE user_id = ?";
        try (Connection conn = DBUtility.getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql)) {
              stmt.setInt(1, newValue);
              stmt.setInt(2, userId);
              stmt.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
 public static List<String> getUserPatterns(int userId) {
    List<String> patterns = new ArrayList<>();
    try (Connection conn = DBUtility.getConnection()) {
        String sql = "SELECT filename FROM user_patterns WHERE user_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        var rs = stmt.executeQuery();
        while (rs.next()) {
            patterns.add(rs.getString("filename"));
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    return patterns;
}




}//CLASS

    
