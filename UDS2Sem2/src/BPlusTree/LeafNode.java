package BPlusTree;

import java.util.Comparator;

import BPlusTree.Node;
import Classes.Record;

public class LeafNode<R extends Record> extends Node<R> {
	private Object[] records;
	private int positionLeft=-1,positionRight=-1;
	
	@SuppressWarnings("unchecked")
	public LeafNode(int order,  Comparator<Object> comparator) {
		super(order,comparator);
		records =new Object[ORDER+1];
		type='L';
	}	
	
	public Object[] getRecords() {
		return records;
	}

	public int getPositionLeft() {
		return positionLeft;
	}

	public void setPositionLeft(int positionLeft) {
		this.positionLeft = positionLeft;
	}

	public int getPositionRight() {
		return positionRight;
	}

	public void setPositionRight(int positionRight) {
		this.positionRight = positionRight;
	}

	
	@Override
	public boolean isOverflow() {
		return size>ORDER;
	}

	@Override
	public boolean isUnderflow() {
		return size<ORDER/2.0;
	}
	
	@SuppressWarnings("unchecked")
	public R getRecord(Object key) {
		for (int i = 0; i < size; i++) {
			if(comp.compare(key, ((R)records[i]).getKey())==0){
				return (R)records[i];
			}
		}
		return null;
	}
	
	public R getRecord(int index) {
		return (R) records[index];
	}

	public boolean insertIntoNode(R record) {
		int originalSize=size;
		boolean b=false;
		int index=0;
		if(size==0) {
			index=0;
			b=true;
			records[0]=record;//add to internal as well
		}
		else if(comp.compare(record.getKey(), ((R)records[0]).getKey())<0) {
			index=0;
			b=true;
		}
		else if(comp.compare(record.getKey(), ((R)records[size-1]).getKey())>0) {
			index=size;
			b=true;
		}
		else {
			for (int i = 0; i < size-1; i++) {
				if(comp.compare(record.getKey(), ((R)records[i]).getKey())>0 && comp.compare(record.getKey(), ((R)records[i+1]).getKey())<0) {
					index=i+1;
					b=true;
					break;
				}
			}
		}
		
		if(b) {
			for (int i = size; i >index; i--) {
				records[i]=records[i-1];
			}
			records[index]=record;
			size++;
		}
		return originalSize!=size;
	}

	//@Override
	public void removeFromNode(R record) {
		boolean b=false;
		int index=0;
		for (int i = 0; i < size; i++) {
			if(comp.compare(record, records[i])==0) {
				b=true;
				index=i;
				break;
			}
		}
		if(b) {
			for (int i = index; i < ORDER; i++) {
				records[i]=records[i+1];
			}
			records[--size]=null;
		}
	}
	
	public LeafNode<R> split(){ //TODO
		int middleIndex=size/2;
		LeafNode<R> newLeaf = new LeafNode<R>(ORDER,comp);
		
		for (int i = middleIndex; i < records.length; i++) {
			newLeaf.records[i-middleIndex]=records[i];
			records[i]=null;
		}
		newLeaf.size=size-middleIndex;
		size=middleIndex;
		return newLeaf;		
	}
	
	public void setRecords(Object[] records) {
		this.records = records;
	}

	public String toString() {
		String s="";
		if(size==0) s+="Leaf Node ["+this.hashCode()+"]"+" size: "+size+" , Left="+positionLeft+", Right: "+positionRight+" \n";
		else s+="Leaf Node ["+this.hashCode()+"] <"+((R)records[0]).getKey()+","+((R)records[size-1]).getKey()+">  size: "+size+" , Left="+positionLeft+", Right: "+positionRight+" \n";
		for (int i = 0; i < records.length-1; i++) {
			if(records[i]==null) s+=" "+i+" --\n";
			else s+=" "+i+". "+records[i]+"\n";
		}
		return s;
	}



}
