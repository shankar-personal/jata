package org.jata.write;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jata.write.id.generator.AlphaIdGenerator;
import org.jata.write.poms.JataPOMWriter;
import org.jata.write.poms.TopLevelJataPOMWriter;
import org.jata.write.poms.ValidationPOMWriter;
import org.jata.write.util.AccessorUtil;
import org.jata.write.util.BuildFailedException;
import org.jata.write.util.MavenBuildUtil;


public class JataWriter {

	public static final String JATA_BASE_DIR = "jata";

	/**
	 * Make sure that this stays in sync with the pom.xml version number.
	 */
	public static final String JATA_WRITER_VERSION = "0.0.1";

	public static final String JATA_ID_INFO_PROPERTIES_FILE_NAME = "jataIdInfo.props";

	/*
	 * TODO: Immutables. Will need a way to annotate the fields, with the constructor arg positions
	 * as follows:
	 * @Constructors("[parametertypes](positionNumber), [parametertypes](positionNumber) ....) where
	 * parametertypes are the parametertypes in the constructor where the types appear in the same order
	 * as the parameters in the constructor.
	 */
	public static JataIdInfo write(Object object, String dirName) throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException, BuildFailedException {
		return write(object, dirName, null);
	}

	public static JataIdInfo write(Object object, String dirName,
			String jataProjectIdString) throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException,
			InvocationTargetException, BuildFailedException {
		return write(object, dirName, jataProjectIdString, null);

	}

	public static JataIdInfo write(Object object, String dirName, String jataProjectIdString, String versionString)
			throws IOException, IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			InvocationTargetException, BuildFailedException {
		Class clazz = object.getClass();
		String className = clazz.getName();
		if (jataProjectIdString == null) {
			jataProjectIdString = UUID.randomUUID().toString();
		}
		String lclObjectIdString = AlphaIdGenerator.getInstance().getNextId(className);
		String jataUUIDString = UUID.randomUUID().toString();

		String jataFQClassName = write(object, dirName, lclObjectIdString, jataProjectIdString, jataUUIDString, null);
		JataPOMWriter.write(object, dirName, jataProjectIdString, versionString);
		// write validation classes using ValidationWriter
		Set<Class> visitedClassSet = VisitedClasses.getInstance()
				.getVisitedClassSet();
		for (Class visitedClass : visitedClassSet) {
			ValidationWriter.write(dirName, visitedClass);
		}
		ValidationPOMWriter.write(object, dirName, jataProjectIdString, versionString);
		TopLevelJataPOMWriter.write(object, dirName, jataProjectIdString, versionString);

		JataIdInfo jataIdInfo = new JataIdInfo(jataFQClassName, versionString, jataProjectIdString, jataUUIDString);
		String resourcesDirPath = dirName + File.separator + JATA_BASE_DIR
						+ File.separator + "src" + File.separator + "main"
						+ File.separator + "resources";
		File resourceDir = new File(resourcesDirPath);

		if (!resourceDir.exists()) {
			resourceDir.mkdirs();
		}
		else {
			assert resourceDir.isDirectory();
		}

		String jataIdPropsFileName = resourcesDirPath + File.separator
				+ JATA_ID_INFO_PROPERTIES_FILE_NAME;
		JataIdInfo.writeAsProperties(jataIdPropsFileName, jataIdInfo);

		// build the created projects.
		MavenBuildUtil.buildGeneratedJataProject(dirName);
		VisitedClasses.getInstance().reset();
		VisitedObjects.getInstance().reset();
		AnnotationTWVisitedClasses.getInstance().reset();
		AlphaIdGenerator.getInstance().reset();

		return jataIdInfo;

	}

