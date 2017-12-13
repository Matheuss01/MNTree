package BPlusTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.text.Position;

import Classes.Hospitalization;
import Classes.Record;
import Converters.HospitalizationConverter;
import Converters.RecordConverter;
import Converters.ScalarConverter;

public class DiskManager<R extends Record> {
	private final File file;
	private final  Metadata metadata;
	private RecordConverter rc;
	private RandomAccessFile raf;
	private ArrayList<EmptyBlock> emptyBlocks = new ArrayList<>();
	
	
	public DiskManager(File file, RecordConverter rc) throws Exception {
		this.file=file;
		int blockSize=readBlockSize();
		Block<R> dummyBlock = new Block<>(null,null);
		dummyBlock.setContent(readBytes(0, blockSize));
		metadata = dummyBlock.getMetadata();
		this.rc=rc;
		loadEmptyBlocks();
	}
	
	public DiskManager(Metadata metadata, RecordConverter rc) throws Exception { //defaultny subor workingFile , ked sa nenacitava zo suboru
		this.file=new File("src/Files/workingFile.txt");
		this.raf= new RandomAccessFile(file, "rw");
		this.rc=rc;
		this.metadata=metadata;
		loadEmptyBlocks();
		
	}
	
	private int readBlockSize() throws IOException {
		byte[] BlocksizeArr = new byte[Integer.BYTES];
		//RandomAccessFile raf= new RandomAccessFile(file, "r");
		
		raf.seek(4); //velkost bloku je integer na pozicii[4]
		raf.readFully(BlocksizeArr);
		//raf.close();
		return ScalarConverter.toInt(BlocksizeArr);
	}
	
	private int readKeySize() throws IOException {
		byte[] orderArr = new byte[Integer.BYTES];
		//RandomAccessFile raf= new RandomAccessFile(file, "r");
		
		raf.seek(6*Integer.BYTES);
		raf.readFully(orderArr);
		//raf.close();
		return ScalarConverter.toInt(orderArr);
	}
	
	private int readRecordSize() throws IOException {
		byte[] orderArr = new byte[Integer.BYTES];
		//RandomAccessFile raf= new RandomAccessFile(file, "r");
		
		raf.seek(7*Integer.BYTES);
		raf.readFully(orderArr);
		//raf.close();
		return ScalarConverter.toInt(orderArr);
	}
	
	private void writeBytes(int pos, byte[] block) throws IOException {
		//System.out.println("bytes");
		//RandomAccessFile raf= new RandomAccessFile(file, "rw");
		raf.seek(pos);
		raf.write(block, 0, block.length);
		//raf.close();
	}
	
	private byte[] readBytes(int pos, int len) throws IOException {
		byte[] arr = new byte[len];
		//RandomAccessFile raf= new RandomAccessFile(file, "rw");
		raf.seek(pos*metadata.getBlockSize());
		raf.readFully(arr);
		//raf.close();
		return arr;
	}
	
	public Block<R> readBlock(int pos) throws Exception{
		byte[] arr = new byte[metadata.getBlockSize()];
		//RandomAccessFile raf= new RandomAccessFile(file, "rw");
		raf.seek(pos*metadata.getBlockSize());
		raf.readFully(arr);
		//raf.close();
		Block<R> block = new Block<R>(rc, metadata);
		block.setContent(arr);
		return block;
	}
	
	public void setFileLength(int newLength) throws FileNotFoundException, IOException {
	   // RandomAccessFile raf = new RandomAccessFile(file, "rw");
	    raf.setLength(newLength);
	}
	
