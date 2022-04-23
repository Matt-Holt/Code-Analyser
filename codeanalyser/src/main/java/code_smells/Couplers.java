package code_smells;

import java.io.File;
import java.util.ArrayList;
import metrics.MethodMetrics;
import metrics.Metrics;
import other.Code;

/*
 * couplers consist of:
 * - Feature envy (uses another class more than itself)
 * - Inappropriate intimacy (uses fields and methods of another class too much)
 * - message chains (a() -> b() -> c() -> d())
 * - middle man (class that only deligates work to another class)
 */
public class Couplers {

	private File file;
	private Metrics metrics;
	private SmellReader sReader;
	private String type = "Couplers";
	private Code code = new Code();
	private ArrayList<MethodMetrics> methodChain = new ArrayList<MethodMetrics>();
	
	//Constructor
	public Couplers(File file, Metrics metrics, SmellReader sReader) {
		this.file = file;
		this.metrics = metrics;
		this.sReader = sReader;
		checkForSmells();
	}
	
	private void checkForSmells() {
		//Checks methods for method chains
		for (int i = 0; i < metrics.getMethods().size(); i++) {
			methodChain.clear();
			MethodMetrics m = metrics.getMethods().get(i);
			checkMethod(m);
			
			if (methodChain.size() >= 3) {
				String name = "Message Chain";
				String desc = "The class '" + metrics.getFileName() + "' contains a large " +
				"method chain of " + methodChain.size() + " calls, starting at the method '" + m.getMethodName() + "'. " +
			    "This is considered bad practice.";
				sReader.createSmell(name, desc, type);
			}
		}
		
		for (int i = 0; i < methodChain.size(); i++)
			System.out.println(methodChain.get(i).getMethodName());
	}
	
	/**
	 * Recursive method that checks if method contains call
	 * for other method, then checks that one
	 * 
	 * @param m method metrics
	 */
	private void checkMethod(MethodMetrics m) {		
		for (int i = 0; i < m.getCodeLines().size(); i++) {
			String line = m.getCodeLines().get(i);
			MethodMetrics methodFound = code.extractMethod(line, metrics.getMethods());
			
			if (methodFound != null && !methodChain.contains(methodFound) && !m.equals(methodFound)) {
				methodChain.add(methodFound);
				checkMethod(methodFound);
			}
		}
	}
}