package code_smells;

import java.io.File;

import metrics.Metrics;

/*
 * couplers consist of:
 * - Feature envy (uses another class more than itself)
 * - Inappropriate intimacy (uses fields and methods of another class too much)
 * - message chains (a() -> b() -> c() -> d())
 * - middle man (class that only deligates work to another class)
 */
public class Couplers extends CodeSmells {

	public Couplers(String name, String desc) {
		super(name, desc);
	}
}