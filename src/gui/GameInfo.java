/*
 * Stores the contents of the game to be readable by the 
 * controller's rendering algorithms
 * 7/3/20
 */
package gui;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *
 * @author Joseph
 */
public class GameInfo {
    
    
    public static final byte EMPTY = 0; //Bytes used to represent pieces in ByteBoard
    public static final byte WHITE_PAWN = 1;
    public static final byte WHITE_BISHOP = 2;
    public static final byte WHITE_KNIGHT = 3;
    public static final byte WHITE_ROOK = 4;
    public static final byte WHITE_QUEEN = 5;
    public static final byte WHITE_KING = 6;
    public static final byte BLACK_PAWN = 7;
    public static final byte BLACK_BISHOP = 8;
    public static final byte BLACK_KNIGHT = 9;
    public static final byte BLACK_ROOK = 10;
    public static final byte BLACK_QUEEN = 11;
    public static final byte BLACK_KING = 12;
    
    public static final String[] NUMBER_TO_LETTER_TABLE = {"a","b","c","d","e","f","g","h"}; //convert number to readable format
    public static final String[] NUMBER_TO_FEN = {"","P","B","N","R","Q","K","p","b","n","r","q","k"}; //convert number to FEN format
    public static final String INITIAL_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -";
    
    public static final byte[][] INITIAL_BOARD = 
                {{WHITE_ROOK,WHITE_KNIGHT,WHITE_BISHOP,WHITE_QUEEN,WHITE_KING,WHITE_BISHOP,WHITE_KNIGHT,WHITE_ROOK},
                 {WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN},
                 {EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
                 {EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
                 {EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
                 {EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY},
                 {BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN},
                 {BLACK_ROOK,BLACK_KNIGHT,BLACK_BISHOP,BLACK_QUEEN,BLACK_KING,BLACK_BISHOP,BLACK_KNIGHT,BLACK_ROOK}};
    
    public static final byte WIDTH = 8;
    public static final byte HEIGHT = 8;
    
    private final ArrayList<Move> moves = new ArrayList<>(); //all moves
    private byte[][] currentBoard; //the current board to perform actions on
    private int moveNum = -1; //the current move

    public int getMoveNum() {
        return moveNum;
    }
    
    public ArrayList<Move> getMoves() {
        return moves;
    }

    public byte[][] getCurrentBoard() {
        return currentBoard;
    }

    public void setCurrentBoard(byte[][] currentBoard) {
        this.currentBoard = currentBoard;
    }
    
    public GameInfo() {
        currentBoard = cloneArray(INITIAL_BOARD);
    }
    
    public boolean hasKingMoved(boolean forWhiteKing) {
        int i = forWhiteKing ? 0 : 1;
        while(i < moveNum+1) {
            Move move = moves.get(i);
            if(move.isCastle() || (move.hasPieceMoved() && move.getPieceMoved().isKing())) {
                return false;
            }
            i=i+2;
        }
        return true;
    }
    
    /**
     * Returns whether or not the move at the current moveNum can castle
     * @param forWhiteKing is kingWhite
     * @param kingSide, the side of the rook to check for
     * @return canCastle
     */
    public boolean canCastle(boolean forWhiteKing, boolean kingSide) {
        int i = forWhiteKing ? 0 : 1;
        while(i < moveNum) {
            Move move = moves.get(i);
            if(move.isCastle() || (move.hasPieceMoved() && move.getPieceMoved().isKing() 
                    && move.getPieceMoved().isWhite() == forWhiteKing)) {
                return false;
            }
            i=i+2;
        }
        if(kingSide && !canKingSideCastle(-1)) {
            return false;
        } else return kingSide || canQueenSideCastle(-1);
    }

    /**
     * Returns whether or not the move at the current moveNum can castle
     * @param forWhiteKing is kingWhite
     * @param rookToCastle, the rook to castle for (cannot have moved)
     * @return canCastle
     * @deprecated method will not work if board has been reconstructed 
     * the Rook needs the be the exact same memory reference to work
     */
    @Deprecated
    public boolean canCastle(boolean forWhiteKing, Piece rookToCastle) {
        int i = forWhiteKing ? 0 : 1;
        while(i < moveNum+1) {
            Move move = moves.get(i);
            if(move.isCastle() || (move.hasPieceMoved() && (move.getPieceMoved().isKing() 
                    || move.getPieceMoved() == rookToCastle))) {
                return false;
            }
            i=i+2;
        }
        return true;
    }
    
    public boolean canGoLeft() {
        return moveNum > -1;
    }
    
    public boolean canGoRight() {
        return moveNum < getMoveCount()-1;
    }
    
    public void goLeft() {
        moveNum--;
    }
    
    public void goRight() {
        moveNum++;
    }
    
    public void goFarLeft() {
        moveNum = -1;
    }
    
    public void goFarRight() {
        moveNum = getMoveCount()-1;
    }
    
    public void goTo(int num) {
        moveNum = num;
    }
    
    public int getMoveCount() {
        return moves.size();
    }
    
    private byte[][] getBoardByIndex(int num) {
        return moves.get(num).getBoard();
    }
    
    public byte[][] getBoardByNumber(int num) {
        if(num != -1) {
            return moves.get(num).getBoard();
        } 
        return GameInfo.INITIAL_BOARD;
    }

    public byte[][] getBeforeLastBoard() {
        if(getMoveCount() > 1) {
            return getBoardByIndex(getMoveCount()-2);
        }
        return GameInfo.INITIAL_BOARD;
    }
    
    public byte[][] getLastBoard() {
        if(getMoveCount() > 0) {
            return getBoardByIndex(getMoveCount()-1);
        }
        return GameInfo.INITIAL_BOARD;
    }
    
    public boolean isLastTurnWhite() {
        return ((getMoveCount()-1) % 2) != 0;
    }
    
    public boolean isOnLastTurn() {
        return moveNum == moves.size()-1;
    }
    
    /**
     * Takes back a move by removing move from list and shifting moveNum back
     */
    public void takeBackMove() {
        moves.remove(getMoveCount()-1);
        moveNum = getMoveCount()-1;
        if(getMoveCount()>0) {
            currentBoard = cloneArray(getBoardByIndex(getMoveCount()-1));
        } else {
            currentBoard = cloneArray(INITIAL_BOARD);
        }  
    }
    
    public boolean canTakeBack() {
        return getMoveCount() > 0;
    }
    
    /**
     * Checks moves to check if king can castle, if on initial position can always castle
     * @param offset, the number of moves to go back to
     * @return anKingSideCastle
     */
    public boolean canKingSideCastle(int offset) {
        int num = moveNum - offset;
        if((num < moves.size()) && num > -1) {
            return moves.get(num).canKingSideCastle();
        } else {
            return true;
        }
    }
    
    /**
     * Checks moves to check if king can castle, if on initial position can always castle
     * @param offset, the number of moves to go back to
     * @return anKingSideCastle
     */
    public boolean canQueenSideCastle(int offset) {
        int num = moveNum - offset;
        if((num < moves.size()) && num > -1) {
            return moves.get(num).canQueenSideCastle();
        } else {
            return true;
        }
    }
    
    /**
     * Performs a standard chess move on the gameInfo
     * 
     * Modifies currentBoard
     * Increases the moveNum by one
     * adds move with copy of currentBoard as ByteBoard to move list
     * 
     * @param oldTile, tile to move from
     * @param newTile, tile to move to 
     */
    public void makeMove(Tile oldTile, Tile newTile) {
        boolean taken = newTile.hasPiece();
        currentBoard[newTile.getRow()][newTile.getCol()] = currentBoard[oldTile.getRow()][oldTile.getCol()];
        currentBoard[oldTile.getRow()][oldTile.getCol()] = EMPTY;
        moveNum++;
        Move move = new Move(oldTile.getRow(), oldTile.getCol(), newTile.getRow(), 
                newTile.getCol(), oldTile.getPiece(), cloneArray(currentBoard),
                canKingSideCastle(-2), canQueenSideCastle(-2), moveNum % 2 == 0, taken);
        moves.add(move);
    }
    
    /**
     * Performs an en passant chess move on the gameInfo
     * 
     * Modifies currentBoard
     * Increases the moveNum by one
     * adds move with copy of currentBoard as ByteBoard to move list
     * 
     * @param oldTile, tile to move from
     * @param newTile, tile to move to 
     * @param taken the piece taken en passant
     */
    public void makeMoveEnPassant(Tile oldTile, Tile newTile, Piece taken) {
        currentBoard[newTile.getRow()][newTile.getCol()] = currentBoard[oldTile.getRow()][oldTile.getCol()];
        currentBoard[oldTile.getRow()][oldTile.getCol()] = EMPTY;
        currentBoard[taken.getTile().getRow()][taken.getTile().getCol()] = EMPTY;
        moveNum++;
        Move move = new Move(oldTile.getRow(), oldTile.getCol(), newTile.getRow(), 
                newTile.getCol(), oldTile.getPiece(), cloneArray(currentBoard), 
                canKingSideCastle(-2), canQueenSideCastle(-2), moveNum % 2 == 0, false);
        moves.add(move);
    }
    
    /**
     * Performs a promotion chess move on the gameInfo
     * 
     * Modifies currentBoard
     * Increases the moveNum by one
     * adds move with copy of currentBoard as ByteBoard to move list
     * 
     * @param oldTile, tile to move from
     * @param newTile, tile to move to 
     * @param promotionTo piece to be promoted to
     */
    public void makeMovePromotion(Tile oldTile, Tile newTile, Piece promotionTo) {
        boolean taken = newTile.hasPiece();
        currentBoard[newTile.getRow()][newTile.getCol()] = promotionTo.getInfoCode();
        currentBoard[oldTile.getRow()][oldTile.getCol()] = EMPTY;
        moveNum++;
        Move move = new Move(oldTile.getRow(), oldTile.getCol(), newTile.getRow(), 
                newTile.getCol(), oldTile.getPiece(), cloneArray(currentBoard), 
                canKingSideCastle(-2), canQueenSideCastle(-2), moveNum % 2 == 0,taken);
        moves.add(move);
    }
    
    /**
     * Performs a castle QueenSide on the gameInfo
     * 
     * Modifies currentBoard
     * Increases the moveNum by one
     * adds move with copy of currentBoard as ByteBoard to move list
     * 
     * @param king the king to perform castling on
     * @param rook the rook to perform castling with
     */
    public void makeMoveCastleQueenSide(Piece king, Piece rook) {
        Tile kingTile = king.getTile();
        Tile rookTile = rook.getTile();
        currentBoard[kingTile.getRow()][kingTile.getCol()-2] = currentBoard[kingTile.getRow()][kingTile.getCol()];
        currentBoard[rookTile.getRow()][rookTile.getCol()+3] = currentBoard[rook.getTile().getRow()][rook.getTile().getCol()];
        currentBoard[kingTile.getRow()][kingTile.getCol()] = EMPTY;
        currentBoard[rookTile.getRow()][rookTile.getCol()] = EMPTY;
        moveNum++;
        Move move = new Move(kingTile.getRow(), kingTile.getCol(), kingTile.getRow(), kingTile.getCol()-2,
                Move.QUEEN_SIDE_CASTLE, cloneArray(currentBoard));
        moves.add(move);  
    }
    
    /**
     * Performs a castle KingSide on the gameInfo
     * 
     * Modifies currentBoard
     * Increases the moveNum by one
     * adds move with copy of currentBoard as ByteBoard to move list
     * 
     * @param king the king to perform castling on
     * @param rook the rook to perform castling with
     */
    public void makeMoveCastleKingSide(Piece king, Piece rook) {
        Tile kingTile = king.getTile();
        Tile rookTile = rook.getTile();
        currentBoard[kingTile.getRow()][kingTile.getCol()+2] = currentBoard[kingTile.getRow()][kingTile.getCol()];
        currentBoard[rookTile.getRow()][rookTile.getCol()-2] = currentBoard[rook.getTile().getRow()][rook.getTile().getCol()];
        currentBoard[kingTile.getRow()][kingTile.getCol()] = EMPTY;
        currentBoard[rookTile.getRow()][rookTile.getCol()] = EMPTY;
        moveNum++;
        Move move = new Move(kingTile.getRow(), kingTile.getCol(), kingTile.getRow(), kingTile.getCol()+2,
                Move.KING_SIDE_CASTLE, cloneArray(currentBoard));
        moves.add(move); 
    }

    /**
     * Return the positions of the tiles involved in the most recent move
     * @return positions of the old and new tile, in that order
     */
    public ArrayList<int[]> getRecentlyMovedTileCoordinates() {
        if(moveNum < 0) {
            return new ArrayList<>();
        }
        Move currentMove = moves.get(moveNum);
        ArrayList<int[]> recentlyMoved = new ArrayList<>();
        currentMove.addRecent(recentlyMoved);
        return recentlyMoved;
    }
    
    public void setRecentPromotion(Piece promotionTo) {
        moves.get(moves.size()-1).setPromotionNot(promotionTo);
    }
    
    public void setRecentEnPassant() {
        moves.get(moves.size()-1).setEnPassantNot();
    }
    
    public void setRecentCheck() {
        moves.get(moves.size()-1).setCheckNot();
    }
    
    public void setRecentCheckMate() {
        moves.get(moves.size()-1).setCheckMateNot();
    }
    
    public void setRecentStaleMate() {
        moves.get(moves.size()-1).setStaleMateNot();
    }
    
    public void setRecentCapture() {
        moves.get(moves.size()-1).setCapture(true);
    }
    
    @Deprecated
    public void setRecentNotation() {
        moves.get(moves.size()-1).setNotation();
    }
    
    /**
     * Calculates the FEN for the current moveNum move
     * @return FEN as a string
     */
    public String getMoveFEN() {
        if(moveNum < 0) {
            return INITIAL_FEN;
        }
        Move move = moves.get(moveNum);
        byte[][] board = move.getBoard();
        StringBuilder FEN = new StringBuilder();
        int spaceCount = 0;
        for(int i = Game.HEIGHT-1; i >= 0; i--) {
            for(int j = 0; j < Game.WIDTH; j++) {
                if(board[i][j] != EMPTY) {
                    if(spaceCount != 0) {
                        FEN.append(spaceCount);
                    } 
                    FEN.append(NUMBER_TO_FEN[board[i][j]]);
                    spaceCount = 0;
                } else {
                    spaceCount++;
                }
            }
            if(i != 0) {
                if (spaceCount != 0) {
                    FEN.append(spaceCount);
                }
                spaceCount = 0;
                FEN.append("/");
            }
        }
        boolean boolIsWhite = moveNum % 2 == 0;
        String isWhite = boolIsWhite ? " b" : " w";
        FEN.append(isWhite);
        boolean whiteQueenSide = true;
        boolean whiteKingSide = true;
        boolean blackQueenSide = true;
        boolean blackKingSide = true;
        boolean whiteKingMoved = hasKingMoved(true);
        boolean blackKingMoved = hasKingMoved(false);
        Move checkWhite;
        Move checkBlack;
        if(moveNum > 0) {
            checkWhite = boolIsWhite ? moves.get(moveNum) : moves.get(moveNum - 1);
            checkBlack = boolIsWhite ? moves.get(moveNum - 1) : moves.get(moveNum);
            whiteKingSide = checkWhite.canKingSideCastle() && whiteKingMoved;
            whiteQueenSide = checkWhite.canQueenSideCastle() && whiteKingMoved;
            blackKingSide = checkBlack.canKingSideCastle() && blackKingMoved;
            blackQueenSide = checkBlack.canQueenSideCastle() && blackKingMoved;
        } 
        if(whiteQueenSide || whiteKingSide || blackQueenSide || blackKingSide) {
            String K = whiteKingSide ? "K" : "";
            String Q = whiteQueenSide ? "Q" : "";
            String k = blackKingSide ? "k" : "";
            String q = blackQueenSide ? "q" : "";
            FEN.append(" ").append(K).append(Q).append(k).append(q);
        } else {
            FEN.append(" -");
        }
        if(moveNum != -1) {
            if(move.canEnPassant(boolIsWhite)) {
                int row = move.getNewPos()[0]+1;
                int col = move.getNewPos()[1];
                if(boolIsWhite) {
                    int passantRow = row-1;
                    FEN.append(" ").append(NUMBER_TO_LETTER_TABLE[col]).append(passantRow);
                } else {
                    int passantRow = row+1;
                    FEN.append(" ").append(NUMBER_TO_LETTER_TABLE[col]).append(passantRow);
                }
            } else {
                FEN.append(" -");
            }
        } else {
            FEN.append(" -");
        }
        return FEN.toString();
    }
    
    /**
     * Return game PGN for file export by adding each Move notation
     * @param result, the result of the game to get headers for
     * @return PGN as a string
     */
    public String getGamePGN(String result) {
        StringBuilder PGN = new StringBuilder(getPGNHeaders(result));
        int i = 0;
        for(Move move : moves) {
            if(i % 10 == 0) {
                PGN.append("\n");
            }
            if(i % 2 == 0) {
                PGN.append(" ").append(((i / 2) + 1)).append(".");
            }
            PGN.append(" ").append(move.getNotation());
            i++;
        } 
        return PGN.toString();
    }
    
    /**
     * Return PGN headers for file export
     * @param result, the result of the game to return headers for
     * @return headers as a string
     */
    public String getPGNHeaders(String result) {
        return "[Event \"ChessLite Practice\"]\n"
                + "[Site \"ChessLite GUI\"]\n"
                + "[Date \"" + getDate() + "\"]\n"
                + "[White \"Player1\"]\n"
                + "[Black \"Player2\"]\n"
                + result + "\n";
    }
    
    /**
     * Gets the current date of the system
     * @return date as String
     */
    public String getDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now(); 
        return dtf.format(now);
    }
    
    /**
     * Clones the array
     * @param src ByteBoard to be copied
     * @return return copy as ByteBoard
     */
    public static byte[][] cloneArray(byte[][] src) {
        int length = src.length;
        byte[][] target = new byte[length][src[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[i].length);
        }
        return target;
    }
    
}

