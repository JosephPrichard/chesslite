/*
 * JavaFx main class for the ChessLite Application GUI
 * Represents the core structure of the application
 * 6/11/20
 */
package gui;

import fxutil.AudioClipPlayer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joseph
 */
public class ChessLite extends Application {
    
    public static final String TITLE = "ChessLite V1 - Joseph";
    public static final String APP_ICON_PATH = "/resources/blackknight.png"; //icon paths
    public static final String NEW_ICON_PATH = "/resources/boardsparkle.png";
    public static final String SETTINGS_ICON_PATH = "/resources/whitepawn.png";
    public static final String SOUND_CLIP_PATH = ChessLite.class.getResource("/resources/movepiece.wav").toExternalForm();
    public static final ObservableList<String> TIMER_OPTIONS = FXCollections.observableArrayList( //combobox options
            "Casual Untimed", "Classic 30+20", "Rapid 15+10", "Blitz 3+2", "Bullet 1+0");
    public static final int[][] TIMER_INFO = {{30*60,20},{15*60,10},{3*60,2},{60,0}};
    public static final ObservableList<String> PIECES_OPTIONS = FXCollections.observableArrayList(
            "Classic", "Alpha", "Book", "Gothic");
    public static final ObservableList<String> COLORS_OPTIONS = FXCollections.observableArrayList(
            "Brown", "Blue", "Green", "Red");
    public static final String CONFIG_NAME = "ChessLiteConfig"; //file and config info
    public static final String DEFAULT_CONFIG_PATH = "/resources/DefaultConfig.dat";
    public static final String FOLDER = System.getProperty("file.separator") + ".ChessLiteDat";
    public static final String CONFIG_DIR = System.getProperty("user.home") + FOLDER;
    public static final String CONFIG_PATH = System.getProperty("user.home") + 
            FOLDER + System.getProperty("file.separator") + CONFIG_NAME + ".dat";
    public static final String[] AVAILABLE_PATHS = {"classic","alpha","book","gothic"};
    public static final int BROWN = 0;
    public static final int BLUE = 1;
    public static final int GREEN = 2;
    public static final int RED = 3;

    private final double height = GraphicsEnvironment.getLocalGraphicsEnvironment().
            getDefaultScreenDevice().getDisplayMode().getHeight()*0.80;
    private final double width = height*1.36;
    private final double scale = height/960;
    private final AudioClipPlayer clip = new AudioClipPlayer(SOUND_CLIP_PATH); //sound clip
    private String path = "alpha"; //path for piece package
    private int colorTheme = GREEN; //color theme

    public AudioClipPlayer getClip() {
        return clip;
    }

    public String getPath() {
        return path;
    }

