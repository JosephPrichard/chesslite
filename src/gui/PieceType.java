/*
 * Enum factory for constructing pieces to be used in the GUI
 * 7/3/20.
 */
package gui;

import gui.pieces.Bishop;
import gui.pieces.King;
import gui.pieces.Knight;
import gui.pieces.Pawn;
import gui.pieces.Queen;
import gui.pieces.Rook;

/**
 *
 * @author Joseph
 */
public enum PieceType {
    
    NoPiece {
        @Override
        public Piece createPiece(Tile tile) {
            return null;
        }   
    },
    WhitePawn {
        @Override
        public Piece createPiece(Tile tile) {
            return new Pawn(true, tile);
        }        
    },
    WhiteBishop {
        @Override
        public Piece createPiece(Tile tile) {
            return new Bishop(true, tile);        }
        
    },
    WhiteKnight {
        @Override
        public Piece createPiece(Tile tile) {
            return new Knight(true, tile);
        }        
    },
    WhiteRook {
        @Override
        public Piece createPiece(Tile tile) {
            return new Rook(true, tile);
        }        
    },
    WhiteQueen {
        @Override
        public Piece createPiece(Tile tile) {
            return new Queen(true, tile);
        }     
    },
    WhiteKing {
        @Override
        public Piece createPiece(Tile tile) {
            return new King(true, tile);
        }
    },
    BlackPawn {
        @Override
        public Piece createPiece(Tile tile) {
            return new Pawn(false, tile);
        }
    },
    BlackBishop {
        @Override
        public Piece createPiece(Tile tile) {
            return new Bishop(false, tile);
        }
    },
    BlackKnight {
        @Override
        public Piece createPiece(Tile tile) {
            return new Knight(false, tile);
        }
    },
    BlackRook {
        @Override
        public Piece createPiece(Tile tile) {
            return new Rook(false, tile);
        }
    },
    BlackQueen {
        @Override
        public Piece createPiece(Tile tile) {
            return new Queen(false, tile);
        } 
    },
    BlackKing {
        @Override
        public Piece createPiece(Tile tile) {
            return new King(false, tile);
        }      
    };
    
    /**
     * Abstract: Returns the proper piece to be constructed
     * @param tile to be used in piece construction
     * @return the piece
     */
    public abstract Piece createPiece(Tile tile);
    
}
