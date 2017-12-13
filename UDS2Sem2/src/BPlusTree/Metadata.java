package BPlusTree;

public class Metadata {
	private final int blockSize;
	private int positionOfRoot;
	private int positionOfFirstFreeBlock;
	private int numberOfRecords;
	private final int internalOrder;
	private final int leafOrder;
	private final int keySize;
	private final int recordSize;
	private int numberOfBlocks;
	
	public Metadata(int blockSize, int positionOfRoot, int positionOfFirstFreeBlock, int numberOfRecords, int leafOrder,int internalOrder, int keySize, int recordSize, int numberOFBlocks) {
		super();
		this.blockSize = blockSize;
		this.positionOfRoot = positionOfRoot;
		this.positionOfFirstFreeBlock = positionOfFirstFreeBlock;
		this.numberOfRecords = numberOfRecords;
		this.leafOrder = leafOrder;
		this.keySize=keySize;
		this.recordSize=recordSize;
		this.numberOfBlocks=numberOFBlocks;
		this.internalOrder = internalOrder;
	}
	
	
	public int getInternalOrder() {
		return internalOrder;
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

	public int getLeafOrder() {
		return leafOrder;
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
				+ positionOfFirstFreeBlock + "\nNumber of records : " + numberOfRecords + "\nLeaf Order : " + leafOrder+ "\nInternal Order : " + internalOrder + "\nKey size : "
				+ keySize + "\nRecord size : " + recordSize + "\nNumber of blocks : " + numberOfBlocks ;
	}
	
	
	
	
}
