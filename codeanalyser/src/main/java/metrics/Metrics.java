package metrics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import other.Code;

public class Metrics {	

	private String fileName = "";
	private String type = "";
	private String fileSize = "0 Bytes";
	private int totalLines;
	private int commentLines;
	private String averageMethodComplexity;
	private ArrayList<FieldMetrics> fields = new ArrayList<FieldMetrics>();
	private ArrayList<MethodMetrics> methods = new ArrayList<MethodMetrics>();
	public ArrayList<String> allCodeLines = new ArrayList<String>();
	private boolean inMethod = false;
	private MethodMetrics currentMethod;
	private int methodStartLine = -1;
	private Code code = new Code();
	
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
		switch(timesDivided) {
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
		
		if (methodStartLine >= 0)
			currentMethod.addCodeLine(t);
		
		//If method continues, it is a normal line		
		if (t.length() > 0) {
			allCodeLines.add(t);
			code.countFields(t, fields);
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
		if (code.isOutsideMethod()) {
			//Method
			if (code.containsValidChar(t, ')') && !code.containsValidChar(t, '=')) {
				String methodLine = mergeLines(t);
				if (methodLine.length() > 0) {
					if (!code.containsValidChar(methodLine, '=')) {
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
			else if (code.containsValidChar(t, ';')) {
				String fieldLine = mergeLines(t);
				if (fieldLine.length() > 0) {
					FieldMetrics f = new FieldMetrics(fieldLine);
					fields.add(f);	
				}
			}
		}
		
		if (code.containsValidChar(t, '{')) {
			if (inMethod && code.isOutsideMethod())
				methodStartLine = totalLines + 1;

			code.countValidCurlyBrackets(t);
		}
		
		if (code.containsValidChar(t, '}')) {
			code.countValidCurlyBrackets(t);
			
			/**
			 * Checks if current line is outside of method if it is
			 * find difference between method start line and current line
			 */
			if (inMethod && code.isOutsideMethod()) {
				int methodLines = totalLines - methodStartLine;
				currentMethod.setNumOfLines(methodLines);
				currentMethod.getCodeLines().remove(currentMethod.getCodeLines().size() - 1);
				methodStartLine = -1;
				inMethod = false;
			}
		}
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
		for (int i = allCodeLines.size() - 1; i >= 0; i--)
		{
			String l = allCodeLines.get(i);
			
			if (!l.equals(exception)) {
				if (code.containsValidChar(l, '}') || code.containsValidChar(l, '{') || code.containsValidChar(l, ';'))
					return line;
			}
			
			line = l + " " + line;
		}
		return "";
	}
	
	/**Sets type of file */
	public void setType(String type) {
		this.type = type;
	}
	
	/** @return className */
	public String getFileName() {
		return fileName;
	}
	/** @return type */
	public String getType() {
		return type;
	}
	/** @return size */
	public String getSize() {
		return fileSize;
	}
	/** @return total lines */
	public int getTotalLines() {
		return totalLines;
	}
	/** @return total comments */
	public int getCommentLines() {
		return commentLines;
	}
	/** @return array of variables */
	public ArrayList<FieldMetrics> getFields() {
		return fields;
	}
	/** @return array of methods */
	public ArrayList<MethodMetrics> getMethods() {
		return methods;
	}
	/** @return list of all lines */
	public ArrayList<String> getAllCodeLines() {
		return allCodeLines;
	}
	
	/** @return average method complexity */
	public String getAverageMethodComplexity() {
		return averageMethodComplexity;
	}	
}