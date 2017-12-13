package Tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import BPlusTree.BPlusTree;
import BPlusTree.Block;
import BPlusTree.EmptyBlock;
import BPlusTree.InternalNode;
import BPlusTree.LeafNode;
import BPlusTree.Node;
import Classes.Numb;
import Comparators.Comparators;
import Converters.NumbConverter;

public class BPlusTreeInsertTest {
	Comparator<Object> comparator = Comparators.numbComparator;
	NumbConverter numbConverter  = new NumbConverter(comparator);
	private BPlusTree<Numb> tree ;
	private static BPlusTree<Numb> tree2 ;
	
	

	//@Test
	public void mainTest() throws Exception {
		tree= new BPlusTree<Numb>(Comparators.numbComparator, numbConverter, 1000, 4, 14);
		Random rnd = new Random(10);
		
		int numberOfInsertedRecords=1000;
		ArrayList<Numb> nums = new ArrayList<>(numberOfInsertedRecords);
		
		for (int i = 0; i < 1000; i++) {
			nums.add(new Numb(i));
		}
		Collections.shuffle(nums, rnd);
			
		for (int x = 0; x < 1; x++) { //zmena shufflovania
			rnd=new Random(x);
			Collections.shuffle(nums,rnd);
			tree=new BPlusTree<Numb>(Comparators.numbComparator, numbConverter, 1000, 4, 14);
			for (int i = 0; i <1000; i++) {
				Numb n = nums.get(i);
				tree.add(n);
				//printInorder();
				testInorder();
				testGet(nums, i);
				testSize(i+1);
				testDelete(n);
				
			}
			System.out.println(tree.get(99));
			System.out.println(tree.getMetadata().getNumberOfBlocks());
		}
		
		
	}
	
	/*
	 * testuje ci je inorder v spravnom poradi
	 */
	public void testInorder() throws IOException, Exception {
		LinkedList<Numb> inOrder = tree.inOrder();
		Assert.assertTrue(inOrder.size()==tree.getMetadata().getNumberOfRecords());
		Numb n = inOrder.removeFirst();
		for (Numb numb : inOrder) {
			Assert.assertTrue(comparator.compare(n.getKey(), numb.getKey())<0);		
			n=numb;
		}
	}
	
