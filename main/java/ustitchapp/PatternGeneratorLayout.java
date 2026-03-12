package ustitchapp;

import java.awt.image.RenderedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.types.TFloat32;
import ustitch.tensormodule.TensorUtils;
import ustitchapp_db.DBUtility;
import ustitchapp_db.DatabaseSetUp;
import ustitchapp_db.Session;

public class PatternGeneratorLayout {

    private static Image latestGeneratedPattern;
    private static PatternType lastPatternType = null;
    private String[] currentPatternFile = new String[1]; // random pattern file name
    private static GalleryManager galleryManager = new GalleryManager();

    public static void create(Stage stage, Scene previousScene) {
        //VARIABLES NEEDED TO PATTERN RANDOM GENERATOR--------------------------
        String[] currentPatternFile = new String[1];
        String[] fileName = currentPatternFile;

        //DEFINE LAYOUTS--------------------------------------------------------
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");//CSS class for styling the sidebar

        //Logo View-------------------------------------------------------------
        ImageView logoView = new ImageView();//declare view
        try{
            Image logo2 = new Image(App.class.getResourceAsStream("/images/logo2.png"));//File
            logoView.setImage(logo2);//set view
            logoView.setFitWidth(200);//to fit size
            logoView.setFitHeight(240); //long of the ellipse form
            logoView.setPreserveRatio(false); //to change proportions
            logoView.getStyleClass().add("logo");//CSS
        }catch(Exception ex) {
            System.err.println("Error loading logo image: " + ex.getMessage());
        }//catch errors 

        //Circular clip to border image
        Ellipse clip = new Ellipse(); //define radious
        clip.setRadiusX(100);// half of width
        clip.setRadiusY(120);// half of height
        clip.setCenterX(100);// centerX = half of width
        clip.setCenterY(120);// centerY = half of height
        logoView.setClip(clip);
        logoView.getStyleClass().add("logo-container");//CSS

        //CONTROLS--------------------------------------------------------------
        //Buttons
        Button pattern = new Button("Generate Random Pattern");
        pattern.getStyleClass().add("buttonpattern");//CSS
        //------------------------------
        Button patternai = new Button("Generate AI Pattern");
        patternai.getStyleClass().add("buttonpattern");//CSS
        //-----------------------------
        Button buttonback = new Button("LOG OUT");
        buttonback.getStyleClass().add("buttonback");
        //------------------------------
        Button buttonlearning = new Button("Go to Learning Section");
        buttonlearning.getStyleClass().add("buttonlearning");
        //------------------------------
        Button buttondifficult = new Button("Analyze Difficulty Level");
        buttondifficult.getStyleClass().add("buttondifficult");
        //------------------------------
        Button cleanbrush = new Button("Clean Brush");
        cleanbrush.getStyleClass().add("cleanbrush");
        //------------------------------
        Button saveButton = new Button("Save Pattern Image");
        saveButton.getStyleClass().add("savebutton");
        //-----------------------------
        Button uploadContentButton = new Button("Upload Content Image");
        uploadContentButton.getStyleClass().add("savebutton");
        //-----------------------------


        //Textfield1
        Text textpattern = new Text("Welcome to the Pattern Section!"
                + "\nGenerate your own pattern using the AI button!"
                + "\nChoose one of our random patterns!"
        );
        textpattern.getStyleClass().add("textpattern");//CSS

        //Textfield2
        Text textcolor = new Text("Pick a color for your fabric pattern!"
                + "\nPick some brush colors to simulate your threads."
                + "\nVisualize your color design!");
        textcolor.getStyleClass().add("textcolor");//CSS
        
        Text textai = new Text("AI Patterns are more abstract and advance level."
                + "\nUse the upload content button to add more images."
                + "\nIncrease the variety of the AI content!");
        textai.getStyleClass().add("textcolor");

        //Lines
        Separator line2 = new Separator();// add a line
        line2.getStyleClass().add("line2");//CSS
        Separator line3 = new Separator();// add a line
        line3.getStyleClass().add("line2");//CSS
        Separator line4 = new Separator();// add a line
        line4.getStyleClass().add("line2");//CSS

        //Label
        Label difficultyLabel = new Label();
        difficultyLabel.setText("");
        difficultyLabel.getStyleClass().add("difficult-text");

        //PATTERN GENERATOR (SIMPLE VERSION)------------------------------------
        //Images----------------------------------------------------------------
        List<Image> patternlist = new ArrayList<>();
        try {
            for (int i = 1; i <= 10; i++) {
                Image img = new Image(PatternGeneratorLayout.class.getResourceAsStream("/images/pattern" + i + ".png"));
                patternlist.add(img);
            }
        } catch (Exception ex) {
            System.err.println("Error loading pattern" +  ".png: " + ex.getMessage());
        }//catch errors
        ImageView randompatt = new ImageView();

        //Mapping images with their respective difficult level and suggestions
        Map<String, String> patternDifficulties = new HashMap<>();
        patternDifficulties.put("pattern1.png", "Intermediate (Stitch suggested: Stem or Chain)");
        patternDifficulties.put("pattern2.png", "Intermediate (Stitch suggested: Stem or Split line)");
        patternDifficulties.put("pattern3.png", "Intermediate (Stitch suggested: Stem and Satin)");
        patternDifficulties.put("pattern4.png", "Intermediate (Stitch suggested: Stem and Satin)");
        patternDifficulties.put("pattern5.png", "Beginner (Stitch suggested: Chain)");
        patternDifficulties.put("pattern6.png", "Intermediate (Stitch suggested: Chain and Split line)");
        patternDifficulties.put("pattern7.png", "Intermediate (Stitch suggested: Open fly and Split line)");
        patternDifficulties.put("pattern8.png", "Advanced (Stitch suggested: French Knot, Satin and Split line)");
        patternDifficulties.put("pattern9.png", "Intermediate (Sitch suggested: Chain and Satin)");
        patternDifficulties.put("pattern10.png", "Beginner (Stitch suggested: Chain)");

        //COLOR PALLETE SUGGESTION FEATURE--------------------------------------
        //Canvas Color Fabric suggestion
        Canvas overlayCanvas = new Canvas(randompatt.getFitWidth(), randompatt.getFitHeight());
        GraphicsContext gc = overlayCanvas.getGraphicsContext2D();
        overlayCanvas.widthProperty().bind(randompatt.fitWidthProperty());//fit to pattern
        overlayCanvas.heightProperty().bind(randompatt.fitHeightProperty());//fit to pattern

        //Color settings for brush and canvas
        Color[] selected = new Color[]{Color.TRANSPARENT};//canvas overlay
        Color[] brushColor = new Color[]{Color.RED};//brush
        Rectangle overlay = new Rectangle();//fit to pattern form
        overlay.widthProperty().bind(randompatt.fitWidthProperty());//fit to pattern
        overlay.heightProperty().bind(randompatt.fitHeightProperty());//foit to pattern
        overlay.setFill(Color.TRANSPARENT);//color effect

        //Overlay canvas effect settings container
        StackPane imageOverlay = new StackPane(randompatt, overlay, overlayCanvas);
        imageOverlay.setPrefSize(300, 620);
        imageOverlay.setMaxSize(700, 620);//size
        imageOverlay.getStyleClass().add("pattern-image");//fit to images 
        StackPane.setMargin(randompatt, new Insets(0));

        //JavaFX Color picker for canvas
        ColorPicker colorpicker = new ColorPicker();
        colorpicker.getStyleClass().add("colorpicker");//CSS
        colorpicker.setOnAction(e -> {
            selected[0] = colorpicker.getValue().deriveColor(0, 1, 1, 0.7);
            overlay.setFill(selected[0]);//only overlay
        });

        //JavaFX Color picker for brush
        ColorPicker brushPicker = new ColorPicker();
        brushPicker.getStyleClass().add("brushpicker");//CSS
        brushPicker.setOnAction(e -> {
            brushColor[0] = brushPicker.getValue().deriveColor(0, 1, 1, 0.1);
        });

        //Drawing on canvas
        overlayCanvas.setOnMouseDragged(e -> {
            gc.setFill(brushColor[0]); // only brush
            double brushSize = 10;
            gc.fillOval(e.getX() - brushSize/2, e.getY() - brushSize/2, brushSize, brushSize);
        });

        //clean brush action button
        cleanbrush.setOnAction(e->{
            gc.clearRect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());
        });

        
        // Prepare content and style images for AI patterns
List<Image> contentImages = new ArrayList<>();
List<Image> styleImages = new ArrayList<>();


// Load content images (6 images)
for (int i = 1; i <= 10; i++) {
    contentImages.add(new Image(PatternGeneratorLayout.class.getResourceAsStream("/styles/content" + i + ".jpg")));
    styleImages.add(new Image(PatternGeneratorLayout.class.getResourceAsStream("/styles/style" + i + ".jpg")));
}


        
        ComboBox<String> aiDifficultyPicker = new ComboBox<>();
aiDifficultyPicker.getItems().addAll("Beginner", "Intermediate", "Advanced");
aiDifficultyPicker.setValue("Intermediate"); // default
aiDifficultyPicker.getStyleClass().add("difficulty-picker");


