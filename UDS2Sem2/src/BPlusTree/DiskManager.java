package BPlusTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import Classes.Hospitalization;
import Classes.Record;
import Converters.HospitalizationConverter;
import Converters.RecordConverter;
import Converters.ScalarConverter;

public class DiskManager<R extends Record> {
	private final File file;
	private final  Metadata metadata;
	private RecordConverter rc;
	
	
	public DiskManager(File file, RecordConverter rc) throws Exception {
		this.file=file;
		int blockSize=readBlockSize();
		Block<R> dummyBlock = new Block<>(null,null);
		dummyBlock.setContent(readBytes(0, blockSize));
		metadata = dummyBlock.getMetadata();
		this.rc=rc;
	}
	
	public DiskManager(Metadata metadata, RecordConverter rc) throws IOException { //defaultny subor workingFile , ked sa nenacitava zo suboru
		this.file=new File("src/Files/workingFile.txt");
		this.rc=rc;
		this.metadata=metadata;
		
	}
	
	private int readBlockSize() throws IOException {
		byte[] BlocksizeArr = new byte[Integer.BYTES];
		RandomAccessFile raf= new RandomAccessFile(file, "r");
		
		raf.seek(4); //velkost bloku je integer na pozicii[4]
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
	
	private void writeBytes(int pos, byte[] block) throws IOException {
		//System.out.println("bytes");
		RandomAccessFile raf= new RandomAccessFile(file, "rw");
		raf.seek(pos);
		raf.write(block, 0, block.length);
		raf.close();
	}
	
	private byte[] readBytes(int pos, int len) throws IOException {
		byte[] arr = new byte[len];
		RandomAccessFile raf= new RandomAccessFile(file, "rw");
		raf.seek(pos*metadata.getBlockSize());
		raf.readFully(arr);
		raf.close();
		return arr;
	}
	
	public Block<R> readBlock(int pos) throws Exception{
		byte[] arr = new byte[metadata.getBlockSize()];
		RandomAccessFile raf= new RandomAccessFile(file, "rw");
		raf.seek(pos*metadata.getBlockSize());
		raf.readFully(arr);
		raf.close();
		Block<R> block = new Block<R>(rc, metadata);
		block.setContent(arr);
		return block;
	}
	
	public void setFileLength(int newLength) throws FileNotFoundException, IOException {
	    RandomAccessFile raf = new RandomAccessFile(file, "rw");
	    raf.setLength(newLength);
	}
	
	private void writeBlock(int pos, Block<R> block) throws IOException {
		RandomAccessFile raf= new RandomAccessFile(file, "rw");
		raf.seek(pos*metadata.getBlockSize());
		//System.out.println(block.getType()+" "+ pos+", pocet blokov: "+metadata.getNumberOfBlocks());
		raf.write(block.toByteArray(), 0, metadata.getBlockSize());
		raf.close();
	}
	
	public void writeMetadata(Metadata metadata) throws IOException {
		//System.out.println("write metadata");
		Block<R> block = new Block<R>(rc, metadata);
		block.setContent(metadata);
		writeBlock(0, block);
	}
	
	public void writeNode(int position, Node<R> node) throws Exception {
		Block<R> block = new Block<R>(rc, metadata);
		block.setContent(node);
		writeBlock(position,block);
	}
	
	public Node<R> readNode(int position) throws IOException, Exception{
		Block<R> block = new Block<R>(rc, metadata);
		block.setContent(readBytes(position, metadata.getBlockSize()));
		return block.getNode();
	}
	
	public Metadata readMetadata() throws IOException, Exception{
		Block<R> block = new Block<R>(rc, metadata);
		block.setContent(readBytes(0, metadata.getBlockSize()));
		return block.getMetadata();
	}
	
	public EmptyBlock readEmptyBlock(int pos) throws IOException, Exception {
		Block<R> block = new Block<R>(rc, metadata);
		block.setContent(readBytes(pos, metadata.getBlockSize()));
		return block.getEmptyBlock();
	}
	
	public int writeNewNode(Node<R> node) throws IOException, Exception { //returns posit. of new block
		int numberOfBlocks = metadata.getNumberOfBlocks();
		int firstFree  = metadata.getPositionOfFirstFreeBlock();
		int pos;
		
		if(firstFree==-1) {   // znamena ze nie je volny blok -> subor je plny , volny blok bude za koncom suboru
			pos=numberOfBlocks;
			numberOfBlocks++;
		}else {
			pos=firstFree;
			EmptyBlock emptyBlock = readEmptyBlock(firstFree);
			firstFree=emptyBlock.getPositionOfNextFreeBlock();
		}
		metadata.setPositionOfFirstFreeBlock(firstFree);
		metadata.setNumberOfBlocks(numberOfBlocks);
		
		writeNode(pos, node);
		return pos;
	}
	
	public void writeEmptyBlock(int pos, EmptyBlock emptyBlock) throws IOException {
		Block<R> block = new Block<R>(rc, metadata);
		block.setContent(emptyBlock);
		writeBlock(pos,block);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws IOException {
	/*	File f = new File("src/Files/testFile.txt");
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
		
		*/
		
		
		
	}
}
