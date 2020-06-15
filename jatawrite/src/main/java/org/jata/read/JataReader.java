package org.jata.read;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.jata.dynamic.AmbiguosMethodException;
import org.jata.dynamic.Dynamic;
import org.jata.write.ATTypeLevelId;
import org.jata.write.AnnotationTemplateWriter;
import org.jata.write.JataIdInfo;
import org.jata.write.JataUUId;
import org.jata.write.JataWriter;

public class JataReader {

	public final static String APPHOME_PROP_NAME = "app.home";

	public final static String LIB_DIR = "lib";

	public final static String VALIDATION = "vld";

	public final static String JATA = "jata";

	public final static String JAR_SUFFIX = ".jar";

	/**
	 * If there are no jataFQClassName classes in the jars used,
	 * we return a null. If several matches happen, currently we return the
	 * first match. The order of lookup is an alphabetical sorting of the urls in the lib directory
	 * in you app.home directory.
	 * @param jataFQClassName
	 * @param projectName
	 * @param version
	 * @return
	 * @throws JataReadFailureException
	 */
	public static Object read(String jataFQClassName, String projectName,
			String version) throws JataReadFailureException {
		return read(jataFQClassName, projectName,
			version, "");

	}
	/**
	 * @param projectName
	 *            project name
	 * @param version
	 *            version
	 * @param jataUUID TODO
	 * @return the object
	 * @throws JataReadFailureException
	 */
	public static Object read(String jataFQClassName, String projectName,
			String version, String jataUUID) throws JataReadFailureException {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			classLoader = JataReader.class.getClassLoader();

		}
		URL[] urls = getAppLibUrls();

