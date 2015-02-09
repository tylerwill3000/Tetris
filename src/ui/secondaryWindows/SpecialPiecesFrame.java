package ui.secondaryWindows;

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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ui.GameFrame;
import ui.NextPiecePanel;
import util.FrameUtils;
import model.Piece;
import model.PieceFactory;
import model.PieceFactory.PieceType;
import model.Properties;
import model.ScoreModel;

public class SpecialPiecesFrame extends SupplementarySettingsFrame {
	
	private List<PieceSelectorButton> _pieceSelectorButtons = new ArrayList<>();;
	
	public SpecialPiecesFrame() { 
		
		JPanel piecePanels = new JPanel(new GridLayout(1,3));
		
		for (PieceType pieceType : PieceType.getSpecialPieces()) {
			
			// Panel to display this piece
			NextPiecePanel display = new NextPiecePanel("\"" + pieceType + "\"", new Piece(pieceType));
			
			// Selector button for this piece
			PieceSelectorButton selector = new PieceSelectorButton(pieceType);
			selector.setActiveState(Properties.getActivePieceProperty(pieceType));
			_pieceSelectorButtons.add(selector);
			
			JLabel pointBonus = new JLabel("+" + ScoreModel.getSpecialPieceBonusPoints(pieceType) + " points per line");
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
		
		add(piecePanels, BorderLayout.CENTER);
		add(FrameUtils.nestInPanel(_btnSaveAndClose), BorderLayout.SOUTH);
		
		FrameUtils.setIcon(this, "star.png");
		setTitle("Special Pieces");
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	/**
	 *  Provides minor extended functionality to a JButton to handle toggle events
	 * @author Tyler
	 */
	private static class PieceSelectorButton extends JButton {
		
		private PieceType pieceType;
		
		// To construct, must pass the piece type of the piece this button represents
		private PieceSelectorButton(PieceType pieceType) {
			
			this.pieceType = pieceType;
			
			setActiveState(PieceFactory.isPieceActive(pieceType));
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
			Properties.setActivePieceProperty(pieceType, newActiveState);
		}
		
		public boolean isActive() { return getBackground() == Color.YELLOW; }
		
		public void setActiveState(boolean active) {
			super.setBackground(active ? Color.YELLOW : Color.LIGHT_GRAY);
			super.setText(active ? "Active" : "Inactive");
		}
		
	}	
	
}
