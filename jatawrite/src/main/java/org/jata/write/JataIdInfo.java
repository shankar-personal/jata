package org.jata.write;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class JataIdInfo {

	private static final String JATA_VERSION_PROPERTY_NAME = "jataVersion";

	private static final String JATA_FQ_CLASS_PROPERTY_NAME = "jataFQClassName";

	private static final String JATA_UUID_STRING_PROPERTY_NAME = "jataUUIdString";

	private static final String PROJECT_NAME_PROPERTY_NAME = "projectName";

	private String jataFQClassName;

	private String jataVersion;

	private String jataUUIdString;

	private String projectName;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public JataIdInfo(String jataFQClassName, String jataVersion, String projectName, String jataUUIdString) {
		super();
		this.jataFQClassName = jataFQClassName;
		this.jataVersion = jataVersion;

		this.jataUUIdString = jataUUIdString;
		this.projectName = projectName;
	}

	public JataIdInfo() {
		super();
	}

	public String getJataFQClassName() {
		return jataFQClassName;
	}

	public void setJataFQClassName(String jataFQClassName) {
		this.jataFQClassName = jataFQClassName;
	}

	public String getJataUUIdString() {
		return jataUUIdString;
	}

	public void setJataUUIdString(String jataUUIdString) {
		this.jataUUIdString = jataUUIdString;
	}

	public String getJataVersion() {
		return jataVersion;
	}

	public void setJataVersion(String jataVersion) {
		this.jataVersion = jataVersion;
	}

	public static void writeAsProperties(String propsFilePath,
			JataIdInfo jataIdInfo) throws IOException {
		Properties props = new Properties();

		if (jataIdInfo.getJataVersion() != null) {
			props.put(JATA_VERSION_PROPERTY_NAME, jataIdInfo.getJataVersion());
		} else {
			props.put(JATA_VERSION_PROPERTY_NAME, "");
		}

		if (jataIdInfo.getJataUUIdString() != null) {
			props.put(JATA_UUID_STRING_PROPERTY_NAME, jataIdInfo
					.getJataUUIdString());
		} else {
			props.put(JATA_UUID_STRING_PROPERTY_NAME, "");
		}

		props.put(JATA_FQ_CLASS_PROPERTY_NAME, jataIdInfo.getJataFQClassName());
		if (jataIdInfo.getProjectName() != null) {
			props.put(PROJECT_NAME_PROPERTY_NAME, jataIdInfo.getProjectName());
		} else {
			props.put(PROJECT_NAME_PROPERTY_NAME, "");
		}
		File propsFile = new File(propsFilePath);
		propsFile.createNewFile();
		OutputStream out = new FileOutputStream(propsFile);
		props.store(out, "");

	}

	public static JataIdInfo readFromProperties(String propsFilePath) throws IOException
	{
		Properties props = new Properties();
		InputStream inStream = new FileInputStream(propsFilePath);
		props.load(inStream);
		JataIdInfo jataIdInfo = new JataIdInfo();
	    jataIdInfo.setJataFQClassName(props.getProperty(JATA_FQ_CLASS_PROPERTY_NAME));
	    jataIdInfo.setJataUUIdString(props.getProperty(JATA_UUID_STRING_PROPERTY_NAME));
	    jataIdInfo.setJataVersion(props.getProperty(JATA_VERSION_PROPERTY_NAME));
	    jataIdInfo.setProjectName(props.getProperty(PROJECT_NAME_PROPERTY_NAME));
		return jataIdInfo;

	}



}
