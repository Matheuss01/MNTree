package Classes;

import MNTree.Record;

public class Numb implements Record{

	private int n;
	
	public Numb(int n) {
		this.n=n;
	}
	
	@Override
	public Object getKey() {
		return n;
	}

	@Override
	public Object getValue() {
		return n;
	}
	
	public String toString() {
		return n+"";
	}

}
