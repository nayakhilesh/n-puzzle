package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Solver {

	public void solve(int n, int[] input, int zeroIndex) {

		IntArrayWrapper inputWrapper = new IntArrayWrapper(input);
		GameState.setN(n);

		GameState initialGameState = new GameState(input, zeroIndex, 0);
		UpdatablePriorityQueue<GameState> pq = new UpdatablePriorityQueue<GameState>();

		Map<IntArrayWrapper, GameState> visitedBoards = new HashMap<IntArrayWrapper, GameState>();
		visitedBoards.put(inputWrapper, initialGameState);

		Map<IntArrayWrapper, BoardMoveTuple> cameFrom = new HashMap<IntArrayWrapper, BoardMoveTuple>();
		cameFrom.put(inputWrapper, null);

		// TODO identify impossible input configurations

		GameState currentGameState = initialGameState;
		while (currentGameState != null && !isGoal(currentGameState.getBoard())) {

			List<Integer> candidateMoves = currentGameState.getCandidateMoves();

			for (int move : candidateMoves) {
				GameState newGameState = currentGameState.copyAndMakeMove(move);
				IntArrayWrapper newGameStateBoardWrapper = new IntArrayWrapper(
						newGameState.getBoard());

				if (!visitedBoards.containsKey(newGameStateBoardWrapper)) {
					pq.add(newGameState);
					visitedBoards.put(newGameStateBoardWrapper, newGameState);
					cameFrom.put(newGameStateBoardWrapper, new BoardMoveTuple(
							currentGameState.getBoard(), move));
				} else {
					GameState oldGameState = visitedBoards
							.get(newGameStateBoardWrapper);
					if (newGameState.getCurrDist() < oldGameState.getCurrDist()) {
						if (pq.contains(oldGameState)) {
							pq.replace(oldGameState, newGameState);
						}
						visitedBoards.put(newGameStateBoardWrapper,
								newGameState);
						cameFrom.put(newGameStateBoardWrapper,
								new BoardMoveTuple(currentGameState.getBoard(),
										move));
					}
				}
			}

			currentGameState = pq.poll();
		}

		LinkedList<Integer> moves = new LinkedList<Integer>();
		IntArrayWrapper finalBoardWrapper = new IntArrayWrapper(
				currentGameState.getBoard());
		BoardMoveTuple t;
		while ((t = cameFrom.get(finalBoardWrapper)) != null) {
			moves.addFirst(t.getMove());
			finalBoardWrapper = new IntArrayWrapper(t.getBoard());
		}

		System.out.println(moves.size());

		for (int move : moves) {
			switch (move) {
			case GameState.UP:
				System.out.println("UP");
				break;
			case GameState.RIGHT:
				System.out.println("RIGHT");
				break;
			case GameState.DOWN:
				System.out.println("DOWN");
				break;
			case GameState.LEFT:
				System.out.println("LEFT");
				break;
			}
		}

	}

	private boolean isGoal(int[] board) {

		for (int i = 0; i < board.length - 1; i++) {
			if (board[i] != i)
				return false;
		}

		return true;
	}

	public static void main(String[] args) throws NumberFormatException,
			IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int n = Integer.parseInt(br.readLine());

		int[] input = new int[n * n];

		int zeroIndex = 0;
		for (int i = 0; i < n * n; i++) {
			int value = Integer.parseInt(br.readLine());
			if (value == 0)
				zeroIndex = i;
			input[i] = value;
		}

		long start = System.nanoTime();

		Solver solver = new Solver();
		solver.solve(n, input, zeroIndex);

		long end = System.nanoTime();

		System.out.println(((end - start) / 1000000000.0) + "s");

	}

}