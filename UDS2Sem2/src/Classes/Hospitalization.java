package Classes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Hospitalization {
	private Date start;
	private Date end;
	private String diagnosis; //max 40
	
	public Hospitalization(Date start, Date end, String diagnosis) {
		super();
		this.start = start;
		this.end = end;
		this.diagnosis = diagnosis;
	}
	
	public Hospitalization(Date start,String diagnosis) {
		super();
		this.start = start;
		this.end = null;
		this.diagnosis = diagnosis;
	}
	
	
	public Date getStart() {
		return start;
	}
	
	public void setStart(Date start) {
		this.start = start;
	}
	
	public Date getEnd() {
		return end;
	}
	
	public void setEnd(Date end) {
		this.end = end;
	}
	
	public String getDiagnosis() {
		return diagnosis;
	}
	
	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	
	public String toString() {
		SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
		return "[Hospitalization] Start: "+f.format(start)+", End: "+(end.getTime()==Long.MAX_VALUE?"---":f.format(end))+", Diagnosis: "+diagnosis;
	}
	
}
