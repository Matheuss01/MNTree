package Classes;

import java.util.ArrayList;
import java.util.Date;

public class Patient implements Record{
	private String name; //25
	private String surname ; //25
	private ArrayList<Hospitalization> hospitalizations = new ArrayList<Hospitalization>(100); //max 100x
	private int patientID; 
	private Date birthDate;
	
		
	public Patient(int patientID, Date birthDate, String name, String surname) {
		super();
		this.patientID = patientID;
		this.birthDate = birthDate;
		this.name=name;
		this.surname=surname;
	}
	
	public Patient(int patientID, Date birthDate, String name, String surname, ArrayList<Hospitalization> hospitalizations) {
		super();
		this.patientID = patientID;
		this.birthDate = birthDate;
		this.name=name;
		this.surname=surname;
		this.hospitalizations=hospitalizations;
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
	
	public String toString() {
		String s="[Patient] ID: "+patientID+", name: "+name+", surname: "+surname+", birthDate: "+birthDate.toString()+"\n";
		for (int i = 0; i < hospitalizations.size(); i++) {
			s+=" "+i+": "+hospitalizations.get(i)+"\n";
		}
		return s;
	}

	@Override
	public Object getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
