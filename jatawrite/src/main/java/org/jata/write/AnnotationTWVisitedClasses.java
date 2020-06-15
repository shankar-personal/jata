package org.jata.write;

import java.util.HashMap;
import java.util.Map;

public class AnnotationTWVisitedClasses {

	Map<Class, Boolean> visitedClassSet = new HashMap<Class, Boolean>();

	private static AnnotationTWVisitedClasses instance = new AnnotationTWVisitedClasses();

	private AnnotationTWVisitedClasses() {
	}

	public static AnnotationTWVisitedClasses getInstance() {
		return instance;
	}

	/**
	 * @return the visitedClassSet
	 */
	public Map<Class, Boolean> getVisitedClassSet() {
		return visitedClassSet;
	}

	public void reset() {
		visitedClassSet.clear();
	}

}
