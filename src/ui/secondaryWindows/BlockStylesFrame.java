package ui.secondaryWindows;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import ui.GameFrame;
import ui.NextPiecePanel;
import ui.util.FrameUtils;
import model.Piece;
import model.PieceFactory;
import model.Properties;

public class BlockStylesFrame extends SupplementarySettingsFrame {
	
	private JComboBox<String> styleSelector = new JComboBox<>(new String[]{"Beveled","Etched"});
	private NextPiecePanel preview = new NextPiecePanel("Preview", new Piece(PieceFactory.PieceType.T_BLOCK));
	
	private static final Border[] PIECE_BORDERS = {
		GameFrame.BEVEL_BORDER,
		GameFrame.ETCHED_BORDER
	};
	
	public BlockStylesFrame() {
		
		JPanel previewPanel = new JPanel(new BorderLayout());
		previewPanel.add(preview);
		
		JPanel styleSelectorPanel = new JPanel();
		styleSelectorPanel.add(new JLabel("Style: "));
		styleSelectorPanel.add(styleSelector);
		styleSelector.setSelectedIndex(Integer.parseInt(Properties.GAME_PROPERTIES.getProperty("piece.border.style")));
		
		JPanel menu = new JPanel(new GridLayout(2,1));
		menu.add(styleSelectorPanel);
		menu.add(FrameUtils.nestInPanel(saveAndClose));
		
		// Write new border property to properties file on selection change
		styleSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Properties.setPieceBorderProperty(styleSelector.getSelectedIndex());
				preview.paintCurrentPiece();
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
		return PIECE_BORDERS[Properties.getPieceBorderProperty()];
	}
	
}
