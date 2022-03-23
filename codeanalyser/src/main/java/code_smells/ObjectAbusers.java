package code_smells;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import metrics.Metrics;

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

				if (list.contains("switch") || list.contains("if"))
					inStatement = true;
				
				if (inStatement) {
					if (list.contains("case") || list.contains("if") || list.contains("else") || list.contains("default"))
						numOfCases++;
				}
			}
			
			if (numOfCases >= 4) {
				String name = "Complex statement";
			String desc = "The class '" + file.getName() + "' has a complex switch/if statement" + 
					" consisting of " + numOfCases + " cases. A complex switch or series of if else statements " +
					"may cause confusion when refactoring in the future.";
			sReader.createSmell(name, desc, type);
			}
			
			scanner.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
}