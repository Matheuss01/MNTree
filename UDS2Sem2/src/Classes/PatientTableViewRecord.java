package Classes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PatientTableViewRecord {
	private SimpleStringProperty name;
	private SimpleStringProperty surname;
	private SimpleIntegerProperty PID;
	private SimpleStringProperty birthDate;
	
	
	public PatientTableViewRecord(Patient patient) {
		super();
		this.name = new SimpleStringProperty(patient.getName());
		this.surname = new SimpleStringProperty(patient.getSurname());
		this.PID = new SimpleIntegerProperty(patient.getPatientID());
		
		SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
		String date = f.format(patient.getBirthDate());	
		this.birthDate = new SimpleStringProperty(date);
	}

	public String getName() {
		return name.get();
	}


	public String getSurname() {
		return surname.get();
	}


	public Integer getPID() {
		return PID.get();
	}


	public String getBirthDate() {
		return birthDate.get();
	}

	
    public static LinkedList<PatientTableViewRecord> getRecords(LinkedList<Patient> list){
    	LinkedList<PatientTableViewRecord> res = new LinkedList<>();
    	for (Patient p : list) {
			res.add(new PatientTableViewRecord(p));
		}
    	return res;
    }
	
}
