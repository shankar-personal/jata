package org.jata.sample.app;



import junit.framework.TestCase;

import org.jata.read.JataReader;
import org.jata.write.sample.BoxWithLatch;

public class TestReader extends TestCase {


	 public void testJataRead() {

		String jataFQClassName = "data.org.jata.write.sample.BoxWithLatch.a.BoxWithLatch";

		String versionString = "0.0.1";
		String projectName = "";

		Object object = JataReader.read(jataFQClassName, projectName, versionString);
		BoxWithLatch readBox =(BoxWithLatch)object;
		long height = readBox.height;
		TestCase.assertEquals(30, height);
	}

	 public void testJataReadByUUID() {


			String jataFQClassName = "data.org.jata.write.sample.BoxWithLatch.a.BoxWithLatch";

			String versionString = "0.0.1";
			String projectName = "";
			String uuidString = "9f8eef74-b8be-4c18-8168-668c2559786b";


			Object object = JataReader.read(jataFQClassName, projectName, versionString, uuidString);
			BoxWithLatch readBox =(BoxWithLatch)object;
			long length = readBox.length;
			TestCase.assertEquals(20, length);


			String secondUuidString = "2abde5c5-16b5-41f1-84bc-165f3bf3840e";

			Object secondobject = JataReader.read(jataFQClassName, projectName, versionString, secondUuidString);
			BoxWithLatch secondReadBox =(BoxWithLatch)secondobject;
			long secondLength = secondReadBox.length;
			TestCase.assertEquals(40, secondLength);
		}
}