	public static String write(Object object, String dirName, String lclObjectIdString,
			String projectIdString, String jataUUIDString, Class clazz) throws IOException, IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			InvocationTargetException {
		if (clazz == null) {
			clazz = object.getClass();
		}
		String className = clazz.getName();
		String simpleClassName = clazz.getSimpleName();
		String packageName = clazz.getPackage().getName();
		Class superClass = clazz.getSuperclass();
		VisitedClasses.getInstance().getVisitedClassSet().add(clazz);

		Field[] declaredFields = clazz.getDeclaredFields();
		if (lclObjectIdString == null) {
			lclObjectIdString = AlphaIdGenerator.getInstance().getNextId(className);
			VisitedObjects.getInstance().getVisitedObjectMap().put(object,
					lclObjectIdString);
		}

		String relativePackageDir = getRelativePackageDir(lclObjectIdString, className, simpleClassName);
		File directory = initializePackageDirectories(dirName, relativePackageDir);

		if (!directory.isDirectory()) {
			// we should throw an exception.
			assert false;
		}

		String outputFileName = directory + File.separator + simpleClassName
				+ ".java";
		String relativeClassLocation = relativePackageDir + File.separator
				+ simpleClassName;
		String jataFQClassName = relativeClassLocation
				.replace(File.separatorChar, '.');

		Writer writer = null;
		try {

			writer = new BufferedWriter(new FileWriter(outputFileName));
			String ownerPackageName = writeBeginning(object, dirName, lclObjectIdString,
					projectIdString, jataUUIDString, clazz, simpleClassName, packageName, superClass, writer);

			for (Field field : declaredFields) {
				if (!isStatic(field)) {
					Object objectValue = null;
					boolean fieldAccessible = true;
					try {
						objectValue = AccessorUtil.getValue(object, field);
					} catch (NoSuchMethodException nsme) {
						fieldAccessible = false;
					} catch (IllegalAccessException iae) {
						fieldAccessible = false;
					}
					if (fieldAccessible) {

						String fieldGenericString = field.toGenericString();
						VisitedClasses.getInstance().getVisitedClassSet().add(
								field.getType());

						if (field.getType().isPrimitive()) {

							writePrimitive(simpleClassName, packageName,
									writer, objectValue, fieldGenericString);

						} else if (isStringType(field)) {

							writeString(simpleClassName, packageName, writer,
									objectValue, fieldGenericString);

						} else if (isSingleDimensionCollectionType(field))
							writeSingleDimensionedCollection(dirName,
									simpleClassName, packageName, writer,
									ownerPackageName, field, objectValue,
									fieldGenericString, projectIdString, jataUUIDString);
						else if (isTwoDimensionCollectionType(field)) {
							writeDoubleDimensionedCollection(dirName,
									simpleClassName, packageName, writer,
									ownerPackageName, field, objectValue,
									fieldGenericString, projectIdString, jataUUIDString);

						} else if (field.getType().isArray())
							writeArray(dirName, simpleClassName, packageName,
									writer, field, objectValue,
									fieldGenericString, projectIdString, jataUUIDString);
						else if (field.getType().isEnum()) {
							writeEnum(simpleClassName, packageName, writer,
									field, objectValue, fieldGenericString);

						} else
							writeSimpleReference(dirName, simpleClassName,
									packageName, writer, objectValue,
									fieldGenericString, projectIdString, jataUUIDString);
						writer.write("\n");
						writer.write("\n");

					}
				}
			}

			if (!isObjectBaseClass(object, clazz)) {

				writeGetDataObjectMethod(clazz, writer, lclObjectIdString);
			}

			writeEnding(writer);
		} finally {
			// flush and close both "output" and its underlying FileWriter
			if (writer != null)
				writer.close();
		}
		return jataFQClassName;

	}

	/**
	 * @param dirName
	 * @param relativePackageDir TODO
	 * @return
	 */
	private static File initializePackageDirectories(String dirName,
			String relativePackageDir) {

		String dataSrcPath = JATA_BASE_DIR + File.separator + "src" + File.separator
				+ "main" + File.separator + "java";
		File directory = new File(dirName + File.separator + dataSrcPath
				+ File.separator + relativePackageDir);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return directory;
	}

	private static String getRelativePackageDir(String lclObjectIdString, String className, String simpleClassName) {
		int lastIndex = className.lastIndexOf(".");
		String classDirs = className.substring(0, lastIndex).replace('.',
				File.separatorChar);

		String relativePackageDir = "data" + File.separator + classDirs
						+ File.separator + simpleClassName + File.separator + lclObjectIdString;
		return relativePackageDir;
	}

	/**
	 * @param writer
	 * @throws IOException
	 */
	private static void writeEnding(Writer writer) throws IOException {
		writer.write("}");
	}