		return read(jataFQClassName, projectName, version, classLoader, urls,
				jataUUID);
	}
	private static URL[] getAppLibUrls() {
		String appHomeDirPath = System.getProperty(APPHOME_PROP_NAME);
		if (appHomeDirPath == null) {
			appHomeDirPath = ".";
		}

		// TODO: we need to load these jars infrequently.

		String jataDirPath = appHomeDirPath + File.separator + LIB_DIR
				+ File.separator + JATA;
		File jataDir = new File(jataDirPath);

		File[] jataJarFiles = jataDir.listFiles(new FileFilter() {

			public boolean accept(File pathname) {

				return pathname.isFile()
						&& pathname.getName().endsWith(JAR_SUFFIX);
			}
		});

		URL[] urls = new URL[jataJarFiles.length];
		for (int i = 0; i < jataJarFiles.length; i++) {

			try {
				urls[i] = jataJarFiles[i].toURI().toURL();
			} catch (MalformedURLException e) {

				throw new JataReadFailureException(e);
			}

		}
		return urls;
	}

	/**
	 * @param jataIdInfoPropsFilePath
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static Object read(String jataIdInfoPropsFilePath, URL url) throws IOException {
		JataIdInfo jataIdInfo = JataIdInfo.readFromProperties(jataIdInfoPropsFilePath);
		return read(jataIdInfo.getJataFQClassName(), jataIdInfo.getProjectName(), jataIdInfo.getJataVersion(), jataIdInfo.getJataUUIdString(),
				url);

	}

	/**
	 * @param jataFQClassName
	 * @param projectName
	 * @param version
	 * @param jataUUID
	 * @param url
	 * @return
	 */
	public static Object read(String jataFQClassName, String projectName,
			String version, String jataUUID, URL url) {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			classLoader = JataReader.class.getClassLoader();

		}
		URL[] urls = new URL[1];
		urls[0] = url;
		return read(jataFQClassName, projectName, version, classLoader, urls,
				jataUUID);
	}

	/**
	 * @param jataFQClassName
	 * @param projectName
	 * @param version
	 * @param url
	 * @return
	 */
	public static Object read(String jataFQClassName, String projectName,
			String version, URL url) {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			classLoader = JataReader.class.getClassLoader();

		}
		URL[] urls = new URL[1];
		urls[0] = url;
		return read(jataFQClassName, projectName, version, classLoader, urls,
				null);
	}

	/**
	 * @param jataFQClassName
	 * @param projectName
	 * @param version
	 * @param jataUUID
	 * @param classLoader
	 * @param url
	 * @return
	 */
	public static Object read(String jataFQClassName, String projectName,
			String version, String jataUUID, ClassLoader classLoader, URL url) {
		URL[] urls = new URL[1];
		urls[0] = url;
		return read(jataFQClassName, projectName, version, classLoader, urls,
				jataUUID);
	}

	/**
	 *

	 * @param jataFQClassName
	 * @param projectName
	 * @param version
	 * @param classLoader
	 * @param url
	 * @return
	 */
	public static Object read(String jataFQClassName, String projectName,
			String version, ClassLoader classLoader, URL url) {
		URL[] urls = new URL[1];
		urls[0] = url;
		return read(jataFQClassName, projectName, version, classLoader, urls,
				null);
	}

	/**
	 * If there are no annotTemplateFQClassName classes in the jars used,
	 * we return an empty annotationList.
	 * We will someday make this public.
	 * @param methodName
	 * @param parameterTypes
	 * @param annotationClass
	 * @param annotTemplateFQClassName
	 * @param projectName
	 * @param version
	 * @param jataUUId
	 * @return
	 */
	private static List<Annotation> readMethodAnnotations(String methodName,
			Class<?>[] parameterTypes, Class<?> annotationClass,
			String annotTemplateFQClassName, String projectName,
			String version, String jataUUId) {
		annotTemplateFQClassName = fixAnnotTemplateFQClassName(annotTemplateFQClassName);
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			classLoader = JataReader.class.getClassLoader();

		}
		URL[] urls = getAppLibUrls();
		return readMethodAnnotations(methodName, parameterTypes,
				annotationClass, annotTemplateFQClassName, projectName,
				version, classLoader, urls, jataUUId);

	}

	/**
	 * If there are no annotTemplateFQClassName classes in the jars used,
	 * we return an empty annotationList.
	 * We will someday make this public.
	 * @param methodName
	 * @param parameterTypes
	 * @param annotationClass
	 * @param annotTemplateFQClassName
	 * @param projectName
	 * @param version
	 * @param classLoader
	 * @param urls
	 * @param jataUUId
	 * @return
	 */
	private static List<Annotation> readMethodAnnotations(String methodName,
			Class<?>[] parameterTypes, Class<?> annotationClass,
			String annotTemplateFQClassName, String projectName,
			String version, ClassLoader classLoader, URL[] urls, String jataUUId) {
		annotTemplateFQClassName = fixAnnotTemplateFQClassName(annotTemplateFQClassName);
		List<Annotation> annotationList = new ArrayList<Annotation>();
		List<URLClassLoader> jataClassLoaders = new ArrayList<URLClassLoader>();
		for (URL url : urls) {
			URL[] tempUrls = new URL[1];
			tempUrls[0] = url;
			jataClassLoaders.add(new URLClassLoader(tempUrls, classLoader));
		}
		Class<?> jataClass = null;

		for (URLClassLoader jataClassLoader : jataClassLoaders) {
			try {
				jataClass = jataClassLoader.loadClass(annotTemplateFQClassName);

				Method method = jataClass.getMethod(methodName, parameterTypes);
				JataUUId consideredJataUUId = jataClass
						.getAnnotation(JataUUId.class);
				ATTypeLevelId consideredProjectId = jataClass
				.getAnnotation(ATTypeLevelId.class);
				if ((projectName != null) && (projectName.length() > 0)) {
					if (consideredProjectId != null) {
						if (projectName.equals(consideredProjectId.projectName())) {
							getAnnotationsByJataUUId(method, jataUUId,
									annotationList, jataClass,
									consideredJataUUId, annotationClass);
						}
					}
				} else {
					getAnnotationsByJataUUId(method, jataUUId, annotationList,
							jataClass, consideredJataUUId, annotationClass);
				}
			} catch (ClassNotFoundException e) {
				// we don't care if some of the jars don't have the class in
				// question.
			} catch (SecurityException e) {
				throw new JataReadFailureException(e);
			} catch (IllegalArgumentException e) {
				throw new JataReadFailureException(e);
			} catch (NoSuchMethodException e) {
				throw new JataReadFailureException(e);
			}
		}

		return annotationList;
	}

	/**
	 * If there are no annotTemplateFQClassName classes in the jars used, we
	 * return an empty annotationList.
	 *
	 * @param fieldName
	 * @param annotationClass
	 * @param annotTemplateFQClassName
	 * @param projectName
	 * @param version
	 * @param jataUUId
	 * @return
	 */
	public static List<Annotation> readFieldAnnotations(String fieldName,
			Class<?> annotationClass, String annotTemplateFQClassName,
			String projectName, String version, String jataUUId) {
		annotTemplateFQClassName = fixAnnotTemplateFQClassName(annotTemplateFQClassName);
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			classLoader = JataReader.class.getClassLoader();

		}
		URL[] urls = getAppLibUrls();
		return readFieldAnnotations(fieldName, annotationClass,
				annotTemplateFQClassName, projectName, version, classLoader,
				urls, jataUUId);

	}

	/**
	 * If there are no annotTemplateFQClassName classes in the jars used,
	 * we return an empty annotationList.
	 * @param fieldName
	 * @param annotationClass
	 * @param annotTemplateFQClassName
	 * @param projectName
	 * @param version
	 * @param classLoader
	 * @param urls
	 * @param jataUUId
	 * @return
	 */
	public static List<Annotation> readFieldAnnotations(String fieldName,
			Class<?> annotationClass, String annotTemplateFQClassName,
			String projectName, String version, ClassLoader classLoader,
			URL[] urls, String jataUUId) {

		assert fieldName != null;
		annotTemplateFQClassName = fixAnnotTemplateFQClassName(annotTemplateFQClassName);

		List<Annotation> annotationList = new ArrayList<Annotation>();
		List<URLClassLoader> jataClassLoaders = new ArrayList<URLClassLoader>();
		for (URL url : urls) {
			URL[] tempUrls = new URL[1];
			tempUrls[0] = url;
			jataClassLoaders.add(new URLClassLoader(tempUrls, classLoader));
		}
		Class<?> jataClass = null;

		for (URLClassLoader jataClassLoader : jataClassLoaders) {
			try {
				jataClass = jataClassLoader.loadClass(annotTemplateFQClassName);
				Field field = jataClass.getField(fieldName);
				JataUUId consideredJataUUId = jataClass
						.getAnnotation(JataUUId.class);
				ATTypeLevelId consideredProjectId = jataClass
				.getAnnotation(ATTypeLevelId.class);
				if ((projectName != null) && (projectName.length() > 0)) {
					if (consideredProjectId != null) {
						if (projectName.equals(consideredProjectId.projectName())) {
							getAnnotationsByJataUUId(field, jataUUId,
									annotationList, jataClass,
									consideredJataUUId, annotationClass);
						}
					}
				} else {
					getAnnotationsByJataUUId(field, jataUUId, annotationList,
							jataClass, consideredJataUUId, annotationClass);
				}
			} catch (ClassNotFoundException e) {
				// we don't care if some of the jars don't have this class
			} catch (SecurityException e) {
				throw new JataReadFailureException(e);
			} catch (IllegalArgumentException e) {
				throw new JataReadFailureException(e);
			} catch (NoSuchFieldException e) {
				throw new JataReadFailureException(e);
			}
		}

		return annotationList;
	}

	private static String fixAnnotTemplateFQClassName(
			String annotTemplateFQClassName) {
		if (annotTemplateFQClassName
				.startsWith(AnnotationTemplateWriter.ANNOT_PACAKGE_RPEFIX)) {
			return annotTemplateFQClassName;
		} else {
			return AnnotationTemplateWriter.ANNOT_PACAKGE_RPEFIX
					+ annotTemplateFQClassName;
		}
	}

	public static List<Annotation> readAnnotations(Class<?> annotationClass,
			String annotTemplateFQClassName, String projectName,
			String version, String jataUUId) {
		annotTemplateFQClassName = fixAnnotTemplateFQClassName(annotTemplateFQClassName);

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			classLoader = JataReader.class.getClassLoader();

		}
		URL[] urls = getAppLibUrls();

		return readAnnotations(annotationClass, annotTemplateFQClassName,
				projectName, version, classLoader, urls, jataUUId);

	}

	/**
	 * If there are no annotTemplateFQClassName classes in the jars used,
	 * we return an empty annotationList.
	 * @param annotationClass
	 * @param annotTemplateFQClassName
	 * @param projectName
	 * @param version
	 * @param classLoader
	 * @param urls
	 * @param jataUUId
	 * @return
	 */
	public static List<Annotation> readAnnotations(Class<?> annotationClass,
			String annotTemplateFQClassName, String projectName,
			String version, ClassLoader classLoader, URL[] urls, String jataUUId) {
		annotTemplateFQClassName = fixAnnotTemplateFQClassName(annotTemplateFQClassName);

		List<Annotation> annotationList = new ArrayList<Annotation>();
		List<URLClassLoader> jataClassLoaders = new ArrayList<URLClassLoader>();
		for (URL url : urls) {
			URL[] tempUrls = new URL[1];
			tempUrls[0] = url;
			jataClassLoaders.add(new URLClassLoader(tempUrls, classLoader));
		}
		Class<?> jataClass = null;

		for (URLClassLoader jataClassLoader : jataClassLoaders) {
			try {
				jataClass = jataClassLoader.loadClass(annotTemplateFQClassName);
				Annotation[] annotations = jataClass.getAnnotations();
				JataUUId consideredJataUUId = jataClass
						.getAnnotation(JataUUId.class);
				ATTypeLevelId consideredProjectId = jataClass
						.getAnnotation(ATTypeLevelId.class);
				if ((projectName != null) && (projectName.length() > 0)) {
					if (consideredProjectId != null ) {
						if (projectName.equals(consideredProjectId.projectName())) {
							getAnnotationsByJataUUId(jataUUId, annotationList,
									jataClass, consideredJataUUId,
									annotationClass);
						}
					}
				} else {
					getAnnotationsByJataUUId(jataUUId, annotationList,
							jataClass, consideredJataUUId, annotationClass);
				}
			} catch (ClassNotFoundException e) {

				// we don't care if the the class is not present in one of the
				// jars.
			} catch (SecurityException e) {
				throw new JataReadFailureException(e);
			} catch (IllegalArgumentException e) {
				throw new JataReadFailureException(e);
			}
		}

		return annotationList;

	}

	private static void getAnnotationsByJataUUId(Method method,
			String jataUUId, List<Annotation> annotationList,
			Class<?> jataClass, JataUUId consideredJataUUId,
			Class annotationClass) {
		if (jataUUId != null) {
			if (consideredJataUUId != null) {
				if (consideredJataUUId.id().equals(jataUUId)) {
					Annotation[] annotations = null;
					Method fetchedMethod = null;
					try {
						fetchedMethod = jataClass.getMethod(method.getName(),
								method.getParameterTypes());
					} catch (SecurityException e) {
						new JataReadFailureException(e);

					} catch (NoSuchMethodException e) {
						new JataReadFailureException(e);
					}

					if (annotationClass == null) {
						annotations = fetchedMethod.getAnnotations();
						for (Annotation annotation : annotations) {
							annotationList.add(annotation);
						}
					} else {
						Annotation annotation = fetchedMethod
								.getAnnotation(annotationClass);
						annotationList.add(annotation);
					}

				}
			}
		} else {
			Annotation[] annotations = null;
			Method fetchedMethod = null;
			try {
				fetchedMethod = jataClass.getMethod(method.getName(), method
						.getParameterTypes());
			} catch (SecurityException e) {
				new JataReadFailureException(e);

			} catch (NoSuchMethodException e) {
				new JataReadFailureException(e);
			}

			if (annotationClass == null) {
				annotations = fetchedMethod.getAnnotations();
				for (Annotation annotation : annotations) {
					annotationList.add(annotation);
				}
			} else {
				Annotation annotation = fetchedMethod
						.getAnnotation(annotationClass);
				annotationList.add(annotation);
			}
		}
	}

	private static void getAnnotationsByJataUUId(Field field, String jataUUId,
			List<Annotation> annotationList, Class<?> jataClass,
			JataUUId consideredJataUUId, Class annotationClass) {
		if (jataUUId != null) {
			if (consideredJataUUId.id().equals(jataUUId)) {
				Annotation[] annotations = null;
				Method fetchedMethod = null;
				try {
					fetchedMethod = jataClass.getMethod(field.getName());
				} catch (SecurityException e) {
					new JataReadFailureException(e);

				} catch (NoSuchMethodException e) {
					new JataReadFailureException(e);
				}

				if (annotationClass == null) {
					annotations = fetchedMethod.getAnnotations();
					for (Annotation annotation : annotations) {
						annotationList.add(annotation);
					}
				} else {
					Annotation annotation = fetchedMethod
							.getAnnotation(annotationClass);
					annotationList.add(annotation);
				}

			}
		} else {
			Annotation[] annotations = null;
			Method fetchedMethod = null;
			try {
				fetchedMethod = jataClass.getMethod(field.getName());
			} catch (SecurityException e) {
				new JataReadFailureException(e);

			} catch (NoSuchMethodException e) {
				new JataReadFailureException(e);
			}

			if (annotationClass == null) {
				annotations = fetchedMethod.getAnnotations();
				for (Annotation annotation : annotations) {
					annotationList.add(annotation);
				}
			} else {
				Annotation annotation = fetchedMethod
						.getAnnotation(annotationClass);
				annotationList.add(annotation);
			}
		}
	}

	private static void getAnnotationsByJataUUId(String jataUUId,
			List<Annotation> annotationList, Class<?> jataClass,
			JataUUId consideredJataUUId, Class annotationClass) {
		if (jataUUId != null) {
			if (consideredJataUUId != null) {
				if (consideredJataUUId.id().equals(jataUUId)) {
					Annotation[] annotations = null;
					if (annotationClass == null) {
						annotations = jataClass.getAnnotations();
						for (Annotation annotation : annotations) {
							annotationList.add(annotation);
						}
					} else {
						Annotation annotation = jataClass
								.getAnnotation(annotationClass);
						annotationList.add(annotation);
					}

				}
			}
		} else {
			Annotation[] annotations = null;
			if (annotationClass == null) {
				annotations = jataClass.getAnnotations();
				for (Annotation annotation : annotations) {
					annotationList.add(annotation);
				}
			} else {
				Annotation annotation = jataClass
						.getAnnotation(annotationClass);
				annotationList.add(annotation);
			}
		}
	}

	/**
	 * If there are no jataFQClassName classes in the jars used,
	 * we return a null. If several matches happen, currently we return the
	 * first match. The order of lookup is an alphabetical sorting of the urls.
	 * @param jataFQClassName
	 * @param projectName
	 * @param version
	 * @param classLoader
	 * @param urls
	 * @param jataUUId
	 * @return
	 */
	private static Object read(String jataFQClassName, String projectName,
			String version, ClassLoader classLoader, URL[] urls, String jataUUId) {
		List<URLClassLoader> jataClassLoaders = new ArrayList<URLClassLoader>();
		for (URL url : urls) {
			URL[] tempUrls = new URL[1];
			tempUrls[0] = url;
			jataClassLoaders.add(new URLClassLoader(tempUrls, classLoader));
		}
		Class<?> jataClass = null;
		Object object = null;

		for (URLClassLoader jataClassLoader : jataClassLoaders) {
			try {
				jataClass = jataClassLoader.loadClass(jataFQClassName);

				JataUUId consideredJataUUId = jataClass
						.getAnnotation(JataUUId.class);
				if ((jataUUId != null) && (jataUUId.length() > 0)) {
					if (consideredJataUUId != null) {
						if (jataUUId.equals(consideredJataUUId.id())) {
							object = loadObjectFromJata(jataFQClassName,
									jataClassLoader);
						}
					}
				} else {
					object = loadObjectFromJata(jataFQClassName,
							jataClassLoader);
				}
				if (object != null)
					return object;
			} catch (ClassNotFoundException e) {
				// we don't care if the the class is not present in one of the
				// jars.
			} catch (SecurityException e) {
				throw new JataReadFailureException(e);
			} catch (IllegalArgumentException e) {
				throw new JataReadFailureException(e);
			} catch (NoSuchMethodException e) {
				throw new JataReadFailureException(e);
			} catch (IllegalAccessException e) {
				throw new JataReadFailureException(e);
			} catch (InvocationTargetException e) {
				throw new JataReadFailureException(e);
			} catch (AmbiguosMethodException e) {
				throw new JataReadFailureException(e);
			} catch (InstantiationException e) {
				throw new JataReadFailureException(e);
			}
		}
		return object;
	}

	private static Object loadObjectFromJata(String jataFQClassName,
			URLClassLoader jataClassLoader) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, AmbiguosMethodException,
			InstantiationException {
		Class<?> jataClass;
		Object object;
		jataClass = jataClassLoader.loadClass(jataFQClassName);
		ClassLoader preReadContextClassLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(jataClassLoader);
		object = Dynamic.callStatic(jataClass, JataWriter.GET_DATA_METHOD_NAME);
		Thread.currentThread().setContextClassLoader(preReadContextClassLoader);
		return object;
	}

}
