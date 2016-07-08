package com.tyler.tetris.ui.swing;

public class BlockStylesFrame {
	/*
	private JComboBox<String> _jcbxStyleSelector = new JComboBox<>(new String[]{"Beveled","Etched"});
	private NextPiecePanel _previewPanel = new NextPiecePanel("Preview", new Block(BlockType.T_BLOCK));
	
	private static final Border[] PIECE_BORDERS = {
		TetrisFrame.BEVEL_BORDER,
		TetrisFrame.ETCHED_BORDER
	};
	
	public BlockStylesFrame() {
		
		JPanel previewPanel = new JPanel(new BorderLayout());
		previewPanel.add(_previewPanel);
		
		JPanel styleSelectorPanel = new JPanel();
		styleSelectorPanel.add(new JLabel("Style: "));
		styleSelectorPanel.add(_jcbxStyleSelector);
		_jcbxStyleSelector.setSelectedIndex(TetrisProperties.getPieceBorder());
		
		JPanel menu = new JPanel(new GridLayout(2,1));
		menu.add(styleSelectorPanel);
		menu.add(FrameUtils.nestInPanel(_btnSaveAndClose));
		
		// Write new border property to properties file on selection change
		_jcbxStyleSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TetrisProperties.setPieceBorder(_jcbxStyleSelector.getSelectedIndex());
				_previewPanel.paintCurrentPiece();
			}
		});
		
		setLayout(new BorderLayout());
		add(previewPanel, BorderLayout.CENTER);
		add(menu, BorderLayout.SOUTH);
		
		FrameUtils.setIcon(this, "palette.png");
		setTitle("Styles");
		setResizable(false);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		
	}
	
	public static Border getCurrentPieceBorder() {
		return PIECE_BORDERS[TetrisProperties.getPieceBorder()];
	}
	*/
	
}
