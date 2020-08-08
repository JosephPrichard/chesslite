/*
 * JavaFx main class for the ChessLite Application GUI
 * 6/11/20
 */
package gui;

import fxutil.AudioClipPlayer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Joseph
 */
public class ChessLite extends Application {
    
    public static final String TITLE = "ChessLite V1 - Joseph";
    
    public static final String APP_ICON_PATH = "/resources/blackknight.png"; //icon paths
    public static final String NEW_ICON_PATH = "/resources/boardsparkle.png";
    public static final String SETTINGS_ICON_PATH = "/resources/whitepawn.png";
    
    public static final int NO_TIMER = -1; //timer type for Game
    public static final int CLASSIC = 0;
    public static final int RAPID = 1;
    public static final int BLITZ = 2;
    public static final int BULLET = 3;

    public final static double HEIGHT = Screen.getPrimary().getBounds().getHeight()*0.8;
    public final static double WIDTH = HEIGHT*1.35;
    public final static double SCALE = HEIGHT/960;
    
    public static final String SOUND_CLIP_PATH = ChessLite.class.getResource("/resources/movepiece.wav").toExternalForm();
    public static AudioClipPlayer clip; //soundclip
    
    public static final ObservableList<String> TIMER_OPTIONS = FXCollections.observableArrayList( //combobox options
            "Casual Untimed", "Classic 30+20", "Rapid 15+10", "Blitz 3+2", "Bullet 1+0");
    public static final int[][] TIMER_INFO = {{30*60,20},{15*60,10},{3*60,2},{1*60,0}};
    public static final ObservableList<String> PIECES_OPTIONS = FXCollections.observableArrayList(
            "Classic", "Alpha", "Book", "Wooden", "Gothic");
    public static final ObservableList<String> COLORS_OPTIONS = FXCollections.observableArrayList(
            "Brown", "Blue", "Green", "Red");
    
    public static String PATH = "alpha"; //path for piece package
    public static final String CONFIG_NAME = "ChessConfig"; //file and config info
    public static final String DEFAULT_CONFIG_PATH = "/resources/DefaultConfig.dat";
    public static final String S = System.getProperty("file.separator");
    public static final String CONFIG_PATH = System.getProperty("user.home") + S + CONFIG_NAME + ".dat";
    public static final String[] AVALIABLE_PATHS = {"classic","alpha","book","wooden","gothic"};
    public static final int BROWN = 0;
    public static final int BLUE = 1;
    public static final int GREEN = 2;
    public static final int RED = 3;
    public static int COLOR_THEME = GREEN; //color theme

