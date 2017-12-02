package BPlusTree;

public class EmptyBlock {
	private int positionOfNextFreeBlock;

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
