package code_smells;

import java.io.File;

import metrics.Metrics;

/*
 * Change preventer's consist of:
 * - Divergent change (change many methods when making change to one)
 * - Shotgun surgery (Making small change makes you change many other classes)
 * - Parallel inheritance hierarchy (adding subclass makes you add a subclass for another class)
 */
public class ChangePreventer {
	
	public ChangePreventer(Metrics metrics) {
		
	}
}