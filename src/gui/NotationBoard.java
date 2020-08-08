/*
 * A class bound to a list of movedata that renders the movedata to be viewed
 */
package gui;

import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

/**
 *
 * @author Joseph
 */
public class NotationBoard extends ScrollPane{
    
    private final Game game; //controller to redirect flow
    private final ArrayList<Move> movedata;
    private final VBox movegui;
    private final ArrayList<HBox> moves;
    private Label msglbl;
        
    /**
     * Constructs a NotationBoard liked to a VBox GUI, controller class, and moveList
     * @param movesIn, moves to be rendered on GUI
     * @param verticalIn, GUI to update
     * @param gameIn, controller to redirect flow 
     */
    public NotationBoard(ArrayList<Move> movesIn, VBox verticalIn, Game gameIn) {
        moves = new ArrayList<>();
        movedata = movesIn;
        movegui = verticalIn;
        game = gameIn;
        setVvalue(1.0);     
        movegui.heightProperty().addListener(observable -> setVvalue(1D));
    }
    
    /**
     * Add a message to bottom of VBox GUI component when finished
     * @param msg to be added
     */
    public void addFinishedMessage(String msg) {
        msglbl = new Label(msg);
        msglbl.setPadding(new Insets(10 * ChessLite.SCALE,0,10 * ChessLite.SCALE,0));
        msglbl.setFont(Font.font("Roboto", FontPosture.ITALIC, 22 * ChessLite.SCALE));
        msglbl.setMinSize(((Game.BAR_WIDTH*0.65)-(ChessLite.SCALE*25)), 58 * ChessLite.SCALE);
        msglbl.setMaxSize(((Game.BAR_WIDTH*0.65)-(ChessLite.SCALE*25)), 58 * ChessLite.SCALE);
        msglbl.setWrapText(true);
        msglbl.setAlignment(Pos.CENTER);
        movegui.getChildren().add(msglbl);
    }
    
    /** 
     * Remove finished message (needed for takeBack)
     */
    public void removeFinishedMessage() {
        if(msglbl != null) {
            movegui.getChildren().remove(msglbl);
        }
    }
    
    /**
     * Add the most recent move on the moveList to VBox GUI
     */
    public void addLastToGUI() {
        int lastIndex = movedata.size() - 1;
        String ply = movedata.get(lastIndex).getNotation();
        if (lastIndex % 2 == 0) {
            HBox move = new HBox();
            Label numlbl = new Label(Integer.toString(moves.size() + 1) + ". ");
            numlbl.setFont(new Font("Roboto", 22*ChessLite.SCALE));
            numlbl.setMinSize((40*ChessLite.SCALE), 38*ChessLite.SCALE);
            numlbl.setMaxSize((40*ChessLite.SCALE), 38*ChessLite.SCALE);
            Label plylbl = new Label(ply);
            plylbl.setFont(new Font("Roboto", 22*ChessLite.SCALE));
            plylbl.setId("ply");
            plylbl.setPadding(new Insets(0,0,0,10));
            plylbl.setMinSize((90*ChessLite.SCALE), 38*ChessLite.SCALE);
            plylbl.setMaxSize((90*ChessLite.SCALE), 38*ChessLite.SCALE);
            plylbl.setOnMouseClicked((event)->{
                game.goTo(lastIndex);
            });
            move.getChildren().addAll(numlbl,plylbl);
            movegui.getChildren().add(move);
            moves.add(move);
        } else {
            Label plylbl = new Label(ply);
            plylbl.setFont(new Font("Roboto", 22*ChessLite.SCALE));
            plylbl.setId("ply");
            plylbl.setPadding(new Insets(0,0,0,10));
            plylbl.setMinSize((90*ChessLite.SCALE), 38*ChessLite.SCALE);
            plylbl.setMaxSize((90*ChessLite.SCALE), 38*ChessLite.SCALE);
            plylbl.setOnMouseClicked((event)->{
                game.goTo(lastIndex);
            });
            moves.get(moves.size() - 1).getChildren().add(plylbl);
        }
    }
    
    /**
     * Remove the most recent move on the moveList to VBox GUI
     */
    public void removeLastFromGUI() {
        int lastIndex = movedata.size() - 1;
        if (lastIndex % 2 != 0) {
            movegui.getChildren().remove(moves.get(moves.size() - 1));
            moves.remove(moves.size() - 1);
        } else {
            moves.get(moves.size() - 1).getChildren().remove(2);
        }
    }
    
    /**
     * Renders new label GUI to be selected
     * Renders un-selection of old label GUI
     * @param newNum, number of label to be selected
     * @param oldNum, number of label to be unselected
     */
    public void selectEntry(int newNum, int oldNum) {
        if(oldNum > -1) {
            if(oldNum % 2 == 0) {
                moves.get(oldNum/2).getChildren().get(1).setId("ply");
            } else {
                moves.get(oldNum/2).getChildren().get(2).setId("ply");
            }
        }
        if(newNum > -1) {
            if(newNum % 2 == 0) {
                moves.get(newNum/2).getChildren().get(1).setId("plyhighlighted");
            } else {
                moves.get(newNum/2).getChildren().get(2).setId("plyhighlighted");
            }
        }
    }
    
    /**
     * Renders new label GUI to be selected
     * @param newNum , number of label to be selected
     */
    public void selectEntry(int newNum) {
        if(newNum > -1) {
            if(newNum % 2 == 0) {
                moves.get(newNum/2).getChildren().get(1).setId("plyhighlighted");
            } else {
                moves.get(newNum/2).getChildren().get(2).setId("plyhighlighted");
            }
        }
    }

    
}
