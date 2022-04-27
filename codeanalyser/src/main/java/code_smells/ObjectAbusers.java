package code_smells;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import metrics.MethodMetrics;
import metrics.Metrics;
import other.Code;

/*
 * Object orient abuses consist of:
 * - complex switch statement
 * - temporary fields
 * - refused bequest (sub-class only uses some properties & methods of super)
 * - classes performing identical functions with different method names
 */
public class ObjectAbusers {
	
	private File file;
	Metrics metrics;
	ArrayList<Metrics> allMetrics;
	SmellReader sReader;
	String type = "Object Orient Abusers";
	Code code = new Code();
	
	//Constructor
	public ObjectAbusers(File file, Metrics metrics, ArrayList<Metrics> allMetrics, SmellReader sReader) {
		this.file = file;
		this.metrics = metrics;
		this.sReader = sReader;
		this.allMetrics = allMetrics;
		allMetrics.remove(metrics);
		checkForSmells();
	}
	
	private void checkForSmells() {		
		boolean inStatement = false;
		int numOfCases = 0;
		int o = 0;
		int c = 0;
		
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				
				if (line.length() <= 0)
					continue;
				
				List<String> list = Arrays.asList(line.split(" "));
				
				if (code.containsValidWord(line, "switch")) {					
					numOfCases = 0;
					inStatement = true;
				}
				
				if (inStatement) {					
					if (code.containsValidWord(line, "case") || code.containsValidWord(line, "default"))
						numOfCases++;

					if (code.containsValidChar(line, '{'))
						o++;

					if (code.containsValidChar(line, '}')) {
						c++;
						
						if (o == c) {
							if (numOfCases >= 4) {
								String name = "Complex statement";
								String desc = "The class '" + file.getName() + "' has a complex switch/if statement" + 
									" consisting of " + numOfCases + " cases. A complex switch or series of if else statements " +
									"may cause confusion when refactoring in the future.";
								sReader.createSmell(name, desc, type);	
							}
							inStatement = false;
						}
					}
				}
			}
			
			for (int i = 0; i < metrics.getMethods().size(); i++) {			
				if (metrics.getMethods().get(i).getCodeLines().size() <= 3)
					continue;
				
				compareWithClasses(metrics.getMethods().get(i));
			}
			
			scanner.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * compares contents of method with contents of method from all
	 * other classes
	 * 
	 * @param nothing
	 * @return nothing
	 */
	private void compareWithClasses(MethodMetrics method) {		
		ArrayList<String> currentMethodLines = method.getCodeLines();
		
		for (int i = 0; i < allMetrics.size(); i++) {
			Metrics otherMetrics = allMetrics.get(i);
			for (int j = 0; j < otherMetrics.getMethods().size(); j++) {
				MethodMetrics otherMethod = otherMetrics.getMethods().get(j);
				ArrayList<String> methodLines = otherMethod.getCodeLines();
				
				if (currentMethodLines.equals(methodLines)) {
					String name = "Duplicate Method";
					String desc = "The method '" + method.getMethodName() + "' in the class '" + metrics.getFileName() +
							"' contains identical code to the method '" + otherMethod.getMethodName() + "' in the class '" +
							otherMetrics.getFileName() + "'.";
					sReader.createSmell(name, desc, type);
				}
					
			}
		}
	}
}