package BPlusTree;

import java.util.Comparator;

public abstract class Node<R extends Record> {
	
	protected char type; //I=internal , L=leaf
	protected final int ORDER;
	protected final Comparator<Object> comp;
	protected int size=0;
	
	public Node(int order, Comparator<Object> comparator) {
		ORDER=order;
		comp=comparator;
	}
	
	public char getType() {
		return type;
	}
	
	public int getSize() {
		return size;
	}
	
	
	public abstract boolean isOverflow();
	public abstract boolean isUnderflow();
	//public abstract void insertIntoNode(R record);
	//public abstract void removeFromNode(R record);
	
}
