package BPlusTreeGUI;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.ResourceBundle;

import BPlusTree.BPlusTree;
import BPlusTree.LeafNode;
import BPlusTree.Metadata;
import Classes.HospitalizationTableViewRecord;
import Classes.Patient;
import Comparators.Comparators;
import Converters.Constants;
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
	

	private BPlusTree<Patient> bPlusTree ; //toto sa potom poriesi tak ze sa najprv zvoli ci sa chce strom nacitat zo suboru alebo vytvorit novy a podla toho sa strom inicializuje
	private AllBlocksFrameController allBlocksController;
	private IntervalFindFrameController intervalFindFrameController;
	
	private LeafNode<Patient> actualLeafNode;
	private Patient actualPatient;
	
	@FXML
	public void showBlocks() throws Exception {
		//loading new stage
		URL url = getClass().getResource("/BPlusTreeGUI/AllBlocksFrame.fxml");
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
			PatientConverter patConverter = new PatientConverter(Comparators.patientComparator);
			bPlusTree= new BPlusTree<Patient>(3, Comparators.patientComparator, patConverter, 6000*3, Integer.BYTES, Constants.MAXSIZE_PATIENT);
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
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
			actualPatient=actualLeafNode.getRecord((Object)findKey);
			if(actualPatient==null) {
				System.out.println("patient with that number not found");
				return;
			}
			
			nameField.setText(actualPatient.getName());
			surnameField.setText(actualPatient.getSurname());
			personalIDField.setText(actualPatient.getPatientID()+"");
			
			SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
			birthDateField.setText(f.format(actualPatient.getBirthDate()));
			
			//hospitalizations
			ObservableList<HospitalizationTableViewRecord> data = FXCollections.observableList(HospitalizationTableViewRecord.getRecords(actualPatient.getHospitalizations()));       

	        hospitalizationTableView.setItems(data);
	        TableColumn colStart = new TableColumn("Start");
	        colStart.setCellValueFactory(new PropertyValueFactory<HospitalizationTableViewRecord, String>("dateStart"));
	        TableColumn colEnd = new TableColumn("End");
	        colEnd.setCellValueFactory(new PropertyValueFactory<HospitalizationTableViewRecord, String>("dateEnd"));
	        TableColumn colDiagnosis = new TableColumn("Diagnosis");
	        colDiagnosis.setCellValueFactory(new PropertyValueFactory<HospitalizationTableViewRecord, String>("diagnosis"));
	        
	        colStart.setPrefWidth(100);
	        colEnd.setPrefWidth(100);
	        colDiagnosis.setPrefWidth(300);
	        
	        hospitalizationTableView.setMaxWidth(4*130);
	        hospitalizationTableView.getColumns().setAll(colStart,colEnd,colDiagnosis);
		
			
			
						
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
				Patient p = new Patient(rnd.nextInt(1000), new Date(0), (char)('A'+rnd.nextInt(26))+"", (char)('A'+rnd.nextInt(26))+"");
				int hospitalizations=rnd.nextInt(10);
				for (int j = 0; j < hospitalizations; j++) {
					p.startHospitalization(new Date(100000*10000), "Test epidemy");
					p.finishHospitalization(new Date(1000000*100000));
				}
				if(rnd.nextBoolean())p.startHospitalization(new Date(123456789), "tralala diagnosis");
				bPlusTree.add(p);
			}
	//	}catch(Exception e) {
	//		System.out.println("Problem with generating patients");
	//	}
		
	}
	
	@FXML
	public void findIntervalButtonClicked() throws Exception {
		//try {			
			int minKey=Integer.parseInt(findIntervalFieldFrom.getText());
			int maxKey=Integer.parseInt(findIntervalFieldTo.getText());
			LinkedList<Patient> patients = bPlusTree.getRecordsInInterval(minKey, maxKey);

			//loading new stage
			URL url = getClass().getResource("/BPlusTreeGUI/IntervalFindFrame.fxml");
			FXMLLoader loader = new FXMLLoader(url);
			AnchorPane root = (AnchorPane)loader.load();
			intervalFindFrameController=loader.getController();
			
			intervalFindFrameController.fillTableList(patients);
			
					
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Selected patients in interval");
			stage.show();
		//}catch(Exception e) {
			//System.out.println("Probably incorect keys in interval");
		//}
	}
	
	@FXML
	public void deleteButtonClicked() throws IOException, Exception {
		if(actualPatient!=null) bPlusTree.delete(actualPatient.getKey());
		actualLeafNode=null;
		actualPatient=null;
	}
	
	@FXML
	public void createNewButtonClicked() throws IOException, Exception {
		//try {
			actualLeafNode=null;
			actualPatient=null;
				
			String name=nameField.getText();
			String surname=surnameField.getText();
			int personalID = Integer.parseInt(personalIDField.getText());
			
			SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
			Date birthDate = f.parse(birthDateField.getText());
			
			Patient p = new Patient(personalID, birthDate, name, surname);
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
		String newName=nameField.getText();
		String newSurname=surnameField.getText();
		int newID = Integer.parseInt(personalIDField.getText());
		
		SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
		Date newBirthDate = f.parse(birthDateField.getText());
		
		if(newID==actualPatient.getPatientID()) {
			actualPatient.setBirthDate(newBirthDate);
			actualPatient.setName(newName);
			actualPatient.setSurname(newSurname);
			
			bPlusTree.update(actualLeafNode);
		}else { //if key was changed
			bPlusTree.delete(actualPatient.getKey());
			bPlusTree.add(actualPatient);
		}
	}
	
	@FXML
	public void startHospitalization() throws ParseException {
		if(actualPatient==null) {
			System.out.println("Patient hasnt been selected");
			return;
		}
		SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
		
		actualPatient.startHospitalization(f.parse(hospitalizationDateFromField.getText()), hospitalizationDiagnosisField.getText());
	}
	
	@FXML
	public void finishHospitalization() throws ParseException {
		if(actualPatient==null) {
			System.out.println("Patient hasnt been selected");
			return;
		}		
		SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
		
		actualPatient.finishHospitalization(f.parse(hospitalizationDateToField.getText()));
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


}
