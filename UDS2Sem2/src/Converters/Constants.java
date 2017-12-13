package Converters;

public final class Constants {
	//for hospitalization
	public static  final int MAXSIZE_START=Long.BYTES;
	public static final int MAXSIZE_END=Long.BYTES;
	public static  final int MAXSIZE_DIAGNOSIS=40;
	public static final int MAXSIZE_HOSPITALIZATION = MAXSIZE_START+MAXSIZE_END+MAXSIZE_DIAGNOSIS;
	
	//for Patient
	public static final int MAXSIZE_NAME = 25;
	public static final int MAXSIZE_SURNAME = 25;
	public static final int MAXSIZE_ID = Integer.BYTES;
	public static final int MAXSIZE_BIRTHDATE = Long.BYTES;
	
	public static final int MAX_NUM_OF_HOSPITALIZATIONS=100;
	public static final int ACTUAL_NUM_OF_HOSPITALIZATIONS=100;
	
	public static final int MAXSIZE_PATIENT = MAXSIZE_NAME+MAXSIZE_SURNAME+MAXSIZE_ID+MAXSIZE_BIRTHDATE+MAX_NUM_OF_HOSPITALIZATIONS*MAXSIZE_HOSPITALIZATION + ACTUAL_NUM_OF_HOSPITALIZATIONS;
	
	/*public static void main(String[] args) {
		System.out.println(MAXSIZE_PATIENT);
	}*/
	
	//for leaf node
	
	public static final int MAXSIZE_METADATA_LEAF_NODE=Integer.BYTES+Integer.BYTES+Integer.BYTES+Integer.BYTES; //type,size(num of records), right, left
	public static final int MAXSIZE_METADATA_INTERNAL_NODE=Integer.BYTES+Integer.BYTES; //type,size(num of keys)
	
}
