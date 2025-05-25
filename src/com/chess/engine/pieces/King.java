package com.chess.engine.pieces;

import java.util.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;

public class King extends Piece {
	// Has similar movements to a queen but it is not in a vector, only limited to move one square near
	private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

	public King(final int piecePosition, final Alliance pieceAlliance) {
		super(PieceType.KING, piecePosition, pieceAlliance, true);
	}
	public King(final Alliance pieceAlliance,
			final int piecePosition,
			final boolean isFirstMove) {
		super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
	}

	@Override
	public Collection<Move> calculateLegalMove(final Board board) {
		final List<Move> legalMoves = new ArrayList<Move>();
		for (final int currentCandidate: CANDIDATE_MOVE_COORDINATES) {
			final int candidateDestinationCoordinate = this.piecePosition + currentCandidate;
			if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				
				if (isFirstColumnExclusion(this.piecePosition, currentCandidate) || 				 
					isEighthColumnExclusion(this.piecePosition, currentCandidate)) {
					continue;
				}
				final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
			if (!candidateDestinationTile.isTileOccupied()) {
				legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
			} else {
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
		return PieceType.KING.toString();
	}
	@Override
	public King movePiece(Move move) {
		return new King(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate(), false); 
	}
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1 || candidateOffset == 7 || candidateOffset == -9);
	}
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 1 || candidateOffset == -7 || candidateOffset == 9);
	}

}
