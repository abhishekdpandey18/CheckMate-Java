package com.chess.GUI;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.SwingUtilities;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MoveFactory;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveStatus;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;


@SuppressWarnings("deprecation")
public class Table extends Observable {

	private final JFrame gameFrame;
	private GameHistoryPanel gameHistoryPanel;
	private TakenPiecesPanel takenPiecesPanel;
	private BoardPanel boardPanel;
	private MoveLog moveLog;
	private final GameSetUp gameSetUp;
	private Board chessBoard;
	
	private Tile sourceTile;
	private Tile destinationTile;
	private Piece humanMovedPiece;
	private BoardDirection boardDirection;
	
	private Move computerMove;
	
	private boolean highlightLegalMoves;
	private boolean flipBoardAfterMove;
	
	private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
	private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
	private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
	private static String defaultPieceImagePath = "Chess pieces/";
	
	private final Color lightTileColor = Color.decode("#f2e1c3");
	private final Color darkTileColor = Color.decode("#c3a082");
	
	private final Color checkColor = Color.decode("#FF0000");
	private final Color checkMateColor = Color.decode("#2C1313");
	
	private static final Table INSTANCE = new Table();
	public Table() {
		this.gameFrame = new JFrame("JChess");
		this.gameFrame.setLayout(new BorderLayout());
		final JMenuBar tableMenuBar = createTableMenuBar();
		this.gameFrame.setJMenuBar(tableMenuBar);
		this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
		this.chessBoard = Board.createStandardBoard();
		this.gameHistoryPanel = new GameHistoryPanel();
		this.takenPiecesPanel = new TakenPiecesPanel();
		this.boardPanel = new BoardPanel();
		this.moveLog = new MoveLog();
		this.addObserver(new TableGameAIWatcher());
		this.gameSetUp = new GameSetUp(this.gameFrame, true);
		this.boardDirection = BoardDirection.NORMAL;
		this.highlightLegalMoves = false;
		this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
		this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
		this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
		this.gameFrame.setVisible(true);
	}
	
	public static Table get() {
		return INSTANCE;
	}
	
