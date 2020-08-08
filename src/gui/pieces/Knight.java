/*
 * Class for the Knight StackPane GUI and functionality
 * 7/3/20
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
public final class Knight extends Piece{
    
    /**
     * Attack pattern:
     * O    O    O    O    O    O    O    O 
     * O    O    O    O    O    O    O    O 
     * O    O    X    O    X    O    O    O 
     * O    X    O    O    O    X    O    O 
     * O    O    O    N    O    O    O    O 
     * O    X    O    O    O    X    O    O 
     * O    O    X    O    X    O    O    O 
     * O    O    O    O    O    O    O    O 
     */
    
    public final String WHITE_KNIGHT = "/resources/" + ChessLite.PATH + "/whiteknight.png";
    public final String BLACK_KNIGHT = "/resources/" + ChessLite.PATH + "/blackknight.png";
    
    /**
     * Constructs a Knight
     * 
     * @param isWhite side of the piece
     * @param tile tile piece belongs to
    */
    public Knight(boolean isWhite, Tile tile) {
        super(isWhite, tile);
        Image image;
        if(isWhite) {
            image = new Image(WHITE_KNIGHT);
        } else {
            image = new Image(BLACK_KNIGHT);
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
        
        int[][] offsets = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};
        for(int[] offset : offsets) {
            if(withinBounds(row+offset[0],col+offset[1])) {
                Tile tile = tiles[row+offset[0]][col+offset[1]];
                if(!tile.hasPiece() || (tile.getPiece().isWhite() != isWhite())) {
                    avaliable.add(tile);
                }
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
        int[][] offsets = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};
        for(int[] offset : offsets) {
            if(withinBounds(row+offset[0],col+offset[1])) {
                Tile tile = tiles[row+offset[0]][col+offset[1]];
                if(whiteListed(whiteList, tile) && 
                        (!tile.hasPiece() || (tile.getPiece().isWhite() != isWhite()))) {
                    avaliable.add(tile);
                }
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
        int[][] offsets = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};
        for(int[] offset : offsets) {
            if(withinBounds(row+offset[0],col+offset[1])) {
                Tile tile = tiles[row+offset[0]][col+offset[1]];
                if((tile.hasPiece() && (tile.getPiece().isWhite() == isWhite()) 
                        && tile.getPiece().isKnight()) && tile.getPiece() != this) {
                    int[] loc = {row+offset[0],col+offset[1]};
                    locations.add(loc);
                }
            }
        }
        return locations;
    }
    
    @Override
    public boolean isKnight() {
        return true;
    }
    
    @Override
    public String getNotation() {
        return "N";
    }
    
    @Override
    public int getValue() {
        return 3;
    }
    
    @Override
    public byte getInfoCode() {
        return isWhite() ? GameInfo.WHITE_KNIGHT : GameInfo.BLACK_KNIGHT;
    }
}
