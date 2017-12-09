package Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import Classes.Hospitalization;
import Classes.Patient;
import Classes.Record;

public class PatientConverter extends RecordConverter{
	
	public PatientConverter(Comparator<Object> comparator) {
		super(comparator);
	}

	HospitalizationConverter hospConverter = new HospitalizationConverter();
	
	
	public int getMaxSizeOfKey() {
		return Constants.MAXSIZE_ID;
	}
	
	public int getMaxSizeOfRecord() {
		return Constants.MAXSIZE_PATIENT;
	}
	
	public byte[] recordToByteArr(Record p) {
		byte[] nameArr = new byte[Constants.MAXSIZE_NAME];
		byte[] surnameArr = new byte[Constants.MAXSIZE_SURNAME];
		byte[] idArr = new byte[Constants.MAXSIZE_ID];
		byte[] birthdateArr = new byte[Constants.MAXSIZE_BIRTHDATE];
		byte[] numberOfHospitalizations = new byte[Integer.BYTES];
		byte[][] hospitalizationsArr = new byte[Constants.MAX_NUM_OF_HOSPITALIZATIONS][Constants.MAXSIZE_HOSPITALIZATION];
		
		nameArr=ScalarConverter.toByteArr(((Patient)p).getName(),Constants.MAXSIZE_NAME);
		surnameArr=ScalarConverter.toByteArr(((Patient)p).getSurname(),Constants.MAXSIZE_SURNAME);
		idArr=ScalarConverter.toByteArr(((Patient)p).getPatientID());
		birthdateArr=ScalarConverter.toByteArr(((Patient)p).getBirthDate());
		numberOfHospitalizations=ScalarConverter.toByteArr(((Patient)p).getHospitalizations().size());
		for (int i = 0; i < ((Patient)p).getHospitalizations().size(); i++) {
			hospitalizationsArr[i]=hospConverter.toByte(((Patient)p).getHospitalizations().get(i));
		}
		
		byte[] res = new byte[Constants.MAXSIZE_PATIENT];
		int a=0;
		for (int i = 0; i < nameArr.length; i++) {
			res[a+i]=nameArr[i];
		}
		a+=nameArr.length;
		
		for (int i = 0; i < surnameArr.length; i++) {
			res[a+i]=surnameArr[i];
		}
		a+=surnameArr.length;
		
		for (int i = 0; i < idArr.length; i++) {
			res[a+i]=idArr[i];
		}
		a+=idArr.length;
		
		for (int i = 0; i < birthdateArr.length; i++) {
			res[a+i]=birthdateArr[i];
		}
		a+=birthdateArr.length;
		
		for (int i = 0; i < numberOfHospitalizations.length; i++) {
			res[a+i]=numberOfHospitalizations[i];
		}
		a+=numberOfHospitalizations.length;
		
		for (int i = 0; i < hospitalizationsArr.length; i++) {
			for (int j = 0; j < hospitalizationsArr[0].length; j++) {
				res[a+j]=hospitalizationsArr[i][j];
			}
			a+=hospitalizationsArr[0].length;
		}
		
		return res;
	}
	
	public Record toRecord(byte[] arr) {
		byte[] nameArr = new byte[Constants.MAXSIZE_NAME];
		byte[] surnameArr = new byte[Constants.MAXSIZE_SURNAME];
		byte[] idArr = new byte[Constants.MAXSIZE_ID];
		byte[] birthdateArr = new byte[Constants.MAXSIZE_BIRTHDATE];
		byte[] numberOfHospitalizations = new byte[Integer.BYTES];
		byte[][] hospitalizationsArr = new byte[Constants.MAX_NUM_OF_HOSPITALIZATIONS][Constants.MAXSIZE_HOSPITALIZATION];
		
		int a=0;
		for (int i = 0; i < nameArr.length; i++) {
			nameArr[i]=arr[a+i];
		}
		
		String name=ScalarConverter.toString(nameArr);
		a+=nameArr.length;
		
		for (int i = 0; i < surnameArr.length; i++) {
			surnameArr[i]=arr[a+i];
		}
		String surname=ScalarConverter.toString(surnameArr);
		a+=surnameArr.length;
		
		for (int i = 0; i < idArr.length; i++) {
			idArr[i]=arr[a+i];
		}
		int id=ScalarConverter.toInt(idArr);
		a+=idArr.length;
		
		for (int i = 0; i < birthdateArr.length; i++) {
			birthdateArr[i]=arr[a+i];
		}
		Date birthdate=ScalarConverter.toDate(birthdateArr);
		a+=birthdateArr.length;
		
		for (int i = 0; i < numberOfHospitalizations.length; i++) {
			numberOfHospitalizations[i]=arr[a+i];
		}
		a+=numberOfHospitalizations.length;
		int num = ScalarConverter.toInt(numberOfHospitalizations);
		ArrayList<Hospitalization> hospitalizations = new ArrayList<>(num);
		for (int i = 0; i < num; i++) {
			for (int j = 0; j < hospitalizationsArr[0].length; j++) {
				hospitalizationsArr[i][j]=arr[a+j];
			}
			a+=hospitalizationsArr[0].length;
			hospitalizations.add(hospConverter.toHospitalization(hospitalizationsArr[i]));
		}
		System.out.println(a);
		return new Patient(id, birthdate, name, surname,hospitalizations);
	}
	
	public byte[] keyToByteArr(Object key) {
		if(key==null) return ScalarConverter.toByteArr(0);
		return ScalarConverter.toByteArr((int)key);
	}
	
	public Object toKey(byte[] arr) {
		return ScalarConverter.toInt(arr);
	}
	
	//-------------------------------------------------------------

	
	
	
	
	
	
	public static void main(String[] args) {
		//PatientConverter pc=new PatientConverter();
		String s="aaa";
		byte[] arr=ScalarConverter.toByteArr(s,10);
		System.out.println(Arrays.toString(arr));
		s=ScalarConverter.toString(arr);
		System.out.println(s);
		arr=ScalarConverter.toByteArr(s,10);
		System.out.println(Arrays.toString(arr));
		s=ScalarConverter.toString(arr);
		System.out.println(s);
		arr=ScalarConverter.toByteArr(s);
		System.out.println(Arrays.toString(arr));
	}
}
