import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
		private int[] board;

		public Tuple(int[] board, int move) {
			this.board = board;
			this.move = move;
		}

	}

	private static class ArrayWrapper {

		int[] board;

		public ArrayWrapper(int[] board) {
			this.board = board;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(board);
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
			ArrayWrapper other = (ArrayWrapper) obj;
			if (!Arrays.equals(board, other.board))
				return false;
			return true;
		}

	}

	private static class GameState implements Comparable<GameState> {

		private static int n;
		private int[] board;
		private int zeroIndex;
		private int currDist;
		private int sumOfManhattanDistsToGoal;

		public GameState(int[] board, int zeroIndex, int currDist,
				int sumOfManhattanDistsToGoal) {
			this.board = board;
			this.zeroIndex = zeroIndex;
			this.currDist = currDist;
			this.sumOfManhattanDistsToGoal = sumOfManhattanDistsToGoal;
		}

		public GameState(int[] board, int zeroIndex, int currDist) {
			this(board, zeroIndex, currDist,
					getSumOfManhattanDistsToGoal(board));
		}

		public GameState copyAndMakeMove(int move) {

			int row = zeroIndex / n;
			int col = zeroIndex % n;

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

			int targetIndex = targetRow * n + targetCol;
			int targetValue = board[targetIndex];

			int targetGoalRow = targetValue / n;
			int targetGoalCol = targetValue % n;
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
				sum += Math.abs(value / n - i / n)
						+ Math.abs(value % n - i % n);
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

			int row = zeroIndex / n;
			int col = zeroIndex % n;

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
		int[] input = new int[n * n];

		ArrayWrapper inputWrapper = new ArrayWrapper(input);

		int zeroIndex = 0;
		for (int i = 0; i < n * n; i++) {
			int value = Integer.parseInt(br.readLine());
			if (value == 0)
				zeroIndex = i;
			input[i] = value;
		}

		long start = System.nanoTime();

		GameState initialGameState = new GameState(input, zeroIndex, 0);
		PriorityQueue<GameState> pq = new java.util.PriorityQueue<GameState>();
		pq.add(initialGameState);

		Map<ArrayWrapper, GameState> visitedBoards = new HashMap<ArrayWrapper, GameState>();
		visitedBoards.put(inputWrapper, initialGameState);

		Map<ArrayWrapper, Tuple> cameFrom = new HashMap<ArrayWrapper, Tuple>();
		cameFrom.put(inputWrapper, null);

		// TODO identify impossible input configurations

		GameState currentGameState = initialGameState;
		while (currentGameState != null && !isGoal(currentGameState.board)) {

			List<Integer> candidateMoves = currentGameState.getCandidateMoves();

			for (int move : candidateMoves) {
				GameState newGameState = currentGameState.copyAndMakeMove(move);
				ArrayWrapper newGameStateBoardWrapper = new ArrayWrapper(
						newGameState.board);

				if (!visitedBoards.containsKey(newGameStateBoardWrapper)) {
					pq.add(newGameState);
					visitedBoards.put(newGameStateBoardWrapper, newGameState);
					cameFrom.put(newGameStateBoardWrapper, new Tuple(
							currentGameState.board, move));
				} else {
					GameState oldGameState = visitedBoards
							.get(newGameStateBoardWrapper);
					if (newGameState.currDist < oldGameState.currDist) {
						pq.remove(oldGameState);
						pq.add(newGameState);
						visitedBoards.put(newGameStateBoardWrapper,
								newGameState);
						cameFrom.put(newGameStateBoardWrapper, new Tuple(
								currentGameState.board, move));
					}
				}
			}

			currentGameState = pq.poll();
		}

		LinkedList<Integer> moves = new LinkedList<Integer>();
		ArrayWrapper finalBoardWrapper = new ArrayWrapper(
				currentGameState.board);
		Tuple t;
		while ((t = cameFrom.get(finalBoardWrapper)) != null) {
			moves.addFirst(t.move);
			finalBoardWrapper = new ArrayWrapper(t.board);
		}

		System.out.println(moves.size());

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

		long end = System.nanoTime();

		System.out.println(((end - start) / 1000000000.0) + "s");

	}

	public static boolean isGoal(int[] board) {

		for (int i = 0; i < board.length; i++) {
			if (board[i] != i)
				return false;
		}

		return true;
	}

}