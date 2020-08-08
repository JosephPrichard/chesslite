/*
 * A version of the Game class that has a timer
 * Just like the Game class, a core assumption is that the Board starts from the legal starting position
 * 7/3/20
 */
package gui;

import static gui.Game.BAR_WIDTH;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Joseph
 */
public class TimedGame extends Game {

    private final double increment; //seconds
    private double blackValue; //real value of Timer
    private double whiteValue;
    private final Label whiteTimerGUI; //displayed value of Timer
    private final Label blackTimerGUI;
    private final Timeline whiteTimer; //control of Timer
    private final Timeline blackTimer;
    private Button backButton;

    /**
     * Constructs a new Game belonging to a stage
     * @param whiteStart, the starting position of the Game Board object
     * @param timeInSeconds, the total time for each side
     * @param incrementInSeconds, the increment in seconds after a move
     * @param stage, the stage game belongs to
     * @param timertype, the type of timer used in game re construction
     */
    public TimedGame(boolean whiteStart, double timeInSeconds, double incrementInSeconds, Stage stage, int timertype) {
        super(whiteStart, stage);
        setTimerType(timertype);
        blackValue = timeInSeconds;
        whiteValue = timeInSeconds;
        blackTimerGUI = new Label(convertToDisplayFormat((int) blackValue));
        whiteTimerGUI = new Label(convertToDisplayFormat((int) whiteValue));
        whiteTimerGUI.setTextFill(Color.rgb(80, 80, 80));
        blackTimerGUI.setTextFill(Color.rgb(80, 80, 80));
        whiteTimerGUI.setFont(new Font("Roboto",43*ChessLite.SCALE));
        blackTimerGUI.setFont(new Font("Roboto",43*ChessLite.SCALE));
        increment = incrementInSeconds;
        whiteTimer = new Timeline();
        blackTimer = new Timeline();
        whiteTimer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            if (getGameInfo().isLastTurnWhite() && getStage().isFocused()) {
                whiteValue--;
                whiteTimerGUI.setText(convertToDisplayFormat((int) whiteValue));
            }
            if (whiteValue == 0) {
                whiteTimerGUI.setTextFill(Color.rgb(255, 255, 255));
                whiteTimerGUI.setId("timerbgrightred");
                String msg = "Time Out : 0-1";
                getNotationTable().addFinishedMessage(msg);
                onGameFinished();
            } else if (whiteValue < 10) {
                whiteTimerGUI.setId("timerbgrightpink");
            } else {
                whiteTimerGUI.setId("timerbgright");
            }
        }));
        whiteTimer.setCycleCount(Timeline.INDEFINITE);
        blackTimer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            if (!getGameInfo().isLastTurnWhite() && getStage().isFocused()) {
                blackValue--;
                blackTimerGUI.setText(convertToDisplayFormat((int) blackValue));
            }
            if (blackValue == 0) {
                blackTimerGUI.setTextFill(Color.rgb(255, 255, 255));
                blackTimerGUI.setId("timerbgleftred");
                String msg = "Time Out : 1-0";
                getNotationTable().addFinishedMessage(msg);
                onGameFinished();
            } else if (blackValue < 10) {
                blackTimerGUI.setId("timerbgleftpink");
            } else {
                blackTimerGUI.setId("timerbgleft");
            }
        }));
        blackTimer.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Performs initialization similar to Game::constructGame
     * @param whiteStart, the starting position of the Game Board object
     * @param timeInSeconds, the total time for each side
     * @param incrementInSeconds, the increment in seconds after a move
     * @param stage, the stage game belongs to
     * @param timertype, the type of timer used in game re construction
     * @return a constructed Timed Game
     */
    public static final TimedGame constructTimedGame(boolean whiteStart, double timeInSeconds,
            double incrementInSeconds, Stage stage, int timertype) {
        TimedGame game = new TimedGame(whiteStart, timeInSeconds, incrementInSeconds, stage, timertype);
        game.initBoard(whiteStart);
        game.initRoot();
        game.preGame();
        return game;
    }

    @Override
    public void initRoot() {
        setSideBar(constructScoreBoard());
        HBox topBar = constructTopBar();
        HBox topBorder = constructTopBorder();
        AnchorPane.setTopAnchor(topBar, 1.0);
        AnchorPane.setTopAnchor(getBoardGUI(), TOP_BAR_HEIGHT + 20.0);
        AnchorPane.setLeftAnchor(getBoardGUI(), 15.0);
        AnchorPane.setTopAnchor(getSideBar(), ((getStage().getHeight() - 
                (ELEMENT_HEIGHT + ELEMENT_HEIGHT + SCOREBOARD_HEIGHT + ELEMENT_HEIGHT))/2)+10);
        AnchorPane.setLeftAnchor(getSideBar(), (10 + BOARD_SIZE) + (getStage().getWidth() - 10 - BOARD_SIZE - BAR_WIDTH) / 2);
        getRoot().getChildren().addAll(topBorder,topBar,getBoardGUI(), getSideBar());
    }

    /**
     * Adds time to timers after a move has been made
     */
    public void addTime() {
        if (!getGameInfo().isLastTurnWhite()) {
            whiteValue = whiteValue + increment;
            whiteTimerGUI.setText(convertToDisplayFormat((int) whiteValue));
            if (whiteValue < 10) {
                whiteTimerGUI.setId("timerbgrightpink");
            } else {
                whiteTimerGUI.setId("timerbgright");
            }
        } else {
            blackValue = blackValue + increment;
            blackTimerGUI.setText(convertToDisplayFormat((int) blackValue));
            if (blackValue < 10) {
                blackTimerGUI.setId("timerbgleftpink");
            } else {
                blackTimerGUI.setId("timerbgleft");
            }
        }
    }

    @Override
    public Button constructBackButton() {
        backButton = new Button();
        ImageView image = new ImageView(new Image("/resources/arrowsmall.png"));
        image.setFitHeight(25 * ChessLite.SCALE);
        image.setFitWidth(25 * ChessLite.SCALE);
        backButton.setGraphic(image);
        backButton.setMinSize(50 * ChessLite.SCALE, 50 * ChessLite.SCALE);
        backButton.setMaxSize(50 * ChessLite.SCALE, 50 * ChessLite.SCALE);
        backButton.setFocusTraversable(false);
        backButton.setId("boardbutton");
        backButton.setOnAction((event) -> {
            takeBackMove();
        });
        return backButton;
    }

    @Override
    public void onGameFinished() {
        super.onGameFinished();
        whiteTimer.stop();
        blackTimer.stop();
        backButton.setDisable(true);
        clearSelectables();
    }

    @Override
    public void makeMove(Tile tile) {
        super.makeMove(tile);
        //after both black and white have made one move, the timer starts
        //time is not added for the first 2 turns
        int size = getGameInfo().getMoves().size();
        if (size == 2) {
            whiteTimer.play();
            blackTimer.play();
        } else if (size > 2) {
            addTime();
        }
    }

    @Override
    public void makeMoveEnPassant(Tile tile, int offset) {
        super.makeMoveEnPassant(tile, offset);
        addTime();
    }

    @Override
    public void makeMovePromotion(Tile tile, Piece piece) {
        super.makeMovePromotion(tile, piece);
        addTime();
    }

    @Override
    public void makeMoveCastle(boolean forWhite, boolean kingSide) {
        super.makeMoveCastle(forWhite, kingSide);
        addTime();
    }

    @Override
    public VBox constructScoreBoard() {
        VBox sidebar = new VBox();
        sidebar.setId("scoreoutsets");
        sidebar.setMinSize(BAR_WIDTH, ELEMENT_HEIGHT + ELEMENT_HEIGHT + SCOREBOARD_HEIGHT + ELEMENT_HEIGHT);
        sidebar.setMaxSize(BAR_WIDTH, ELEMENT_HEIGHT + ELEMENT_HEIGHT + SCOREBOARD_HEIGHT + ELEMENT_HEIGHT);
        HBox titles = constructTitles();
        HBox timers = constructTimers();
        HBox bottombuttons = constructButtonPanel();
        setUpNotationGUI();
        HBox notationHBox = new HBox();
        notationHBox.getChildren().add(getNotationTable());
        notationHBox.setPadding(new Insets(0, BAR_WIDTH * 0.125, 0, BAR_WIDTH * 0.125));
        sidebar.getChildren().addAll(titles, timers, notationHBox, bottombuttons);
        return sidebar;
    }

    /**
     * Construction of HBox Game GUI component
     * @return HBox to be constructed and returned
     */
    public final HBox constructTimers() {
        HBox timers = new HBox();
        timers.setMinSize(BAR_WIDTH, ELEMENT_HEIGHT);
        timers.setMaxSize(BAR_WIDTH, ELEMENT_HEIGHT);
        whiteTimerGUI.setAlignment(Pos.CENTER);
        whiteTimerGUI.setMinSize(BAR_WIDTH / 2, ELEMENT_HEIGHT);
        whiteTimerGUI.setMaxSize(BAR_WIDTH / 2, ELEMENT_HEIGHT);
        whiteTimerGUI.setId("timerbgright");
        blackTimerGUI.setAlignment(Pos.CENTER);
        blackTimerGUI.setMinSize((BAR_WIDTH / 2)-1, ELEMENT_HEIGHT);
        blackTimerGUI.setMaxSize((BAR_WIDTH / 2)-1, ELEMENT_HEIGHT);
        blackTimerGUI.setId("timerbgleft");
        timers.getChildren().addAll(whiteTimerGUI, blackTimerGUI);
        return timers;
    }

    /**
     * Utility function to convert Timer Integer value to a string
     * @param seconds
     * @return 
     */
    private static String convertToDisplayFormat(int seconds) {
        int minutes = (seconds / 60) <= 99 ? seconds / 60 : 99;
        String minString = Integer.toString(minutes);
        minString = minString.length() > 1 ? Integer.toString(minutes) : "0" + Integer.toString(minutes);
        int remainder = seconds % 60;
        String secString = Integer.toString(remainder);
        secString = secString.length() > 1 ? Integer.toString(remainder) : "0" + Integer.toString(remainder);
        return minString + " : " + secString;
    }
}
