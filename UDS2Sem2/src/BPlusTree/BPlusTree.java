package BPlusTree;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import Classes.Record;
import Converters.RecordConverter;

public class BPlusTree<R extends Record> {
	
	private Node<R> root; 
	private final Comparator<Object> comp;
	private final DiskManager<R> diskManager;
	private final Metadata metadata;
	private HashMap<Node<R>,Integer> mapping = new HashMap<>();
	
	public BPlusTree(int order, Comparator<Object> comparator, RecordConverter recordConverter, int blockSize, int keySize, int recordSize) throws Exception { //bez nacitania stromu zo subotu, strom sa vytvara v pamati
		this.comp=comparator;
		root= new LeafNode<R>(order, comparator);
		metadata=new Metadata(blockSize, 1, -1, 0, order, keySize, recordSize,2); //-1-> no empty block , root je 2. block cize position 1
		diskManager = new DiskManager<R>(metadata, recordConverter);
		diskManager.writeMetadata(metadata);
		diskManager.writeNode(metadata.getPositionOfRoot(), root);		
	}
	
	/*public BPlusTree(int order, Comparator<Object> comparator, File f) { //ked bude zadany subor z ktoreho sa ma strom nacitat
		this.comp=comparator;
		//root= new LeafNode<R>(order, comparator); ------root sa nacita zo suboru
	}*/
	
	public DiskManager<R> getDiskManager(){
		return diskManager;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}
	//first it finds leaf which is supposed to contain the key
	//key is searched in the leaf
	//if no such key -> return null
	public R get(Object key) throws IOException, Exception {
		LeafNode<R> leaf=(LeafNode<R>)getTraversalToLeaf(key).getLast();
		return leaf.getRecord(key);
	}
	
	public LeafNode<R> getLeaf(Object key) throws IOException, Exception {
		LinkedList<Node<R>> travers =getTraversalToLeaf(key);
		if(travers.size()<=0) return null;
		else return (LeafNode<R>)travers.getLast();
		
	}
	
	public void update(LeafNode<R> leaf) throws Exception {
		diskManager.writeNode(mapping.get(leaf), leaf);
	}
	
	public LinkedList<R> getRecordsInInterval(Object minKey, Object maxKey) throws IOException, Exception{
		LinkedList<R> res = new LinkedList<>();
		LeafNode<R> leaf=(LeafNode<R>)getTraversalToLeaf(minKey).getLast();
		while(leaf!=null) {
			for (int i = 0; i < leaf.getSize(); i++) {
				if(comp.compare(leaf.getRecord(i).getKey(), minKey)>=0 && comp.compare(leaf.getRecord(i).getKey(), maxKey)<=0) {
					res.add(leaf.getRecord(i));
				}
			}
			if(comp.compare(leaf.getRecord(leaf.getSize()-1).getKey(), maxKey)>=0) break;
			if(leaf.getPositionRight()<0) break;
			else leaf = (LeafNode<R>)diskManager.readNode(leaf.getPositionRight());
		}
		return res;
	}
	
	private LeafNode<R> minNode() throws IOException, Exception {
		Node<R> n=root;
		if(n.size==0) return null; 
		int index=0;
		while(n.getType()=='I') {			
			index=((InternalNode<R>)n).getPointer(0);
			n=diskManager.readNode(index);
		}		
		return (LeafNode<R>) n;
	}
	
	private LeafNode<R> maxNode() throws IOException, Exception {
		Node<R> n=root;
		if(n.size==0) return null; 
		int index;
		while(n.getType()=='I') {
			index=((InternalNode<R>)n).getPointer(n.size);
			n=diskManager.readNode(index);
		}		
		return (LeafNode<R>) n;
	}
	
	public R min() throws IOException, Exception {
		LeafNode<R> leaf = minNode();
		return leaf.getRecord(0);
	}
	
	public R max() throws IOException, Exception {
		LeafNode<R> leaf = minNode();
		return leaf.getRecord(leaf.size-1);
	}
	

