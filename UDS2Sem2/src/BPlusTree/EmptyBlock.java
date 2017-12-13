package BPlusTree;

public class EmptyBlock {
	private int positionOfNextFreeBlock;
	private int diskPosition;

	public int getDiskPosition() {
		return diskPosition;
	}

	public void setDiskPosition(int diskPosition) {
		this.diskPosition = diskPosition;
	}

	public void setPositionOfNextFreeBlock(int positionOfNextFreeBlock) {
		this.positionOfNextFreeBlock = positionOfNextFreeBlock;
	}

	public EmptyBlock(int positionOfNextFreeBlock) {
		super();
		this.positionOfNextFreeBlock = positionOfNextFreeBlock;
	}

	public int getPositionOfNextFreeBlock() {
		return positionOfNextFreeBlock;
	}
	
	public String toString() {
		return "EMPTY BLOCK\nPosition of next free block : "+positionOfNextFreeBlock;
	}
	
	
}
