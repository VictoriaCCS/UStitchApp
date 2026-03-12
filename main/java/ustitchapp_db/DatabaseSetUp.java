
package ustitchapp_db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSetUp {

    public static void createTables() throws SQLException {
        //Get Connection with datautility class---------------------------------
        try (Connection conn = DBUtility.getConnection();
             Statement stmt = conn.createStatement()) {

            //TABLES FOR DATA STORE---------------------------------------------
            //Create table if it doesn’t exist----------------------------------
            //User table data to sig in and log in 
            String createUserTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    email VARCHAR(100) NOT NULL UNIQUE,
                    password VARCHAR(100) NOT NULL
                );
            """;
            stmt.execute(createUserTable);
            System.out.println("Users table ready!");
            
            
            //User progress table-----------------------------------------------
            String createUserProgress = """
                                        CREATE TABLE IF NOT EXISTS UserProgress (
                                        id INT PRIMARY KEY AUTO_INCREMENT,
                                        user_id INT,
                                        images_uploaded INT DEFAULT 0,
                                        videos_watched INT DEFAULT 0,
                                        quiz_answered INT DEFAULT 0,
                                        patterns_generated INT DEFAULT 0
                                        );
                                         """;
            stmt.execute(createUserProgress);
            System.out.println("UserProgress table ready!");
            
            
            //User patterns table-----------------------------------------------
            String createUserPatterns = """
                                        CREATE TABLE IF NOT EXISTS user_patterns (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                            user_id INT NOT NULL,
                                            filename VARCHAR(255) NOT NULL,
                                            saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            FOREIGN KEY (user_id) REFERENCES users(id)
                                        );
                                        """;
            stmt.execute(createUserPatterns);
            System.out.println("UserPatterns table ready!");
            
            
            //User Gallery table------------------------------------------------
            String createUserGallery = """
                                       CREATE TABLE IF NOT EXISTS user_gallery (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                           user_id INT NOT NULL,
                                           path VARCHAR(255) NOT NULL,
                                           uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           FOREIGN KEY (user_id) REFERENCES users(id));
                                       """;
            stmt.execute(createUserGallery);
            System.out.println("User Gallery Table ready!");

            
            
            
            //Insert test user--------------------------------------------------
            ResultSet rsUser = stmt.executeQuery("SELECT COUNT(*) AS count FROM users WHERE id = 1");
            if (rsUser.next() && rsUser.getInt("count") == 0) {
                stmt.executeUpdate("""
                INSERT INTO users (id, email, password)
                VALUES (1, 'testuser@example.com', '1234');
            """);
            System.out.println("Test user inserted!");
            } else {
                System.out.println("Test user already exists, skipping insert.");
            }

            //Insert profess row------------------------------------------------
            ResultSet rsProgress = stmt.executeQuery("SELECT COUNT(*) AS count FROM UserProgress WHERE user_id = 1");
            if (rsProgress.next() && rsProgress.getInt("count") == 0) {
                stmt.executeUpdate("""
                INSERT INTO UserProgress (user_id, images_uploaded, videos_watched, quiz_answered, patterns_generated)
                VALUES (1, 0, 0, 0, 0);
            """);
            System.out.println("UserProgress test record inserted!");
            } else {
                System.out.println("UserProgress already exists, skipping insert.");
            }

            

            //Quick test console------------------------------------------------
            System.out.println("=== TESTING USER PROGRESS TABLE ===");
            ResultSet rs = stmt.executeQuery("SELECT * FROM UserProgress");
            while (rs.next()) {
                System.out.println("User ID: " + rs.getInt("user_id"));
                System.out.println("Images uploaded: " + rs.getInt("images_uploaded"));
                System.out.println("Videos watched: " + rs.getInt("videos_watched"));
                System.out.println("Quiz answered: " + rs.getInt("quiz_answered"));
                System.out.println("Patterns generated: " + rs.getInt("patterns_generated"));
                System.out.println("---------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Add this method at the bottom of the class
    public static void updateProgress(String column, int increment) {
        String sql = "UPDATE UserProgress SET " + column + " = " + column + " + ? WHERE user_id = ?";
        try (Connection conn = DBUtility.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, increment);
            pstmt.setInt(2, Session.getCurrentUserId()); //assuming user_id = 1
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}//CLASS