	/**
	 * @param object
	 * @param dirName
	 * @param lclObjectIdString
	 * @param projectIdString TODO
	 * @param jataUUIDString TODO
	 * @param clazz
	 * @param simpleClassName
	 * @param packageName
	 * @param superClass
	 * @param writer
	 * @return owner package name
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	private static String writeBeginning(Object object, String dirName,
			String lclObjectIdString, String projectIdString, String jataUUIDString,
			Class clazz, String simpleClassName, String packageName, Class superClass, Writer writer)
			throws IOException, IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		String ownerPackageName = "data." + packageName + "." + simpleClassName
				+ "." + lclObjectIdString;

		writer.write("package " + ownerPackageName + ";");

		writer.write("\n");
		writer.write("\n");

		writeImports(writer);
		writer.write("\n");
		writer.write("\n");

		writer.write("@" + JataUUId.class.getSimpleName() + "(id=\""
				+ jataUUIDString + "\")");
		writer.write("\n");
		writer.write("@" + JataProjectId.class.getSimpleName() + "(id=\""
				+ projectIdString + "\")");
		writer.write("\n");
		if (superClass.getName().equals(java.lang.Object.class.getName())) {
			if (isObjectBaseClass(object, clazz)) {
				writer.write("public abstract class " + simpleClassName + " {");

			} else {
				writer.write("public class " + simpleClassName + " {");
			}
		} else {
			String superClassIdString = AlphaIdGenerator.getInstance()
					.getNextId(superClass.getName());
			write(object, dirName, superClassIdString, projectIdString, jataUUIDString, superClass);

			String dataObjectSuperClassName = getDataObjectClassName(
					superClass, superClassIdString);
			if (isObjectBaseClass(object, clazz)) {
				writer.write("public abstract class " + simpleClassName
						+ " extends " + dataObjectSuperClassName + " {");

			} else {
				writer.write("public class " + simpleClassName + " extends "
						+ dataObjectSuperClassName + " {");
			}

		}
		writer.write("\n");
		writer.write("\n");
		return ownerPackageName;
	}

	/**
	 * @param simpleClassName
	 * @param packageName
	 * @param writer
	 * @param objectValue
	 * @param fieldGenericString
	 * @throws IOException
	 */
	private static void writeString(String simpleClassName, String packageName,
			Writer writer, Object objectValue, String fieldGenericString)
			throws IOException {
		writer.write("           "
				+ fieldGenericString.replaceFirst(
						packageName + "." + simpleClassName + ".", "")
						.replaceFirst("private", "public"));
		writer.write(" = \"" + objectValue + "\";");
	}

	/**
	 * @param simpleClassName
	 * @param packageName
	 * @param writer
	 * @param objectValue
	 * @param fieldGenericString
	 * @throws IOException
	 */
	private static void writePrimitive(String simpleClassName,
			String packageName, Writer writer, Object objectValue,
			String fieldGenericString) throws IOException {
		// TODO: We need to handle protected visibility
		// modifier
		// both when specified and not specified.

		writer.write("           "
				+ fieldGenericString.replaceFirst(
						packageName + "." + simpleClassName + ".", "")
						.replaceFirst("private", "public"));
		writer.write(" = " + objectValue + ";");
	}

	/**
	 * @param dirName
	 * @param simpleClassName
	 * @param packageName
	 * @param writer
	 * @param objectValue
	 * @param fieldGenericString
	 * @param projectIdString TODO
	 * @param jataUUIDString TODO
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	private static void writeSimpleReference(String dirName,
			String simpleClassName, String packageName, Writer writer,
			Object objectValue, String fieldGenericString, String projectIdString, String jataUUIDString) throws IOException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		{
			// this is the place where we write out simple
			// references.

			Object value = objectValue;

			if (value == null) {
				writer.write("           "
						+ fieldGenericString.replaceFirst(
								packageName + "." + simpleClassName + ".", "")
								.replaceFirst("private", "public"));
				writer.write("                  = ");
				writer.write("\n");
				writer.write("                          null;");

			} else {

				VisitedObjects visitedObjects = VisitedObjects.getInstance();
				String propertyIdString = visitedObjects.getVisitedObjectMap()
						.get(value);
				if (propertyIdString == null) {
					propertyIdString = AlphaIdGenerator.getInstance()
							.getNextId(value.getClass().getName());
					visitedObjects.getVisitedObjectMap().put(value,
							propertyIdString);
					write(value, dirName, propertyIdString, projectIdString, jataUUIDString, value.getClass());
				}

				String dataObjectClassName = getDataObjectClassName(value
						.getClass(), propertyIdString);

				writer.write("           "
						+ fieldGenericString.replaceFirst(
								packageName + "." + simpleClassName + ".", "")
								.replaceFirst("private", "public"));
				writer.write(" = ");
				writer.write("\n");
				writer.write("                          " + dataObjectClassName
						+ ".getData()");
				writer.write(";");
			}
		}
	}

	/**
	 * @param simpleClassName
	 * @param packageName
	 * @param writer
	 * @param field
	 * @param objectValue
	 * @param fieldGenericString
	 * @throws IOException
	 */
	private static void writeEnum(String simpleClassName, String packageName,
			Writer writer, Field field, Object objectValue,
			String fieldGenericString) throws IOException {
		writer.write("           "
				+ fieldGenericString.replaceFirst(
						packageName + "." + simpleClassName + ".", "")
						.replaceFirst("private", "public"));
		writer.write(" = " + field.getType().getName() + "." + objectValue
				+ ";");
	}

