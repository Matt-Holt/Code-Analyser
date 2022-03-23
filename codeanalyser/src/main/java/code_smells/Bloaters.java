package code_smells;

import java.io.File;
import java.util.Arrays;

import metrics.FieldMetrics;
import metrics.MethodMetrics;
import metrics.Metrics;

/*
 * Bloaters consist of:
 * - long methods
 * - long classes
 * - long parameter list in method
 * - primitive obsession (too many primitive data types)
 * - Data clumps (different parts of code contain same lines)
 */
public class Bloaters {
	private final String[] primatives = {"int", "byte", "string", "short", "long", "float", "double", "boolean", "char"};
	private final String type = "Bloaters";
	private Metrics metrics;
	SmellReader sReader;
	
	public Bloaters(Metrics metrics, SmellReader sReader) {
		this.metrics = metrics;
		this.sReader = sReader;
		checkForSmells();
	}
	
	private void checkForSmells() {
		if (metrics.getTotalLines() >= 900 || metrics.getMethods().size() >= 20 || metrics.getFields().size() >= 20) {
			String name = "Long Class";
			String desc = "The class '" + metrics.getFileName() + "' has " + metrics.getTotalLines() + 
					" lines, " + metrics.getMethods().size() + " methods, and " +
					metrics.getFields().size() + " fields. It may be difficult to refactor" +
					" this code in the future, consider shortening it.";
			sReader.createSmell(name, desc, type);
		}
		
		
		
		checkFields();
		checkMethods();
	}
	
	/*
	 * Checks all fields for their type to count primitive obsession 
	 * 
	 * @param nothing
	 * @return nothing
	 */
	private void checkFields() {
		int count = 0;
		for (FieldMetrics f : metrics.getFields()) {
			String type = f.getType().toLowerCase();
			boolean isPrimative = Arrays.stream(primatives).anyMatch(type::contains);
			
			if (isPrimative)
				count++;
		}
		
		if (count >= 10) {
			String name = "Primative obsession";
			String desc = "The class '" + metrics.getFileName() + "' uses a lot of " + 
			"primative data types that could have been represented in the form of small objects";
			sReader.createSmell(name, desc, type);
		}
	}
	
	/*
	 * Checks all methods to see if they are too long,
	 * or have too many arguments.
	 * 
	 * @param nothing
	 * @return nothing
	 */
	private void checkMethods()
	{
		for (MethodMetrics m : metrics.getMethods()) {
			//Checks if methods has too many lines of code
			if (m.getNumOfLines() >= 30) {
				String name = "Long Method";
				String desc = "The method '" + m.getMethodName() + "' in '" + metrics.getFileName() +
						"' consists of " + m.getNumOfLines() + " lines of code. Maybe consider splitting " +
						"it into different methods.";
				sReader.createSmell(name, desc, type);
			}
			
			//Checks if method has long parameter list
			if (m.getArguments().length >= 5) {
				String name = "Large Parameter List";
				String desc = "The method " + m.getMethodName() + " in '" + metrics.getFileName() + "' has " +
				m.getArguments().length + " parameters. This may make it harder to understand the method.";
				sReader.createSmell(name, desc, type);
			}
		}
	}
}