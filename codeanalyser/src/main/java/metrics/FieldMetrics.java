package metrics;

public class FieldMetrics {
	private String fieldName;
	private String type;
	private int timesUsed;
	
	//Constructor for this class
	public FieldMetrics(String fieldLine) {
		int equalsPos = fieldLine.indexOf("=");
		if (equalsPos != -1)
			fieldLine = fieldLine.substring(0, equalsPos);
		
		String[] keyWords = fieldLine.split(" ");
		fieldName = keyWords[keyWords.length - 1];
		fieldName = fieldName.replace(";", "");
		type = keyWords[keyWords.length - 2];
	}
	
	//@return field name string
	public String getFieldName() {
		return fieldName;
	}

	//@return timesUsed integer
	public int getUseCount() {
		return timesUsed;
	}

	//@return field type string
	public String getType() {
		return type;
	}
	
	public void setUseCount(int timesUsed) {
		this.timesUsed = timesUsed;
	}
}
