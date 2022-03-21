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
		Bloaters bloaters = new Bloaters(metrics, this);
		Dispensables dispensables = new Dispensables(file, metrics, this);
		//ChangePreventer cPreventers = new ChangePreventer(file);
		//Couplers couplers = new Couplers(file);
		//ObjectAbusers objAbusers = new ObjectAbusers(file);
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
