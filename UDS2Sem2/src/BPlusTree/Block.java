package BPlusTree;

import Classes.Record;
import Converters.RecordConverter;
import Converters.ScalarConverter;
import BPlusTree.LeafNode;

public class Block<R extends Record> {
	private static final Exception typeException = new Exception("block with incorrect type");
	private RecordConverter recConv;
	private Object content;
	private char type;
	private final Metadata metadata;
	
	public Block(RecordConverter rc, Metadata metadata) {
		recConv=rc;
		this.metadata=metadata;
	}

	public char getType() {
		return type;
	}
	public void setContent(Node<R> content) {
		type=content.getType();
		this.content=content;
	}
	
	public void setContent(Metadata content) {
		type='M';
		this.content=content;
	}
	
	public void setContent(EmptyBlock content) {
		type='E';
		this.content=content;
	}
	
	public void setContent(byte[] content) throws Exception {
		int a=0;
		byte[] intTypeArr = new byte[Integer.BYTES];
		for (int i = 0; i < intTypeArr.length; i++) {
			intTypeArr[i]=content[i];
		}
		a+=intTypeArr.length;
		int intType = ScalarConverter.toInt(intTypeArr);
		if(intType==0) type='M';
		else if(intType==1) type='L';
		else if(intType==2) type='I';
		else if(intType==3) type='E';
		else throw typeException;
		
		if(type=='E') { //int 3 for emty block
			byte[] nextFreeArr = new byte[Integer.BYTES];
			for (int i = 0; i < nextFreeArr.length; i++) {
				nextFreeArr[i]=content[a+i];
			}	
			int nextFree = ScalarConverter.toInt(nextFreeArr);
		
			this.content=new EmptyBlock(nextFree);
		}
		else if(type=='M') { //int 0 for metadata
			byte[] blocSizeArr = new byte[Integer.BYTES];
			byte[] rootPosArr = new byte[Integer.BYTES];
			byte[] firstFreeBlockPosArr = new byte[Integer.BYTES];
			byte[] numberOfRecordsArr = new byte[Integer.BYTES];
			byte[] leafOrderArr = new byte[Integer.BYTES];
			byte[] internalOrderArr = new byte[Integer.BYTES];
			byte[] keySizeArr = new byte[Integer.BYTES];
			byte[] recordSizeArr = new byte[Integer.BYTES];
			byte[] numberOfBlocksArr = new byte[Integer.BYTES];
			
			for (int i = 0; i < blocSizeArr.length; i++) {
				blocSizeArr[i]=content[i+a];
			}
			a+=blocSizeArr.length;
			for (int i = 0; i < rootPosArr.length; i++) {
				rootPosArr[i]=content[i+a];
			}
			a+=rootPosArr.length;
			for (int i = 0; i < firstFreeBlockPosArr.length; i++) {
				firstFreeBlockPosArr[i]=content[i+a];
			}
			a+=firstFreeBlockPosArr.length;
			for (int i = 0; i < numberOfRecordsArr.length; i++) {
				numberOfRecordsArr[i]=content[i+a];
			}
			a+=numberOfRecordsArr.length;
			for (int i = 0; i < leafOrderArr.length; i++) {
				leafOrderArr[i]=content[i+a];
			}
			a+=leafOrderArr.length;
			for (int i = 0; i < internalOrderArr.length; i++) {
				internalOrderArr[i]=content[i+a];
			}
			a+=internalOrderArr.length;
			for (int i = 0; i < keySizeArr.length; i++) {
				keySizeArr[i]=content[i+a];
			}
			a+=keySizeArr.length;
			for (int i = 0; i < recordSizeArr.length; i++) {
				recordSizeArr[i]=content[i+a];
			}
			a+=recordSizeArr.length;
			for (int i = 0; i < numberOfBlocksArr.length; i++) {
				numberOfBlocksArr[i]=content[i+a];
			}
			a+=numberOfBlocksArr.length;
			
			int blockSize = ScalarConverter.toInt(blocSizeArr);
			int rootPos = ScalarConverter.toInt(rootPosArr);
			int firstFree = ScalarConverter.toInt(firstFreeBlockPosArr);
			int numOfRecords = ScalarConverter.toInt(numberOfRecordsArr);
			int leafOrder = ScalarConverter.toInt(leafOrderArr);
			int internalOrder = ScalarConverter.toInt(internalOrderArr);
			int keySize = ScalarConverter.toInt(keySizeArr);
			int recordSize = ScalarConverter.toInt(recordSizeArr);
			int numberOfBlocks = ScalarConverter.toInt(numberOfBlocksArr);
			
			this.content= new Metadata(blockSize,rootPos,firstFree, numOfRecords,leafOrder,internalOrder,keySize,recordSize,numberOfBlocks);		
		}
		else if(type=='L') { //1 leaf
			//System.out.println("here");
			byte[] sizeArr = new byte[Integer.BYTES];
			byte[] leftArr = new byte[Integer.BYTES];
			byte[] rightArr = new byte[Integer.BYTES];
			//byte[][] recordsArr = new byte[];
			
			for (int i = 0; i < sizeArr.length; i++) {
				sizeArr[i]=content[i+a];
			}
			a+=sizeArr.length;
			for (int i = 0; i < leftArr.length; i++) {
				leftArr[i]=content[i+a];;
			}
			a+=leftArr.length;
			for (int i = 0; i < rightArr.length; i++) {
				rightArr[i]=content[i+a];
			}
			a+=rightArr.length;
			
			int size=ScalarConverter.toInt(sizeArr);
			int left=ScalarConverter.toInt(leftArr);
			int right=ScalarConverter.toInt(rightArr);
			//System.out.println(size+" "+left+" "+right+" "+metadata.getOrder());
			
			Object[] records = new Object[metadata.getLeafOrder()+1];
			byte[] record=new byte[metadata.getRecordSize()]; //add record size and key size to metadata
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < record.length; j++) {
					record[j]=content[j+a];
				}
				records[i]=(R)recConv.toRecord(record);
				a+=record.length;
			}
			LeafNode<R> leaf = new LeafNode<R>(metadata.getLeafOrder(), recConv.getComparator());
			leaf.setPositionLeft(left);
			leaf.setPositionRight(right);
			leaf.setSize(size);
			leaf.setRecords(records);
			this.content=leaf;		
		}
		else if(type=='I') { //2 internal
			byte[] sizeArr = new byte[Integer.BYTES];
			
			for (int i = 0; i < sizeArr.length; i++) {
				sizeArr[i]=content[i+a];
			}
			a+=sizeArr.length;
			
			int size=ScalarConverter.toInt(sizeArr);
			
			Object[] keys = new Object[metadata.getInternalOrder()+1]; 
			int[] pointers = new int[metadata.getInternalOrder()+1+1];
			byte[] keyArr=new byte[metadata.getKeySize()]; //add record size and key size to metadata
			for (int i = 0; i < metadata.getInternalOrder(); i++) {
				for (int j = 0; j < keyArr.length; j++) {
					keyArr[j]=content[j+a];
				}
				if(i<size) keys[i]=recConv.toKey(keyArr);
				a+=keyArr.length;
			}
			
			byte[] pointerArr=new byte[Integer.BYTES]; //add record size and key size to metadata
			for (int i = 0; i < metadata.getInternalOrder()+1; i++) {
				for (int j = 0; j < pointerArr.length; j++) {
					pointerArr[j]=content[j+a];
				}
				if(i<=size) pointers[i]=ScalarConverter.toInt(pointerArr);
				a+=pointerArr.length;
			}
			InternalNode<R> internal =new InternalNode<>(metadata.getInternalOrder(), recConv.getComparator());
			internal.setSize(size);
			internal.setKeys(keys);
			internal.setPointers(pointers);
			this.content= internal;			
		}
	}
	
	
	public byte[] toByteArray() {
		byte[] arr = new byte[metadata.getBlockSize()];
		if(type=='E') { //int 3 for emty block
			byte[] typeArr = ScalarConverter.toByteArr((int)3);
			byte[] nextArr = ScalarConverter.toByteArr((int)((EmptyBlock)content).getPositionOfNextFreeBlock());
			for (int i = 0; i < typeArr.length; i++) {
				arr[i]=typeArr[i];
			}			
			for (int i = 0; i < nextArr.length; i++) {
				arr[typeArr.length+i]=nextArr[i];
			}
		}
		if(type=='M') { //int 0 for metadata
			int a=0;
			byte[] typeArr = ScalarConverter.toByteArr(0);
			byte[] blocSizeArr = ScalarConverter.toByteArr(((Metadata)content).getBlockSize());
			byte[] rootPosArr = ScalarConverter.toByteArr(((Metadata)content).getPositionOfRoot());
			byte[] firstFreeBlockPosArr = ScalarConverter.toByteArr(((Metadata)content).getPositionOfFirstFreeBlock());
			byte[] numberOfRecordsArr = ScalarConverter.toByteArr(((Metadata)content).getNumberOfRecords());
			byte[] leafOrderArr = ScalarConverter.toByteArr(((Metadata)content).getLeafOrder());
			byte[] internalOrderArr = ScalarConverter.toByteArr(((Metadata)content).getInternalOrder());
			byte[] keySizeArr = ScalarConverter.toByteArr(((Metadata)content).getKeySize());
			byte[] recordSizeArr = ScalarConverter.toByteArr(((Metadata)content).getRecordSize());
			byte[] numberOfBlocksArr = ScalarConverter.toByteArr(((Metadata)content).getNumberOfBlocks());
			
			//System.out.println("arrsize:"+arr.length+", typearrsize:"+typeArr.length);
			for (int i = 0; i < typeArr.length; i++) {
				arr[i+a]=typeArr[i];
			}
			a+=typeArr.length;
			for (int i = 0; i < blocSizeArr.length; i++) {
				arr[i+a]=blocSizeArr[i];
			}
			a+=blocSizeArr.length;
			for (int i = 0; i < rootPosArr.length; i++) {
				arr[i+a]=rootPosArr[i];
			}
			a+=rootPosArr.length;
			for (int i = 0; i < firstFreeBlockPosArr.length; i++) {
				arr[i+a]=firstFreeBlockPosArr[i];
			}
			a+=firstFreeBlockPosArr.length;
			for (int i = 0; i < numberOfRecordsArr.length; i++) {
				arr[i+a]=numberOfRecordsArr[i];
			}
			a+=numberOfRecordsArr.length;
			for (int i = 0; i < leafOrderArr.length; i++) {
				arr[i+a]=leafOrderArr[i];
			}
			a+=leafOrderArr.length;
			for (int i = 0; i < internalOrderArr.length; i++) {
				arr[i+a]=internalOrderArr[i];
			}
			a+=internalOrderArr.length;
			for (int i = 0; i < keySizeArr.length; i++) {
				arr[i+a]=keySizeArr[i];
			}
			a+=keySizeArr.length;
			for (int i = 0; i < recordSizeArr.length; i++) {
				arr[i+a]=recordSizeArr[i];
			}
			a+=recordSizeArr.length;
			for (int i = 0; i < numberOfBlocksArr.length; i++) {
				arr[i+a]=numberOfBlocksArr[i];
			}
			a+=numberOfBlocksArr.length;
		}
		if(type=='L') { //int 1 for leaf
			byte[] typeArr = ScalarConverter.toByteArr(1);
			byte[] sizeArr = ScalarConverter.toByteArr(((LeafNode<R>)content).getSize());
			byte[] leftArr = ScalarConverter.toByteArr(((LeafNode<R>)content).getPositionLeft());
			byte[] rightArr = ScalarConverter.toByteArr(((LeafNode<R>)content).getPositionRight());
			int a=0;
			
			for (int i = 0; i < typeArr.length; i++) {
				arr[i+a]=typeArr[i];
			}
			a+=typeArr.length;
			for (int i = 0; i < sizeArr.length; i++) {
				arr[i+a]=sizeArr[i];
			}
			a+=sizeArr.length;
			for (int i = 0; i < leftArr.length; i++) {
				arr[i+a]=leftArr[i];
			}
			a+=leftArr.length;
			for (int i = 0; i < rightArr.length; i++) {
				arr[i+a]=rightArr[i];
			}
			a+=rightArr.length;
			
			byte[] record;
			int size=((LeafNode<R>)content).getSize();
			//System.out.println("size "+size);
			for (int i = 0; i < size; i++) {
				record=recConv.recordToByteArr((R)((LeafNode<R>)content).getRecords()[i]);
				for (int j = 0; j < record.length; j++) {
					arr[j+a]=record[j];
				}
				a+=record.length;
			}
		}
		if(type=='I') { //int 2 for internal
			byte[] typeArr = ScalarConverter.toByteArr(2);
			byte[] sizeArr = ScalarConverter.toByteArr(((InternalNode<R>)content).getSize());
			int a=0;
			
			for (int i = 0; i < typeArr.length; i++) {
				arr[i+a]=typeArr[i];
			}
			a+=typeArr.length;
			for (int i = 0; i < sizeArr.length; i++) {
				arr[i+a]=sizeArr[i];
			}
			a+=sizeArr.length;
			
			byte[] record;
			for (int i = 0; i < ((InternalNode<R>)content).getOrder(); i++) {
				record=recConv.keyToByteArr(((InternalNode<R>)content).getKeys()[i]); //!!!!!!! tuto to zlyha zmenit order na size a posilat null dummy byty
				for (int j = 0; j < record.length; j++) {
					arr[j+a]=record[j];
				}
				a+=record.length;
			}
			for (int i = 0; i < ((InternalNode<R>)content).getOrder()+1; i++) {
				record=ScalarConverter.toByteArr(((InternalNode<R>)content).getPointers()[i]);
				for (int j = 0; j < record.length; j++) {
					arr[j+a]=record[j];
				}
				a+=record.length;
			}
		}
		return arr;
	}
	
	public Node<R> getNode(){
		return (Node<R>)content;
	}
	
	public Metadata getMetadata(){
		return (Metadata)content;
	}
	
	public EmptyBlock getEmptyBlock() {
		return (EmptyBlock)content;
	}
	
	public String toString() {
		return content.toString();
	}
}
