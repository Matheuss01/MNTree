package BPlusTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import Classes.Hospitalization;
import Classes.Record;
import Converters.Constants;
import Converters.HospitalizationConverter;
import Converters.RecordConverter;
import Converters.ScalarConverter;

public class DiskManager<R extends Record> {
	private final File file;
	private final int blockSize;
	private final int order;
	private final int keySize;
	private final int recordSize;
	private Block<R> block;
	private Metadata metadata;
	private RecordConverter rc;
	
	public DiskManager(File file, RecordConverter rc) throws IOException {
		this.file=file;
		this.blockSize=readBlockSize();
		this.order=readOrder();
		this.keySize=readKeySize();
		this.recordSize=readRecordSize();
		this.rc=rc;
		block = new Block<R>(rc, blockSize, order,keySize,recordSize);
	}
	
	public DiskManager(RecordConverter rc, int blockSize, int order, int keySize, int recordSize) throws IOException { //defaultny subor workingFile , ked sa nenacitava zo suboru
		this.file=new File("src/Files/workingFile.txt");;
		this.blockSize=blockSize;
		this.order=order;
		this.keySize=keySize;
		this.recordSize=recordSize;
		this.rc=rc;
		block = new Block<R>(rc, blockSize, order,keySize,recordSize);
		metadata=new Metadata(blockSize, 1, -1, 0, order, keySize, recordSize, 2);
	}
	
	private int readBlockSize() throws IOException {
		byte[] BlocksizeArr = new byte[Integer.BYTES];
		RandomAccessFile raf= new RandomAccessFile(file, "r");
		
		raf.seek(0);
		raf.readFully(BlocksizeArr);
		raf.close();
		return ScalarConverter.toInt(BlocksizeArr);
	}
	
	private int readOrder() throws IOException {
		byte[] orderArr = new byte[Integer.BYTES];
		RandomAccessFile raf= new RandomAccessFile(file, "r");
		
		raf.seek(5*Integer.BYTES);
		raf.readFully(orderArr);
		raf.close();
		return ScalarConverter.toInt(orderArr);
	}
	
	private int readKeySize() throws IOException {
		byte[] orderArr = new byte[Integer.BYTES];
		RandomAccessFile raf= new RandomAccessFile(file, "r");
		
		raf.seek(6*Integer.BYTES);
		raf.readFully(orderArr);
		raf.close();
		return ScalarConverter.toInt(orderArr);
	}
	
	private int readRecordSize() throws IOException {
		byte[] orderArr = new byte[Integer.BYTES];
		RandomAccessFile raf= new RandomAccessFile(file, "r");
		
		raf.seek(7*Integer.BYTES);
		raf.readFully(orderArr);
		raf.close();
		return ScalarConverter.toInt(orderArr);
	}
	
	private void writeBlock(int pos, byte[] block) throws IOException {
		RandomAccessFile raf= new RandomAccessFile(file, "rw");
		raf.seek(pos);
		raf.write(block, 0, block.length);
		raf.close();
	}
	
	private byte[] readBytes(int pos, int len) throws IOException {
		byte[] arr = new byte[len];
		RandomAccessFile raf= new RandomAccessFile(file, "rw");
		raf.seek(pos*blockSize);
		raf.readFully(arr);
		raf.close();
		return arr;
	}
	
	public Block<R> readBlock(int pos) throws Exception{
		byte[] arr = new byte[blockSize];
		RandomAccessFile raf= new RandomAccessFile(file, "rw");
		raf.seek(pos*blockSize);
		raf.readFully(arr);
		raf.close();
		Block<R> block = new Block<>(rc,blockSize,order,keySize,recordSize);
		block.setContent(arr);
		return block;
	}
	
	public void setFileLength(int newLength) throws FileNotFoundException, IOException {
	    RandomAccessFile raf = new RandomAccessFile(file, "rw");
	    raf.setLength(newLength);
	}
	
	public Metadata createMetadata(int blockSize, int positionOfRoot, int positionOfFirstFreeBlock, int numberOfRecords, int order, int keySize, int recordSize, int numberOfBlocks) {
		return new Metadata(blockSize, positionOfRoot, positionOfFirstFreeBlock, numberOfRecords, order, keySize, recordSize, numberOfBlocks);
	}
	
	private void writeBlock(int pos, Block<R> block) throws IOException {
		RandomAccessFile raf= new RandomAccessFile(file, "rw");
		raf.seek(pos*blockSize);
		raf.write(block.toByteArray(), 0, blockSize);
		raf.close();
	}
	
	public void writeMetadata(Metadata metadata) throws IOException {
		block.setContent(metadata);
		writeBlock(0, block);
	}
	
	public void writeNode(int position, Node<R> node) throws Exception {
		block.setContent(node);
		writeBlock(position,block);
	}
	
	public Node<R> readNode(int position) throws IOException, Exception{
		block.setContent(readBytes(position, blockSize));
		return block.getNode();
	}
	
	public Metadata readMetadata() throws IOException, Exception{
		block.setContent(readBytes(0, blockSize));
		return block.getMetadata();
	}
	
	public EmptyBlock readEmptyBlock(int pos) throws IOException, Exception {
		block.setContent(readBytes(pos, blockSize));
		return block.getEmptyBlock();
	}
	
	/*public int getFreeBlockAndSetNew() {
		int res;
		int numberOfBlocks = metadata.numberOfBlocks;
		int firstFree  = metadata.positionOfFirstFreeBlock;
		if(firstFree==-1) {
			res=numberOfBlocks;
			numberOfBlocks++;
		}
		else {
			res=firstFree;
		}
	}*/
	
	public int writeNewNode(Node<R> node) throws IOException, Exception { //returns posit. of new block
		int numberOfBlocks = metadata.numberOfBlocks;
		int firstFree  = metadata.positionOfFirstFreeBlock;
		int pos;
		
		if(firstFree==-1) {   // ynamena ze nie je volny blok -> subor je plny , volny blok bude za koncom suboru
			pos=numberOfBlocks;
			numberOfBlocks++;
		}else {
			pos=firstFree;
			EmptyBlock emptyBlock = readEmptyBlock(firstFree);
			firstFree=emptyBlock.getPositionOfNextFreeBlock();
		}
		metadata.positionOfFirstFreeBlock=firstFree;
		metadata.numberOfBlocks=numberOfBlocks;
		
		writeNode(pos, node);
		return pos;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws IOException {
		File f = new File("src/Files/testFile.txt");
		DiskManager dm = new DiskManager(f, null); // premenit z null
		byte[] name = new byte[50];
		for (int i = 0; i < name.length; i++) {
			name[i]=(byte)i;
		}
		dm.writeBlock(0, name);
		
		Hospitalization h = new Hospitalization(new Date(5000L*500000L), new Date(5000L*1000000L), "Broken leg");
		HospitalizationConverter hc = new HospitalizationConverter();
		//dm.writeBlock(0, hc.toByte(h));
		
		//Hospitalization h2 = hc.toHospitalization(dm.readBlock(0, Constants.MAXSIZE_HOSPITALIZATION));
		//dm.writeBlock(3, ScalarConverter.toByteArr("Matej Papik"));
		//System.out.println(h2);
		dm.setFileLength(40);
		System.out.println("size:"+f.length()+", usable space: "+f.getUsableSpace()+", total space: "+f.getTotalSpace()+", free space: "+f.getFreeSpace());
		
		
		
		
		
	}
}
