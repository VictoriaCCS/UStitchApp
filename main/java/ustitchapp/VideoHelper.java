package ustitchapp;


import java.io.File;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

// ======================= VIDEO HELPER ==========================
public class VideoHelper {

    // Build absolute path to a video in BOTH cases:
    // 1) Running from NetBeans (resources/videos)
    // 2) Installed MSI (app/resources/videos)
    public static File resolveVideoFile(String relativeVideoPath) {

        // Base folder of the installed app OR project root
        File baseDir = new File(System.getProperty("user.dir"));

        // Path #1: jpackage MSI layout → app/resources/videos
        File videoPath = new File(baseDir, "app/resources/" + relativeVideoPath);

        if (videoPath.exists()) {
            System.out.println("✔ Found video in MSI folder: " + videoPath.getAbsolutePath());
            return videoPath;
        }

        // Path #2: running from NetBeans → resources/videos
        videoPath = new File(baseDir, "resources/" + relativeVideoPath);

        if (videoPath.exists()) {
            System.out.println("✔ Found video in IDE folder: " + videoPath.getAbsolutePath());
            return videoPath;
        }

        // Not found anywhere
        System.err.println("❌ Video not found: " + relativeVideoPath);
        return null;
    }

    // Create MediaPlayer safely
    public static MediaPlayer createMediaPlayer(String relativeVideoPath, Slider volumeSlider, Runnable onEnd) {

        try {
            File videoFile = resolveVideoFile(relativeVideoPath);
            if (videoFile == null) {
                return null;
            }

            Media media = new Media(videoFile.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);

            player.setAutoPlay(false);
            player.setVolume(volumeSlider.getValue());

            if (onEnd != null) {
                player.setOnEndOfMedia(onEnd);
            }

            return player;

        } catch (Exception e) {
            System.err.println("❌ Error creating MediaPlayer for: " + relativeVideoPath);
            e.printStackTrace();
            return null;
        }
    }
}
