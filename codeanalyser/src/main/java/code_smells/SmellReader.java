package code_smells;

import java.io.File;
import java.util.ArrayList;
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
	 * Passes file to all other smell files to analyse them
	 * for those types of smells.
	 * 
	 * @param line
	 * @return nothing
	 */
	public void readFile() {
		new Bloaters(metrics, this);
		new Dispensables(file, metrics, this);
		//ChangePreventer(file, this);
		new Couplers(file, metrics, this);
		new ObjectAbusers(file, this);
	}
	
	protected void createSmell(String name, String desc, String type) {
		CodeSmells smell = new CodeSmells(name, desc, type);
		smells.add(smell);
	}
	
	//@return all code smells
	public ArrayList<CodeSmells> getSmells() {
		return smells;
	}
}