        //BUTTON ACTIONS--------------------------------------------------------
        //Pattern Simple to generate random images
        pattern.setOnAction(e -> {
            Random random = new Random();
            int index = random.nextInt(patternlist.size());
            Image selectedImage = patternlist.get(index);

            latestGeneratedPattern = selectedImage;
            randompatt.setImage(selectedImage);
            randompatt.setFitWidth(500);
            randompatt.setFitHeight(620);
            randompatt.setPreserveRatio(true);
            randompatt.setSmooth(true); 
            overlay.setFill(selected[0]);
            lastPatternType = PatternType.RANDOM;

            gc.clearRect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());

            currentPatternFile[0] = "pattern" + (index + 1) + ".png"; 
        });

        //Button Difficult level
        buttondifficult.setOnAction(e->{
            if (lastPatternType == PatternType.RANDOM && currentPatternFile[0] != null) {
        String difficultyInfo = patternDifficulties.get(currentPatternFile[0]);
        difficultyLabel.setText(difficultyInfo != null ? difficultyInfo : "Difficulty not available.");
    } 
    else if (lastPatternType == PatternType.AI && latestGeneratedPattern != null) {
        String level = analyzeAIPatternDifficulty(latestGeneratedPattern);
        difficultyLabel.setText(level + " (AI pattern)");
    } 
    else {
        difficultyLabel.setText("No pattern selected.");
    }
});
            

        //Button Logout
        buttonback.setOnAction(e-> {
            if (previousScene != null) {
                stage.setScene(App.loginScene);
            } else {
                System.err.println("Previous scene is null!");
            }
        });

        //ButtonLearning
        buttonlearning.setOnAction(e ->{
            LearningLayout.create(stage, previousScene);//<--CALL TO THE LEARNING CLASS
        });

        
        //SAVE BUTTON-----------------------------------------------------------
        //save------------------------------
