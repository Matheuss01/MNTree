package Classes;

import java.text.SimpleDateFormat;
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
		SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
		
		String s="[Patient] ID: "+patientID+", name: "+name+", surname: "+surname+", birthDate: "+f.format(birthDate)+"";
		for (int i = 0; i < hospitalizations.size(); i++) {
			s+="\n  "+i+": "+hospitalizations.get(i);
		}
		return s;
	}
	
	public void startHospitalization(Date start, String diagnosis) {
		if(hospitalizations.size()>0) 
			if(hospitalizations.get(hospitalizations.size()-1).getEnd().getTime()==Long.MAX_VALUE){
			System.out.println("Hospitalization hasnt been finished yet");
			return;
			}
		Hospitalization h = new Hospitalization(start,new Date(Long.MAX_VALUE), diagnosis);
		hospitalizations.add(h);
	}
	
	public void finishHospitalization(Date end) {
		if(hospitalizations.size()==0) {
			System.out.println("Not yet hospitalized");
			return;
		}
		if(hospitalizations.get(hospitalizations.size()-1).getEnd().getTime()!=Long.MAX_VALUE){
			System.out.println("Hospitalization has been alreadyfinished");
			return;
		}
		hospitalizations.get(hospitalizations.size()-1).setEnd(end);
	}
	
	

	@Override
	public Object getKey() {
		return patientID;
	}

	@Override
	public Object getValue() {
		return this;
	}
	
}
