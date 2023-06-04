/*
 * Class for the Pawn StackPane GUI and functionality
 * 6/3/20
 */
package gui.pieces;

import gui.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

/**
 *
 * @author Joseph
 */
public final class Pawn extends Piece{
    
     /*
     * Attack pattern: 
     * Can only move to - M 
     * Only available if piece to Take - T
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
    
    private final ArrayList<Tile> availableEnPassant = new ArrayList<>();
    private final ArrayList<Tile> availablePromotion = new ArrayList<>();
    
    public String whitePawn;
    public String blackPawn;
    
    public final void setPaths(String path) {
        whitePawn = "/resources/" + path + "/whitepawn.png";
        blackPawn = "/resources/" + path + "/blackpawn.png";
    }
    
    /**
     * Constructs a Pawn
     * 
     * @param isWhite side of the piece
     * @param tile tile piece belongs to
     * @param path, path for image
    */
    public Pawn(boolean isWhite, Tile tile, String path) {
        super(isWhite, tile);
        setPaths(path);
        Image image;
        if(isWhite) {
            image = new Image(whitePawn);
        } else {
            image = new Image(blackPawn);
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(tileSize);
        imageView.setFitWidth(tileSize);
        this.getChildren().add(imageView);
    }

    @Override
    public void pieceAvailableMoves() {
        availableEnPassant.clear();
        availablePromotion.clear();
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = getTile().getRow();
        int col = getTile().getCol();
        ArrayList<Tile> available = getAvailable();
        
        if(isWhite()) {
            if(withinBounds(row+1,col)) {
                Tile firstTile = tiles[row+1][col];
                if(!firstTile .hasPiece()) {
                    if (row != 6) {
                        available.add(firstTile);
                    } else {
                        availablePromotion.add((firstTile));
                    }
                    if(getTile().getRow() == 1) {
                        Tile secondTile = tiles[row+2][col];
                        if(!secondTile.hasPiece()) {
                            available.add(secondTile);
                        }
                    }
                }
            }
           if(withinBounds(row+1,col+1)) {
                Tile tile = tiles[row+1][col+1];
                if(tile.hasPiece() && 
                        tile.getPiece().isWhite() != isWhite()) {
                    if(row != 6){
                        available.add(tile);
                    } else {
                        availablePromotion.add(tile);
                    }
                }
            }
            if(withinBounds(row+1,col-1)) {
                Tile tile = tiles[row+1][col-1];
                if(tile.hasPiece() && 
                        tile.getPiece().isWhite() != isWhite()) {
                    if(row != 6) {
                        available.add(tile);
                    } else {
                        availablePromotion.add(tile);
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
                        availableEnPassant.add(toMove);
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
                        availableEnPassant.add(toMove);
                    }
                }
            }
        } else {
            if(withinBounds(row-1,col)) {
                Tile firstTile = tiles[row-1][col];
                if(!firstTile.hasPiece()) {
                    if(row != 1) {
                        available.add(firstTile);
                    } else {
                        availablePromotion.add(firstTile);
                    }
                    if(getTile().getRow() == 6) {
                        Tile secondTile = tiles[row-2][col];
                        if(!secondTile.hasPiece()) {
                            available.add(secondTile);
                        }
                    }
                }
            }
            if(withinBounds(row-1,col+1)) {
                Tile tile = tiles[row-1][col+1];
                if(tile.hasPiece() && tile.getPiece().isWhite() != isWhite()) {
                    if(row != 1) {
                        available.add(tile);
                    } else {
                        availablePromotion.add(tile);
                    }
                }
            }
            if(withinBounds(row-1,col-1)) {
                Tile tile = tiles[row-1][col-1];
                if(tile.hasPiece() && tile.getPiece().isWhite() != isWhite()) {
                    if(row != 1) {
                        available.add(tile);
                    } else {
                        availablePromotion.add(tile);
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
                        availableEnPassant.add(toMove);
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
                        availableEnPassant.add(toMove);
                    }
                }
            }
        }
    }
    
    @Override
    public void pieceAvailableMoves(ArrayList<Tile> whiteList) {
        availableEnPassant.clear();
        availablePromotion.clear();
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = getTile().getRow();
        int col = getTile().getCol();
        ArrayList<Tile> available = getAvailable();
        
        if(isWhite()) {
            if(withinBounds(row+1,col)) {
                Tile firstTile = tiles[row+1][col];
                if(!firstTile .hasPiece()) {
                    if (row != 6) {
                        if(whiteListed(whiteList, firstTile)) {
                            available.add(firstTile);
                        }
                    } else {
                        if(whiteListed(whiteList, firstTile)) {
                            availablePromotion.add((firstTile));
                        }
                    }
                    if(getTile().getRow() == 1) {
                        Tile secondTile = tiles[row+2][col];
                        if(!secondTile.hasPiece() && whiteListed(whiteList, secondTile)) {
                            available.add(secondTile);
                        }
                    }
                }
            }
           if(withinBounds(row+1,col+1)) {
                Tile tile = tiles[row+1][col+1];
                if(whiteListed(whiteList, tile) && tile.hasPiece() && 
                        tile.getPiece().isWhite() != isWhite()) {
                    if(row != 6){
                        available.add(tile);
                    } else {
                        availablePromotion.add(tile);
                    }
                }
            }
            if(withinBounds(row+1,col-1)) {
                Tile tile = tiles[row+1][col-1];
                if(whiteListed(whiteList, tile) && tile.hasPiece() && 
                        tile.getPiece().isWhite() != isWhite()) {
                    if(row != 6) {
                        available.add(tile);
                    } else {
                        availablePromotion.add(tile);
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
                        availableEnPassant.add(toMove);
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
                        availableEnPassant.add(toMove);
                    }
                }
            }
        } else {
            if(withinBounds(row-1,col)) {
                Tile firstTile = tiles[row-1][col];
                if(!firstTile.hasPiece()) {
                    if(row != 1) {
                        if(whiteListed(whiteList, firstTile)) {
                            available.add(firstTile);
                        }    
                    } else {
                        if(whiteListed(whiteList, firstTile)) {
                            availablePromotion.add(firstTile);
                        } 
                    }
                    if(getTile().getRow() == 6 ) {
                        Tile secondTile = tiles[row-2][col];
                        if(!secondTile.hasPiece() && whiteListed(whiteList, secondTile)) {
                            available.add(secondTile);
                        }
                    }
                }
            }
            if(withinBounds(row-1,col+1)) {
                Tile tile = tiles[row-1][col+1];
                if(whiteListed(whiteList, tile) && tile.hasPiece() && 
                        tile.getPiece().isWhite() != isWhite()) {
                    if(row != 1) {
                        available.add(tile);
                    } else {
                        availablePromotion.add(tile);
                    }
                }
            }
            if(withinBounds(row-1,col-1)) {
                Tile tile = tiles[row-1][col-1];
                if(whiteListed(whiteList, tile) && tile.hasPiece() && 
                        tile.getPiece().isWhite() != isWhite()) {
                    if(row != 1) {
                        available.add(tile);
                    } else {
                        availablePromotion.add(tile);
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
                        availableEnPassant.add(toMove);
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
                        availableEnPassant.add(toMove);
                    }
                }
            }
        }
    }
    
    /**
     * Overrides render method to include available tiles for enPassant and Promotion
     */
    @Override
    protected void renderSelectable() {
        super.renderSelectable();
        availableEnPassant.forEach((availableTile) -> getController().addEnPassantSelectable(availableTile, isWhite() ? -1 : 1));
        availablePromotion.forEach((availableTile) -> getController().addPromotionSelectable(availableTile));
    }
    
    /**
     * Overrides render method to include available tiles for enPassant and Promotion
     */
    @Override
    protected void renderVisualize() {
        super.renderVisualize();
        availableEnPassant.forEach((availableTile) -> getController().addVisualize(availableTile));
        availablePromotion.forEach((availableTile) -> getController().addVisualize(availableTile));
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
        return !getAvailable().isEmpty() || !availablePromotion.isEmpty() || !availableEnPassant.isEmpty();
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
