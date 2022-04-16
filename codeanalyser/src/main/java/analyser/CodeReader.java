package analyser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import code_smells.*;
import errors.ErrorReader;
import errors.Error;
import metrics.Metrics;

/*
 * Handles the java files and sends them to the code smells,
 * errors and metrics class to be analysed
 */
public class CodeReader {
	ArrayList<File> filesInDirectory = new ArrayList<File>();
	ArrayList<Metrics> allMetrics = new ArrayList<Metrics>();
	ArrayList<CodeSmells> allSmells = new ArrayList<CodeSmells>();
	ArrayList<Error> allErrors = new ArrayList<Error>();
	
	/**
	 * Adds file to the filesInDirectory array
	 * 
	 * @param file
	 * @return nothing
	 */
	public void addFile(File file) {
		if (file.getName().equalsIgnoreCase("module-info.java"))
			return;
		
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
					addFile(newFile);
				}
				//If directory, get it's contents by looping again
				else if (ext.equalsIgnoreCase("directory"))
					addFromDirectory(path + "\\" + fileName);
			}
		}
	}

	
	/**
	 * A recursive method that goes through all paths of
	 * a ones github directory creating java files based on the
	 * content there
	 * 
	 * @param path
	 * @return nothing
	 * */
	public void addFromGithub(String path) throws IOException {
		final Document doc = Jsoup.connect("https://github.com/" + path).get();
		Elements elements = doc.getElementsByClass("js-details-container Details");
		elements = elements.select("a");
		elements.select("js-navigation-open Link--primary");
		    
		for (int i = 0; i < elements.size(); i++) {
		 Element e = elements.get(i);
			    
		 if (!e.className().equals("js-navigation-open Link--primary"))
			 continue;
			    
			String href = e.attr("href");
			int j = e.text().lastIndexOf(".");
			String ext = "directory";
				
			//Directories will return -1 since there's no dots
			if (j >= 0)
				ext = e.text().substring(j + 1);
			
			if (ext.equalsIgnoreCase("java")) {
				createFileFromHref(href, e.text());
			}
			else if (ext.equalsIgnoreCase("directory")) {
				addFromGithub(href);
			}
		}
	}
	
	/**
	 * Creates a file from link to github file
	 * 
	 * @param href
	 * @return nothing
	 */
	private void createFileFromHref(String href, String name) {
		try {
			File file = File.createTempFile(name, ".java");
			PrintWriter w = new PrintWriter(file);
			final Document doc = Jsoup.connect("https://github.com/" + href).get();
			Elements elements = doc.getElementsByClass("highlight tab-size js-file-line-container js-code-nav-container js-tagsearch-file");
			elements = elements.select("td");
			
			for (int i = 0; i < elements.size(); i++) {
				Element e = elements.get(i);
				w.write(e.text() + "\n");
			}
			
			w.close();
			addFile(file);
		}
		catch (Exception e) {
			JOptionPane alert = new JOptionPane();
			alert.showMessageDialog(alert, e);
			System.out.println(e);
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
			while (scanner.hasNextLine())
				metrics.readLine(scanner.nextLine());

			//Reads file for all smells
			SmellReader smellReader = new SmellReader(metrics, file, allMetrics);
			allSmells.addAll(smellReader.getSmells());

			//Reads file for all errors
			ErrorReader errorReader = new ErrorReader(file);
			allErrors.addAll(errorReader.getErrors());
			
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
		for (File file : filesInDirectory)
			readFile(file);
	}
	
	/**
	 * Prints all file names if the list contains any
	 * 
	 * @param nothing
	 * @return nothing
	 */
	public void printFileNames() {
		if (filesInDirectory.size() >= 0) {
			System.out.println("This directory contains the following java files:");
			
			for (int i = 0; i < filesInDirectory.size(); i++)
				System.out.println(i + 1 + ". " + filesInDirectory.get(i).getName());	
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