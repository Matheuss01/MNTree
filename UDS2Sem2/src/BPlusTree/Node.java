package BPlusTree;

import java.util.Comparator;

import Classes.Record;

public abstract class Node<R extends Record> {
	
	protected char type; //I=internal , L=leaf
	protected final int ORDER;
	protected final Comparator<Object> comp;
	protected int size=0;
	
	protected InternalNode<R> parent;
	protected int blockPosition;
	
	public int getBlockPosition() {
		return blockPosition;
	}

	public void setBlockPosition(int blockPosition) {
		this.blockPosition = blockPosition;
	}

	public InternalNode<R> getParent() {
		return parent;
	}

	public void setParent(InternalNode<R> parent) {
		this.parent = parent;
	}

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
	
	public int getOrder() {
		return ORDER;
	}
	
	
	public int getORDER() {
		return ORDER;
	}

	public Comparator<Object> getComp() {
		return comp;
	}

	public void setType(char type) {
		this.type = type;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public abstract boolean isOverflow();
	public abstract boolean isUnderflow();
	
}