	public void add(R record) throws IOException, Exception {
		int pos;
		LinkedList<Node<R>> path = getTraversalToLeaf(record.getKey());
		
		//*********adding overroot
		InternalNode<R> overRoot = new InternalNode<R>(metadata.getOrder(), comp);
		path.addFirst(overRoot);
		LeafNode<R> leaf = (LeafNode<R>)path.removeLast();
		InternalNode<R> parent=(InternalNode<R>)path.getLast();;
		boolean isAdded =leaf.insertIntoNode(record); //znamena ze taky tam este nebol a bude sa zvysovat size
		if(isAdded) metadata.setNumberOfRecords(metadata.getNumberOfRecords()+1); //size++
		
		if(leaf.isOverflow()) {
			LeafNode<R> newLeaf =leaf.split(); //number of blocks sa inkrementuje priamo v node
			pos=diskManager.writeNewNode(newLeaf); //adresa resp. pozicia noveho nodu(blocku) v subore
			mapping.put(newLeaf, pos);
			newLeaf.setPositionLeft(mapping.get(leaf));
			newLeaf.setPositionRight(leaf.getPositionRight());
			diskManager.writeNode(mapping.get(newLeaf), newLeaf);
			if(leaf.getPositionRight()!=-1) {
				LeafNode<R> rightneighbour = (LeafNode<R>)diskManager.readNode(leaf.getPositionRight());
				mapping.put(rightneighbour, leaf.getPositionRight());
				rightneighbour.setPositionLeft(mapping.get(newLeaf));
				diskManager.writeNode(mapping.get(rightneighbour), rightneighbour);
			}
			leaf.setPositionRight(mapping.get(newLeaf));
			parent=(InternalNode<R>)path.removeLast();
			parent.insertIntoNode(newLeaf,pos);                 //priradenie otcovi novy leaf + jeho pozicia
			
			//this means overroot was used for as a new root
			if(path.isEmpty()) {
				//number of blocks sa inkrementuje priamo v node , ....pouzity overroot a ten nie je zo suboru
				parent.setFirstPointerToNode(mapping.get(leaf));
				root=parent;
				pos=diskManager.writeNewNode(parent);
				mapping.put(parent,pos);
				metadata.setPositionOfRoot(mapping.get(parent));
			}else {
				diskManager.writeNode(mapping.get(parent), parent);
			}
		}
		diskManager.writeNode(mapping.get(leaf), leaf); 

		boolean inCycle = false;
		InternalNode<R> node=parent;
		InternalNode<R> newInternal;
		while(node.isOverflow()) {
			inCycle = true;
			
			newInternal = node.split();
			pos=diskManager.writeNewNode(newInternal);
			mapping.put(newInternal,pos);
			parent=(InternalNode<R>) path.removeLast();
			parent.insertIntoNode(newInternal,pos);
			
			if(path.isEmpty()) {
				parent.setFirstPointerToNode(mapping.get(node));
				root=parent;
				pos=diskManager.writeNewNode(parent);
				mapping.put(parent,pos);
				metadata.setPositionOfRoot(mapping.get(parent));
			}
			diskManager.writeNode(mapping.get(newInternal), newInternal);
			diskManager.writeNode(mapping.get(node),node);
			node=parent;
		}
		if(!path.isEmpty() && inCycle) {
			diskManager.writeNode(mapping.get(node),node);
		}
		
		diskManager.writeMetadata(metadata);
	}
	
	//get the leaf which contains record
	//or leaf which is designated for the record
	public LinkedList<Node<R>> getTraversalToLeaf(Object key) throws IOException, Exception{
		LinkedList<Node<R>> res = new LinkedList<>();
		mapping.clear(); // vymaze sa mapovanie
		mapping.put(root, metadata.getPositionOfRoot());
		Node<R> n=root;
		int index;
		if(n==null) return res; 
		while(n.getType()=='I') {
			res.add(n);
			index=((InternalNode<R>)n).getPointer(key);
			n=diskManager.readNode(index);
			mapping.put(n, index);
		}		
		res.add(n);
		return res;
	}
	
