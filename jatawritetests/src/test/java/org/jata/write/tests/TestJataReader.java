package org.jata.write.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.maven.embedder.MavenEmbedderException;
import org.jata.read.JataReader;
import org.jata.write.BuildFailedException;
import org.jata.write.JataIdInfo;
import org.jata.write.JataWriter;
import org.jata.write.poms.JataPOMWriter;
import org.jata.write.sample.BoxWithLatch;
import org.jata.write.sample.Latch;


public class TestJataReader extends TestCase {

	private static final String OUTPUT_DIR = TestJataWriter.getTestBaseDir()
			+ File.separator + "jata" + File.separator + "reader"
			+ File.separator;

	private String SECOND_OUTPUT_DIR = TestJataWriter.getTestBaseDir()
			+ File.separator + "jata" + File.separator + "readerSecond"
			+ File.separator;

	private static final String PROJECT_NAME = "DemoJata";

	@Override
	public void setUp() throws IOException {
		File file = new File(OUTPUT_DIR);
		if (file.exists()) {
			FileUtils.deleteDirectory(file);
		}

		file = new File(SECOND_OUTPUT_DIR);
		if (file.exists()) {
			FileUtils.deleteDirectory(file);
		}

	}

	public void testJataRead() throws IOException, IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			InvocationTargetException, MavenEmbedderException,
			BuildFailedException {

		BoxWithLatch box = new BoxWithLatch(10, 20, 30);
		Latch latch = new Latch();
		latch.setName("sealed");
		box.setLatch(latch);

		String versionString = "0.0.1";
		JataIdInfo jataIdInfo = JataWriter.write(box, OUTPUT_DIR, PROJECT_NAME,
				versionString);

		String jataFQClassName = jataIdInfo.getJataFQClassName();
		String jataJarPath = OUTPUT_DIR + File.separator + "jata"
				+ File.separator + "target" + File.separator
				+ JataPOMWriter.getJataProjectName(PROJECT_NAME) + "-"
				+ versionString + ".jar";
		File jataJarFile = new File(jataJarPath);
		URL jataJarUrl = jataJarFile.toURL();

		String projectName = PROJECT_NAME;
		Object object = JataReader.read(jataFQClassName, projectName, versionString, jataJarUrl);
		BoxWithLatch readBox =(BoxWithLatch)object;
		int height = readBox.getHeight();
		TestCase.assertEquals(30, height);

		BoxWithLatch secondBox = new BoxWithLatch(20, 40, 30);
		Latch secondLatch = new Latch();
		secondLatch.setName("sealed");
		secondBox.setLatch(latch);



		JataIdInfo secondJataIdInfo = JataWriter.write(secondBox, SECOND_OUTPUT_DIR, PROJECT_NAME + "2",
				versionString);
	}

}
