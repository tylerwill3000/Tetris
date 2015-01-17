package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import model.Piece;
import model.PieceFactory;

public class BlockStylesFrame extends JFrame {
	
	private JComboBox<String> styleSelector = new JComboBox<>(new String[]{"Beveled","Etched"});
	private GameFrame.TetrisButton save = new GameFrame.TetrisButton("Return");
	private NextPiecePanel preview = new NextPiecePanel("Preview", new Piece(PieceFactory.PieceType.T_BLOCK));
	
	private static final Border[] PIECE_BORDERS = {
		GameFrame.BEVEL_BORDER,
		GameFrame.ETCHED_BORDER
	};
	
	// Corresponds to index of chosen border in combobox
	private static int chosenPieceBorder = 0;
	
	BlockStylesFrame() {
		
		JPanel previewPanel = new JPanel(new BorderLayout());
		previewPanel.add(preview);
		
		JPanel styleSelectorPanel = new JPanel();
		styleSelectorPanel.add(new JLabel("Style: "));
		styleSelectorPanel.add(styleSelector);
		styleSelector.setSelectedIndex(chosenPieceBorder);
		
		JPanel buttonContainer = new JPanel();
		buttonContainer.add(save);
		
		JPanel menu = new JPanel(new GridLayout(2,1));
		menu.add(styleSelectorPanel);
		menu.add(buttonContainer);
		
		styleSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chosenPieceBorder = styleSelector.getSelectedIndex();
				preview.paintCurrentPiece();
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { dispose(); }
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
	
	static Border getCurrentPieceBorder() { return PIECE_BORDERS[chosenPieceBorder]; }
	
}