	private void writeBlock(int pos, Block<R> block) throws IOException {
		//RandomAccessFile raf= new RandomAccessFile(file, "rw");
		raf.seek(pos*metadata.getBlockSize());
		//System.out.println(block.getType()+" "+ pos+", pocet blokov: "+metadata.getNumberOfBlocks());
		raf.write(block.toByteArray(), 0, metadata.getBlockSize());
		//raf.close();
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
	
	public ArrayList<EmptyBlock> getEmptyBlocks() {
		return emptyBlocks;
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
		int pos = getFirstEmptyBlock();		
		writeNode(pos, node);
		return pos;
	}
	
	public void writeEmptyBlock(int pos, EmptyBlock emptyBlock) throws IOException {
		Block<R> block = new Block<R>(rc, metadata);
		block.setContent(emptyBlock);
		writeBlock(pos,block);
	}
	
	public void closeRAF() throws IOException {
		raf.close();
	}
	
	private void loadEmptyBlocks() throws IOException, Exception {
		int nextEmptyBlockPosition=metadata.getPositionOfFirstFreeBlock();
		while(nextEmptyBlockPosition>0) {
			EmptyBlock eb = readEmptyBlock(nextEmptyBlockPosition);
			eb.setDiskPosition(nextEmptyBlockPosition);
			emptyBlocks.add(eb);
			nextEmptyBlockPosition=eb.getPositionOfNextFreeBlock();
		}
	}
	/*
	 * returns block# of first emty block if any block is empty
	 * if no empty block avaible, then alocates new block at the end of the file
	 */
	public int getFirstEmptyBlock() throws IOException {
		int res;
		if(emptyBlocks.size()==0) {
			res=metadata.getNumberOfBlocks();
			metadata.setNumberOfBlocks(metadata.getNumberOfBlocks()+1);
		}
		else {
			res=metadata.getPositionOfFirstFreeBlock();
			metadata.setPositionOfFirstFreeBlock(emptyBlocks.remove(0).getPositionOfNextFreeBlock());
		}
		writeMetadata(metadata);
		return res;
	}
	
	
	
	public void addEmptyBlock(int posOfNewEmpty) throws IOException {
		EmptyBlock newEB = new EmptyBlock(-1);
		newEB.setDiskPosition(posOfNewEmpty);
		emptyBlocks.add(newEB);
		emptyBlocks.sort((e1,e2)->((Integer)e1.getDiskPosition()).compareTo(e2.getDiskPosition()));
		for (int i = 0; i < emptyBlocks.size(); i++) {
			if(emptyBlocks.get(i)==newEB) {
				if(i>0) {
					emptyBlocks.get(i-1).setPositionOfNextFreeBlock(newEB.getDiskPosition());
					writeEmptyBlock(emptyBlocks.get(i-1).getDiskPosition(), emptyBlocks.get(i-1));
				}
				if(i<emptyBlocks.size()-1) newEB.setPositionOfNextFreeBlock(emptyBlocks.get(i+1).getDiskPosition());
				break;
				
			}
		}
		metadata.setPositionOfFirstFreeBlock(emptyBlocks.isEmpty()?-1:emptyBlocks.get(0).getDiskPosition());
		
		writeEmptyBlock(newEB.getDiskPosition(), newEB);
		
		shrinkFile(); //   set lenght of the file according to number of blocks
		

		writeMetadata(metadata);

		
		//!!!! pridat skracovanie suboru
	}
	
	private void shrinkFile() throws IOException {
		while(true) {
			if(emptyBlocks.size()==0) break;
			if(emptyBlocks.get(emptyBlocks.size()-1).getDiskPosition()==metadata.getNumberOfBlocks()-1) {
				metadata.setNumberOfBlocks(metadata.getNumberOfBlocks()-1);
				emptyBlocks.remove(emptyBlocks.size()-1);
			}else {
				break;
			}
		}

			
			
		if(emptyBlocks.isEmpty()) metadata.setPositionOfFirstFreeBlock(-1);
		else {
			emptyBlocks.get(emptyBlocks.size()-1).setPositionOfNextFreeBlock(-1);
			writeEmptyBlock(emptyBlocks.get(emptyBlocks.size()-1).getDiskPosition(), emptyBlocks.get(emptyBlocks.size()-1));
		}
		
		raf.setLength(metadata.getBlockSize()*metadata.getNumberOfBlocks());
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
