package Classes;

import java.util.ArrayList;
import java.util.Date;

public class Patient {
	private String name;
	private String surname ;
	private ArrayList<Hospitalization> hospitalizations = new ArrayList<Hospitalization>(100);
	private int patientID;
	private Date birthDate;
	
		
	public Patient(int patientID, Date birthDate, String name, String surname) {
		super();
		this.patientID = patientID;
		this.birthDate = birthDate;
		this.name=name;
		this.surname=surname;
	}
	
	public int getPatientID() {
		return patientID;
	}
	
	public void setPatientID(int patientID) {
		this.patientID = patientID;
	}
	
	public Date getBirthDate() {
		return birthDate;
	}
	
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setName(String name) {
		this.name=name;
	}
	
	public void setSurname(String surname) {
		this.surname=surname;
	}
	
	public ArrayList<Hospitalization> getHospitalizations() {
		return hospitalizations;
	}
	
	
}
