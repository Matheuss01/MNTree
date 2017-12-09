package BPlusTreeGUI;

import java.util.LinkedList;

import Classes.Patient;
import Classes.PatientTableViewRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class IntervalFindFrameController {
	@FXML private TableView<PatientTableViewRecord> intervalFindTableView;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void fillTableList(LinkedList<Patient> patients) {
		
		 ObservableList<PatientTableViewRecord> data = FXCollections.observableList(PatientTableViewRecord.getRecords(patients));       

	        intervalFindTableView.setItems(data);
	        TableColumn colPID = new TableColumn("Patient ID");
	        colPID.setCellValueFactory(new PropertyValueFactory<PatientTableViewRecord, String>("PID"));
	        TableColumn colName = new TableColumn("Name");
	        colName.setCellValueFactory(new PropertyValueFactory<PatientTableViewRecord, String>("name"));
	        TableColumn colSurname = new TableColumn("Surname");
	        colSurname.setCellValueFactory(new PropertyValueFactory<PatientTableViewRecord, String>("surname"));
	        TableColumn colBirth = new TableColumn("Birth date");
	        colBirth.setCellValueFactory(new PropertyValueFactory<PatientTableViewRecord, String>("birthDate"));
	        
	        colBirth.setPrefWidth(130);
	        colName.setPrefWidth(130);
	        colPID.setPrefWidth(130);
	        colSurname.setPrefWidth(130);
	        
	        intervalFindTableView.setMaxWidth(4*130);
	        intervalFindTableView.getColumns().setAll(colPID,colName,colSurname, colBirth);
	}
}
