package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import model.PieceFactory;

public class SpecialPiecesFrame extends JFrame {
	
	// Allows me to keep track of which PieceSelectorButton objects map to which piece IDs
	Map<Integer, PieceSelectorButton> pieceIDToButtonMap = new HashMap<>();
	
	private NextPiecePanel cornerBlockDisplay =
			new NextPiecePanel("Corner Piece", PieceFactory.order(PieceFactory.CORNER_BLOCK_ID, Color.YELLOW));
	
	private NextPiecePanel twinPillarsBlockDisplay =
			new NextPiecePanel("Twin-pillars Piece", PieceFactory.order(PieceFactory.TWIN_PILLARS_BLOCK_ID, Color.GREEN));
	
	private JButton jbtSave = new JButton("Save and Return");
	
	SpecialPiecesFrame() {
		
		// All all selector buttons and set their text and background according to which are
		// cached as current active pieces
		pieceIDToButtonMap.put(
			PieceFactory.CORNER_BLOCK_ID,
			new PieceSelectorButton(PieceFactory.isPieceActive(PieceFactory.CORNER_BLOCK_ID)));
		
		pieceIDToButtonMap.put(
			PieceFactory.TWIN_PILLARS_BLOCK_ID,
			new PieceSelectorButton(PieceFactory.isPieceActive(PieceFactory.TWIN_PILLARS_BLOCK_ID)));
		
		JPanel cornerBlockPanel = new JPanel(new BorderLayout());
		cornerBlockPanel.add(cornerBlockDisplay, BorderLayout.CENTER);
		cornerBlockPanel.add(pieceIDToButtonMap.get(PieceFactory.CORNER_BLOCK_ID), BorderLayout.SOUTH);
		
		JPanel twinPillarsPanel = new JPanel(new BorderLayout());
		twinPillarsPanel.add(twinPillarsBlockDisplay, BorderLayout.CENTER);
		twinPillarsPanel.add(pieceIDToButtonMap.get(PieceFactory.TWIN_PILLARS_BLOCK_ID), BorderLayout.SOUTH);
		
		JPanel piecePanels = new JPanel(new GridLayout(1,2));
		piecePanels.add(cornerBlockPanel);
		piecePanels.add(twinPillarsPanel);
		
		JPanel saveContainer = new JPanel();
		saveContainer.add(jbtSave);
		jbtSave.setFocusable(false);
		jbtSave.addActionListener(SaveListener);
		
		add(piecePanels, BorderLayout.CENTER);
		add(saveContainer, BorderLayout.SOUTH);
		
		FrameUtils.setIcon(this, "star.png");
		setTitle("Special Pieces");
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	// The purpose of the save listener is to take all active pieces (depending
	// one which pieces the player selected) and add them to the valid piece ID
	// pool in the PieceFactory, as well as remove those from the pool that are
	// not selected
	private ActionListener SaveListener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			
			for (Integer id : PieceFactory.SPECIAL_BLOCK_IDS) {
				
				// Obtain the button representing this piece ID
				PieceSelectorButton b = pieceIDToButtonMap.get(id);
				
				if (b.isSelected())
					PieceFactory.addPieceID(id);
				else
					PieceFactory.removePieceID(id);
				
			}
			
			dispose();
			
		}
		
	};
	
	// Provides minor extended functionality to a JButton to handle toggle events
	private static class PieceSelectorButton extends JButton {
		
		private PieceSelectorButton(boolean selected) {
			
			this.setBackground(selected);
			this.setText(selected);
			
			setFocusable(false);
			
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { toggle(); }
			});
			
			setPreferredSize(new Dimension(getWidth(), 30));
			
		}
		
		private void toggle() {
			boolean currentlySelected = getBackground() == Color.YELLOW;
			this.setBackground(!currentlySelected);
			this.setText(!currentlySelected);
		}
		
		public void setBackground(boolean selected) {
			super.setBackground(selected ? Color.YELLOW : Color.LIGHT_GRAY);
		}
		
		public void setText(boolean selected) {
			super.setText(selected ? "Active" : "Inactive");
		}		
		
	}	
	
}
