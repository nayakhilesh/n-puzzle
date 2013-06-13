package main;

import java.util.Arrays;

public class IntArrayWrapper {

	int[] board;

	public IntArrayWrapper(int[] board) {
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
		IntArrayWrapper other = (IntArrayWrapper) obj;
		if (!Arrays.equals(board, other.board))
			return false;
		return true;
	}

}
