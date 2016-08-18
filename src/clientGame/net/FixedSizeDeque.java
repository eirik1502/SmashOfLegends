package clientGame.net;

import java.util.Iterator;
import java.util.LinkedList;

public class FixedSizeDeque<T> implements Iterable<T>{


	private LinkedList<T> queue = new LinkedList<>();
	private int maxSize;
	
	
	public FixedSizeDeque(int size) {
		maxSize = size;
	}
	
	public int getFixedSize() {
		return maxSize;
	}
	
	public void add(T e) {
		if (queue.size() >= maxSize) queue.poll();
		queue.add(e);
	}
	public T peek() {
		return queue.peek();
	}
	public T poll() {
		return queue.poll();
	}
	
	public void addFirst(T e) {
		if (queue.size() >= maxSize) queue.pollLast();
		queue.addFirst(e);
	}
	public T peekLast() {
		return queue.peekLast();
	}
	public T pollLast() {
		return queue.pollLast();
	}
	
	
	public T get(int index) {
		return queue.get(index);
	}
	public void set(int index, T e) {
		queue.set(index, e);
	}

	public int size() {
		return queue.size();
	}
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	public boolean containes(T e) {
		return queue.contains(e);
	}
	
	public Iterator<T> iterator() {
		return queue.iterator();
	}
}