	public LinkedList<Node<R>> levelOrder() throws IOException, Exception{
		LinkedList<Node<R>> res = new LinkedList<>();
		LinkedList<Node<R>> front = new LinkedList<>();
		res.add(root);
		Node<R> node =root;
		while(node.type=='I') {
			for (int i = 0; i <= node.size; i++) {
				res.addLast(diskManager.readNode(((InternalNode<R>)node).pointers[i]));
				front.addLast(diskManager.readNode(((InternalNode<R>)node).pointers[i]));
			}
			node=front.removeFirst();
		}
		return res;
	}
	
	public LinkedList<R> inOrder() throws IOException, Exception{
		LinkedList<R> inOrder = new LinkedList<>();
		LeafNode<R> leaf = minNode();
		while(leaf.getPositionRight()!=-1) {
			//System.out.println("----------");
		//	System.out.println(leaf);
			//System.out.println("----------");
			for (int i = 0; i < leaf.size; i++) {
				inOrder.add(leaf.getRecord(i));
			}
			//System.out.println("position right: "+leaf.getPositionRight());
			leaf=(LeafNode<R>)diskManager.readNode(leaf.getPositionRight());
		}		
		for (int i = 0; i < leaf.size; i++) inOrder.add(leaf.getRecord(i));
		
		return inOrder;
	}
	
	/*
	 *if new record added and is the first element in the leaf then is needed to alter 
	 *the key of old first element in upper internal nodes to the value of key of the new record
	 * it will find that node in traverse and upate it
	 */
	public InternalNode<R> getInternalToUpdateAfterDelete(LinkedList<Node<R>> travers,Object oldFirstRecordKey, Object newFirstRecordKey){
		
		for (Node<R> node : travers) {
			if(node.getType()=='I') { //is internal
				//System.out.println(node);
				if(((InternalNode<R>)node).containsKey(oldFirstRecordKey)) {
					//System.out.println("Contains : last record key "+oldFirstRecordKey+" ,new record key "+newFirstRecordKey);
					((InternalNode<R>)node).replaceKey(oldFirstRecordKey, newFirstRecordKey);
					return (InternalNode<R>)node;
				}
			}
		}
		return null;
	}
	
