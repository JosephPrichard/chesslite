/*
 * A version of the Game class that has a timer
 * Just like the Game class, a core assumption is that the Board starts from the legal starting position
 * 7/3/20
 */
package gui;

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
     * @param timerType, the type of timer used in game re construction
     * @param app, the application object
     */
    public TimedGame(boolean whiteStart, double timeInSeconds, double incrementInSeconds, Stage stage, int timerType, ChessLite app) {
        super(whiteStart, stage, app);
        setTimerType(timerType);
        blackValue = timeInSeconds;
        whiteValue = timeInSeconds;
        blackTimerGUI = new Label(convertToDisplayFormat((int) blackValue));
        whiteTimerGUI = new Label(convertToDisplayFormat((int) whiteValue));
        whiteTimerGUI.setTextFill(Color.rgb(80, 80, 80));
        blackTimerGUI.setTextFill(Color.rgb(80, 80, 80));
        whiteTimerGUI.setFont(new Font("Roboto",43*getApp().getScale()));
        blackTimerGUI.setFont(new Font("Roboto",43*getApp().getScale()));
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
     * @param timerType, the type of timer used in game re construction
     * @param app, application object
     * @return a constructed Timed Game
     */
    public static TimedGame constructTimedGame(boolean whiteStart, double timeInSeconds,
                                               double incrementInSeconds, Stage stage, int timerType, ChessLite app) {
        TimedGame game = new TimedGame(whiteStart, timeInSeconds, incrementInSeconds, stage, timerType, app);
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
        AnchorPane.setTopAnchor(getBoardGUI(), getTopBarHeight() + 20.0);
        AnchorPane.setLeftAnchor(getBoardGUI(), 15.0);
        AnchorPane.setTopAnchor(getSideBar(), ((getStage().getHeight() - 
                (getElementHeight() + getElementHeight() + getScoreBoardHeight() + getElementHeight()))/2)+10);
        AnchorPane.setLeftAnchor(getSideBar(), (10 + getBoardSize()) + (getStage().getWidth() - 10 - getBoardSize() - getBarWidth()) / 2);
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
        image.setFitHeight(25 * getApp().getScale());
        image.setFitWidth(25 * getApp().getScale());
        backButton.setGraphic(image);
        backButton.setMinSize(50 * getApp().getScale(), 50 * getApp().getScale());
        backButton.setMaxSize(50 * getApp().getScale(), 50 * getApp().getScale());
        backButton.setFocusTraversable(false);
        backButton.setId("boardbutton");
        backButton.setOnAction((event) -> takeBackMove());
        return backButton;
    }

    @Override
    public void onGameFinished() {
        super.onGameFinished();
        whiteTimer.stop();
        blackTimer.stop();
        backButton.setDisable(true);
        clearSelectable();
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
        sidebar.setMinSize(getBarWidth(), getElementHeight() + getElementHeight() + getScoreBoardHeight() + getElementHeight());
        sidebar.setMaxSize(getBarWidth(), getElementHeight() + getElementHeight() + getScoreBoardHeight() + getElementHeight());
        HBox titles = constructTitles();
        HBox timers = constructTimers();
        HBox bottomButtons = constructButtonPanel();
        setUpNotationGUI();
        HBox notationHBox = new HBox();
        notationHBox.getChildren().add(getNotationTable());
        notationHBox.setPadding(new Insets(0, getBarWidth() * 0.1, 0, getBarWidth() * 0.1));
        sidebar.getChildren().addAll(titles, timers, notationHBox, bottomButtons);
        return sidebar;
    }

    /**
     * Construction of HBox Game GUI component
     * @return HBox to be constructed and returned
     */
    public final HBox constructTimers() {
        HBox timers = new HBox();
        timers.setMinSize(getBarWidth(), getElementHeight());
        timers.setMaxSize(getBarWidth(), getElementHeight());
        whiteTimerGUI.setAlignment(Pos.CENTER);
        whiteTimerGUI.setMinSize(getBarWidth() / 2, getElementHeight());
        whiteTimerGUI.setMaxSize(getBarWidth() / 2, getElementHeight());
        whiteTimerGUI.setId("timerbgright");
        blackTimerGUI.setAlignment(Pos.CENTER);
        blackTimerGUI.setMinSize((getBarWidth() / 2)-1, getElementHeight());
        blackTimerGUI.setMaxSize((getBarWidth() / 2)-1, getElementHeight());
        blackTimerGUI.setId("timerbgleft");
        timers.getChildren().addAll(whiteTimerGUI, blackTimerGUI);
        return timers;
    }

    /**
     * Utility function to convert Timer Integer value to a string
     * @param seconds the time to convert
     * @return string as display format
     */
    private static String convertToDisplayFormat(int seconds) {
        int minutes = Math.min((seconds / 60), 99);
        String minString = Integer.toString(minutes);
        minString = minString.length() > 1 ? Integer.toString(minutes) : "0" + minutes;
        int remainder = seconds % 60;
        String secString = Integer.toString(remainder);
        secString = secString.length() > 1 ? Integer.toString(remainder) : "0" + remainder;
        return minString + " : " + secString;
    }
}
