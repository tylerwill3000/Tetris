package com.tyler.tetris;

import static org.junit.Assert.*;

import org.junit.Test;

public class BlockTest {

	@Test
	public void testInitialState() {
		Block block = new Block(BlockType.BOX);
		assertFalse(block.isHoldBlock());
		assertEquals(0, block.getOrientation());
		assertEquals(BlockType.BOX, block.getType());
	}
	
	@Test
	public void getOccupiedSquares() {
		
	}
	
}
