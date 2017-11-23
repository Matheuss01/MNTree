package Classes;

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
		return "[Hospitalization] Start: "+start.toString()+", End: "+end+", Diagnosis: "+diagnosis;
	}
	
}
