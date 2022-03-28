package errors;

public class Error {
	String errorName;
	String errorDesc;
		
	//Contstructor
	public Error(String errorName, String errorDesc) {
		this.errorName = errorName;
		this.errorDesc = errorDesc;
	}

	public String getErrorName() {
		return errorName;
	}
	
	public String getErrorDesc() {
		return errorDesc;
	}
}
