/*
 * Class for the King StackPane GUI and functionality
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
public final class King extends Piece{
    
    /*
     * Attack pattern:
     * O    O    O    O    O    O    O    O 
     * O    O    O    O    O    O    O    O 
     * O    O    O    O    O    O    O    O 
     * O    O    X    X    X    O    O    O 
     * O    O    X    K    X    O    O    O 
     * O    O    X    X    X    O    O    O 
     * O    O    O    O    O    O    O    O 
     * O    O    O    O    O    O    O    O 
     */
    
    private final ArrayList<Tile> availableCastle = new ArrayList<>();
    
    public String whiteKing;
    public String blackKing;
    
    public final void setPaths(String path) {
        whiteKing = "/resources/" + path + "/whiteking.png";
        blackKing = "/resources/" + path + "/blackking.png";
    }
    
    /**
     * Constructs a King
     * 
     * @param isWhite side of the piece
     * @param tile tile piece belongs to
     * @param path for image
    */
    public King(boolean isWhite, Tile tile, String path) {
        super(isWhite, tile);
        setPaths(path);
        Image image;
        if(isWhite) {
            image = new Image(whiteKing);
        } else {
            image = new Image(blackKing);
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(tileSize);
        imageView.setFitWidth(tileSize);
        this.getChildren().add(imageView);
    }

    @Override
    public void pieceAvailableMoves() {
        availableCastle.clear();
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = getTile().getRow();
        int col = getTile().getCol();
        ArrayList<Tile> available = getAvailable();
        available.addAll(controller.kingMoves());
        
        //castle
        int bottomRow = isWhite() ? 0 : 7;
        if (row == bottomRow && col == 4) {
            Tile rookTile = tiles[row][col+3];
            if (controller.getAttackingKing().isEmpty()
                    && !controller.inCheck(this, row, col+1) && !controller.inCheck(this, row, col+2)
                    && !tiles[row][col+1].hasPiece() && !tiles[row][col+2].hasPiece()
                    && rookTile.hasPiece() && rookTile.getPiece().isRook()
                    && controller.canCastle(this, true)) {
                availableCastle.add(tiles[row][col+2]);
            }
            rookTile = tiles[row][col-4];
            if (controller.getAttackingKing().isEmpty()
                    && !controller.inCheck(this, row, col-1) && !controller.inCheck(this, row, col-2)
                    && !tiles[row][col-1].hasPiece() && !tiles[row][col-2].hasPiece()
                    && rookTile.hasPiece() && rookTile.getPiece().isRook()
                    && controller.canCastle(this, false)) {
                availableCastle.add(tiles[row][col-2]);
            }
        }
    }
    
    @Override
    public void pieceAvailableMoves(ArrayList<Tile> whiteList) {
        availableCastle.clear();
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = getTile().getRow();
        int col = getTile().getCol();
        ArrayList<Tile> available = getAvailable();
        available.addAll(controller.kingMoves());
        
        //castle
        int bottomRow = isWhite() ? 0 : 7;
        if (row == bottomRow && col == 4) {
            Tile rookTile = tiles[row][col+3];
            if (controller.getAttackingKing().isEmpty()
                    && !controller.inCheck(this, row, col+1) && !controller.inCheck(this, row, col+2)
                    && !tiles[row][col+1].hasPiece() && !tiles[row][col+2].hasPiece()
                    && rookTile.hasPiece() && rookTile.getPiece().isRook()
                    && controller.canCastle(this, true)) {
                availableCastle.add(tiles[row][col+2]);
            } 
            rookTile = tiles[row][col-4];
            if (controller.getAttackingKing().isEmpty()
                    && !controller.inCheck(this, row, col-1) && !controller.inCheck(this, row, col-2)
                    && !tiles[row][col-1].hasPiece() && !tiles[row][col-2].hasPiece()
                    && rookTile.hasPiece() && rookTile.getPiece().isRook()
                    && controller.canCastle(this, false)) {
                availableCastle.add(tiles[row][col-2]);
            }
        }
    }
    
    /**
     * Overrides render method to include available tiles for Castling
     */
    @Override
    protected void renderSelectable() {
        super.renderSelectable();
        availableCastle.forEach((availableTile) -> {
            boolean kingSide = availableTile.getCol() > 4;
            getController().addCastleSelectable(availableTile, isWhite(), kingSide);
        });
    }
    
    /**
     * Overrides render method to include available tiles for Castling
     */
    @Override
    protected void renderVisualize() {
        super.renderVisualize();
        availableCastle.forEach((availableTile) -> getController().addVisualize(availableTile));
    }
    
    @Override
    public ArrayList<int[]> calcCommonPieceLocations(int[] location) {
        return new ArrayList<>();
    }
    
    @Override
    public boolean isKing() {
        return true;
    }
    
    @Override
    public String getNotation() {
        return "K";
    }
    
    @Override
    public int getValue() {
        return -1;
    }
    
    @Override
    public byte getInfoCode() {
        return isWhite() ? GameInfo.WHITE_KING : GameInfo.BLACK_KING;
    }
}
