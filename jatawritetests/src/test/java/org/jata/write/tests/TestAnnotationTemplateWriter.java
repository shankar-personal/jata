package org.jata.write.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.jata.write.AnnotationTemplateWriter;
import org.jata.write.BuildFailedException;
import org.jata.write.sample.BoxWithLatch;
import org.jata.write.sample.annot.SampleAnnot;


public class TestAnnotationTemplateWriter extends TestCase {

	private static final String OUTPUT_DIR = TestJataWriter.getTestBaseDir() + File.separator +  "annotation" + File.separator + "writer" + File.separator;

	private static final String PROJECT_NAME = "AnnotationJava";

	public void testAnnotationTemplateWrite() throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, BuildFailedException {

		String versionString = "0.0.1";
		List<Class<?>> annotClasses = new ArrayList<Class<?>>();
		annotClasses.add(SampleAnnot.class);
		AnnotationTemplateWriter.write(OUTPUT_DIR, BoxWithLatch.class.getName(),
				PROJECT_NAME,  versionString, annotClasses);

	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		File file = new File(OUTPUT_DIR);
		assert file.exists();
		FileUtils.deleteDirectory(file);
		super.tearDown();
	}


}
