package org.jata.write.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class AccessorUtil {

	public static boolean isStatic(Field field) {
		String fieldGenericString = field.toGenericString();

		String[] fieldTokens = fieldGenericString.split(" ");
		for(String fieldToken: fieldTokens) {
			if (fieldToken.trim().equals("static")) {
				return true;
			}
		}

		return false;
	}

	public static boolean isAccessible(Field field) {
		if (field.isAccessible()) {
			return true;
		}
		else {
			String gettorMethodName = getGettorMethodName(field);
			Class<?> declaringClass = field.getDeclaringClass();
			Class[] types = new Class[] {};
			try {
				declaringClass.getMethod(gettorMethodName, types);
			} catch (NoSuchMethodException nsme) {
				return false;
			}
			return true;
		}
	}

	private static String getGettorMethodName(Field field) {
		String fieldName = field.getName();
		String gettorMethodName = null;
		char firstChar = fieldName.charAt(0);
		char replacedFirstChar = Character.toUpperCase(firstChar);
		gettorMethodName = "get" + replacedFirstChar
				+ fieldName.substring(1, fieldName.length());
		return gettorMethodName;
	}

	public static Object getValue(Object object, Field field)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		// TODO: we should try properties and if they don't work
		// we should try field access and if that doesn't work
		// we give up.
		String gettorMethodName = getGettorMethodName(field);
		Class[] types = new Class[] {};
		Method method = null;
		boolean gettorMethodFound = true;
		try {
			method = object.getClass().getMethod(gettorMethodName, types);
		} catch (NoSuchMethodException nsme) {
			gettorMethodFound = false;
		}

		Object result = null;
		if (gettorMethodFound) {
			result = method.invoke(object, new Object[0]);
			return result;
		}
		result = field.get(object);
		return result;
	}


	private static String getSettorMethodName(Field field) {
		String fieldName = field.getName();
		String gettorMethodName = null;
		char firstChar = fieldName.charAt(0);
		char replacedFirstChar = Character.toUpperCase(firstChar);
		gettorMethodName = "set" + replacedFirstChar
				+ fieldName.substring(1, fieldName.length());
		return gettorMethodName;
	}

	public static void setValue(Object object, Field field, Object value)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		String settorMethodName = getSettorMethodName(field);
		//Class[] types = new Class[] {value.getClass()};
		Class[] types = new Class[] {field.getType()};
		boolean settorMethodFound = true;

		Method method = null;
		try {
			//method = object.getClass().getMethod(settorMethodName, types);
			method = getCallableMethod(settorMethodName, object.getClass(), types);
		}
		catch (NoSuchMethodException nsme) {
			// it could be because the value.getClass() may be a boxable type
			// let us try the boxed equivalent
			if (hasBoxedAlternative(value)) {

				Class boxedEquivalentType = getBoxedEquivalentType(value);
				types = new Class[] {boxedEquivalentType};
				try {
					method = object.getClass().getMethod(settorMethodName,
							types);
				} catch (NoSuchMethodException nsme2) {
					settorMethodFound = false;
				}

			}
			else {
				// we should try field assignment

				settorMethodFound = false;
			}
		}
		if (settorMethodFound) {
			Object[] arguments = new Object[] { value };
			method.invoke(object, arguments);
		} else {
			field.set(object, value);
		}

	}

	private static Method getCallableMethod(String methodName, Class clazz, Class[] types) throws NoSuchMethodException {

		Method[] declaredMethods = getDeclaredMethods(methodName, clazz);

		for (Method declaredMethod: declaredMethods) {

			Class[] parameterTypes = declaredMethod.getParameterTypes();
			boolean parametersAssignable = true;
			for (int i=0; i < parameterTypes.length; i++) {
				if (!parameterTypes[i].isAssignableFrom(types[i])) {
					parametersAssignable = false;
				}
			}
			if (parametersAssignable) {
				return declaredMethod;
			}

		}

		throw new NoSuchMethodException(" Couldn't find a callable method");

	}

	private static Method[] getDeclaredMethods(String methodName, Class clazz) {
		Method[] declaredMethods = clazz.getDeclaredMethods();
		ArrayList<Method> declaredMethodsWithName = new ArrayList<Method>();
		for (Method declaredMethod: declaredMethods) {
			if (declaredMethod.getName().equals(methodName)) {
				declaredMethodsWithName.add(declaredMethod);
			}
		}

		Method[] returnMethodArray = new Method[declaredMethodsWithName.size()];
		return declaredMethodsWithName.toArray(returnMethodArray);


	}

	private static Class getBoxedEquivalentType(Object paramater) {

		if (paramater instanceof java.lang.Integer) {
			return int.class;
		} else if (paramater instanceof java.lang.Long) {
			return long.class;
		} else if (paramater instanceof java.lang.Double) {
			return double.class;
		} else if (paramater instanceof java.lang.Float) {
			return float.class;
		} else if (paramater instanceof java.lang.Character) {
			return char.class;
		} else if (paramater instanceof java.lang.Byte) {
			return byte.class;
		} else if (paramater instanceof java.lang.Boolean) {
			return boolean.class;
		} else if (paramater instanceof java.lang.Short) {
			return short.class;
		}
		return null;

	}

	private static boolean hasBoxedAlternative(Object object) {
		return ((object instanceof java.lang.Integer)
				|| (object instanceof java.lang.Long)
				|| (object instanceof java.lang.Double)
				|| (object instanceof java.lang.Float)
				|| (object instanceof java.lang.Character)
				|| (object instanceof java.lang.Byte)
				|| (object instanceof java.lang.Integer)
				|| (object instanceof java.lang.Boolean)
				|| (object instanceof java.lang.Short)

				);
	}

}