	public void delete(Object key) throws IOException, Exception {
		LinkedList<Node<R>> path = getTraversalToLeaf(key);
		LinkedList<Node<R>> path2 = new LinkedList<Node<R>>();
		/*for (Node<R> node : path) {
			path2.addFirst(node);
		}*/
		LeafNode<R> leaf = (LeafNode<R>)path.removeLast();
		Object oldFirstKey = leaf.getRecord(0).getKey();
		if(leaf.getRecord(key)==null) return;                                // zadany kluc neexistuje
		metadata.setNumberOfRecords(metadata.getNumberOfRecords()-1);
		InternalNode<R> parent = (InternalNode<R>)path.getLast();
		//path.addLast(leaf);
		leaf.removeFromNode(key);
		//InternalNode<R> internalToAlterKeyIn = getInternalToUpdateAfterDelete(path, key, oldFirstKey); // v tomto node sa este na konci musi upravit kluc na novy
		
		int posRight=getPositionOfRightSibling(leaf, parent);
		int posLeft=getPositionOfLeftSibling(leaf, parent);
		LeafNode<R> right = (LeafNode<R>)getRightSibling(leaf, parent);
		LeafNode<R> left = (LeafNode<R>)getLeftSibling(leaf, parent);
		if(right!=null) mapping.put(right, posRight);
		if(left!=null) mapping.put(left, posLeft);
		
		//case 1 ->no underflow
		if(!leaf.isUnderflow()) {
			diskManager.writeNode(mapping.get(leaf), leaf);
			
		}
		//case 2 ->(Only for leaf) something to borrow from sibling which has the same parent
		else if(leaf.borrowFromSibling(right, left, parent, oldFirstKey)){
			if(left!= null) diskManager.writeNode(mapping.get(left), left);
			if(right!= null) diskManager.writeNode(mapping.get(right), right);
			diskManager.writeNode(mapping.get(leaf), leaf);
			diskManager.writeNode(mapping.get(parent), parent);
		}
		//case 3 -> musi sa mergovat
		else {
			mergeLeaves(leaf, right, left, parent, oldFirstKey);
			//return;
		}

		
		InternalNode<R> rightSibling,leftSibling;	
		InternalNode<R> node=(InternalNode<R>)path.removeLast();
		parent=(InternalNode<R>)path.removeLast();
		
		//System.out.println(node+"\n\n\n"+parent);
		boolean canBorrow=false;
		while(node.isUnderflow() && node!=root) {
			//todo merge,borrow
			posRight=getPositionOfRightSibling(node, parent);
			posLeft=getPositionOfLeftSibling(node, parent);
			rightSibling = (InternalNode<R>)getRightSibling(node, parent);
			leftSibling = (InternalNode<R>)getLeftSibling(node, parent);			
			if(right!=null) mapping.put(right, posRight);
			if(left!=null) mapping.put(left, posLeft);
			canBorrow=node.borrowFromSibling(rightSibling, leftSibling, parent, key);
			if(canBorrow) {
				diskManager.writeNode(mapping.get(node), node);
				if(rightSibling!=null) diskManager.writeNode(mapping.get(rightSibling), rightSibling);
				if(leftSibling!=null) diskManager.writeNode(mapping.get(leftSibling), leftSibling);
			}
			else {//mergovalo sa
				mergeInternals(node, rightSibling, leftSibling, parent, key);
			}
					
			node=parent;
			if(!path.isEmpty()) {
				parent=(InternalNode<R>)path.removeLast();				//ak je prazdny prehlasit za empty node inak le ulozit
			}
			else break;
		}
		diskManager.writeNode(mapping.get(node), node);
				
		
		/*if(internalToAlterKeyIn!=null) {
			System.out.println("not null internal to replace key in");
			internalToAlterKeyIn.replaceKey(key, leaf.getRecord(0).getKey());
			diskManager.writeNode(mapping.get(internalToAlterKeyIn), internalToAlterKeyIn);
		}*/
		
		diskManager.writeMetadata(metadata);
		
			
		
	}
	/*
	 * gets the left sibling of a node which has the same parent
	 */
	public Node<R> getLeftSibling(Node<R> node, InternalNode<R> parent) throws IOException, Exception{
		int leftSiblingPosition;
		
		if(node.type=='L') {
			leftSiblingPosition=getPositionOfLeftSibling(node, parent);
			if(leftSiblingPosition>=0) {
				LeafNode<R> leftSibling=(LeafNode<R>)diskManager.readNode(leftSiblingPosition);
				mapping.put(leftSibling, leftSiblingPosition);
				return leftSibling;
			}
		}else {
			leftSiblingPosition=getPositionOfLeftSibling(node, parent);
			if(leftSiblingPosition>=0) {
				InternalNode<R> leftSibling=(InternalNode<R>)diskManager.readNode(leftSiblingPosition);
				mapping.put(leftSibling, leftSiblingPosition);
				return leftSibling;
			}
		}
		return null;
	}
	
	public Node<R> getRightSibling(Node<R> node, InternalNode<R> parent) throws IOException, Exception{
		int rightSiblingPosition;
		
		if(node.type=='L') {
			rightSiblingPosition=getPositionOfRightSibling(node, parent);
			if(rightSiblingPosition>=0) {
				LeafNode<R> rightSibling=(LeafNode<R>)diskManager.readNode(rightSiblingPosition);
				mapping.put(rightSibling, rightSiblingPosition);
				return rightSibling;
			}
		}else {
			rightSiblingPosition=getPositionOfRightSibling(node, parent);
			if(rightSiblingPosition>=0) {
				InternalNode<R> rightSibling=(InternalNode<R>)diskManager.readNode(rightSiblingPosition);
				mapping.put(rightSibling, rightSiblingPosition);
				return rightSibling;
			}
		}
		return null;
	}
	
