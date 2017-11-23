package Comparators;

import java.util.Comparator;

import Classes.Numb;

public class Comparators {
	public static Comparator<Object> numbComparator= new Comparator<Object>() {
		
		@Override
		public int compare(Object o1, Object  o2) {
			long res= (long)(int)o1-(long)(int)o2;
			
			if(res>0) return 1;
			else if(res<0) return -1;
			else return  0;
		}
	};
}
