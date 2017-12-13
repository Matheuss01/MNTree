package BPlusTree;

import java.util.Comparator;

import BPlusTree.Node;
import Classes.Record;

public class InternalNode<R extends Record> extends Node<R> {

	public Object[] keys;
	public int[] pointers;
	private Object phantomKey;
	
	@SuppressWarnings("unchecked")
	public InternalNode(int order,  Comparator<Object> comparator) {
		super(order,comparator);
		keys = new Object[ORDER+1];
		pointers =/*(Node<R>[])*/ new int[ORDER+1+1]; 
		type='I';
	}
	
	public Object getPhantomKey() {
		return phantomKey;
	}
	
	public void setPhantomKey(Object key) {
		phantomKey=key;
	}
	
	
	public Object[] getKeys() {
		return keys;
	}

	public void setKeys(Object[] keys) {
		this.keys = keys;
	}

	public int[] getPointers() {
		return pointers;
	}

	public void setPointers(int[] pointers) {
		this.pointers = pointers;
	}

	@Override
	public boolean isOverflow() {
		return size>ORDER;
	}

	@Override
	public boolean isUnderflow() {
		return size<ORDER/2;
	}
	
	static boolean isUnderflow(int size, int order) {
		return size<order/2;
	}

	
	
	//@Override
	//1. inserting leafnode-> add key from first record in parameter leaf node
	//2. inserting internalNode ->remove first key from parameter node , add it into curent node , and rearrange pointers
	public void insertIntoNode(Node<R> node, int indexOfNode) {
		if(node.type=='L') {
			LeafNode<R> leaf =((LeafNode<R>)node);
			Object key = ((R)leaf.getRecords()[0]).getKey();
			int indexToPlace=size;
			//finding index for placing the key in
			for (int i = 0; i < size; i++) {
				if(comp.compare(key, keys[i])<0) {
					indexToPlace=i;
					break;
				}				
			}
			//rearrange keys
			pointers[size+1]=pointers[size];
			for (int i = size; i >0; i--) {
				if(i==indexToPlace) break;
				keys[i]=keys[i-1];
				pointers[i]=pointers[i-1];
			}
			//System.out.println("N-O-D-E"+node);
			pointers[indexToPlace+1]=indexOfNode;
			/*if(indexToPlace==size) keys[indexToPlace+1]=key;
			else */
			keys[indexToPlace]=key;
		}else { //if internal Node
			InternalNode<R> interNode =((InternalNode<R>)node);
			Object key = interNode.keys[0];    
			
			/*if((Integer)key == 30) {
				System.out.println("tuu");
			}*/
			
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
			pointers[size+1]=pointers[size];
			for (int i = size; i >0; i--) {
				if(i==indexToPlace) break;
				keys[i]=keys[i-1];
				pointers[i]=pointers[i-1];
			}
			pointers[indexToPlace+1]=indexOfNode;
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
			//keys[i]=null;
			newInternal.pointers[i-middleIndex]=pointers[i+1];
			//pointers[i+1]=null;
		}
		newInternal.size=size-middleIndex-1;
		size=middleIndex;
		return newInternal;		
	}
	
	public void setFirstPointerToNode(int node) {
		pointers[0]=node;
	}

	//@Override
	public void removeFromNode(Object key) { 
		boolean b=false;
		int index=0;
		for (int i = 0; i < size; i++) {
			if(comp.compare(key, keys[i])==0) {
				b=true;
				index=i;
				break;
			}
		}
		if(b) {
			for (int i = index; i < keys.length-1; i++) {
				keys[i]=keys[i+1];
			}
			for (int i = index+1; i < pointers.length-1; i++) {
				pointers[i]=pointers[i+1];
			}
			size--;
		}
	}
	
	public void removeFromNode(LeafNode<R> node) { 
		int positionOfNode = node.getBlockPosition();

		for (int i = 0; i < size+1; i++) {
			if(pointers[i]==positionOfNode) {
				removeFromNode(keys[i-1]);
				return;
			}
		}
	}
	
	public String toString() {
		String s="";
		s+="Internal Node[ "+this.hashCode()+"] <"+(size>0?(keys[0]+","+keys[size-1]):"empty")+"> size: "+size+"\n";
		s+="KEYS\n";
		for (int i = 0; i < ORDER; i++) {
			if(i>=size) s+=" Key "+i+"--\n";
			else s+=" Key "+i+". "+keys[i]+"\n";
		}
		s+="NODES\n";
		for (int i = 0; i < ORDER+1; i++) {
			if(i>size) s+=" Node "+i+"--\n";
			else s+=" Node "+i+". "+pointers[i]+"\n";
		}
		return s;
	}
	
	//toto bude pre DISK
	public int getPointer(Object key) {
		for (int i = 0; i < size; i++) {
			if(comp.compare(key, keys[i])<0) return pointers[i];
		}
		return pointers[size];
	}
	
	/*
	 * returns position of block which is left sibling for a node starting with a given key , if first son of a node then returns -1
	 */
	/*public int getLeftSibling(Object key) {
		if(size==0) return -1;
		
		for (int i = 0; i < size; i++) {
			if(comp.compare(key, keys[i])<=0) return pointers[i];
		}
		return pointers[size-1];
	}*/
	
