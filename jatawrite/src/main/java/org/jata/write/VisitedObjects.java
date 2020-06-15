package org.jata.write;

import java.util.HashMap;
import java.util.Map;

public class VisitedObjects {

	Map<Object, String> visitedObjectMap = new HashMap<Object, String>();

	private static VisitedObjects instance = new VisitedObjects();

	private VisitedObjects() {
	}

	public static VisitedObjects getInstance() {
		return instance;
	}

	public Map<Object, String> getVisitedObjectMap() {
		return visitedObjectMap;
	}

	public void reset() {
		visitedObjectMap.clear();
	}


}
