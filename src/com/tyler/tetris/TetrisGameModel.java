package com.tyler.tetris;

public class TetrisGameModel {

	private TetrisAudioSystem audioSystem;
	private TetrisBoard board;
	private ScoreKeeper scoreKeeper;
	
	public TetrisGameModel() {
		this.audioSystem = new TetrisAudioSystem();
		this.board = new TetrisBoard();
		this.scoreKeeper = new ScoreKeeper();
	}
	
	public TetrisGameModel(boolean timeAttack, boolean ghostSquares, boolean saveScores, int difficulty, boolean sountrackOn, boolean effectsOn) {
		this();
		this.audioSystem.setSoundtrackMuted(!sountrackOn);
		this.audioSystem.setEffectsMuted(!effectsOn);
		this.board.setGhostSquaresEnabled(ghostSquares);
		this.scoreKeeper.setTimeAttack(timeAttack);
		this.scoreKeeper.setDifficulty(difficulty);
	}
	
	public TetrisAudioSystem getAudioSystem() {
		return audioSystem;
	}
	
	public TetrisBoard getBoard() {
		return board;
	}
	
	public ScoreKeeper getScoreKeeper() {
		return scoreKeeper;
	}
	
}
