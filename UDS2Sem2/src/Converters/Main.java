package Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import Classes.Hospitalization;
import Classes.Patient;
import Comparators.Comparators;

public class Main {
	public static void main(String[] args) {
		PatientConverter pc = new PatientConverter(Comparators.numbComparator);		
		ArrayList<Hospitalization> hospitalizations = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			hospitalizations.add(new Hospitalization(new Date(1000000l*200000l), new Date(1000000l*500000l), "Broken nose"));
		}
		Patient p = new Patient(1, new Date(1000000l*200000l), "Matej", "Papik",hospitalizations);
		
		System.out.println(p);
		byte[] patientArr = pc.recordToByteArr(p);
		System.out.println(Arrays.toString(patientArr));
		Patient b = (Patient) pc.toRecord(patientArr);
		System.out.println(b);
	}
}
