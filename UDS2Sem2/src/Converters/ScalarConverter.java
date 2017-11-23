package Converters;

import java.nio.ByteBuffer;
import java.util.Date;

public class ScalarConverter {
	public static byte[] toByteArr(Date date) {
		long l = date.getTime();
		return toByteArr(l);
	}
	
	public static Date toDate(byte[] arr) {
		Date d = new Date(toLong(arr));
		return d;
	}
	
	public static String toString(byte[] arr) {
		return new String(arr).trim();
	}
	
	public static byte[] toByteArr(String s) {
		return s.getBytes();
	}
	
	/*public static String toString(byte[] arr, int size) {
		return new String(arr);//.trim();
	}*/
	
	public static byte[] toByteArr(String s, int size) {
		byte[] res=new byte[size];
		byte[] res1=s.getBytes();
		for (int i = 0; i < size; i++) {
			if(i<res1.length) res[i]=res1[i];
			else res[i]='\0';
		}
		return res;
	}
	
	public static byte[] toByteArr(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}

	public static long toLong(byte[] arr) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.put(arr);
	    buffer.flip();//need flip 
	    return buffer.getLong();
	}
	
	public static byte[] toByteArr(int x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
	    buffer.putInt(x);
	    return buffer.array();
	}

	public static int toInt(byte[] arr) {
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
	    buffer.put(arr);
	    buffer.flip();//need flip 
	    return buffer.getInt();
	}
}
