/*
 * A tile for the GUI class.
 * 7/4/20
 */
package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *
 * @author Joseph
 */
public final class Tile extends StackPane {

    public static final double TILE_SIZE = 100*ChessLite.SCALE;
    public static final Color CHECK = Color.rgb(139,0,0,0.7);
    public static final Color CHECK_ORANGE = Color.rgb(200,120,0,0.7);
    public static final Color LIGHT_SELECTED = Color.rgb(233,217,0,0.2);
    public static final Color DARK_RED = Color.rgb(162,88,71); 
    public static final Color DARK_BLUE = Color.rgb(111,135,170);
    public static final Color DARK_BROWN = Color.rgb(168,138,101);
    public static final Color DARK_GREEN = Color.rgb(129,149,90);
    public static final Color SELECTED_DARK_0 = Color.rgb(211,194,91);
    public static final Color SELECTED_LIGHT_0 = Color.rgb(246,245,146);
    public static final Color SELECTED_DARK_1 = Color.rgb(96,141,201);
    public static final Color SELECTED_LIGHT_1 = Color.rgb(167,200,231);
    public static final Color SELECTED_DARK_2 = Color.rgb(192,201,85);
    public static final Color SELECTED_LIGHT_2 = Color.rgb(246,245,146);
    public static final Color SELECTED_DARK_3 = Color.rgb(204,167,103);
    public static final Color SELECTED_LIGHT_3 = Color.rgb(246,245,146);
    public static final Color LIGHT = Color.rgb(240,217,181); 
    public static final Color LIGHTER = Color.rgb(223,223,211);
    public static final Color HIGHLIGHT = Color.rgb(233,217,100,0.5);
    public static final Color BLUE_HIGHLIGHT = Color.rgb(85,156,185,0.7);
    private int rowBoard; //position relative to board
    private int colBoard;
    private double xReal; //real GUI position
    private double yReal;
    private boolean isLight;
    private boolean isSelected;
    private Piece piece; //piece on tile
    private final Game controller; //controller to redirect flow to
    private final Rectangle rec; //rendered shapes
    private final Rectangle highlight;
    private final Shape checkShape;

    public Game getController() {
        return controller;
    }
    
    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    public Boolean hasPiece() {
        return piece != null;
    }
    
    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public void setRowBoard(int rowBoard) {
        this.rowBoard = rowBoard;
    }

    public void setColBoard(int colBoard) {
        this.colBoard = colBoard;
    }

    public void setxReal(double xReal) {
        this.xReal = xReal;
    }

    public void setyReal(double yReal) {
        this.yReal = yReal;
    }

    public void setIsLight(boolean isLight) {
        this.isLight = isLight;
    }

    public int getRow(){
        return rowBoard;
    }

    public int getCol() {
        return colBoard;
    }

    public double getxReal() {
        return xReal;
    }

    public double getyReal() {
        return yReal;
    }

    public boolean isLight() {
        return isLight;
    }

    public boolean isSelected() {
        return isSelected;
    }
    
    public boolean hasKing() {
        return hasPiece() && getPiece().isKing();
    }
    
    /**
     * Adds label to tile
     * @param lbl to be added (typically the tile row or col position)
     */
    public void addLabel(Label lbl) {
        if(isLight) {
            lbl.setId("darkfont");
        } else {
            lbl.setId("lightfont");
        }
        this.getChildren().add(lbl);
    }
    
    public static Color getHighlight() {
        Color[] colors = {HIGHLIGHT, BLUE_HIGHLIGHT, HIGHLIGHT, HIGHLIGHT};
        return colors[ChessLite.COLOR_THEME];
    }
    
    public void setHighLighted() {
        highlight.setFill(getHighlight());
    }
    
    public void setUnHighLighted() {
        highlight.setFill(Color.TRANSPARENT);
    }
    
    public static Color getDarkColor() {
        Color[] colors = {DARK_BROWN, DARK_BLUE, DARK_GREEN, DARK_RED};
        return colors[ChessLite.COLOR_THEME];
    }
    
    public static Color getLightColor() {
        Color[] colors = {LIGHT, LIGHTER, LIGHTER, LIGHTER};
        return colors[ChessLite.COLOR_THEME];
    }
    
