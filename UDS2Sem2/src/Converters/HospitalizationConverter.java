package Converters;

import Classes.Hospitalization;

public class HospitalizationConverter {
	
	public Hospitalization toHospitalization(byte[] arr) {
		byte[] startArr = new byte[Constants.MAXSIZE_START];
		byte[] endArr = new byte[Constants.MAXSIZE_END];
		byte[] diagnosisArr = new byte[Constants.MAXSIZE_DIAGNOSIS];
		
		for (int i = 0; i < Constants.MAXSIZE_START; i++) {
			startArr[i]=arr[i];
		}
		
		for (int i = 0; i < Constants.MAXSIZE_END; i++) {
			endArr[i]=arr[Constants.MAXSIZE_START+i];
		}
		
		for (int i = 0; i < Constants.MAXSIZE_DIAGNOSIS; i++) {
			diagnosisArr[i]=arr[Constants.MAXSIZE_START+Constants.MAXSIZE_END+i];
		}
		
		return new Hospitalization(ScalarConverter.toDate(startArr), ScalarConverter.toDate(endArr), ScalarConverter.toString(diagnosisArr));
	}
	
	public byte[] toByte(Hospitalization h) {
		byte[] startArr = ScalarConverter.toByteArr(h.getStart());
		byte[] endArr = ScalarConverter.toByteArr(h.getEnd());
		byte[] diagnosisArr = ScalarConverter.toByteArr(h.getDiagnosis(),Constants.MAXSIZE_DIAGNOSIS);
		byte[] res = new byte[Constants.MAXSIZE_DIAGNOSIS+Constants.MAXSIZE_END+Constants.MAXSIZE_START];
		
		for (int i = 0; i < Constants.MAXSIZE_START; i++) {
			res[i]=startArr[i];
		}
		
		for (int i = 0; i < Constants.MAXSIZE_END; i++) {
			res[Constants.MAXSIZE_START+i]=endArr[i];
		}
		
		for (int i = 0; i < Constants.MAXSIZE_DIAGNOSIS; i++) {
			res[Constants.MAXSIZE_START+Constants.MAXSIZE_END+i]=diagnosisArr[i];
		}
		return res;
	}
}
