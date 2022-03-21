package code_smells;

public class CodeSmells {

	protected String smellName;
	protected String smellDesc;
	protected String smellType;

	//Constructor
	public CodeSmells(String name, String desc, String type) {
		smellName = name;
		smellDesc = desc;
		smellType = type;
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