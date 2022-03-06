package code_smells;

import java.io.File;
import java.util.ArrayList;

import metrics.MethodMetrics;
import metrics.Metrics;

/*
 * Reads the file for any smells and
 * creates a smell object for each
 */
public class SmellReader {
	private ArrayList<CodeSmells> smells = new ArrayList<CodeSmells>();
	private Metrics metrics;
	private File file;
	
	//Constructor
	public SmellReader(Metrics metrics, File file) {
		this.metrics = metrics;
		this.file = file;
		readFile();
	}
	
	/**
	 * Read line and analyse it for it's smells
	 * 
	 * @param line
	 * @return nothing
	 */
	public void readFile() {
		checkForBloaters();
		Bloaters bloaters = new Bloaters(metrics);
		Dispensables dispensables = new Dispensables(file);
		ChangePreventer cPreventers = new ChangePreventer(file);
		Couplers couplers = new Couplers(file);
		ObjectAbusers objAbusers = new ObjectAbusers(file);
	}
	
	/*
	 * Checks for any bloater code smells
	 * 
	 *@param nothing
	 *@return nothing 
	 */
	public void checkForBloaters() {
		String type = "Bloaters";
		if (metrics.getTotalLines() >= 900 || metrics.getMethods().size() >= 20 || metrics.getFields().size() >= 20) {
			String name = "Long Class";
			String desc = "This class has " + metrics.getTotalLines() + " lines, " +
					metrics.getMethods().size() + " methods, and " +
					metrics.getFields().size() + " fields. It may be difficult to refactor" +
					" this code in the future, consider shortening it.";
			createSmell(name, desc, type);
		}
		
		for (MethodMetrics m : metrics.getMethods()) {
			//Checks if methods has too many lines of code
			if (m.getNumOfLines() >= 30) {
				String name = "Long Method";
				String desc = "The method '" + m.getMethodName() + "' consists of " +
						m.getNumOfLines() + " lines of code. Maybe consider splitting" +
						"it into different methods.";
				createSmell(name, desc, type);
			}
			
			//Checks if method has long parameter list
			if (m.getArguments().length >= 5) {
				String name = "Large Parameter List";
				String desc = "The method " + m.getMethodName() + " has " + m.getArguments().length +
						" parameters. This may make it harder to understand the method.";
				createSmell(name, desc, type);
			}
		}
	}
	
	private void createSmell(String name, String desc, String type) {
		CodeSmells smell = new CodeSmells(name, desc, type);
		smells.add(smell);
	}
	
	//@return all code smells
	public ArrayList<CodeSmells> getSmells() {
		return smells;
	}
}
