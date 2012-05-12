package com.h2.tinyspring.android.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ ElementType.TYPE })
public @interface AndroidLayout {
	public int value() default -1;
}