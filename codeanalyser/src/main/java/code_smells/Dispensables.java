package code_smells;

import java.io.File;

import metrics.Metrics;

/*
 * Dispensables consist of:
 * - Unnecessary explanatory comments
 * - Duplicate code
 * - Lazy class (class that doesn't do enough to warrant it's creation)
 * - Data class (only used to store fields, and getter/setters)
 * - Dead code (code that isn't called)
 * - speculative generality (unused class)
 */
public class Dispensables {

	public Dispensables(Metrics metrics) {
		
	}
}