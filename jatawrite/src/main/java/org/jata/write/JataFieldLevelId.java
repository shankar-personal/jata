package org.jata.write;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface JataFieldLevelId {
	String id();
}
