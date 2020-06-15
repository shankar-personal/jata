package org.jata.write.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.embedder.MavenEmbedderException;
import org.jata.write.BuildFailedException;
import org.jata.write.JataWriter;
import org.jata.write.sample.BoxWithLatch;
import org.jata.write.sample.Latch;

import junit.framework.TestCase;


public class TestJataWriterDemo extends TestCase {

	private static final String OUTPUT_DIR = TestJataWriter.getTestBaseDir()
			+ File.separator + "jata" + File.separator + "writer"
			+ File.separator + "demo" + File.separator;

	private static final String PROJECT_NAME = "DemoJata";


	@Override
	public void setUp() throws IOException {
		File file = new File(OUTPUT_DIR);
		if (file.exists()) {
			FileUtils.deleteDirectory(file);
		}

	}

	public void testJataWrite() throws IOException, IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			InvocationTargetException, MavenEmbedderException, BuildFailedException {
		String javaHome = System.getProperty("java.home");
		BoxWithLatch box = new BoxWithLatch(10, 20, 30);
		Latch latch = new Latch();
		latch.setName("sealed");
		box.setLatch(latch);

		JataWriter.write(box, OUTPUT_DIR, PROJECT_NAME);
	}

}
