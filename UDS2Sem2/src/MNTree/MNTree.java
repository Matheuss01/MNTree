package MNTree;

import java.util.Comparator;
import java.util.LinkedList;

public class MNTree<R extends Record> {
	
	private Node<R> root; 
	private final int ORDER;
	private final Comparator<Object> comp;
	
	public MNTree(int order, Comparator<Object> comparator) {
		this.ORDER=order;
		this.comp=comparator;
		root= new LeafNode<R>(order, comparator);
	}
	
	public void add(R record) {
		LinkedList<Node<R>> path = getLeaf(record);
		//*********adding overroot
		InternalNode<R> overRoot = new InternalNode<R>(ORDER, comp);
		path.addFirst(overRoot);
		InternalNode<R> parent;
		LeafNode<R> leaf = (LeafNode<R>)path.removeLast();
		leaf.insertIntoNode(record);
		if(leaf.isOverflow()) {
			//System.out.println("Overflow");
			//return;
			
			LeafNode<R> newLeaf =leaf.split();
			parent=(InternalNode<R>)path.removeLast();
			parent.insertIntoNode(newLeaf);
			parent.setFirstPointerToNode(leaf);
			
			if(path.isEmpty()) {
				parent.size=1;
				root=parent;
			}
		}
		
		/*InternalNode<R> node;
		while(node.isOverflow()) {
			
		}*/
	}
	
	
	
	//get the leaf which contains record
	//or leaf which is designated for the record
	public LinkedList<Node<R>> getLeaf(R record){
		LinkedList<Node<R>> res = new LinkedList<>();
		Node<R> n=root;
		if(n==null) return res; 
		while(n.getType()=='I') {
			res.add(n);
			n=((InternalNode<R>)n).getPointer(record);
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