	/**
	 * @param dirName
	 * @param simpleClassName
	 * @param packageName
	 * @param writer
	 * @param field
	 * @param objectValue
	 * @param fieldGenericString
	 * @param projectIdString TODO
	 * @param jataUUIDString TODO
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	private static void writeArray(String dirName, String simpleClassName,
			String packageName, Writer writer, Field field, Object objectValue,
			String fieldGenericString, String projectIdString, String jataUUIDString) throws IOException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		{

			Class componentType = field.getType().getComponentType();

			int arrayDimensions = 1;

			while (componentType.isArray()) {
				componentType = componentType
						.getComponentType();
				arrayDimensions++;
			}

			if (AccessorUtil.isStatic(field)) {

				writer.write("           " + "public static "
						+ componentType.getName() );

			}
			else {
				writer.write("           " + "public "
						+ componentType.getName() );

			}
			for (int i = 0; i < arrayDimensions; i++) {
				writer.write("[]");
			}

			writer.write( " " + field.getName());

			writer.write(" = new " + componentType.getName());
			for (int i = 0; i < arrayDimensions; i++) {
				writer.write("[]");
			}
			writer.write("\n");

			writeArrayElements(objectValue, dirName, writer, 0, projectIdString, jataUUIDString);
			writer.write(";");

		}
	}

	/**
	 * @param dirName
	 * @param simpleClassName
	 * @param packageName
	 * @param writer
	 * @param ownerPackageName
	 * @param field
	 * @param objectValue
	 * @param fieldGenericString
	 * @param projectIdString TODO
	 * @param jataUUIDString TODO
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	private static void writeDoubleDimensionedCollection(String dirName,
			String simpleClassName, String packageName, Writer writer,
			String ownerPackageName, Field field, Object objectValue,
			String fieldGenericString, String projectIdString, String jataUUIDString) throws IOException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		String collectionDataObjectClassName = null;
		String unAlteredCollectionDataObjectClassName = null;
		if (isGeneric(field)) {
			Type genericType = field.getGenericType();
			unAlteredCollectionDataObjectClassName = genericType.toString();

			collectionDataObjectClassName = unAlteredCollectionDataObjectClassName
					.replace('<', '.').replace(">", "").replace(" ", "").replace(",","And");
		} else {
			unAlteredCollectionDataObjectClassName = field.getType().getName();
			collectionDataObjectClassName = unAlteredCollectionDataObjectClassName;
		}

		Object value = objectValue;
		if (value == null) {
			writer.write("           "
					+ fieldGenericString.replaceFirst(
							packageName + "." + simpleClassName + ".", "")
							.replaceFirst("private", "public"));
			writer.write("                  = ");
			writer.write("\n");
			writer.write("                          null;");

		} else {
			String propertyIdString = VisitedObjects.getInstance()
					.getVisitedObjectMap().get(value);

			if (propertyIdString == null) {
				propertyIdString = AlphaIdGenerator.getInstance().getNextId(
						collectionDataObjectClassName);
				VisitedObjects.getInstance().getVisitedObjectMap().put(value,
						propertyIdString);

				writeTwoDimensionalCollection(value, dirName, propertyIdString,
						projectIdString,
						collectionDataObjectClassName,
						unAlteredCollectionDataObjectClassName, ownerPackageName + "." + simpleClassName, jataUUIDString);
			}
			String dataObjectClassName = getCollectionDataObjectClassName(
					collectionDataObjectClassName, propertyIdString);

			writer.write("           "
					+ fieldGenericString.replaceFirst(
							packageName + "." + simpleClassName + ".", "")
							.replaceFirst("private", "public"));
			writer.write(" = ");
			writer.write("\n");
			writer.write("                          " + dataObjectClassName
					+ ".getData()");
			writer.write(";");
		}
	}

	/**
	 * @param dirName
	 * @param simpleClassName
	 * @param packageName
	 * @param writer
	 * @param ownerPackageName
	 * @param field
	 * @param objectValue
	 * @param fieldGenericString
	 * @param projectIdString TODO
	 * @param jataUUIDString TODO
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	private static void writeSingleDimensionedCollection(String dirName,
			String simpleClassName, String packageName, Writer writer,
			String ownerPackageName, Field field, Object objectValue,
			String fieldGenericString, String projectIdString, String jataUUIDString) throws IOException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		{
			String collectionDataObjectClassName = null;
			String unAlteredCollectionDataObjectClassName = null;
			if (isGeneric(field)) {
				Type genericType = field.getGenericType();
				unAlteredCollectionDataObjectClassName = genericType.toString();

				collectionDataObjectClassName = unAlteredCollectionDataObjectClassName
						.replace('<', '.').replace(">", "");
			} else {
				unAlteredCollectionDataObjectClassName = field.getType()
						.getName();
				collectionDataObjectClassName = unAlteredCollectionDataObjectClassName;
			}

			Object value = objectValue;
			if (value == null) {
				writer.write("           "
						+ fieldGenericString.replaceFirst(
								packageName + "." + simpleClassName + ".", "")
								.replaceFirst("private", "public"));
				writer.write("                  = ");
				writer.write("\n");
				writer.write("                          null;");

			} else {
				String propertyIdString = VisitedObjects.getInstance()
						.getVisitedObjectMap().get(value);

				if (propertyIdString == null) {
					propertyIdString = AlphaIdGenerator.getInstance()
							.getNextId(collectionDataObjectClassName);
					VisitedObjects.getInstance().getVisitedObjectMap().put(
							value, propertyIdString);

					writeOneDimensionalCollection(value, dirName,
							propertyIdString, projectIdString,
							jataUUIDString,
							collectionDataObjectClassName, unAlteredCollectionDataObjectClassName, ownerPackageName + "." + simpleClassName);
				}
				String dataObjectClassName = getCollectionDataObjectClassName(
						collectionDataObjectClassName, propertyIdString);

				writer.write("           "
						+ fieldGenericString.replaceFirst(
								packageName + "." + simpleClassName + ".", "")
								.replaceFirst("private", "public"));
				writer.write(" = ");
				writer.write("\n");
				writer.write("                          " + dataObjectClassName
						+ ".getData()");
				writer.write(";");
			}

		}
	}


	private static void writeArrayElements(Object arrayObject, String dirName,
			Writer writer, int tabDepth, String projectIdString, String jataUUIDString) throws IOException,
			IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {

		writer.write("                                {  ");
		for (int t = 0; t < tabDepth; t++) {
			writer.write("   ");
		}

		int arrayLength = Array.getLength(arrayObject);
		for (int i = 0; i < arrayLength; i++) {
			Object arrayElement = Array.get(arrayObject, i);
			if (arrayElement == null) {

				writer.write("null");
				if (i != (arrayLength - 1)) {
					writer.write(",  ");
				}

			} else
			if (arrayElement.getClass().isArray()) {
				writeArrayElements(arrayElement, dirName, writer, tabDepth + 1, projectIdString, jataUUIDString);
				if (i != (arrayLength - 1)) {
					writer.write(",\n");
				}
			} else if (!isPrimitiveOrString(arrayElement)) {
				// we are dealing with non primitives here

					String propertyIdString = VisitedObjects.getInstance()
							.getVisitedObjectMap().get(arrayElement);

					if (propertyIdString == null) {
						propertyIdString = AlphaIdGenerator.getInstance()
								.getNextId(arrayElement.getClass().getName());
						VisitedObjects.getInstance().getVisitedObjectMap().put(
								arrayElement, propertyIdString);
						write(arrayElement, dirName, propertyIdString,
								projectIdString, jataUUIDString, arrayElement.getClass());
					}
					String dataObjectClassName = getDataObjectClassName(
							arrayElement.getClass(), propertyIdString);

					writer.write("                          "
							+ dataObjectClassName + ".getData()");
					if (i != (arrayLength - 1)) {
						writer.write(",");
					}

			} else {
				// we are dealing with primitives here

				if (arrayElement != null) {
					writer.write(arrayElement.toString());
				} else {
					// we have to write different entries depending on the
					// primitive type
					if ((arrayElement instanceof Integer)
							|| (arrayElement instanceof Byte)
							|| (arrayElement instanceof Short)) {
						writer.write("0");
					} else if (arrayElement instanceof Character) {
						writer.write("\'\'");
					} else if (arrayElement instanceof String) {
						writer.write("null");
					} else if (arrayElement instanceof Double) {
						writer.write("0.0d");
					} else if (arrayElement instanceof Boolean) {
						writer.write("false");
					} else if (arrayElement instanceof Long) {
						writer.write("0L");
					} else if (arrayElement instanceof Float) {
						writer.write("0.0f");
					}
				}
				if (i != (arrayLength - 1)) {
					writer.write(",  ");
				}

			}

		}
		writer.write("                                }");

	}

	public static String getSimpleName(String dataObjectClassName) {

		int index = dataObjectClassName.lastIndexOf(".");
		return dataObjectClassName.substring(index + 1, dataObjectClassName
				.length())
				+ "Collection";
	}

	public static void writeOneDimensionalCollection(Object object,
			String dirName, String lclObjectIdString, String projectIdString,
			String jataUUIDString, String dataObjectClassName, String unAlteredCollectionDataObjectClassName, String ownerClassName)
			throws IOException, IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			InvocationTargetException {

		if (lclObjectIdString == null) {
			lclObjectIdString = AlphaIdGenerator.getInstance().getNextId(
					dataObjectClassName);
		}

		String simpleClassName = getSimpleName(dataObjectClassName);

		String classDirs = dataObjectClassName.replace('.', File.separatorChar);

		String dataSrcPath = JATA_BASE_DIR + File.separator +"src" + File.separator + "main" + File.separator
				+ "java";

		File directory = new File(dirName + File.separator + dataSrcPath
				+ File.separator + "data" + File.separator + classDirs
				+ File.separator + lclObjectIdString);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		if (!directory.isDirectory()) {
			// we should throw an exception.
			assert false;
		}

		String outputFileName = directory + File.separator + simpleClassName
				+ ".java";

		Writer output = null;
		try {
			// use buffering
			// FileWriter always assumes default encoding is OK!
			output = new BufferedWriter(new FileWriter(outputFileName));

			output.write("package " + "data." + dataObjectClassName + "."
					+ lclObjectIdString + ";");

			output.write("\n");
			output.write("\n");

			writeImports(output);
			output.write("\n");
			output.write("\n");

			output.write("@" + JataUUId.class.getSimpleName() + "(id=\""
					+ jataUUIDString + "\")");
			output.write("\n");
			output.write("@" + JataProjectId.class.getSimpleName() + "(id=\""
					+ lclObjectIdString + "\")");
			output.write("\n");
			output.write("public class " + simpleClassName + " {");
			output.write("\n");
			output.write("\n");

			writeOneDimensionalCollectionGetDataObjectMethod(object, output,
					lclObjectIdString, projectIdString,
					jataUUIDString, dataObjectClassName,
					unAlteredCollectionDataObjectClassName, ownerClassName, dirName);
			output.write("}");
		} finally {
			// flush and close both "output" and its underlying FileWriter
			if (output != null)
				output.close();
		}

	}

	public static void writeTwoDimensionalCollection(Object object,
			String dirName, String lclObjectIdString, String projectIdString,
			String dataObjectClassName, String unAlteredCollectionDataObjectClassName, String ownerClassName, String jataUUIDString)
			throws IOException, IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			InvocationTargetException {

		if (lclObjectIdString == null) {
			lclObjectIdString = AlphaIdGenerator.getInstance().getNextId(
					dataObjectClassName);
		}

		String simpleClassName = getSimpleName(dataObjectClassName);


		String classDirs = dataObjectClassName.replace('.', File.separatorChar);

		String dataSrcPath =JATA_BASE_DIR + File.separator + "src" + File.separator + "main" + File.separator
				+ "java";

		File directory = new File(dirName + File.separator + dataSrcPath
				+ File.separator + "data" + File.separator + classDirs
				+ File.separator + lclObjectIdString);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		if (!directory.isDirectory()) {
			// we should throw an exception.
			assert false;
		}

		String outputFileName = directory + File.separator + simpleClassName
				+ ".java";

		Writer output = null;
		try {
			// use buffering
			// FileWriter always assumes default encoding is OK!
			output = new BufferedWriter(new FileWriter(outputFileName));

			output.write("package " + "data." + dataObjectClassName + "."
					+ lclObjectIdString + ";");

			output.write("\n");
			output.write("\n");

			writeImports(output);
			output.write("\n");
			output.write("\n");

			output.write("@" + JataUUId.class.getSimpleName() + "(id=\""
					+ jataUUIDString + "\")");
			output.write("\n");
			output.write("@" + JataProjectId.class.getSimpleName() + "(id=\""
					+ lclObjectIdString + "\")");
			output.write("\n");
			output.write("public class " + simpleClassName + " {");
			output.write("\n");
			output.write("\n");

			writeTwoDimensionalCollectionGetDataObjectMethod(object, output,
					lclObjectIdString, projectIdString,
					jataUUIDString, dataObjectClassName,
					unAlteredCollectionDataObjectClassName, ownerClassName, dirName);
			output.write("}");
		} finally {
			// flush and close both "output" and its underlying FileWriter
			if (output != null)
				output.close();
		}

	}

	/**
	 * @param value
	 * @param propertyIdString
	 * @return
	 */
	private static String getDataObjectClassName(Class clazz,
			String propertyIdString) {
		String dataObjectClassName = "data." + clazz.getName() + "."
				+ propertyIdString + "." + clazz.getSimpleName();
		return dataObjectClassName;
	}

