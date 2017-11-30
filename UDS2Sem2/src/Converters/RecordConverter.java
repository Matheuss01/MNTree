package Converters;

import java.util.Comparator;

import Classes.Record;

public abstract class RecordConverter {
	protected Comparator<Object> keyComparator ;
	
	public RecordConverter(Comparator<Object> comparator) {
		this.keyComparator=comparator;
	}
	
	public abstract byte[] recordToByteArr(Record r);
	public abstract Record toRecord(byte[] arr);	
	public abstract byte[] keyToByteArr(Object key);	
	public abstract Object toKey(byte[] arr);
	
	public Comparator<Object> getComparator(){
		return keyComparator;
	}
}


