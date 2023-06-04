/*
 * Class for the Bishop StackPane GUI and functionality
 * 7/3/20
 */
package gui.pieces;

import gui.Game;
import gui.GameInfo;
import gui.Piece;
import gui.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

/**
 *
 * @author Joseph
 */
public final class Bishop extends Piece {
    
    /*
     * Attack pattern:
     * O    O    O    O    O    O    O    X 
     * X    O    O    O    O    O    X    O 
     * O    X    O    O    O    X    O    O 
     * O    O    X    O    X    O    O    O 
     * O    O    O    B    O    O    O    O 
     * O    O    X    O    X    O    O    O 
     * O    X    O    O    O    X    O    O 
     * X    O    O    O    O    O    X    O 
     * 
     */
    
    public String whiteBishop;
    public String blackBishop;
    
    public final void setPaths(String path) {
        whiteBishop = "/resources/" + path + "/whitebishop.png";
        blackBishop = "/resources/" + path + "/blackbishop.png";
    }
    
    /**
     * Constructs a Bishop
     * 
     * @param isWhite side of the piece
     * @param tile tile piece belongs to
     * @param path for image
    */
    public Bishop(boolean isWhite, Tile tile, String path) {
        super(isWhite, tile);
        setPaths(path);
        Image image;
        if(isWhite) {
            image = new Image(whiteBishop);
        } else {
            image = new Image(blackBishop);
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(tileSize);
        imageView.setFitWidth(tileSize);
        this.getChildren().add(imageView);
    }
    
    @Override
    public void pieceAvailableMoves() {
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = getTile().getRow();
        int col = getTile().getCol();
        ArrayList<Tile> available = getAvailable();
        
        int[][] multipliers = {{1,1},{-1,1},{1,-1},{-1,-1}};
        for(int[] multiplier : multipliers) {
            int i = 1;
            boolean canContinue = true;
            while(canContinue) {
                if(withinBounds(row+(i*multiplier[0]),col+(i*multiplier[1]))) {
                    Tile tile = tiles[row+(i*multiplier[0])][col+(i*multiplier[1])];
                    if((!tile.hasPiece())) {
                        available.add((tile));
                    } else if((tile.getPiece().isWhite() != isWhite())) {
                        available.add((tile));
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
    public void pieceAvailableMoves(ArrayList<Tile> whiteList) {
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = getTile().getRow();
        int col = getTile().getCol();
        ArrayList<Tile> available = getAvailable();

        int[][] multipliers = {{1,1},{-1,1},{1,-1},{-1,-1}};
        for(int[] multiplier : multipliers) {
            int i = 1;
            boolean canContinue = true;
            while(canContinue) {
                if(withinBounds(row+(i*multiplier[0]),col+(i*multiplier[1]))) {
                    Tile tile = tiles[row+(i*multiplier[0])][col+(i*multiplier[1])];
                    if((!tile.hasPiece())) {
                        if(whiteListed(whiteList, tile)) {
                            available.add((tile));
                        }
                    } else if((tile.getPiece().isWhite() != isWhite())) {
                        if(whiteListed(whiteList, tile)) {
                            available.add((tile));
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
        int[][] multipliers = {{1,1},{-1,1},{1,-1},{-1,-1}};
        for(int[] multiplier : multipliers) {
            int i = 1;
            boolean canContinue = true;
            while(canContinue) {
                if(withinBounds(row+(i*multiplier[0]),col+(i*multiplier[1]))) {
                    Tile tile = tiles[row+(i*multiplier[0])][col+(i*multiplier[1])];
                    if(tile.hasPiece()) {
                        if((tile.getPiece().isWhite() == isWhite()) 
                                && tile.getPiece().isBishop() && tile.getPiece() != this) {
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
    public boolean isBishop() {
        return true;
    }
    
    @Override
    public String getNotation() {
        return "B";
    }
    
    @Override
    public int getValue() {
        return 3;
    }
    
    @Override
    public byte getInfoCode() {
        return isWhite() ? GameInfo.WHITE_BISHOP : GameInfo.BLACK_BISHOP;
    }

}
