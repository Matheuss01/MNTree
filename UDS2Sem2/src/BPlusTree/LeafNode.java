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

	static boolean isOverflow(int size, int order) {
		return size>order;
	}
	
	static boolean isUnderflow(int size, int order) {
		return size<order/2.0;
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
	public void removeFromNode(Object key) {
		boolean b=false;
		int index=0;
		for (int i = 0; i < size; i++) {
			if(comp.compare(key, ((R)records[i]).getKey())==0) {
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
	
	
	
	public boolean borrowFromSibling(LeafNode<R> right, LeafNode<R> left, InternalNode<R> parent, Object keyToRemove) {
		//na zaciatku je uz stary record vymazany
		int sizeRight=right==null?-1:right.getSize();
		int sizeLeft=left==null?-1:left.getSize();
		//ked bude -1 znamena ze vzdy bude underflow siblinga a do tej vetvy nepojde
		
		if(!LeafNode.isUnderflow(sizeRight-1, ORDER)) {
			borrowFromRightSibling(right, parent,keyToRemove);
			return true;
		}
		else if(!LeafNode.isUnderflow(sizeLeft-1, ORDER)) {
			borrowFromLeftSibling(left, parent,keyToRemove);
			return true;
		}
		return false;
	}
	
	private void borrowFromLeftSibling(LeafNode<R> left, InternalNode<R> parent, Object keyToRemove) {
		R recordToBorrow = (R)left.getRecords()[left.getSize()-1];
		left.removeFromNode(recordToBorrow.getKey());
		
		insertIntoNode(recordToBorrow); ///it has to be position 0 ...-> check it
		//System.out.println("----"+keyToRemove+" , "+recordToBorrow.getKey());
		parent.replaceKey(keyToRemove, recordToBorrow.getKey());
		
	}
	
	private void borrowFromRightSibling(LeafNode<R> right, InternalNode<R> parent, Object keyToRemove) {
		R recordToBorrow = (R)right.getRecords()[0];
		right.removeFromNode(recordToBorrow.getKey());
		
		insertIntoNode(recordToBorrow); ///it has to be position size ...-> check it
		parent.replaceKey(recordToBorrow.getKey(),((R)right.getRecords()[0]).getKey()); //v otcovi sa nahradi kluc na prvok co siel za prvkom na pozicii 0 , terazjsim prvkom na poziii 0 v right siblingovi
	}
	
	//****************************MERGE
	
	
	public void mergeWithLeftSibling(LeafNode<R> left, InternalNode<R> parent, Object keyToRemove) {
		while(size>0) {
			left.insertIntoNode((R)records[size-1]);
			removeFromNode(((R)records[size-1]).getKey());
		}
		parent.removeFromNode(keyToRemove);
		if(parent.getSize()==0) parent.setPhantomKey(keyToRemove);
	}
	
	


	
	
	

}
