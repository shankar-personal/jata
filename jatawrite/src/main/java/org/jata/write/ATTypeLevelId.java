package org.jata.write;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ATTypeLevelId {
	String id();

	/**
	 * identifies the projectName to which the annotation template belongs.
	 */
	String projectName() default "";

	/**
	 * the syntax is majorNumber.minorNumer
	 */
	String version() default "";

}
