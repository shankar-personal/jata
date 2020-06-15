package org.jata.write.poms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class TopLevelAnnotationPOMWriter {

	public static void write(Class clazz, String dirName, String idString) throws IOException {
		write(clazz, dirName, idString, null);
	}

	public static void write(Class clazz, String dirName, String idString, String version) throws IOException {
		String outputFileName = dirName + File.separator + "pom.xml";
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
			writer.write("   <artifactId>" + idString +"</artifactId>\n");
			writer.write("   <version> " + version+ "</version>\n");
			writer.write("   <packaging> pom </packaging>\n");
			writer.write("   <description></description>\n");
			writer.write("   <modules>\n");
			writer.write("        <module> validation </module>\n");
			writer.write("        <module> annotations </module>\n");
			writer.write("   </modules>\n");
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
