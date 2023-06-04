/*
 * Class for the to store a Move made in a Game
 * 7/3/20
 */
package gui;

import java.util.ArrayList;

/**
 *
 * @author Joseph
 */
public class Move {
    
    public static final String[] NUMBER_TO_LETTER_TABLE = {"a","b","c","d","e","f","g","h"}; //used in conversion to notation
    public static final int NO_CASTLE = 0;
    public static final int KING_SIDE_CASTLE = 1;
    public static final int QUEEN_SIDE_CASTLE = 2;
     
    private final byte[][] board; //byte board to be stored
    private final int[] oldPos = new int[2];
    private final int[] newPos = new int[2];
    private Piece pieceMoved;
    private final int castleStatus;
    private String notation;
    private boolean capture = false;
    
    private boolean canKingSideCastle;
    private boolean canQueenSideCastle;

    public void setCanKingSideCastle(boolean canKingSideCastle) {
        this.canKingSideCastle = canKingSideCastle;
    }

    public void setCanQueenSideCastle(boolean canQueenSideCastle) {
        this.canQueenSideCastle = canQueenSideCastle;
    }

    public byte[][] getBoard() {
        return board;
    }

    public int[] getOldPos() {
        return oldPos;
    }

    public int[] getNewPos() {
        return newPos;
    }
    
    public Piece getPieceMoved() {
        return pieceMoved;
    }

    public void setPieceMoved(Piece pieceMoved) {
        this.pieceMoved = pieceMoved;
    }
    
    public boolean hasPieceMoved() {
        return pieceMoved != null;
    }
    
    public boolean isCastle() {
        return castleStatus>0;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }
    
    /**
     * Constructs a new Move
     * @param oldRow to be moved from
     * @param oldCol to be moved from
     * @param newRow to be moved to
     * @param newCol to be moved to
     * @param moved, piece moved on turn
     * @param boardIn, ByteBoard to be stored
     * @param canKingSideCastleIn, whether or not king could castle last Move
     * @param canQueenSideCastleIn, whether or not queen could castle last Move
     * @param forWhite, side move is for
     * @param taken whether a piece was taken this turn
     */
    public Move(int oldRow, int oldCol, int newRow, int newCol, Piece moved, byte[][] boardIn,
                boolean canKingSideCastleIn, boolean canQueenSideCastleIn, boolean forWhite, boolean taken) {
        oldPos[0] = oldRow;
        oldPos[1] = oldCol;
        newPos[0] = newRow;
        newPos[1] = newCol;
	pieceMoved = moved;
        castleStatus = NO_CASTLE;
        board = boardIn;   
        canKingSideCastle = canKingSideCastleIn;
        canQueenSideCastle = canQueenSideCastleIn;
        if(!taken) {
            notation = calcPiecePrefix(pieceMoved,oldPos,newPos) + getCharacterNotation(newPos[1]) + (newPos[0]+1);
        } else {
            notation = calcPiecePrefix(pieceMoved,oldPos,newPos) + "x" + getCharacterNotation(newPos[1]) + (newPos[0]+1);
        }
        if(forWhite) {
            if(moved.getTile().getCol() == Game.LOWER_BOUNDARY && moved.getTile().getRow() == Game.LOWER_BOUNDARY) {
                canQueenSideCastle = false;
            } else if(moved.getTile().getCol() == Game.UPPER_BOUNDARY && moved.getTile().getRow() == Game.LOWER_BOUNDARY) {
                canKingSideCastle = false;
            }
        } else {
            if(moved.getTile().getCol() == Game.LOWER_BOUNDARY && moved.getTile().getRow() == Game.UPPER_BOUNDARY) {
                canQueenSideCastle = false;
            } else if(moved.getTile().getCol() == Game.UPPER_BOUNDARY && moved.getTile().getRow() == Game.UPPER_BOUNDARY) {
                canKingSideCastle = false;
            }
        }
    }
    
