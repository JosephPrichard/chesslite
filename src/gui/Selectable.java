/*
 * A selectable dot for the user to make moves with
 * 7/4/20
 */
package gui;

import gui.pieces.Bishop;
import gui.pieces.Knight;
import gui.pieces.Queen;
import gui.pieces.Rook;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static gui.ChessLite.APP_ICON_PATH;

/**
 *
 * @author Joseph
 */
public class Selectable extends Pane {
    
    public final String WHITE_QUEEN = "/resources/" + ChessLite.PATH + "/whitequeen.png";
    public final String BLACK_QUEEN = "/resources/" + ChessLite.PATH + "/blackqueen.png";
    public final String WHITE_KNIGHT = "/resources/" + ChessLite.PATH + "/whiteknight.png";
    public final String BLACK_KNIGHT = "/resources/" + ChessLite.PATH + "/blackknight.png";
    public final String WHITE_BISHOP = "/resources/" + ChessLite.PATH + "/whitebishop.png";
    public final String BLACK_BISHOP = "/resources/" + ChessLite.PATH + "/blackbishop.png";
    public final String WHITE_ROOK = "/resources/" + ChessLite.PATH + "/whiterook.png";
    public final String BLACK_ROOK = "/resources/" + ChessLite.PATH + "/blackrook.png";
    
    public static final double TILE_SIZE = 100*ChessLite.SCALE;
    public static final double SELECTABLE_SIZE = 15*ChessLite.SCALE;
    public static final double TILE_CIRCLE_SIZE = 45*ChessLite.SCALE;
    public static final Color GREY = Color.rgb(90,90,90,0.5); 
    public static final Color LIGHT_GREY = Color.rgb(110,110,110,0.5); 
    private final Tile tile;
    private final Game controller;
    private final Circle nopiece;
    private final Rectangle rec;
    private final Shape shape;
    private final Shape crown;

    public Tile getTile() {
        return tile;
    }

    public Game getController() {
        return controller;
    }
    
    /**
     * Constructs a selectable that can perform moves on GUI action, or perform moves
     * programmatically
     * @param tile, the tile selectable is on
     * @param controller, controller to redirect flow back to
     * @param hover, color of hover
     * @param ring, color of ring
     * @param solid, color of selectable
     * @param castle, color of castle ring 
     */
    public Selectable(Tile tile, Game controller, Color hover, Color ring, Color solid, Color castle) {
        this.setStyle("-fx-cursor: hand;");
        this.controller = controller;
        this.tile = tile;
        
        nopiece = new Circle();
        nopiece.setRadius(SELECTABLE_SIZE);
        nopiece.setFill(solid);
        nopiece.setLayoutX(TILE_SIZE / 2);
        nopiece.setLayoutY(TILE_SIZE / 2);

        Rectangle rect = new Rectangle(0, 0, TILE_SIZE, TILE_SIZE);
        Circle circ = new Circle(TILE_SIZE / 2, TILE_SIZE / 2, Math.min(TILE_SIZE, TILE_SIZE) / 2);
        shape = Shape.subtract(rect, circ);
        shape.setFill(ring);
       
        crown = Shape.subtract(rect, circ);
        crown.setFill(castle);
        
        rec = new Rectangle();
        rec.setWidth(TILE_SIZE);
        rec.setHeight(TILE_SIZE);
        rec.setFill(hover);
        
        setMinWidth(Tile.TILE_SIZE);
        setMinHeight(Tile.TILE_SIZE);
        
        setOnMouseClicked(e -> {
            controller.getSelectedTile().getPiece().setCloseable(false);
            move();
        });
    }

    public void setHighlightsNoHover() {
        if(!tile.hasPiece()) {
            setHasNoPiece();
        } else {
            setHasPiece();
        }
    }
    
    public void setHighlights() {
        if(!tile.hasPiece()) {
            setHasNoPieceHover();
        } else {
            setHasPieceHover();
        }
    }
    
    public void setHasNoPieceHover() {
        getChildren().add(nopiece);
        setOnMouseEntered(e -> {
            tile.getChildren().add(rec);
            getChildren().remove(nopiece);
        });
        setOnMouseExited(e -> {
            tile.getChildren().remove(rec);
            getChildren().add(nopiece);
        });
    }
    
    public void setHasPieceHover() {
        getChildren().add(shape);
        setOnMouseEntered(e -> {
            tile.getChildren().add(rec);
            getChildren().remove(shape);
        });
        setOnMouseExited(e -> {
            tile.getChildren().remove(rec);
            getChildren().add(shape);
        });
    }
    
    public void setHasNoPiece() {
        getChildren().add(nopiece);
    }
    
    public void setHasPiece() {
        getChildren().add(shape);
    }

    /**
     * A selectable move action to be called either on GUI click or programmatically
     * By default move does nothing, should be Override to perform an action
     */
    public void move() {}
    
    public void setCrownIcon() {
        getChildren().add(crown);
        setOnMouseEntered(e -> {
            tile.getChildren().add(rec);
            getChildren().remove(crown);
        });
        setOnMouseExited(e -> {
            tile.getChildren().remove(rec);
            getChildren().add(crown);
        });
    }
    
