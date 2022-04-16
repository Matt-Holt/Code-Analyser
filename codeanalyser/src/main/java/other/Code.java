package other;

import java.util.ArrayList;

import metrics.FieldMetrics;
import metrics.MethodMetrics;

public class Code {

	private int openCurlyBrackets = 0;
	private int closedCurlyBrackets = 0;
	
	/**
	 * Checks line for any fields
	 * @param line
	 * @return nothing
	 */
	public void countFields(String line, ArrayList<FieldMetrics> fields) {
		for (int i = 0; i < fields.size(); i++) {			
			FieldMetrics field = fields.get(i);
			String fieldName = field.getFieldName();
			
			if (containsValidWord(line, fieldName))
				field.setUseCount(field.getUseCount() + 1);
		}
	}
	
	/**
	 * Checks if current line contains method
	 * 
	 * @param line string & method array
	 * @return method Metrics
	 */
	public MethodMetrics extractMethod(String line, ArrayList<MethodMetrics> methods) {
		for (int i = 0; i < methods.size(); i++) {	
			MethodMetrics method = methods.get(i);
			String methodName = method.getMethodName();
			
			//Returns method if it is used
			if (containsValidWord(line, methodName))
				return method;
		}
		return null;
	}
	
	/**
	 * Checks to see if the line contains the given word
	 * 
	 * @param String line, String word
	 * @return boolean
	 */
	public boolean containsValidWord(String line, String word) {
		//Returns false if word is not found
		if (!line.contains(word))
			return false;
		
		int startPos = line.indexOf(word) - 1;
		int endPos = line.indexOf(word) + word.length();
		char before = ' ';
		char after = ' ';
		
		//Sets char for before and after word
		if (startPos >= 0) {
			before = line.charAt(startPos);
			
			if (endPos < line.length())
				after = line.charAt(endPos);
		}

		//char before and after word is NOT a letter
		if (!Character.isLetter(before) && !Character.isLetter(after)) {
			if (before != '\'' && before != '\"' && after != '\'' && after != '\"')
				return true;
		}
		
		return false;
	}
	
	/**
	 * This method counts all valid instances of { and }
	 * 
	 * @param line
	 * @return nothing
	 */
	public void countValidCurlyBrackets(String line) {
		boolean valid = true;
		for (int i = 0; i < line.length(); i++) {
			char ch = line.charAt(i);
			if (ch == '\"' || ch == '\'')
				valid = !valid;

			if (ch == '{' && valid)
				openCurlyBrackets++;
			
			if (ch == '}' && valid)
				closedCurlyBrackets++;
		}
	}
	
	/**
	 * Checks if the line is a valid statement
	 * 
	 * @param line
	 * @return boolean isValid
	 */
	public boolean isValidStatement(String line) {
		for (int i = 0; i < line.length(); i++) {
			Character c = line.charAt(i);
			if (Character.isLetter(c))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if char is in string and not surrounded by "" or ''
	 * 
	 * @param String line, char c
	 * @return boolean
	 */
	public boolean containsValidChar(String line, char c) {
		boolean valid = true;
		for (int i = 0; i < line.length(); i++) {
			char ch = line.charAt(i);
			if (ch == '\"' || ch == '\'')
				valid = !valid;
			
			if (ch == c && valid)
				return true;
		}
		return false;
	}	
	
	/**@return boolean*/
	public boolean isOutsideMethod() {
		return openCurlyBrackets - 1 == closedCurlyBrackets;
	}

	/**@return int*/
	public int getOpenCurlyBrackets() {
		return openCurlyBrackets;
	}

	/**@return int*/
	public int getClosedCurlyBrackets() {
		return closedCurlyBrackets;
	}
}
