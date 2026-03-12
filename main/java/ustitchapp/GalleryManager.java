package ustitchapp;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import ustitchapp_db.DBUtility;
import ustitchapp_db.Session;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.scene.layout.StackPane;

public class GalleryManager {

    private Stage galleryStage;
    private FlowPane galleryPane;

    public void openGallery() {
        if (galleryStage == null) {
            galleryStage = new Stage();
            galleryStage.setTitle("Saved Patterns Gallery");

            galleryPane = new FlowPane();
            galleryPane.setHgap(10);
            galleryPane.setVgap(10);
            galleryPane.setPadding(new Insets(10));

            ScrollPane scroll = new ScrollPane(galleryPane);
            scroll.setFitToWidth(true);

            Scene scene = new Scene(scroll, 500, 700);
            galleryStage.setScene(scene);
        }

        // Clear previous images completely
        galleryPane.getChildren().clear();

        try (Connection conn = DBUtility.getConnection()) {
            String sql = "SELECT DISTINCT filename FROM user_patterns WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Session.getCurrentUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String dbPath = rs.getString("filename");
                File file = new File(dbPath);
                if (file.exists()) {
                    Image img = new Image(file.toURI().toString(), 150, 150, true, true);
                    ImageView iv = new ImageView(img);
                    
                    iv.setFitWidth(150);
                    iv.setFitHeight(150);
                    galleryPane.getChildren().add(iv);
                    iv.setOnMouseClicked(e -> {
    Stage popup = new Stage();
    popup.setTitle("Preview");

    Image fullImg = new Image(file.toURI().toString());
    ImageView fullView = new ImageView(fullImg);
    fullView.setPreserveRatio(true);
    fullView.setFitWidth(800); // limit width, change as needed

    StackPane root = new StackPane(fullView);   // <-- wrap ImageView in a Parent
    root.setPadding(new Insets(10));

    Scene popupScene = new Scene(root);         // now compatible
    popup.setScene(popupScene);
    popup.show();
});

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Cannot load gallery.").show();
            return;
        }

        if (!galleryStage.isShowing()) {
            galleryStage.show();
        } else {
            galleryStage.toFront();
        }
    }
}
