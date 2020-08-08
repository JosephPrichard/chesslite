/*
 * A class to represent a Board within a chess Game
 * Manages Board logic and boardGUI
 * A core assumption is that the tiles[][] start from the legal starting position
 * 7/3/20
 */
package gui;

import static gui.Game.HEIGHT;
import static gui.Game.WIDTH;
import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Joseph
 */
public class Board {
    
    public static final String[] NUMBER_TO_LETTER_TABLE = {"a","b","c","d","e","f","g","h"};
    public static final PieceType[] NOTATION_TO_CONSTRUCTOR_TABLE = {PieceType.NoPiece, //convert ByteBoard to objects
        PieceType.WhitePawn, PieceType.WhiteBishop, PieceType.WhiteKnight,
        PieceType.WhiteRook, PieceType.WhiteQueen, PieceType.WhiteKing, 
        PieceType.BlackPawn, PieceType.BlackBishop, PieceType.BlackKnight,
        PieceType.BlackRook, PieceType.BlackQueen, PieceType.BlackKing};
    public static int UPPER_BOUND = 8;
    public static int LOWER_BOUND = -1;
    
    private Pane boardGUI;
    private final Tile[][] tiles = new Tile[WIDTH][HEIGHT]; //board tiles
    private final ArrayList<Piece> blackNotKing = new ArrayList<>(); //pieces
    private final ArrayList<Piece> whiteNotKing = new ArrayList<>();
    private Piece blackKing; //kings
    private Piece whiteKing;
    private final ArrayList<Tile> attackingKing = new ArrayList<>(); //tiles attacking King
    private final ArrayList<Tile> attackWhiteListed = new ArrayList<>(); //avaliable tiles during attack
    private final ArrayList<Tile> kingCanMove = new ArrayList<>(); //avaliable tiles for King
    
    /**
     * Normalizes a value direction within the context of Chess to be quantifiable into
     * the basic system of Rows and Columns. If the number is equal to 0, the 
     * normalized value is 0. Otherwise it is the number divided by its absolute 
     * value, which would retain the sign, specifying the direction of movement.
     * @param num, the number to be normalized
     * @return the normalized value
     */
    public static final int normalize(int num) {
        return num == 0 ? 0 : num/Math.abs(num);
    }
    
    /**
     * Returns whether or not a row and column position is within the bounds of 
     * a chess Board
     * @param row, the row for the position
     * @param col, the column for the position
     * @return true or false
     */
    protected static final boolean withinBounds(int row, int col) {
        return (row < UPPER_BOUND && row > LOWER_BOUND) && (col < UPPER_BOUND && col > LOWER_BOUND);
    }
    
    /**
     * Returns the character notation for a given number 
     * @param num, the number (typically a Column)
     * @return the notation
     */
    public static String getCharacterNotation(int num) {
        return NUMBER_TO_LETTER_TABLE[num];
    }

