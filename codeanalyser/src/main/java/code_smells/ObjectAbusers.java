package code_smells;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
	SmellReader sReader;
	String type = "Object Orient Abusers";
	Code code = new Code();
	
	//Constructor
	public ObjectAbusers(File file, SmellReader sReader) {
		this.file = file;
		this.sReader = sReader;
		checkForSmells();
	}
	
	private void checkForSmells() {
		boolean inStatement = false;
		int numOfCases = 0;
		
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				List<String> list = Arrays.asList(line.split(" "));
				System.out.println(numOfCases);
				
				if (code.containsKeyWord(line, "switch")) {					
					numOfCases = 0;
					inStatement = true;
				}
				
				
				if (inStatement) {
					if (list.contains("case") || list.contains("default"))
						numOfCases++;
					
					if (list.contains("}")) {						
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
			
			scanner.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
}