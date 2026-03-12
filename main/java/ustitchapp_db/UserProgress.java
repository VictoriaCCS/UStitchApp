
package ustitchapp_db;

/**
 *
 * @author vcaro
 */
public class UserProgress {
    private int userId;
    private int imagesUploaded;
    private int videosWatched;
    private int quizAnswered;
    private int patternsGenerated;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getImagesUploaded() { return imagesUploaded; }
    public void setImagesUploaded(int imagesUploaded) { this.imagesUploaded = imagesUploaded; }

    public int getVideosWatched() { return videosWatched; }
    public void setVideosWatched(int videosWatched) { this.videosWatched = videosWatched; }

    public int getQuizAnswered() { return quizAnswered; }
    public void setQuizAnswered(int quizAnswered) { this.quizAnswered = quizAnswered; }

    public int getPatternsGenerated() { return patternsGenerated; }
    public void setPatternsGenerated(int patternsGenerated) { this.patternsGenerated = patternsGenerated; }
}

