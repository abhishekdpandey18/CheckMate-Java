package com.chess.GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.chess.GUI.Table.PlayerType;
import com.chess.engine.player.Player;


public class GameSetUp extends JDialog {
	
	private PlayerType whitePlayerType;
	private PlayerType blackPlayerType;
	private JSpinner searchDepthSpinner;
	
	private static final String HUMAN_TEXT = "Human";
	private static final String COMPUTER_TEXT = "Computer";
	
	public GameSetUp(final JFrame frame, final boolean model) {
		super(frame, model);
		final JPanel myPanel = new JPanel(new GridLayout(0,1));
		final JRadioButton whiteHumanButton = new JRadioButton(HUMAN_TEXT);
		final JRadioButton blackHumanButton = new JRadioButton(HUMAN_TEXT);
		final JRadioButton whiteComputerButton = new JRadioButton(COMPUTER_TEXT);
		final JRadioButton blackComputerButton = new JRadioButton(COMPUTER_TEXT);

		whiteHumanButton.setActionCommand(HUMAN_TEXT);
		final ButtonGroup whiteGroup = new ButtonGroup();
		whiteGroup.add(whiteHumanButton);
		whiteGroup.add(whiteComputerButton);
		whiteHumanButton.setSelected(true);
		
		final ButtonGroup blackGroup = new ButtonGroup();
		blackGroup.add(blackHumanButton);
		blackGroup.add(blackComputerButton);
		blackHumanButton.setSelected(true);
		
		getContentPane().add(myPanel);
		myPanel.add(new JLabel("White"));
		myPanel.add(whiteHumanButton);
		myPanel.add(whiteComputerButton);
		myPanel.add(new JLabel("Black"));
		myPanel.add(blackHumanButton);
		myPanel.add(blackComputerButton);
		
		myPanel.add(new JLabel("Search"));
		this.searchDepthSpinner = addLabeledSpinner(myPanel, "Search Depth", new SpinnerNumberModel(3, 0, Integer.MAX_VALUE, 1));
		
		final JButton cancelButton = new JButton("Cancel");
		final JButton okButton = new JButton("OK");
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				whitePlayerType = whiteComputerButton.isSelected() ? PlayerType.COMPUTER : PlayerType.HUMAN;
				blackPlayerType = blackComputerButton.isSelected() ? PlayerType.COMPUTER : PlayerType.HUMAN;
				GameSetUp.this.setVisible(false);
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Cancel");
				GameSetUp.this.setVisible(false);
			}
		});
		
		myPanel.add(okButton);
		myPanel.add(cancelButton);
		
		setLocationRelativeTo(frame);
		pack();
		setVisible(false);
	}
	
	void promptUser() {
		setVisible(true);
		repaint();
	}
	
	boolean isAIPlayer(final Player player) {
		if (player.getAlliance().isWhite()) {
			return getWhitePlayerType() == PlayerType.COMPUTER;
		}
		return getBlackPlayerType() == PlayerType.COMPUTER;
	}

	PlayerType getWhitePlayerType() {
		return this.whitePlayerType;
	}
	PlayerType getBlackPlayerType() {
		return this.blackPlayerType;
	}
	private static JSpinner addLabeledSpinner(final Container c,
											final String label,
											final SpinnerNumberModel model) {
		final JLabel l = new JLabel(label);
		c.add(l);
		final JSpinner spinner = new JSpinner(model);
		l.setLabelFor(spinner);
		c.add(spinner);
		return spinner;
	}
	int getSearchDepth() {
		return (Integer)(this.searchDepthSpinner.getValue());
	}
}
