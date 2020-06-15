package org.jata.dynamic;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author shankar Copyright 2007 Shankar Narayan Licensed under the Apache
 *         License, Version 2.0 (the "License"); you may not use this file
 *         except in compliance with the License. You may obtain a copy of the
 *         License at http://www.apache.org/licenses/LICENSE-2.0 Unless required
 *         by applicable law or agreed to in writing, software distributed under
 *         the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 *         CONDITIONS OF ANY KIND, either express or implied. See the License
 *         for the specific language governing permissions and limitations under
 *         the License.
 *
 */
public class Dynamic {

	private static final String ANY = "any";

	/**
	 * Currently this method is not implemented fully.
	 * @param obj
	 * @param methodName
	 * @param methodProvider
	 * @param arguments
	 * @return
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AmbiguosMethodException
	 * @throws InstantiationException
	 */
	public static Object call(Object obj, String methodName,
			String methodProvider, Object... arguments)
			throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, AmbiguosMethodException,
			InstantiationException {
		if ((methodProvider == null) || methodProvider.equalsIgnoreCase(ANY)) {
			return call(obj, methodName, arguments);
		}
		throw new UnsupportedOperationException(
				"We don't yet support this option");
	}

	/**
	 * Call this method to invoke methods on an object reflectively.
	 *
	 * @param obj the object on which you are making the invocation
	 * @param methodName the name of the method.
	 * @param arguments the arguments to the method in the same order you would provide
	 * @return the returned object.
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AmbiguosMethodException
	 * @throws InstantiationException
	 */
	public static Object call(Object obj, String methodName,
			Object... arguments) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			AmbiguosMethodException, InstantiationException {
		Class clazz = obj.getClass();
		return callMethod(clazz, obj,  methodName,
				arguments);


	}

	/**
	 * a utility method to test for boxed equality.
	 * @param primitiveParameterType
	 * @param nonPrimitiveParameterType
	 * @return true or false depending on if the primitiveParameterType is boxed
	 *         equal to the non primitive
	 */
	private static boolean areParametersBoxedEqual(Class firstParameterType,
			Class secondParameterType) {
		assert ((firstParameterType.isPrimitive() && (!secondParameterType.isPrimitive())) ||   (secondParameterType.isPrimitive() && (!firstParameterType.isPrimitive()))   );

		Class primitiveParameterType = null;
		Class nonPrimitiveParameterType = null;
		if (firstParameterType.isPrimitive()) {
			primitiveParameterType = firstParameterType;
			nonPrimitiveParameterType = secondParameterType;
		}
		else {
			primitiveParameterType = secondParameterType ;
			nonPrimitiveParameterType = firstParameterType;
		}


		if (primitiveParameterType.getName().equals(long.class.getName())) {
			return nonPrimitiveParameterType.getName().equals(
					java.lang.Long.class.getName());
		} else if (primitiveParameterType.getName().equals(int.class.getName())) {
			return nonPrimitiveParameterType.getName().equals(
					java.lang.Integer.class.getName());
		} else if (primitiveParameterType.getName().equals(
				boolean.class.getName())) {
			return nonPrimitiveParameterType.getName().equals(
					java.lang.Boolean.class.getName());
		} else if (primitiveParameterType.getName()
				.equals(byte.class.getName())) {
			return nonPrimitiveParameterType.getName().equals(
					java.lang.Byte.class.getName());
		} else if (primitiveParameterType.getName()
				.equals(char.class.getName())) {
			return nonPrimitiveParameterType.getName().equals(
					java.lang.Character.class.getName());
		} else if (primitiveParameterType.getName().equals(
				short.class.getName())) {
			return nonPrimitiveParameterType.getName().equals(
					java.lang.Short.class.getName());
		} else if (primitiveParameterType.getName().equals(
				float.class.getName())) {
			return nonPrimitiveParameterType.getName().equals(
					java.lang.Float.class.getName());
		} else if (primitiveParameterType.getName().equals(
				double.class.getName())) {
			return nonPrimitiveParameterType.getName().equals(
					java.lang.Double.class.getName());
		}
		return false;
	}

