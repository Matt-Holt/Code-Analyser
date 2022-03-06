package code_smells;

import java.io.File;

import metrics.Metrics;

public class CodeSmells {

	protected String smellName;
	protected String smellDesc;
	protected String smellType;

	//Constructor
	public CodeSmells(String name, String desc, String type) {
		smellName = name;
		smellDesc = desc;
	}

	public String getSmellName() {
		return smellName;
	}
	
	public String getSmellDesc() {
		return smellDesc;
	}
	
	public String getSmellType() {
		return smellType;
	}
}