package org.jata.dynamic.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.jata.dynamic.AmbiguosMethodException;
import org.jata.dynamic.Dynamic;
import org.jata.dynamic.sample.OrdinaryBox;

import junit.framework.TestCase;



public class TestDynamic extends TestCase {

	/**
	 *
	 */
	public TestDynamic() {
		super();
	}

	/**
	 * @param arg0
	 */
	public TestDynamic(String arg0) {
		super(arg0);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testTraditionalReflection() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, AmbiguosMethodException, InstantiationException {

		Long length = new Long(20);
		Long width = new Long(20);
		Long height = new Long(20);

		OrdinaryBox box = new OrdinaryBox();
		Class[] parameterTypes = new Class[] {Long.class, Long.class, Long.class};
		Method getVolumeMethod = OrdinaryBox.class.getMethod("getVolume", parameterTypes);
		Object[] arguments = new Object[]{length, width, height};
		long volume = (Long) getVolumeMethod.invoke(box,arguments);
		assertEquals(volume, length*width*height);

	}

	public void testDynamicNonPrimitiveParamterTypes() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, AmbiguosMethodException, InstantiationException {

		long length = 20;
		long width = 20;
		long height = 20;
		OrdinaryBox box = new OrdinaryBox();

		long volume = (Long)Dynamic.call(box, "getVolume", long.class, length, long.class, width, long.class, height);
		assertEquals(volume, length*width*height);

	}


	public void testDynamicStaticMethod() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, AmbiguosMethodException, InstantiationException, ClassNotFoundException {

		long length = 20;
		long width = 20;
		long height = 20;

		long volume = (Long)Dynamic.callStatic(OrdinaryBox.class.getName(), "getStaticVolume", long.class, length, long.class, width, long.class, height);
		assertEquals(volume, length*width*height);

	}

	public void testDynamicStaticNoArgMethod() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, AmbiguosMethodException, InstantiationException, ClassNotFoundException {

		long max = (Long)Dynamic.callStatic(OrdinaryBox.class.getName(), "getStaticMax");
		assertEquals(max, 1000);

	}

	public void testDynamicStaticNoArgMethodWithClass() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, AmbiguosMethodException, InstantiationException, ClassNotFoundException {

		long max = (Long)Dynamic.callStatic(OrdinaryBox.class, "getStaticMax");
		assertEquals(max, 1000);

	}

	public void testDynamicPrimitiveParamterTypes() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, AmbiguosMethodException, InstantiationException {

		long length = 20;
		long width = 20;
		long height = 20;
		OrdinaryBox box = new OrdinaryBox();

		long volume = (Long)Dynamic.call(box, "getPrimitiveVolume", long.class, length, long.class, width, long.class, height);
		assertEquals(volume, length*width*height);

	}

	public void testDynamicInheritance() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, AmbiguosMethodException, InstantiationException {

		long length = 20;
		long width = 20;
		long height = 20;
		OrdinaryBox box = new OrdinaryBox();

		Set<Integer> contentIds = new HashSet<Integer>();
		contentIds.add(1);
		contentIds.add(2);
		contentIds.add(3);

		Dynamic.call(box, "setContentIds",Set.class, contentIds);
		Set<Integer> retrunedContentIds = (Set<Integer>)Dynamic.call(box, "getContentIds");
		assertEquals(retrunedContentIds.size(), 3);

	}

	public void testSimpleConstruction() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, AmbiguosMethodException, InstantiationException, ClassNotFoundException {

		int length = 20;
		int width = 20;
		int height = 20;

		OrdinaryBox box = (OrdinaryBox)Dynamic.makeNew(org.jata.dynamic.sample.OrdinaryBox.class.getName(), int.class, length, int.class, width, int.class, height);

		Set<Integer> contentIds = new HashSet<Integer>();
		contentIds.add(1);
		contentIds.add(2);
		contentIds.add(3);

		Dynamic.call(box, "setContentIds", Set.class, contentIds);
		Set<Integer> retrunedContentIds = (Set<Integer>)Dynamic.call(box, "getContentIds");
		assertEquals(retrunedContentIds.size(), 3);

	}

	public void testDynamicVarargs() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, AmbiguosMethodException, InstantiationException {

		long length = 20;
		long width = 20;
		OrdinaryBox box = new OrdinaryBox();
		long topArea = (Long)Dynamic.call(box, "getTopArea", long.class, length, long.class, width);
		assertEquals(topArea, length*width);

		long topAreaOne = (Long)Dynamic.call(box, "getTopAreaOne", long.class, length, long.class, width);
		assertEquals(topAreaOne, length*width);

	}

	/**
	 * This test is testing a bug in java 5. The compiler should not allow
	 * ambiguous methods to be defined. The correct recourse is for the compiler
	 * to insist that the method is renamed and force a disambiguation.
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AmbiguosMethodException
	 * @throws InstantiationException
	 */
	public void testDynamicAmbiguousVarargs() throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, AmbiguosMethodException, InstantiationException {

		long length = 20;
		long width = 20;
		OrdinaryBox box = new OrdinaryBox();
		try {
			long topArea = (Long) Dynamic.call(box, "getTopAreaTwo", long.class, length, long.class,
					width);
		} catch (AmbiguosMethodException ame) {
			assertTrue(true);
			return;
		}
		fail();
	}

}
