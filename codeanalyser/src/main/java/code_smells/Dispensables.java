package code_smells;

import java.io.File;
import java.util.ArrayList;

import metrics.FieldMetrics;
import metrics.Metrics;
import other.Code;

/*
 * Dispensables consist of:
 * - Unnecessary explanatory comments
 * - Duplicate code
 * - Lazy class (class that doesn't do enough to warrant it's creation)
 * - Data class (only used to store fields, and getter/setters)
 * - Dead code (code that isn't used)
 * - speculative generality (unused class)
 */
public class Dispensables {

	private File file;
	private Metrics metrics;
	private SmellReader sReader;
	private Code code = new Code();
	String type = "Dispensables";
	
	//Constructor
	public Dispensables(File file, Metrics metrics, SmellReader sReader) {
		this.file = file;
		this.metrics = metrics;
		this.sReader = sReader;
		checkForSmells();
	}
	
	private void checkForSmells() {
		//Checks for any unused fields
		checkFields();
		//Checks for duplicate code
		checkForDuplicates();
		
		//Checks if class is necessary
		if (metrics.getAllCodeLines().size() <= 10) {
			String name = "Lazy class";
			String desc = "The class '" + metrics.getFileName() + "' does not do very much. " +
			"It may be better to delete this class.";
			sReader.createSmell(name, desc, type);
		}

		//Checks if class has too many comments
		float comments = metrics.getCommentLines();
		float allLines = metrics.getTotalLines();
		float total = comments / allLines;

		//Comments take up 50% of all lines
		if (total >= 0.5f) {
			String name = "Excessive comments";
			String desc = "Atleast 50% of the class '" + metrics.getFileName() +
			"' is made up comments. If the class requires all those comments to be " +
			"understood then it should be streamlined.";
			sReader.createSmell(name, desc, type);
		}
	}
	
	/**
	 * Checks the fields for any smells
	 * 
	 * @param nothing
	 * @return nothing
	 */
	private void checkFields() {
		for (FieldMetrics f : metrics.getFields()) {
			if (f.getUseCount() == 0) {
				String name = "Unused field";
				String desc = "The field '" + f.getFieldName() + "' from the class '" +
				metrics.getFileName() + "' is unused. It may as well be removed.";
				sReader.createSmell(name, desc, type);
			}
		}
	}
	
	private void checkForDuplicates() {
		ArrayList<String> lines = metrics.getAllCodeLines();
		lines.sort(null);
		boolean isDuplicate = false;
		
		for (int i = 1; i < lines.size(); i++) {
			String current = lines.get(i);
			String previous = lines.get(i - 1);
			
			if (code.isValidStatement(previous) && code.isValidStatement(current)) {
				if (current.equalsIgnoreCase(previous) && (current.split(".").length > 1 || current.split(" ").length > 1))
					isDuplicate = true;
			}
		}
		
		if (isDuplicate) {
			String name = "Duplicate code";
			String desc = "The class '" + metrics.getFileName() + "' contains duplicate code. " +
			"It would be more efficient if there was extracted into a method.";
			sReader.createSmell(name, desc, type);
		}
	}
}