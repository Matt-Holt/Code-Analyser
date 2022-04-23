package errors;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import code_smells.CodeSmells;
import other.Code;

public class ErrorReader {
	private File file;
	private ArrayList<CodeSmells> errors = new ArrayList<CodeSmells>();
	
	public ErrorReader(File file) {
		this.file = file;
		checkForErrors();
	}
	
	/**
	 * Reads through file and checks for syntax
	 * errors.
	 * 
	 * @param nothing
	 * @return nothing
	 */
	private void checkForErrors() {
		Code code = new Code();
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				code.countValidCurlyBrackets(line);
				}
			int n = code.getClosedCurlyBrackets() - code.getOpenCurlyBrackets();

			//More open brackets than closed
			if (n < 0) {
				String name = "Missing closing bracket";
				String desc = "The class '"+ file.getName() + "' is missing a closing curcly bracket. "
						+ "This ruins the structure of the code and the code cannot compile with this error.";
				createError(name, desc);	
			}
			//More closed brackets than open
			else if (n > 0) {
				String name = "Extra closing bracket";
				String desc = "The class '"+ file.getName() + "' has a closing curcly bracket that does not have a "
						+ "matching open curly bracket. This ruins the structure of the code and the code cannot "
						+ "compile with this error.";
				createError(name, desc);
			}
			scanner.close();
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/*
	 * Creates error object and adds it to array
	 * 
	 * @param errorName Str, errorDesc Str, line int
	 * @return nothing
	 */
	private void createError(String name, String desc) {
		CodeSmells error = new CodeSmells(name, desc, "Errors");
		errors.add(error);
	}
	
	//@return list of errors
	public ArrayList<CodeSmells> getErrors() {
		return errors;
	}
}
