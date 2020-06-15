package org.jata.write.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.maven.embedder.MavenEmbedderException;
import org.jata.write.AnnotationTemplateWriter;
import org.jata.write.BuildFailedException;
import org.jata.write.sample.BoxWithLatch;
import org.jata.write.sample.annot.SampleAnnot;


public class TestTwoAnnotationTemplateWriter extends TestCase {

	private static final String OUTPUT_DIR = TestJataWriter.getTestBaseDir()
			+ File.separator + "jata" + File.separator + "reader"
			+ File.separator;

	private String SECOND_OUTPUT_DIR = TestJataWriter.getTestBaseDir()
			+ File.separator + "jata" + File.separator + "readerSecond"
			+ File.separator;

	private static final String PROJECT_NAME = "DemoJataAnnotTwo";

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

	public void testAnnotTemplateWriteTwice() throws IOException, IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			InvocationTargetException, MavenEmbedderException,
			BuildFailedException, ClassNotFoundException {
	String versionString = "0.0.1";

		List<Class<?>> annotClasses = new ArrayList<Class<?>>();
		annotClasses.add(SampleAnnot.class);
		AnnotationTemplateWriter.write(OUTPUT_DIR, BoxWithLatch.class.getName(),
				PROJECT_NAME,  versionString, annotClasses);

		AnnotationTemplateWriter.write(SECOND_OUTPUT_DIR, BoxWithLatch.class.getName(),
				PROJECT_NAME+ "2",  versionString, annotClasses);
	}

}
