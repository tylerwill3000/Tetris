package com.tyler.tetris.model;

import static java.util.stream.Collectors.toList;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.tyler.tetris.model.Block.ColoredSquare;

public enum BlockType {
	
	BOX(
		
		// Orientations. All the same for each direction
		// XX
		// XX
		new int[][][]{ 					
			{ {0,0},{-1,0},{-1,1},{0,1} },
			{ {0,0},{-1,0},{-1,1},{0,1} },
			{ {0,0},{-1,0},{-1,1},{0,1} },
			{ {0,0},{-1,0},{-1,1},{0,1} }						
		},
		
		// Next panel squares
		new int[][]{ {1,1},{1,2},{2,1},{2,2} },
		
		// Start row
		4,
		
		// Color
		new Color(0, 70, 255)
		
	),
	
	L_BLOCK_L(
		
		// Orientations
		new int[][][]{
			
			// X..
			// XXX
			// ...
			{ {-2,0},{-1,0},{-1,1},{-1,2} },
			
			// .XX
			// .X.
			// .X.
			{ {-2,1},{-2,2},{-1,1},{0,1} },
			
			// ...
			// XXX
			// ..X
			{ {-1,0},{-1,1},{-1,2},{0,2} },
			
			// .X.
			// .X.
			// XX.
			{ {0,0},{0,1},{-1,1},{-2,1} }
			
		},
		
		// Next panel squares
		new int[][]{ {1,1},{2,1},{2,2},{2,3} },
		
		// Start row
		5,
		
		// Color
		Color.YELLOW			
			
	),
	
	L_BLOCK_R(
			
		// Orientations
		new int[][][]{
			
			// ..X
			// XXX
			// ...
			{ {-1,0},{-1,1},{-1,2},{-2,2} },
			
			// .X.
			// .X.
			// .XX
			{ {0,1},{0,2},{-1,1},{-2,1} },
			
			// ...
			// XXX
			// X..
			{ {0,0},{-1,0},{-1,1},{-1,2} },
			
			// XX.
			// .X.
			// .X.
			{ {-2,0},{-2,1},{-1,1},{0,1} },
			
		},
		
		// Next panel squares
		new int[][]{ {1,3},{2,3},{2,2},{2,1} },
		
		// Start row
		5,
		
		// Color
		Color.PINK
			
	),
	
	S_BLOCK_L(
			
		// Orientations
		new int[][][]{
			
			// ...
			// XX.
			// .XX
			{ {-1,0},{-1,1},{0,1},{0,2} },
		
			// .X.
			// XX.
			// X..
			{ {0,0},{-1,0},{-1,1},{-2,1} },
		
			// Other 2 orientations are the same as the first 2, so
			// just cycle through them
			{ {-1,0},{-1,1},{0,1},{0,2} },
			{ {0,0},{-1,0},{-1,1},{-2,1} }
		
		},
		
		// Next panel squares
		new int[][]{ {1,1},{1,2},{2,2},{2,3} },
		
		// Start row
		4,
		
		// Color
		Color.GREEN

	),
	
	S_BLOCK_R(
		
		// Orientations
		new int[][][]{
			
			// ...
			// .XX
			// XX.
			{ {0,0},{0,1},{-1,1},{-1,2} },
			
			// .X.
			// .XX
			// ..X
			{ {-2,1},{-1,1},{-1,2},{0,2} },
			
			// Other 2 orientations are the same as the first 2, so
			// just cycle through them
			{ {0,0},{0,1},{-1,1},{-1,2} },
			{ {-2,1},{-1,1},{-1,2},{0,2} }
			
		},
		
		// Next panel squares
		new int[][]{ {1,2},{1,3},{2,1},{2,2} },
		
		// Start row
		4,
		
		// Color
		new Color(170, 45, 255) // Purple
		
	),
	
	STRAIGHT_LINE(
		
		// Orientations
		new int[][][]{
			
			// ....
			// ....
			// XXXX
			// ....
			{ {-1,0},{-1,1},{-1,2},{-1,3} },
			
			// .X..
			// .X..
			// .X..
			// .X..
			{ {0,1},{-1,1},{-2,1},{-3,1} },
			
			// Other 2 orientations are the same as the first 2, so
			// just cycle through them
			{ {-1,0},{-1,1},{-1,2},{-1,3} },				
			{ {0,1},{-1,1},{-2,1},{-3,1} }
		
		},
		
		// Next panel squares
		new int[][]{ {0,2},{1,2},{2,2},{3,2} },
		
		// Start row
		4,
		
		// Color
		new Color(0, 200, 200) // Blue-green
		
	),
	
