/*
 * Class for the King StackPane GUI and functionality
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
    
    private final ArrayList<Tile> avaliableCastle = new ArrayList<>();
    
    public final String WHITE_KING = "/resources/" + ChessLite.PATH + "/whiteking.png";
    public final String BLACK_KING = "/resources/" + ChessLite.PATH + "/blackking.png";
    
    /**
     * Constructs a King
     * 
     * @param isWhite side of the piece
     * @param tile tile piece belongs to
    */
    public King(boolean isWhite, Tile tile) {
        super(isWhite, tile);
        Image image;
        if(isWhite) {
            image = new Image(WHITE_KING);
        } else {
            image = new Image(BLACK_KING);
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(TILE_SIZE);
        imageView.setFitWidth(TILE_SIZE);
        this.getChildren().add(imageView);
    }

    @Override
    public void pieceAvaliableMoves() {
        avaliableCastle.clear();
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = getTile().getRow();
        int col = getTile().getCol();
        ArrayList<Tile> avaliable = getAvaliable();
        controller.kingMoves().forEach((Tile tile) -> {
            avaliable.add(tile);
        });
        
        //castle
        int bottomRow = isWhite() ? 0 : 7;
        if (row == bottomRow && col == 4) {
            Tile rookTile = tiles[row][col+3];
            if (controller.getAttackingKing().isEmpty()
                    && !controller.inCheck(this, row, col+1) && !controller.inCheck(this, row, col+2)
                    && !tiles[row][col+1].hasPiece() && !tiles[row][col+2].hasPiece()
                    && rookTile.hasPiece() && rookTile.getPiece().isRook()
                    && controller.canCastle(this, true)) {
                avaliableCastle.add(tiles[row][col+2]);
            }
            rookTile = tiles[row][col-4];
            if (controller.getAttackingKing().isEmpty()
                    && !controller.inCheck(this, row, col-1) && !controller.inCheck(this, row, col-2)
                    && !tiles[row][col-1].hasPiece() && !tiles[row][col-2].hasPiece()
                    && rookTile.hasPiece() && rookTile.getPiece().isRook()
                    && controller.canCastle(this, false)) {
                avaliableCastle.add(tiles[row][col-2]);
            }
        }
    }
    
    @Override
    public void pieceAvaliableMoves(ArrayList<Tile> whiteList) {
        avaliableCastle.clear();
        Game controller = getController();
        Tile[][] tiles = controller.getTiles();
        int row = getTile().getRow();
        int col = getTile().getCol();
        ArrayList<Tile> avaliable = getAvaliable();
        controller.kingMoves().forEach((Tile tile) -> {
            avaliable.add(tile);
        });
        
        //castle
        int bottomRow = isWhite() ? 0 : 7;
        if (row == bottomRow && col == 4) {
            Tile rookTile = tiles[row][col+3];
            if (controller.getAttackingKing().isEmpty()
                    && !controller.inCheck(this, row, col+1) && !controller.inCheck(this, row, col+2)
                    && !tiles[row][col+1].hasPiece() && !tiles[row][col+2].hasPiece()
                    && rookTile.hasPiece() && rookTile.getPiece().isRook()
                    && controller.canCastle(this, true)) {
                avaliableCastle.add(tiles[row][col+2]);
            } 
            rookTile = tiles[row][col-4];
            if (controller.getAttackingKing().isEmpty()
                    && !controller.inCheck(this, row, col-1) && !controller.inCheck(this, row, col-2)
                    && !tiles[row][col-1].hasPiece() && !tiles[row][col-2].hasPiece()
                    && rookTile.hasPiece() && rookTile.getPiece().isRook()
                    && controller.canCastle(this, false)) {
                avaliableCastle.add(tiles[row][col-2]);
            }
        }
    }
    
    /**
     * Overrides render method to include avaliable tiles for Castling
     */
    @Override
    protected void renderSelectables() {
        super.renderSelectables();
        avaliableCastle.forEach((avaliableTile) -> {
            boolean kingSide = avaliableTile.getCol() > 4;
            getController().addCastleSelectable(avaliableTile, isWhite(), kingSide);
        });
    }
    
    /**
     * Overrides render method to include avaliable tiles for Castling
     */
    @Override
    protected void renderVisualizables() {
        super.renderSelectables();
        avaliableCastle.forEach((avaliableTile) -> {
            getController().addVisualizable(avaliableTile);
        });
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
