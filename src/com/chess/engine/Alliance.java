package com.chess.engine;

import com.chess.engine.board.BoardUtils;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

public enum Alliance {
	WHITE {
		@Override
		public int getDirection() {
			return -1;
		}
		public boolean isWhite() {
			return true;
		}
		public boolean isBlack() {
			return false;
		}
		@Override
		public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
			return whitePlayer;
		}
		@Override
		public int getOppositeDirection() {
			return 1;
		}
		@Override
		public boolean isPawnPromotionSquare(int position) {
			return BoardUtils.EIGHTH_RANK[position];
		}
	},
	BLACK {
		@Override
		public int getDirection() {
			return 1;
		}
		public boolean isWhite() {
			return false;
		}
		public boolean isBlack() {
			return true;
		}
		@Override
		public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
			return blackPlayer;
		}
		@Override
		public int getOppositeDirection() {
			return -1;
		}
		@Override
		public boolean isPawnPromotionSquare(int position) {
			return BoardUtils.FIRST_RANK[position];
		}
	};
	public abstract int getDirection();
	public abstract boolean isWhite();
	public abstract boolean isBlack();
	public abstract int getOppositeDirection();
	public abstract boolean isPawnPromotionSquare(int position);
	
	public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
