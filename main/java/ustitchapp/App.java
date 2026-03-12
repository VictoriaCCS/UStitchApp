package ustitchapp;

//Imports
import java.sql.Connection;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ustitchapp_db.DatabaseSetUp;
import ustitchapp_db.Session;
import ustitchapp_db.UserDataAccess;
import ustitch.tensormodule.TensorAI;
import ustitchapp_db.DBUtility;


//Class declared
public class App extends Application {
    public static Scene loginScene; 
    //Define stage
    @Override
    public void start(Stage stage) throws SQLException {
        
        //Connect Application with Database SetUp-------------------------------
        DatabaseSetUp.createTables(); 
        // Ensure test user exists in this DB
        try{
        Connection conn = DBUtility.getConnection();
        DBUtility.ensureTestUserExists();
        
        //Logo View
        Image logo = new Image(getClass().getResourceAsStream("/images/logo.jpg"));//File
        ImageView logoView = new ImageView(logo);//declare view
        logoView.setFitWidth(200);//to fit size
        logoView.setFitHeight(280); //long of the ellipse form
        logoView.setPreserveRatio(false); //to change proportions
        logoView.getStyleClass().add("logo");//CSS
        
        
        //Circular clip to border image
        Ellipse clip = new Ellipse(); //define radious
        clip.setRadiusX(100);// half of width
        clip.setRadiusY(140);// half of height
        clip.setCenterX(100);// centerX = half of width
        clip.setCenterY(140);// centerY = half of height
        logoView.setClip(clip);
        
        
        //Textfield 1 and 2
        TextField txt1 = new TextField();
        txt1.getStyleClass().add("txt1");
        txt1.setPromptText("Enter your email");
        /*-------------------------------------------
        TextField txt2 = new TextField();
        txt2.getStyleClass().add("txt2");
        txt2.setPromptText("Enter your password");
        */
        
        PasswordField pass = new PasswordField();
        pass.getStyleClass().add("pass");
        pass.setPromptText("Enter a Password");
        //-------------------------------------------
        
        
        //Buttons Layout
        HBox session = new HBox(10);
        Button log = new Button("Log in");
        Button sign = new Button("Sign in");
        log.getStyleClass().add("log");//CSS
        sign.getStyleClass().add("sign");//CSS
        
        
        //Label with the name of the App
        Label label = new Label(" UStitch"); //declare
        Separator line = new Separator();// add a line
        line.getStyleClass().add("line");
        label.getStyleClass().add("label"); //CSS
        
        
        //Text
        Text text = new Text("Welcome to UStitch Embroidery App");
        text.getStyleClass().add("my-text");
        
        
        //TextField and PasswordField
        //Boolean to show and hide password
        TextField visible = new TextField();
        visible.getStyleClass().add("visible");
        visible.setVisible(false);
        visible.setManaged(false);
        CheckBox showPass = new CheckBox("Show Password");
        showPass.getStyleClass().add("my-pass-text");
        CheckBox rememberMe = new CheckBox("Remember Me");
        rememberMe.getStyleClass().add("my-pass-text");
        
        //To display postition of checkboxes
        HBox checkboxes = new HBox(20); // 20 = space between them
        checkboxes.setAlignment(Pos.CENTER);
        checkboxes.setPadding(new Insets(5, 0, 10, 0));
        checkboxes.getChildren().addAll(showPass, rememberMe);

        //Load remembered email-------------------------------------------------
        String remembered = UserDataAccess.getRememberedEmail();
        if (!remembered.isEmpty()) {
            txt1.setText(remembered);
            rememberMe.setSelected(true);
        }


        
        
        //MAIN SECTION CONTAINERR-----------------------------------------------
        //To contain and display all controls on the screen
        //VBox and StackPane to containt everything
        //VBOX
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(label, line, text, session,txt1, pass, visible, checkboxes, logoView);
        vbox.setAlignment(Pos.CENTER); //center contain
        session.setAlignment(Pos.CENTER);//center contain
        session.getChildren().addAll(log, sign);
        
        //STACK PANE------------------------------------------------------------
        StackPane main = new StackPane(vbox); //center the VBox itself
        main.getStyleClass().add("main");
        Scene scene = new Scene(main, 1450, 790);//define screen size
        stage.setScene(scene);
        stage.show();
        

        //BUTTON ACTIONS--------------------------------------------------------
        //To connect the different stages
        //When user clicks on log in of sign in he will be redirected to the new stage
        //LOG IN ACTION---------------------------------------------------------
        log.setOnAction(e -> { 
    String email = txt1.getText().trim();
    String password = pass.getText().trim();

    //Validation
    if (email.isEmpty() || password.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Login Required");
        alert.setHeaderText(null);
        alert.setContentText("Please enter email and password to log in");
        alert.showAndWait();
        return;
    }

    //Check user in DB (returns user ID or null)
    Integer userId = UserDataAccess.getUserId(email, password);
    UserDataAccess.printAllUsers();

    System.out.println("DEBUG: Logged in userId = " + userId);

    // ❗ REMOVE THIS — you were setting a NULL session
    // Session.setCurrentUserId(userId);

    if (userId != null) {
        // Save logged-in user for the rest of the app
        Session.setCurrentUserId(userId);
        Session.setCurrentUserEmail(email);

        PatternGeneratorLayout.create(stage, loginScene);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login Successful");
        alert.setHeaderText(null);
        alert.setContentText("Welcome back!");
        alert.showAndWait();

        //Remember Me
        if (rememberMe.isSelected()) {
            UserDataAccess.saveRememberedEmail(email);
        } else {
            UserDataAccess.saveRememberedEmail("");
        }

    } else {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);
        alert.setContentText("Invalid email or password");
        alert.showAndWait();
    }
});


        

        //SIGN-IN ACTION -------------------------------------------------------------
        sign.setOnAction(e -> {
            //Variables needed
            String email = txt1.getText().trim();
            String password = pass.getText().trim();

        //Validation
        if (email.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Registration Required");
            alert.setHeaderText(null);
            alert.setContentText("Please enter email and password to register");
            alert.showAndWait();
            return;
        }
        //Connect with dataaccess class to validate data
        boolean created = UserDataAccess.insertUser(email, password);

        if (created) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("Account created! You can now log in.");
            alert.showAndWait();
            txt1.clear();
            pass.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Failed");
            alert.setHeaderText(null);
            alert.setContentText("Could not create account. Email may already exist.");
            alert.showAndWait();
        }});

        
        //CHECKBOX PASSWORD VIEW ACTION-----------------------------------------
        // Keep the fields in sync always
        visible.textProperty().addListener((obs, oldText, newText) -> {
            pass.setText(newText);
        });
        pass.textProperty().addListener((obs, oldText, newText) -> {
            visible.setText(newText);
        });
        showPass.setOnAction(e -> {
            if(showPass.isSelected()){
                visible.setVisible(true);
                visible.setManaged(true);
                pass.setVisible(false);
                pass.setManaged(false);
            }else{
                pass.setVisible(true);
                pass.setManaged(true);
                visible.setVisible(false);
                visible.setManaged(false);
            }});
        
  
        
        //CSS FILE CONNECTION---------------------------------------------------
        scene.getStylesheets().add(getClass().getResource("/UStitchStyle.css").toExternalForm());
        //STAGE SETTINGS--------------------------------------------------------
        stage.setTitle("UStitch");
        stage.setScene(scene);
        loginScene = scene; // <- this stores the login scene for later use

        stage.show();
        
        } catch (SQLException e) {
        e.printStackTrace();
    }
        
    }//Override
    
    
    //MAIN METHOD---------------------------------------------------------------
    //Set launch of the App
    public static void main(String[] args) {
        // Run TensorFlow model
        TensorAI.runModel();
        launch();
    }//MAIN

}//CLASS