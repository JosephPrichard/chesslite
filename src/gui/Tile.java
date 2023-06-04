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
import javafx.scene.text.Font;

import java.util.Objects;

/**
 *
 * @author Joseph
 */
public final class Tile extends StackPane {

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
    
    public double tileSize;
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
    
    public double getTileSize() {
        return tileSize;
    }

    private void setTileSize(double scale) {
        this.tileSize = 100*scale;
    }

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

    public void setXReal(double xReal) {
        this.xReal = xReal;
    }

    public void setYReal(double yReal) {
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

    public double getXReal() {
        return xReal;
    }

    public double getYReal() {
        return yReal;
    }

    public boolean isLight() {
        return isLight;
    }

    public boolean isSelected() {
        return isSelected;
    }
    
    public boolean hasKing() {
        return hasPiece() && Objects.requireNonNull(getPiece()).isKing();
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
        lbl.setFont(new Font("Roboto",17*getController().getApp().getScale()));
        this.getChildren().add(lbl);
    }
    
    public Color getHighlight() {
        Color[] colors = {HIGHLIGHT, BLUE_HIGHLIGHT, HIGHLIGHT, HIGHLIGHT};
        return colors[controller.getApp().getColorTheme()];
    }
    
    public void setHighLighted() {
        highlight.setFill(getHighlight());
    }
    
    public void setUnHighLighted() {
        highlight.setFill(Color.TRANSPARENT);
    }
    
    public Color getDarkColor() {
        Color[] colors = {DARK_BROWN, DARK_BLUE, DARK_GREEN, DARK_RED};
        return colors[controller.getApp().getColorTheme()];
    }
    
    public Color getLightColor() {
        Color[] colors = {LIGHT, LIGHTER, LIGHTER, LIGHTER};
        return colors[controller.getApp().getColorTheme()];
    }
    
    public Color getDarkSelectedColor() {
        Color[] colors = {SELECTED_DARK_0,SELECTED_DARK_1,SELECTED_DARK_2,SELECTED_DARK_3};
        return colors[controller.getApp().getColorTheme()];
    }
    
    public Color getLightSelectedColor() {
        Color[] colors = {SELECTED_LIGHT_0,SELECTED_LIGHT_1,SELECTED_LIGHT_2,SELECTED_LIGHT_3};
        return colors[controller.getApp().getColorTheme()];
    }
    public Color getCheckColor() {
        Color[] colors = {CHECK,CHECK,CHECK,CHECK_ORANGE};
        return colors[controller.getApp().getColorTheme()];
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
        setTileSize(controller.getApp().getScale());
        this.isLight = light;
        this.rowBoard = row;
        this.colBoard = col;
        this.controller = controller;
        
        if(boardIsWhite) {
            xReal = col * tileSize;
            yReal = ((Game.WIDTH-1) * tileSize) - (row * tileSize);
            relocate(xReal, yReal);
        } else {
            xReal = ((Game.HEIGHT-1) * tileSize) - (col * tileSize);
            yReal = row * tileSize;
            relocate(xReal, yReal);
        }
        
        setOnMousePressed(e -> {
            Tile last = getController().getSelectedTile();
            if (last != null) {
                last.getPiece().setCloseable(false);
            }
            getController().clearSelectable();
        });
        
        rec = new Rectangle();
        rec.setWidth(tileSize);
        rec.setHeight(tileSize);
        setUnselected();
        
        highlight = new Rectangle();
        highlight.setWidth(tileSize);
        highlight.setHeight(tileSize);
        setUnHighLighted();
        
        Rectangle rect = new Rectangle(0, 0, tileSize, tileSize);
        Circle round = new Circle(tileSize / 2, tileSize / 2, (tileSize) / 2);
        checkShape = Shape.subtract(rect, round);
        checkShape.setFill(getCheckColor());
        
        Label testLabel = new Label(row + "," + col);
        testLabel.setId("tinyfont");
        StackPane.setAlignment(testLabel, Pos.TOP_LEFT);
        
        getChildren().addAll(rec,highlight);
    }
    
}