	/*public int getRightSibling(Object key) {
		for (int i = 0; i < size; i++) {
			if(comp.compare(key, keys[i])<0) return pointers[i+1];
		}
		return -1;
	}*/
	
	public int getRightSibling(Node<R> son) {
		for (int i = 0; i < size; i++) {
			if(pointers[i]==son.getBlockPosition()) {
				return pointers[i+1];
			}
		}
		return -1;
	}
	
	public int getLeftSibling(Node<R> son) {
		for (int i = 1; i < size+1; i++) {
			if(pointers[i]==son.getBlockPosition()) {
				return pointers[i-1];
			}
		}
		return -1;
	}
	
	public int getPointer(int index){
		return pointers[index];
	}
	
	public boolean containsKey(Object key) {
		for (int i = 0; i < size; i++) {
			if(comp.compare(key, keys[i])==0) return true;
		}
		return false;
	}
	
	public void replaceKey(R oldRecord, R newRecord) {
		for (int i = 0; i < size; i++) {
			if(comp.compare(oldRecord.getKey(), keys[i])==0) {
				keys[i]=newRecord.getKey();
			}
		}
	}
	
	public void replaceKey(Object oldKey, Object newKey) {
		for (int i = 0; i < size; i++) {
			//System.out.println(size+" "+oldKey+" "+keys[i]+" "+newKey);
			if(comp.compare(oldKey, keys[i])==0) {
				keys[i]=newKey;
			}
		}
	}
	
	private Object getKeyForRightRotation(InternalNode<R> parent) {
		
		for (int i = 1; i < parent.getSize()+1; i++) {
			if(parent.getPointer(i)==blockPosition) return parent.getKeys()[i-1];
		}
		System.out.println("tu by nemal byt : IN rightrotation");
		return -1;
	}
	
	private Object getKeyForLeftRotation(InternalNode<R> parent) { // pointer za klucom
		for (int i = 0; i < parent.getSize(); i++) {
			if(parent.getPointer(i)==blockPosition) return parent.getKeys()[i];
		}
		System.out.println("tu by nemal byt : IN leftrotation");
		return -1;
	}
	
	//*******BORROW
	
	public boolean borrowFromSibling(InternalNode<R> right, InternalNode<R> left, InternalNode<R> parent, Object keyToRemove) {
		//na zaciatku je uz stary record vymazany
		int sizeRight=right==null?-1:right.getSize();
		int sizeLeft=left==null?-1:left.getSize();
		//ked bude -1 znamena ze vzdy bude underflow siblinga a do tej vetvy nepojde
		
		if(!InternalNode.isUnderflow(sizeRight-1, ORDER)) {
			borrowFromRightSibling(right, parent,keyToRemove);
			return true;
		}
		else if(!InternalNode.isUnderflow(sizeLeft-1, ORDER)) {
			borrowFromLeftSibling(left, parent,keyToRemove);
			return true;
		}
		return false;
	}
	
	private void borrowFromLeftSibling(InternalNode<R> left, InternalNode<R> parent, Object keyToRemove) {
		Object keyToBorrow = left.getKeys()[left.getSize()-1];
		int positionToBorrow = left.getPointer(left.getSize());
		Object KeyToReplaceInParent=getKeyForRightRotation(parent);
		left.removeFromNode(keyToBorrow);
		parent.replaceKey(KeyToReplaceInParent, keyToBorrow);
		
		for (int i = size; i > 0; i--) {
			keys[i] = keys[i-1];
		}
		for (int i = size+1; i > 0; i--) {
			pointers[i] = pointers[i-1];
		}
		
		keys[0]=KeyToReplaceInParent;
		pointers[0]=positionToBorrow;
		size++;
		
	}
	
	private void borrowFromRightSibling(InternalNode<R> right, InternalNode<R> parent, Object keyToRemove) {
		Object keyToBorrow = right.getKeys()[0];
		int positionToBorrow = right.getPointer(0);
		Object KeyToReplaceInParent=getKeyForLeftRotation(parent);
		//right.removeFromNode(keyToBorrow);
		
		for (int i =0; i<right.getSize() ; i++) {
			right.keys[i] = right.keys[i+1];
		}
		for (int i =0; i<right.getSize()+1 ; i++) {
			right.pointers[i] = right.pointers[i+1];
		}
		right.size--;
		
		parent.replaceKey(KeyToReplaceInParent, keyToBorrow);
		
		keys[size]=KeyToReplaceInParent;
		pointers[size+1]=positionToBorrow;
		size++;
	}
	
	public void mergeWithLeftSibling(InternalNode<R> left, InternalNode<R> parent, Object keyToRemove) {
		
		Object parentsKey; 
		parentsKey = getKeyForRightRotation(parent);
		left.getKeys()[left.getSize()]=parentsKey;
		for (int i = 0; i < size; i++) {
			left.getKeys()[left.getSize()+1+i]=keys[i];
			left.getPointers()[left.getSize()+1+i]=pointers[i];
		}
		left.setSize(left.getSize()+1+size);
		left.getPointers()[left.getSize()]=pointers[size];
		
		parent.removeFromNode(parentsKey);
		
		size=0;
	}

}
