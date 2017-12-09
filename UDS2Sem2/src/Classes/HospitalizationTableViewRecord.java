package Classes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class HospitalizationTableViewRecord {
	private SimpleStringProperty dateStart;
	private SimpleStringProperty dateEnd;
	private SimpleStringProperty diagnosis;
	
	
	public HospitalizationTableViewRecord(Hospitalization h) {
		super();
		SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
		this.dateStart = new SimpleStringProperty(f.format(h.getStart()));
		this.dateEnd = new SimpleStringProperty(h.getEnd().getTime()==Long.MAX_VALUE?"---":f.format(h.getEnd()));
		this.diagnosis = new SimpleStringProperty(h.getDiagnosis());
	}

	public String getDateStart() {
		return dateStart.get();
	}


	public String getDateEnd() {
		return dateEnd.get();
	}


	public String getDiagnosis() {
		return diagnosis.get();
	}

    public static LinkedList<HospitalizationTableViewRecord> getRecords(List<Hospitalization> list){
    	LinkedList<HospitalizationTableViewRecord> res = new LinkedList<>();
    	for (Hospitalization h : list) {
			res.add(new HospitalizationTableViewRecord(h));
		}
    	return res;
    }
	
}
