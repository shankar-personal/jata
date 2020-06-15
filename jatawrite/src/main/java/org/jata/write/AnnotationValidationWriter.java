package org.jata.write;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class AnnotationValidationWriter {

	/*
	 * TODO: Deal with inheritance the same way we dealt with in JataWriter.
	 * TODO: We also need to add no args constructor.
	 */
	public static void write(String dirName, Class<?> clazz) throws IOException,
			IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException {
		assert clazz != null;

		assert clazz.isAnnotation();

		String className = clazz.getName();
		if (className.startsWith("java.") || className.startsWith("javax.")) {
			return;
		}
		String simpleClassName = clazz.getSimpleName();
		String packageName = clazz.getPackage().getName();


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

			// todo: we need to write the retentiontype annotations.
			Retention retentionAnnotation =clazz.getAnnotation(Retention.class);

			RetentionPolicy retentionPolicy = retentionAnnotation.value();

			if (retentionPolicy.equals(RetentionPolicy.CLASS)) {
				writer.write("@" + Retention.class.getName()
						+ "(" + RetentionPolicy.class.getName() + ".CLASS)");
			} else if (retentionPolicy.equals(RetentionPolicy.RUNTIME)) {
				writer.write("@" + Retention.class.getName()
						+ "(" + RetentionPolicy.class.getName() + ".RUNTIME)");
			} else if (retentionPolicy.equals(RetentionPolicy.SOURCE)) {
				writer.write("@" + Retention.class.getName()
						+ "(" + RetentionPolicy.class.getName() + ".SOURCE)");
			}
			writer.write("\n");

			Target targetAnnotation = clazz.getAnnotation(Target.class);
			ElementType[] elementTypes = targetAnnotation.value();

			if (elementTypes.length == 1) {
				ElementType elementType = elementTypes[0];
				writeElementTypeForSingleElementType(writer, elementType);

			} else {
				writeMultipleElementTypeProlog(writer);
				int position = 0;
				for (ElementType elementType : elementTypes) {
					writeSingleElementTypeOfSeveralElementTypes(writer,
							elementType, position, elementTypes.length);
					position++;
				}
				writeMultipleElementTypeEpilog(writer);

			}
			writer.write("\n");

			writer.write("public @interface " + simpleClassName + " {");
			writer.write("\n");
			writer.write("\n");

			Method[] declaredMethods = clazz.getDeclaredMethods();
			for (Method method : declaredMethods) {
				Object defaultValue = method.getDefaultValue();
				String methodName = method.getName();
				Class<?> returnType = method.getReturnType();

				if (returnType.getName().equals(String.class.getName())) {
					writer.write("   " + returnType.getSimpleName() + "    "
							+ methodName + " ()" + "    default  \""
							+ defaultValue + "\";");
				}
				else {
					writer.write("   " + returnType.getSimpleName() + "    "
							+ methodName + " ()" + "    default  "
							+ defaultValue + " ;");

				}

				writer.write("\n");
				writer.write("\n");

			}
			writer.write("}");
		} finally {
			// flush and close both "output" and its underlying FileWriter
			if (writer != null)
				writer.close();
		}

	}

	private static void writeMultipleElementTypeProlog(Writer writer) throws IOException{
		writer.write("@" + Target.class.getName()
				+ "({");

	}
	private static void writeSingleElementTypeOfSeveralElementTypes(Writer writer,
			ElementType elementType, int position, int numberOfElementTypes) throws IOException{
		if (position != 0) {
			writer.write(", ");

		}
		if (elementType.equals(ElementType.ANNOTATION_TYPE)) {
			writer.write(ElementType.class.getName() + ".ANNOTATION_TYPE");
		}
		else if (elementType.equals(ElementType.CONSTRUCTOR)) {
			writer.write(ElementType.class.getName() + ".CONSTRUCTOR");
		}
		else if (elementType.equals(ElementType.FIELD)) {
			writer.write(ElementType.class.getName() + ".FIELD");
		}
		else if (elementType.equals(ElementType.LOCAL_VARIABLE)) {
			writer.write(ElementType.class.getName() + ".LOCAL_VARIABLE");
		}
		else if (elementType.equals(ElementType.METHOD)) {
			writer.write(ElementType.class.getName() + ".METHOD");
		}
		else if (elementType.equals(ElementType.PACKAGE)) {
			writer.write(ElementType.class.getName() + ".PACKAGE");
		}
		else if (elementType.equals(ElementType.PARAMETER)) {
			writer.write(ElementType.class.getName() + ".PARAMETER");
		}
		else if (elementType.equals(ElementType.TYPE)) {
			writer.write(ElementType.class.getName() + ".TYPE");
		}

	}
	private static void writeMultipleElementTypeEpilog(Writer writer) throws IOException{
		writer.write("})");

	}

	private static void writeElementTypeForSingleElementType(Writer writer, ElementType elementType) throws IOException {
		if (elementType.equals(ElementType.ANNOTATION_TYPE)) {
			writer.write("@" + Target.class.getName()
					+ "(" + ElementType.class.getName() +".ANNOTATION_TYPE)");
		}
		else if (elementType.equals(ElementType.CONSTRUCTOR)) {
			writer.write("@" + Target.class.getName()
					+ "(" + ElementType.class.getName() +".CONSTRUCTOR)");
		}
		else if (elementType.equals(ElementType.FIELD)) {
			writer.write("@" + Target.class.getName()
					+ "(" + ElementType.class.getName() +".FIELD)");
		}
		else if (elementType.equals(ElementType.LOCAL_VARIABLE)) {
			writer.write("@" + Target.class.getName()
					+ "(" + ElementType.class.getName() +".LOCAL_VARIABLE)");
		}
		else if (elementType.equals(ElementType.METHOD)) {
			writer.write("@" + Target.class.getName()
					+ "(" + ElementType.class.getName() +".METHOD)");
		}
		else if (elementType.equals(ElementType.PACKAGE)) {
			writer.write("@" + Target.class.getName()
					+ "(" + ElementType.class.getName() +".PACKAGE)");
		}
		else if (elementType.equals(ElementType.PARAMETER)) {
			writer.write("@" + Target.class.getName()
					+ "(" + ElementType.class.getName() +".PARAMETER)");
		}
		else if (elementType.equals(ElementType.TYPE)) {
			writer.write("@" + Target.class.getName()
					+ "(" + ElementType.class.getName() +".TYPE)");
		}
	}

}
