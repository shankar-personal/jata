package org.jata.write.id.generator;

import java.util.HashMap;
import java.util.Map;

public class AlphaIdGenerator {

	private static AlphaIdGenerator instance = new AlphaIdGenerator();

	private Map<String, String> alphaCountersByPackageName = null;

	private AlphaIdGenerator() {
		alphaCountersByPackageName = new HashMap<String, String>();

	}

	public static AlphaIdGenerator getInstance() {
		return instance;
	}

	public String getNextId(String packageName) {
		String currentId = alphaCountersByPackageName.get(packageName);
		String nextId = "";
		if (currentId == null) {
			nextId = "a";
			alphaCountersByPackageName.put(packageName, nextId);
			return nextId;
		}

		char lastChar = currentId.charAt(currentId.length() -1);
		char nextChar = ++lastChar;
		if (Character.isLetter(nextChar)) {
			nextId = currentId.substring(0, currentId.length() -1) + nextChar;
			alphaCountersByPackageName.put(packageName, nextId);
			return nextId;
		}
		else {
			nextId = currentId + nextChar;
			alphaCountersByPackageName.put(packageName, nextId);
			return nextId;
		}

	}

	public void reset() {
		alphaCountersByPackageName.clear();
	}

}