    /**
     * Static class initialization performed upon Application startup
     * Access the application configuration file to find application settings
     * If no file can be found a new one is created with default settings contained in
     * DEFAULT_CONFIG_PATH 
     */
    static {
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
                    String path = data[0];
                    if(contains(AVALIABLE_PATHS,path)) {
                        PATH = path;
                    }
                    int colornum = (Integer.parseInt(data[1]));
                    if(colornum <= RED && colornum >= BROWN) {
                        COLOR_THEME = colornum;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ChessLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Writes a string to the configuration file
     * @param str, the string to the written
     */
    public static void changeConfigData(String str) {
        writeStringToFile(str, CONFIG_PATH);
    }
    
    /**
     * Returns whether or not a string is contained within an array of strings
     * @param arr, the array to contain
     * @param stringIn, the string to be checked
     * @return whether the string is contained
     */
    public static boolean contains(String[] arr, String stringIn) {
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
    public static void writeStringToFile(String string, String path) {
        File file = new File(path);
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file,false))) {
                writer.write(string);
                writer.close();
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
    public static void copyToFile(BufferedReader reader, File file) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                String contents = "";
                String line;
                while((line = reader.readLine()) != null) {
                    contents = contents + line;
                    contents = contents + "\n";
                }
                writer.write(contents);
                reader.close();
                writer.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ChessLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    @Override
    public void init() {
        clip = new AudioClipPlayer(SOUND_CLIP_PATH);
        clip.startLoop();
    }

    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.getIcons().add(new Image(APP_ICON_PATH));
        primaryStage.setTitle(TITLE);
        primaryStage.setHeight(HEIGHT);
        primaryStage.setWidth(WIDTH);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            Platform.exit();
            System.exit(0);
        });
        Pane root = createPlayPane(primaryStage);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(ChessLite.class.getResource("/resources/chess.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Opens the player vs player select window
     * @param stage stage the window belongs to
     */
    public static void newGame(Stage stage) {
        BorderPane newroot = new BorderPane();
        Scene secondScene = new Scene(newroot, stage.getScene().getWidth()/2.3, stage.getScene().getHeight()/1.5);
        secondScene.getStylesheets().add(ChessLite.class.getResource("/resources/chess.css").toExternalForm());
        Stage newWindow = new Stage();
        
        VBox content = new VBox();
        content.setSpacing(20);
        
        Label timerlabel = new Label("Time Control");
        timerlabel.setFont(new Font("Roboto",22*ChessLite.SCALE));
        ObservableList<String> options = TIMER_OPTIONS;
        ComboBox timerBox = new ComboBox<>(options);
        timerBox.setId("combobox");
        double size = 20*ChessLite.SCALE;
        timerBox.setStyle("-fx-font: " + size + "px \"Roboto\";");
        timerBox.getSelectionModel().select(0);
        content.setAlignment(Pos.CENTER);
        
        content.getChildren().addAll(timerlabel, timerBox);
        
        HBox horizontalbuttons = new HBox();
        Button confirmbutton = new Button("Start");
        confirmbutton.setOnAction((ActionEvent event) -> {
            Stage thestage = (Stage) confirmbutton.getScene().getWindow();
            int selected = timerBox.getSelectionModel().getSelectedIndex();
            Pane pane;
            if(selected == 0) {
                pane = createPlayPane(stage);
            } else {
                pane = createPlayPaneTimed(stage, TIMER_INFO[selected-1][0], TIMER_INFO[selected-1][1],selected-1);
            }
            thestage.close();
            stage.getScene().setRoot(pane);
        });
        confirmbutton.setId("appbutton");
        confirmbutton.setFont(new Font("Roboto",22*ChessLite.SCALE));
        confirmbutton.setFocusTraversable(false);
        Button cancelbutton = new Button("Cancel");
        cancelbutton.setOnAction((ActionEvent event) -> {
            Stage thestage = (Stage) cancelbutton.getScene().getWindow();
            thestage.close();
        });
        cancelbutton.setId("appbutton");
        cancelbutton.setFont(new Font("Roboto",22*ChessLite.SCALE));
        cancelbutton.setFocusTraversable(false);
        horizontalbuttons.getChildren().addAll(confirmbutton, cancelbutton);
        horizontalbuttons.setSpacing(20);
        horizontalbuttons.setAlignment(Pos.CENTER);
        BorderPane.setMargin(horizontalbuttons, new Insets(0,0,50,0));
        BorderPane.setMargin(content, new Insets(50,0,50,0));
        newroot.setBottom(horizontalbuttons);
        newroot.setTop(content);
        
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
    public static void openAppearancePanel(Stage stage, Game game) {
        BorderPane newroot = new BorderPane();
        Scene secondScene = new Scene(newroot, stage.getScene().getWidth()/2.3, stage.getScene().getHeight()/1.5);
        secondScene.getStylesheets().add(ChessLite.class.getResource("/resources/chess.css").toExternalForm());
        Stage newWindow = new Stage();
        
        VBox content = new VBox();
        content.setSpacing(20);
        
        HBox styles = new HBox();
        styles.setSpacing(20);
        Label styleLabel = new Label("Pieces Style");
        styleLabel.setFont(new Font("Roboto",22*ChessLite.SCALE));
        ObservableList<String> pieceoptions = PIECES_OPTIONS;
        ComboBox pieceBox = new ComboBox<>(pieceoptions);
        pieceBox.setId("combobox");
        pieceBox.getSelectionModel().select(getPieceSelection(PATH));
        double size = 20*ChessLite.SCALE;
        pieceBox.setStyle("-fx-font: " + size + "px \"Roboto\";");
        content.setAlignment(Pos.CENTER);
        styles.setAlignment(Pos.CENTER);
        styles.getChildren().addAll(styleLabel, pieceBox);
        
        HBox colors = new HBox();
        colors.setSpacing(20);
        Label colorLabel = new Label("Board Color");
        colorLabel.setFont(new Font("Roboto",22*ChessLite.SCALE));
        ObservableList<String> coloroptions = COLORS_OPTIONS;
        ComboBox colorBox = new ComboBox<>(coloroptions);
        colorBox.setId("combobox");
        colorBox.getSelectionModel().select(COLOR_THEME);
        double size1 = 20*ChessLite.SCALE;
        colorBox.setStyle("-fx-font: " + size1 + "px \"Roboto\";");
        content.setAlignment(Pos.CENTER);
        colors.setAlignment(Pos.CENTER);
        colors.getChildren().addAll(colorLabel, colorBox);
        
        content.getChildren().addAll(styles,colors);
        
        HBox horizontalbuttons = new HBox();
        Button confirmbutton = new Button("Confirm");
        confirmbutton.setOnAction((ActionEvent event) -> {
            Stage thestage = (Stage) confirmbutton.getScene().getWindow();
            int selected = pieceBox.getSelectionModel().getSelectedIndex();
            PATH = AVALIABLE_PATHS[selected];
            COLOR_THEME = colorBox.getSelectionModel().getSelectedIndex();
            game.reRenderBoard();
            changeConfigData(PATH + "\n" + COLOR_THEME);
            thestage.close();
        });
        confirmbutton.setId("appbutton");
        confirmbutton.setFont(new Font("Roboto",22*ChessLite.SCALE));
        confirmbutton.setFocusTraversable(false);
        Button cancelbutton = new Button("Cancel");
        cancelbutton.setOnAction((ActionEvent event) -> {
            Stage thestage = (Stage) cancelbutton.getScene().getWindow();
            thestage.close();
        });
        cancelbutton.setId("appbutton");
        cancelbutton.setFont(new Font("Roboto",22*ChessLite.SCALE));
        cancelbutton.setFocusTraversable(false);
        horizontalbuttons.getChildren().addAll(confirmbutton, cancelbutton);
        horizontalbuttons.setSpacing(20);
        horizontalbuttons.setAlignment(Pos.CENTER);
        BorderPane.setMargin(horizontalbuttons, new Insets(0,0,50,0));
        BorderPane.setMargin(content, new Insets(50,0,50,0));
        newroot.setBottom(horizontalbuttons);
        newroot.setTop(content);
        
        newWindow.getIcons().add(new Image(SETTINGS_ICON_PATH));
        newWindow.setTitle("Settings");
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
    public static int getPieceSelection(String pathIn) {
        int i = 0;
        for(String path : AVALIABLE_PATHS) {
            if(path.equals(pathIn)) {
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
    public static Pane createPlayPane(Stage stage) {
        Game controller = Game.constructGame(true, stage);
        return controller.getRoot();
    }
    
    /**
     * Creates the game play pane for the player vs Player GUI
     * @param stage, the stage the game belongs to
     * @param time, the starting timer
     * @param inc, the timer to be incremented
     * @param timertype, the timer type to be used upon game reconstruction
     * @return Play Pane for Border Pane
     */
    public static Pane createPlayPaneTimed(Stage stage, double time, double inc, int timertype) {
        TimedGame controller = TimedGame.constructTimedGame(true, time, inc, stage, timertype);
        return controller.getRoot();
    }
    
    /**
     * A confirmation window before the application is exited
     * @param stage, the stage the window belongs to
     */
    public static void exitAppConfirm(Stage stage) {
        VBox content = new VBox();
        content.setSpacing(20);
        content.setAlignment(Pos.CENTER);
        Scene secondScene = new Scene(content, stage.getScene().getWidth()/2.3, stage.getScene().getHeight()/1.5);
        secondScene.getStylesheets().add(ChessLite.class.getResource("/resources/chess.css").toExternalForm());
        Stage newWindow = new Stage();
        
        Label confirmLbl = new Label("Quit Application");
        confirmLbl.setId("largefont");
        Label confirmLblsmall = new Label("Are you sure you want to quit using TreeFrog?");
        confirmLblsmall.setId("smallfont");
        confirmLblsmall.setMaxSize(300*SCALE,200*SCALE);
        confirmLblsmall.setWrapText(true);
        HBox horizontalbuttons = new HBox();
        horizontalbuttons.setAlignment(Pos.CENTER);
        content.getChildren().addAll(confirmLbl, confirmLblsmall, horizontalbuttons);
        Button confirmbutton = new Button("Yes");
        confirmbutton.setOnAction((ActionEvent event) -> {
            Platform.exit();
            System.exit(0);
        });
        confirmbutton.setId("appbutton");
        confirmbutton.setFocusTraversable(false);
        Button cancelbutton = new Button("No");
        cancelbutton.setOnAction((ActionEvent event) -> {
            Stage thestage = (Stage) cancelbutton.getScene().getWindow();
            thestage.close();
        });
        cancelbutton.setId("appbutton");
        cancelbutton.setFocusTraversable(false);
        horizontalbuttons.getChildren().addAll(confirmbutton, cancelbutton);
        horizontalbuttons.setSpacing(20);
        
        newWindow.getIcons().add(new Image(APP_ICON_PATH));
        newWindow.setTitle("Confirmation");
        newWindow.setScene(secondScene);
        newWindow.setResizable(false);

        newWindow.setX(stage.getX() + 4*Tile.TILE_SIZE);
        newWindow.setY(stage.getY() + 3*Tile.TILE_SIZE);
        newWindow.setHeight(300*SCALE);
        newWindow.setWidth(450*SCALE);

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