	public void show() {
		Table.get().getMoveLog().clear();
		Table.get().getGameHistoryPanel().redo( Table.get().getGameBoard(), Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
	}
	
	private GameSetUp getGameSetUp() {
		return this.gameSetUp;
	}
	private Board getGameBoard() {
		return this.chessBoard;
	}
	private MoveLog getMoveLog() {
		return this.moveLog;
	}
	private GameHistoryPanel getGameHistoryPanel() {
		return this.gameHistoryPanel;
	}
	private TakenPiecesPanel getTakenPiecesPanel() {
		return this.takenPiecesPanel;
	}
	private BoardPanel getBoardPanel() {
		return this.boardPanel;
	}

	private JMenuBar createTableMenuBar() {
		final JMenuBar tableMenuBar = new JMenuBar();
		tableMenuBar.add(createFileMenu());
		tableMenuBar.add(createPreferencesMenu());
		tableMenuBar.add(createOptionsMenu());
		return tableMenuBar;
	}

	private JMenu createFileMenu() {
		final JMenu fileMenu = new JMenu("File");
		
		final JMenuItem openPGN = new JMenuItem("Load PGN File");
		openPGN.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Open up that PGN file");
			}
		});
		fileMenu.add(openPGN);
		
		final JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				System.exit(0);
			}	
		});
		fileMenu.add(exitMenuItem);
		return fileMenu;
	}
	
	private JMenu createPreferencesMenu() {
		final JMenu preferencesMenu = new JMenu("Preferences");
		final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
		flipBoardMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				boardDirection = boardDirection.opposite();
				boardPanel.drawBoard(chessBoard);
				System.out.println("Flip");
			}
		});
		final JCheckBoxMenuItem flipEachMove = new JCheckBoxMenuItem("Flip Board After Each Move", false);
		flipEachMove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chessBoard.currentPlayer().getAlliance().isBlack()) {
					boardDirection = BoardDirection.FLIPPED;
				} else {
					boardDirection = BoardDirection.NORMAL;
				}
				boardPanel.drawBoard(chessBoard);
				flipBoardAfterMove = flipEachMove.isSelected();
			}
		});
		preferencesMenu.add(flipBoardMenuItem);
		preferencesMenu.add(flipEachMove);
		preferencesMenu.addSeparator();
		
		final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", false);
		
		legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
				
			}	
		});
		
		preferencesMenu.add(legalMoveHighlighterCheckbox);
		
		return preferencesMenu;
	}
	
	public JMenu createOptionsMenu() {
		final JMenu optionsMenu = new JMenu("Options");
		
		final JMenuItem setUpGameMenuItem = new JMenuItem("Setup Game");
		setUpGameMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Table.get().getGameSetUp().promptUser();
				Table.get().setupUpdate(Table.get().getGameSetUp());
			}
		});
		
		
		
		final JMenuItem undoMove = new JMenuItem("Undo Last Move");
		undoMove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				undoLastMove();
			}
		});
		final JMenuItem newGame = new JMenuItem("New Game");
		newGame.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			undoAllMoves();
		}
		});
		optionsMenu.add(setUpGameMenuItem);
		optionsMenu.add(newGame);
		optionsMenu.add(undoMove);
		return optionsMenu;
	}
	
	private void setupUpdate(final GameSetUp gameSetUp) {
		setChanged();
		notifyObservers(gameSetUp);
	}
	private static class TableGameAIWatcher implements Observer {

		public void update(final Observable o, final Object arg) {
			if (Table.get().getGameSetUp().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
					!Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
					!Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
				//Create an AI thread
				//execute the AI
				final AIThinkTank thinkTank = new AIThinkTank();
				thinkTank.execute();
			}
			
			if(Table.get().getGameBoard().currentPlayer().isInCheckMate()) {
				System.out.println("Game Over, " + Table.get().getGameBoard().currentPlayer().toString()+ " is in Check Mate!");
			}
			if(Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
				System.out.println("Game Over, " + Table.get().getGameBoard().currentPlayer().toString()+ " is in Stale Mate!");
			}
		}
	}
	
	public void updateGameBoard(final Board board) {
		this.chessBoard = board;
	}
	public void updateComputerMove(final Move move) {
		this.computerMove = move;
	}
	private void moveMadeUpdate(final PlayerType playerType) {
		setChanged();
		notifyObservers(playerType);
	}
	private static class AIThinkTank extends SwingWorker<Move, String> {
		
		private AIThinkTank() {
			
		}

		@Override
		protected Move doInBackground() throws Exception {
			
			final MoveStrategy miniMax = new MiniMax(Table.get().getGameSetUp().getSearchDepth());
			
			final Move bestMove = miniMax.execute(Table.get().getGameBoard());
			
			return bestMove;
		}
		
		@Override
		public void done() {
			try {
				final Move bestMove = get();
				Table.get().updateComputerMove(bestMove);
				Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
				Table.get().getMoveLog().addMove(bestMove);
				Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
				Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
				Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
				Table.get().moveMadeUpdate(PlayerType.COMPUTER);
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void undoLastMove() {
		final Move lastMove = (this.getMoveLog().removeMove(this.getMoveLog().size() - 1));
		this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getToBoard();
		this.getMoveLog().removeMove(lastMove);
		this.gameHistoryPanel.redo(chessBoard, this.getMoveLog());
		this.takenPiecesPanel.redo(this.getMoveLog());
		if (flipBoardAfterMove) {
			boardDirection = boardDirection.opposite();
		}
		this.boardPanel.drawBoard(chessBoard);
	}
	private void undoAllMoves() {
		/*for (int i = this.getMoveLog().size() - 1; i >= 0; i--) {
			undoLastMove();
		}*/
		this.chessBoard = Board.createStandardBoard();
		this.moveLog = new MoveLog();
		this.gameHistoryPanel.redo(chessBoard, this.moveLog);
		this.takenPiecesPanel.redo(this.moveLog);;
		this.boardPanel.drawBoard(chessBoard);
	}
	public enum BoardDirection {
		NORMAL {
			@Override
			List<TilePanel> traverse(final List<TilePanel> boardTiles) {
				return boardTiles;
			}
			@Override
			BoardDirection opposite() {
				return FLIPPED;
			}
		},
		FLIPPED {
			@Override
			List<TilePanel> traverse(final List<TilePanel> boardTiles) {
				List<TilePanel> copy = new ArrayList<TilePanel>();
				for (int i = boardTiles.size() - 1; i >= 0; i--) {
					copy.add(boardTiles.get(i));
				}
				return copy;
			}	
			@Override
			BoardDirection opposite() {
				return NORMAL;
			}
			
		};
		abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
		abstract BoardDirection opposite();
	}
	
	private class BoardPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private List<TilePanel> boardTiles;
	
		
		BoardPanel() {
			super(new GridLayout(8,8));
			this.boardTiles = new ArrayList<>();
			for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
				final TilePanel tilePanel = new TilePanel(this, i);
				this.boardTiles.add(tilePanel);
				add(tilePanel);
			}
			setPreferredSize(BOARD_PANEL_DIMENSION);
			revalidate();
		}


		public void drawBoard(final Board board) {
			this.removeAll();
			System.out.println("Drawing board");
			for (final TilePanel tilePanel: boardDirection.traverse(boardTiles)) {
				tilePanel.drawTile(board);
				add(tilePanel);
			}
			validate(); 
			repaint();
		}
	}
	
	public static class MoveLog {
		
		private final List<Move> moves;
		MoveLog() {
			this.moves = new ArrayList<Move>();
		}
		public List<Move> getMoves() {
			return this.moves;
		}
		public void addMove(final Move move) {
			this.moves.add(move);
		}
		public int size() {
			return this.moves.size();
		}
		public void clear() {
			this.moves.clear();
		}
		public Move removeMove(final int index) {
			return this.moves.remove(index);
		}
		public boolean removeMove(final Move move) {
			return this.moves.remove(move);
		}
		
	}
	
	enum PlayerType {
		HUMAN,
		COMPUTER
	}
	private class TilePanel extends JPanel {
		
		
		private final int tileId;
		
		TilePanel(final BoardPanel boardPanel,
				final int tileId) {
			super(new GridBagLayout());
			this.tileId = tileId;
			setPreferredSize(TILE_PANEL_DIMENSION);
			assignTileColor();
			assignTilePieceIcon(chessBoard);
			
			addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						System.out.println("Deselected");
						sourceTile = null;
						destinationTile = null;
						humanMovedPiece = null;
					} else if (SwingUtilities.isLeftMouseButton(e)) {
							if (sourceTile == null) {
								//first click
								System.out.println("Selected Tile");
								sourceTile = chessBoard.getTile(tileId);
								if (sourceTile.isTileOccupied() && sourceTile.getPiece().getPieceAlliance().equals(chessBoard.currentPlayer().getAlliance())) {
								humanMovedPiece = sourceTile.getPiece();
								}
								if (humanMovedPiece == null) {
									sourceTile = null;
								}
							} else if (chessBoard.getTile(tileId).isTileOccupied() && chessBoard.getTile(tileId).getPiece().getPieceAlliance().equals(chessBoard.currentPlayer().getAlliance())) {
								sourceTile = chessBoard.getTile(tileId);
								humanMovedPiece = sourceTile.getPiece();
							} else {
							//second click
							System.out.println("Destination Tile Selected");
							destinationTile = chessBoard.getTile(tileId);
							final Move move = MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
							System.out.println("Move created");
							final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
							System.out.println("Move made");
							if (transition.getMoveStatus().isDone()) {
								chessBoard = transition.getTransitionBoard();
								moveLog.addMove(move);
								if (flipBoardAfterMove && move != null) {
									boardDirection = boardDirection.opposite();
								}
								//Add move made to log
							}
							sourceTile = null;
							destinationTile = null;
							humanMovedPiece = null;
							}
						
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									gameHistoryPanel.redo(chessBoard, moveLog);
									takenPiecesPanel.redo(moveLog);
									
									
									if(gameSetUp.isAIPlayer(chessBoard.currentPlayer())) {
										Table.get().moveMadeUpdate(PlayerType.HUMAN);
									}
									System.out.println("Creating new board...");
									boardPanel.drawBoard(chessBoard);
									System.out.println("Done");
								}
							});
				}
			}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			revalidate();
			repaint();
			
		}
		public void drawTile(final Board board) {
			assignTileColor();
			assignTilePieceIcon(board);
			highLightLegals(board);
			revalidate();
			repaint();
		}

		private void assignTilePieceIcon(final Board board) {
			this.removeAll();
			if (board.getTile(this.tileId).isTileOccupied()) {

				try {
					final BufferedImage image = ImageIO.read(new File(defaultPieceImagePath + board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0,1) +
							board.getTile(this.tileId).getPiece().toString() + ".gif"));
					add(new JLabel(new ImageIcon(image)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void highLightLegals(final Board board) {
			//if (highlightLegalMoves) {
				for (final Move move: pieceLegalMoves(board)) {
					if (move.getDestinationCoordinate() == this.tileId) {
						if (chessBoard.currentPlayer().makeMove(move).getMoveStatus() != MoveStatus.LEAVES_PLAYER_IN_CHECK) {
						try {
							if (move.isAttack()) {
								setBorder(BorderFactory.createLineBorder(Color.RED, 5));
							} else {
							add(new JLabel(new ImageIcon(ImageIO.read(new File("Chess pieces/green_dot.png")))));
							setBorder(BorderFactory.createLineBorder(Color.GRAY));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					}
					
				}
			//}
		}

		private Collection<Move> pieceLegalMoves(final Board board) {
			if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                final List<Move> pieceMoves = new ArrayList<>();
                for(Move move : board.currentPlayer().getLegalMoves()) {
                    if(move.getMovedPiece() == humanMovedPiece) {
                        pieceMoves.add(move);
                    }
                }
                return pieceMoves;
            }
            return Collections.emptyList();
		}

		private void assignTileColor() {
            boolean isLight = ((tileId + tileId / 8) % 2 == 0);
            setBackground(isLight ? lightTileColor : darkTileColor);
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            if (chessBoard.whitePlayer().isInCheck()) {
            	if (chessBoard.whitePlayer().getPlayerKing().getPiecePosition() == tileId) {
            		setBackground(checkColor);
            	}
            }
            if (chessBoard.blackPlayer().isInCheck()) {
            	if (chessBoard.blackPlayer().getPlayerKing().getPiecePosition() == tileId) {
            		setBackground(checkColor);
            	}
            }
            if (chessBoard.whitePlayer().isInCheckMate()) {
            	if (chessBoard.whitePlayer().getPlayerKing().getPiecePosition() == tileId) {
            		setBackground(checkMateColor);
            	}
            }
            if (chessBoard.blackPlayer().isInCheckMate()) {
            	if (chessBoard.blackPlayer().getPlayerKing().getPiecePosition() == tileId) {
            		setBackground(checkMateColor);
            	}
            }
           
		}
		
	}
}
