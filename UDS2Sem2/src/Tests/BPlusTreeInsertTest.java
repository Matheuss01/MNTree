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
import Classes.Numb;
import Comparators.Comparators;
import Converters.NumbConverter;

public class BPlusTreeInsertTest {
	Comparator<Object> comparator = Comparators.numbComparator;
	NumbConverter numbConverter  = new NumbConverter(comparator);
	private BPlusTree<Numb> tree ;
	
	

	@Test
	public void mainTest() throws Exception {
		tree= new BPlusTree<Numb>(60, Comparators.numbComparator, numbConverter, 1000, 4, 14);
		Random rnd = new Random(10);
		
		int numberOfInsertedRecords=100;
		ArrayList<Numb> nums = new ArrayList<>(numberOfInsertedRecords);
		
		for (int i = 0; i < 100; i++) {
			nums.add(new Numb(i));
		}
		Collections.shuffle(nums, rnd);
			
		for (int x = 0; x < 10; x++) { //zmena shufflovania
			rnd=new Random(x);
			Collections.shuffle(nums,rnd);
			tree=new BPlusTree<Numb>(9, Comparators.numbComparator, numbConverter, 1000, 4, 14);
			for (int i = 0; i <50; i++) {
				Numb n = nums.get(i);
				tree.add(n);
				//printInorder();
				testInorder();
				testGet(nums, i);
				testSize(i+1);
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
	
	public void  testGet(Object key, Numb insertedNumb, Numb releasedNumb) {
		
	}
}
