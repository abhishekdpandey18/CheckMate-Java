// It is time to make a chess game
package com.chess.engine.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.chess.engine.pieces.Piece;

public abstract class Tile {
// Creating the tiles and their coordinates
	protected final int tileCoordinate;
	
	private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();
	
	private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() {
		final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
		
		for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
			emptyTileMap.put(i, new EmptyTile(i));
		}
		
		return Collections.unmodifiableMap(emptyTileMap);
	}
	
	public static Tile createTile(final int tileCoordinate, final Piece piece) {
		return piece != null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
	}
	private Tile(final int tileCoordinate) {
		this.tileCoordinate = tileCoordinate;
	}
	
	public abstract boolean isTileOccupied();
	
	public abstract Piece getPiece();
	
	public int getTileCoordinate() {
		return this.tileCoordinate;
	}
	
	// Telling us that certain tiles are empty	
	public static final class EmptyTile extends Tile {
		private EmptyTile(final int coordinate) {
			super(coordinate);
		}

		@Override
		public boolean isTileOccupied() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String toString() {
			return "-";
		}
		@Override
		public Piece getPiece() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	
	// Used to say that a tile is occupied
	public static final class OccupiedTile extends Tile {
		private final Piece pieceOnTile;
		
		private OccupiedTile(final int tileCoordinate, final Piece pieceOnTile) {
			super(tileCoordinate);
			this.pieceOnTile = pieceOnTile;
		}
		
		@Override
		 public boolean isTileOccupied() {
			return true;
		}
		@Override
		public String toString() {
			return this.getPiece().getPieceAlliance().isBlack() ? this.getPiece().toString().toLowerCase() :
				this.getPiece().toString();
		}
		// Returns what piece is sitting on the occupied tile
		@Override
		public Piece getPiece() {
			return pieceOnTile;
		}
	}	
}
