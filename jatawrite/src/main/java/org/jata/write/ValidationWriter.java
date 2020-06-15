package org.jata.write;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.jata.write.util.AccessorUtil;


public class ValidationWriter {

	/*
	 * TODO: Deal with inheritance the same way we dealt with in JataWriter.
	 * TODO: We also need to add no args constructor.
	 */
	public static void write(String dirName, Class clazz) throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException {
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
		String simpleClassName = clazz.getSimpleName();
		String packageName = clazz.getPackage().getName();

		Class superClass = clazz.getSuperclass();

		Field[] declaredFields = clazz.getDeclaredFields();

		int lastIndex = className.lastIndexOf(".");
		String classDirs = className.substring(0, lastIndex).replace('.',
				File.separatorChar);

		String dataSrcPath = "validation" + File.separator
				+ "src" + File.separator + "main" + File.separator + "java";

		File directory = new File(dirName + File.separator + dataSrcPath
				+ File.separator + classDirs);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		if (!directory.isDirectory()) {
			// we should throw an exception.
			assert false;
		}

		String outputFileName = directory + File.separator + simpleClassName
				+ ".java";

		Writer writer = null;
		try {

			writer = new BufferedWriter(new FileWriter(outputFileName));

			writer.write("package " + packageName + ";");

			writer.write("\n");
			writer.write("\n");
			writer.write("\n");
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

			// write no args constructor

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
									+ componentType.getName());
							for (int i = 0; i < dimensionCount; i++) {
								writer.write("[]");

							}
							writer.write("  " + fieldName + " = null ;");

						} else if (lhsFieldGenericString.indexOf("<") >= 0) {

							String typeName = null;
							typeName = field.getGenericType().toString();

							writer.write("           " + "public static "
									+ typeName + " " + fieldName + " = null ;");

						} else {

							writer.write("           " + "public static "
									+ fieldType.getName() + " " + fieldName);
							if (fieldType.isPrimitive()) {
								writer.write(" ;");
							} else {
								writer.write(" = null ;");
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
									+ componentType.getName());
							for (int i = 0; i < dimensionCount; i++) {
								writer.write("[]");

							}
							writer.write("  " + fieldName + " = null ;");

						} else if (lhsFieldGenericString.indexOf("<") >= 0) {

							String typeName = null;
							typeName = field.getGenericType().toString();

							// this implies generic is involved
							writer.write("           " + "public " + typeName
									+ " " + fieldName + " = null ;");

						} else {
							writer.write("           " + "public "
									+ fieldType.getName() + " " + fieldName);
							if (fieldType.isPrimitive()) {
								writer.write(" ;");
							} else {
								writer.write(" = null ;");
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

}
