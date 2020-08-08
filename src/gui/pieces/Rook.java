/*
 * Class for the Rook StackPane GUI and functionality
 * 7/3/20.
 */
package gui.pieces;

import gui.ChessLite;
import gui.Game;
import gui.Piece;
import gui.Tile;
import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import gui.GameInfo;

/**
 *
 * @author Joseph
 */
public final class Rook extends Piece{
    
    /*
     * Attack pattern: 
     * O    O    O    X    O    O    O    O 
     * O    O    O    X    O    O    O    O 
     * O    O    O    X    O    O    O    O 
     * O    O    O    X    O    O    O    O 
     * X    X    X    R    X    X    X    X 
     * O    O    O    X    O    O    O    O 
     * O    O    O    X    O    O    O    O 
     * O    O    O    X    O    O    O    O 
     */
    
    public final String WHITE_ROOK = "/resources/" + ChessLite.PATH + "/whiterook.png";
    public final String BLACK_ROOK = "/resources/" + ChessLite.PATH + "/blackrook.png";
    
    /**
     * Constructs a Rook
     * 
     * @param isWhite side of the piece
     * @param tile tile piece belongs to
    */
    public Rook(boolean isWhite, Tile tile) {
        super(isWhite, tile);
        Image image;
        if(isWhite) {
            image = new Image(WHITE_ROOK);
            
        } else {
            image = new Image(BLACK_ROOK);
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(TILE_SIZE);
        imageView.setFitWidth(TILE_SIZE);
        this.getChildren().add(imageView);
    }
    
    @Override
    public void pieceAvaliableMoves() {
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = getTile().getRow();
        int col = getTile().getCol();
        ArrayList<Tile> avaliable = getAvaliable();
        
        int[][] multipliers = {{1,0},{-1,0},{0,1},{0,-1}};
        for(int[] multiplier : multipliers) {
            int i = 1;
            boolean canContinue = true;
            while(canContinue) {
                if(withinBounds(row+(i*multiplier[0]),col+(i*multiplier[1]))) {
                    Tile tile = tiles[row+(i*multiplier[0])][col+(i*multiplier[1])];
                    if((!tile.hasPiece())) {
                        avaliable.add((tile));
                    } else if((tile.getPiece().isWhite() != isWhite())) {
                        avaliable.add((tile));
                        canContinue = false;
                    } else {
                        canContinue = false;
                    }
                } else {
                    canContinue = false;
                }
                i++;
            }
        }
    }
    
    @Override
    public void pieceAvaliableMoves(ArrayList<Tile> whiteList) {
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = getTile().getRow();
        int col = getTile().getCol();
        ArrayList<Tile> avaliable = getAvaliable();
        
        int[][] multipliers = {{1,0},{-1,0},{0,1},{0,-1}};
        for(int[] multiplier : multipliers) {
            int i = 1;
            boolean canContinue = true;
            while(canContinue) {
                if(withinBounds(row+(i*multiplier[0]),col+(i*multiplier[1]))) {
                    Tile tile = tiles[row+(i*multiplier[0])][col+(i*multiplier[1])];
                    if((!tile.hasPiece())) {
                        if(whiteListed(whiteList, tile)) {
                            avaliable.add((tile));
                        }
                    } else if((tile.getPiece().isWhite() != isWhite())) {
                        if(whiteListed(whiteList, tile)) {
                            avaliable.add((tile));
                        }
                        canContinue = false;
                    } else {
                        canContinue = false;
                    }
                } else {
                    canContinue = false;
                }
                i++;
            }
        }
    }
    
    @Override
    public ArrayList<int[]> calcCommonPieceLocations(int[] location) {
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = location[0];
        int col = location[1];
        ArrayList<int[]> locations = new ArrayList<>();
        int[][] multipliers = {{1,0},{-1,0},{0,1},{0,-1}};
        for(int[] multiplier : multipliers) {
            int i = 1;
            boolean canContinue = true;
            while(canContinue) {
                if(withinBounds(row+(i*multiplier[0]),col+(i*multiplier[1]))) {
                    Tile tile = tiles[row+(i*multiplier[0])][col+(i*multiplier[1])];
                    if(tile.hasPiece()) {
                        if((tile.getPiece().isWhite() == isWhite()) 
                                && tile.getPiece().isRook() && tile.getPiece() != this) {
                            int[] loc = {row+(i*multiplier[0]), col+(i*multiplier[1])};
                            locations.add(loc);
                        }
                        canContinue = false;
                    }
                } else {
                    canContinue = false;
                }
                i++;
            }
        }
        return locations;
    }
    
    @Override
    public boolean isRook() {
        return true;
    }
    
    @Override
    public String getNotation() {
        return "R";
    }
    
    @Override
    public int getValue() {
        return 4;
    }

    @Override
    public byte getInfoCode() {
        return isWhite() ? GameInfo.WHITE_ROOK : GameInfo.BLACK_ROOK;
    }
}
