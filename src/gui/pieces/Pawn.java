/*
 * Class for the Pawn StackPane GUI and functionality
 * 6/3/20
 */
package gui.pieces;

import gui.ChessLite;
import gui.GameInfo;
import gui.Game;
import gui.Move;
import gui.Piece;
import gui.Tile;
import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Joseph
 */
public final class Pawn extends Piece{
    
     /*
     * Attack pattern: 
     * Can only move to - M 
     * Only avaliable if piece to Take - T 
     * white 
     * O    O    O    O 
     * T    M    T    O 
     * O    P    O    O 
     * O    O    O    O 
     * black
     * O    O    O    O 
     * O    P    O    O 
     * T    M    T    O 
     * O    O    O    O 
     * 
     */
    
    private final ArrayList<Tile> avaliableEnPassant = new ArrayList<>();
    private final ArrayList<Tile> avaliablePromotion = new ArrayList<>();
    
    public final String WHITE_PAWN = "/resources/" + ChessLite.PATH + "/whitepawn.png";
    public final String BLACK_PAWN = "/resources/" + ChessLite.PATH + "/blackpawn.png";
    
    /**
     * Constructs a Pawn
     * 
     * @param isWhite side of the piece
     * @param tile tile piece belongs to
    */
    public Pawn(boolean isWhite, Tile tile) {
        super(isWhite, tile);
        Image image;
        if(isWhite) {
            image = new Image(WHITE_PAWN);
        } else {
            image = new Image(BLACK_PAWN);
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(TILE_SIZE);
        imageView.setFitWidth(TILE_SIZE);
        this.getChildren().add(imageView);
    }

    @Override
    public void pieceAvaliableMoves() {
        avaliableEnPassant.clear();
        avaliablePromotion.clear();
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = getTile().getRow();
        int col = getTile().getCol();
        ArrayList<Tile> avaliable = getAvaliable();
        
        if(isWhite()) {
            if(withinBounds(row+1,col)) {
                Tile firstTile = tiles[row+1][col];
                if(!firstTile .hasPiece()) {
                    if (row != 6) {
                        avaliable.add(firstTile);
                    } else {
                        avaliablePromotion.add((firstTile));
                    }
                    if(getTile().getRow() == 1) {
                        Tile secondTile = tiles[row+2][col];
                        if(!secondTile.hasPiece()) {
                            avaliable.add(secondTile);
                        }
                    }
                }
            }
           if(withinBounds(row+1,col+1)) {
                Tile tile = tiles[row+1][col+1];
                if(tile.hasPiece() && 
                        tile.getPiece().isWhite() != isWhite()) {
                    if(row != 6){
                        avaliable.add(tile);
                    } else {
                        avaliablePromotion.add(tile);
                    }
                }
            }
            if(withinBounds(row+1,col-1)) {
                Tile tile = tiles[row+1][col-1];
                if(tile.hasPiece() && 
                        tile.getPiece().isWhite() != isWhite()) {
                    if(row != 6) {
                        avaliable.add(tile);
                    } else {
                        avaliablePromotion.add(tile);
                    }
                }
            }
            Move lastMove = controller.getLastMove();
            if(lastMove != null) {
                if(withinBounds(row,col-1) && withinBounds(row+1,col-1)) {
                    Tile toTake = tiles[row][col-1];
                    Tile toMove = tiles[row+1][col-1];
                    if(row == 4 && toTake.hasPiece() 
                            && toTake.getPiece().isPawn() && 
                            toTake.getPiece().isWhite() != isWhite() 
                            && lastMove.getNewPos()[1] == col-1 && lastMove.getNewPos()[0] == row
                            && lastMove.getOldPos()[1] == col-1 && lastMove.getOldPos()[0] == row+2) {
                        avaliableEnPassant.add(toMove);
                    }
                }
                if(withinBounds(row,col+1) && withinBounds(row+1,col+1)) {
                    Tile toTake = tiles[row][col+1];
                    Tile toMove = tiles[row+1][col+1];
                    if(row == 4 && toTake.hasPiece() 
                            && toTake.getPiece().isPawn() && 
                            toTake.getPiece().isWhite() != isWhite() 
                            && lastMove.getNewPos()[1] == col+1 && lastMove.getNewPos()[0] == row
                            && lastMove.getOldPos()[1] == col+1 && lastMove.getOldPos()[0] == row+2) {
                        avaliableEnPassant.add(toMove);
                    }
                }
            }
        } else {
            if(withinBounds(row-1,col)) {
                Tile firstTile = tiles[row-1][col];
                if(!firstTile.hasPiece()) {
                    if(row != 1) {
                        avaliable.add(firstTile);
                    } else {
                        avaliablePromotion.add(firstTile);
                    }
                    if(getTile().getRow() == 6) {
                        Tile secondTile = tiles[row-2][col];
                        if(!secondTile.hasPiece()) {
                            avaliable.add(secondTile);
                        }
                    }
                }
            }
            if(withinBounds(row-1,col+1)) {
                Tile tile = tiles[row-1][col+1];
                if(tile.hasPiece() && tile.getPiece().isWhite() != isWhite()) {
                    if(row != 1) {
                        avaliable.add(tile);
                    } else {
                        avaliablePromotion.add(tile);
                    }
                }
            }
            if(withinBounds(row-1,col-1)) {
                Tile tile = tiles[row-1][col-1];
                if(tile.hasPiece() && tile.getPiece().isWhite() != isWhite()) {
                    if(row != 1) {
                        avaliable.add(tile);
                    } else {
                        avaliablePromotion.add(tile);
                    }
                }
            }
            Move lastMove = controller.getLastMove();
            if(lastMove != null) {
                if(withinBounds(row,col-1) && withinBounds(row-1,col-1)) {
                    Tile toTake = tiles[row][col-1];
                    Tile toMove = tiles[row-1][col-1];
                    if(row == 3 && toTake.hasPiece() 
                            && toTake.getPiece().isPawn() && 
                            toTake.getPiece().isWhite() != isWhite() 
                            && lastMove.getNewPos()[1] == col-1 && lastMove.getNewPos()[0] == row
                            && lastMove.getOldPos()[1] == col-1 && lastMove.getOldPos()[0] == row-2) {
                        avaliableEnPassant.add(toMove);
                    }
                }
                if(withinBounds(row,col+1) && withinBounds(row-1,col-1)) {
                    Tile toTake = tiles[row][col+1];
                    Tile toMove = tiles[row-1][col+1];
                    if(row == 3 && toTake.hasPiece() 
                            && toTake.getPiece().isPawn() && 
                            toTake.getPiece().isWhite() != isWhite() 
                            && lastMove.getNewPos()[1] == col+1 && lastMove.getNewPos()[0] == row
                            && lastMove.getOldPos()[1] == col+1 && lastMove.getOldPos()[0] == row-2) {
                        avaliableEnPassant.add(toMove);
                    }
                }
            }
        }
    }
    
    @Override
    public void pieceAvaliableMoves(ArrayList<Tile> whiteList) {
        avaliableEnPassant.clear();
        avaliablePromotion.clear();
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = getTile().getRow();
        int col = getTile().getCol();
        ArrayList<Tile> avaliable = getAvaliable();
        
        if(isWhite()) {
            if(withinBounds(row+1,col)) {
                Tile firstTile = tiles[row+1][col];
                if(!firstTile .hasPiece()) {
                    if (row != 6) {
                        if(whiteListed(whiteList, firstTile)) {
                            avaliable.add(firstTile);
                        }
                    } else {
                        if(whiteListed(whiteList, firstTile)) {
                            avaliablePromotion.add((firstTile));
                        }
                    }
                    if(getTile().getRow() == 1) {
                        Tile secondTile = tiles[row+2][col];
                        if(!secondTile.hasPiece() && whiteListed(whiteList, secondTile)) {
                            avaliable.add(secondTile);
                        }
                    }
                }
            }
           if(withinBounds(row+1,col+1)) {
                Tile tile = tiles[row+1][col+1];
                if(whiteListed(whiteList, tile) && tile.hasPiece() && 
                        tile.getPiece().isWhite() != isWhite()) {
                    if(row != 6){
                        avaliable.add(tile);
                    } else {
                        avaliablePromotion.add(tile);
                    }
                }
            }
            if(withinBounds(row+1,col-1)) {
                Tile tile = tiles[row+1][col-1];
                if(whiteListed(whiteList, tile) && tile.hasPiece() && 
                        tile.getPiece().isWhite() != isWhite()) {
                    if(row != 6) {
                        avaliable.add(tile);
                    } else {
                        avaliablePromotion.add(tile);
                    }
                }
            }
            Move lastMove = controller.getLastMove();
            if(lastMove != null) {
                if(withinBounds(row,col-1) && withinBounds(row+1,col-1)) {
                    Tile toTake = tiles[row][col-1];
                    Tile toMove = tiles[row+1][col-1];
                    if(whiteListed(whiteList, toTake) && row == 4 && toTake.hasPiece() 
                            && toTake.getPiece().isPawn() && 
                            toTake.getPiece().isWhite() != isWhite() 
                            && lastMove.getNewPos()[1] == col-1 && lastMove.getNewPos()[0] == row
                            && lastMove.getOldPos()[1] == col-1 && lastMove.getOldPos()[0] == row+2) {
                        avaliableEnPassant.add(toMove);
                    }
                }
                if(withinBounds(row,col+1) && withinBounds(row+1,col+1)) {
                    Tile toTake = tiles[row][col+1];
                    Tile toMove = tiles[row+1][col+1];
                    if(whiteListed(whiteList, toTake) && row == 4 && toTake.hasPiece() 
                            && toTake.getPiece().isPawn() && 
                            toTake.getPiece().isWhite() != isWhite() 
                            && lastMove.getNewPos()[1] == col+1 && lastMove.getNewPos()[0] == row
                            && lastMove.getOldPos()[1] == col+1 && lastMove.getOldPos()[0] == row+2) {
                        avaliableEnPassant.add(toMove);
                    }
                }
            }
        } else {
            if(withinBounds(row-1,col)) {
                Tile firstTile = tiles[row-1][col];
                if(!firstTile.hasPiece()) {
                    if(row != 1) {
                        if(whiteListed(whiteList, firstTile)) {
                            avaliable.add(firstTile);
                        }    
                    } else {
                        if(whiteListed(whiteList, firstTile)) {
                            avaliablePromotion.add(firstTile);
                        } 
                    }
                    if(getTile().getRow() == 6 ) {
                        Tile secondTile = tiles[row-2][col];
                        if(!secondTile.hasPiece() && whiteListed(whiteList, secondTile)) {
                            avaliable.add(secondTile);
                        }
                    }
                }
            }
            if(withinBounds(row-1,col+1)) {
                Tile tile = tiles[row-1][col+1];
                if(whiteListed(whiteList, tile) && tile.hasPiece() && 
                        tile.getPiece().isWhite() != isWhite()) {
                    if(row != 1) {
                        avaliable.add(tile);
                    } else {
                        avaliablePromotion.add(tile);
                    }
                }
            }
            if(withinBounds(row-1,col-1)) {
                Tile tile = tiles[row-1][col-1];
                if(whiteListed(whiteList, tile) && tile.hasPiece() && 
                        tile.getPiece().isWhite() != isWhite()) {
                    if(row != 1) {
                        avaliable.add(tile);
                    } else {
                        avaliablePromotion.add(tile);
                    }
                }
            }
            Move lastMove = controller.getLastMove();
            if(lastMove != null) {
                if(withinBounds(row,col-1) && withinBounds(row-1,col-1)) {
                    Tile toTake = tiles[row][col-1];
                    Tile toMove = tiles[row-1][col-1];
                    if(whiteListed(whiteList, toTake) && row == 3 && toTake.hasPiece() 
                            && toTake.getPiece().isPawn() && 
                            toTake.getPiece().isWhite() != isWhite() 
                            && lastMove.getNewPos()[1] == col-1 && lastMove.getNewPos()[0] == row
                            && lastMove.getOldPos()[1] == col-1 && lastMove.getOldPos()[0] == row-2) {
                        avaliableEnPassant.add(toMove);
                    }
                }
                if(withinBounds(row,col+1) && withinBounds(row-1,col-1)) {
                    Tile toTake = tiles[row][col+1];
                    Tile toMove = tiles[row-1][col+1];
                    if(whiteListed(whiteList, toTake) && row == 3 && toTake.hasPiece() 
                            && toTake.getPiece().isPawn() && 
                            toTake.getPiece().isWhite() != isWhite() 
                            && lastMove.getNewPos()[1] == col+1 && lastMove.getNewPos()[0] == row
                            && lastMove.getOldPos()[1] == col+1 && lastMove.getOldPos()[0] == row-2) {
                        avaliableEnPassant.add(toMove);
                    }
                }
            }
        }
    }
    
    /**
     * Overrides render method to include avaliable tiles for enPassant and Promotion
     */
    @Override
    protected void renderSelectables() {
        super.renderSelectables();
        avaliableEnPassant.forEach((avaliableTile) -> {
            getController().addEnPassantSelectable(avaliableTile, isWhite() ? -1 : 1);
        });
        avaliablePromotion.forEach((avaliableTile) -> {
            getController().addPromotionSelectable(avaliableTile);
        });
    }
    
    /**
     * Overrides render method to include avaliable tiles for enPassant and Promotion
     */
    @Override
    protected void renderVisualizables() {
        super.renderVisualizables();
        avaliableEnPassant.forEach((avaliableTile) -> {
            getController().addVisualizable(avaliableTile);
        });
        avaliablePromotion.forEach((avaliableTile) -> {
            getController().addVisualizable(avaliableTile);
        });
    }
    
    @Override
    public ArrayList<int[]> calcCommonPieceLocations(int[] location) {
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = location[0];
        int col = location[1];
        ArrayList<int[]> locations = new ArrayList<>();
        Tile tileLoc = tiles[row][col];
        Tile tileBelow =  isWhite() ? tiles[row-1][col] : tiles[row+1][col];
        if(tileLoc.hasPiece() || (tileBelow.hasPiece() && tileBelow.getPiece().isPawn() && tileBelow.getPiece() != this)) {
            if(isWhite()) {
                if(withinBounds(row-1,col+1)) {
                    Tile tile = tiles[row-1][col+1];
                    if((tile.hasPiece() && tile.getPiece().isWhite() == isWhite() && tile.getPiece() != this && tile.getPiece().isPawn())) {
                        int[] loc = {row-1,col+1};
                        locations.add(loc);
                    }
                }
                if(withinBounds(row-1,col-1)) {
                    Tile tile = tiles[row-1][col-1];
                    if((tile.hasPiece() && tile.getPiece().isWhite() == isWhite() && tile.getPiece() != this && tile.getPiece().isPawn())) {
                        int[] loc = {row-1,col-1};
                        locations.add(loc);
                    }
                }
            } else {
                if(withinBounds(row+1,col+1)) {
                    Tile tile = tiles[row+1][col+1];
                    if((tile.hasPiece() && tile.getPiece().isWhite() == isWhite() && tile.getPiece() != this && tile.getPiece().isPawn())) {
                        int[] loc = {row+1,col+1};
                        locations.add(loc);
                    }
                }
                if(withinBounds(row+1,col-1)) {
                    Tile tile = tiles[row+1][col-1];
                    if((tile.hasPiece() && tile.getPiece().isWhite() == isWhite() && tile.getPiece() != this && tile.getPiece().isPawn())) {
                        int[] loc = {row+1,col-1};
                        locations.add(loc);
                    }
                }
            }
        }
        return locations;
    }
    
    @Override
    protected boolean hasLegalMoves() {
        return !getAvaliable().isEmpty() || !avaliablePromotion.isEmpty() || !avaliableEnPassant.isEmpty();
    }
    
    @Override
    public boolean isPawn() {
        return true;
    }
    
    @Override
    public String getNotation() {
        return "";
    }
    
    @Override
    public int getValue() {
        return 1;
    }
    
    @Override
    public byte getInfoCode() {
        return isWhite() ? GameInfo.WHITE_PAWN : GameInfo.BLACK_PAWN;
    }
}
