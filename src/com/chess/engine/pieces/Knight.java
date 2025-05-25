package com.chess.engine.pieces;

import com.chess.engine.board.*;
import com.chess.engine.Alliance;
import java.util.*;

public class Knight extends Piece {
	
	// The knight only has 8 moves at maximum, when you add these to its current position, those are where it can legally move
	private final static int[] CANDIDATE_MOVE_COORDINATES = { -17, -15, -10, -6, 6, 10, 15, 17};
	
	public Knight(final int piecePosition, final Alliance pieceAlliance) {
		super(PieceType.KNIGHT, piecePosition, pieceAlliance,true);	
	}
	public Knight(final Alliance pieceAlliance,
			final int piecePosition,
			final boolean isFirstMove) {
		super(PieceType.KNIGHT, piecePosition, pieceAlliance, isFirstMove);
	}

	@Override
	public Collection<Move> calculateLegalMove(final Board board) {
		
		final List<Move> legalMoves = new ArrayList<Move>();
		// Testing each coordinate to see if they are legal
		for (int currentCandidate: CANDIDATE_MOVE_COORDINATES) {
			final int candidateDestinationCoordinate = this.piecePosition + currentCandidate;
			if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				//checking for exclusions
				if (isFirstColumnExclusion(this.piecePosition, currentCandidate) || 
					isSecondColumnExclusion(this.piecePosition, currentCandidate) || 
					isSeventhColumnExclusion(this.piecePosition, currentCandidate) || 
					isEighthColumnExclusion(this.piecePosition, currentCandidate)) {
					continue;
				}
				final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
			// Checking if the destination tile is occupied or not, if so, can't legally move there	
			if (!candidateDestinationTile.isTileOccupied()) {
				legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
			} else {
				//If the tile is occupied, if it is the opponent's piece, it could be moved to and captured
				final Piece pieceAtDestination = candidateDestinationTile.getPiece();
				final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();	
				if (this.pieceAlliance != pieceAlliance) {
					legalMoves.add(new Move.AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
				}
			}
		}
	}	
		return Collections.unmodifiableList(legalMoves);
	}
	@Override
	public String toString() {
		return PieceType.KNIGHT.toString();
	}
	@Override
	public Knight movePiece(Move move) {
		return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance()); 
	}
	// Looking for exceptions whereas if the knight is in the first column, it cannot jump to the other side of the board
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -17 || candidateOffset == -10 || candidateOffset == 6
				|| candidateOffset == 15);	
	}
	
	private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.SECOND_COLUMN[currentPosition] && (candidateOffset == -10 || candidateOffset == 6);
	}
	
	private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.SEVENTH_COLUMN[currentPosition] && (candidateOffset == -6 || candidateOffset == 10);
	}
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -15 || candidateOffset == -6 ||
				candidateOffset == 10 || candidateOffset == 17);
	}
}
