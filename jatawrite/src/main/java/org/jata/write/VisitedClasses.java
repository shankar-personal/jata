package org.jata.write;

import java.util.HashSet;
import java.util.Set;

public class VisitedClasses {

	Set<Class> visitedClassSet = new HashSet<Class>();

	private static VisitedClasses instance = new VisitedClasses();

	private VisitedClasses() {
	}

	public static VisitedClasses getInstance() {
		return instance;
	}

	/**
	 * @return the visitedClassSet
	 */
	public Set<Class> getVisitedClassSet() {
		return visitedClassSet;
	}

	public void reset() {
		visitedClassSet.clear();
	}

}
