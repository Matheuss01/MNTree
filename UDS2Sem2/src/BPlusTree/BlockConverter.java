package BPlusTree;

public interface BlockConverter<R extends Record> {
	public byte[] toByteArray(Node<R> node);
	public Node<R> toNode(byte[] arr);
	
}
