package ustitchapp_db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * ProgressService manages the user progress tracking in the application.
 * Ensures that each user starts with empty progress and prevents
 * displaying old user data.
 */
public class ProgressService {

    /**
     * Retrieves the progress of a user by their userId.
     * If the user has no progress yet, creates a new progress row
     * with zeros in the database.
     * 
     * @param userId the ID of the user
     * @return UserProgress object containing the user's progress
     */
    public static UserProgress getProgressByUserId(int userId) {
        UserProgress progress = new UserProgress();

        try (Connection conn = DBUtility.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM UserProgress WHERE user_id = ?")) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // User already has progress: load it
                progress.setUserId(rs.getInt("user_id"));
                progress.setImagesUploaded(rs.getInt("images_uploaded"));
                progress.setVideosWatched(rs.getInt("videos_watched"));
                progress.setQuizAnswered(rs.getInt("quiz_answered"));
                progress.setPatternsGenerated(rs.getInt("patterns_generated"));
            } else {
                // No progress yet: insert a new row with zeros
                try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO UserProgress (user_id, images_uploaded, videos_watched, quiz_answered, patterns_generated) " +
                        "VALUES (?, 0, 0, 0, 0)")) {
                    insertStmt.setInt(1, userId);
                    insertStmt.executeUpdate();
                }

                // Progress object is already initialized with zeros by default
                progress.setUserId(userId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return progress;
    }

} // CLASS
