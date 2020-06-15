package org.jata.write.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class DataObjectUtil {

	public static Object getData(String dataObjectClassName,
			String objectClassName) {

		Class dataObjectClass;
		Object object = null;
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			if (classLoader == null) {
				classLoader = DataObjectUtil.class.getClassLoader();
			}

			dataObjectClass = classLoader.loadClass(dataObjectClassName);

			Class objectClass = classLoader.loadClass(objectClassName);

			Object dataObject = dataObjectClass.newInstance();
			object = objectClass.newInstance();

			Field[] dataObjectFields = dataObjectClass.getDeclaredFields();

			// TODO: For immutables, we need to work with the constructors.
			// and the class definition needs to help us out by identifying the
			// constructor arg position of the field in the all field constructor.
			for (Field dataObjectField : dataObjectFields) {
				Object value = AccessorUtil.getValue(dataObject,
						dataObjectField);
				Field objectField = objectClass.getDeclaredField(dataObjectField
						.getName());
				AccessorUtil.setValue(object, objectField, value);
			}
			setSuperClassProperties(object, dataObject, objectClass, dataObjectClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException();
		} catch (InstantiationException e) {
			throw new RuntimeException();
		} catch (IllegalAccessException e) {
			throw new RuntimeException();
		} catch (SecurityException e) {
			throw new RuntimeException();
		} catch (IllegalArgumentException e) {
			throw new RuntimeException();
		} catch (NoSuchMethodException e) {
			throw new RuntimeException();
		} catch (InvocationTargetException e) {
			throw new RuntimeException();
		} catch (NoSuchFieldException e) {
			throw new RuntimeException();
		}
		return object;
	}

	public static void setSuperClassProperties(Object object,
			Object dataObject, Class clazzInHierarchy,
			Class dataObjectClassInClassHierarchy) throws SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			NoSuchFieldException {

		Class objectSuperClassInHierarchy = clazzInHierarchy.getSuperclass();
		Class dataObjectSuperClassInHierarchy = dataObjectClassInClassHierarchy
				.getSuperclass();
		if (!objectSuperClassInHierarchy.getName()
				.equals(java.lang.Object.class.getName())) {
			Field[] dataObjectFields = objectSuperClassInHierarchy.getDeclaredFields();
			for (Field dataObjectField : dataObjectFields) {
				Object value = AccessorUtil.getValue(dataObject,
						dataObjectField);
				Field objectField = dataObjectSuperClassInHierarchy
						.getDeclaredField(dataObjectField.getName());
				AccessorUtil.setValue(object, objectField, value);
			}
			setSuperClassProperties(object, dataObject, objectSuperClassInHierarchy, dataObjectSuperClassInHierarchy);
		}

	}


}