    /**
     * Opens the promotion selection GUI to choose the piece to promote to
     * @param isWhite whether or not the promotion to is White
     */
    public void promotionSelection(boolean isWhite) {
        Pane pane = new Pane();
        pane.setMinSize(TILE_SIZE, TILE_SIZE);
        VBox elements = new VBox();
        elements.setPadding(new Insets(30,10,10,10));
        elements.setSpacing(20);
        elements.setAlignment(Pos.CENTER);
        HBox buttons = new HBox();
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(0,15,0,15));
        buttons.setAlignment(Pos.CENTER);
        HBox buttons1 = new HBox();
        buttons1.setSpacing(10);
        buttons1.setPadding(new Insets(0,15,0,15));
        buttons1.setAlignment(Pos.CENTER);
        Label selectLabel = new Label("Select: ");
        selectLabel.setFont(new Font("Roboto",20*ChessLite.SCALE));
        selectLabel.setAlignment(Pos.CENTER);
        selectLabel.setId("smallfont");
        Button queen = new Button();
        queen.setId("promotionbutton");
        queen.setFocusTraversable(false);
        queen.setOnAction((event) -> {
            Stage thestage = (Stage) queen.getScene().getWindow();
            ChessLite.clip.play();
            controller.makeMovePromotion(tile, new Queen(isWhite, tile));
            controller.clearSelectables();
            thestage.close();
        });
        ImageView queenimg = new ImageView(new Image(getClass().getResourceAsStream(isWhite ?
                WHITE_QUEEN : BLACK_QUEEN)));
        queenimg.setFitHeight(80*ChessLite.SCALE);
        queenimg.setFitWidth(80*ChessLite.SCALE);
        queen.setGraphic(queenimg);
        Button knight = new Button();
        knight.setId("promotionbutton");
        knight.setFocusTraversable(false);
        knight.setOnAction((event) -> {
            Stage thestage = (Stage) knight.getScene().getWindow();
            ChessLite.clip.play();
            controller.makeMovePromotion(tile, new Knight(isWhite, tile));
            controller.clearSelectables();
            thestage.close();
        });
        ImageView knightimg = new ImageView(new Image(getClass().getResourceAsStream(isWhite ?
                WHITE_KNIGHT : BLACK_KNIGHT)));
        knightimg.setFitHeight(80*ChessLite.SCALE);
        knightimg.setFitWidth(80*ChessLite.SCALE);
        knight.setGraphic(knightimg);
        Button rook = new Button();
        rook.setId("promotionbutton");
        rook.setFocusTraversable(false);
        rook.setOnAction((event) -> {
            Stage thestage = (Stage)rook.getScene().getWindow();
            ChessLite.clip.play();
            controller.makeMovePromotion(tile, new Rook(isWhite, tile));
            controller.clearSelectables();
            thestage.close();
        });
        ImageView rookimg = new ImageView(new Image(getClass().getResourceAsStream(isWhite ?
                WHITE_ROOK : BLACK_ROOK)));
        rookimg.setFitHeight(80*ChessLite.SCALE);
        rookimg.setFitWidth(80*ChessLite.SCALE);
        rook.setGraphic(rookimg);
        Button bishop = new Button();
        bishop.setId("promotionbutton");
        bishop.setFocusTraversable(false);
        bishop.setOnAction((event) -> {
            Stage thestage = (Stage) bishop.getScene().getWindow();
            ChessLite.clip.play();
            controller.makeMovePromotion(tile, new Bishop(isWhite, tile));
            controller.clearSelectables();
            thestage.close();
        });
        ImageView bishopimg = new ImageView(new Image(getClass().getResourceAsStream(isWhite ?
                WHITE_BISHOP : BLACK_BISHOP)));
        bishopimg.setFitHeight(80*ChessLite.SCALE);
        bishopimg.setFitWidth(80*ChessLite.SCALE);
        bishop.setGraphic(bishopimg);
        
        buttons.getChildren().addAll(queen,knight);
        buttons1.getChildren().addAll(rook,bishop);
        
        elements.getChildren().addAll(selectLabel, buttons, buttons1);
        pane.getChildren().add(elements);
        
        Scene scene = getController().getRoot().getScene();
        Scene secondScene = new Scene(pane, scene.getWidth()/2.3, scene.getHeight()/3);
        secondScene.getStylesheets().add(ChessLite.class.getResource("/resources/chess.css").toExternalForm());
        Stage newWindow = new Stage();
        
        newWindow.getIcons().add(new Image(APP_ICON_PATH));
        newWindow.setTitle("Pawn Promotion");
        newWindow.setScene(secondScene);
        newWindow.setResizable(false);
        newWindow.setOnCloseRequest((event)->{
            controller.getSelectedTile().getPiece().moveTo(controller.getSelectedTile());
            controller.clearSelectables();
        });

        newWindow.setX(scene.getWindow().getX() + scene.getWindow().getWidth()/4.5);
        newWindow.setY(scene.getWindow().getY() + scene.getWindow().getHeight()/3.2);
        newWindow.setWidth(80*ChessLite.SCALE*2 + 120*ChessLite.SCALE);
        newWindow.setHeight(scene.getWindow().getHeight()/2.5);

        newWindow.initOwner(scene.getWindow());
        newWindow.initModality(Modality.APPLICATION_MODAL); 
        newWindow.showAndWait();

    }
}