	/**
	 * Currently this method is not implemented fully.
	 * @param obj
	 * @param methodName
	 * @param methodProvider
	 * @param arguments
	 * @return
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AmbiguosMethodException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public static Object callStatic(String className, String methodName,
			String methodProvider, Object... arguments)
			throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, AmbiguosMethodException,
			InstantiationException, ClassNotFoundException {
		if ((methodProvider == null) || methodProvider.equalsIgnoreCase(ANY)) {
			return callStatic(className, methodName, arguments);
		}
		throw new UnsupportedOperationException(
				"We don't yet support this option");
	}


	/**
	 * Call this method to invoke methods on an object reflectively.
	 *
	 * @param obj the object on which you are making the invocation
	 * @param methodName the name of the method.
	 * @param arguments the arguments to the method in the same order you would provide
	 * @return the returned object.
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AmbiguosMethodException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public static Object callStatic(String className, String methodName,
			Object... arguments) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			AmbiguosMethodException, InstantiationException, ClassNotFoundException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = Dynamic.class.getClassLoader();
		}
		Class clazz = cl.loadClass(className);
		return callMethod(clazz, null,  methodName,
			arguments);

	}

	public static Object callStatic(Class clazz, String methodName,
			Object... arguments) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			AmbiguosMethodException, InstantiationException, ClassNotFoundException {
		return callMethod(clazz, null,  methodName,
			arguments);
	}

	/**
	 * Call this method to invoke methods on an object reflectively.
	 *
	 * @param obj the object on which you are making the invocation
	 * @param methodName the name of the method.
	 * @param arguments the arguments to the method in the same order you would provide
	 * @return the returned object.
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AmbiguosMethodException
	 * @throws InstantiationException
	 */
	private static Object callMethod(Class clazz, Object obj, String methodName,
			Object... arguments) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			AmbiguosMethodException, InstantiationException{
		boolean isVarargsMatch = false;
		boolean methodMatchFound = false;
		int numberOfMethodMatches = 0;
		Class varargsComponentType = null;
		int varargsBeginIndex = -1;
		Class[] parameterTypes = new Class[arguments.length/2];
		Object[] methodArguments = new Object[arguments.length/2];
		for (int i = 0; i < arguments.length/2; i++) {
			parameterTypes[i] = (Class)arguments[i*2];
		}

		for (int i = 0; i < arguments.length/2; i++) {
			methodArguments[i] = arguments[i*2+1];
		}


		Method method = null;
		try {
			method = clazz.getDeclaredMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException nsme) {
			// We are handling the situation of auto boxing and inheritance.
			// Ideally java should have discerned that the parameterTypes
			// supplied in the getDeclaredMethod(...) and
			// the parameterTypes of methods on the class
			// could use autoboxed types interchangeably and
			// returned the appropriate method.
			Method[] methods = clazz.getDeclaredMethods();

			for (Method methodInArray : methods) {

				if (methodInArray.getName().equals(methodName)) {
					if (methodInArray.isVarArgs()) {
						Class[] methodInArrayParameterTypes = methodInArray
								.getParameterTypes();
						boolean parameterTypeMatch = true;
						for (int i = 0; i < methodInArrayParameterTypes.length; i++) {
							if (i != methodInArrayParameterTypes.length - 1) {
								Class methodInArrayParameterType = methodInArrayParameterTypes[i];
								// if isAssignable worked for boxed types
								// we would not have this problem.

								if (methodInArrayParameterType.isPrimitive() || parameterTypes[i].isPrimitive()) {

									// parameterType.
									if (!areParametersBoxedEqual(methodInArrayParameterType,
											parameterTypes[i])) {
										parameterTypeMatch = false;
										break;
									}
								} else {
									if (!(methodInArrayParameterType
											.isAssignableFrom(parameterTypes[i]))) {
										parameterTypeMatch = false;
										break;
									}
								}
							} else {
								// we have to deal with varargs

								Class methodInArrayParameterType = methodInArrayParameterTypes[i];

								varargsComponentType = methodInArrayParameterTypes[i]
										.getComponentType();

								for (int j = i; j < parameterTypes.length; j++) {
									if ((!varargsComponentType
											.isAssignableFrom(parameterTypes[i])) && (!areParametersBoxedEqual(varargsComponentType, parameterTypes[i]))) {
										parameterTypeMatch = false;
										break;
									}
									// todo this is actually only a
								}

								if (!parameterTypeMatch) {
									break;
								} else {
									// todo this is actually only a
									// potential varargs match.
									isVarargsMatch = true;
									numberOfMethodMatches++;
									if (numberOfMethodMatches > 1) {
										throw new AmbiguosMethodException(
												"Two methods map to the same varags method signature");
									}
									varargsBeginIndex = i;
									methodMatchFound = true;
									method = methodInArray;
								}
							}
						}
					} else {
						// not varargs part

						Class[] methodInArrayParameterTypes = methodInArray
								.getParameterTypes();
						boolean parameterTypeMatch = true;
						if (methodInArrayParameterTypes.length == parameterTypes.length) {
							for (int i = 0; i < methodInArrayParameterTypes.length; i++) {

								Class methodInArrayParameterType = methodInArrayParameterTypes[i];
								// if isAssignable worked for boxed types
								// we would not have this problem.

								if (methodInArrayParameterType.isPrimitive() || parameterTypes[i].isPrimitive()) {

									// parameterType.
									if (!areParametersBoxedEqual(methodInArrayParameterType,
											parameterTypes[i])) {
										parameterTypeMatch = false;
										break;
									}
								} else {
									if (!(methodInArrayParameterType
											.isAssignableFrom(parameterTypes[i]))) {
										parameterTypeMatch = false;
										break;
									}
								}

							}
						} else {
							parameterTypeMatch = false;
						}
						if (parameterTypeMatch) {
							methodMatchFound = true;
							numberOfMethodMatches++;
							if (numberOfMethodMatches > 1) {
								throw new AmbiguosMethodException(
										"Two methods map to the same varags method signature");
							}
							method = methodInArray;
						}
					}
				}
			}
			if (!methodMatchFound) {
				throw nsme;
			}

		}
		if (!isVarargsMatch) {
			return method.invoke(obj, methodArguments);
		} else {
			Object[] revisedArguments = new Object[varargsBeginIndex + 1];
			Object varargsArray = Array.newInstance(varargsComponentType,
					methodArguments.length - varargsBeginIndex);
			revisedArguments[varargsBeginIndex] = varargsArray;
			for (int i = 0; i < methodArguments.length; i++) {
				if (i < varargsBeginIndex) {
					revisedArguments[i] = methodArguments[i];
				} else {
					Array
							.set(varargsArray, i - varargsBeginIndex,
									methodArguments[i]);

				}
			}
			return method.invoke(obj, revisedArguments);
		}
	}

