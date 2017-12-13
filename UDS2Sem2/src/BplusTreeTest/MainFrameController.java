package BplusTreeTest;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.ResourceBundle;

import BPlusTree.BPlusTree;
import BPlusTree.LeafNode;
import BPlusTree.Metadata;
import Classes.Numb;
import Comparators.Comparators;
import Converters.Constants;
import Converters.NumbConverter;
import Converters.PatientConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainFrameController implements Initializable {
	
	//find
	@FXML private TextField findFieldPersonalID;
	@FXML private Button findButton;
	
	//find interval
	@FXML private Button findIntervalButton;
	@FXML private TextField findIntervalFieldFrom;
	@FXML private TextField findIntervalFieldTo;
	
	//generate
	@FXML private Button generateButton;
	@FXML private TextField generateFieldNumberOfRecords;
	
	//show blocks
	@FXML private Button showBlocksButton;
	
	//patient record
	@FXML private TextField nameField;
	@FXML private TextField surnameField;
	@FXML private TextField personalIDField;
	@FXML private TextField birthDateField;
	
	@FXML private TableView hospitalizationTableView;
	@FXML private Button saveButton;
	@FXML private Button deleteButton;
	@FXML private Button createNewButton;
	@FXML private Button clearButton;
	
	@FXML private TextField hospitalizationDateFromField;
	@FXML private TextField hospitalizationDiagnosisField;
	@FXML private TextField hospitalizationDateToField;
	

	private BPlusTree<Numb> bPlusTree ; //toto sa potom poriesi tak ze sa najprv zvoli ci sa chce strom nacitat zo suboru alebo vytvorit novy a podla toho sa strom inicializuje
	private AllBlocksFrameController allBlocksController;
	private IntervalFindFrameController intervalFindFrameController;
	
	private LeafNode<Numb> actualLeafNode;
	private Numb actualNumb;
	
	@FXML
	public void showBlocks() throws Exception {
		//loading new stage
		URL url = getClass().getResource("/BplusTreeTest/AllBlocksFrame.fxml");
		FXMLLoader loader = new FXMLLoader(url);
		ScrollPane root = (ScrollPane)loader.load();
		allBlocksController=loader.getController();
		
		Metadata meta = bPlusTree.getDiskManager().readMetadata();
		for (int i = 0; i < meta.getNumberOfBlocks(); i++) {
			allBlocksController.addBlock(bPlusTree.getDiskManager().readBlock(i));
		}
				
		Stage stage = new Stage();
		stage.setWidth(1500);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("File Blocks");
		stage.show();
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) { //toto tu pre strom neskor nebude
		try {
			//loading b+ tree
			NumbConverter numbConverter = new NumbConverter(Comparators.numbComparator);
			//bPlusTree= new BPlusTree<Numb>(2,2,Comparators.numbComparator, numbConverter, 100, 4, 14);
			test();
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		}catch(Exception e) {
			System.out.println("Problem with loading!");
		}
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@FXML
	public void findButtonClicked() throws IOException, Exception {
		//try {
			int findKey = Integer.parseInt(findFieldPersonalID.getText());
			
			actualLeafNode=bPlusTree.getLeaf(findKey);
			if(actualLeafNode==null) {
				System.out.println("Patient not found");
				return;
			}
			actualNumb=actualLeafNode.getRecord((Object)findKey);
			if(actualNumb==null) {
				System.out.println("patient with that number not found");
				return;
			}
			
			surnameField.setText(actualNumb.getS());
			personalIDField.setText(actualNumb.getN()+"");
		
			
						
		//}catch(Exception e) {
		//	System.out.println("Not found");
	//	}
	}
	
	@FXML
	public void generateButtonClicked() throws IOException, Exception {
	//	try {
			Random rnd = new Random(10);
			int howMany = Integer.parseInt(generateFieldNumberOfRecords.getText());
			for (int i = 0; i < howMany; i++) {
				Numb p = new Numb(rnd.nextInt(1000));
				bPlusTree.add(p);
			}
	//	}catch(Exception e) {
	//		System.out.println("Problem with generating patients");
	//	}
		
	}
	
	
	
	@FXML
	public void deleteButtonClicked() throws IOException, Exception {
		if(actualNumb!=null) bPlusTree.delete(actualNumb.getKey());
		actualLeafNode=null;
		actualNumb=null;
	}
	
	@FXML
	public void createNewButtonClicked() throws IOException, Exception {
		//try {
			actualLeafNode=null;
			actualNumb=null;
				
			//String surname=surnameField.getText();
			int personalID = Integer.parseInt(personalIDField.getText());
		
			
			Numb p = new Numb(personalID);
			bPlusTree.add(p);
			clear();
						
	//	}catch(Exception e) {
		//	System.out.println("Incorrect format of data inserted");
		//}
	}
	
	
	@FXML
	public void clearButtonClicked() {
		clear();
	}
	
	private void clear() {
		nameField.setText("");
		surnameField.setText("");
		personalIDField.setText("");
		birthDateField.setText("");	
	}
	
	@FXML
	public void saveButtonClicked() throws Exception {
		String newSurname=surnameField.getText();
		int newID = Integer.parseInt(personalIDField.getText());

		if(newID==actualNumb.getN()) {
			actualNumb.setS(newSurname);
			
			bPlusTree.update(actualLeafNode);
		}else { //if key was changed
			bPlusTree.delete(actualNumb.getKey());
			actualNumb.setN(newID);
			actualNumb.setS(newSurname);
			bPlusTree.add(actualNumb);
		}
		clear();
	}
	
	
	public void closeRAF() throws IOException {
		bPlusTree.getDiskManager().closeRAF();
	}























/*@Override
	public void initialize(URL location, ResourceBundle resources) { //toto tu pre strom neskor nebude
		try {
			//loading b+ tree
			bPlusTree= new BPlusTree<Numb>(15, Comparators.numbComparator, new NumbConverter(Comparators.numbComparator), 300, 4, 14);
			Numb[] numbs = new Numb[15];
			numbs[0] = new Numb(0,"Zero");
			numbs[1]= new Numb(1,"One");
			numbs[2] = new Numb(2,"Two");
			numbs[3] = new Numb(3,"Three");
			numbs[4] = new Numb(4,"Four");
			numbs[5] = new Numb(5,"Five");
			numbs[6] = new Numb(6,"Six");
			numbs[7] = new Numb(7,"Seven");
			numbs[8] = new Numb(8,"Eight");
			numbs[9] = new Numb(9,"Nine");
			numbs[10] = new Numb(10,"Ten");
			numbs[11] = new Numb(11,"Eleven");
			numbs[12] = new Numb(12,"Twelve");
			numbs[13] = new Numb(13,"Thirteen");
			numbs[14] = new Numb(14,"Fourteen");
			/*for (int i = 0; i < 340; i++) {
				//bPlusTree.add(numbs[i]);
				bPlusTree.add(new Numb(i));
			}*/
			
			/*bPlusTree.add(numbs[1]);
			bPlusTree.add(numbs[2]);
			bPlusTree.add(numbs[4]);
			bPlusTree.add(numbs[5]);
			bPlusTree.add(numbs[3]);
			bPlusTree.add(numbs[6]);
			
			bPlusTree.delete(5);
			bPlusTree.delete(6);
			//bPlusTree.delete(1);
			Random rnd = new Random(10);
			for (int i = 0; i < 350; i++) {
				bPlusTree.add(new Numb(rnd.nextInt(100)));
			}
			//bPlusTree.delete(12);
			//bPlusTree.add(numbs[14]);
			//bPlusTree.delete(13);
			//bPlusTree.delete(11);
			/*bPlusTree.delete(3);
			bPlusTree.delete(4);
			bPlusTree.delete(5);
			bPlusTree.delete(0);
			bPlusTree.delete(7);
			bPlusTree.delete(2);
			bPlusTree.delete(12);
			bPlusTree.delete(6);
			bPlusTree.delete(8);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}*/
	
	public void test() throws Exception {
		bPlusTree= new BPlusTree<Numb>(3,3,Comparators.numbComparator, new NumbConverter(Comparators.numbComparator), 1000, 4, 14);
		Random rnd = new Random(5);
		
		ArrayList<Numb> nums = new ArrayList<>();
		int num;
		for (int i = 0; i < 3; i++) {
			System.out.println(i+" start");
			num=rnd.nextInt(30);
			for (int j = 0; j < num; j++) {
				
				Numb n = new Numb(rnd.nextInt(10000));
				System.out.println("inserted "+n+" "+i+" "+j);
				nums.add(n);
				bPlusTree.add(n);
			}
			//testInorder();
			
			boolean b=false;
			num=rnd.nextInt(nums.size());
			for (int j = 0; j < num; j++) {
				Numb n = nums.remove(rnd.nextInt(nums.size()-1));
				System.out.println(n+" "+i+" "+j);
			//	5561, N#5561 10 87
				//4643, N#4643 10 88
				//9449, N#9449 10 89
				
				if(i==2 && j==0) {
					System.out.println(n);
				//	return;
				}
				//9451, N#9451 0 20
				bPlusTree.delete(n.getKey());

			}
			
			//testInorder();
		}
	}

}
