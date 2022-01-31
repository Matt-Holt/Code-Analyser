package metrics;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.control.TextArea;


public class Metrics {	
	private String fileName = "";
	private String type = "";
	private int totalLines;
	private int commentLines;
	//<field, times_used>
	private HashMap<String, Integer> fields = new HashMap<String, Integer>();
	//<method, lines_long>
	private HashMap<String, Integer> methods = new HashMap<String, Integer>();
	private String averageMethodComplexity;

	private boolean inMethod;
	private boolean inField;
	private String methodKey;
	private int startLine;
	private int openCurlyBrackets = 0;
	private int closedCurlyBrackets = 0;
	
	/*
	 * Constructor for metrics
	 * @param fileName
	 */
	public Metrics(String fileName) {
		fileName = fileName.replace(".java", "");
		this.fileName = fileName;
	}
	
	/**
	 * Read line and analyse it for it's metrics
	 * 
	 * @param line
	 * @return nothing
	 */
	public void readLine(String line) {
		String t = line.trim();
		totalLines++;
		
		//Is a comment
		if (t.startsWith("//") || t.startsWith("/*") || t.startsWith("*")) {
			commentLines++;
		}
		//Is a normal line
		else {
			
			//Sets type of file
			if (type.length() <= 0) {
				if (line.toLowerCase().contains(" class"))
					type = "Class";
				else if (line.toLowerCase().contains(" enum"))
					type = "Enum";
				else if (line.toLowerCase().contains(" interface"))
					type = "Interface";
				else if (line.toLowerCase().contains(" module"))
					type = "Module";
			}
			
			//Method
			if (t.contains("(") && t.contains(")")) {
				if (openCurlyBrackets - 1 == closedCurlyBrackets && !t.contains("=")) {
					String[] methodLine = t.split("\\(")[0].split(" ");
					String method = methodLine[methodLine.length - 1];
					methods.put(method, 1);
					
					//Application is now at start of method
					startLine = totalLines;
					methodKey = method;
					inMethod = true;
				}
			}
			
			//Variable
			if (t.length() > 0) {
				if (openCurlyBrackets - 1 == closedCurlyBrackets) {
					String field = "";
					if (t.contains("=")) {
						//Gets each word before the semicolon 
						String[] words = t.split("=")[0].split(" ");
						
						if (words.length > 1)
							field = words[words.length - 1];
					}
					else if (t.contains(";") && t.length() > 1) {
						String[] words = t.split(";")[0].split(" ");
						
						if (words.length > 1)
							field = words[words.length - 1];
					}
					
					//Adds to list if field is set
					if (field.length() > 0)
						fields.put(field, totalLines);
				}
			}
			
			if (t.contains("{"))
				openCurlyBrackets++;
			
			if (t.contains("}")) {
				closedCurlyBrackets++;
				
				/**
				 * Checks if current line is outside of method if it is
				 * find difference between method start line and current line
				 */
				if (inMethod && openCurlyBrackets - 1 == closedCurlyBrackets) {
					int methodLines = (totalLines - 1) - startLine;
					methods.put(methodKey, methodLines);
					inMethod = false;
				}
			}

			//If still in field and reaches end of it
			 if (inField && t.contains(";")) {
				 inField = false;
			 }
		}
	}

	/**
	 * @return className
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @return total lines
	 */
	public int getTotalLines() {
		return totalLines;
	}
	/**
	 * @return total comments
	 */
	public int getCommentLines() {
		return commentLines;
	}
	/**
	 * @return array of variables
	 */
	public HashMap<String, Integer> getFields() {
		return fields;
	}
	/**
	 * @return array of methods
	 */
	public HashMap<String, Integer> getMethods() {
		return methods;
	}
	/**
	 * @return average method complexity
	 */
	public String getAverageMethodComplexity() {
		return averageMethodComplexity;
	}	
}