saveButton.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent e) {
        // Vars must be declared ONCE here:
        String fileName;
        Image imageToSave;

        // Step 1 — No pattern generated?
        if (lastPatternType == null) {
            new Alert(Alert.AlertType.WARNING, "No pattern has been generated yet.").show();
            return;
        }

        try (Connection conn = DBUtility.getConnection()) {

            // --------------------------
            // RANDOM PATTERN SAVE
            // --------------------------
            if (lastPatternType == PatternType.RANDOM) {

                if (currentPatternFile[0] == null) {
                    new Alert(Alert.AlertType.ERROR, "Random pattern file missing.").show();
                    return;
                }

                fileName = currentPatternFile[0];
                imageToSave = imageOverlay.snapshot(null, null);
            }

            // --------------------------
            // AI PATTERN SAVE
            // --------------------------
            else { // PatternType.AI

                if (latestGeneratedPattern == null) {
                    new Alert(Alert.AlertType.ERROR, "AI pattern missing.").show();
                    return;
                }

                fileName = "ai_pattern_" + System.currentTimeMillis() + ".png";
                imageToSave = latestGeneratedPattern;
            }

            // Step 3 — Save physically in a user-writable folder
            String userHome = System.getProperty("user.home"); // user's home folder
            File saveFolder = new File(userHome, "Documents/UStitchApp/patterns");
            if (!saveFolder.exists()) saveFolder.mkdirs(); // create folder if missing

            File patternFile = new File(saveFolder, fileName); // full path
            saveImageToFile(imageToSave, patternFile.getAbsolutePath());

            // Step 4 — Save in DB
            String sql = "INSERT INTO user_patterns (user_id, filename, saved_at) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Session.getCurrentUserId());
            stmt.setString(2, patternFile.getAbsolutePath()); // absolute path
            stmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
            stmt.close();

            // Step 5 — Update progress
            DatabaseSetUp.updateProgress("patterns_generated", 1);

            new Alert(Alert.AlertType.INFORMATION, "Pattern saved successfully!").show();

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error while saving pattern.").show();
        }
    }
});

        

        // Inside create() method where buttons are defined:
Button openGalleryButton = new Button("Open Gallery");
openGalleryButton.getStyleClass().add("buttonpattern");
openGalleryButton.setOnAction(e ->{ 
cleanBlankPatterns(); // remove blank/test AI patterns first
    galleryManager.openGallery();
});
         


javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
fileChooser.getExtensionFilters().addAll(
    new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
);