    /**
     * Constructs a new Move
     * @param oldRow to be moved from
     * @param oldCol to be moved from
     * @param newRow to be moved to
     * @param newCol to be moved to
     * @param castleType KING_SIDE_CASTLE or QUEEN_SIDE_CASTLE
     * @param boardIn, ByteBoard to be stored
     */
    public Move(int oldRow, int oldCol, int newRow, int newCol, int castleType, byte[][] boardIn) {
        oldPos[0] = oldRow;
        oldPos[1] = oldCol;
        newPos[0] = newRow;
        newPos[1] = newCol;
        castleStatus = castleType;
        board = boardIn;
        canKingSideCastle = false;
        canQueenSideCastle = false;
        notation = isQueenSide(castleStatus) ? "0-0-0" : "0-0";
    }
    
    /**
     * Can be used to set the notation of a move
     * @deprecated because notation is already set in construction, re-setting notation
     * could lead to the WRONG information being set if called at the wrong time
     */
    @Deprecated
    public void setNotation() {
        if(castleStatus == NO_CASTLE) {
            String notationToAdd;
            if(!capture) {
                notationToAdd = calcPiecePrefix(pieceMoved,oldPos,newPos) + getCharacterNotation(newPos[1]) + (newPos[0]+1);
            } else {
                notationToAdd = calcPiecePrefix(pieceMoved,oldPos,newPos) + "x" + getCharacterNotation(newPos[1]) + (newPos[0]+1);
            }
            notation = notationToAdd;
        } else {
            notation = isQueenSide(castleStatus) ? "0-0-0" : "0-0";
        }
    }
    
    /**
     * Calculates the piece prefix to be used
     * 
     * In the event there are multiple pieces of the same type that game move to
     * the same location, the piece moved needs a position specifier
     * Follows preference of 1.Col 2.Row 3.Col+Row
     * 
     * @param piece, piece to find notation for
     * @param old, old position of the move
     * @param loc, new location the piece is moved to
     * @return the piece prefix as string (including position specifier if needed)
     */
    public final String calcPiecePrefix(Piece piece, int[] old, int[] loc) {
        String str = pieceMoved.getNotation();
        boolean matchingCol = false;
        boolean matchingRow = false;
        ArrayList<int[]> commonPieceLocations = piece.calcCommonPieceLocations(loc);
        if(!commonPieceLocations.isEmpty()) {
            for (int[] location : commonPieceLocations) {
                if (location[0] == piece.getTile().getRow()) {
                    matchingRow = true;
                }
                if (location[1] == piece.getTile().getCol()) {
                    matchingCol = true;
                }
            }
            if (matchingRow) {
                str = str + getCharacterNotation(old[1]);
            }
            if (matchingCol) {
                str = str + (old[0]+1);
            } 
            if(!matchingRow && !matchingCol) {
                str = str + getCharacterNotation(old[1]);
            }
        }
        return str;
    }
    
    /**
     * Returns the character notation for a given number 
     * @param num, the number (typically a Column)
     * @return the notation
     */
    public static String getCharacterNotation(int num) {
        return NUMBER_TO_LETTER_TABLE[num];
    }
    
    public void addRecent(ArrayList<int[]> recentlyMoved) {
        recentlyMoved.add(oldPos);
        recentlyMoved.add(newPos);
    }
    
    public static boolean isQueenSide(int type) {
        return type == QUEEN_SIDE_CASTLE;
    }
    
    public void setEnPassantNot() {
        notation = notation + "e.p.";
    }
    
    public void setCheckNot() {
        notation = notation + "+";
    }
    
    public void setCheckMateNot() {
        notation = notation + "#";
    }
    
    public void setPromotionNot(Piece promotionTo) {
        notation = notation + promotionTo.getNotation();
    }
    
    public void setStaleMateNot() {
        notation = notation + "$";
    }
    
    public void setCapture(boolean captureIn) {
        capture = captureIn;
    }
    
    public boolean canKingSideCastle() {
        return canKingSideCastle;
    }
    
    public boolean canQueenSideCastle() {
        return canQueenSideCastle;
    }
    
    /**
     * Calculates whether enPassant can be performed from a move
     * @param forWhite, whether the enPassant is for white
     * @return whether or not enPassant can be performed
     */
    public boolean canEnPassant(boolean forWhite) {
        if(pieceMoved != null) {
            return pieceMoved.isPawn() && ((forWhite && oldPos[0] == 1 && newPos[0] == 3) || 
                    (!forWhite && oldPos[0] == 6 && newPos[0] == 4));
        } else {
            return false;
        }
    }

}
