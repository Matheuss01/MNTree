package Tests;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import BPlusTree.Metadata;
import BPlusTreeGUI.AllBlocksFrameController;
import Classes.Numb;
import Comparators.Comparators;
import MNTree.InternalNode;
import MNTree.LeafNode;
import MNTree.MNTree;
import MNTree.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class MNTreeTest {

	int size=5;
	private MNTree<Numb> mntree = new MNTree<Numb>(size, Comparators.numbComparator);
	
	@Test
	public void test() {
		//fail("Not yet implemented");
	}
	
	@Test
	public void insertTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Random rnd = new Random(5);
		for (int i = 0; i < 10; i++) {
			mntree.add(new Numb(rnd.nextInt(),"wefer"));
			LinkedList<Node<Numb>> nodes = mntree.levelOrder();
			//System.out.println(nodes.size());
			//System.out.println("Size: "+mntree.getSize()+"inorder: "+String.valueOf(mntree.inOrder()));
			for (Node<Numb> node : nodes) {
				Assert.assertTrue(correctOrder(node));
				Assert.assertTrue(correctSize(node));
				Assert.assertFalse(isOverFlow(node));
				Assert.assertFalse(isUnderFlow(node));
				Assert.assertTrue(inorderTest());			
			}
		}
	}
	
	public boolean inorderTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = mntree.getClass().getDeclaredField("comp");
		f.setAccessible(true);
		Comparator<Object> comp = (Comparator<Object>)f.get(mntree);
		
		LinkedList<Numb> inorder = mntree.inOrder();
		Numb i=inorder.removeFirst();
		for (Numb n : inorder) {
			if(comp.compare(i.getKey(), n.getKey())>=0) return false;
			//System.out.print(i.getKey()+" < "+ n.getKey()+"---");
			i=n;
		}
		return true;
	}

	
	//leaf has number of not null elements equal the size
	public boolean correctSize(Node<Numb> node) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		int counter =0;
		if(node.getType()=='I') {
			Field f = ((InternalNode<Numb>)node).getClass().getDeclaredField("keys");
			f.setAccessible(true);
			Object[] keys = (Object[])f.get(node);
			
			f=((InternalNode<Numb>)node).getClass().getDeclaredField("pointers");
			f.setAccessible(true);
			Node<Numb>[] pointers = (Node<Numb>[])f.get(node);
						
			for (int i = 0; i <keys.length ; i++) { 
				if(keys[i]!=null) counter++;
			}
			if(counter!=node.getSize()) return false;
			
			counter=0;
			for (int i = 0; i <pointers.length ; i++) {
				if(pointers[i]!=null) counter++;
			}
			if(counter!=node.getSize()+1) return false;
			
		}else {
			Field f = ((LeafNode<Numb>)node).getClass().getDeclaredField("records");
			f.setAccessible(true);
			Object[] records = (Object[])f.get(node);
			counter=0;
			for (int i = 0; i <records.length ; i++) {
				if(records[i]!=null) counter++;
			}
			if(counter!=node.getSize()) return false;
		}
		return true;
	}
	
	public boolean correctOrder(Node<Numb> node) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = mntree.getClass().getDeclaredField("comp");
		f.setAccessible(true);
		Comparator<Object> comp = (Comparator<Object>)f.get(mntree);
		
		if(node.getType()=='I') {
			f = ((InternalNode<Numb>)node).getClass().getDeclaredField("keys");
			f.setAccessible(true);
			Object[] keys = (Object[])f.get(node);
			
			for (int i = 0; i <node.getSize()-1 ; i++) { 
				if(comp.compare(keys[i], keys[i+1])>=0) return false;
			}
			
		}else {
			f = ((LeafNode<Numb>)node).getClass().getDeclaredField("records");
			f.setAccessible(true);
			Object[] records = (Object[])f.get(node);
			for (int i = 0; i <node.getSize()-1 ; i++) {
				if(comp.compare(((Numb)records[i]).getKey(),((Numb)records[i+1]).getKey())>=0) return false;
			}
		}
		return true;
	}
	
	public boolean isUnderFlow(Node<Numb> node) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = mntree.getClass().getDeclaredField("root");
		f.setAccessible(true);
		Node<Numb>  root = (Node<Numb>)f.get(mntree);
		if(node==root) return false;
		return node.isUnderflow();
	}
	
	public boolean isOverFlow(Node<Numb> node) {
		return node.isOverflow();
	}
	
	/*public boolean correctChildren(Node<Numb> node) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		int counter =0;
		if(node.getType()=='I') {
			
			Field f=((InternalNode<Numb>)node).getClass().getDeclaredField("pointers");
			Node<Numb>[] pointers = (Node<Numb>[])f.get(node);
						
			for (int i = 0; i <node.getSize() ; i++) {
				if(pointers[i]!=null) {
					f=((InternaNode<Numb>)node).getClass().getDeclaredField("pointers");
					Node<Numb>[] pointers = (Node<Numb>[])f.get(node);
				}
			}
			if(counter!=node.getSize()+1) return false;
			
		}
		return true;
	}*/
	
	

	

}
