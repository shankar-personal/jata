package org.jata.write.poms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.jata.write.JataWriter;


public class ValidationPOMWriter {

	public static void write(Object object, String dirName, String idString) throws IOException {
		write(object, dirName, idString, null);
	}

	public static String getValidationProjectName( String idString) {
		return idString +"-validation" ;
	}

	public static void write(Object object, String dirName, String idString, String version) throws IOException {

		Class clazz = object.getClass();
		writeForClass(dirName, idString, version, clazz);
	}

	public static void writeForClass(String dirName, String idString, String version, Class clazz) throws IOException {
		String outputFileName = dirName + File.separator + "validation" + File.separator +  "pom.xml";
		String packageName = clazz.getPackage().getName();

		if (version == null) {
			version = "0.0.1";
		}

		Writer writer = null;
		try {

			writer = new BufferedWriter(new FileWriter(outputFileName));

			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			writer.write("<project>\n");
		    writer.write("   <modelVersion>4.0.0</modelVersion>\n");
			writer.write("   <groupId> " + packageName + "</groupId>\n");
			writer.write("   <artifactId>" + getValidationProjectName(idString) + "</artifactId>\n");
			writer.write("   <version> " + version+ "</version>\n");
			writer.write("   <description></description>\n");
			writer.write("   <parent>\n");
			writer.write("            <groupId> " + packageName + " </groupId>\n");
			writer.write("            <artifactId>" + idString + "</artifactId>\n");
			writer.write("            <version>" + JataWriter.JATA_WRITER_VERSION +"</version>\n");
			writer.write("   </parent>\n");
			writer.write("   <dependencies>\n");
			writer.write("        <dependency>\n");
			writer.write("            <groupId>org.jata</groupId>\n");
			writer.write("            <artifactId>jatawrite</artifactId>\n");
			writer.write("            <version>" + JataWriter.JATA_WRITER_VERSION +"</version>\n");
			writer.write("        </dependency>\n");
			writer.write("   </dependencies>\n");
			writer.write("   <build>\n");
			writer.write("        <resources>\n");
			writer.write("         <resource>\n");
			writer.write("           <directory>${basedir}/src/main/java</directory>\n");
			writer.write("         </resource>\n");
			writer.write("       </resources>\n");
			writer.write("       <plugins>\n");
			writer.write("            <plugin>\n");
			writer.write("                <groupId>org.apache.maven.plugins</groupId>\n");
			writer.write("                <artifactId>maven-compiler-plugin</artifactId>\n");
			writer.write("                <configuration>\n");
			writer.write("                   <source>1.5</source>\n");
			writer.write("                   <target>1.5</target>\n");
			writer.write("                </configuration>\n");
			writer.write("            </plugin>\n");
			writer.write("       </plugins>\n");
			writer.write("   </build>\n");
			writer.write("</project>\n");



		} finally {
            //	flush and close both "output" and its underlying FileWriter
			if (writer != null)
				writer.close();
		}
	}

}
