package code_smells;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import metrics.Metrics;

/*
 * Reads the file for any smells and
 * creates a smell object for each
 */
public class SmellReader {
	private ArrayList<CodeSmells> smells = new ArrayList<CodeSmells>();
	private HashMap<File, Metrics> filesRead = new HashMap<File, Metrics>();
	private Metrics metrics;
	private ArrayList<Metrics> allMetrics;
	private File file;
	
	//Constructor
	public SmellReader(Metrics metrics, File file, ArrayList<Metrics> allMetrics) {
		this.metrics = metrics;
		this.file = file;
		this.allMetrics = allMetrics;
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
		new ChangePreventer(file, this);
		new Couplers(file, metrics, this);
		new ObjectAbusers(file, metrics, allMetrics, this);
		
		filesRead.put(file, metrics);
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
