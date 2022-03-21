package code_smells;

import java.io.File;

import metrics.FieldMetrics;
import metrics.Metrics;

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
	
	//Constructor
	public Dispensables(File file, Metrics metrics, SmellReader sReader) {
		this.file = file;
		this.metrics = metrics;
		this.sReader = sReader;
		checkForSmells();
	}
	
	private void checkForSmells() {
		String type = "Dispensables";
		//Checks for any unused fields
		for (FieldMetrics f : metrics.getFields()) {
			if (f.getUseCount() == 0) {
				String name = "Unused field";
				String desc = "The field '" + f.getFieldName() + "' is unused. " +
						"It may as well be removed.";
				sReader.createSmell(name, desc, type);
			}
		}
	}
}