	/**
	 * @param value
	 * @param propertyIdString
	 * @return
	 */
	private static String getCollectionDataObjectClassName(
			String collectionDataObjectClassName, String propertyIdString) {
		String dataObjectClassName = "data." + collectionDataObjectClassName
				+ "." + propertyIdString + "."
				+ getSimpleName(collectionDataObjectClassName);
		return dataObjectClassName;
	}

	private static void writeOneDimensionalCollectionGetDataObjectMethod(
			Object collectionObject, Writer writer, String lclObjectIdString,
			String projectIdString,

			String jataUUIDString,
			String dataObjectClassName, String unAlteredCollectionDataObjectClassName, String ownerClassName, String dirName) throws IOException,
			IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {

		writer.write("           public static "
				+ unAlteredCollectionDataObjectClassName + " getData() {");
		writer.write("\n");
		writer.write("\n");

		String genericParameter = getGenericParameterType(unAlteredCollectionDataObjectClassName);

		writer.write("               Class ownerOfCollection = "
				+ ownerClassName + ".class ;");
		writer.write("\n");
		writer.write("\n");

		// if
		// (collectionObject.getClass().isAssignableFrom(java.util.Set.class)) {
		if (java.util.Set.class.isAssignableFrom(collectionObject.getClass())) {
			writer.write("               "
					+ unAlteredCollectionDataObjectClassName
					+ " collection = new "
					+ collectionObject.getClass().getName() + "<"
					+ genericParameter + ">() ;");
		} else if (collectionObject.getClass().isAssignableFrom(
				java.util.List.class)) {
			writer.write("               "
					+ unAlteredCollectionDataObjectClassName
					+ " collection = new "
					+ collectionObject.getClass().getName() + "<"
					+ genericParameter + ">() ;");

		}
		writer.write("\n");
		writer.write("\n");

		Collection objectCollection = (Collection) collectionObject;
		for (Object objectInCollection : objectCollection) {

			if (isPrimitiveOrString(objectInCollection)) {
				writer.write("               collection.add("
						+ objectInCollection + ") ;");
			} else {
				String propertyIdString = VisitedObjects.getInstance()
						.getVisitedObjectMap().get(objectInCollection);

				if (propertyIdString == null) {
					propertyIdString = AlphaIdGenerator.getInstance()
							.getNextId(objectInCollection.getClass().getName());
					VisitedObjects.getInstance().getVisitedObjectMap().put(
							objectInCollection, propertyIdString);

					write(objectInCollection, dirName, propertyIdString,
							projectIdString, jataUUIDString, objectCollection.getClass());
				}
				String dataObjectClassNameForObjectInCollection = getDataObjectClassName(
						objectInCollection.getClass(), propertyIdString);
				writer.write("              collection.add("
						+ dataObjectClassNameForObjectInCollection
						+ ".getData()) ;");
			}
			writer.write("\n");

		}

		writer.write("\n");
		writer.write("               return collection;");

		writer.write("\n");
		writer.write("           }");
		writer.write("\n");

	}

