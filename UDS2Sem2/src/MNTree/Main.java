package MNTree;

import java.util.Scanner;

import Classes.Numb;
import Comparators.Comparators;

public class Main {
	public static void main(String[] args) {
		Scanner S = new Scanner(System.in);
		MNTree<Numb> mntree = new MNTree<Numb>(5, Comparators.numbComparator);
		//LeafNode<Numb> node = new LeafNode(5, Comparators.numbComparator);
		while(S.hasNextInt()) {
			/*Numb n = new Numb(S.nextInt());
			node.insertIntoNode(n);
			System.out.println("Adding----------");
			System.out.println(node);
			Numb m = new Numb(S.nextInt());
			node.removeFromNode(m);
			System.out.println("Removing----------");
			System.out.println(node);*/
			
			Numb n = new Numb(S.nextInt());
			mntree.add(n);
			System.out.println("Adding----------");
			for (Node<Numb> node: mntree.levelOrder()) {
				System.out.println(node);
				System.out.println("----------------------");
			}
			
			
			
		}
	}
}
