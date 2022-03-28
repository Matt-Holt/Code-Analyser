package other;

public class Code {

	private int openCurlyBrackets = 0;
	private int closedCurlyBrackets = 0;
	
	public void countValidCurlyBrackets(String line) {
		boolean valid = true;
		for (int i = 0; i < line.length(); i++) {
			char ch = line.charAt(i);
			if (ch == '\"' || ch == '\'')
				valid = !valid;

			if (ch == '{' && valid)
				openCurlyBrackets++;
			
			if (ch == '}' && valid)
				closedCurlyBrackets++;
		}
	}
	
	/**
	 * Checks if char is in string and not surrounded by "" or ''
	 * 
	 * @param String line, char c
	 * @return boolean
	 */
	public boolean containsValidChar(String line, char c) {
		boolean valid = true;
		for (int i = 0; i < line.length(); i++) {
			char ch = line.charAt(i);
			if (ch == '\"' || ch == '\'')
				valid = !valid;
			
			if (ch == c && valid)
				return true;
		}
		return false;
	}
	
	public boolean isOutsideMethod() {
		return openCurlyBrackets - 1 == closedCurlyBrackets;
	}

	public int getOpenCurlyBrackets() {
		return openCurlyBrackets;
	}

	public int getClosedCurlyBrackets() {
		return closedCurlyBrackets;
	}
}
