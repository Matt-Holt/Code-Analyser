package errors;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import code_smells.CodeSmells;
import other.Code;

public class ErrorReader {
	private File file;
	private ArrayList<CodeSmells> errors = new ArrayList<CodeSmells>();
	private Code code = new Code();
	
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
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				code.countValidBrackets(line);
				}
			
			countBrackets();
			scanner.close();
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * Checks for missing, or extra brackets
	 */
	private void countBrackets() {
		int c = code.getClosedCurlyBrackets() - code.getOpenCurlyBrackets();
		int n = code.getClosedBrackets() - code.getOpenBrackets();
		int s = code.getClosedSquareBrackets() - code.getOpenSquareBrackets();
		System.out.println(c + ", " + n + ", " + s);
		
		//More open curly brackets than closed
		if (c < 0) {
			String name = "Missing { bracket";
			String desc = "The class '"+ file.getName() + "' is missing a closing curcly bracket. "
					+ "This ruins the structure of the code and the code cannot compile with this error.";
			createError(name, desc);	
		}
		//More closed curly brackets than open
		else if (c > 0) {
			String name = "unexpected } bracket";
			String desc = "The class '"+ file.getName() + "' has a closing curcly bracket that does not have a "
					+ "matching open curly bracket. This ruins the structure of the code and the code cannot "
					+ "compile with this error.";
			createError(name, desc);
		}
		//More open square brackets than closed
		if (s < 0) {
			String name = "Missing [ bracket";
			String desc = "The class '"+ file.getName() + "' is missing a closing square bracket. "
					+ "This ruins the structure of the code and the code cannot compile with this error.";
			createError(name, desc);	
		}
		//More closed square brackets than open
		else if (s > 0) {
			String name = "unexpected ] bracket";
			String desc = "The class '"+ file.getName() + "' has a closing square bracket that does not have a "
					+ "matching open square bracket. This ruins the structure of the code and the code cannot "
					+ "compile with this error.";
			createError(name, desc);
		}
		//More open brackets than closed
		if (n < 0) {
			String name = "Missing ( bracket";
			String desc = "The class '"+ file.getName() + "' is missing a closing bracket. "
					+ "This ruins the structure of the code and the code cannot compile with this error.";
			createError(name, desc);	
		}
		//More closed brackets than open
		else if (n > 0) {
			String name = "unexpected ) bracket";
			String desc = "The class '"+ file.getName() + "' has a closing bracket that does not have a "
					+ "matching open bracket. This ruins the structure of the code and the code cannot "
					+ "compile with this error.";
			createError(name, desc);
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
