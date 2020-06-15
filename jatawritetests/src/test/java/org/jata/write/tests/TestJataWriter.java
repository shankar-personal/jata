package org.jata.write.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.embedder.MavenEmbedderException;
import org.jata.write.BuildFailedException;
import org.jata.write.JataWriter;
import org.jata.write.sample.BoxWithLatch;
import org.jata.write.sample.BoxWithMap;
import org.jata.write.sample.BoxWithMultiDimensionalNonPrimitiveArray;
import org.jata.write.sample.BoxWithMultiDimensionalPrimitiveArray;
import org.jata.write.sample.BoxWithSet;
import org.jata.write.sample.Button;
import org.jata.write.sample.Color;
import org.jata.write.sample.DoubleLatch;
import org.jata.write.sample.Latch;
import org.jata.write.sample.Person;

import junit.framework.TestCase;


public class TestJataWriter extends TestCase {

	private static final String OUTPUT_DIR = TestJataWriter.getTestBaseDir() + File.separator +  "jata" + File.separator + "writer" + File.separator;

	private static final String PROJECT_NAME = "DemoJata";

	public void testJataWriteWithNullReference() throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException, MavenEmbedderException, BuildFailedException {

		BoxWithSet box = new BoxWithSet(10, 20, 30);
		box.setName("Big");

		JataWriter.write(box, OUTPUT_DIR, PROJECT_NAME);
	}

	public void testJataWriteWithEnum() throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException, MavenEmbedderException, BuildFailedException {

		Button button = new Button();
		button.setName("Big");
		button.setColor(Color.RED);

		JataWriter.write(button, OUTPUT_DIR, PROJECT_NAME);
	}

	public void testJataWrite() throws IOException, IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			InvocationTargetException, MavenEmbedderException, BuildFailedException {

		BoxWithLatch box = new BoxWithLatch(10, 20, 30);

		Latch latch = new Latch();
		latch.setName("sealed");
		box.setLatch(latch);

		JataWriter.write(box, OUTPUT_DIR, PROJECT_NAME);
	}

	public void testJataWriteWithSet() throws IOException, IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			InvocationTargetException, MavenEmbedderException, BuildFailedException {

		BoxWithSet box = new BoxWithSet(10, 20, 30);
		box.setName("Big");
		Latch latch = new Latch();
		latch.setName("sealed");
		box.setLatch(latch);
		Set<Integer> contentIds = new HashSet<Integer>();
		contentIds.add(1);
		contentIds.add(2);
		contentIds.add(3);
		box.setContentIds(contentIds);

		JataWriter.write(box, OUTPUT_DIR, PROJECT_NAME);
	}

	public void testJataWriteWithMap() throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException, MavenEmbedderException, BuildFailedException {

		BoxWithMap box = new BoxWithMap(10, 20, 30);
		box.setName("Big");
		Latch latch = new Latch();
		latch.setName("sealed");
		box.setLatch(latch);
		Set<Integer> contentIds = new HashSet<Integer>();
		contentIds.add(1);
		contentIds.add(2);
		contentIds.add(3);
		box.setContentIds(contentIds);

		Map<String, Person> people = new HashMap<String, Person>();
		Person john = new Person();
		john.setName("John");
		john.setHeight(6.0);
		john.setWeight(189.2);

		Person mathew = new Person();
		mathew.setName("Mathew");
		mathew.setHeight(6.0);
		mathew.setWeight(189.2);
		people.put(john.getName(), john);
		people.put(mathew.getName(), mathew);
		box.setPeople(people);

		JataWriter.write(box, OUTPUT_DIR, PROJECT_NAME);
	}

	public void testJataWriteWithArray() throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException, MavenEmbedderException, BuildFailedException {

		BoxWithMultiDimensionalNonPrimitiveArray box = new BoxWithMultiDimensionalNonPrimitiveArray(
				10, 20, 30);
		box.setName("Big");
		Latch latch = new Latch();
		latch.setName("sealed");
		box.setLatch(latch);
		Set<Integer> contentIds = new HashSet<Integer>();
		contentIds.add(1);
		contentIds.add(2);
		contentIds.add(3);
		box.setContentIds(contentIds);

		Person john = new Person();
		john.setName("John");
		john.setHeight(6.0);
		john.setWeight(189.2);

		Person mathew = new Person();
		mathew.setName("Mathew");
		mathew.setHeight(6.0);
		mathew.setWeight(189.2);
		Person[][][] peoples = new Person[][][] {
				{ { null, john }, { null, mathew } },
				{ { null, null }, { null, mathew } } };


		box.setPeople(peoples);

		JataWriter.write(box, OUTPUT_DIR, PROJECT_NAME);
	}

	public void testJataWriteWithArrayOfPrimitives() throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException, MavenEmbedderException, BuildFailedException {

		BoxWithMultiDimensionalPrimitiveArray box = new BoxWithMultiDimensionalPrimitiveArray(
				10, 20, 30);
		box.setName("Big");
		Latch latch = new Latch();
		latch.setName("sealed");
		box.setLatch(latch);
		Set<Integer> contentIds = new HashSet<Integer>();
		contentIds.add(1);
		contentIds.add(2);
		contentIds.add(3);
		box.setContentIds(contentIds);

		Person john = new Person();
		john.setName("John");
		john.setHeight(6.0);
		john.setWeight(189.2);

		Person mathew = new Person();
		mathew.setName("Mathew");
		mathew.setHeight(6.0);
		mathew.setWeight(189.2);

		int[][][] coordinates = new int[][][]{
				{ { 0, 5 }, { 0, 6 } },
				{ { 0, 0 }, { 0, 0 } } };

		//Arrays. people.getClass()

		box.setCoordinates(coordinates);


		JataWriter.write(box, OUTPUT_DIR, PROJECT_NAME);
	}

	public void testJataWriteWithAbstractClass() throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException, MavenEmbedderException, BuildFailedException {

		Person person = new Person();

		person.setName("Arjun");
		person.setHeight(6.3);
		person.setWeight(175.2);
		JataWriter.write(person, OUTPUT_DIR, PROJECT_NAME);
	}

	public void testJataWriteWithSuperClass() throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException, MavenEmbedderException, BuildFailedException {

		DoubleLatch doubleLatch = new DoubleLatch();
		doubleLatch.setName("sealed");
		doubleLatch.setSecondName("super sealed");

		JataWriter.write(doubleLatch, OUTPUT_DIR, PROJECT_NAME);
	}

	public void testJataWriteWithInterface() throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException, MavenEmbedderException, BuildFailedException {

		BoxWithSet box = new BoxWithSet(10, 20, 30);
		box.setName("Big");
		Latch latch = new Latch();
		latch.setName("sealed");
		box.setLatch(latch);
		Set<Integer> contentIds = new HashSet<Integer>();
		contentIds.add(1);
		contentIds.add(2);
		contentIds.add(3);
		box.setContentIds(contentIds);

		JataWriter.write(box, OUTPUT_DIR, PROJECT_NAME);
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

	public static String getTestBaseDir() {
		String resultPath = null;
		String baseDirPath = System.getProperty("basedir");
		if (baseDirPath != null) {
			resultPath = baseDirPath + File.separator + "target"
					+ File.separator + "testoutput";
		} else {
			resultPath = "." + File.separator + "target" + File.separator
					+ "testoutput";
		}
		return resultPath;
	}

}