	public int getPositionOfLeftSibling(Node<R> node, InternalNode<R> parent) throws IOException, Exception{
		int leftSiblingPosition;
		Object phantomKey;
		
		if(node.type=='L') {
			leftSiblingPosition=parent.getLeftSibling(((R)((LeafNode<R>)node).getRecords()[0]).getKey());
			return leftSiblingPosition;
		}else {
			if(node.getSize()==0) {
				phantomKey=((InternalNode<R>)node).getPhantomKey();
				leftSiblingPosition=parent.getLeftSibling(phantomKey);
			}
			else leftSiblingPosition=parent.getLeftSibling(((InternalNode<R>)node).getKeys()[0]);
			return leftSiblingPosition;
		}
	}
	
	public int getPositionOfRightSibling(Node<R> node, InternalNode<R> parent) throws IOException, Exception{
		if(node==null) return -1;
		int rightSiblingPosition;
		Object phantomKey;
		if(node.type=='L') {
			rightSiblingPosition=parent.getRightSibling(((R)((LeafNode<R>)node).getRecords()[0]).getKey());
			return rightSiblingPosition;
		}else {
			if(node.getSize()==0) {
				phantomKey=((InternalNode<R>)node).getPhantomKey();
				rightSiblingPosition=parent.getRightSibling(phantomKey);
			}
			else rightSiblingPosition=parent.getRightSibling(((InternalNode<R>)node).getKeys()[0]);
			
			return rightSiblingPosition;
		}
	}
	
	//*******************MERGE OPERATIONS 
	
	public void mergeLeaves(LeafNode<R> node, LeafNode<R> right, LeafNode<R> left, InternalNode<R> parent, Object keyToRemove) throws IOException, Exception { //NEZABUDNUT NA RETAZENIE
		int posOfNewEmpty,addressOfNextSibling=-1 ;
		LeafNode<R> sibling=null;
		
		
		
		if(left==null) { //if this is the leftmost child of its parent
			addressOfNextSibling = getPositionOfRightSibling(right, parent);
			if(addressOfNextSibling>=0) {
				sibling=(LeafNode<R>)diskManager.readNode(addressOfNextSibling);
				sibling.setPositionLeft(mapping.get(node));
				diskManager.writeNode(addressOfNextSibling, sibling);
			}
			
			node.setPositionRight(addressOfNextSibling);
			right.mergeWithLeftSibling(node, parent, right.getRecord(0).getKey());  //right tymto zanika
			posOfNewEmpty=mapping.get(right);
			
			diskManager.writeNode(mapping.get(node), node);
			diskManager.writeNode(mapping.get(right), right);
			
			
			
		}
		else { //not the leftmost child , it means can merge with left sibling
			if(right==null) addressOfNextSibling=-1;
			else addressOfNextSibling = mapping.get(right);
			if(addressOfNextSibling>=0) {
				sibling=right;
				sibling.setPositionLeft(mapping.get(left));
				diskManager.writeNode(addressOfNextSibling, sibling);
			}
			
			left.setPositionRight(addressOfNextSibling);
			node.mergeWithLeftSibling(left, parent, keyToRemove);
			posOfNewEmpty=mapping.get(node);
			
			diskManager.writeNode(mapping.get(left), left);
		}
		
		//znizit pocet blokov ---> empty block management
		//metadata.setNumberOfBlocks(metadata.getNumberOfBlocks()-1);
		//diskManager.writeMetadata(metadata);
		
		diskManager.writeNode(mapping.get(parent), parent);
		
		
		EmptyBlock emptyBlock = new EmptyBlock(-1);
		diskManager.writeEmptyBlock(posOfNewEmpty, emptyBlock);
	}
	
	public void mergeInternals(InternalNode<R> node, InternalNode<R> right, InternalNode<R> left, InternalNode<R> parent, Object keyToRemove) throws Exception {
		EmptyBlock emptyBlock = new EmptyBlock(-1);
		
		if(left==null) {
			right.mergeWithLeftSibling(node, parent, keyToRemove);
			diskManager.writeNode(mapping.get(node), node);
			diskManager.writeEmptyBlock(mapping.get(right), emptyBlock);
		}
		else {
			node.mergeWithLeftSibling(left, parent, keyToRemove);
			diskManager.writeNode(mapping.get(left), left);
			diskManager.writeEmptyBlock(mapping.get(node), emptyBlock);
		}
	}
	
	
	
	
	
}
