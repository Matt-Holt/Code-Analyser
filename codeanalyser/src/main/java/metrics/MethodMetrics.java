package metrics;

public class MethodMetrics {
	private String methodName;
	private int numOfLines;
	private String returnType;
	private String[] arguments;
	
	//Constructor for this class
	public MethodMetrics(String methodLine) {
		int OpenBracketPos = methodLine.indexOf("(");
		int closedBracketPos = methodLine.indexOf(")");
		
		String argLine = methodLine.substring(OpenBracketPos + 1, closedBracketPos);
		methodLine = methodLine.substring(0, OpenBracketPos);
		arguments = argLine.split(",");
		
		//Separates line into keywords
		String[] keyWords = methodLine.split(" ");
		methodName = keyWords[keyWords.length - 1];
		returnType = keyWords[keyWords.length - 2];
	}
	
	//@param total num of lines
	public void setNumOfLines(int total) {
		numOfLines = total;
	}
	
	 //@return method name string
	public String getMethodName() {
		return methodName;
	}

	 //@return amount of lines integer
	public int getNumOfLines() {
		return numOfLines;
	}

	 //@return return type string
	public String getReturnType() {
		return returnType;
	}

	 //@return method arguments string array list
	public String[] getArguments() {
		return arguments;
	}
	
}
