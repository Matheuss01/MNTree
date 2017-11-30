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
	private final int ORDER;
	private final Comparator<Object> comp;
	private int size;
	private DiskManager<R> diskManager;
	private Metadata metadata;
	private HashMap<Node<R>,Integer> mapping = new HashMap<>();
	
	public BPlusTree(int order, Comparator<Object> comparator, RecordConverter recordConverter, int blockSize, int keySize, int recordSize) throws Exception { //bez nacitania stromu zo subotu, strom sa vytvara v pamati
		this.ORDER=order;
		this.comp=comparator;
		root= new LeafNode<R>(order, comparator);
		size=0;
		diskManager = new DiskManager<R>(recordConverter,blockSize,order,keySize,recordSize);
		metadata=new Metadata(blockSize, 1, -1, 0, order, keySize, recordSize,1); //-1-> no empty block , root je 2. block cize position 1
		diskManager.writeMetadata(metadata);
		diskManager.writeNode(metadata.positionOfRoot, root);		
	}
	
	public BPlusTree(int order, Comparator<Object> comparator, File f) { //ked bude zadany subor z ktoreho sa ma strom nacitat
		this.ORDER=order;
		this.comp=comparator;
		//root= new LeafNode<R>(order, comparator); ------root sa nacita zo suboru
		size=0;
	}
	
	
	public Node<R> getRoot() {
		return root;
	}

	public void setRoot(Node<R> root) {
		this.root = root;
	}

	public DiskManager<R> getDiskManager() {
		return diskManager;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public int getSize() {
		return size;
	}
	
	//first it finds leaf which is supposed to contain the key
	//key is searched in the leaf
	//if no such key -> return null
	/*public R get(Object key) {
		LeafNode<R> leaf=(LeafNode<R>)getLeaf(key).getLast();
		return leaf.getRecord(key);
	}*/
	
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
	
	public LinkedList<R> inOrder() throws IOException, Exception{
		LinkedList<R> inOrder = new LinkedList<>();
		LeafNode<R> leaf = minNode();
		while(leaf.getPositionRight()!=-1) {
			for (int i = 0; i < leaf.size; i++) {
				inOrder.add(leaf.getRecord(i));
			}
			leaf=(LeafNode<R>)diskManager.readNode(leaf.getPositionRight());
		}		
		for (int i = 0; i < leaf.size; i++) inOrder.add(leaf.getRecord(i));
		
		return inOrder;
	}

	public void add(R record) throws IOException, Exception {
		int pos;
		LinkedList<Node<R>> path = getLeaf(record.getKey());
		//*********adding overroot
		InternalNode<R> overRoot = new InternalNode<R>(ORDER, comp);
		path.addFirst(overRoot);
		LeafNode<R> leaf = (LeafNode<R>)path.removeLast();
		InternalNode<R> parent=(InternalNode<R>)path.getLast();;
		leaf.insertIntoNode(record);
		if(leaf.isOverflow()) {
			LeafNode<R> newLeaf =leaf.split();
			pos=diskManager.writeNewNode(newLeaf); //adresa resp. pozicia noveho nodu(blocku) v subore
			mapping.put(newLeaf, pos);
			System.out.println(mapping.get(leaf)+"--------");
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
				parent.setFirstPointerToNode(mapping.get(leaf));
				root=parent;
				pos=diskManager.writeNewNode(parent);
				mapping.put(parent,pos);
				metadata.positionOfRoot=mapping.get(parent);
			}else {
				diskManager.writeNode(mapping.get(parent), parent);
			}
		}
		diskManager.writeNode(mapping.get(leaf), leaf); //update leafu aj na disk
		//potiato by to malo fungovat
		
		InternalNode<R> node=parent;
		InternalNode<R> newInternal;
		while(node.isOverflow()) {
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
				metadata.positionOfRoot=mapping.get(parent);
			}
			diskManager.writeNode(mapping.get(newInternal), newInternal);
			diskManager.writeNode(mapping.get(node),node);
			node=parent;
		}
		//if adding successful , then size++
		size++;
	}
	/*
	public void delete(Object key) {
		
	}*/
	
	
	
	//get the leaf which contains record
	//or leaf which is designated for the record
	public LinkedList<Node<R>> getLeaf(Object key) throws IOException, Exception{
		LinkedList<Node<R>> res = new LinkedList<>();
		mapping.clear(); // vymaze sa mapovanie
		mapping.put(root, metadata.positionOfRoot);
		Node<R> n=root;
		int index;
		if(n==null) return res; 
		while(n.getType()=='I') {
			res.add(n);
			//n=((InternalNode<R>)n).getPointer(key);
			index=((InternalNode<R>)n).getPointer(key);
			n=diskManager.readNode(index);
			mapping.put(n, index);
		}		
		res.add(n);
		return res;
	}
	/*
	public LinkedList<Node<R>> levelOrder(){
		LinkedList<Node<R>> res = new LinkedList<>();
		LinkedList<Node<R>> front = new LinkedList<>();
		res.add(root);
		Node<R> node =root;
		while(node.type=='I') {
			for (int i = 0; i <= node.size; i++) {
				res.addLast(((InternalNode<R>)node).pointers[i]);
				front.addLast(((InternalNode<R>)node).pointers[i]);
			}
			node=front.removeFirst();
		}
		return res;
	}
	
	public LinkedList<R> inOrder(){
		LinkedList<R> res = new LinkedList<>();
		LeafNode<R> node =minNode();
		while(node!=null) {
			for (int i = 0; i < node.size; i++) {
				res.addLast((R)node.getRecords()[i]);
			}
			node=node.getRight();
		}
		return res;
	}*/
	
	
}
