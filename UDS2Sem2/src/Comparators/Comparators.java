package Comparators;

import java.util.Comparator;

import Classes.Numb;

public class Comparators {
	public static Comparator<Object> numbComparator= new Comparator<Object>() {
		
		@Override
		public int compare(Object o1, Object  o2) {
			return (int)o1-(int)o2;
		}
	};
}
