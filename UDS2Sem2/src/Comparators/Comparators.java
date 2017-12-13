package Comparators;

import java.util.Comparator;

import Classes.Hospitalization;
import Classes.Patient;

public class Comparators {
	public static Comparator<Object> numbComparator= new Comparator<Object>() {
		
		@Override
		public int compare(Object o1, Object  o2) {
			long res= (long)(int)o1-(long)(int)o2;
			
			if(res>0) return 1;
			else if(res<0) return -1;
			else return  0;
		}
	};
	
	
	public static Comparator<Object> patientComparator= new Comparator<Object>() {
		
		@Override
		public int compare(Object o1, Object  o2) {
			long res= (long)(int)o1-(long)(int)o2;
			
			if(res>0) return 1;
			else if(res<0) return -1;
			else return  0;
		}
	};
	
	public static Comparator<Patient> patientEqualsChecker= new Comparator<Patient>() {
		
		@Override
		public int compare(Patient p1, Patient  p2) {
			int res=0;
			res+=p1.getName().compareTo(p2.getName());
			res+=p1.getSurname().compareTo(p2.getSurname());
			res+=((Integer)p1.getPatientID()).compareTo(p2.getPatientID());
			res+=((Long)p1.getBirthDate().getTime()).compareTo(p2.getBirthDate().getTime());
			if(p1.getHospitalizations().size()!=p2.getHospitalizations().size()) return -1;
			for (int i = 0; i < p1.getHospitalizations().size(); i++) {
				res+=Comparators.hospitalizationEqualsChecker.compare(p1.getHospitalizations().get(i), p2.getHospitalizations().get(i));
			}
						
			return res;
		}
	};
	
	public static Comparator<Hospitalization> hospitalizationEqualsChecker= new Comparator<Hospitalization>() {
		
		@Override
		public int compare(Hospitalization h1, Hospitalization  h2) {
			int res=0;
			res+=h1.getDiagnosis().trim().compareTo(h2.getDiagnosis().trim());
			res+=((Long)h1.getEnd().getTime()).compareTo(h2.getEnd().getTime());
			res+=((Long)h1.getStart().getTime()).compareTo(h2.getStart().getTime());
			return res;
		}
	};
	
	
}
