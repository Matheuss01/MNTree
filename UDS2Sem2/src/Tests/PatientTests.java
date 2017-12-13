package Tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import BPlusTree.BPlusTree;
import BPlusTree.Block;
import BPlusTree.InternalNode;
import BPlusTree.LeafNode;
import BPlusTree.Node;
import Classes.Patient;
import Comparators.Comparators;
import Converters.Constants;
import Converters.PatientConverter;

public class PatientTests {
	
	BPlusTree<Patient> tree ;
	
	void mainTest() {
		
	}
	
	@Test
	public void testRandomInsertDelete() throws Exception {
		tree= new BPlusTree<Patient>(Comparators.patientComparator, new PatientConverter(Comparators.patientComparator), 6000*3, Integer.BYTES, Constants.MAXSIZE_PATIENT);
		
		
		ArrayList<Patient> patients = new ArrayList<>();
		for (int x = 0; x < 100; x++) {   //rozne nasady generatora
			Random rnd = new Random(x);
			int num;
			for (int i = 0; i < 10; i++) {
				System.out.println("start"+i);
				num=rnd.nextInt(30);
				for (int j = 0; j < num; j++) {
					Patient p = new Patient(rnd.nextInt(10000),new Date(rnd.nextLong()),rnd.nextInt()+"",rnd.nextInt()+"");
					int numHosps = rnd.nextInt(100);
					for (int k = 0; k < numHosps-1; k++) {
						p.startHospitalization(new Date(rnd.nextLong()), "diagnosis"+rnd.nextInt());
						p.finishHospitalization(new Date(rnd.nextLong()));
					}
					if(rnd.nextBoolean()) p.startHospitalization(new Date(rnd.nextLong()), "diagnosis"+rnd.nextInt());
					if(tree.get(p.getKey())==null) tree.add(p);
					else {
						tree.delete(p.getKey());
						tree.add(p);
					}
					patients.add(p);
					
					/*System.out.println(p);
					System.out.println("------------------------");
					System.out.println(tree.get(p.getKey()));*/
					Assert.assertTrue(Comparators.patientEqualsChecker.compare(p, tree.get(p.getKey()))==0);
					testNodes();
					testInorder();
					testIfLastBlockEmpty();
					numberOfRecordsTest();
				}
				
				if(patients.size()==0)continue;
				num=rnd.nextInt(patients.size());
				for (int j = 0; j < num; j++) {
					Patient p = patients.remove(rnd.nextInt(patients.size()-1));
					tree.delete(p.getKey());
					Assert.assertTrue(tree.get(p.getKey())==null);
					testInorder();
					testNodes();
					testIfLastBlockEmpty();
					numberOfRecordsTest();
				}

			}
		}
		
		
	}
	
	public void testInorder() throws IOException, Exception {
		LinkedList<Patient> inOrder = tree.inOrder();
		Assert.assertTrue(inOrder.size()==tree.getMetadata().getNumberOfRecords());
		Patient p = inOrder.removeFirst();
		for (Patient pat : inOrder) {
			Assert.assertTrue(Comparators.patientComparator.compare(p.getKey(), pat.getKey())<0);		
			p=pat;
		}
	}
	
	/*
	 * prechadza kazdy node od korena ku kazdemu listu
	 * kontrola ci sa prvy kluc listu zhoduje s klucom v otcovi z ktoreho tam smeroval
	 * kontrola ci sa vsetky kluce v internal nodoch nachadzaju ako prve kluce v listoch s vinimkou prveho min listu
	 * kont. poctu blockov z mtadat
	 */
	public void testNodes() throws IOException, Exception {
		LinkedList<Node<Patient>> nodes = new LinkedList<>();
		Node<Patient> node = tree.getDiskManager().readNode(tree.getMetadata().getPositionOfRoot()),nextNode;
		LinkedList<Object> firstKeys = new LinkedList<Object>();
		nodes.add(node);
		Patient minPatient = tree.min();
		int howManyLeavesDontHaveIndexKey=0,howMnyNodes=0; // musi byt 1 nakonci , minimum stromu nema korespondujuci index v strome v inner nodoch
		
		while(!nodes.isEmpty()) {
			howMnyNodes++;
			node=nodes.removeFirst();
			if(node.getType()=='I') {
				for (int i = 0; i < node.getSize()+1; i++) {
					nextNode=tree.getDiskManager().readNode(((InternalNode<Patient>) node).getPointer(i));
					nodes.addLast(nextNode);
					
					if(i!=0 && nextNode.getType()=='L') {
						Assert.assertTrue(Comparators.patientComparator.compare(((LeafNode<Patient>)nextNode).getRecord(0).getKey(), ((InternalNode<Patient>) node).getKeys()[i-1])==0);
					}
				}
				for (int i=0;i<node.getSize();i++) {
					firstKeys.add(((InternalNode<Patient>) node).getKeys()[i]);
				}
				
			}
			else {//if leaf
				Patient firstPatient=(Patient)((LeafNode<Patient>)node).getRecords()[0];
				if(Comparators.patientComparator.compare(minPatient.getKey(), firstPatient.getKey())==0) {
					howManyLeavesDontHaveIndexKey++;
				}
				else {
					Assert.assertTrue(firstKeys.contains(firstPatient.getKey()));
				}
			}
		}
		Assert.assertTrue(howManyLeavesDontHaveIndexKey==1); //min leaf sa tam nenachadza
		Assert.assertTrue(howMnyNodes<tree.getMetadata().getNumberOfBlocks());  //pocet blokov
		
	}
	
	/*
	 * kontrola skratenia suboru, posledny block nieje empty block
	 */
	public void testIfLastBlockEmpty() throws Exception {
		Block<Patient> block = tree.getDiskManager().readBlock(tree.getMetadata().getNumberOfBlocks()-1);
		Assert.assertTrue(block.getType()!='E');
	}
	
	public void numberOfRecordsTest() throws IOException, Exception {
		Assert.assertTrue(tree.getMetadata().getNumberOfRecords()==tree.inOrder().size());
	}
	
}
