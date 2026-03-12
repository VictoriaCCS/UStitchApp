package ustitchapp_db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtility {

    private static final String DB_URL;
    private static final String USER = "sa";
    private static final String PASS = "";

    static {
        // Get AppData folder for current user
        String appDataPath = System.getenv("APPDATA") + "\\UStitchApp";

        // Make sure the folder exists
        File folder = new File(appDataPath);
        if (!folder.exists()) folder.mkdirs();

        // Set DB_URL to use AppData
        DB_URL = "jdbc:h2:" + appDataPath + "\\ustitchDB";

        // 💥 IMPORTANT: Create test user automatically
        try {
            ensureTestUserExists();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // Ensure test user exists
    public static void ensureTestUserExists() {
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "MERGE INTO Users KEY(email) VALUES(?, ?)"
            );
            ps.setString(1, "testuser@example.com");
            ps.setString(2, "1234");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Increment columns
    public static void incrementUploads(int userId) {
        incrementColumn(userId, "images_uploaded");
    }

    public static void incrementVideosWatched(int userId) {
        incrementColumn(userId, "videos_watched");
    }

    public static void incrementQuizAnswered(int userId) {
        incrementColumn(userId, "quiz_answered");
    }

    public static void incrementPatternsGenerated(int userId) {
        incrementColumn(userId, "patterns_generated");
    }

    private static void incrementColumn(int userId, String column) {
        try (ResultSet rs = UserDataAccess.getUserProgress(userId)) {
            if (rs != null && rs.next()) {
                int current = rs.getInt(column);
                UserDataAccess.updateUserProgress(userId, column, current + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