	T_BLOCK(
		
		// Orientations
		new int[][][]{
			
			// ...
			// XXX
			// .X.
			{ {-1,0},{-1,1},{-1,2},{0,1} },
			
			// .X.
			// XX.
			// .X.
			{ {-1,0},{-2,1},{-1,1},{0,1} },
			
			// .X.
			// XXX
			// ...
			{ {-2,1},{-1,0},{-1,1},{-1,2} },
			
			// .X.
			// .XX
			// .X.
			{ {0,1},{-1,1},{-1,2},{-2,1} }		
			
		},
		
		// Next panel squares
		new int[][]{ {1,1},{1,2},{1,3},{2,2} },
		
		// Start row
		4,
		
		// Color
		new Color(255, 30, 0) // Red
			
	),
	
	TWIN_PILLARS(
			
		// Orientations
		new int[][][]{
			
			// ...
			// X.X
			// X.X
			{ {0,0},{-1,0},{-1,2},{0,2} },
			
			// XX.
			// ...
			// XX.
			{ {-2,0},{-2,1},{0,0},{0,1} },
			
			// Other two orientations are the same, so cycle through them again
			{ {0,0},{-1,0},{-1,2},{0,2} },
			{ {-2,0},{-2,1},{0,0},{0,1} },
			
		},
		
		// Next panel squares
		new int[][]{ {1,1},{2,1},{1,3},{2,3} },
		
		// Start row
		4,
		
		// Color
		new Color(80, 140, 45) // Forest-green
		
	),
	
	ROCKET(
		
		// Orientations
		new int[][][]{
			
			// .X.
			// .X.
			// X.X
			{ {0,0},{-1,1},{-2,1},{0,2} },
			
			// X..
			// .XX
			// X..
			{ {-2,0},{0,0},{-1,1},{-1,2} },
			
			// X.X
			// .X.
			// .X.
			{ {-2,0},{-2,2},{-1,1},{0,1} },
					
			// ..X
			// XX.
			// ..X
			{ {-1,0},{-1,1},{0,2},{-2,2} },
					
		},
		
		// Next panel squares
		new int[][]{ {3,1},{3,3},{2,2},{1,2} },
		
		// Start row
		5,
		
		// Color
		Color.ORANGE				
			
	),
	
	DIAMOND(
		
		// Orientations. All the same for each direction:
		// .X.
		// X.X
		// .X.
		new int[][][]{
			{ {0,1},{-1,0},{-1,2},{-2,1} },
			{ {0,1},{-1,0},{-1,2},{-2,1} },
			{ {0,1},{-1,0},{-1,2},{-2,1} },
			{ {0,1},{-1,0},{-1,2},{-2,1} }
		},
		
		// Next panel squares
		new int[][]{ {1,2},{2,1},{2,3},{3,2} },
		
		// Start row
		4,
		
		// Color
		Color.LIGHT_GRAY
		
	),
	
	;
	
	private int[][][] orientations;
	private int[][] nextPanelSquares;
	private int startRow;
	private Color color;
	
	private BlockType(int[][][] orientations, int[][] nextPanelSquares, int startRow, Color color) {
		this.orientations = orientations;
		this.nextPanelSquares = nextPanelSquares;
		this.startRow = startRow;
		this.color = color;
	}
	
	public List<Block.ColoredSquare> getNextPanelSquares() {
		return Arrays.stream(nextPanelSquares)
		             .map(coords -> new Block.ColoredSquare(color, coords[0], coords[1]))
		             .collect(toList());
	}

	public int getStartRow() {
		return startRow;
	}

	public Color getColor() {
		return color;
	}
	
	/**
	 * Renders the list of squares this type occupies at the given orientation, row and column
	 */
	public List<Block.ColoredSquare> calcOccupiedSquares(int orientation, int row, int col) {
		if (orientation < 0 || orientation > 3) {
			throw new IllegalArgumentException("Orientation value must be between 0 and 3");
		}
		int[][] offsets = orientations[orientation];
		return Arrays.stream(offsets)
		             .map(offset -> new ColoredSquare(row + offset[0], col + offset[1]))
		             .collect(toList());
	}
	
	@Override
	public String toString() {
		return name().charAt(0) + name().substring(1).toLowerCase().replace('_', ' ');
	}
	
	public static List<BlockType> getDefaultBlocks() {
		return Arrays.asList(
			BOX,
			L_BLOCK_L,
			L_BLOCK_R,
			S_BLOCK_L,
			S_BLOCK_R,
			STRAIGHT_LINE,
			T_BLOCK				
		);
	}
	
	public static List<BlockType> getSpecialBlocks() {
		return Arrays.asList(
			TWIN_PILLARS,
			ROCKET,
			DIAMOND
		);
	}

}
