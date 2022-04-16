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
	
	/**@return string*/
	public String getSmellName() {
		return smellName;
	}

	/**@return string*/
	public String getSmellDesc() {
		return smellDesc;
	}

	/**@return string*/
	public String getSmellType() {
		return smellType;
	}
}