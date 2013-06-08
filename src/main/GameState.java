package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameState implements Comparable<GameState> {

	public final static int UP = 1;
	public final static int RIGHT = 2;
	public final static int DOWN = 4;
	public final static int LEFT = 3;

	private static int n;
	private int[] board;
	private int zeroIndex;
	private int currDist;
	private int sumOfManhattanDistsToGoal;

	public static int getN() {
		return n;
	}

	public static void setN(int n) {
		GameState.n = n;
	}

	public int[] getBoard() {
		return board;
	}

	public int getCurrDist() {
		return currDist;
	}

	public GameState(int[] board, int zeroIndex, int currDist,
			int sumOfManhattanDistsToGoal) {
		this.board = board;
		this.zeroIndex = zeroIndex;
		this.currDist = currDist;
		this.sumOfManhattanDistsToGoal = sumOfManhattanDistsToGoal;
	}

	public GameState(int[] board, int zeroIndex, int currDist) {
		this(board, zeroIndex, currDist, getSumOfManhattanDistsToGoal(board));
	}

	public GameState copyAndMakeMove(int move) {

		int row = zeroIndex / getN();
		int col = zeroIndex % getN();

		int targetRow = row;
		int targetCol = col;

		switch (move) {
		case UP:
			targetRow = row - 1;
			break;
		case RIGHT:
			targetCol = col + 1;
			break;
		case DOWN:
			targetRow = row + 1;
			break;
		case LEFT:
			targetCol = col - 1;
			break;
		}

		int targetIndex = targetRow * getN() + targetCol;
		int targetValue = board[targetIndex];

		int targetGoalRow = targetValue / getN();
		int targetGoalCol = targetValue % getN();
		int targetMove = 5 - move;

		boolean isZeroCloser = (row > 0 && UP == move)
				|| (col > 0 && LEFT == move);
		boolean isTargetCloser = (targetRow - targetGoalRow > 0 && UP == targetMove)
				|| (targetRow - targetGoalRow < 0 && DOWN == targetMove)
				|| (targetCol - targetGoalCol > 0 && LEFT == targetMove)
				|| (targetCol - targetGoalCol < 0 && RIGHT == targetMove);

		int[] newBoard = copyAndSwapIndexes(board, zeroIndex, targetIndex);
		int newSumOfManhattanDistsToGoal = sumOfManhattanDistsToGoal
				+ (isZeroCloser ? -1 : 1) + (isTargetCloser ? -1 : 1);

		return new GameState(newBoard, targetIndex, currDist + 1,
				newSumOfManhattanDistsToGoal);
	}

	private static int getSumOfManhattanDistsToGoal(int[] board) {

		int sum = 0;
		for (int i = 0; i < board.length; i++) {
			int value = board[i];
			sum += Math.abs(value / getN() - i / getN())
					+ Math.abs(value % getN() - i % getN());
		}

		return sum;
	}

	@Override
	public int compareTo(GameState other) {
		return (this.currDist + this.sumOfManhattanDistsToGoal)
				- (other.currDist + other.sumOfManhattanDistsToGoal);
	}

	private int[] copyAndSwapIndexes(int[] board, int index1, int index2) {
		int[] newBoard = board.clone();

		int temp = newBoard[index1];
		newBoard[index1] = newBoard[index2];
		newBoard[index2] = temp;

		return newBoard;
	}

	public List<Integer> getCandidateMoves() {

		List<Integer> listOfMoves = new ArrayList<Integer>(4);

		int row = zeroIndex / getN();
		int col = zeroIndex % getN();

		if (row != 0)
			listOfMoves.add(UP);
		if (row != getN() - 1)
			listOfMoves.add(DOWN);
		if (col != 0)
			listOfMoves.add(LEFT);
		if (col != getN() - 1)
			listOfMoves.add(RIGHT);

		return listOfMoves;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(board);
		result = prime * result + currDist;
		result = prime * result + sumOfManhattanDistsToGoal;
		result = prime * result + zeroIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameState other = (GameState) obj;
		if (!Arrays.equals(board, other.board))
			return false;
		if (currDist != other.currDist)
			return false;
		if (sumOfManhattanDistsToGoal != other.sumOfManhattanDistsToGoal)
			return false;
		if (zeroIndex != other.zeroIndex)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GameState [board=" + Arrays.toString(board) + ", zeroIndex="
				+ zeroIndex + ", currDist=" + currDist
				+ ", sumOfManhattanDistsToGoal=" + sumOfManhattanDistsToGoal
				+ "]";
	}

}
