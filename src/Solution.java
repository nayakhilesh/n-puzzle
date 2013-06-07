import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Solution {

	private final static int UP = 1;
	private final static int RIGHT = 2;
	private final static int DOWN = 4;
	private final static int LEFT = 3;

	private static class Tuple {

		private int move;
		private List<Integer> board;

		public Tuple(List<Integer> board, int move) {
			this.board = board;
			this.move = move;
		}
	}

	private static class GameState implements Comparable<GameState> {

		private static int n;

		private List<Integer> board;
		private int zeroIndex;
		private int currDist;
		private int sumOfManhattanDistsToGoal;

		public GameState(List<Integer> board, int zeroIndex, int currDist,
				int sumOfManhattanDistsToGoal) {
			this.board = board;
			this.zeroIndex = zeroIndex;
			this.currDist = currDist;
			this.sumOfManhattanDistsToGoal = sumOfManhattanDistsToGoal;
		}

		public GameState(List<Integer> board, int zeroIndex, int currDist) {
			this(board, zeroIndex, currDist,
					getSumOfManhattanDistsToGoal(board));
		}

		public GameState copyAndMakeMove(int move) {

			int row = getRow(zeroIndex);
			int col = getCol(zeroIndex);

			int targetRow = 0, targetCol = 0;

			switch (move) {
			case UP:
				targetRow = row - 1;
				targetCol = col;
				break;
			case RIGHT:
				targetRow = row;
				targetCol = col + 1;
				break;
			case DOWN:
				targetRow = row + 1;
				targetCol = col;
				break;
			case LEFT:
				targetRow = row;
				targetCol = col - 1;
				break;
			}

			int targetIndex = getIndex(targetRow, targetCol);
			int targetValue = board.get(targetIndex);

			int targetGoalRow = getRow(targetValue);
			int targetGoalCol = getCol(targetValue);
			int targetMove = getOppMove(move);

			boolean isZeroCloser = (row > 0 && UP == move)
					|| (col > 0 && LEFT == move);
			boolean isTargetCloser = (targetRow - targetGoalRow > 0 && UP == targetMove)
					|| (targetRow - targetGoalRow < 0 && DOWN == targetMove)
					|| (targetCol - targetGoalCol > 0 && LEFT == targetMove)
					|| (targetCol - targetGoalCol < 0 && RIGHT == targetMove);

			List<Integer> newBoard = copyAndSwapIndexes(board, zeroIndex,
					targetIndex);
			int newZeroIndex = targetIndex;
			int newCurrDist = currDist + 1;
			int newSumOfManhattanDistsToGoal = sumOfManhattanDistsToGoal
					+ (isZeroCloser ? -1 : 1) + (isTargetCloser ? -1 : 1);

			return new GameState(newBoard, newZeroIndex, newCurrDist,
					newSumOfManhattanDistsToGoal);
		}

		private static int getSumOfManhattanDistsToGoal(List<Integer> board) {

			int sum = 0;
			int index = 0;
			for (int value : board) {
				sum += getManhattanDistToGoal(value, index);
				index++;
			}

			return sum;
		}

		// inline
		private static int getManhattanDistToGoal(int value, int index) {
			int goalRow = getRow(value);
			int goalCol = getCol(value);
			int row = getRow(index);
			int col = getCol(index);
			return Math.abs(goalRow - row) + Math.abs(goalCol - col);
		}

		@Override
		public int compareTo(GameState other) {
			return (this.currDist + this.sumOfManhattanDistsToGoal)
					- (other.currDist + other.sumOfManhattanDistsToGoal);
		}

		// if slow try inlining these methods
		private static int getRow(int index) {
			return index / n;
		}

		// inline
		private static int getCol(int index) {
			return index % n;
		}

		private List<Integer> copyAndSwapIndexes(List<Integer> board,
				int index1, int index2) {
			List<Integer> newBoard = new ArrayList<Integer>(board);

			int temp = newBoard.get(index1);
			newBoard.set(index1, newBoard.get(index2));
			newBoard.set(index2, temp);

			return newBoard;
		}

		// inline
		private int getOppMove(int move) {
			return 5 - move;
		}

		// inline
		private int getIndex(int row, int col) {
			return (row * 3) + col;
		}

		public List<Integer> getCandidateMoves() {

			List<Integer> listOfMoves = new ArrayList<Integer>(4);

			int row = getRow(zeroIndex);
			int col = getCol(zeroIndex);

			if (row != 0)
				listOfMoves.add(UP);
			if (row != n - 1)
				listOfMoves.add(DOWN);
			if (col != 0)
				listOfMoves.add(LEFT);
			if (col != n - 1)
				listOfMoves.add(RIGHT);

			return listOfMoves;
		}

	}

	public static void main(String[] args) throws NumberFormatException,
			IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int n = Integer.parseInt(br.readLine());
		GameState.n = n;
		List<Integer> input = new ArrayList<Integer>();

		for (int i = 1; i <= n * n; i++) {
			input.add(Integer.parseInt(br.readLine()));
		}

		int zeroIndex = input.indexOf(0);
		GameState initialGameState = new GameState(input, zeroIndex, 0);
		PriorityQueue<GameState> pq = new java.util.PriorityQueue<GameState>();
		pq.add(initialGameState);

		Map<List<Integer>, GameState> visitedBoards = new HashMap<List<Integer>, GameState>();
		visitedBoards.put(input, initialGameState);

		Map<List<Integer>, Tuple> cameFrom = new HashMap<List<Integer>, Tuple>();
		cameFrom.put(input, null);

		// TODO identify impossible input configurations

		GameState currentGameState;
		do {

			currentGameState = pq.poll();
			List<Integer> candidateMoves = currentGameState.getCandidateMoves();

			for (int move : candidateMoves) {

				GameState newGameState = currentGameState.copyAndMakeMove(move);
				if (!visitedBoards.containsKey(newGameState.board)) {
					pq.add(newGameState);
					visitedBoards.put(newGameState.board, newGameState);
					cameFrom.put(newGameState.board, new Tuple(
							currentGameState.board, move));
				} else {
					GameState oldGameState = visitedBoards
							.get(newGameState.board);
					if (newGameState.currDist < oldGameState.currDist) {
						pq.remove(oldGameState);
						pq.add(newGameState);
						visitedBoards.put(newGameState.board, newGameState);
						cameFrom.put(newGameState.board, new Tuple(
								currentGameState.board, move));
					}
				}
			}

		} while (!isGoal(currentGameState.board));

		List<Integer> moves = new LinkedList<Integer>();
		List<Integer> finalBoard = currentGameState.board;
		Tuple t;
		while ((t = cameFrom.get(finalBoard)) != null) {
			moves.add(t.move);
			finalBoard = t.board;
		}

		System.out.println(moves.size());

		Collections.reverse(moves);
		for (int move : moves) {
			switch (move) {
			case UP:
				System.out.println("UP");
				break;
			case RIGHT:
				System.out.println("RIGHT");
				break;
			case DOWN:
				System.out.println("DOWN");
				break;
			case LEFT:
				System.out.println("LEFT");
				break;
			}
		}

	}

	public static boolean isGoal(List<Integer> board) {

		int index = 0;
		for (int value : board) {
			if (value != index)
				return false;
			index++;
		}

		return true;
	}

}