	/**
	 *
	 * Currently this method is not fully implemented.
	 * @param obj
	 * @param methodProvider
	 * @param arguments
	 * @return
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws AmbiguosMethodException
	 * @throws ClassNotFoundException
	 */
	public static Object makeNew(String className, String methodProvider,
			Object... arguments) throws SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException, AmbiguosMethodException,
			ClassNotFoundException {
		if ((methodProvider == null) || methodProvider.equalsIgnoreCase(ANY)) {
			return makeNew(className, arguments);
		}
		throw new UnsupportedOperationException(
				"We don't yet support this option");
	}

	/**
	 * @param className the name of the class that you want to create.
	 * @param arguments the constructor argument.
	 * @return the returned new object.
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws AmbiguosMethodException
	 * @throws ClassNotFoundException
	 */
	public static Object makeNew(String className, Object... arguments)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException,
			AmbiguosMethodException, ClassNotFoundException {

		boolean isVarargsMatch = false;
		boolean constructorMatchFound = false;
		int numberOfConstructorMatches = 0;
		Class varargsComponentType = null;
		int varargsBeginIndex = -1;

		Class[] parameterTypes = new Class[arguments.length/2];
		Object[] methodArguments = new Object[arguments.length/2];
		for (int i = 0; i < arguments.length/2; i++) {
			parameterTypes[i] = (Class)arguments[i*2];
		}

		for (int i = 0; i < arguments.length/2; i++) {
			methodArguments[i] = arguments[i*2+1];
		}
		Constructor constructor = null;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Class<?> clazz = cl.loadClass(className);
		try {
			constructor = clazz.getConstructor(parameterTypes);
		} catch (NoSuchMethodException nsme) {
			// We are handling the situation of auto boxing and inheritance.
			// Ideally java should have discerned that the parameterTypes
			// supplied in the getDeclaredMethod(...) and
			// the parameterTypes of constructors on the class
			// could use autoboxed types interchangeably and
			// returned the appropriate constructor.
			Constructor[] constructors = clazz.getConstructors();

			for (Constructor constructorInArray : constructors) {

				if (constructorInArray.isVarArgs()) {
					Class[] constructorInArrayParameterTypes = constructorInArray
							.getParameterTypes();
					boolean parameterTypeMatch = true;
					for (int i = 0; i < constructorInArrayParameterTypes.length; i++) {
						if (i != constructorInArrayParameterTypes.length - 1) {
							Class constructorInArrayParameterType = constructorInArrayParameterTypes[i];
							// if isAssignable worked for boxed types
							// we would not have this problem.

							if (constructorInArrayParameterType.isPrimitive() || parameterTypes[i].isPrimitive()) {

								// parameterType.
								if (!areParametersBoxedEqual(constructorInArrayParameterType,
										parameterTypes[i])) {
									parameterTypeMatch = false;
									break;
								}
							} else {
								if (!(constructorInArrayParameterType
										.isAssignableFrom(parameterTypes[i]))) {
									parameterTypeMatch = false;
									break;
								}
							}
						} else {
							// we have to deal with varargs

							Class constructorInArrayParameterType = constructorInArrayParameterTypes[i];

							varargsComponentType = constructorInArrayParameterTypes[i]
									.getComponentType();

							for (int j = i; j < parameterTypes.length; j++) {
								if ((!varargsComponentType
										.isAssignableFrom(parameterTypes[i]))
										&& (!areParametersBoxedEqual(
												varargsComponentType,
												parameterTypes[i]))) {
									parameterTypeMatch = false;
									break;
								}
								// todo this is actually only a
							}

							if (!parameterTypeMatch) {
								break;
							} else {
								// todo this is actually only a
								// potential varargs match.
								isVarargsMatch = true;
								numberOfConstructorMatches++;
								if (numberOfConstructorMatches > 1) {
									throw new AmbiguosMethodException(
											"Two constructors map to the same varags method signature");
								}
								varargsBeginIndex = i;
								constructorMatchFound = true;
								constructor = constructorInArray;
							}
						}
					}
				} else {
					// not varargs part

					Class[] constructorInArrayParameterTypes = constructorInArray
							.getParameterTypes();
					boolean parameterTypeMatch = true;
					if (constructorInArrayParameterTypes.length == parameterTypes.length) {
						for (int i = 0; i < constructorInArrayParameterTypes.length; i++) {

							Class constructorInArrayParameterType = constructorInArrayParameterTypes[i];
							// if isAssignable worked for boxed types
							// we would not have this problem.

							if (constructorInArrayParameterType.isPrimitive() || parameterTypes[i].isPrimitive()) {

								// parameterType.
								if (!areParametersBoxedEqual(constructorInArrayParameterType,
										parameterTypes[i])) {
									parameterTypeMatch = false;
									break;
								}
							} else {
								if (!(constructorInArrayParameterType
										.isAssignableFrom(parameterTypes[i]))) {
									parameterTypeMatch = false;
									break;
								}
							}

						}
					} else {
						parameterTypeMatch = false;
					}
					if (parameterTypeMatch) {
						constructorMatchFound = true;
						numberOfConstructorMatches++;
						if (numberOfConstructorMatches > 1) {
							throw new AmbiguosMethodException(
									"Two constructors map to the same varags method signature");
						}
						constructor = constructorInArray;
					}
				}
			}
			if (!constructorMatchFound) {
				throw nsme;
			}

		}
		if (!isVarargsMatch) {
			return constructor.newInstance(methodArguments);
		} else {
			Object[] revisedArguments = new Object[varargsBeginIndex + 1];
			Object varargsArray = Array.newInstance(varargsComponentType,
					methodArguments.length - varargsBeginIndex);
			revisedArguments[varargsBeginIndex] = varargsArray;
			for (int i = 0; i < methodArguments.length; i++) {
				if (i < varargsBeginIndex) {
					revisedArguments[i] = methodArguments[i];
				} else {
					Array
							.set(varargsArray, i - varargsBeginIndex,
									methodArguments[i]);

				}
			}
			return constructor.newInstance(revisedArguments);
		}
	}

}
