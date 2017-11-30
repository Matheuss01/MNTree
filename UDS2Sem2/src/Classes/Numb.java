package Classes;

public class Numb implements Record{

	private int n;
	private String s;
	
	public Numb(int n, String s) {
		this.n=n;
		this.s=s;
	}
	
	@Override
	public Object getKey() {
		return n;
	}

	@Override
	public Object getValue() {
		return this;
	}
	
	public String toString() {
		return n+", "+s;
	}

	public int getN() {
		return n;
	}

	public String getS() {
		return s;
	}

	
	
}
