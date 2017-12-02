package BPlusTree;

public class Metadata {
	private final int blockSize;
	private int positionOfRoot;
	private int positionOfFirstFreeBlock;
	private int numberOfRecords;
	private final int order;
	private final int keySize;
	private final int recordSize;
	private int numberOfBlocks;
	
	public Metadata(int blockSize, int positionOfRoot, int positionOfFirstFreeBlock, int numberOfRecords, int order, int keySize, int recordSize, int numberOFBlocks) {
		super();
		this.blockSize = blockSize;
		this.positionOfRoot = positionOfRoot;
		this.positionOfFirstFreeBlock = positionOfFirstFreeBlock;
		this.numberOfRecords = numberOfRecords;
		this.order = order;
		this.keySize=keySize;
		this.recordSize=recordSize;
		this.numberOfBlocks=numberOFBlocks;
	}
	
	public int getNumberOfBlocks() {
		return numberOfBlocks;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public int getPositionOfRoot() {
		return positionOfRoot;
	}

	public int getPositionOfFirstFreeBlock() {
		return positionOfFirstFreeBlock;
	}

	public int getNumberOfRecords() {
		return numberOfRecords;
	}

	public int getOrder() {
		return order;
	}

	public int getKeySize() {
		return keySize;
	}

	public int getRecordSize() {
		return recordSize;
	}

	public void setPositionOfRoot(int positionOfRoot) {
		this.positionOfRoot = positionOfRoot;
	}

	public void setPositionOfFirstFreeBlock(int positionOfFirstFreeBlock) {
		this.positionOfFirstFreeBlock = positionOfFirstFreeBlock;
	}

	public void setNumberOfRecords(int numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	public void setNumberOfBlocks(int numberOfBlocks) {
		this.numberOfBlocks = numberOfBlocks;
	}

	@Override
	public String toString() {
		return "METADATA\nBlock size : " + blockSize + "\nPosition of root : " + positionOfRoot + "\nPosition of first free block : "
				+ positionOfFirstFreeBlock + "\nNumber of records : " + numberOfRecords + "\nOrder : " + order + "\nKey size : "
				+ keySize + "\nRecord size : " + recordSize + "\nNumber of blocks : " + numberOfBlocks ;
	}
	
	
	
	
}