	private static void writeTwoDimensionalCollectionGetDataObjectMethod(
			Object collectionObject, Writer writer, String lclObjectIdString,
			String projectIdString,

			String jataUUIDString,
			String dataObjectClassName, String unAlteredCollectionDataObjectClassName, String ownerClassName, String dirName) throws IOException,
			IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {

		writer.write("           public static "
				+ unAlteredCollectionDataObjectClassName + " getData() {");
		writer.write("\n");
		writer.write("\n");

		String[] genericParameters = getGenericParameterTypes(unAlteredCollectionDataObjectClassName);

		writer.write("               Class ownerOfCollection = "
				+ ownerClassName + ".class ;");
		writer.write("\n");
		writer.write("\n");

		// if
		// (collectionObject.getClass().isAssignableFrom(java.util.Set.class)) {
		if (java.util.Map.class.isAssignableFrom(collectionObject.getClass())) {
			writer.write("               "
					+ unAlteredCollectionDataObjectClassName + " map = new "
					+ collectionObject.getClass().getName() + "<"
					+ genericParameters[0] + "," + genericParameters[1]
					+ ">() ;");
		}

		writer.write("\n");
		writer.write("\n");

		Map map = (Map) collectionObject;
		for (Object key : map.keySet()) {

			if (isPrimitiveOrString(key)) {
				if (key.getClass().isPrimitive()) {
					writer.write("               map.put(" + key + ",");
				}
				else {
					writer.write("               map.put(\"" + key + "\",");
				}
			} else {
				String propertyIdString = VisitedObjects.getInstance()
						.getVisitedObjectMap().get(key);

				if (propertyIdString == null) {
					propertyIdString = AlphaIdGenerator.getInstance()
							.getNextId(key.getClass().getName());
					//
					VisitedObjects.getInstance().getVisitedObjectMap().put(key,
							propertyIdString);

					write(key, dirName, propertyIdString, projectIdString, jataUUIDString, key.getClass());
				}
				String dataObjectClassNameForObjectInCollection = getDataObjectClassName(
						key.getClass(), propertyIdString);
				writer.write("              map.put("
						+ dataObjectClassNameForObjectInCollection
						+ ".getData()) , ");
			}
			Object value = map.get(key);

			if (isPrimitiveOrString(value)) {
				if (value.getClass().isPrimitive()) {
					writer.write("  " + value + ");");
				}
				else {
					writer.write("  \"" + value + "\");");
				}
			} else {
				String propertyIdString = VisitedObjects.getInstance()
						.getVisitedObjectMap().get(value);

				if (propertyIdString == null) {
					propertyIdString = AlphaIdGenerator.getInstance()
							.getNextId(value.getClass().getName());
					//

					VisitedObjects.getInstance().getVisitedObjectMap().put(
							value, propertyIdString);
					write(value, dirName, propertyIdString, projectIdString, jataUUIDString, value.getClass());
				}
				String dataObjectClassNameForObjectInCollection = getDataObjectClassName(
						value.getClass(), propertyIdString);
				writer.write(" " + dataObjectClassNameForObjectInCollection
						+ ".getData()); ");
			}

			writer.write("\n");

		}

		writer.write("\n");
		writer.write("               return map;");

		writer.write("\n");
		writer.write("           }");
		writer.write("\n");

	}

