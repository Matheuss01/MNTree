package MNTree;

import java.util.Comparator;
import java.util.LinkedList;

public class MNTree<R extends Record> {
	
	private Node<R> root; 
	private final int ORDER;
	private final Comparator<Object> comp;
	private int size;
	
	public MNTree(int order, Comparator<Object> comparator) {
		this.ORDER=order;
		this.comp=comparator;
		root= new LeafNode<R>(order, comparator);
		size=0;
	}
	
	
	//first it finds leaf which is supposed to contain the key
	//key is searched in the leaf
	//if no such key -> return null
	public R get(Object key) {
		LeafNode<R> leaf=(LeafNode<R>)getLeaf(key).getLast();
		return leaf.getRecord(key);
	}
	
	public R min() {
		return null;
	}
	
	public R max() {
		return null;
	}
	
	//private Leaf<s>
	
	public void add(R record) {
		LinkedList<Node<R>> path = getLeaf(record.getKey());
		//*********adding overroot
		InternalNode<R> overRoot = new InternalNode<R>(ORDER, comp);
		path.addFirst(overRoot);
		LeafNode<R> leaf = (LeafNode<R>)path.removeLast();
		InternalNode<R> parent=(InternalNode<R>)path.getLast();;
		leaf.insertIntoNode(record);
		if(leaf.isOverflow()) {		
			LeafNode<R> newLeaf =leaf.split();
			parent=(InternalNode<R>)path.removeLast();
			parent.insertIntoNode(newLeaf);
			
			//this means overroot was used for as a new root
			if(path.isEmpty()) {
				parent.setFirstPointerToNode(leaf);
				root=parent;
			}
		}
		
		InternalNode<R> node=parent;
		InternalNode<R> newInternal;
		while(node.isOverflow()) {
			newInternal = node.split();
			parent=(InternalNode<R>)path.removeLast();
			parent.insertIntoNode(newInternal);
			
			if(path.isEmpty()) {
				parent.setFirstPointerToNode(node);
				root=parent;
			}else node=parent;
		}
		//if adding successful , then size++
	}
	
	public void delete(Object key) {
		
	}
	
	
	
	//get the leaf which contains record
	//or leaf which is designated for the record
	public LinkedList<Node<R>> getLeaf(Object key){
		LinkedList<Node<R>> res = new LinkedList<>();
		Node<R> n=root;
		if(n==null) return res; 
		while(n.getType()=='I') {
			res.add(n);
			n=((InternalNode<R>)n).getPointer(key);
		}		
		res.add(n);
		return res;
	}
	
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
	
	
	
	
}
