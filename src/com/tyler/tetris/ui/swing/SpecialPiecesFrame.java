package com.tyler.tetris.ui.swing;

public class SpecialPiecesFrame {
	/*
	private List<PieceSelectorButton> _pieceSelectorButtons = new ArrayList<>();;
	
	public SpecialPiecesFrame() { 
		
		JPanel piecePanels = new JPanel(new GridLayout(1,3));
		
		for (BlockType pieceType : BlockType.getSpecialBlocks()) {
			
			// Panel to display this piece
			NextPiecePanel display = new NextPiecePanel("\"" + pieceType + "\"", new Block(pieceType));
			
			// Selector button for this piece
			PieceSelectorButton selector = new PieceSelectorButton(pieceType);
			selector.setActiveState(TetrisProperties.isUsingBlockType(pieceType));
			_pieceSelectorButtons.add(selector);
			
			JLabel pointBonus = new JLabel("+" + ScoreModel.getSpecialPieceBonusPoints(pieceType) + " points per line");
			pointBonus.setHorizontalAlignment(SwingConstants.CENTER);
			pointBonus.setBorder(TetrisFrame.LINE_BORDER);
			
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
	
	private static class PieceSelectorButton extends JButton {
		
		private BlockType pieceType;
		
		// To construct, must pass the piece type of the piece this button represents
		private PieceSelectorButton(BlockType pieceType) {
			
			this.pieceType = pieceType;
			
			setActiveState(BlockConveyor.isActive(pieceType));
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
			TetrisProperties.setUsingBlockType(pieceType, newActiveState);
		}
		
		public boolean isActive() { return getBackground() == Color.YELLOW; }
		
		public void setActiveState(boolean active) {
			super.setBackground(active ? Color.YELLOW : Color.LIGHT_GRAY);
			super.setText(active ? "Active" : "Inactive");
		}
		
	}	
*/	
}
