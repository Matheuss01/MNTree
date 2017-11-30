package Converters;

import java.util.Comparator;

import Classes.Numb;
import Classes.Record;

public class NumbConverter extends RecordConverter{

	public NumbConverter(Comparator<Object> comparator) {
		super(comparator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] recordToByteArr(Record r) {
		if(r==null) return ScalarConverter.toByteArr("",14);
		
		byte[] numArr = ScalarConverter.toByteArr(((Numb)r).getN());
		byte[] sArr = ScalarConverter.toByteArr(((Numb)r).getS(),10);
		byte[] res = new byte[numArr.length+sArr.length];
		for (int i = 0; i < res.length; i++) {
			if(i<numArr.length) res[i]=numArr[i];
			else res[i]=sArr[i-numArr.length];
		}
		return res;
	}

	@Override
	public Record toRecord(byte[] arr) {
		byte[] numArr = new byte[4];
		byte[] sArr = new byte[10];
		for (int i = 0; i < arr.length; i++) {
			if(i<numArr.length) numArr[i]=arr[i];
			else sArr[i-numArr.length]=arr[i];
		}
		return new Numb(ScalarConverter.toInt(numArr),ScalarConverter.toString(sArr));
	}

	@Override
	public byte[] keyToByteArr(Object key) {
		if(key==null) return ScalarConverter.toByteArr((int)0);
		return ScalarConverter.toByteArr((int)key);
	}

	@Override
	public Object toKey(byte[] arr) {
		return ScalarConverter.toInt(arr);
	}

}
