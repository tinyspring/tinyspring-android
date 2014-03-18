package com.tinyspring.android.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinyspring.springframework.util.ReflectionUtils;

public class AnnotatedFieldResolver {
	Logger logger = LoggerFactory.getLogger(AnnotatedFieldResolver.class);
	Object object;

	public AnnotatedFieldResolver(Object object) {
		this.object = object;
	}

	public <T> List<T> getValues(Class<T> c, Class annotation) {
		List<T> values = new ArrayList<T>();
		try {
			List<Field> fields = findFieldsAnnotatedWith(annotation);
			for (Field field : fields) {
				ReflectionUtils.makeAccessible(field);
				values.add((T) field.get(this.object));
			}
		} catch (Exception e) {
			this.logger.error("Problem when getting value from fields", e);
		}
		return values;
	}

	public List<Field> findFieldsAnnotatedWith(Class c) {
		List<Field> fields = new ArrayList<Field>();

		for (Field field : this.object.getClass().getDeclaredFields()) {
			if (field.getAnnotation(c) != null) {
				fields.add(field);
			}
		}

		return fields;
	}
}