    public static Color getDarkSelectedColor() {
        Color[] colors = {SELECTED_DARK_0,SELECTED_DARK_1,SELECTED_DARK_2,SELECTED_DARK_3};
        return colors[ChessLite.COLOR_THEME];
    }
    
    public static Color getLightSelectedColor() {
        Color[] colors = {SELECTED_LIGHT_0,SELECTED_LIGHT_1,SELECTED_LIGHT_2,SELECTED_LIGHT_3};
        return colors[ChessLite.COLOR_THEME];
    }
    public static Color getCheckColor() {
        Color[] colors = {CHECK,CHECK,CHECK,CHECK_ORANGE};
        return colors[ChessLite.COLOR_THEME];
    }
    
    public void setUnselectedNoReset() {
        isSelected = false;
        rec.setFill(isLight ? getLightColor() : getDarkColor());
    }
    
    public void setUnselected() {
        isSelected = false;
        rec.setFill(isLight ? getLightColor() : getDarkColor());
    }
    
    public void setSelected() {
        isSelected = true;
        rec.setFill(isLight ? getLightSelectedColor() : getDarkSelectedColor());
    }
    
    public void setInCheck() {
        getChildren().remove(checkShape);
        getChildren().add(checkShape);
    }
    
    public void setOffCheck() {
        getChildren().remove(checkShape);
    }
    
    /**
     * Performs a move from the piece on this tile to another given tile
     * @param tile to move to
     */
    public void movePiece(Tile tile) {
        tile.setPiece(this.getPiece()); 
        this.getPiece().setTile(tile);
        this.getPiece().moveToSlowly(tile);
        this.setPiece(null);
    }
    
    /**
     * Performs an EnPassant move from the piece on this tile to another given tile
     * @param tile to move to
     * @param taken, the piece to be Taken en passant
     */
    public void movePieceEnPassant(Tile tile, Tile taken) {
        tile.setPiece(this.getPiece()); 
        this.getPiece().setTile(tile);
        this.getPiece().moveToSlowly(tile);
        taken.setPiece(null);
        this.setPiece(null);
    }
    
    /**
     * Constructs a Tile object
     * @param light, whether or not the tile is light
     * @param row of tile on board
     * @param col of tile on board
     * @param boardIsWhite point of view board is viewed from
     * @param controller, the Game controller the Tile belongs to, and will redirect
     * flow back to
     */
    public Tile(boolean light, int row, int col, boolean boardIsWhite, Game controller) {
        this.isLight = light;
        this.rowBoard = row;
        this.colBoard = col;
        this.controller = controller;
        
        if(boardIsWhite) {
            xReal = col * TILE_SIZE;
            yReal = ((Game.HEIGHT-1) * Tile.TILE_SIZE) - (row * TILE_SIZE);
            relocate(xReal, yReal);
        } else {
            xReal = ((Game.WIDTH-1) * Tile.TILE_SIZE) - (col * TILE_SIZE);
            yReal = row * TILE_SIZE;
            relocate(xReal, yReal);
        }
        
        setOnMousePressed(e -> {
            Tile last = getController().getSelectedTile();
            if (last != null) {
                last.getPiece().setCloseable(false);
            }
            getController().clearSelectables();
        });
        
        rec = new Rectangle();
        rec.setWidth(TILE_SIZE);
        rec.setHeight(TILE_SIZE);
        setUnselected();
        
        highlight = new Rectangle();
        highlight.setWidth(TILE_SIZE);
        highlight.setHeight(TILE_SIZE);
        setUnHighLighted();
        
        Rectangle rect = new Rectangle(0, 0, TILE_SIZE, TILE_SIZE);
        Circle circ = new Circle(TILE_SIZE / 2, TILE_SIZE / 2, Math.min(TILE_SIZE, TILE_SIZE) / 2);
        checkShape = Shape.subtract(rect, circ);
        checkShape.setFill(getCheckColor());
        
        Label testLabel = new Label(row + "," + col);
        testLabel.setId("tinyfont");
        StackPane.setAlignment(testLabel, Pos.TOP_LEFT);
        
        getChildren().addAll(rec,highlight);
    }
    
}
