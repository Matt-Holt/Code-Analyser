package metrics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Metrics {	
	private String fileName = "";
	private String type = "";
	private String fileSize = "0B";
	private int totalLines;
	private int commentLines;
	private String averageMethodComplexity;
	private ArrayList<FieldMetrics> fields = new ArrayList<FieldMetrics>();
	private ArrayList<MethodMetrics> methods = new ArrayList<MethodMetrics>();
	public ArrayList<String> allLines = new ArrayList<String>();
	private boolean inMethod = false;
	private MethodMetrics currentMethod;
	private int methodStartLine = -1;
	public int openCurlyBrackets = 0;
	public int closedCurlyBrackets = 0;
	
	/*
	 * Constructor for metrics
	 * @param fileName
	 */
	public Metrics(String fileName, long newSize) {
		fileName = fileName.replace(".java", "");
		this.fileName = fileName;

		//Represents size in largest amount
		double size = newSize;
		int timesDivided = 0;
		while (size / 1024 >= 1) {
			size /= 1024;
			timesDivided++;
		}
		
		String sizeAmount = "";
		switch(timesDivided)
		{
			case 0:
				sizeAmount = " Bytes";
				break;
			case 1:
				sizeAmount = "KB";
				break;
			case 2:
				sizeAmount = "MB";
				break;
			case 3:
				sizeAmount = "GB";
				break;
			default:
				sizeAmount = "TB";
				break;
		}
		
		DecimalFormat df = new DecimalFormat("#.#");
		fileSize = (Double.parseDouble(df.format(size))) + sizeAmount;
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
			return;
		}
		
		//If method continues, it is a normal line		
		if (t.length() > 0) {
			allLines.add(t);
			checkForField(t);
		}
		
		//Sets type of file
		if (type.length() <= 0) {
			String[] lineWords = line.toLowerCase().split(" ");
			for (int i = 0; i < lineWords.length; i++) {
				String word = lineWords[i];
				if (word.equals("class") || word.equals("enum") || word.equals("interface") || word.equals("module")) {
					type = word.toLowerCase();
					break;
				}
			}
		}

		//Outside of methods but within' class
		if (openCurlyBrackets - 1 == closedCurlyBrackets) {
			//Method
			if (containsValidChar(t, ')') && !containsValidChar(t, '=')) {
				String methodLine = mergeLines(t);
				if (methodLine.length() > 0) {
					if (!containsValidChar(methodLine, '=')) {
						//Add method
						MethodMetrics m = new MethodMetrics(methodLine);
						methods.add(m);
						currentMethod = m;
						inMethod = true;	
					}
					else {
						//Add field
						FieldMetrics f = new FieldMetrics(methodLine);
						fields.add(f);	
					}
				}
			}
			
			//Field
			else if (containsValidChar(t, ';')) {
				String fieldLine = mergeLines(t);
				if (fieldLine.length() > 0) {
					FieldMetrics f = new FieldMetrics(fieldLine);
					fields.add(f);	
				}
			}
		}
		
		if (containsValidChar(t, '{')) {
			if (inMethod && openCurlyBrackets - 1 == closedCurlyBrackets)
				methodStartLine = totalLines + 1;

			countValidCurlyBrackets(t);
		}
		
		if (containsValidChar(t, '}')) {
			countValidCurlyBrackets(t);
			
			/**
			 * Checks if current line is outside of method if it is
			 * find difference between method start line and current line
			 */
			if (inMethod && openCurlyBrackets - 1 == closedCurlyBrackets) {
				int methodLines = totalLines - methodStartLine;
				currentMethod.setNumOfLines(methodLines);
				methodStartLine = -1;
				inMethod = false;
			}
		}
	}
	
	private void countValidCurlyBrackets(String line) {
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
	 * Checks line for any fields
	 * @param line
	 * @return nothing
	 */
	private void checkForField(String line) {
		for (int i = 0; i < fields.size(); i++) {			
			FieldMetrics field = fields.get(i);
			String fieldName = field.getFieldName();	
			
			//Continues to next iteration in loop if field is not in line
			if (!line.contains(fieldName))
				continue;
			
			int startPos = line.indexOf(fieldName) - 1;
			int endPos = line.indexOf(fieldName) + fieldName.length();
			char before = ' ';
			char after = ' ';
			
			//Sets char for before and after field
			if (startPos >= 0) {
				before = line.charAt(startPos);
				
				if (endPos < line.length())
					after = line.charAt(endPos);
			}
			
			//Increments use counter
			if (!Character.isLetter(before) && !Character.isLetter(after))
				field.setUseCount(field.getUseCount() + 1);
		}
	}
	
	/**
	 * Checks if char is in string and not surrounded by "" or ''
	 * 
	 * @param String line, char c
	 * @return boolean
	 */
	private boolean containsValidChar(String line, char c) {
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

	/**
	 * Merges any keyword spilling onto other lines
	 * into one line
	 * 
	 * @param exception
	 * @return
	 */
	private String mergeLines(String exception) {
		String line = "";
		for (int i = allLines.size() - 1; i >= 0; i--)
		{
			String l = allLines.get(i);
			
			if (!l.equals(exception)) {
				if (containsValidChar(l, '}') || containsValidChar(l, '{') || containsValidChar(l, ';'))
					return line;
			}
			
			line = l + " " + line;
		}
		return "";
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
	 * @return size
	 */
	public String getSize() {
		return fileSize;
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
	public ArrayList<FieldMetrics> getFields() {
		return fields;
	}
	/**
	 * @return array of methods
	 */
	public ArrayList<MethodMetrics> getMethods() {
		return methods;
	}
	/**
	 * @return average method complexity
	 */
	public String getAverageMethodComplexity() {
		return averageMethodComplexity;
	}	
}