	/*
	 * testuje ci btree vracia tie iste hodnoty ako su hodnoty vo vygenerovanom poli
	 */
	public void testGet(ArrayList<Numb> list, int indexLimit) throws IOException, Exception {
		for (int i = 0; i < indexLimit; i++) {
			Numb n = list.get(i);
			Numb r=tree.get(n.getKey());
			//System.out.println(n.toString()+";"+r.toString());
			Assert.assertTrue(n.toString().equals(r.toString()));
		}
	}
	/*
	 * testuje ci sa pocet zaznamov v strome zhoduje s poctom zaznamov vlozenych do stromu
	 */
	public void testSize(int n) {
		Assert.assertTrue(n==tree.getMetadata().getNumberOfRecords());
	}
	
	
	public void printInorder() throws IOException, Exception {
		for (Numb n : tree.inOrder()) {
			System.out.print(n+"; ");
		}
		System.out.println();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//delete
	
	public void testDelete(Numb n) throws IOException, Exception {
		Assert.assertTrue(tree.get(n.getKey())!=null);
		tree.delete(n.getKey());
		Assert.assertTrue(tree.get(n.getKey())==null);
		tree.add(n);
	}
	
	
	@Test
	public void testRandomInsertDelete() throws Exception {
		tree= new BPlusTree<Numb>(3,3,Comparators.numbComparator, numbConverter, 1000, 4, 14);
		Random rnd = new Random(10);
		
		ArrayList<Numb> nums = new ArrayList<>();
		int num;
		for (int i = 0; i < 13; i++) {
			num=rnd.nextInt(1000);
			for (int j = 0; j < num; j++) {
				Numb n = new Numb(rnd.nextInt(10000));
				nums.add(n);
				tree.add(n);
				System.out.println(n+" "+i+" "+j);
				testInorder();
			}
			System.out.println(i);
			testInorder();
			
			
			num=rnd.nextInt(nums.size());
			for (int j = 0; j < num; j++) {
				Numb n = nums.remove(rnd.nextInt(nums.size()-1));
				System.out.println(n);
				if((int)n.getKey()==43) {
					System.out.println();
				}
				tree.delete(n.getKey());
			}
			
			testInorder();
		}
	}
	
	
	
	public static void main(String args[]) throws Exception {
		tree2= new BPlusTree<Numb>(3,3,Comparators.numbComparator, new NumbConverter(Comparators.numbComparator), 1000, 4, 14);
		Random rnd = new Random(10);
		
		ArrayList<Numb> nums = new ArrayList<>();
		int num;
		for (int i = 0; i < 100; i++) {
			System.out.println(i+" start");
			num=rnd.nextInt(100);
			for (int j = 0; j < num; j++) {
				//System.out.println(i);
				Numb n = new Numb(rnd.nextInt(10000));
				nums.add(n);
				tree2.add(n);
			}
			//testInorder();
			
			boolean b=false;
			num=rnd.nextInt(nums.size());
			for (int j = 0; j < num; j++) {
				Numb n = nums.remove(rnd.nextInt(nums.size()-1));
				System.out.println(n+" "+i+" "+j);
				
				if(tree2.getDiskManager().getEmptyBlocks().size()>0) 
					if(tree2.getDiskManager().getEmptyBlocks().get(tree2.getDiskManager().getEmptyBlocks().size()-1).getPositionOfNextFreeBlock()!=-1) {
					System.out.println("-------------------------"+n+" "+i);
				}
				
				if(checkSonsForEmptyBlock()) {
					System.out.println("********************"+n+" "+i);
					return;
				}
				
				if(checkSonsFirstKeys()) {
					System.out.println("///////////////////////"+n+" "+i+" "+j);
					return;
				}
				
				if(freeBocksTest()) {
					System.out.println("=========================="+n+" "+i+" "+j);
					return;
				}
				if(i==0 && j==3) {
					System.out.println(n);
					//return;
				}
				if(i==2 && j==0) {
					System.out.println("tu");
				}
				tree2.delete(n.getKey());
				if(checkEmptyPos()) {
					System.out.println(tree2.getMetadata().getPositionOfFirstFreeBlock()+" "+tree2.getDiskManager().getEmptyBlocks().size());
					return;
				}
			}
			
			//testInorder();
		}
	}
	
	
	public static boolean checkEmptyPos() {
		if(tree2.getMetadata().getPositionOfFirstFreeBlock()<0 && tree2.getDiskManager().getEmptyBlocks().size()!=0) {
			return true;
		}
		return false;
	}
	
	public static boolean checkSonsForEmptyBlock() throws IOException, Exception {
		Node<Numb> node = tree2.getDiskManager().readNode(tree2.getMetadata().getPositionOfRoot());
		if(node.getType()=='I') {
			for (Integer i : ((InternalNode<Numb>)node).pointers) {
				Block<Numb> b = tree2.getDiskManager().readBlock(i);
				if(b.getType()=='E') {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean checkSonsFirstKeys() throws IOException, Exception {
		Node<Numb> node = tree2.getDiskManager().readNode(tree2.getMetadata().getPositionOfRoot());
		if(node.getType()=='I') {
			for (int i=1;i<node.getSize()+1;i++) {
				Node<Numb> son = tree2.getDiskManager().readNode(((InternalNode<Numb>)node).getPointers()[i]);
				if(son.getType()=='L') {
					
					if(node.getComp().compare(((InternalNode<Numb>)node).getKeys()[i-1],((Numb)((LeafNode<Numb>)son).getRecords()[0]).getKey())!=0)
					 return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean freeBocksTest() {
		ArrayList<EmptyBlock> blocks = tree2.getDiskManager().getEmptyBlocks();
		if(blocks.size()==0) return false;
		for (int i = 1; i < blocks.size(); i++) {
			if(blocks.get(i).getDiskPosition()!=blocks.get(i-1).getPositionOfNextFreeBlock()) return true;
			if(blocks.get(i).getDiskPosition()<1 && i!=blocks.size()-1) return true;
		}
		if(blocks.get(blocks.size()-1).getPositionOfNextFreeBlock()!=-1) return true;
		return false;
	}
	
	
	
}
