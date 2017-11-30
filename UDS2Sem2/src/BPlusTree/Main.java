package BPlusTree;

import java.io.IOException;
import java.util.Scanner;

import Classes.Numb;
import Comparators.Comparators;
import Converters.Constants;
import Converters.NumbConverter;
import Converters.PatientConverter;
import Converters.RecordConverter;
import BPlusTree.Node;

public class Main {
	public static void main(String[] args) throws Exception {
		Scanner S = new Scanner(System.in);
		//NumbConverter patConv = new NumbConverter(Comparators.numbComparator);
		//BPlusTree<Numb> mntree = new BPlusTree<Numb>(3, Comparators.numbComparator,patConv,100,Integer.BYTES,10);
		//mntree.getDiskManager().writeMetadata(mntree.getMetadata());
		//Metadata m = mntree.getDiskManager().readMetadata();
		//System.out.println(m.blockSize+","+m.recordSize+","+m.keySize+","+m.numberOfRecords+","+m.order+","+m.positionOfFirstFreeBlock+","+m.positionOfRoot+",");
		
		/*Numb n1 = new Numb(5,"Five");
		Numb n2 = new Numb(1,"One");
		Numb n3 = new Numb(3,"Three");*/
		
		/*mntree.add(n1);
		//System.out.println(mntree.getRoot());
		mntree.getDiskManager().writeNode(0,mntree.getRoot());
		LeafNode<Numb> root = (LeafNode<Numb>)mntree.getDiskManager().readNode(0);
		//System.out.println(root.getSize());
		//System.out.println(root.getRecords()[0]);
		System.out.println(root);
		
		mntree.add(n2);
		mntree.getDiskManager().writeNode(0,mntree.getRoot());
		
		//System.out.println(mntree.getRoot());
		System.out.println("normal root:"+mntree.getRoot());
		 root = (LeafNode<Numb>)mntree.getDiskManager().readNode(0);
		 System.out.println(root);
		mntree.add(n3);
		mntree.getDiskManager().writeNode(0,mntree.getRoot());
		root = (LeafNode<Numb>)mntree.getDiskManager().readNode(0);
		System.out.println(root   );*/
		

		//***************************************************************************************
		
		NumbConverter patConv = new NumbConverter(Comparators.numbComparator);
		BPlusTree<Numb> mntree = new BPlusTree<Numb>(3, Comparators.numbComparator,patConv,100,Integer.BYTES,14);
		
		Numb n1 = new Numb(5,"Five");
		Numb n2 = new Numb(1,"One");
		Numb n3 = new Numb(3,"Three");
		Numb n4 = new Numb(4,"Four");
		Numb n5=  new Numb(6,"Six");
		Numb n6=  new Numb(2,"Two");
		Numb n7=  new Numb(7,"Seven");
		Numb n8 = new Numb(8,"Eight");
		Numb n9 = new Numb(9,"Nine");
		Numb n10=  new Numb(10,"Ten");
		Numb n11=  new Numb(11,"Eleven");
		Numb n12=  new Numb(12,"Twelve");
		
		Node<Numb> root = mntree.getRoot();
		System.out.println(root);
		root=mntree.getDiskManager().readNode(1);
		System.out.println("--------------------");
		System.out.println(root);
		
		mntree.add(n1);
		System.out.println("(RAMroot) "+mntree.getRoot());
		root=mntree.getDiskManager().readNode(1);
		System.out.println("position of root on disk: "+mntree.getMetadata().positionOfRoot);
		System.out.println("(rootFromDisk) "+root);
		
		System.out.println("ADDING n2");
		mntree.add(n2);
		System.out.println("(RAMroot) "+mntree.getRoot());
		root=mntree.getDiskManager().readNode(1);
		System.out.println("(rootFromDisk) "+root);
		
		System.out.println("ADDING n3");
		mntree.add(n3);
		System.out.println("(RAMroot) "+mntree.getRoot());
		root=mntree.getDiskManager().readNode(1);
		System.out.println("(rootFromDisk) "+root);
		
		System.out.println("ADDING n4");
		mntree.add(n4);
		System.out.println("(RAMroot) "+mntree.getRoot());
		int posOfRoot=mntree.getMetadata().positionOfRoot;
		System.out.println("position of root: "+posOfRoot);
		root=mntree.getDiskManager().readNode(posOfRoot);
		System.out.println("(rootFromDisk) "+root);
		
		mntree.add(n5);
		mntree.add(n6);
		mntree.add(n7);
		mntree.add(n8);
		mntree.add(n9);
		mntree.add(n10);
		mntree.add(n11);
		System.out.println("*********************************************");
		System.out.println("Root from disk");
		root=mntree.getDiskManager().readNode(mntree.getMetadata().positionOfRoot);
		System.out.println(root);
		
		System.out.println("Internal1 from disk");
		// int i=((InternalNode<Numb>)root).getPointer(0);
		System.out.println(mntree.getDiskManager().readNode(((InternalNode<Numb>)root).getPointer(0)));

		System.out.println("Internal2 from disk");
		System.out.println(mntree.getDiskManager().readNode(((InternalNode<Numb>)root).getPointer(1)));
		
		System.out.println("Leaf1 from disk");
		System.out.println(mntree.getDiskManager().readNode(1));
		
		System.out.println("Leaf2 from disk");
		System.out.println(mntree.getDiskManager().readNode(2));
		
		System.out.println("Leaf3 from disk");
		System.out.println(mntree.getDiskManager().readNode(4));
		
		System.out.println("Leaf4 from disk");
		System.out.println(mntree.getDiskManager().readNode(5));
		
		System.out.println("Leaf5 from disk");
		System.out.println(mntree.getDiskManager().readNode(6));
		
		System.out.println("INORDER **************************");
		for (Numb n : mntree.inOrder()) {
			System.out.println(n);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
