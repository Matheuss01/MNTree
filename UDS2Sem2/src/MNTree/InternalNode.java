package MNTree;

import java.util.Comparator;

public class InternalNode<R extends Record> extends Node<R> {

	public Object[] keys;
	public Node<R>[] pointers;
	
	@SuppressWarnings("unchecked")
	public InternalNode(int order,  Comparator<Object> comparator) {
		super(order,comparator);
		keys = new Object[ORDER+1];
		pointers =/*(Node<R>[])*/ new Node[ORDER+1+1]; 
		type='I';
	}

	@Override
	public boolean isOverflow() {
		return size>ORDER;
	}

	@Override
	public boolean isUnderflow() {
		return size<ORDER/2;
	}

	//@Override
	//1. inserting leafnode-> add key from first record in parameter leaf node
	//2. inserting internalNode ->remove first key from parameter node , add it into curent node , and rearrange pointers
	public void insertIntoNode(Node<R> node) {
		if(node.type=='L') {
			LeafNode<R> leaf =((LeafNode<R>)node);
			Object key = ((R)leaf.records[0]).getKey();
			int indexToPlace=size;
			//finding index for placing the key in
			for (int i = 0; i < size; i++) {
				if(comp.compare(key, keys[i])<0) {
					indexToPlace=i;
					break;
				}				
			}
			//rearrange keys
			for (int i = size; i >0; i--) {
				if(i==indexToPlace) break;
				keys[i]=keys[i-1];
				pointers[i]=pointers[i-1];
			}
			//System.out.println("N-O-D-E"+node);
			pointers[indexToPlace+1]=node;
			/*if(indexToPlace==size) keys[indexToPlace+1]=key;
			else */
			keys[indexToPlace]=key;
		}else { //if internal Node
			InternalNode<R> interNode =((InternalNode<R>)node);
			Object key = ((R)interNode.keys[0]).getKey();
			
			//delete first key in old node
			for (int i = 0; i < keys.length-1; i++) {
				interNode.keys[i]=interNode.keys[i+1];
			}
			
			//finding index for placing the key in
			int indexToPlace=size;
			for (int i = 0; i < size; i++) {
				if(comp.compare(key, keys[i])<0) {
					indexToPlace=i;
					break;
				}				
			}
			//reaarange keys
			for (int i = size; i >0; i--) {
				if(i==indexToPlace) break;
				keys[i]=keys[i-1];
				pointers[i]=pointers[i-1];
			}
			pointers[indexToPlace+1]=node;
			/*if(indexToPlace==size) keys[indexToPlace+1]=key;
			else */
			keys[indexToPlace]=key;
		}		
		size++;
	}
	
	public InternalNode<R> split(){
		int middleIndex=size/2;
		InternalNode<R> newInternal = new InternalNode<R>(ORDER,comp);
		for (int i = middleIndex; i < keys.length; i++) {
			newInternal.keys[i-middleIndex]=keys[i];
			keys[i]=null;
			newInternal.pointers[i-middleIndex]=pointers[i+1];
			pointers[i+1]=null;
		}
		newInternal.size=size-middleIndex-1;
		size=middleIndex;
		return newInternal;		
	}
	
	public void setFirstPointerToNode(Node<R> node) {
		pointers[0]=node;
		for (int i=0;i<pointers.length;i++) {
			System.out.println(i+". "+(pointers[i]==null?"null":pointers[i].hashCode())+"SET--------");
		}
	}

	//@Override
	public void removeFromNode(R record, Node<R> node) {
		boolean b=false;
		int index=0;
		for (int i = 0; i < size; i++) {
			if(comp.compare(record.getKey(), keys[i])==0) {
				b=true;
				index=i;
				break;
			}
		}
		if(b) {
			for (int i = index; i < keys.length; i++) {
				keys[i]=keys[i+1];
			}
			pointers[size]=null;
			keys[--size]=null;
		}
	}
	
	public String toString() {
		String s="";
		s+="Internal Node[ "+this.hashCode()+"] <"+keys[0]+","+keys[size-1]+"> size: "+size+"\n";
		s+="KEYS\n";
		for (int i = 0; i < ORDER; i++) {
			if(keys[i]==null) s+=" Key "+i+"--\n";
			else s+=" Key "+i+". "+keys[i]+"\n";
		}
		s+="NODES\n";
		for (int i = 0; i < ORDER+1; i++) {
			if(pointers[i]==null) s+=" Node "+i+"--\n";
			else s+=" Node "+i+". "+pointers[i].hashCode()+"\n";
		}
		return s;
	}
	
	//toto bude pre DISK
	public int getPointerIndex(R record) {
		for (int i = 0; i < size; i++) {
			if(comp.compare(record.getKey(), keys[i])<0) return i;
		}
		return size;
	}
	//zatial pre Pamat
	public Node<R> getPointer(R record) {
		for (int i = 0; i < size; i++) {
			if(comp.compare(record.getKey(), keys[i])<0) return pointers[i];
		}
		return pointers[size];
	}
	
	
}