    public Pane getBoardGUI() {
        return boardGUI;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public ArrayList<Piece> getBlackNotKing() {
        return blackNotKing;
    }

    public ArrayList<Piece> getWhiteNotKing() {
        return whiteNotKing;
    }

    public Piece getBlackKing() {
        return blackKing;
    }

    public Piece getWhiteKing() {
        return whiteKing;
    }

    public ArrayList<Tile> getAttackingKing() {
        return attackingKing;
    }

    public ArrayList<Tile> getAttackWhiteListed() {
        return attackWhiteListed;
    }

    public ArrayList<Tile> getKingCanMove() {
        return kingCanMove;
    }
    
    public Piece getKing(boolean white) {
        return white ? whiteKing : blackKing;
    }
    
    /**
     * Calculates whether or not the pieces of a given Side belonging to the board 
     * have any legal moves.
     * @param white, whether the color of the pieces to be checked is white
     * @return whether or not a Side has any legal moves
     */
    public boolean hasLegalMoves(boolean white) {
        ArrayList<Piece> pieces = white ? whiteNotKing : blackNotKing;
        boolean legal = false;
        for(Piece piece : pieces) {
            if(piece.hasLegalMoves()) {
                legal = true;
            }
        }
        Piece king = white ? whiteKing : blackKing;
        if(king.hasLegalMoves()) {
            legal = true;
        }
        return legal;
    }
    
    /**
     * Calculates the moves for all the pieces of a given Side
     * @param white, whether the pieces to be used are white
     */
    public void calculateMoves(boolean white) {
        Piece king = white ? whiteKing : blackKing;
        attackingKing(king,attackingKing);
        kingCanMove(king,attackingKing,kingCanMove);
        king.calcAvaliableMoves();
        attackWhiteListed(attackingKing,king,attackWhiteListed);
        if(white) {
            whiteNotKing.forEach((piece) -> {
                piece.calcAvaliableMoves();
            });
        } else {
            blackNotKing.forEach((piece) -> {
                piece.calcAvaliableMoves();
            });
        }
    }
    
    /**
     * Calculates whether or not a certain position is in Check, in another sense,
     * whether or not a given King WOULD BE in check if it were on that position.
     * 
     * Calculation is performed by checking all of the possible attacking squares
     * with a Diagonal shaped checking algorithm, a Horizontal one, and an L-shaped
     * one. Each algorithm checks for specific attacking pieces. 
     * If an attacking piece is found the algorithm ends
     * 
     * @param king, the king to check for and perform evaluations on
     * @param row, the row of the position
     * @param col, the column of the position
     * @return whether or not the position is in Check
     */
    public boolean inCheck(Piece king, int row, int col) {
        
        int[][] diagonals = {{1,1},{-1,1},{1,-1},{-1,-1}};
        int[][] horizontals = {{1,0},{-1,0},{0,-1},{0,1}};
        int[][] knightOffsets = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};
        
        //diagonals: queen, bishop, pawn
        for (int[] diagonal : diagonals) {
            int i = 1;
            boolean canContinue = true;
            while (canContinue) {
                if (withinBounds(row+(i*diagonal[0]),col+(i*diagonal[1]))) {
                    Tile tile = tiles[row+(i*diagonal[0])][col+(i*diagonal[1])];
                    if (tile.hasPiece() && tile.getPiece() != king
                            && (tile.getPiece().isWhite() != king.isWhite())) {
                        Piece piece = tile.getPiece();
                        if (piece.isBishop() || piece.isQueen()
                                || (piece.isPawn() && king.isWhite() != piece.isWhite() && i == 1)) {
                            return true;
                        }
                        canContinue = false;
                    }
                    if ((tile.hasPiece() && tile.getPiece() != king)) {
                        canContinue = false;
                    }
                } else {
                    canContinue = false;
                }
                i++;
            }
        }

        //horizontals: rook and queen
        for (int[] horizontal : horizontals) {
            int i = 1;
            boolean canContinue = true;
            while (canContinue) {
                if (withinBounds(row+(i*horizontal[0]),col+(i*horizontal[1]))) {
                    Tile tile = tiles[row+(i*horizontal[0])][col+(i*horizontal[1])];
                    if (tile.hasPiece() && tile.getPiece() != king
                            && (tile.getPiece().isWhite() != king.isWhite())) {
                        Piece piece = tile.getPiece();
                        if (piece.isRook() || piece.isQueen()) {
                            return true;
                        }
                        canContinue = false;
                    }
                    if ((tile.hasPiece() && tile.getPiece() != king)) {
                        canContinue = false;
                    }
                } else {
                    canContinue = false;
                }
                i++;
            }
        }
        
        //L-shaped: knight
        for(int[] offset : knightOffsets) {
            if(withinBounds(row+offset[0],col+offset[1])) {
                Tile tile = tiles[row+offset[0]][col+offset[1]];
                if(tile.hasPiece()) {
                    Piece piece = tile.getPiece();
                    if(piece.isKnight() && tile.getPiece() != king &&  
                            tile.getPiece().isWhite() != king.isWhite()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Calculates the tiles attacking a given King and writes the Tiles to
     * an ArrayList
     * 
     * Calculation is performed by checking all of the possible attacking squares
     * with a Diagonal shaped checking algorithm, a Horizontal one, and an L-shaped
     * one. Each algorithm checks for specific attacking pieces.
     * 
     * @param king, the given King 
     * @param attackingMoves, the list of tiles to be written to (cleared before writing)
     */
    public void attackingKing(Piece king, ArrayList<Tile> attackingMoves) {
        attackingMoves.clear();
        int row = king.getTile().getRow();
        int col = king.getTile().getCol();
        int[][] diagonals = {{1,1},{-1,1},{1,-1},{-1,-1}};
        int[][] horizontals = {{1,0},{-1,0},{0,-1},{0,1}};
        int[][] knightOffsets = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};
        
        //diagonals: queen, bishop, pawn
        for (int[] diagonal : diagonals) {
            int i = 1;
            boolean canContinue = true;
            while (canContinue) {
                if (withinBounds(row+(i*diagonal[0]),col+(i*diagonal[1]))) {
                    Tile tile = tiles[row+(i*diagonal[0])][col+(i*diagonal[1])];
                    if (tile.hasPiece() && (tile.getPiece().isWhite() != king.isWhite())) {
                        Piece piece = tile.getPiece();
                        if (piece.isBishop() || piece.isQueen()
                                || (piece.isPawn() && king.isWhite() != piece.isWhite() && i == 1)) {
                            attackingMoves.add(tile);
                        }
                        canContinue = false;
                    }
                    if ((tile.hasPiece() && tile.getPiece() != king)) {
                        canContinue = false;
                    }
                } else {
                    canContinue = false;
                }
                i++;
            }
        }

        //horizontals: rook and queen
        for (int[] horizontal : horizontals) {
            int i = 1;
            boolean canContinue = true;
            while (canContinue) {
                if (withinBounds(row+(i*horizontal[0]),col+(i*horizontal[1]))) {
                    Tile tile = tiles[row+(i*horizontal[0])][col+(i*horizontal[1])];
                    if (tile.hasPiece() && (tile.getPiece().isWhite() != king.isWhite())) {
                        Piece piece = tile.getPiece();
                        if (piece.isRook() || piece.isQueen()) {
                            attackingMoves.add(tile);
                        }
                        canContinue = false;
                    }
                    if ((tile.hasPiece() && tile.getPiece() != king)) {
                        canContinue = false;
                    }
                } else {
                    canContinue = false;
                }
                i++;
            }
        }
        
        //L-shaped: knight
        for(int[] offset : knightOffsets) {
            if(withinBounds(row+offset[0],col+offset[1])) {
                Tile tile = tiles[row+offset[0]][col+offset[1]];
                if(tile.hasPiece()) {
                    Piece piece = tile.getPiece();
                    if(piece.isKnight() && tile.getPiece().isWhite() != king.isWhite()) {
                        attackingMoves.add(tile);
                    }
                }
            }
        }
    }
    
    /**
     * Calculates the Tiles a given King can move to given the tiles currently attacking
     * said King and writes the tiles to an ArrayList
     * 
     * Calculation iterates through the avaliable diagonal and horizontal positions
     * and performs a basic availability evaluation, but with 2 additional rules:
     * The King cannot move to a square that is inCheck relative to its own type
     * (Utilizes Board::inCheck)
     * The King cannot move to a square adjacent to another King,
     * adjacent King evaluations read from diagonal king check
     * and horizontal king check offset multidimensional arrays to calculate
     * the evaluation offsets to be used at each of the horizontal and diagonal offset arrays
     * 
     * @param king, the given King
     * @param attackingKing, the tiles attacking the King
     * @param canMove, the list of tiles to be written to (cleared before writing)
     */
    public final void kingCanMove(Piece king, ArrayList<Tile> attackingKing, ArrayList<Tile> canMove) {
        canMove.clear();
        int row = king.getTile().getRow();
        int col = king.getTile().getCol();
        
        int diagonalOffsets[][] = {{1,1},{1,-1},{-1,1},{-1,-1}};
        int dkc[][] = {{2,2},{1,2},{2,1},{0,2},{2,0}}; //diagonal king check
        int horizontalOffsets[][] = {{1,0},{0,1},{-1,0},{0,-1}};
        int hkc[][][] = {{{2,1},{2,0},{2,-1}},{{1,2},{0,2},{-1,2}},
            {{-2,1},{-2,0},{-2,-1}},{{1,-2},{0,-2},{-1,-2}}}; //horizontal king check
        
        //diagonals
        for(int[] offset : diagonalOffsets) {
            if(withinBounds(row+offset[0],col+offset[1])) {
                Tile tileToMove = tiles[row+offset[0]][col+offset[1]];
                if((!tileToMove.hasPiece() 
                || (tileToMove.getPiece().isWhite() != king.isWhite()))
                && !inCheck(king, row+offset[0], col+offset[1])
                && (!withinBounds(row+(dkc[0][0]*offset[0]),col+(dkc[0][1]*offset[1]))
                        || !tiles[row+(dkc[0][0]*offset[0])][col+(dkc[0][1]*offset[1])].hasKing())
                && (!withinBounds(row+(dkc[1][0]*offset[0]),col+(dkc[1][1]*offset[1])) 
                        || !tiles[row+(dkc[1][0]*offset[0])][col+(dkc[1][1]*offset[1])].hasKing())
                && (!withinBounds(row+(dkc[2][0]*offset[0]),col+(dkc[2][1]*offset[1])) 
                        || !tiles[row+(dkc[2][0]*offset[0])][col+(dkc[2][1]*offset[1])].hasKing())
                && (!withinBounds(row+(dkc[3][0]*offset[0]),col+(dkc[3][1]*offset[1])) 
                        || !tiles[row+(dkc[3][0]*offset[0])][col+(dkc[3][1]*offset[1])].hasKing())
                && (!withinBounds(row+(dkc[4][0]*offset[0]),col+(dkc[4][1]*offset[1])) 
                        || !tiles[row+(dkc[4][0]*offset[0])][col+(dkc[4][1]*offset[1])].hasKing())) {
                    canMove.add(tiles[row+offset[0]][col+offset[1]]);
                }
            }
        }
        
        //horizontals
        int i= 0;
        for(int[] offset : horizontalOffsets) {
            if(withinBounds(row+offset[0],col+offset[1])) {
                Tile tileToMove = tiles[row+offset[0]][col+offset[1]];
                if((!tileToMove.hasPiece() 
                || (tileToMove.getPiece().isWhite() != king.isWhite()))
                && !inCheck(king, row+offset[0], col+offset[1])
                && (!withinBounds(row+(hkc[i][0][0]),col+(hkc[i][0][1]))
                        || !tiles[row+(hkc[i][0][0])][col+(hkc[i][0][1])].hasKing())
                && (!withinBounds(row+(hkc[i][1][0]),col+(hkc[i][1][1])) 
                        || !tiles[row+(hkc[i][1][0])][col+(hkc[i][1][1])].hasKing())
                && (!withinBounds(row+(hkc[i][2][0]),col+(hkc[i][2][1])) 
                        || !tiles[row+(hkc[i][2][0])][col+(hkc[i][2][1])].hasKing())) {
                    canMove.add(tiles[row+offset[0]][col+offset[1]]);
                }
            }
            i++;
        }
    }
    
    /**
     * Calculates the WhiteListed tiles based on a given King and the tiles
     * attacking it, and writes the WhiteListed tiles to an ArrayList
     * 
     * WhiteListed tiles are defined as Tiles that would be able to defend the
     * King from Check without moving the King itself. When the King is in Check
     * other pieces may only make whiteListed moves
     * 
     * Algorithm is terminated upon 4 conditions:
     * The list of tiles attacking the King is empty and therefore there is no
     * whiteList
     * The list of tiles attacking the King is over 1 and therefore there are no
     * WhiteListed defense positions
     * The first attacking Piece is a Knight and therefore the only WhiteListed
     * tile is the Knight position (a Knight cannot be blocked, only taken to prevent
     * Check)
     * Neither is true so the full algorithm is performed: Every tile from the 
     * attacked King location up until the first attacking Piece location is added
     * to the WhiteList
     * 
     * @param attackingKing, the tiles attacking the given King 
     * @param king, the given King
     * @param whiteListed, the ArrayList to be written to, representing the WhiteList
     */
    public void attackWhiteListed(ArrayList<Tile> attackingKing, Piece king, ArrayList<Tile> whiteListed) {
        whiteListed.clear();
        if(attackingKing.isEmpty() || attackingKing.size() > 1) {
            return;
        }
        Tile attack = attackingKing.get(0);
        if(attack.hasPiece() && attack.getPiece().isKnight()) {
            whiteListed.add(attack);
            return;
        }
        int row = king.getTile().getRow(); //start at king
        int col = king.getTile().getCol();
        int r = normalize(attack.getRow() - king.getTile().getRow());
        int c = normalize(attack.getCol() - king.getTile().getCol());
        boolean atAttacker = false; //loop until at attacker
        while (!atAttacker) {
            if(withinBounds(row+r,col+c)) {
                Tile t = tiles[row+r][col+c];
                if (t.getPiece() == attack.getPiece()) {
                    atAttacker = true;
                }
                whiteListed.add(t); //add all tiles to whiteList
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
            } else {
                atAttacker = true;
            }
        }
    }
    
    /**
     * Initializes the Board object with the BoardGUI viewed from White
     * perspective, and the Board ArrayLists used to store references to the Board
     * pieces
     * @param board, the board as ByteBoard, a 2D array of bytes representing
     * the type of piece at each tile row/col position
     * @param game, the game each Piece belongs to (determines a Piece effect on
     * Game flow and Game GUI)
     */
    public final void initWhiteBoard(byte[][] board, Game game) {
        blackNotKing.clear();
        whiteNotKing.clear();
        Pane boardUI = new Pane();
        boolean isLight = false;
        //i is row, j is column
        for(int i = 0; i < WIDTH; i++) {
            for(int j = 0; j < HEIGHT; j++) {
                Tile tile = new Tile(isLight, i, j, true, game);
                tiles[i][j] = tile;
                boardUI.getChildren().add(tile);
                isLight = !isLight;
                Piece piece = NOTATION_TO_CONSTRUCTOR_TABLE[board[i][j]].createPiece(tiles[i][j]);
                if(piece != null) {
                    if(piece.isWhite()) {
                        if(piece.isKing()) {
                            whiteKing = piece;
                        } else {
                            whiteNotKing.add(piece);
                        }
                    } else {
                        if(piece.isKing()) {
                            blackKing = piece;
                        } else {
                            blackNotKing.add(piece);
                        }
                    }
                    tiles[i][j].setPiece(piece);
                    boardUI.getChildren().add(piece);
                    piece.toFront();
                }
                if(i == 0) {
                    Label notationLabel = new Label(getCharacterNotation(j));
                    notationLabel.setId("tinyfont");
                    StackPane.setAlignment(notationLabel, Pos.BOTTOM_LEFT);
                    tile.addLabel(notationLabel);
                } 
                if(j == (WIDTH - 1)) {
                    Label notationLabel = new Label(Integer.toString(i+1));
                    notationLabel.setId("tinyfont");
                    StackPane.setAlignment(notationLabel, Pos.TOP_RIGHT);
                    tile.addLabel(notationLabel);
                }
            }
            isLight = !isLight;
        }
        boardGUI = boardUI;
        getBoardGUI().setId("board");
    }
    
    /**
     * Initializes the Board object with the BoardGUI viewed from Black
     * perspective, and the Board ArrayLists used to store references to the Board
     * pieces
     * @param board, the board as ByteBoard, a 2D array of bytes representing
     * the type of piece at each tile row/col position
     * @param game, the game each Piece belongs to (determines a Piece effect on
     * Game flow and Game GUI)
     */
    public final void initBlackBoard(byte[][] board, Game game) {
        blackNotKing.clear();
        whiteNotKing.clear();
        Pane boardUI = new Pane();
        boolean IsLight = false;
        //i is row, j is column
        for(int i = 0; i < WIDTH; i++) {
            for(int j = 0; j < HEIGHT; j++) {
                Tile tile = new Tile(IsLight, i, j, false, game);
                tiles[i][j] = tile;
                boardUI.getChildren().add(tile);
                IsLight = !IsLight;
                Piece piece = NOTATION_TO_CONSTRUCTOR_TABLE[board[i][j]].createPiece(tiles[i][j]);
                if(piece != null) {
                    if(piece.isWhite()) {
                        if(piece.isKing()) {
                            whiteKing = piece;
                        } else {
                            whiteNotKing.add(piece);
                        }
                    } else {
                        if(piece.isKing()) {
                            blackKing = piece;
                        } else {
                            blackNotKing.add(piece);
                        }
                    }
                    tiles[i][j].setPiece(piece);
                    boardUI.getChildren().add(piece);
                    piece.toFront();
                }
                if(i == HEIGHT-1) {
                    Label notationLabel = new Label(getCharacterNotation(j));
                    StackPane.setAlignment(notationLabel, Pos.BOTTOM_LEFT);
                    tile.addLabel(notationLabel);
                } 
                if(j == 0) {
                    Label notationLabel = new Label(Integer.toString(i+1));
                    StackPane.setAlignment(notationLabel, Pos.TOP_RIGHT);
                    tile.addLabel(notationLabel);
                }
            }
            IsLight = !IsLight;
        }
        boardGUI = boardUI;
        getBoardGUI().setId("board");
    }
    
}
