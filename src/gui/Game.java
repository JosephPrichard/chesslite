/*
 * A Controller class to manage the current Game's game flow and GUI
 * A core assumption is that the Board starts from the legal starting position
 * 7/3/20
 */
package gui;

import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joseph
 */
public class Game {

    public static final int LOWER_BOUNDARY = 0;
    public static final int UPPER_BOUNDARY = 7;
    public static final int HEIGHT = 8;
    public static final int WIDTH = 8;
    
    public static final Color GREEN = Color.rgb(85,107,47,0.7);
    public static final Color RED = Color.rgb(128,0,0,0.7);
    public static final int IN_PROGRESS = 0;
    public static final int CHECKMATE = 1;
    public static final int STALEMATE = 2;
    public static final int NO_TIMER = -1;
    
    private double boardSize;
    private double elementHeight;
    private double scoreBoardHeight;
    private double topBarHeight;
    private double tileSize; //calculate GUI sizes based of ChessLite application scale
    private double barWidth;
    private double barHeight;
    
    private ChessLite app; //dependency on application 
    
    private final GameInfo gameInfo = new GameInfo(); //deals with previous moves and ByteBoard storage
    private final Board board = new Board(); //the current Board
    
    private VBox sideBar;
    private NotationBoard notationTable;
    private final Circle whiteCircle;
    private final Circle blackCircle;
    private final AnchorPane root;
    private final Stage stage;
    
    private Tile selectedTile; //the current selected tile all moves are relative to this tile
    private final ArrayList<Selectable> selectable = new ArrayList<>(); //all active selectable
    private final ArrayList<Tile> highlightedTiles = new ArrayList<>();
    private boolean whiteBoardPosition;
    private boolean inCheck = false;
    private boolean isWhiteTurn = true;
    private boolean moveReadyState = true;
    private boolean finished = false;
    private boolean canRender = true;
    private int gameResult = IN_PROGRESS;
    private int timerType = NO_TIMER;
    public static final int[][] TIMER_INFO = {{30*60,20},{15*60,10},{3*60,2},{60,0}};

    /**
     * Constructs a new Game belonging to a stage
     * @param whiteStart, the starting position of the Game Board object
     * @param stageIn, the stage game belongs to
     * @param app, the application object
     */
    protected Game(boolean whiteStart, Stage stageIn, ChessLite app) {
        setApp(app);
        whiteCircle = new Circle();
        blackCircle = new Circle();
        whiteCircle.setRadius(8*app.getScale());
        blackCircle.setRadius(8*app.getScale());
        whiteBoardPosition = whiteStart;
        root = new AnchorPane();
        stage = stageIn;
    }
    
    /**
     * Constructs game and performs a standard initialization on the game
     * initializes the Game Board
     * initializes Game GUI's root
     * calculates the Board avaliable moves
     * renders the current Side turn
     * @param whiteStart, the starting position of the Game Board object
     * @param stageIn, the stage game belongs to
     * @param app, the application object
     * @return constructed game
     */
    public static Game constructGame(boolean whiteStart, Stage stageIn, ChessLite app) {
        Game game = new Game(whiteStart, stageIn, app);
        game.initBoard(whiteStart);
        game.initRoot();
        game.preGame();
        return game;
    }
    
    /**
     * Performs a standard initialization of the Game Board
     * @param whiteStart, position of Board from proper perspective
     */
    public final void initBoard(boolean whiteStart) {
        if(whiteStart) {
            board.initWhiteBoard(GameInfo.INITIAL_BOARD,this);
        } else {
            board.initBlackBoard(GameInfo.INITIAL_BOARD,this);
        }
    }
    
    /**
     * Initialization of the Game GUI's root node
     * Sets position of score board, navigation bar, and board GUI
     */
    public void initRoot() {   
        setSideBar(constructScoreBoard());
        HBox topBar = constructTopBar();
        HBox topBorder = constructTopBorder();
        AnchorPane.setTopAnchor(topBar, 1.0);
        AnchorPane.setTopAnchor(getBoardGUI(), topBarHeight + 20.0);
        AnchorPane.setLeftAnchor(getBoardGUI(), 15.0);
        AnchorPane.setTopAnchor(sideBar, ((stage.getHeight() - 
                (elementHeight + scoreBoardHeight + elementHeight))/2)+10);
        AnchorPane.setLeftAnchor(sideBar, (10+boardSize)+(stage.getWidth()-10-boardSize-barWidth)/2);
        root.getChildren().addAll(topBorder,topBar,sideBar,getBoardGUI());
    }
    
    private void setApp(ChessLite app) {
        this.app = app;
        tileSize = 100*app.getScale(); //calculate GUI sizes based of ChessLite application scale
        boardSize = tileSize * HEIGHT;
        elementHeight = 80*app.getScale();
        scoreBoardHeight = 275*app.getScale();
        topBarHeight = 70*app.getScale();
        barWidth = (app.getWidth() - 110 - HEIGHT* tileSize);
        barHeight = app.getScale()*(app.getHeight()/1.5);
    }

    public double getElementHeight() {
        return elementHeight;
    }

    public double getScoreBoardHeight() {
        return scoreBoardHeight;
    }

    public double getTopBarHeight() {
        return topBarHeight;
    }

    public double getTileSize() {
        return tileSize;
    }

    public double getBarWidth() {
        return barWidth;
    }

    public double getBarHeight() {
        return barHeight;
    }

    public double getBoardSize() {
        return boardSize;
    }
    
    public final ChessLite getApp() {
        return app;
    }
    
    public ArrayList<Tile> getAttackingKing() {
        return board.getAttackingKing();
    }

    public ArrayList<Tile> getAttackWhiteListed() {
        return board.getAttackWhiteListed();
    }

    public Piece getBlackKing() {
        return board.getBlackKing();
    }

    public Piece getWhiteKing() {
        return board.getWhiteKing();
    }
    
    public ArrayList<Tile> kingMoves() {
        return board.getKingCanMove();
    }
    
