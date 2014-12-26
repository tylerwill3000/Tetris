package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.GameBoardModel;
import model.PieceFactory;

public class SpecialPiecesFrame extends JFrame {
	
	private static final Map<Integer,String> pieceIDToName = initPieceIDToNameMap();
	
	private List<PieceSelectorButton> pieceSelectorButtons = new ArrayList<>();;
	
	private GameFrame.TetrisButton jbtReturn = new GameFrame.TetrisButton("Return");
	
	SpecialPiecesFrame() { 
		
		JPanel piecePanels = new JPanel(new GridLayout(1,3));
		
		for (int pieceID : PieceFactory.SPECIAL_BLOCK_IDS) {
			
			// Panel to display this piece
			NextPiecePanel display = new NextPiecePanel(
				pieceIDToName.get(pieceID),
				PieceFactory.order(pieceID)
			);
			
			// Selector button for this piece
			PieceSelectorButton selector = new PieceSelectorButton(pieceID);
			pieceSelectorButtons.add(selector);
			
			JLabel pointBonus = new JLabel("+" + GameBoardModel.getSpecialPieceBonusesPoints(pieceID) + " points per line");
			pointBonus.setHorizontalAlignment(SwingConstants.CENTER);
			pointBonus.setBorder(GameFrame.LINE_BORDER);
			
			JPanel menu = new JPanel(new BorderLayout());
			menu.add(selector, BorderLayout.NORTH);
			menu.add(pointBonus, BorderLayout.SOUTH);
			
			JPanel piecePanel = new JPanel(new BorderLayout());
			piecePanel.add(display, BorderLayout.CENTER);
			piecePanel.add(menu, BorderLayout.SOUTH);
			piecePanels.add(piecePanel);
			
		}
		
		JPanel saveContainer = new JPanel();
		saveContainer.add(jbtReturn);
		
		jbtReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dispose(); }
		});
		
		add(piecePanels, BorderLayout.CENTER);
		add(saveContainer, BorderLayout.SOUTH);
		
		FrameUtils.setIcon(this, "star.png");
		setTitle("Special Pieces");
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	private static Map<Integer,String> initPieceIDToNameMap() {
		Map<Integer,String> map = new HashMap<>();
		map.put(PieceFactory.TWIN_PILLARS_BLOCK_ID, "\"Twin-pillars\"");
		map.put(PieceFactory.ROCKET_BLOCK_ID, "\"Rocket\"");
		map.put(PieceFactory.DIAMOND_BLOCK_ID, "\"Diamond\"");
		return map;
	}
	
	// Provides minor extended functionality to a JButton to handle toggle events
	private static class PieceSelectorButton extends JButton {
		
		private int pieceID;
		
		// To construct, must pass the piece ID of the piece this button represents
		private PieceSelectorButton(int pieceID) {
			
			this.pieceID = pieceID;
			
			setActiveState(PieceFactory.isPieceActive(pieceID));
			setFocusable(false);
			
			addMouseMotionListener(new MouseAdapter() {
				public void mouseMoved(MouseEvent e) {
					setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
			});
			
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { toggle(); }
			});
			
			setPreferredSize(new Dimension(getWidth(), 28));
			
		}
		
		private void toggle() {
			
			boolean newActiveState = !isActive();
			setActiveState(newActiveState);
			
			if (newActiveState)
				PieceFactory.addPieceID(pieceID);
			else
				PieceFactory.removePieceID(pieceID);
				
		}
		
		public boolean isActive() { return getBackground() == Color.YELLOW; }
		
		public void setActiveState(boolean active) {
			super.setBackground(active ? Color.YELLOW : Color.LIGHT_GRAY);
			super.setText(active ? "Active" : "Inactive");
		}
		
	}	
	
}