    public int getColorTheme() {
        return colorTheme;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getScale() {
        return scale;
    }
    
    /**
     * Writes a string to the configuration file
     * @param str, the string to the written
     */
    public void changeConfigData(String str) {
        writeStringToFile(str, CONFIG_PATH);
    }
    
    /**
     * Returns whether or not a string is contained within an array of strings
     * @param arr, the array to contain
     * @param stringIn, the string to be checked
     * @return whether the string is contained
     */
    public boolean contains(String[] arr, String stringIn) {
        for(String string : arr) {
            if(string.equals(stringIn)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Writes a string to a specified file path
     * @param string, the given string to write
     * @param path, the given file path to write to
     */
    public void writeStringToFile(String string, String path) {
        File file = new File(path);
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file,false))) {
                writer.write(string);
            }
        } catch (IOException ex) {
            Logger.getLogger(ChessLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    /**
     * Copies a file to a another file
     * @param reader, the BufferedReader to be copied to
     * @param file, the reader for the file to be read from
     */
    public void copyToFile(BufferedReader reader, File file) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                StringBuilder contents = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    contents.append(line);
                    contents.append("\n");
                }
                writer.write(contents.toString());
                reader.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ChessLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    /**
     * Object initialization performed upon Application startup
     * Access the application configuration file to find application settings
     * If no file can be found a new one is created with default settings contained in
     * DEFAULT_CONFIG_PATH 
     */
    @Override
    public void init() {
        File dir = new File(CONFIG_DIR);  
        dir.mkdir();
        File file = new File(CONFIG_PATH);  
        try {
            if(file.createNewFile()) {
                BufferedReader defaultConfigReader = new BufferedReader(new InputStreamReader(
                        ChessLite.class.getResourceAsStream(DEFAULT_CONFIG_PATH)));
                copyToFile(defaultConfigReader,file);
            } else {
                if(file.canRead()) {
                    String[] data = new String[2];
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        int i = 0;
                        while ((line = br.readLine()) != null) {
                            data[i] = line;
                            i++;
                        }
                    }
                    String str = data[0];
                    if(contains(AVAILABLE_PATHS,str)) {
                        this.path = str;
                    }
                    int colorNum = (Integer.parseInt(data[1]));
                    if(colorNum <= RED && colorNum >= BROWN) {
                        colorTheme = colorNum;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ChessLite.class.getName()).log(Level.SEVERE, null, ex);
        }
        clip.startLoop();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(APP_ICON_PATH));
        primaryStage.setTitle(TITLE);
        primaryStage.setHeight(height);
        primaryStage.setWidth(width);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            Platform.exit();
            System.exit(0);
        });
        Pane root = createPlayPane(primaryStage);
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(ChessLite.class.getResource("/resources/chess.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Opens the player vs player select window
     * @param stage stage the window belongs to
     */
    public void newGame(Stage stage) {
        BorderPane newRoot = new BorderPane();
        Scene secondScene = new Scene(newRoot, stage.getScene().getWidth()/2.3, stage.getScene().getHeight()/1.5);
        secondScene.getStylesheets().add(ChessLite.class.getResource("/resources/chess.css").toExternalForm());
        Stage newWindow = new Stage();
        
        VBox content = new VBox();
        content.setSpacing(20);
        
        Label timerLabel = new Label("Time Control");
        timerLabel.setFont(new Font("Roboto",22*scale));
        ComboBox timerBox = new ComboBox<>(TIMER_OPTIONS);
        timerBox.setId("combobox");
        double size = 20*scale;
        timerBox.setStyle("-fx-font: " + size + "px \"Roboto\";");
        timerBox.getSelectionModel().select(0);
        content.setAlignment(Pos.CENTER);
        
        content.getChildren().addAll(timerLabel, timerBox);
        
        HBox horizontalButtons = new HBox();
        Button confirmButton = new Button("Start");
        confirmButton.setOnAction((ActionEvent event) -> {
            Stage theStage = (Stage) confirmButton.getScene().getWindow();
            int selected = timerBox.getSelectionModel().getSelectedIndex();
            Pane pane;
            if(selected == 0) {
                pane = createPlayPane(stage);
            } else {
                pane = createPlayPaneTimed(stage, TIMER_INFO[selected-1][0], TIMER_INFO[selected-1][1],selected-1);
            }
            theStage.close();
            stage.getScene().setRoot(pane);
        });
        confirmButton.setId("appbutton");
        confirmButton.setFont(new Font("Roboto",22*scale));
        confirmButton.setFocusTraversable(false);
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction((ActionEvent event) -> {
            Stage theStage = (Stage) cancelButton.getScene().getWindow();
            theStage.close();
        });
        cancelButton.setId("appbutton");
        cancelButton.setFont(new Font("Roboto",22*scale));
        cancelButton.setFocusTraversable(false);
        horizontalButtons.getChildren().addAll(confirmButton, cancelButton);
        horizontalButtons.setSpacing(20);
        horizontalButtons.setAlignment(Pos.CENTER);
        BorderPane.setMargin(horizontalButtons, new Insets(0,0,50,0));
        BorderPane.setMargin(content, new Insets(50,0,50,0));
        newRoot.setBottom(horizontalButtons);
        newRoot.setTop(content);
        
        newWindow.getIcons().add(new Image(NEW_ICON_PATH));
        newWindow.setTitle("New Game");
        newWindow.setScene(secondScene);
        newWindow.setResizable(false);

        newWindow.setX(stage.getX() + stage.getWidth()/3);
        newWindow.setY(stage.getY() + stage.getHeight()/3);
        newWindow.setWidth(stage.getWidth()/3);
        newWindow.setHeight(stage.getHeight()/2.3);

        newWindow.initOwner(stage);
        newWindow.initModality(Modality.APPLICATION_MODAL); 
        newWindow.showAndWait();
    }
    
    /**
     * Opens the player vs player select window
     * @param stage, stage window belongs to
     * @param game, the game to be re-rendered for upon change
     */
    public void openAppearancePanel(Stage stage, Game game) {
        BorderPane newRoot = new BorderPane();
        Scene secondScene = new Scene(newRoot, stage.getScene().getWidth()/2.3, stage.getScene().getHeight()/1.5);
        secondScene.getStylesheets().add(ChessLite.class.getResource("/resources/chess.css").toExternalForm());
        Stage newWindow = new Stage();
        
        VBox content = new VBox();
        content.setSpacing(20);
        
        HBox styles = new HBox();
        styles.setSpacing(20);
        Label styleLabel = new Label("Pieces Style");
        styleLabel.setFont(new Font("Roboto",22*scale));
        ComboBox pieceBox = new ComboBox<>(PIECES_OPTIONS);
        pieceBox.setPrefWidth(150*scale);
        pieceBox.setId("combobox");
        pieceBox.getSelectionModel().select(getPieceSelection(path));
        double size = 20*scale;
        pieceBox.setStyle("-fx-font: " + size + "px \"Roboto\";");
        content.setAlignment(Pos.CENTER);
        styles.setAlignment(Pos.CENTER);
        styles.getChildren().addAll(styleLabel, pieceBox);
        
        HBox colors = new HBox();
        colors.setSpacing(20);
        Label colorLabel = new Label("Board Color");
        colorLabel.setFont(new Font("Roboto",22*scale));
        ComboBox colorBox = new ComboBox<>(COLORS_OPTIONS);
        colorBox.setPrefWidth(130*scale);
        colorBox.setId("combobox");
        colorBox.getSelectionModel().select(colorTheme);
        double size1 = 20*scale;
        colorBox.setStyle("-fx-font: " + size1 + "px \"Roboto\";");
        content.setAlignment(Pos.CENTER);
        colors.setAlignment(Pos.CENTER);
        colors.getChildren().addAll(colorLabel, colorBox);
        
        content.getChildren().addAll(styles,colors);
        
        HBox horizontalButtons = new HBox();
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction((ActionEvent event) -> {
            Stage theStage = (Stage) confirmButton.getScene().getWindow();
            int selected = pieceBox.getSelectionModel().getSelectedIndex();
            path = AVAILABLE_PATHS[selected];
            colorTheme = colorBox.getSelectionModel().getSelectedIndex();
            game.reRenderBoard();
            changeConfigData(path + "\n" + colorTheme);
            theStage.close();
        });
        confirmButton.setId("appbutton");
        confirmButton.setFont(new Font("Roboto",22*scale));
        confirmButton.setFocusTraversable(false);
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction((ActionEvent event) -> {
            Stage theStage = (Stage) cancelButton.getScene().getWindow();
            theStage.close();
        });
        cancelButton.setId("appbutton");
        cancelButton.setFont(new Font("Roboto",22*scale));
        cancelButton.setFocusTraversable(false);
        horizontalButtons.getChildren().addAll(confirmButton, cancelButton);
        horizontalButtons.setSpacing(20);
        horizontalButtons.setAlignment(Pos.CENTER);
        BorderPane.setMargin(horizontalButtons, new Insets(0,0,50,0));
        BorderPane.setMargin(content, new Insets(50,0,50,0));
        newRoot.setBottom(horizontalButtons);
        newRoot.setTop(content);
        
        newWindow.getIcons().add(new Image(SETTINGS_ICON_PATH));
        newWindow.setTitle("Appearance");
        newWindow.setScene(secondScene);
        newWindow.setResizable(false);

        newWindow.setX(stage.getX() + stage.getWidth()/3);
        newWindow.setY(stage.getY() + stage.getHeight()/3);
        newWindow.setWidth(stage.getWidth()/3);
        newWindow.setHeight(stage.getHeight()/2.3);

        newWindow.initOwner(stage);
        newWindow.initModality(Modality.APPLICATION_MODAL); 
        newWindow.showAndWait();
    }
    
    /**
     * Returns the index of the selected PATH
     * @param pathIn, the path
     * @return the index
     */
    public int getPieceSelection(String pathIn) {
        int i = 0;
        for(String str : AVAILABLE_PATHS) {
            if(str.equals(pathIn)) {
                return i;
            }
            i++;
        }
        return 0;
    }
    
    /**
     * Creates the game play pane for the player vs Player GUI
     * @param stage, the stage the game belongs to
     * @return Play Pane for Game
     */
    public Pane createPlayPane(Stage stage) {
        Game controller = Game.constructGame(true, stage, this);
        return controller.getRoot();
    }
    
    /**
     * Creates the game play pane for the player vs Player GUI
     * @param stage, the stage the game belongs to
     * @param time, the starting timer
     * @param inc, the timer to be incremented
     * @param timerType, the timer type to be used upon game reconstruction
     * @return Play Pane for Border Pane
     */
    public Pane createPlayPaneTimed(Stage stage, double time, double inc, int timerType) {
        TimedGame controller = TimedGame.constructTimedGame(true, time, inc, stage, timerType, this);
        return controller.getRoot();
    }
    
    /**
     * A confirmation window before the application is exited
     * @param stage, the stage the window belongs to
     */
    public void exitAppConfirm(Stage stage) {
        VBox content = new VBox();
        content.setSpacing(20);
        content.setAlignment(Pos.CENTER);
        Scene secondScene = new Scene(content, stage.getScene().getWidth()/2.3, stage.getScene().getHeight()/1.5);
        secondScene.getStylesheets().add(ChessLite.class.getResource("/resources/chess.css").toExternalForm());
        Stage newWindow = new Stage();
        
        Label confirmLbl = new Label("Quit Application");
        confirmLbl.setId("largefont");
        Label confirmLblSmall = new Label("Are you sure you want to quit using TreeFrog?");
        confirmLblSmall.setId("smallfont");
        confirmLblSmall.setMaxSize(300*scale,200*scale);
        confirmLblSmall.setWrapText(true);
        HBox horizontalButtons = new HBox();
        horizontalButtons.setAlignment(Pos.CENTER);
        content.getChildren().addAll(confirmLbl, confirmLblSmall, horizontalButtons);
        Button confirmButton = new Button("Yes");
        confirmButton.setOnAction((ActionEvent event) -> {
            Platform.exit();
            System.exit(0);
        });
        confirmButton.setId("appbutton");
        confirmButton.setFocusTraversable(false);
        Button cancelButton = new Button("No");
        cancelButton.setOnAction((ActionEvent event) -> {
            Stage theStage = (Stage) cancelButton.getScene().getWindow();
            theStage.close();
        });
        cancelButton.setId("appbutton");
        cancelButton.setFocusTraversable(false);
        horizontalButtons.getChildren().addAll(confirmButton, cancelButton);
        horizontalButtons.setSpacing(20);
        
        newWindow.getIcons().add(new Image(APP_ICON_PATH));
        newWindow.setTitle("Confirmation");
        newWindow.setScene(secondScene);
        newWindow.setResizable(false);

        newWindow.setX(stage.getX() + 100*scale);
        newWindow.setY(stage.getY() + 100*scale);
        newWindow.setHeight(300*scale);
        newWindow.setWidth(450*scale);

        newWindow.initOwner(stage);
        newWindow.initModality(Modality.APPLICATION_MODAL); 
        newWindow.showAndWait();
    }
    
    /**
     * @param args main arguments
     */ 
    public static void main(String[] args) {
        launch(args);
    }
    
}
