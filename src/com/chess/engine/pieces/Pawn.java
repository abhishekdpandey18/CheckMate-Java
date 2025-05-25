package com.chess.engine.pieces;

import java.util.*;
import java.util.Collection;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.PawnPromotion;
import com.chess.engine.board.Tile;

public class Pawn extends Piece {
	
	private final static int[] CANDIDATE_MOVE_COORDINATE = {8, 16, 7, 9};

	public Pawn(final int piecePosition, final Alliance pieceAlliance) {
		super(PieceType.PAWN, piecePosition, pieceAlliance, true);
	}
	public Pawn(final Alliance pieceAlliance,
			final int piecePosition,
			final boolean isFirstMove) {
		super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
	}

	@Override
	public Collection<Move> calculateLegalMove(final Board board) {
		
		final List<Move> legalMoves = new ArrayList<Move>();
		
		for (final int currentCandidateOffset: CANDIDATE_MOVE_COORDINATE) {
			final int candidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection() * currentCandidateOffset);
			
			if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				continue;
			}
			final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
			if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
				// Change later
				if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
					legalMoves.add(new PawnPromotion(new Move.PawnMove(board, this, candidateDestinationCoordinate)));
				} else {
				legalMoves.add(new Move.PawnMove(board, this, candidateDestinationCoordinate));
				}
			} else if (currentCandidateOffset == 16 && 
					((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack()) ||
					(BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite())) &&
					(!board.getTile(candidateDestinationCoordinate).isTileOccupied() && 
					!board.getTile(this.piecePosition + (this.getPieceAlliance().getDirection() * 8)).isTileOccupied())) {
				legalMoves.add(new Move.PawnJump(board, this, candidateDestinationCoordinate));
			} else if (currentCandidateOffset == 7 &&
					!((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) || 
					(BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))) {
				if (candidateDestinationTile.isTileOccupied()) {
					final Piece pieceAtDestination = candidateDestinationTile.getPiece();
					final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
					if (this.pieceAlliance != pieceAlliance) {
						if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
							legalMoves.add(new PawnPromotion(new Move.PawnAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination)));
						} else {
						legalMoves.add(new Move.PawnAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
						}
					}
			} else if (board.getEnPassantPawn() != null) {
				if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
					final Piece pieceOnCandidate = board.getEnPassantPawn();
					if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
						legalMoves.add(new Move.PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
					}
				}
			}
		}  else if (currentCandidateOffset == 9 &&
					!((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()) || 
					(BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()))) {
				
				if (candidateDestinationTile.isTileOccupied()) {
					final Piece pieceAtDestination = candidateDestinationTile.getPiece();
					final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
					if (this.pieceAlliance != pieceAlliance) {
						if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
							legalMoves.add(new Move.PawnPromotion(new Move.PawnAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination)));
						} else {
						legalMoves.add(new Move.PawnAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
						}
					}
			} else if (board.getEnPassantPawn() != null) {
				if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
					final Piece pieceOnCandidate = board.getEnPassantPawn();
					if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
						legalMoves.add(new Move.PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
					}
				}
			} 
	}
}
		return Collections.unmodifiableList(legalMoves);
	}
	@Override
	public String toString() {
		return PieceType.PAWN.toString();
	}
	@Override
	public Pawn movePiece(Move move) {
		return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance()); 
	}
	public Piece getPromotionPiece() {
		return new Queen(this.pieceAlliance, this.piecePosition, false);
	}
}
