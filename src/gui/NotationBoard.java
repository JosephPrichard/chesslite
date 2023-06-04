/*
 * A class bound to a list of move data that renders the move data to be viewed
 */
package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

import java.util.ArrayList;

/**
 *
 * @author Joseph
 */
public class NotationBoard extends ScrollPane{
    
    private final ChessLite app;
    private final Game game; //controller to redirect flow
    private final ArrayList<Move> moveData;
    private final VBox moveGui;
    private final ArrayList<HBox> moves;
    private Label msgLbl;
        
    /**
     * Constructs a NotationBoard liked to a VBox GUI, controller class, and moveList
     * @param movesIn, moves to be rendered on GUI
     * @param verticalIn, GUI to update
     * @param gameIn, controller to redirect flow 
     * @param app, object for application
     */
    public NotationBoard(ArrayList<Move> movesIn, VBox verticalIn, Game gameIn, ChessLite app) {
        moves = new ArrayList<>();
        moveData = movesIn;
        moveGui = verticalIn;
        game = gameIn;
        setVvalue(1.0);     
        moveGui.heightProperty().addListener(observable -> setVvalue(1D));
        this.app = app;
    }
    
    /**
     * Add a message to bottom of VBox GUI component when finished
     * @param msg to be added
     */
    public void addFinishedMessage(String msg) {
        msgLbl = new Label(msg);
        msgLbl.setPadding(new Insets(10 * app.getScale(),0,10 * app.getScale(),0));
        msgLbl.setFont(Font.font("Roboto", FontPosture.ITALIC, 22 * app.getScale()));
        msgLbl.setMinSize(((game.getBarWidth()*0.8)-(app.getScale()*50)), 58 * app.getScale());
        msgLbl.setMaxSize(((game.getBarWidth()*0.8)-(app.getScale()*50)), 58 * app.getScale());
        msgLbl.setWrapText(true);
        msgLbl.setAlignment(Pos.CENTER);
        moveGui.getChildren().add(msgLbl);
    }
    
    /** 
     * Remove finished message (needed for takeBack)
     */
    public void removeFinishedMessage() {
        if(msgLbl != null) {
            moveGui.getChildren().remove(msgLbl);
        }
    }
    
    /**
     * Add the most recent move on the moveList to VBox GUI
     */
    public void addLastToGUI() {
        int lastIndex = moveData.size() - 1;
        String ply = moveData.get(lastIndex).getNotation();
        if (lastIndex % 2 == 0) {
            HBox move = new HBox();
            Label numLbl = new Label((moves.size() + 1) + ". ");
            numLbl.setAlignment(Pos.CENTER);
            numLbl.setFont(new Font("Roboto", 22*app.getScale()));
            numLbl.setMinSize((50*app.getScale()), 38*app.getScale());
            numLbl.setMaxSize((50*app.getScale()), 38*app.getScale());
            Label plyLbl = new Label(ply);
            plyLbl.setFont(new Font("Roboto", 22*app.getScale()));
            plyLbl.setId("ply");
            plyLbl.setPadding(new Insets(0,0,0,10*app.getScale()));
            plyLbl.setMinSize((90*app.getScale()), 38*app.getScale());
            plyLbl.setMaxSize((90*app.getScale()), 38*app.getScale());
            plyLbl.setOnMouseClicked((event)-> game.goTo(lastIndex));
            move.getChildren().addAll(numLbl, plyLbl);
            moveGui.getChildren().add(move);
            moves.add(move);
        } else {
            Label plyLbl = new Label(ply);
            plyLbl.setFont(new Font("Roboto", 22*app.getScale()));
            plyLbl.setId("ply");
            plyLbl.setPadding(new Insets(0,0,0,10));
            plyLbl.setMinSize((90*app.getScale()), 38*app.getScale());
            plyLbl.setMaxSize((90*app.getScale()), 38*app.getScale());
            plyLbl.setOnMouseClicked((event)-> game.goTo(lastIndex));
            moves.get(moves.size() - 1).getChildren().add(plyLbl);
        }
    }
    
    /**
     * Remove the most recent move on the moveList to VBox GUI
     */
    public void removeLastFromGUI() {
        int lastIndex = moveData.size() - 1;
        if (lastIndex % 2 != 0) {
            moveGui.getChildren().remove(moves.get(moves.size() - 1));
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