    public Pane getBoardGUI() {
        return board.getBoardGUI();
    }

    public int getTimerType() {
        return timerType;
    }

    public final void setTimerType(int timerType) {
        this.timerType = timerType;
    }

    public boolean canRender() {
        return canRender;
    }

    public void setCanRender(boolean canRender) {
        this.canRender = canRender;
    }
    
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }
    
    public Stage getStage() {
        return stage;
    }
    
    public boolean isMoveReady() {
        return moveReadyState;
    }

    public void setMoveReadyState(boolean moveReadyState) {
        this.moveReadyState = moveReadyState;
    }
    
    public ArrayList<Selectable> getSelectable() {
        return selectable;
    }

    public VBox getSideBar() {
        return sideBar;
    }

    public void setSideBar(VBox sideBar) {
        this.sideBar = sideBar;
    }
    
    public NotationBoard getNotationTable() {
        return notationTable;
    }

    public void setNotationTable(NotationBoard notationTable) {
        this.notationTable = notationTable;
    }

    public void setIsWhiteTurn(boolean isWhiteTurn) {
        this.isWhiteTurn = isWhiteTurn;
    }

    public final boolean isWhiteTurn() {
        return isWhiteTurn;
    }
    
    public boolean inCheck() {
        return inCheck;
    }
    
    public AnchorPane getRoot() {
        return root;
    }

    public Tile[][] getTiles() {
        return board.getTiles();
    }

    public final GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setSelectedTile(Tile selected) {
        this.selectedTile = selected;
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }
    
    public void addToBoardGUI(Node n) {
        getBoardGUI().getChildren().add(n);
    }
    
    /**
     * Adds a Selectable that performs a Standard chess move on the Game
     * @param tile to be rendered at and the tile to move to
     */
    public void addSelectable(Tile tile) {
        Selectable selectable = new Selectable(tile, this, Selectable.LIGHT_GREY,
                Selectable.LIGHT_GREY, Selectable.GREY, Selectable.LIGHT_GREY, app){
            @Override
            public void move() {
                app.getClip().play();
                makeMove(tile);
                clearSelectable();
            }
        };
        selectable.relocate(tile.getXReal(), tile.getYReal());
        selectable.setHighlightsNoHover();
        this.addToBoardGUI(selectable);
        this.selectable.add(selectable);
    }
    
     /**
     * Adds a Selectable that performs an En Passant move on the Game
     * @param tile to be rendered at and the tile to move to
     * @param offset used to find the piece to be removed
     */
    public void addEnPassantSelectable(Tile tile, int offset) {
        Selectable selectable = new Selectable(tile, this, Selectable.LIGHT_GREY,
                Selectable.LIGHT_GREY, Selectable.GREY, Selectable.LIGHT_GREY, app) {
            @Override
            public void move() {
                app.getClip().play();
                makeMoveEnPassant(tile, offset);
                clearSelectable();
            }
        };
        selectable.relocate(tile.getXReal(), tile.getYReal());
        selectable.setHighlightsNoHover();
        this.addToBoardGUI(selectable);
        this.selectable.add(selectable);
    }
    
     /**
     * Adds a Selectable that performs a Promotion move on the Game
     * @param tile to be rendered at and the tile to move to
     */
    public void addPromotionSelectable(Tile tile) {
        Selectable selectable = new Selectable(tile, this, Selectable.LIGHT_GREY,
                Selectable.LIGHT_GREY, Selectable.GREY, Selectable.LIGHT_GREY, app){
            @Override
            public void move() {
                boolean isWhite = getSelectedTile().getPiece().isWhite();
                promotionSelection(isWhite,app);
            }
        };
        selectable.relocate(tile.getXReal(), tile.getYReal());
        selectable.setHighlightsNoHover();
        this.addToBoardGUI(selectable);
        this.selectable.add(selectable);
    }
    
     /**
     * Adds a Selectable that performs a Castling chess move on the game to a given tile
     * upon clicked
     * @param tile to move to
     * @param forWhite, the Color side castle is performed on top-bottom
     * @param kingSide, the Piece side castle is performed on left-right
     */
    public void addCastleSelectable(Tile tile, boolean forWhite, boolean kingSide) {
        Selectable selectable = new Selectable(tile, this, Selectable.LIGHT_GREY,
                Selectable.LIGHT_GREY, Selectable.GREY, Selectable.LIGHT_GREY, app){
            @Override
            public void move() {
                app.getClip().play();
                makeMoveCastle(forWhite, kingSide);
                clearSelectable();
            }
        };
        selectable.relocate(tile.getXReal(), tile.getYReal());
        selectable.setCrownIcon();
        this.addToBoardGUI(selectable);
        this.selectable.add(selectable);
    }
    
    /**
     * Adds a Visual tile that does not perform any move upon click
     * @param tile to be rendered at 
     */
    public void addVisualize(Tile tile) {
        Selectable selectable = new Selectable(tile, this, Selectable.LIGHT_GREY,
                Selectable.LIGHT_GREY, Selectable.GREY, Selectable.LIGHT_GREY, app);
        selectable.relocate(tile.getXReal(), tile.getYReal());
        selectable.setHighlightsNoHover();
        this.addToBoardGUI(selectable);
        this.selectable.add(selectable);
    }
    
    /**
     * Renders the Last turn on the GUI by changing the color of the visual circles
     * If turn color is GREEN
     * otherwise color is RED
     */
    private void renderTurn() {  
        if(gameInfo.isLastTurnWhite()) {
           whiteCircle.setFill(GREEN);
           blackCircle.setFill(RED);
        } else {
           whiteCircle.setFill(RED);
           blackCircle.setFill(GREEN);
        }  
    }

    /**
     * Clears all selectable from the Game GUI and the CURRENT selected tile
     */
    public void clearSelectable() {
        if(selectedTile != null) {
            selectedTile.setUnselected();
            selectedTile = null;
        }
        selectable.forEach((Selectable selectable) -> getBoardGUI().getChildren().remove(selectable));
        selectable.clear();
    }
    
    /**
     * Highlights the recent tiles active in a move, the tile the piece was moved from
     * and the tile the piece was moved to
     */
    public void highlightRecentTiles() {
        highlightedTiles.forEach(Tile::setUnHighLighted);
        highlightedTiles.clear();
        ArrayList<int[]> coordinates = gameInfo.getRecentlyMovedTileCoordinates();
        coordinates.forEach((coordinate)->{
            highlightedTiles.add(board.getTiles()[coordinate[0]][coordinate[1]]);
            board.getTiles()[coordinate[0]][coordinate[1]].setHighLighted();
        });
    }
    
    /**
     * Returns the last move in the Game gameInfo
     * @return move as Move object
     */
    public Move getLastMove() {
        ArrayList<Move> moves = getGameInfo().getMoves();
        int num = getGameInfo().getMoveNum();
        if(!moves.isEmpty() && num > -1) {
            return moves.get(num);
        } else {
            return null;
        }
    }

    /**
     * Performs pre Game initialization
     * calculates the avaliable moves and renders turn
     */
    public final void preGame() {
        board.calculateMoves(isWhiteTurn());
        renderTurn();
    }
    
    /**
     * Refer to Board::inCheck implementation of this algorithm for details
     * @param king, the king to check for and perform evaluations on
     * @param row, the row of the position
     * @param col, the column of the position
     * @return whether or not the position is in Check
     */
    public boolean inCheck(Piece king, int row, int col) {
        return board.inCheck(king, row, col);
    }
    
    /**
     * Performs a pre-move initialization by using Game Board methods 
     * Uses information from Game Board to properly redirect Game flow
     * Called after every move with intention to be called before a new move can
     * be made
     */
    private void preMove() {
        board.calculateMoves(isWhiteTurn());
        if(!board.getAttackingKing().isEmpty()) { 
            inCheck = true;
            board.getKing(isWhiteTurn()).getTile().setInCheck();
            if(board.getKingCanMove().isEmpty() && !board.hasLegalMoves(isWhiteTurn()))  {
                gameInfo.setRecentCheckMate();
                gameResult = CHECKMATE;
                onGameFinished();
            } else {
                gameInfo.setRecentCheck();
            }
        } else if(!board.hasLegalMoves(isWhiteTurn())) {
            gameInfo.setRecentStaleMate();
            gameResult = STALEMATE;
            onGameFinished();
        }
        renderTurn();
        highlightRecentTiles();
    }
    
    public void onGameFinished() {
        setFinished(true);
    }
    
    /**
     * Public method to perform a Standard move on Game
     * @param tile to be moved to
     */
    public void makeMove(Tile tile) {
        move(tile);
        if(gameResult == CHECKMATE) {
            String msg = isWhiteTurn() ? "Checkmate : 0-1" : "Checkmate : 1-0";
            notationTable.addFinishedMessage(msg);
        } else if(gameResult == STALEMATE) {
            notationTable.addFinishedMessage("Stalemate : Draw");
        }
    }
    
    /**
     * Public method to perform a Standard move on Game
     * @param tile to be moved to
     * @param offset, offset used to remove piece taken en passant
     */
    public void makeMoveEnPassant(Tile tile, int offset) {
        moveEnPassant(tile,offset);
        if(gameResult == CHECKMATE) {
            String msg = isWhiteTurn() ? "Checkmate : 0-1" : "Checkmate : 1-0";
            notationTable.addFinishedMessage(msg);
        } else if(gameResult == STALEMATE) {
            notationTable.addFinishedMessage("Stalemate : Draw");
        }
    }
    
    /**
     * Public method to perform a Standard move on Game
     * @param tile to be moved to
     * @param piece to be promoted to
     */
    public void makeMovePromotion(Tile tile, Piece piece) {
        movePromotion(tile, piece);
        if(gameResult == CHECKMATE) {
            String msg = isWhiteTurn() ? "Checkmate : 0-1" : "Checkmate : 1-0";
            notationTable.addFinishedMessage(msg);
        } else if(gameResult == STALEMATE) {
            notationTable.addFinishedMessage("Stalemate : Draw");
        }
    }
    
    /**
     * Public method to perform a Standard move on Game
     * @param forWhite, the Color side castle is performed on top-bottom
     * @param kingSide, the Piece side castle is performed on left-right
     */
    public void makeMoveCastle(boolean forWhite, boolean kingSide) {
        moveCastle(forWhite, kingSide);
        if(gameResult == CHECKMATE) {
            String msg = isWhiteTurn() ? "Checkmate : 0-1" : "Checkmate : 1-0";
            notationTable.addFinishedMessage(msg);
        } else if(gameResult == STALEMATE) {
            notationTable.addFinishedMessage("Stalemate : Draw");
        }
    }
    
    /**
     * Private method to perform a Standard move on Game and redirect
     * Game flow back to GUI listening
     * 
     * Set Game out of Check
     * Add move to gameInfo 
     * Perform move on GUI
     * Redirect flow to opposing Turn and perform pre-move 
     * Add the most recently added move to notationTable
     * 
     * @param tile to be moved to
     */
    private void move(Tile tile) {
        int oldNot = gameInfo.getMoveNum();
        board.getBlackKing().getTile().setOffCheck();
        board.getWhiteKing().getTile().setOffCheck();
        inCheck = false;
        Tile selected = selectedTile;
        Piece taken = tile.getPiece();
        gameInfo.makeMove(selected,tile);
        selected.movePiece(tile);
        if(taken != null) {
            removeTaken(taken);
            gameInfo.setRecentCapture();
        }   
        isWhiteTurn = !isWhiteTurn;
        preMove();
        notationTable.addLastToGUI();
        notationTable.selectEntry(gameInfo.getMoveNum(), oldNot);
    }
    
     /**
     * Private method to perform an En Passant move on Game and redirect
     * Game flow back to GUI listening
     * 
     * Set Game out of Check
     * Add move to gameInfo 
     * Perform move on GUI
     * Redirect flow to opposing Turn and perform pre-move 
     * Add the most recently added move to notationTable
     * 
     * @param tile to be moved to
     * @param offset, offset used to remove piece taken en passant
     */
    private void moveEnPassant(Tile tile, int offset) {
        int oldNot = gameInfo.getMoveNum();
        board.getBlackKing().getTile().setOffCheck();
        board.getWhiteKing().getTile().setOffCheck();
        inCheck = false;
        Tile selected = selectedTile;
        Piece taken = board.getTiles()[tile.getRow()+offset][tile.getCol()].getPiece();
        gameInfo.makeMoveEnPassant(selected,tile,taken);
        selected.movePieceEnPassant(tile,board.getTiles()[tile.getRow()+offset][tile.getCol()]);   
        removeTaken(taken);
        isWhiteTurn = !isWhiteTurn;
        gameInfo.setRecentEnPassant();
        preMove();
        notationTable.addLastToGUI();
        notationTable.selectEntry(gameInfo.getMoveNum(), oldNot);
    }

     /**
     * Private method to perform a Standard move on Game and redirect
     * Game flow back to GUI listening
     * 
     * Set Game out of Check
     * Add move to gameInfo 
     * Perform move on GUI, including promoting Piece
     * Redirect flow to opposing Turn and perform pre-move 
     * Add the most recently added move to notationTable
     * 
     * @param tile to be moved to
     * @param promotionTo to be promoted to
     */
    private void movePromotion(Tile tile, Piece promotionTo) {
        int oldNot = gameInfo.getMoveNum();
        board.getBlackKing().getTile().setOffCheck();
        board.getWhiteKing().getTile().setOffCheck();
        inCheck = false;
        Tile selected = selectedTile;
        Piece taken = tile.getPiece();
        gameInfo.makeMovePromotion(selected,tile,promotionTo);
        selected.movePiece(tile);
        if(taken != null) {
            removeTaken(taken);
            gameInfo.setRecentCapture();
        }
        promotionDelay(tile.getPiece(),promotionTo);
        tile.setPiece(promotionTo);
        if (isWhiteTurn()) {
            board.getBlackNotKing().remove(taken);
            board.getWhiteNotKing().add(promotionTo);
        } else {
            board.getWhiteNotKing().remove(taken);
            board.getBlackNotKing().add(promotionTo);
        }
        isWhiteTurn = !isWhiteTurn;
        gameInfo.setRecentPromotion(promotionTo);
        preMove();
        notationTable.addLastToGUI();
        notationTable.selectEntry(gameInfo.getMoveNum(), oldNot);
    }
    
    /**
     * Private method to perform a Standard move on Game and redirect
     * Game flow back to GUI listening
     * 
     * Set Game out of Check
     * Identify type of Castle to be performed
     *  Add move to gameInfo 
     *  Perform move on GUI
     * Redirect flow to opposing Turn and perform pre-move 
     * Add the most recently added move to notationTable
     * 
     * @param forWhite, the Color side castle is performed on top-bottom
     * @param kingSide, the Piece side castle is performed on left-right
     */
    private void moveCastle(boolean forWhite, boolean kingSide) {
        int oldNot = gameInfo.getMoveNum();
        board.getBlackKing().getTile().setOffCheck();
        board.getWhiteKing().getTile().setOffCheck();
        inCheck = false;
        if(forWhite) {
            if(kingSide) {
                //king side white castle
                board.getTiles()[LOWER_BOUNDARY][UPPER_BOUNDARY].getPiece().toFront(); //rook to front
                gameInfo.makeMoveCastleKingSide(board.getWhiteKing(), board.getTiles()[LOWER_BOUNDARY][UPPER_BOUNDARY].getPiece());
                board.getWhiteKing().getTile().movePiece(board.getTiles()[LOWER_BOUNDARY][UPPER_BOUNDARY-1]);
                board.getTiles()[LOWER_BOUNDARY][UPPER_BOUNDARY].movePiece(board.getTiles()[LOWER_BOUNDARY][UPPER_BOUNDARY-2]);
            } else {
                //queen side white castle
                board.getTiles()[LOWER_BOUNDARY][LOWER_BOUNDARY].getPiece().toFront(); //rook to front
                gameInfo.makeMoveCastleQueenSide(board.getWhiteKing(), board.getTiles()[LOWER_BOUNDARY][LOWER_BOUNDARY].getPiece());
                board.getWhiteKing().getTile().movePiece(board.getTiles()[LOWER_BOUNDARY][LOWER_BOUNDARY+2]);
                board.getTiles()[LOWER_BOUNDARY][LOWER_BOUNDARY].movePiece(board.getTiles()[LOWER_BOUNDARY][LOWER_BOUNDARY+3]);
            }
        } else {
            if(kingSide) {
                //king side black castle
                board.getTiles()[UPPER_BOUNDARY][UPPER_BOUNDARY].getPiece().toFront(); //rook to front
                gameInfo.makeMoveCastleKingSide(board.getBlackKing(), board.getTiles()[UPPER_BOUNDARY][UPPER_BOUNDARY].getPiece());
                board.getBlackKing().getTile().movePiece(board.getTiles()[UPPER_BOUNDARY][UPPER_BOUNDARY-1]);
                board.getTiles()[UPPER_BOUNDARY][UPPER_BOUNDARY].movePiece(board.getTiles()[UPPER_BOUNDARY][UPPER_BOUNDARY-2]);
            } else {
                //queen side black castle
                board.getTiles()[UPPER_BOUNDARY][LOWER_BOUNDARY].getPiece().toFront(); //rook to front
                gameInfo.makeMoveCastleQueenSide(board.getBlackKing(), board.getTiles()[UPPER_BOUNDARY][LOWER_BOUNDARY].getPiece());
                board.getBlackKing().getTile().movePiece(board.getTiles()[UPPER_BOUNDARY][LOWER_BOUNDARY+2]);
                board.getTiles()[UPPER_BOUNDARY][LOWER_BOUNDARY].movePiece(board.getTiles()[UPPER_BOUNDARY][LOWER_BOUNDARY+3]);
            }
        }
        isWhiteTurn = !isWhiteTurn;
        preMove();
        notationTable.addLastToGUI();
        notationTable.selectEntry(gameInfo.getMoveNum(), oldNot);
    }
    
    /**
     * Remove a piece to be taken from the Board GUI with a delay
     * @param taken the piece to be removed
     */
    private void removeTaken(Piece taken) {
        if (isWhiteTurn()) {
            board.getBlackNotKing().remove(taken);
        } else {
            board.getWhiteNotKing().remove(taken);
        }
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    FadeTransition ft = new FadeTransition(Duration.millis(150), taken);
                    ft.setFromValue(1.0);
                    ft.setToValue(0.1);
                    ft.play();
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded((WorkerStateEvent event) -> getBoardGUI().getChildren().remove(taken));
        new Thread(sleeper).start();
    }
    
    /**
     * Promote a piece on GUI with a delay
     * @param oldPiece the piece to be promoted
     * @param newPiece the piece to be promoted to
     */
    private void promotionDelay(Piece oldPiece, Piece newPiece) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded((WorkerStateEvent event) -> {
            getBoardGUI().getChildren().remove(oldPiece);
            getBoardGUI().getChildren().add(newPiece);
        });
        new Thread(sleeper).start();
    }

    /**
     * Refer to GameInfo::canCastle for more details
     * @param king the king to check for
     * @param kingSide, the side of the rook to check for
     * @return canCastle
     */
    public boolean canCastle(Piece king, boolean kingSide) {
        return gameInfo.canCastle(king.isWhite(), kingSide);
    }
    
    /**
     * Copy the current position FEN to clipboard
     */
    public void copyFENToClip() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection strSel = new StringSelection(gameInfo.getMoveFEN());
        clipboard.setContents(strSel, null);
    }
    
    /**
     * Save the Game as a PGN file
     * Opens up a new window to allow for user's selection of file/path
     */

    public void savePGNAsFile() {
        String result;
        if(finished) {
            if(isWhiteTurn()) {
                result = "[Result 0-1]";
            } else {
                result = "[Result 1-0]";
            }
        } else {
            result = "[Result *]";
        }
        String PGN = gameInfo.getGamePGN(result);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game as PGN");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text", "*.txt"),
                new FileChooser.ExtensionFilter("Notation", "*.pgn") 
        );
        Stage newWindow = new Stage();
        newWindow.setResizable(false);

        newWindow.setX(stage.getX() + stage.getWidth()/3);
        newWindow.setY(stage.getY() + stage.getHeight()/3);
        newWindow.initOwner(stage);
        newWindow.initModality(Modality.APPLICATION_MODAL); 
        File file = fileChooser.showSaveDialog(newWindow);
        if(file != null) {
            try {
                if (!file.createNewFile()) {
                    file.delete();
                    file.createNewFile();
                } 
                writeStringToFile(PGN, file);
            } catch (IOException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Writes a string to a file
     * @param string, the string to write
     * @param file, the file to write to
     */
    public static void writeStringToFile(String string, File file) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file,false))) {
                writer.write(string);
            }
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
     /**
     * Re-render the Game board GUI
     */
    public void reRenderBoard() {
        root.getChildren().remove(getBoardGUI());
        if(whiteBoardPosition) {
            board.initWhiteBoard(gameInfo.getBoardByNumber(gameInfo.getMoveNum()),this);
        } else {
            board.initBlackBoard(gameInfo.getBoardByNumber(gameInfo.getMoveNum()),this);
        }
        AnchorPane.setTopAnchor(getBoardGUI(), topBarHeight + 20.0);
        AnchorPane.setLeftAnchor(getBoardGUI(), 15.0);
        root.getChildren().add(getBoardGUI());
        preMove();
    }

    /**
     * Flips the Game Board GUI
     */
    public void flipBoardGUI() {
        root.getChildren().remove(getBoardGUI());
        if(whiteBoardPosition) {
            board.initBlackBoard(gameInfo.getBoardByNumber(gameInfo.getMoveNum()),this);
        } else {
            board.initWhiteBoard(gameInfo.getBoardByNumber(gameInfo.getMoveNum()),this);
        }
        AnchorPane.setTopAnchor(getBoardGUI(), topBarHeight + 20.0);
        AnchorPane.setLeftAnchor(getBoardGUI(), 15.0);
        root.getChildren().add(getBoardGUI());
        whiteBoardPosition = !whiteBoardPosition;
        preMove();
    }
    
    /**
     * Take Back the most recently made move from Game Info and re-write information
     * into Board and GUI and update gameInfo position
     */
    public void takeBackMove() {
        int oldNum = gameInfo.getMoveNum();
        if(gameInfo.canTakeBack()) {
            root.getChildren().remove(getBoardGUI());
            if(whiteBoardPosition) {
                board.initWhiteBoard(gameInfo.getBeforeLastBoard(),this);
            } else {
                board.initBlackBoard(gameInfo.getBeforeLastBoard(),this);
            }
            AnchorPane.setTopAnchor(getBoardGUI(), topBarHeight + 20.0);
            AnchorPane.setLeftAnchor(getBoardGUI(), 15.0);
            root.getChildren().add(getBoardGUI());
            gameInfo.takeBackMove();
            isWhiteTurn = gameInfo.getMoveNum() % 2 != 0;
            preMove();
            notationTable.removeLastFromGUI();
            if(oldNum < gameInfo.getMoveNum()) {
                notationTable.selectEntry(gameInfo.getMoveNum(),oldNum);
            } else {
                notationTable.selectEntry(gameInfo.getMoveNum());
            }  
            moveReadyState = true;
            finished = false;
            notationTable.removeFinishedMessage();
            gameResult = IN_PROGRESS;
        }      
    }
    
    /**
     * Render gameInfo's board to the left of the current board and update gameInfo position
     */
    public void goLeft() {
        if(gameInfo.canGoLeft()) {
            root.getChildren().remove(getBoardGUI());
            if (whiteBoardPosition) {
                board.initWhiteBoard(gameInfo.getBoardByNumber(gameInfo.getMoveNum()-1),this);
            } else {
                board.initBlackBoard(gameInfo.getBoardByNumber(gameInfo.getMoveNum()-1),this);
            }
            AnchorPane.setTopAnchor(getBoardGUI(), topBarHeight + 20.0);
            AnchorPane.setLeftAnchor(getBoardGUI(), 15.0);
            root.getChildren().add(getBoardGUI());
            gameInfo.goLeft();
            isWhiteTurn = gameInfo.getMoveNum() % 2 != 0;
            preMove();
            notationTable.selectEntry(gameInfo.getMoveNum(), gameInfo.getMoveNum()+1);
            moveReadyState = false;
        }
    }
    
    /**
     * Render gameInfo's board to the right of the current board and update gameInfo position
     */
    public void goRight() {
        if(gameInfo.canGoRight()) {
            root.getChildren().remove(getBoardGUI());
            if (whiteBoardPosition) {
                board.initWhiteBoard(gameInfo.getBoardByNumber(gameInfo.getMoveNum()+1),this);
            } else {
                board.initBlackBoard(gameInfo.getBoardByNumber(gameInfo.getMoveNum()+1),this);
            }
            AnchorPane.setTopAnchor(getBoardGUI(), topBarHeight + 20.0);
            AnchorPane.setLeftAnchor(getBoardGUI(), 15.0);
            root.getChildren().add(getBoardGUI());
            gameInfo.goRight();            
            isWhiteTurn = gameInfo.getMoveNum() % 2 != 0;
            preMove();
            notationTable.selectEntry(gameInfo.getMoveNum(), gameInfo.getMoveNum()-1);
            moveReadyState = gameInfo.getMoveNum() == gameInfo.getMoveCount()-1;
        }
    }
    
    /**
     * Render the starting gameInfo board position and update gameInfo position
     */
    public void goFarLeft() {
        if(gameInfo.canGoLeft()) {
            int oldNum = gameInfo.getMoveNum();
            root.getChildren().remove(getBoardGUI());
            if (whiteBoardPosition) {
                board.initWhiteBoard(GameInfo.INITIAL_BOARD,this);
            } else {
                board.initBlackBoard(GameInfo.INITIAL_BOARD,this);
            }
            AnchorPane.setTopAnchor(getBoardGUI(), topBarHeight + 20.0);
            AnchorPane.setLeftAnchor(getBoardGUI(), 15.0);
            root.getChildren().add(getBoardGUI());
            gameInfo.goFarLeft();
            isWhiteTurn = gameInfo.getMoveNum() % 2 != 0;
            preMove();
            notationTable.selectEntry(-1, oldNum);
            moveReadyState = false;
        }
    }
    
    /**
     * Render the most recent gameInfo board position and update gameInfo position
     */
    public void goFarRight() {
        if(gameInfo.canGoRight()) {
            int oldNum = gameInfo.getMoveNum();
            root.getChildren().remove(getBoardGUI());
            if (whiteBoardPosition) {
                board.initWhiteBoard(gameInfo.getLastBoard(),this);
            } else {
                board.initBlackBoard(gameInfo.getLastBoard(),this);
            }
            AnchorPane.setTopAnchor(getBoardGUI(), topBarHeight + 20.0);
            AnchorPane.setLeftAnchor(getBoardGUI(), 15.0);
            root.getChildren().add(getBoardGUI());
            gameInfo.goFarRight();            
            isWhiteTurn = gameInfo.getMoveNum() % 2 != 0;
            preMove();
            notationTable.selectEntry(gameInfo.getMoveCount()-1, oldNum);
            moveReadyState = gameInfo.getMoveNum() == gameInfo.getMoveCount()-1;
        }
    }
    
    /**
     * Go to a specific board position in the gameInfo and update gameInfo position
     * @param num to go to on game info
     */
    public void goTo(int num) {
        if(num != gameInfo.getMoveNum()) {
            int oldNum = gameInfo.getMoveNum();
            root.getChildren().remove(getBoardGUI());
            if (whiteBoardPosition) {
                board.initWhiteBoard(gameInfo.getBoardByNumber(num),this);
            } else {
               board.initBlackBoard(gameInfo.getBoardByNumber(num),this);
            }
            AnchorPane.setTopAnchor(getBoardGUI(), topBarHeight + 20.0);
            AnchorPane.setLeftAnchor(getBoardGUI(), 15.0);
            root.getChildren().add(getBoardGUI());
            gameInfo.goTo(num); 
            isWhiteTurn = gameInfo.getMoveNum() % 2 != 0;
            preMove();
            notationTable.selectEntry(gameInfo.getMoveNum(), oldNum);
            moveReadyState = gameInfo.getMoveNum() == gameInfo.getMoveCount()-1;
        }
    }
    
    /**
     * Reset the game by constructing a new game of the same parameters 
     */
    public void resetGame() {
        if(timerType == NO_TIMER) {
            stage.getScene().setRoot(app.createPlayPane(stage));
        } else {
            stage.getScene().setRoot(app.createPlayPaneTimed(stage, TIMER_INFO[timerType][0], 
                    TIMER_INFO[timerType][1],timerType));
        }
    }
    
    /**
     * Construction of HBox Game GUI component
     * @return HBox to be constructed and returned
    */
    public HBox constructTopBorder() {
        HBox buttons = new HBox();
        buttons.setId("darkborder");
        buttons.setMinSize(app.getWidth(),1);
        buttons.setMaxSize(app.getWidth(),1);
        return buttons;
    }
    
    /**
     * Construction of HBox Game GUI component
     * @return HBox to be constructed and returned
    */
    public HBox constructTopBar() {
        HBox buttons = new HBox();
        buttons.setId("topbar");
        buttons.getChildren().addAll(constructNewButton(),constructResetButton(),
                constructBorder(), constructFlipButton(), constructBorder(), 
                constructExportPGN(),constructExportFEN(),constructBorder(),
                constructAppearanceButton());
        buttons.setSpacing(3);
        buttons.setMinSize(app.getWidth(),topBarHeight);
        buttons.setMaxSize(app.getWidth(),topBarHeight);
        buttons.setPadding(new Insets(2,35,1,25));
        return buttons;
    }
    
    /**
     * Construction of HBox Game GUI component
     * @return HBox to be constructed and returned
    */
    public HBox constructButtonPanel() {
        HBox buttons = new HBox();
        buttons.setId("topborder");
        buttons.setMinSize(barWidth,elementHeight);
        buttons.setMaxSize(barWidth,elementHeight);
        buttons.setSpacing(25*app.getScale());
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(constructLeftButton(),
                constructBackButton(),constructRightButton());
        return buttons;
    }
    
    /**
     * Construction of HBox Game GUI component
     * @return HBox to be constructed and returned
    */
    public HBox constructBorder() {
        HBox borderContainer = new HBox();
        borderContainer.setMinSize(1+20,topBarHeight);
        borderContainer.setMaxSize(1+20,topBarHeight);
        VBox border = new VBox();
        border.setId("sideborder");
        border.setMinSize(1,topBarHeight-10);
        border.setMaxSize(1,topBarHeight-10);
        borderContainer.setPadding(new Insets(1,10,1,10));
        borderContainer.getChildren().addAll(border);
        return borderContainer;
    }   
    
    /**
     * Construction of GUI Button component
     * @return Button to be returned
    */
    public Button constructExportPGN() {
        Button exportButton = new Button("Save Game");
        ImageView image = new ImageView(new Image("/resources/filesymbol.png"));
        image.setFitHeight(35*app.getScale());
        image.setFitWidth(35*app.getScale());
        exportButton.setGraphic(image);
        exportButton.setGraphicTextGap(0);
        exportButton.setFont(new Font("Roboto",16*app.getScale()));
        exportButton.setMinSize(100*app.getScale(), 63*app.getScale());
        exportButton.setFocusTraversable(false);
        exportButton.setId("barbutton");
        exportButton.setPadding(Insets.EMPTY);
        exportButton.setContentDisplay(ContentDisplay.TOP);
        exportButton.setOnAction((event)-> savePGNAsFile());
        return exportButton;
    }
    
    /**
     * Construction of GUI Button component
     * @return Button to be returned
    */
    public Button constructExportFEN() {
        Button exportButton = new Button("Copy Board");
        ImageView image = new ImageView(new Image("/resources/filesymbol.png"));
        image.setFitHeight(35*app.getScale());
        image.setFitWidth(35*app.getScale());
        exportButton.setGraphic(image);
        exportButton.setGraphicTextGap(0);
        exportButton.setFont(new Font("Roboto",16*app.getScale()));
        exportButton.setMinSize(100*app.getScale(), 63*app.getScale());
        exportButton.setFocusTraversable(false);
        exportButton.setId("barbutton");
        exportButton.setPadding(Insets.EMPTY);
        exportButton.setContentDisplay(ContentDisplay.TOP);
        exportButton.setOnAction((event)-> copyFENToClip());
        return exportButton;
    }
    
    /**
     * Construction of GUI Button component
     * @return Button to be returned
    */
    public Button constructFlipButton() {
        Button flipButton = new Button("Flip Board");
        ImageView image = new ImageView(new Image("/resources/blueflip.png"));
        image.setFitHeight(35*app.getScale());
        image.setFitWidth(35*app.getScale());
        flipButton.setGraphic(image);
        flipButton.setGraphicTextGap(0);
        flipButton.setFont(new Font("Roboto",16*app.getScale()));
        flipButton.setMinSize(80*app.getScale(), 63*app.getScale());
        flipButton.setFocusTraversable(false);
        flipButton.setId("barbutton");
        flipButton.setPadding(Insets.EMPTY);
        flipButton.setContentDisplay(ContentDisplay.TOP);
        flipButton.setOnAction((event)-> flipBoardGUI());
        return flipButton;
    }
    
    /**
     * Construction of GUI Button component
     * @return Button to be returned
    */
    public Button constructResetButton() {
        Button resetButton = new Button("Reset");
        ImageView image = new ImageView(new Image("/resources/greenflip.png"));
        image.setFitHeight(35*app.getScale());
        image.setFitWidth(35*app.getScale());
        resetButton.setGraphic(image);
        resetButton.setGraphicTextGap(0);
        resetButton.setFont(new Font("Roboto",16*app.getScale()));
        resetButton.setMinSize(73*app.getScale(), 63*app.getScale());
        resetButton.setFocusTraversable(false);
        resetButton.setId("barbutton");
        resetButton.setPadding(Insets.EMPTY);
        resetButton.setContentDisplay(ContentDisplay.TOP);
        resetButton.setOnAction((event)-> resetGame());
        return resetButton;
    }
    
    /**
     * Construction of GUI Button component
     * @return Button to be returned
    */
    public Button constructNewButton() {
        Button newButton = new Button("New");
        ImageView image = new ImageView(new Image("/resources/boardsparkle.png"));
        image.setFitHeight(35*app.getScale());
        image.setFitWidth(35*app.getScale());
        newButton.setGraphic(image);
        newButton.setGraphicTextGap(0);
        newButton.setFont(new Font("Roboto",16*app.getScale()));
        newButton.setMinSize(73*app.getScale(), 63*app.getScale());
        newButton.setFocusTraversable(false);
        newButton.setId("barbutton");
        newButton.setPadding(Insets.EMPTY);
        newButton.setContentDisplay(ContentDisplay.TOP);
        newButton.setOnAction((event)-> app.newGame(stage));
        return newButton;
    }
    
    /**
     * Construction of GUI Button component
     * @return Button to be returned
    */
    public Button constructAppearanceButton() {
        Button newButton = new Button("Appearance");
        ImageView image = new ImageView(new Image("/resources/whitepawn.png"));
        image.setFitHeight(35*app.getScale());
        image.setFitWidth(35*app.getScale());
        newButton.setGraphic(image);
        newButton.setGraphicTextGap(0);
        newButton.setFont(new Font("Roboto",16*app.getScale()));
        newButton.setMinSize(90*app.getScale(), 63*app.getScale());
        newButton.setFocusTraversable(false);
        newButton.setId("barbutton");
        newButton.setPadding(Insets.EMPTY);
        newButton.setContentDisplay(ContentDisplay.TOP);
        newButton.setOnAction((event)-> app.openAppearancePanel(stage, this));
        return newButton;
    }
    
    /**
     * Construction of GUI Button component
     * @return Button to be returned
    */
    public Button constructLeftButton() {
        Button leftButton = new Button();
        ImageView image = new ImageView(new Image("/resources/leftarrow.png"));
        image.setFitHeight(35*app.getScale());
        image.setFitWidth(35*app.getScale());
        leftButton.setGraphic(image);
        leftButton.setMinSize(50*app.getScale(), 50*app.getScale());
        leftButton.setMaxSize(50*app.getScale(), 50*app.getScale());
        leftButton.setFocusTraversable(false);
        leftButton.setId("boardbutton");
        leftButton.setOnAction((event)-> goLeft());
        return leftButton;
    }
    
    /**
     * Construction of GUI Button component
     * @return Button to be returned
    */
    public Button constructRightButton() {
        Button rightButton = new Button();
        ImageView image = new ImageView(new Image("/resources/rightarrow.png"));
        image.setFitHeight(35*app.getScale());
        image.setFitWidth(35*app.getScale());
        rightButton.setGraphic(image);
        rightButton.setMinSize(50*app.getScale(), 50*app.getScale());
        rightButton.setMaxSize(50*app.getScale(), 50*app.getScale());
        rightButton.setFocusTraversable(false);
        rightButton.setId("boardbutton");
        rightButton.setOnAction((event)-> goRight());
        return rightButton;
    }
    
    /**
     * Construction of GUI Button component
     * @return Button to be returned
    */
    public Button constructBackButton() {
        Button backButton = new Button();
        ImageView image = new ImageView(new Image("/resources/arrowsmall.png"));
        image.setFitHeight(25*app.getScale());
        image.setFitWidth(25*app.getScale());
        backButton.setGraphic(image);
        backButton.setMinSize(50*app.getScale(), 50*app.getScale());
        backButton.setMaxSize(50*app.getScale(), 50*app.getScale());
        backButton.setFocusTraversable(false);
        backButton.setId("boardbutton");
        backButton.setOnAction((event)-> takeBackMove());
        return backButton;
    }
    
    /**
     * Construction of the Game scoreboard 
     * @return VBox representing scoreboard
    */
    public VBox constructScoreBoard() {
        VBox sidebar = new VBox();
        sidebar.setId("scoreoutsets");
        sidebar.setMinSize(barWidth, elementHeight + scoreBoardHeight + elementHeight);
        sidebar.setMaxSize(barWidth, elementHeight + scoreBoardHeight + elementHeight);
        sidebar.setAlignment(Pos.CENTER);
        HBox titles = constructTitles();
        HBox bottomButtons = constructButtonPanel();
        setUpNotationGUI();
        HBox notationHBox = new HBox();
        notationHBox.getChildren().add(notationTable);
        notationHBox.setPadding(new Insets(0,barWidth*0.1,0,barWidth*0.1));
        sidebar.getChildren().addAll(titles,notationHBox, bottomButtons);
        return sidebar;
    }
    
    public final void setUpNotationGUI() {
        notationTable = constructNotationTable();
    }
    
    /**
     * Constructs Notation Board to display Game notation
     * @return NotationBoard represents Table with Notation liked to gameInfo
     */
    public final NotationBoard constructNotationTable() {
        VBox vertical = new VBox();
        NotationBoard table = new NotationBoard(gameInfo.getMoves(), vertical, this, app);
        table.setId("scrollborder");
        table.setFocusTraversable(false);
        double width = barWidth*0.8;
        vertical.setId("scrollpanebg");
        vertical.setPadding(new Insets(25,20*app.getScale(),25,20*app.getScale()));
        vertical.setFocusTraversable(false);
        table.setContent(vertical);
        table.setMinSize(width, scoreBoardHeight);
        table.setMaxSize(width, scoreBoardHeight);
        table.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        table.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        table.fitToWidthProperty().set(true);
        table.fitToHeightProperty().set(true);
        return table;
    }
    
    /**
     * Construction of HBox Game GUI component
     * @return HBox to be constructed and returned
    */
    public final HBox constructTitles() {
        HBox titles = new HBox();
        titles.setMinSize(barWidth, elementHeight);
        titles.setMaxSize(barWidth, elementHeight);
        Label white = new Label(" White");
        white.setFont(new Font("Roboto", 26*app.getScale()));
        white.setAlignment(Pos.CENTER);
        white.setMinSize(barWidth/2, elementHeight);
        white.setMaxSize(barWidth/2, elementHeight);
        white.setId("lightborderright");
        white.setGraphic(whiteCircle);
        Label black = new Label(" Black");
        black.setFont(new Font("Roboto", 26*app.getScale()));
        black.setAlignment(Pos.CENTER);
        black.setMinSize(barWidth/2, elementHeight);
        black.setMaxSize(barWidth/2, elementHeight);
        black.setId("lightborderleft");
        black.setGraphic(blackCircle);
        titles.getChildren().addAll(white, black);
        return titles;
    }

}

