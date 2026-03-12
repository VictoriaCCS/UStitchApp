
package ustitchapp;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import ustitchapp_db.DBUtility;
import ustitchapp_db.DatabaseSetUp;
import ustitchapp_db.ProgressService;
import ustitchapp_db.Session;
import ustitchapp_db.UserProgress;

/**
 *
 * @author vcaro
 */
public class LearningLayout {
    private static final Map<File, Image> uploadedImages = new LinkedHashMap<>();

    public static void create( Stage stage, Scene previousScene){
         
        //DEFINE LAYOUTS--------------------------------------------------------
        //Sidebar
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");//CSS class for styling the sidebar
        
        //Main learning contain
        HBox videobox = new HBox(20);
        videobox.getStyleClass().add("videobox");//CSS file
        videobox.setAlignment(Pos.CENTER);
        videobox.setPadding(new Insets(20));
        
        //VBox container for the question section
        VBox questionContainer = new VBox(10);
        questionContainer.setAlignment(Pos.CENTER);
        questionContainer.setPadding(new Insets(20));
        questionContainer.getStyleClass().add("question-section");//CSS

        
        //Logo View-------------------------------------------------------------
        ImageView logoView = new ImageView();//declare view
        try{
        Image logo2 = new Image(App.class.getResourceAsStream("/images/logo3.png"));//File
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
        Button buttonback = new Button("LOG OUT");
        buttonback.getStyleClass().add("buttonback");//CSS
        //-------------------------------------------
        Button buttonbackpattern = new Button("Back to pattern section");
        buttonbackpattern.getStyleClass().add("buttonbackpattern");
        //-------------------------------------------
        Button uploadbutton = new Button("Upload File");
        uploadbutton.getStyleClass().add("uploadbutton");//CSS
        //-------------------------------------------
        Button playBtn = new Button("Play");
        playBtn.getStyleClass().add("play");
        //-------------------------------------------
        Button pauseBtn = new Button("Pause");
        pauseBtn.getStyleClass().add("pause");
        //-------------------------------------------
        Button stopBtn = new Button("Stop");
        stopBtn.getStyleClass().add("stop");
        //-------------------------------------------
        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("submitbutton");
        //-------------------------------------------
        Button progressButton = new Button("My Progress");
        progressButton.setPrefWidth(200);
        progressButton.getStyleClass().add("buttonprogress");
        //-------------------------------------------
        Button showGalleryBtn = new Button("Show my Gallery!");
        showGalleryBtn.setOnAction(e -> showUploadedGallery());
        showGalleryBtn.getStyleClass().add("buttonshow");
        //-------------------------------------------
        
        
        //Textfield1-----------------------------------------------------------
        Text textlearning = new Text("Welcome to the Learning Section!"
                + "\nWatch tutorials! Answer quizes!"
                + "\nUpload your embroidery works!"
                + "\nTrack your progress!"
                + "\nCheck your personal gallery!"
                + "\nand enjoy the learning experience!"
        );
        textlearning.getStyleClass().add("textpattern");//CSS
        
        
       


        
        
        //QUESTIONS QUIZ--------------------------------------------------------
        // QUESTIONS
String[] questions = {
    "Which is the best fabric for embroidery?",
    "Best stitch for fill sections is..",
    "What type of stitch is best for outlining shapes?",
    "Which thread type is commonly used for embroidery?",
    "What is the main purpose of an embroidery hoop?",
    "Which stitch creates a knotted effect on the fabric?",
    "When blending colors, what is a common technique?",
    "Needle type for delicate embroidery fabrics?",
    "What is the purpose of a thimble in embroidery?",
    "Which fabric is most commonly used for beginners?",
    "Which stitch is ideal for creating curved lines?"
};

// OPTIONS
String[][] options = {
    {"Cotton", "Calico", "Silk"},
    {"Chain", "Stem", "Satin"},
    {"Satin", "Back Stitch", "French Knot"},
    {"Wool Yarn", "Embroidery Floss","Metallic Thread"},
    {"To hold the fabric taut" ,"To store completed stitches" ,"To thread the needle"},
    {"Chain Stitch", "French Knot", "Running Stitch"},
    {"Using different sized hoops", "Mixing two or more thread strands","Changing the needle type"},
    {"Tapestry Needle" ,"Sharp Needle", "Crewel Needle"},
    {"To guide thread" ,"To protect your finger" ,"To hold the hoop"},
    {"Linen", "Silk", "Denim"},
    {"Stem Stitch" ,"Cross Stitch" ,"French Knot"}
};

// CORRECT ANSWERS (indices corresponding to the correct option in each array)
int[] correctAnswers = {
    0, // Cotton
    2, // Satin
    1, // Back Stitch
    1, // Embroidery Floss
    0, // To hold the fabric taut
    1, // French Knot
    1, // Mixing two or more thread strands
    1, // Sharp Needle
    1, // To protect your finger
    0, // Linen
    0  // Stem Stitch
};

      
        
        //Lines-----------------------------------------------------------------
        Separator line2 = new Separator();// add a line
        line2.getStyleClass().add("line2");//CSS
        
        
        //File Upload-----------------------------------------------------------
        FileChooser filechooser = new FileChooser();
        ImageView uploadview = new ImageView();
        uploadview.setFitWidth(100);
        uploadview.setPreserveRatio(true);
        
        
        //videolabels-----------------------------------------------------------
        String[] levels = {
            "Start",
            "Chain Stitch",
            "Stem Stitch",
            "Open Fly Stitch",
            "Satin Stitch",
            "Rose Stitch",
            "French Knot Stitch",
            "Finish"
        };

        //Label-----------------------------------------------------------------
        Label videoLabel = new Label(levels[0]);
        videoLabel.getStyleClass().add("videolabel");
        
        
        // List of video file paths
         String[] videoFiles = {
             "videos/Start.MP4",
             "videos/ChainStitch.MP4",
             "videos/StemStitch.MP4",
             "videos/OpenFlyStitch.MP4",
             "videos/SatinStitch.MP4",
             "videos/RoseStitch.MP4",
             "videos/FrenchKnotStitch.MP4",
             "videos/Finish.MP4"
             
         };
         //Volume Controls-------------------------------------------------------
        Slider volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.getStyleClass().add("volume");//CSS
        volumeSlider.setPrefWidth(100);

        final int[] currentIndex = {0};
        final boolean[] hasWatchedCurrentVideo = {false};

        //MediaPlayer array to manage disposal safely
        final MediaPlayer[] mediaPlayerArray = { null };

        //Placeholder ImageView
        ImageView placeholderImageView = new ImageView(new Image(App.class.getResourceAsStream("/images/video_placeholder2.jpg")));
        placeholderImageView.getStyleClass().add("placeholder");
        placeholderImageView.setFitWidth(500);
        placeholderImageView.setFitHeight(300);
        placeholderImageView.setPreserveRatio(true);
        placeholderImageView.setSmooth(true);
        placeholderImageView.setManaged(true);
        placeholderImageView.setVisible(true); //initially visible

        //Create MediaView
        MediaView mediaView = new MediaView();
        mediaView.setFitWidth(500);
        mediaView.setFitHeight(300);
        mediaView.setPreserveRatio(true);
        mediaView.setSmooth(true);
        mediaView.setManaged(true);

        //StackPane to hold MediaView, placeholder, and arrows--------------------
        StackPane videoStack = new StackPane();
        videoStack.setPrefSize(500, 300);
        videoStack.getChildren().addAll(mediaView, placeholderImageView);


        //Layout----------------------------------------------------------------
        HBox controls = new HBox(20, playBtn, pauseBtn, stopBtn, volumeSlider);
        controls.setAlignment(Pos.CENTER);

        //Arrow images----------------------------------------------------------
        Image leftImg = new Image(LearningLayout.class.getResourceAsStream("/images/greenarrowleft.png"));
        Image rightImg = new Image(LearningLayout.class.getResourceAsStream("/images/greenarrowright.png"));
        ImageView prevArrow = new ImageView(leftImg);
        ImageView nextArrow = new ImageView(rightImg);
        prevArrow.setFitWidth(100);
        prevArrow.setFitHeight(100);
        nextArrow.setFitWidth(100);
        nextArrow.setFitHeight(100);
        prevArrow.getStyleClass().add("arrow");
        nextArrow.getStyleClass().add("arrow");
        prevArrow.setPreserveRatio(true);
        nextArrow.setPreserveRatio(true);

        //StackPane with MediaView and arrow controls---------------------------
        videoStack.setPrefSize(500, 300);
        videoStack.getChildren().addAll(prevArrow, nextArrow);
        StackPane.setAlignment(prevArrow, Pos.CENTER_LEFT);
        StackPane.setAlignment(nextArrow, Pos.CENTER_RIGHT);

        //Video Container-------------------------------------------------------
        VBox videoContainer = new VBox(7, videoLabel, videoStack, controls);
        videoContainer.setAlignment(Pos.TOP_CENTER);
        videoContainer.getStyleClass().add("video-container");
        videoContainer.setMaxSize(720, 440);
        videoContainer.setMinSize(520, 440);
        videoContainer.setPrefSize(720, 440);
        
       Runnable loadVideo = () -> {
           
    // Stop and dispose previous MediaPlayer
    if (mediaPlayerArray[0] != null) {
        mediaPlayerArray[0].stop();
        mediaPlayerArray[0].dispose();
        mediaPlayerArray[0] = null;
    }

    // Create new MediaPlayer
    // Use VideoHelper.loadVideo
    mediaPlayerArray[0] = VideoHelper.createMediaPlayer(
        videoFiles[currentIndex[0]],
        volumeSlider,
        null
);
// Assign player to MediaView
mediaView.setMediaPlayer(mediaPlayerArray[0]);
    
    
     // Add listener for when video finishes
    if (mediaPlayerArray[0] != null) {
        mediaPlayerArray[0].setOnEndOfMedia(() -> {
            if (!hasWatchedCurrentVideo[0]) {
                DatabaseSetUp.updateProgress("videos_watched", 1);
                hasWatchedCurrentVideo[0] = true;
                System.out.println("Video finished: progress updated!");
            }
        });

        mediaPlayerArray[0].pause();
        mediaPlayerArray[0].seek(Duration.ZERO);
        placeholderImageView.setVisible(true); // start with placeholder
        videoLabel.setText(levels[currentIndex[0]]);
    } else {
        placeholderImageView.setVisible(true);
        videoLabel.setText("Video missing: " + videoFiles[currentIndex[0]]);
    }
};
    
    
    
   


        //Playback Buttons
        playBtn.setOnAction(e -> {
            if (mediaPlayerArray[0] != null) {
                mediaPlayerArray[0].play();
                placeholderImageView.setVisible(false); // hide placeholder only when Play is pressed
            }});

        pauseBtn.setOnAction(e -> {
            if (mediaPlayerArray[0] != null) {
                mediaPlayerArray[0].pause();
            }});

        stopBtn.setOnAction(e -> {
            if (mediaPlayerArray[0] != null) {
                mediaPlayerArray[0].stop();
                placeholderImageView.setVisible(true); // show placeholder again when stopped
            }});

        //Arrow Actions---------------------------------------------------------
        prevArrow.setOnMouseClicked(e -> {
            currentIndex[0] = (currentIndex[0] - 1 + videoFiles.length) % videoFiles.length;
            loadVideo.run();
        });

        nextArrow.setOnMouseClicked(e -> {
            currentIndex[0] = (currentIndex[0] + 1) % videoFiles.length;
            loadVideo.run();
        });
        //Initial Load
        loadVideo.run();

        
        //QUIZ SECTION----------------------------------------------------------
        //Quiz Container--------------------------------------------------------
        VBox quizContainer = new VBox(10);
        quizContainer.setPadding(new Insets(10));
        quizContainer.setMaxWidth(500);
        quizContainer.setAlignment(Pos.TOP_CENTER); // centers everything horizontally
        // Quiz Container






        
        //StackPane to center it in BorderPane bottom---------------------------
        StackPane bottomWrapper = new StackPane(quizContainer);
        bottomWrapper.setAlignment(Pos.TOP_CENTER);
        bottomWrapper.setPadding(new Insets(10));
        bottomWrapper.setMaxWidth(600);

       
        //Array for currentQuestionIndex----------------------------------------
        final int[] currentQuestionIndex = {0};

        
        //Question label--------------------------------------------------------
        Label questionLabel = new Label(questions[currentQuestionIndex[0]]);
        questionLabel.getStyleClass().add("questionlabel");//CSS
        questionLabel.setWrapText(true);   // allow text to go to next line
        questionLabel.setMaxWidth(1000); 
        questionLabel.setAlignment(Pos.CENTER); 
        
        
        //Create radio buttons--------------------------------------------------
        
        
        
        //Feedback label--------------------------------------------------------
Label feedbackLabel = new Label();
feedbackLabel.getStyleClass().add("feedback-label"); // optional CSS

//Create radio buttons--------------------------------------------------
ToggleGroup group = new ToggleGroup();
VBox optionsBox = new VBox(10);
optionsBox.setAlignment(Pos.CENTER);
for (int i = 0; i < options[currentQuestionIndex[0]].length; i++) {
    RadioButton rb = new RadioButton(options[currentQuestionIndex[0]][i]);
    rb.setToggleGroup(group);
    rb.setPrefWidth(200);   

    //for lambda
    final int index = i; 
    rb.setOnAction(e -> {
        System.out.println("Option selected: " + index);
    });
    optionsBox.getChildren().add(rb);
}

        //Submit button action--------------------------------------------------
        final int[] attempts = {0};
        Label attemptsLabel = new Label("Attempts: 0");
        attemptsLabel.getStyleClass().add("attempts-label");


        submitButton.setOnAction(e -> {
    RadioButton selected = (RadioButton) group.getSelectedToggle();
    if (selected != null) {
        attempts[0]++; 
        int selectedIndex = optionsBox.getChildren().indexOf(selected);

        // Feedback
        if (selectedIndex == correctAnswers[currentQuestionIndex[0]]) {
            feedbackLabel.setText("✅ Correct!");
            DatabaseSetUp.updateProgress("quiz_answered", 1);
            feedbackLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            feedbackLabel.setText("❌ Wrong! Correct answer: " 
                + options[currentQuestionIndex[0]][correctAnswers[currentQuestionIndex[0]]]);
            feedbackLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
        // Update attempts label
        attemptsLabel.setText("Attempts: " + attempts[0]);
        // Move to next question after short delay
        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
        pause.setOnFinished(ev -> {
            currentQuestionIndex[0]++;
            if (currentQuestionIndex[0] < questions.length) {
                questionLabel.setText(questions[currentQuestionIndex[0]]);
                for (int i = 0; i < optionsBox.getChildren().size(); i++) {
                    ((RadioButton) optionsBox.getChildren().get(i))
                        .setText(options[currentQuestionIndex[0]][i]);
                }
                group.selectToggle(null); // reset selection
                feedbackLabel.setText(""); // clear previous feedback
                attempts[0] = 0; // reset attempts for new question
            attemptsLabel.setText("Attempts: 0");
            } else {
                questionLabel.setText("🎉 Quiz Finished!");
                optionsBox.getChildren().clear();
                submitButton.setDisable(true);
                feedbackLabel.setText("");
            }
        });
        pause.play();
    }
});
        //Add to container
        quizContainer.getChildren().addAll(questionLabel, optionsBox, submitButton, feedbackLabel, attemptsLabel);
        questionLabel.getStyleClass().add("questionlabel");
        optionsBox.getStyleClass().add("options");
        
        
        //MAIN CONTAINER--------------------------------------------------------
        //BorderPane to organaize elements 
        BorderPane mainlearning = new BorderPane();
        mainlearning.setLeft(sidebar);//Sidebar on the left
        mainlearning.setCenter(videoContainer);
        BorderPane.setAlignment(videoContainer, Pos.TOP_CENTER);
        videoContainer.setTranslateY(20);
        VBox centerContainer = new VBox(10); // spacing between video and quiz
        centerContainer.getChildren().addAll(videoContainer, quizContainer);
        centerContainer.setAlignment(Pos.TOP_CENTER);
        mainlearning.setCenter(centerContainer);
        mainlearning.getStyleClass().add("mainlearning");//CSS
quizContainer.setMaxWidth(Double.MAX_VALUE);
quizContainer.setAlignment(Pos.TOP_CENTER);


        //SCENE FEATURES--------------------------------------------------------
        Scene scene = new Scene(mainlearning, 1450, 790);
        
        
        //BUTTON ACTIONS--------------------------------------------------------
        //Buttonlogout
        buttonback.setOnAction(e -> {
            stage.setScene(App.loginScene);
        });
        
        
        //buttonbackscene-------------------------------------------------------
        buttonbackpattern.setOnAction(e-> {
            if (previousScene != null) {
            PatternGeneratorLayout.create(stage, scene); 
             } else {
        System.err.println("Previous scene is null!");
        }
        });

        
        //buttonprogress--------------------------------------------------------
        progressButton.setOnAction(e -> {
            VBox progressContainer = showProgressSection(stage, scene, centerContainer);
            BorderPane mainLayout = (BorderPane) stage.getScene().getRoot();
            mainLayout.setCenter(progressContainer);
        });
        
        HBox gallerybtns = new HBox();
        gallerybtns.getChildren().addAll(showGalleryBtn);
        
        
        // --- LOAD EXISTING IMAGES FROM DATABASE ---
try (Connection conn = DBUtility.getConnection();
     PreparedStatement stmt = conn.prepareStatement(
             "SELECT path FROM user_gallery WHERE user_id = ?")) {
    stmt.setInt(1, Session.getCurrentUserId());
    ResultSet rs = stmt.executeQuery();
    while (rs.next()) {
        String path = rs.getString("path");
        File file = new File(path); // assuming path stored is absolute
        if(file.exists()){
            Image img = new Image(file.toURI().toString());
            uploadedImages.put(file, img);
            uploadview.setImage(img); // show last image
        }
    }
} catch (SQLException ex) {
    ex.printStackTrace();
}

        
        
        
        
        
        
        
        
      // --- UPLOAD BUTTON ACTION ---
uploadbutton.setOnAction(e -> {
    File file = filechooser.showOpenDialog(null);
    if (file != null) {
        try {
            Image img = new Image(file.toURI().toString());
            
            uploadedImages.put(file, img);
            uploadview.setImage(img); // display last uploaded image

            // --- INSERT INTO DATABASE ---
            try (Connection conn = DBUtility.getConnection()) {
                String sql = "INSERT INTO user_gallery (user_id, path) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Session.getCurrentUserId());
                stmt.setString(2, file.getAbsolutePath());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // --- UPDATE PROGRESS ---
            DatabaseSetUp.updateProgress("images_uploaded", 1);

            System.out.println("Image uploaded and progress updated!");

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load image.");
            alert.show();
            ex.printStackTrace();
        }
    }
});

// --- SHOW GALLERY BUTTON ---
showGalleryBtn.setOnAction(e -> {
    Stage galleryStage = new Stage();
    FlowPane galleryBox = new FlowPane();
galleryBox.setHgap(10);
galleryBox.setVgap(10);
galleryBox.setPadding(new Insets(10));

    galleryBox.setAlignment(Pos.CENTER_LEFT);
    galleryBox.setPadding(new Insets(20));

    for (Image img : uploadedImages.values()) {
        ImageView iv = new ImageView(img);
        iv.setFitWidth(200);
        iv.setPreserveRatio(true);
        iv.setOnMouseClicked(ev -> {
            Stage popup = new Stage();
            ImageView fullView = new ImageView(img);
            fullView.setPreserveRatio(true);
            fullView.setFitWidth(500); // full size display
            fullView.setFitHeight(600);
            StackPane pane = new StackPane(fullView);
            Scene scene1 = new Scene(pane, 500, 600);
            popup.setScene(scene1);
            popup.setTitle("Full Size Image");
            popup.show();
        });
        galleryBox.getChildren().add(iv);
    }

    ScrollPane scrollPane = new ScrollPane(galleryBox);
    scrollPane.setFitToWidth(true);
    Scene scene1 = new Scene(scrollPane, 500, 600);
    galleryStage.setScene(scene1);
    galleryStage.setTitle("Your Gallery");
    galleryStage.show();
});

Separator line4 = new Separator();// add a line
        line4.getStyleClass().add("line2");//CSS
        //ADD ELEMENTS TO THE SIDEBAR-------------------------------------------
        sidebar.getChildren().addAll(logoView, textlearning, line2, uploadbutton, 
                uploadview,line4, gallerybtns, progressButton, buttonbackpattern, buttonback);
        
        
        sidebar.getChildren().add(0, buildUserSection());
        
        //ORGANIZE ELEMENTS-----------------------------------------------------
        //Organize elements: top, right, button, left
        VBox.setMargin(buttonback, new Insets(0, 20, 0, 190));
        VBox.setMargin(textlearning, new Insets (10, 20, 5, 100));
        VBox.setMargin(line2, new Insets (0, 10, 3, 10));
        VBox.setMargin(line4, new Insets(0, 10, 3, 10));
        VBox.setMargin(buttonbackpattern, new Insets(5, 20, 3, 150));
        VBox.setMargin(logoView, new Insets(5, 20, 10, 130));
        VBox.setMargin(uploadbutton, new Insets(5,20,0,100));
        VBox.setMargin(uploadview, new Insets(5,20,10,130));
        StackPane.setMargin(prevArrow, new Insets(0, 0, 0, 10));
        StackPane.setMargin(nextArrow, new Insets(0, 10, 0, 0));
        VBox.setMargin(videoLabel, new  Insets (20,20,10,10));
        HBox.setMargin(playBtn, new Insets (10,0,0,0));
        HBox.setMargin(pauseBtn, new Insets (10,0,0,0));
        HBox.setMargin(stopBtn, new Insets (10,0,0,0));
        HBox.setMargin(volumeSlider, new Insets (10,0,0,0));
        VBox.setMargin(progressButton, new  Insets (10,20,10,100));
        VBox.setMargin(quizContainer, new Insets(50, 0, 10, 0));
        VBox.setMargin(submitButton, new Insets (5,50,3,40));
        VBox.setMargin(feedbackLabel, new Insets (5,50,3,40));
        VBox.setMargin(attemptsLabel, new Insets (0,50,10,40));
        VBox.setMargin(gallerybtns, new Insets(5, 100, 0, 100) );
        VBox.setMargin(uploadview, new Insets(5, 100, 5, 180));
        
        //CSS FILE CONNECTION---------------------------------------------------
        scene.getStylesheets().add(App.class.getResource("/UStitchStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.centerOnScreen();

        stage.setTitle("UStitch");
        stage.show();  
    }
    
    
    //HELPER MEHTOD FOR LEARNING SECTION----------------------------------------
    //VBox to display progess---------------------------------------------------
    private static VBox showProgressSection(Stage stage, Scene learningScene, VBox centerContainer) {
    // Create a new VBox container each time
    VBox progressContainer = new VBox(20);
    progressContainer.setAlignment(Pos.CENTER);

    // Default values
    int images = 0;
    int videos = 0;
    int quizzes = 0;
    int patterns = 0;

    try {
        // Get current user ID from session
        int currentUserId = Session.getCurrentUserId();

        // Retrieve progress using ProgressService (fixed)
        UserProgress progress = ProgressService.getProgressByUserId(currentUserId);

        images = progress.getImagesUploaded();
        videos = progress.getVideosWatched();
        quizzes = progress.getQuizAnswered();
        patterns = progress.getPatternsGenerated();

    } catch (Exception e) {
        e.printStackTrace();
    }

    // Determine level
    String level = "Beginner";
    int total = images + videos + quizzes + patterns;
    if (total >= 10 && total < 20) level = "Intermediate";
    else if (total >= 20) level = "Expert";

    Label levelLabel = new Label("Level: " + level);

    // PieChart (original layout & size kept)
    PieChart pieChart = new PieChart(FXCollections.observableArrayList(
            new PieChart.Data("Images Uploaded", images),
            new PieChart.Data("Videos Watched", videos),
            new PieChart.Data("Quizzes Answered", quizzes),
            new PieChart.Data("Patterns Generated", patterns)
    ));
    pieChart.setTitle("Your Progress");
    pieChart.setLabelsVisible(true);
    pieChart.getStyleClass().add("pieChart"); // CSS styling

    // Show total numbers as text below the chart
    Label numbersLabel = new Label(
            "Images: " + images + " | Videos: " + videos + 
            " | Quizzes: " + quizzes + " | Patterns: " + patterns
    );
    numbersLabel.getStyleClass().add("numbersLabel");

    // Back button to return to learning section
    Button backToLearningBtn = new Button("Back to Learning");
    backToLearningBtn.getStyleClass().add("backlearning");
    backToLearningBtn.setOnAction(e -> {
        BorderPane mainLayout = (BorderPane) stage.getScene().getRoot();
        mainLayout.setCenter(centerContainer); // restore original view
    });

    // Add all nodes to VBox
    progressContainer.getChildren().addAll(levelLabel, pieChart, numbersLabel, backToLearningBtn);

    return progressContainer;
}

    
    //MEDIA PLAYER HELPER METHOD------------------------------------------------
    private static MediaPlayer createMediaPlayer(String relativePath, Slider volumeSlider, Runnable onEnd) {
    try {
        // 1. Determine base folder of the installed application
        File installDir = new File(System.getProperty("user.dir"));

        // 2. Try the jpackage folder structure: app/resources/videos
        File videoPath = new File(installDir, "app/resources/" + relativePath);

        System.out.println("Trying: " + videoPath.getAbsolutePath());

        // 3. If that fails, try fallback: resources/videos (for IDE run)
        if (!videoPath.exists()) {
            videoPath = new File(installDir, "resources/" + relativePath);
            System.out.println("Fallback: " + videoPath.getAbsolutePath());
        }

        if (!videoPath.exists()) {
            System.err.println("❌ Video NOT FOUND anywhere: " + relativePath);
            return null;
        }

        Media media = new Media(videoPath.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(volumeSlider.getValue());
        player.setAutoPlay(false);

        if (onEnd != null) {
            player.setOnEndOfMedia(onEnd);
        }

        return player;

    } catch (Exception e) {
        System.err.println("❌ Error creating MediaPlayer for " + relativePath);
        e.printStackTrace();
        return null;
    }
}



    
  

 
 //HELPER SESSION METHOD--------------------------------------------------------
 private static HBox buildUserSection() {
    HBox userBox = new HBox();
    userBox.setPadding(new Insets(10));
    userBox.setAlignment(Pos.CENTER_LEFT);
    userBox.getStyleClass().add("sidebar-userbox");

    Label userIcon = new Label("👤");
    userIcon.getStyleClass().add("usericon");

    //Safely get email
    String email = Session.getCurrentUserEmail();
    if (email == null) email = "Unknown User";

    Label emailLabel = new Label(email);
    emailLabel.getStyleClass().add("emaillabel");

    // Safely get ID
    Integer id = Session.getCurrentUserId();
    Label idLabel = new Label("ID: " + Session.getCurrentUserId());
    idLabel.getStyleClass().add("idlabel");

    userBox.getChildren().addAll(userIcon, emailLabel, idLabel);
    
    return userBox;
    
}

private static void showUploadedGallery() {
    Stage galleryStage = new Stage();
    galleryStage.initModality(Modality.APPLICATION_MODAL);
    galleryStage.setTitle("Your Uploaded Images");

    FlowPane flowPane = new FlowPane();
    flowPane.setHgap(10);
    flowPane.setVgap(10);
    flowPane.setPadding(new Insets(10));
    

    for (Image img : uploadedImages.values()) {
        ImageView iv = new ImageView(img);
        iv.setFitWidth(150);
        iv.setFitHeight(150);
        iv.setPreserveRatio(true);

        iv.setOnMouseClicked(e -> {
            Stage fullStage = new Stage();
            fullStage.initModality(Modality.APPLICATION_MODAL);
            fullStage.setTitle("Full Size Image");

            ImageView fullView = new ImageView(img);
            fullView.setPreserveRatio(true);
            fullView.setFitWidth(800);

            Scene fullScene = new Scene(new StackPane(fullView), 500, 800);
            fullStage.setScene(fullScene);
            fullStage.showAndWait();
        });

        flowPane.getChildren().add(iv);
    }

    ScrollPane scrollPane = new ScrollPane(flowPane);
    scrollPane.setFitToWidth(true);

    Scene scene = new Scene(scrollPane, 500, 800);
    galleryStage.setScene(scene);
    galleryStage.showAndWait();
}


}//CLASS

   

