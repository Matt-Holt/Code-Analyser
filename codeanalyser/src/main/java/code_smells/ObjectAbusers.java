package code_smells;

import java.io.File;

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
	
	//Constructor
	public ObjectAbusers(File file) {
		this.file = file;
	}
}