/*
 * Abstract representation of a GUI chess piece for the Board
 * 7/4/20
 */
package gui;

import java.util.ArrayList;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 *
 * @author Joseph
 */
public abstract class Piece extends StackPane {
    
    /*
     * Attack Pattern Key: 
     * Empty - O 
     * Attacking - X 
     * This Piece - other symbol 
     */
    
    public static final double TILE_SIZE = 100*ChessLite.SCALE;
    private boolean closeable = false; //will a mouse release close the piece
    private boolean drag; //is the piece currently being dragged
    private double mouseX, mouseY; //position of mouse
    private double oldX, oldY; 
    private double initialX, initialY; //initial postion when mouse is clicked
    private final boolean isWhite;
    private Tile tile; //tile piece is on
    private final ArrayList<Tile> avaliable = new ArrayList<>(); //avaliable tiles of piece used for render

    public boolean isCloseable() {
        return closeable;
    }

    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }

    public ArrayList<Tile> getAvaliable() {
        return avaliable;
    }
    
    public Tile getTile() {
        return tile;
    }

    public final Game getController() {
        return tile.getController();
    }

    public void setOldX(double oldX) {
        this.oldX = oldX;
    }

    public void setOldY(double oldY) {
        this.oldY = oldY;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }
    
    public boolean isQueen() {
        return false;
    }
    
    public boolean isRook() {
        return false;
    }
    
    public boolean isBishop() {
        return false;
    }
    
    public boolean isKing() {
        return false;
    }
    
    public boolean isKnight() {
        return false;
    }
    
    public boolean isPawn() {
        return false;
    }

    /**
     * Constructs a piece
     * 
     * Sets the press action, the drag action, and the release action of the piece
     * used for GUI interactivity and to add Selectable to main GUI
     * 
     * @param isWhiteIn side of the piece
     * @param tileIn tile piece belongs to
    */
    public Piece(boolean isWhiteIn, Tile tileIn) {
        this.setStyle("-fx-cursor: hand;");
        isWhite = isWhiteIn;
        tile = tileIn;
        moveTo(tile);
        setOnMousePressed(e -> {
            doRender();
            toFront();
        });
        setOnMouseDragged((e)->{
            boolean isWhiteTurn = getController().isWhiteTurn();
            if(isWhiteTurn == isWhite() && !getController().isFinished() && getController().isMoveReady()) {
                if(drag) {
                    double x = e.getSceneX() - mouseX + initialX;
                    double y = e.getSceneY() - mouseY + initialY;
                    setTranslateX(x);
                    setTranslateY(y);
                } else {
                    drag = true;
                    toFront();
                    mouseX = e.getSceneX();
                    mouseY = e.getSceneY();
                    Bounds pieceBounds = this.localToScene(this.getBoundsInLocal());
                    double x = e.getSceneX() - (pieceBounds.getMinX() + (Tile.TILE_SIZE / 2)) + oldX;
                    double y = e.getSceneY() - (pieceBounds.getMinY() + (Tile.TILE_SIZE / 2)) + oldY;
                    initialX = x;
                    initialY = y;
                    setTranslateX(x);
                    setTranslateY(y);
                }
            }
        });
        setOnMouseReleased((e)->{
            if(drag) {
                double x = e.getSceneX() - mouseX + initialX;
                double y = e.getSceneY() - mouseY + initialY;
                for(Selectable selectable : getController().getSelectables()) {
                    if(isSelectableCollision(e.getSceneX(), e.getSceneY(), selectable)) {
                        oldX = x;
                        oldY = y;
                        selectable.move();
                        closeable = false;
                        return;
                    }
                }
                setTranslateX(oldX);
                setTranslateY(oldY);
                closeable = true;
            } else {
                if (closeable) {
                    closeable = false;
                    getController().clearSelectables();
                } else {
                    closeable = true;
                }
            }
            drag = false;
        });
    }
    
    /**
     * Calculates whether or not mouse position is within the bounds of a selectable
     * @param x of mouse
     * @param y of mouse
     * @param selectable to check
     * @return true if within bounds 
     */
    private boolean isSelectableCollision(double x, double y, Selectable selectable) {
        Bounds selectableBounds = selectable.localToScene(selectable.getBoundsInLocal());
        return selectableBounds.contains(x, y);
    }

    /**
     *  returns the Piece notation
     * @return notation as String
     */
    public abstract String getNotation();
    
    /**
     *  returns the Piece info code for ByteBoards
     * @return info code as byte
     */
    public abstract byte getInfoCode();
    
    /**
     *  returns the value of a piece
     * @return value as integer
     */
    public abstract int getValue();

    /**
     * Move a piece to a tile (intended to be used behind the scenes)
     * @param tile to move to
     */
    public final void moveTo(Tile tile) {
        oldX = tile.getxReal();
        oldY = tile.getyReal();
        setTranslateX(oldX);
        setTranslateY(oldY);
    }
    
    /**
     * Move to piece to a tile with GUI delay (intended for user to see)
     * @param tile to move to
     */
    public final void moveToSlowly(Tile tile) {
        double x = tile.getxReal() - oldX;
        double y = tile.getyReal() - oldY;
        int time = 150;
        if(distance(x,y) < ((double)Tile.TILE_SIZE)/2) {
            time = 5;
        }
        TranslateTransition tt = new TranslateTransition(Duration.millis(time), this);
        tt.setByX(x);
        tt.setByY(y);
        tt.play();
        oldX = tile.getxReal();
        oldY = tile.getyReal();
    }
    
    /**
     * Calculates the distance between 0 and a position
     * @param x of position
     * @param y of position
     * @return the distance
     */
    public double distance(double x, double y) {
        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }
    
    /**
     * Returns whether a given location is within bounds
     * @param row of postion
     * @param col of position
     * @return true if in bounds
     */
    public final boolean withinBounds(int row, int col) {
        return (row < 8 && row > -1) && (col < 8 && col > -1);
    }
    
    /**
     * Performs rendering of Selectable to the Game GUI the piece belongs to
     * 
     * Establishes closable
     * Un-selects selectable from Game
     * Selects this tile in Game
     * Render selectable based on Game ready position
     * 
     */
    protected void doRender() {
        boolean isWhiteTurn = getController().isWhiteTurn();
        Tile last = getController().getSelectedTile();
        if(last != null && last.getPiece() != this) {
            last.getPiece().setCloseable(false);
        }
        getController().clearSelectables();
        getTile().setSelected();
        getController().setSelectedTile(getTile());
        if (isWhiteTurn == isWhite() && getController().isMoveReady() && !getController().isFinished()
                && getController().canRender()) {
            renderSelectables();
        } else if(isWhiteTurn == isWhite() && !getController().isMoveReady() && getController().canRender()) {
            renderVisualizables();
        }
    }
    
    /**
     * Render a list of selectable
     */
    protected void renderSelectables() {
        avaliable.forEach((avaliableTile) -> {
            getController().addSelectable(avaliableTile);
        });
    }
    
    /**
     * Render a list of visuals
     */
    protected void renderVisualizables() {
        avaliable.forEach((avaliableTile) -> {
            getController().addVisualizable(avaliableTile);
        });
    }
    
    /**
     * Calculates and returns a list of any other pieces of the same 
     * type that can move to the same location on the board
     * @param location the location (the piece is to be moved to later)
     * @return ArrayList of locations other pieces {row,column}
     */
    public abstract ArrayList<int[]> calcCommonPieceLocations(int[] location);
    
    /**
     * Calculates the avaliable moves a piece can make on its Game
     * Board and adds them to attributes
     */
    public abstract void pieceAvaliableMoves();
    
    /**
     * Calculates the avaliable moves a piece can make on its Game
     * Board and adds them to attributes
     * Adds the additional functionality over Piece::pieceAvaliableMoves by checking
     * if the tiles are within the whiteList
     * @param whiteListed, whiteList used to filter out moves
     */
    public abstract void pieceAvaliableMoves(ArrayList<Tile> whiteListed);
    
    /**
     * Calculates the avaliable moves for a piece (utilizes access to board through
     * tile game attribute)
     * 
     * Clear avaliable moves 
     * Calculate pinned whiteList 
     * Access whiteLists from Board through Game 
     * If not King and piece is Pinned and tiles attacking king less than 1 
     * calculate moves with pinned whiteList 
     * else If not King and tiles attacking king equals 1 and piece is not Pinned 
     * calculate moves with attacking king whiteList 
     * else If tiles attacking king is empty or is piece is King 
     * calculate moves without whiteList 
     */
    public void calcAvaliableMoves() {
        avaliable.clear();
        ArrayList<Tile> attackingKing = getController().getAttackingKing();
        ArrayList<Tile> pinnedWhiteList = new ArrayList<>();
        ArrayList<Tile> attackWhiteListed = getController().getAttackWhiteListed();
        Piece king = getController().isWhiteTurn()
                ? getController().getWhiteKing() : getController().getBlackKing();
        boolean isPinned = isPinned(pinnedWhiteList, king);
        if (!isKing() && isPinned && attackingKing.size() < 1) { 
            pieceAvaliableMoves(pinnedWhiteList);
        } else if (!isKing() && attackingKing.size() == 1 && !isPinned) { 
            pieceAvaliableMoves(attackWhiteListed);
        } else if (attackingKing.isEmpty() || isKing()) {
            pieceAvaliableMoves();
        }
    }
    
    /**
     * Calculates the whiteListed tiles if a piece is pinned to ally King to
     * an ArrayList, and returns whether the piece is pinned
     * 
     * Algorithm is terminated upon 3 conditions:
     * This piece is a king (returns false as  King cannot be pinned to itself)
     * The vector between the king and this piece cannot be normalized 
     * (there is no diagonal or horizontal trajectory)
     * If Neither is true, King loops through each tile following the normalized trajectory
     * between itself and this piece until it reaches a potential pinning piece or the bounds
     * of the board while add adding each tile to the WhiteList (a pinned piece may move between 
     * pinning piece and King and remain pinned) If a potential pinning piece is reached the piece 
     * pins if only 2 pieces (itself and this piece) are found from the King to itself
     * and it has attacking trajectory to the King (such as horizontal for a Rook)
     * Algorithm terminates and returns whether or not a pin was found
     * 
     * @param whiteList, the whiteList to write to
     * @param king to compare pin
     * @return whether or not piece is pinned
     */
    public boolean isPinned(ArrayList<Tile> whiteList, Piece king) {
        if(isKing()) {
            return false;
        }
        Tile[][] tiles = getController().getTiles();
        int row = king.getTile().getRow(); //starts at king
        int col = king.getTile().getCol();
        if(!normalizeable(getTile().getRow() - king.getTile().getRow(), getTile().getCol() - king.getTile().getCol())) {
            return false;
        }
        int r = normalize(getTile().getRow() - king.getTile().getRow()); //normalized trajectory
        int c = normalize(getTile().getCol() - king.getTile().getCol());
        boolean atOppositePiece = false; //has the algorithm reached an opposite pinning piece
        boolean isPinned = false;
        boolean reachedThisPiece = false; //has the algorithm reached this piece
        int pieceCounter = 0;
        while (!atOppositePiece) { 
            if(withinBounds(row+r, col+c)) {
                Tile t = tiles[row+r][col+c];
                whiteList.add(t);
                if (t.hasPiece()) {
                    pieceCounter++;
                    if(t.getPiece() == this) {
                        reachedThisPiece = true;
                    }
                    if(t.getPiece().isWhite() != isWhite()) {
                        atOppositePiece = true; //algorithm can terminate, a potential pinning piece has been found
                        if(reachedThisPiece && (t.getPiece().isBishop() || t.getPiece().isQueen()) //check if piece can pin
                                && (r != 0) && (c != 0)) {
                            if(pieceCounter == 2) {
                                isPinned = true;
                            }    
                        }
                        if(reachedThisPiece && (t.getPiece().isRook() || t.getPiece().isQueen()) 
                                && (r == 0 || c == 0)) {
                            if(pieceCounter == 2) {
                                isPinned = true;
                            }
                        }
                    }
                } 
            } else {
                atOppositePiece = true;
            }
            if (r > 0) { //travel in the direction of normalized trajectory
                r++;
            } else if (r < 0) {
                r--;
            }
            if (c > 0) {
                c++;
            } else if (c < 0) {
                c--;
            }
        }
        return isPinned;
    }
    
    /**
     * Returns whether or not a value can be normalized with Piece::normalize
     * @param row to be normalized
     * @param col to be normalized
     * @return true if can be normalized
     */
    public boolean normalizeable(int row, int col) {
        return Math.abs(row) == Math.abs(col) || row == 0 || col == 0;
    }
    
    /**
     * Normalizes a value direction within the context of Chess to be quantifiable into
     * the basic system of Rows and Columns. If the number is equal to 0, the 
     * normalized value is 0. Otherwise it is the number divided by its absolute 
     * value, which would retain the sign, specifying the direction of movement.
     * @param num, the number to be normalized
     * @return the normalized value
     */
    public int normalize(int num) {
        return num == 0 ? 0 : num/Math.abs(num);
    }
    
    /**
     * Checks if a tile is WhiteListed
     * @param whitelist to determine avaliable
     * @param tile to check
     * @return if a tile is whiteListed, returns true
     */
    protected boolean whiteListed(ArrayList<Tile> whitelist, Tile tile) {
        return whitelist.contains(tile);
    }

    /**
     * Checks if a piece has any legal moves
     * @return true if avaliable move list is not empty
     */
    protected boolean hasLegalMoves() {
        return !avaliable.isEmpty();
    }
    
}
