package com.chess.GUI;

import java.util.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import com.chess.GUI.Table.MoveLog;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;

public class TakenPiecesPanel extends JPanel {
	
	private final JPanel northPanel;
	private final JPanel southPanel;
	
	private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
	private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);
	private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

	public TakenPiecesPanel() {
		super(new BorderLayout());
		setBackground(PANEL_COLOR);
		setBorder(PANEL_BORDER);
		this.northPanel = new JPanel(new GridLayout(8, 2));
		this.southPanel = new JPanel(new GridLayout(8,2));
		this.northPanel.setBackground(PANEL_COLOR);
		this.southPanel.setBackground(PANEL_COLOR);
		this.add(this.northPanel, BorderLayout.NORTH);
		this.add(this.southPanel, BorderLayout.SOUTH);
		setPreferredSize(TAKEN_PIECES_DIMENSION);
	}
	
	public void redo(final MoveLog moveLog) {
		this.southPanel.removeAll();
		this.northPanel.removeAll();
		final List<Piece> whiteTakenPieces = new ArrayList<Piece>();
		final List<Piece> blackTakenPieces = new ArrayList<Piece>();
		
		for (final Move move: moveLog.getMoves()) {
			if (move.isAttack()) {
				final Piece takenPiece = move.getAttackedPiece();
				if (takenPiece.getPieceAlliance().isWhite()) {
					whiteTakenPieces.add(takenPiece);
				} else {
				blackTakenPieces.add(takenPiece);	
				}
			}
		}
		
		Collections.sort(whiteTakenPieces, new Comparator<Piece>() {

			@Override
			public int compare(Piece o1, Piece o2) {
				return Integer.compare(o1.getPieceValue(), o2.getPieceValue());
			}
			
		});
		Collections.sort(blackTakenPieces, new Comparator<Piece>() {

			@Override
			public int compare(Piece o1, Piece o2) {
				return Integer.compare(o1.getPieceValue(), o2.getPieceValue());
			}
			
		});
		
		for (final Piece takenPiece: whiteTakenPieces) {
			try {
				final BufferedImage image = ImageIO.read(new File("Chess pieces/" + 
							takenPiece.getPieceAlliance().toString().substring(0,1)
							+ "" + takenPiece.toString() + ".gif"));
				final ImageIcon icon = new ImageIcon(image);
				final JLabel imageLabel = new JLabel(icon);
				this.southPanel.add(imageLabel);
			} catch(final IOException e) {
				e.printStackTrace();
			}
		}
		for (final Piece takenPiece: blackTakenPieces) {
			try {
				final BufferedImage image = ImageIO.read(new File("Chess pieces/" + 
							takenPiece.getPieceAlliance().toString().substring(0,1)
							+ "" + takenPiece.toString() + ".gif"));
				final ImageIcon icon = new ImageIcon(image);
				final JLabel imageLabel = new JLabel(icon);
				this.northPanel.add(imageLabel);
			} catch(final IOException e) {
				e.printStackTrace();
			}
		}
		
		validate();
	}
}
