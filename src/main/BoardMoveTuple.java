package main;

public class BoardMoveTuple {

	private int[] board;
	private int move;

	public BoardMoveTuple(int[] board, int move) {
		this.board = board;
		this.move = move;
	}

	public int[] getBoard() {
		return board;
	}

	public int getMove() {
		return move;
	}

}
