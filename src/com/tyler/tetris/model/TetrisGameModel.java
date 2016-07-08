package com.tyler.tetris.model;

import java.util.Optional;

/**
 * Master container class for all model classes
 * @author Tyler
 */
public class TetrisGameModel {

	private AudioManager audioManager;
	private BlockBoardModel board;
	private ScoreModel scoreModel;
	private Optional<Block> currentHoldPiece;
	
	public TetrisGameModel() {
		this.audioManager = new AudioManager();
		this.board = new BlockBoardModel();
		this.scoreModel = new ScoreModel();
		this.currentHoldPiece = Optional.empty();
	}
	
	public AudioManager getAudioManager() {
		return audioManager;
	}
	
	public BlockBoardModel getBoard() {
		return board;
	}
	
	public ScoreModel getScoreModel() {
		return scoreModel;
	}
	
	public Optional<Block> getCurrentHoldPiece() {
		return currentHoldPiece;
	}

	public void clearCurrentHoldPiece() {
		this.currentHoldPiece = Optional.empty();
	}
	
	public void setCurrentHoldPiece(Block currentHoldPiece) {
		this.currentHoldPiece = Optional.of(currentHoldPiece);
	}
	
}
