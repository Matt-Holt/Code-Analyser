package metrics;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.control.TextArea;

public class Metrics {	
	private String fileName;
	private int totalLines;
	private int commentLines;
	//<field, times_used>
	private HashMap<String, Integer> fields = new HashMap<String, Integer>();
	//<method, lines_long>
	private HashMap<String, Integer> methods = new HashMap<String, Integer>();
	private String averageMethodComplexity;

	private boolean inMethod;
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
			//Method
			if (t.contains("(") && t.contains(")") && !t.contains("=")) {
				if (openCurlyBrackets - 1 == closedCurlyBrackets) {
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
			if (t.contains(";")) {
				if (openCurlyBrackets - 1 == closedCurlyBrackets) {
					String[] fieldLine = null;
					String field = "";
					
					if (t.contains("=")) {
						fieldLine = t.split("=")[0].split(" ");
						field = fieldLine[fieldLine.length - 1];
					}
					else {
						fieldLine = t.split(";")[0].split(" ");
						field = fieldLine[fieldLine.length - 1];
					}
					
					fields.put(field, 1);
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
		}
	}

	/**
	 * @return className
	 */
	public String getFileName() {
		return fileName;
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