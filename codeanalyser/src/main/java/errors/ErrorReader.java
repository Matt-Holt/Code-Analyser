package errors;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import code_smells.CodeSmells;
import other.Code;

public class ErrorReader {
	private File file;
	private ArrayList<Error> errors = new ArrayList<Error>();
	
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

			if (code.getClosedCurlyBrackets() != code.getOpenCurlyBrackets()) {
				String name = "Missing semi-colon";
				String desc = "";
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
		Error error = new Error(name, desc);
		errors.add(error);
	}
	
	//@return list of errors
	public ArrayList<Error> getErrors() {
		return errors;
	}
}
