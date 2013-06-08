package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 
 * @author Akhilesh Nayak
 * 
 *         A priority queue implementation using a binary min heap. Also uses a
 *         hashmap to support O(1) contains and O(log n) remove & replace (can
 *         be used to change the priority of an object). Type parameter should
 *         override hashcode and equals to use hashmap based operations.
 * 
 */

public class UpdatablePriorityQueue<K extends Comparable<? super K>> {

	private ArrayList<K> heap;
	private Map<K, Integer> elementToPositionMap;

	public UpdatablePriorityQueue() {
		heap = new ArrayList<K>();
		elementToPositionMap = new HashMap<K, Integer>();
	}

	public boolean isEmpty() {
		return heap.isEmpty();
	}

	public int size() {
		return heap.size();
	}

	private void swapPositions(int position1, int position2) {

		int index1 = position1 - 1;
		int index2 = position2 - 1;
		K temp1 = heap.get(index1);
		K temp2 = heap.get(index2);

		heap.set(index1, temp2);
		heap.set(index2, temp1);

		elementToPositionMap.put(temp1, position2);
		elementToPositionMap.put(temp2, position1);

	}

	/**
	 * 
	 * @param k1 replaces this
	 * @param k2 with this
	 */
	public void replace(K k1, K k2) {

		if (k1 == null || k2 == null)
			throw new IllegalArgumentException("Input cannot be null");

		if (!elementToPositionMap.containsKey(k1))
			throw new NoSuchElementException(k1 + " isn't in the Queue");

		int positionOfk1 = elementToPositionMap.get(k1);
		int indexOfk1 = positionOfk1 - 1;

		heap.set(indexOfk1, k2);
		elementToPositionMap.remove(k1);
		elementToPositionMap.put(k2, positionOfk1);

		int compareValue = k2.compareTo(k1);

		if (compareValue == 0)
			return;

		if (compareValue < 0)
			swimPosition(positionOfk1);
		else
			sinkPosition(positionOfk1);

	}

	public K poll() {

		K min;

		if (heap.isEmpty()) {
			return null;
		}

		swapPositions(heap.size(), 1); // swap last with root
		min = heap.remove(heap.size() - 1); // remove last
		elementToPositionMap.remove(min);

		if (heap.size() == 0) {
			return min;
		}

		// heap size greater than 0 here

		sinkPosition(1);

		return min;

	}

	private void sinkPosition(int position) {

		// the following pointers: parent, left and right are with respect to
		// positions
		// not indexes -> to get indexes subtract 1
		int parent = position;

		while (true) {
			// percolating down operation
			int left = 2 * parent;
			int right = 2 * parent + 1;

			if (left > heap.size()) {
				// no left child
				// consequently no right child since right child is stored after
				// left
				break;// no children
			}

			if (right > heap.size()) {
				// no right child
				if (left > heap.size()) {
					// no left child
					break;// no children
				} else {
					// only left child exists
					if (heap.get(parent - 1).compareTo(heap.get(left - 1)) > 0) {
						// parent greater than child
						// swap parent and left child
						swapPositions(parent, left);
						parent = left;
					} else {
						break;// parent smaller than only existing child
					}
				}

			} else {
				// both children exist

				// find the smaller child
				int smallerChild = (heap.get(left - 1).compareTo(
						heap.get(right - 1)) < 0) ? left : right;
				if (heap.get(parent - 1).compareTo(heap.get(smallerChild - 1)) > 0) {
					// parent greater than smaller child
					// swap parent and smaller child
					swapPositions(parent, smallerChild);
					parent = smallerChild;
				} else {
					break;// parent smaller than smaller child
				}
			}
		}

	}

	public void add(K k) {

		if (k == null)
			throw new IllegalArgumentException("Cannot add null");

		heap.add(k);
		elementToPositionMap.put(k, heap.size());
		swimPosition(heap.size());

	}

	public void remove(K k) {

		if (k == null)
			throw new IllegalArgumentException("Cannot remove null");

		if (!elementToPositionMap.containsKey(k))
			throw new NoSuchElementException(k + " isn't in the Queue");

		int positionOfk = elementToPositionMap.get(k);

		K last = heap.get(heap.size() - 1);
		swapPositions(heap.size(), positionOfk); // swap last with k
		heap.remove(heap.size() - 1); // remove last
		elementToPositionMap.remove(k);

		int compareValue = k.compareTo(last);

		if (compareValue == 0)
			return;

		if (compareValue > 0)
			swimPosition(positionOfk);
		else
			sinkPosition(positionOfk);

	}

	private void swimPosition(int position) {

		// the following pointers: current, parent are with respect to
		// positions
		// not indexes -> to get indexes subtract 1
		int current = position;
		int parent = current / 2;

		// bubble up operation
		while (parent > 0
				&& heap.get(current - 1).compareTo(heap.get(parent - 1)) < 0) {

			// if current is less than its parent swap them
			swapPositions(parent, current);
			current = parent;
			parent /= 2;

		}

	}

	public boolean contains(K k) {
		return elementToPositionMap.containsKey(k);
	}

}