	private static boolean isPrimitiveOrString(Object object) {
		return ((object instanceof java.lang.Integer)
				|| (object instanceof java.lang.Long)
				|| (object instanceof java.lang.Double)
				|| (object instanceof java.lang.Float)
				|| (object instanceof java.lang.Character)
				|| (object instanceof java.lang.Byte)
				|| (object instanceof java.lang.Integer)
				|| (object instanceof java.lang.Boolean)
				|| (object instanceof java.lang.Short) || (object instanceof java.lang.String));
	}

	private static String[] getGenericParameterTypes(String genericDeclaration) {
		int beginIndexOf = genericDeclaration.indexOf("<");
		int endIndexOf = genericDeclaration.lastIndexOf(">");
		String genericParameterTypes = genericDeclaration.substring(
				beginIndexOf + 1, endIndexOf);
		return genericParameterTypes.split(",");

	}

	private static String getGenericParameterType(String genericDeclaration) {
		int beginIndexOf = genericDeclaration.indexOf("<");
		int endIndexOf = genericDeclaration.lastIndexOf(">");
		return genericDeclaration.substring(beginIndexOf + 1, endIndexOf);
	}

	private static void writeGetDataObjectMethod(Class clazz, Writer writer,
			String lclObjectIdString) throws IOException {

		writer.write("           public static " + clazz.getName()
				+ " getData() {");
		writer.write("\n");

		writer
				.write("                String dataObjectClassName = "
						+ getDataObjectClassName(clazz, lclObjectIdString)
						+ ".class.getName();");
		writer.write("\n");
		writer
				.write("                return ("
						+ clazz.getName()
						+ ") "
						+ org.jata.write.util.DataObjectUtil.class
								.getName() + ".getData(dataObjectClassName,"
						+ clazz.getName() + ".class.getName());");
		writer.write("\n");
		writer.write("           }");
		writer.write("\n");

	}

	private static void writeImports(Writer writer) throws IOException {
		writer.write("import " + JataProjectId.class.getName() + ";");
		writer.write("\n");
		writer.write("import " + JataUUId.class.getName() + ";");
		writer.write("\n");
	}

	private static boolean isStringType(Field field) {
		return field.getType().equals(String.class);
	}

	private static boolean isObjectBaseClass(Object object, Class clazz) {
		return !object.getClass().getName().equals(clazz.getName());
	}

	private static boolean isStatic(Field field) {
		String string = field.toGenericString();
		int indexOfStatic = string.indexOf(" static ");
		boolean returnStatic = (indexOfStatic >= 0);
		return returnStatic;
	}

	private static boolean isGeneric(Field field) {
		Type genericType = field.getGenericType();
		String genericTypeName = genericType.toString();
		return (genericTypeName.indexOf("<") >= 0);
	}

	private static boolean isTwoDimensionCollectionType(Field field) {
		return field.getType().isAssignableFrom(Map.class);
	}

	private static boolean isSingleDimensionCollectionType(Field field) {
		return (field.getType().isAssignableFrom(Set.class) || field.getType()
				.isAssignableFrom(List.class));
	}

	public static final String GET_DATA_METHOD_NAME = "getData";

}