// Upload Content Image
uploadContentButton.setOnAction(e -> {
    File file = fileChooser.showOpenDialog(stage);
    if (file != null) {
        Image img = new Image(file.toURI().toString());
        contentImages.add(img);
        new Alert(Alert.AlertType.INFORMATION, "Content image added!").show();
    }
});






        //AI PATTERN NIGHTMARE--------------------------------------------------
        // --- AI Pattern Button ---
patternai.setOnAction(e -> {
    // Disable button while generating
    patternai.setDisable(true);

    new Thread(() -> {
        try {
            Random random = new Random();

            // --- Pick random content and style images ---
            Image contentImage = contentImages.get(random.nextInt(contentImages.size()));
            Image styleImage = styleImages.get(random.nextInt(styleImages.size()));


            // --- Convert images to tensors ---
            try (TFloat32 contentTensor = TensorUtils.imageToTensor(contentImage);
                 TFloat32 styleTensor = TensorUtils.imageToTensor(styleImage)) {

                // --- Load model & run session ---
                // Get current working directory (where the app runs)
                File baseDir = new File(System.getProperty("user.dir"));

// Path #1: MSI layout → app/resources/ai_tf2
File modelPath = new File(baseDir, "app/resources/ai_tf2");

// Path #2: NetBeans layout → resources/ai_tf2
if (!modelPath.exists()) {
    modelPath = new File(baseDir, "resources/ai_tf2");
}
try (SavedModelBundle model = SavedModelBundle.load(modelPath.getAbsolutePath(), "serve");
     org.tensorflow.Session session = model.session()) {


                    try (TFloat32 outputTensor = (TFloat32) session.runner()
                            .feed("serving_default_placeholder", contentTensor)
                            .feed("serving_default_placeholder_1", styleTensor)
                            .fetch("StatefulPartitionedCall")
                            .run().get(0)) {

                        // --- Convert output tensor to Image ---
                        Image aiPattern = TensorUtils.tensorToImage(outputTensor);

                        // --- Update UI ---
                        javafx.application.Platform.runLater(() -> {
                            latestGeneratedPattern = aiPattern;

                            // Show AI pattern
                            randompatt.setImage(aiPattern);
                            randompatt.setFitWidth(500);
                            randompatt.setFitHeight(620);
                            randompatt.setPreserveRatio(true);

                            // Clear overlay
                            gc.clearRect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());
                            overlay.setFill(Color.TRANSPARENT);

                            // Mark type
                            lastPatternType = PatternType.AI;

                            // Give unique filename
                            currentPatternFile[0] = "ai_pattern_" + System.currentTimeMillis() + ".png";
                        });
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            javafx.application.Platform.runLater(() ->
                new Alert(Alert.AlertType.ERROR, "Error generating AI pattern: " + ex.getMessage()).show()
            );
        } finally {
            // Re-enable button
            javafx.application.Platform.runLater(() -> patternai.setDisable(false));
        }
    }).start();
});



        //MAIN LAYOUTS----------------------------------------------------------
        StackPane imageContainer = imageOverlay;
        imageContainer.setPrefSize(500, 640);
        imageContainer.setMaxSize(700, 640);
        imageContainer.getStyleClass().add("pattern-image");
        StackPane.setMargin(randompatt, new Insets(0));
        HBox patternbtns = new HBox();
        patternbtns.getChildren().addAll(saveButton, uploadContentButton);
        VBox mainpattern = new VBox(imageContainer);
        mainpattern.getStyleClass().add("mainpattern");
        mainpattern.setPadding(new Insets(40));
        mainpattern.setAlignment(Pos.CENTER);
        mainpattern.getChildren().addAll(buttondifficult, difficultyLabel, patternbtns);
        HBox colors = new HBox();
        colors.getChildren().addAll(colorpicker, brushPicker);
        
        
        //Organize elements: top, right, button, left
        VBox.setMargin(buttondifficult, new Insets(5, 20, 0, 50));
        VBox.setMargin(difficultyLabel, new Insets(5, 20, 0, 50));
        VBox.setVgrow(imageContainer, Priority.NEVER);
        VBox.setMargin(buttonback, new Insets(0, 20, 0, 190));
        VBox.setMargin(pattern, new Insets(5, 20, 10, 100));
        VBox.setMargin(patternai, new Insets(0, 20, 5, 100));
        VBox.setMargin(buttonlearning, new Insets(3, 20, 3, 150));
        VBox.setMargin(logoView, new Insets(5, 20, 10, 130));
        VBox.setMargin(line2, new Insets(0, 10, 0, 10));
        VBox.setMargin(line3, new Insets(0, 10, 0, 10));
        VBox.setMargin(line4, new Insets(0, 10, 0, 10));
        VBox.setMargin(textpattern, new Insets(10, 20, 5, 50));
        VBox.setMargin(cleanbrush, new Insets(3, 20, 3, 175));
        HBox.setMargin(brushPicker, new Insets(0, 20, 3, 5));
        HBox.setMargin(colorpicker, new Insets(0, 20, 3, 75));
        VBox.setMargin(textcolor, new Insets(10, 80, 5, 50));
        VBox.setMargin(openGalleryButton, new Insets(5, 20, 3, 100));
        VBox.setMargin(aiDifficultyPicker, new Insets(3, 100, 5, 127));
        VBox.setMargin(textai, new Insets(3, 20, 5, 50));
        HBox.setMargin(uploadContentButton, new Insets (2,100,5,10));
        HBox.setMargin(saveButton, new Insets(3, 50, 3, 240));

        BorderPane root = new BorderPane(mainpattern);
        root.setLeft(sidebar);

        sidebar.getChildren().addAll(logoView, textpattern,textai, line2, pattern, 
                patternai, line3, textcolor, colors, cleanbrush,
                line4, openGalleryButton, buttonlearning, buttonback);

        sidebar.getChildren().add(0, buildUserSection());

        Scene scene = new Scene(root, 1450, 790);
        scene.getStylesheets().add(App.class.getResource("/UStitchStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("UStitch");
        stage.show();    
    }

    //HELPER SESSION METHOD---------------------------------------------------------
    private static HBox buildUserSection() {
        HBox userBox = new HBox();
        userBox.setId("userBox");
        userBox.setPadding(new Insets(10));
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.getStyleClass().add("sidebar-userbox");

        Label userIcon = new Label("👤");
        userIcon.getStyleClass().add("usericon");

        String email = Session.getCurrentUserEmail();
        if (email == null) email = "Unknown User";

        Label emailLabel = new Label(email);
        emailLabel.getStyleClass().add("emaillabel");

        Integer id = Session.getCurrentUserId();
        Label idLabel = new Label("ID: " + Session.getCurrentUserId());
        idLabel.getStyleClass().add("idlabel");

        userBox.getChildren().addAll(userIcon, emailLabel, idLabel);
        System.out.println("DEBUG: Sidebar Session ID = " + Session.getCurrentUserId());

        return userBox;
    }

    private static void savePatternToFile(StackPane patternPane, String filePath) {
        try {
            File folder = new File("saved_patterns");
            if (!folder.exists()) folder.mkdirs();

            WritableImage snapshot = patternPane.snapshot(null, null);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
            ImageIO.write(renderedImage, "png", new File(filePath));

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error saving pattern PNG!");
        }
    }
    
    

public static void cleanBlankPatterns() {
    try (Connection conn = DBUtility.getConnection()) {
        String sql = "SELECT filename FROM user_patterns WHERE user_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, Session.getCurrentUserId());
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String filePath = rs.getString("filename");
            File file = new File(filePath);
            // check if file exists AND is likely a blank/test AI pattern
            if (file.exists() && file.length() < 5000) { // blank images are tiny in bytes
                file.delete(); // delete the file
                // remove from DB
                PreparedStatement delStmt = conn.prepareStatement(
                    "DELETE FROM user_patterns WHERE filename = ? AND user_id = ?");
                delStmt.setString(1, filePath);
                delStmt.setInt(2, Session.getCurrentUserId());
                delStmt.executeUpdate();
                delStmt.close();
            }
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("Error cleaning blank patterns.");
    }
}


private static void saveImageToFile(Image img, String outputPath) throws Exception {
    File file = new File(outputPath);
    file.getParentFile().mkdirs();

    ImageIO.write(
        SwingFXUtils.fromFXImage(img, null),
        "png",
        file
    );
}

private static String analyzeAIPatternDifficulty(Image aiPattern) {
    if (aiPattern == null) return "Unknown";

    int width = (int) aiPattern.getWidth();
    int height = (int) aiPattern.getHeight();
    javafx.scene.image.PixelReader reader = aiPattern.getPixelReader();

    Map<Color, Integer> colorMap = new HashMap<>();

    for (int x = 0; x < width; x += 10) {
        for (int y = 0; y < height; y += 10) {
            Color c = reader.getColor(x, y);
            colorMap.put(c, colorMap.getOrDefault(c, 0) + 1);
        }
    }

    int colorCount = colorMap.size();

    if (colorCount < 10) return "Beginner";
    else if (colorCount < 30) return "Intermediate";
    else return "Advanced";
}

}//class
