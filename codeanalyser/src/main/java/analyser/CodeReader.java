package analyser;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import code_smells.*;
import metrics.Metrics;

/*
 * Handles the java files and sends them to the code smells,
 * errors and metrics class to be analysed
 */
public class CodeReader {
	ArrayList<File> filesInDirectory = new ArrayList<File>();
	ArrayList<Metrics> allMetrics = new ArrayList<Metrics>();
	ArrayList<CodeSmells> allSmells = new ArrayList<CodeSmells>();
	
	/**
	 * Adds file to the filesInDirectory array
	 * 
	 * @param file
	 * @return nothing
	 */
	public void addFile(File file) {
		filesInDirectory.add(file);
	}
	
	/**
	 * A recursive method that goes through all paths of
	 * a directory and adds every java file to the filesInDirectory
	 * array
	 * 
	 * @param path
	 * @return nothing
	 */
	public void addFromDirectory(String path) {
		//directoryPath = path;
		File file = new File(path);
		String[] allFiles = file.list();
		
		if (allFiles != null) {

			for (String fileName : allFiles)
			{
				int i = fileName.indexOf(".");
				String ext = "directory";
				if (i >= 0)
					ext = fileName.substring(i + 1);
				
				//if Java file, add to list
				if (ext.equalsIgnoreCase("java"))
				{
					File newFile = new File(path + "\\" + fileName);
					filesInDirectory.add(newFile);
				}
				//If directory, get it's contents by looping again
				else if (ext.equalsIgnoreCase("directory"))
					addFromDirectory(path + "\\" + fileName);
			}
		}
	}
	
	/**
	 * Reads an entire file that is sent to it
	 * with a scanner
	 * 
	 * @param file
	 * @return nothing
	 */
	private void readFile(File file) {
		try {
			Scanner scanner = new Scanner(file);
			Metrics metrics = new Metrics(file.getName(), file.length());
			
			//Sends each line to the metrics and smells class to be read
			while (scanner.hasNextLine()) {
				metrics.readLine(scanner.nextLine());
			}

			//Reads file for smells
			SmellReader smellReader = new SmellReader(metrics, file);
			
			for (int i = 0; i < smellReader.getSmells().size(); i++) {
				CodeSmells smell = smellReader.getSmells().get(i);
				System.out.println(smell.getSmellName() + ", " + smell.getSmellType());
				System.out.println(smell.getSmellDesc());
				System.out.println();
			}
			
			allMetrics.add(metrics);
			scanner.close();
		}
		catch (Exception e) {
			JOptionPane alert = new JOptionPane();
			alert.showMessageDialog(alert, e);
			System.out.println(e);
		}
	}
	
	/**
	 * Calls readFile method for all files in array
	 * 
	 * @param nothing
	 * @return nothing
	 */
	public void readAllFiles() {
		for (File file : filesInDirectory) {
			readFile(file);
		}
	}
	
	/**
	 * Prints all file names if the list contains any
	 * 
	 * @param nothing
	 * @return nothing
	 */
	private void printFileNames() {
		if (filesInDirectory.size() >= 0) {
			System.out.println("This directory contains the following java files:");
			
			for (int i = 0; i < filesInDirectory.size(); i++)
				System.out.println(i+1 + ". " + filesInDirectory.get(i).getName());	
		}
		else {
			System.out.println("There are no java files here...");
		}
	}
	
	/**
	 * Clears all uploaded files, metrics and smells
	 * 
	 * @param nothing
	 * @return nothing
	 */
	public void clearFiles() {
		filesInDirectory.clear();
		allMetrics.clear();
		allSmells.clear();
	}

	/**
	 * @return Array list of java file
	 */
	public ArrayList<File> getAllFiles() {
		return filesInDirectory;
	}
	/**
	 * @return Array list of metrics
	 */
	public ArrayList<Metrics> getAllMetrics() {
		return allMetrics;
	}
	/**
	 * @return Array list of smells
	 */
	public ArrayList<CodeSmells> getAllSmells() {
		return allSmells;
	}
}