package org.jata.write;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jata.write.poms.AnnotationTemplatePOMWriter;
import org.jata.write.poms.TopLevelAnnotationPOMWriter;
import org.jata.write.poms.ValidationPOMWriter;
import org.jata.write.util.AccessorUtil;
import org.jata.write.util.BuildFailedException;
import org.jata.write.util.MavenBuildUtil;


public class AnnotationTemplateWriter {

	public static final String ANNOTATIONS_BASE_DIR = "annotations";
	public static final String ANNOT_PACAKGE_RPEFIX = "annot.";


	public static JataIdInfo write(String dirName, String className,
			String jataProjectIdString, String version, List<Class<?>> annotationClasses) throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException,
			InvocationTargetException, ClassNotFoundException,
			BuildFailedException {
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			if (classLoader == null) {
				classLoader = AnnotationTemplateWriter.class.getClassLoader();
			}

			if (jataProjectIdString == null) {
				jataProjectIdString = UUID.randomUUID().toString();
			}

			String jataUUIDString = UUID.randomUUID().toString();

			Class clazz = classLoader.loadClass(className);
			write(dirName, clazz, jataProjectIdString, version, jataUUIDString);
			TopLevelAnnotationPOMWriter.write(clazz, dirName,
					jataProjectIdString);
			AnnotationTemplatePOMWriter.write(clazz, dirName,
					jataProjectIdString);

			Map<Class, Boolean> visitedClassMap = AnnotationTWVisitedClasses
					.getInstance().getVisitedClassSet();
			for (Class visitedClass : visitedClassMap.keySet()) {
				ValidationWriter.write(dirName, visitedClass);
			}
			ValidationPOMWriter.writeForClass(dirName, jataProjectIdString,
					version, clazz);
			if (annotationClasses != null) {
				for (Class<?> annotationClass : annotationClasses) {
					AnnotationValidationWriter.write(dirName, annotationClass);
				}
			}

			JataIdInfo jataIdInfo = new JataIdInfo(className, version, jataProjectIdString, jataUUIDString);
			String resourcesDirPath = dirName + File.separator + ANNOTATIONS_BASE_DIR
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
					+ JataWriter.JATA_ID_INFO_PROPERTIES_FILE_NAME;
			JataIdInfo.writeAsProperties(jataIdPropsFileName, jataIdInfo);

			MavenBuildUtil.buildGeneratedJataProject(dirName);
			return jataIdInfo;
		} finally {
			AnnotationTWVisitedClasses.getInstance().reset();
		}


	}

	public static void write(String dirName, Class clazz, String jataProjectIdString, String version, String jataUUIDString)
			throws IOException, IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			InvocationTargetException {
		assert clazz != null;

		if (clazz.isPrimitive()) {
			return;
		}

		if (clazz.isArray()) {
			return;
		}

		String className = clazz.getName();
		if (className.startsWith("java.") || className.startsWith("javax.")) {
			return;
		}


		Boolean hasClassBeenVisited = AnnotationTWVisitedClasses.getInstance().getVisitedClassSet().get(clazz);

		if (hasClassBeenVisited !=null && hasClassBeenVisited ) {
			return;
		}

		AnnotationTWVisitedClasses.getInstance().getVisitedClassSet().put(clazz, true);
		String simpleClassName = clazz.getSimpleName();
		String packageName =  ANNOT_PACAKGE_RPEFIX + clazz.getPackage().getName();

		Class superClass = clazz.getSuperclass();

		Field[] declaredFields = clazz.getDeclaredFields();

		int lastIndex = className.lastIndexOf(".");
		String classDirs = className.substring(0, lastIndex).replace('.',
				File.separatorChar);

		String dataSrcPath = ANNOTATIONS_BASE_DIR
			+ File.separator + "src" + File.separator + "main"
				+ File.separator + "java" + File.separator + "annot";

		File directory = new File(dirName + File.separator + dataSrcPath
				+ File.separator +  classDirs);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		if (!directory.isDirectory()) {
			// we should throw an exception.
			assert false;
		}

		String outputFileName = directory + File.separator + simpleClassName + ".java";

		Writer writer = null;
		try {

			writer = new BufferedWriter(new FileWriter(outputFileName));

			writer.write("package " + packageName + ";");

			writer.write("\n");
			writer.write("\n");
			writer.write("\n");
			writer.write("\n");
			writer.write("@" + JataUUId.class.getName() + "(id=\""
					+ jataUUIDString + "\")");
			writer.write("\n");

			writer.write("@" + ATTypeLevelId.class.getName() + "(id=\""
					+ jataProjectIdString + "\"");

			if ((jataProjectIdString != null) && (!jataProjectIdString.equals(""))) {
				writer.write(", projectName=\"" + jataProjectIdString + "\"");
			}
			if ((version != null) && (!version.equals(""))) {
				writer.write(", version=\"" + version + "\"");
			}
			writer.write(")");
			writer.write("\n");
			if (clazz.isEnum()) {
				writer.write("public enum " + simpleClassName + " {");
				writer.write("\n");
				writer.write("            ");
				Object[] enumConstants = clazz.getEnumConstants();
				for (int i = 0; i < enumConstants.length; i++) {
					Object enumConstant = enumConstants[i];

					writer.write("  " + enumConstant.toString());
					if (i != (enumConstants.length - 1)) {
						writer.write("   ,");

					}
				}

				writer.write("\n");
				writer.write("\n");
				writer.write("}");
				return;

			} else if (superClass.getName().equals(
					java.lang.Object.class.getName())) {

				writer.write("public class " + simpleClassName + " {");
			} else {

				writer.write("public class " + simpleClassName + " extends "
						+ superClass.getName() + " {");
			}

			writer.write("\n");
			writer.write("\n");
			writer.write("           " +"public " + simpleClassName + " () {} ");
			writer.write("\n");

			for (Field field : declaredFields) {

				boolean fieldAccessible = AccessorUtil.isAccessible(field);

				if (fieldAccessible) {
					String fieldName = field.getName();
					Class<?> fieldType = field.getType();
					String fieldGenericString = field.toGenericString();
					String[] fieldGenericStrings = fieldGenericString
							.split(fieldName);
					String lhsFieldGenericString = fieldGenericStrings[0];
					if (AccessorUtil.isStatic(field)) {

						if (fieldType.isArray()) {
							int dimensionCount = 1;
							Class componentType = fieldType.getComponentType();
							while (componentType.isArray()) {
								componentType = componentType
										.getComponentType();
								dimensionCount++;

							}
							writer.write("           " + "public static "
									+ ANNOT_PACAKGE_RPEFIX + componentType.getName());
							for (int i = 0; i < dimensionCount; i++) {
								writer.write("[]");

							}
							writer.write("  " + fieldName + " = null ;");
							write(dirName, componentType, jataProjectIdString, version, jataUUIDString);

						} else if (lhsFieldGenericString.indexOf("<") >= 0) {

							String typeName = null;
							typeName = field.getGenericType().toString();

							writer.write("           " + "public static "
									+ ANNOT_PACAKGE_RPEFIX + typeName + " " + fieldName + " = null ;");

							write(dirName, field.getType(), jataProjectIdString, version, jataUUIDString);

						} else {


							if (fieldType.isPrimitive()) {
								writer
										.write("           " + "public static "
												+ fieldType.getName() + " "
												+ fieldName);
								writer.write(" ;");
							} else {
								if (isStringType(field)) {
									writer.write("           "
											+ "public static "
											+ ANNOT_PACAKGE_RPEFIX
											+ fieldType.getName() + " "
											+ fieldName);
								} else {
									writer.write("           "
											+ "public static "
											+ ANNOT_PACAKGE_RPEFIX
											+ fieldType.getName() + " "
											+ fieldName);
								}
								writer.write(" = null ;");
								write(dirName, field.getType(),
										jataProjectIdString, version,
										jataUUIDString);
							}
						}
					} else {
						if (fieldType.isArray()) {
							int dimensionCount = 1;
							Class componentType = fieldType.getComponentType();
							while (componentType.isArray()) {
								componentType = componentType
										.getComponentType();
								dimensionCount++;

							}
							writer.write("           " + "public "
									+ ANNOT_PACAKGE_RPEFIX + componentType.getName());
							for (int i = 0; i < dimensionCount; i++) {
								writer.write("[]");

							}
							writer.write("  " + fieldName + " = null ;");
							write(dirName, componentType, jataProjectIdString, version, jataUUIDString);

						} else if (lhsFieldGenericString.indexOf("<") >= 0) {

							String typeName = null;
							typeName = field.getGenericType().toString();

							// this implies generic is involved
							writer.write("           " + "public " + ANNOT_PACAKGE_RPEFIX + typeName
									+ " " + fieldName + " = null ;");
							// TODO: We need to fix this.
							write(dirName, field.getType(), jataProjectIdString, version, jataUUIDString);

						} else {

							if (fieldType.isPrimitive()) {
								writer
										.write("           " + "public "
												+ fieldType.getName() + " "
												+ fieldName);
								writer.write(" ;");
							} else {
								if (isStringType(field)) {
									writer.write("           " + "public "
											+ fieldType.getName() + " "
											+ fieldName);

								} else {
									writer.write("           " + "public "
											+ ANNOT_PACAKGE_RPEFIX
											+ fieldType.getName() + " "
											+ fieldName);
								}
								writer.write(" = null ;");
								write(dirName, field.getType(),
										jataProjectIdString, version,
										jataUUIDString);
							}
						}

					}

					writer.write("\n");
					writer.write("\n");

				}
			}

			writer.write("}");
		} finally {
			// flush and close both "output" and its underlying FileWriter
			if (writer != null)
				writer.close();
		}

	}

	private static boolean isStringType(Field field) {
		return field.getType().equals(String.class);
	}

}
