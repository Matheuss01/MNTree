package MNTree;

import java.util.Comparator;

public class LeafNode<R extends Record> extends Node<R> {
	private Object[] records;
	private LeafNode<R> left,right;
	
	@SuppressWarnings("unchecked")
	public LeafNode(int order,  Comparator<Object> comparator) {
		super(order,comparator);
		records =new Object[ORDER+1];
		type='L';
	}	
		
	
	public Object[] getRecords() {
		return records;
	}


	public LeafNode<R> getLeft() {
		return left;
	}

	public void setLeft(LeafNode<R> left) {
		this.left = left;
	}

	public LeafNode<R> getRight() {
		return right;
	}

	public void setRight(LeafNode<R> right) {
		this.right = right;
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

	//@Override
	public void insertIntoNode(R record) {
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
	
	public LeafNode<R> split(){
		int middleIndex=size/2;
		LeafNode<R> newLeaf = new LeafNode<R>(ORDER,comp);
		//dont forget when changing to disk operations -> pointers of chain
		newLeaf.setLeft(this);
		newLeaf.setRight(right);
		if(right!=null) right.setLeft(newLeaf);
		setRight(newLeaf);
		for (int i = middleIndex; i < records.length; i++) {
			newLeaf.records[i-middleIndex]=records[i];
			records[i]=null;
		}
		newLeaf.size=size-middleIndex;
		size=middleIndex;
		return newLeaf;		
	}
	
	
	
	
	
	
	public String toString() {
		String s="";
		s+="Leaf Node ["+this.hashCode()+"] <"+records[0]+","+records[size-1]+">  size: "+size+"\n";
		for (int i = 0; i < records.length-1; i++) {
			if(records[i]==null) s+=" "+i+" --\n";
			else s+=" "+i+". "+records[i]+"\n";
		}
		return s;
	}



}
