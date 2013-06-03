package com.tinyspring.android.plugin;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

import android.app.Activity;

import com.tinyspring.android.annotation.Inject;

/**
 * This plugin is responsible for injecting all instantiated beans into
 * activity class with fields annotated with @Inject  
 * 
 * @author 35pr17
 *
 */
public class InjectFieldsPlugin extends APlugin implements ApplicationContextAware {

	private FieldFilter fieldInjectFilter = new FieldFilter() {

		@Override
		public boolean matches(Field field) {
			return field.getAnnotation(Inject.class) != null;
		}
	};

	private class FieldInjectProcessor implements FieldCallback {

		Activity activity;

		public FieldInjectProcessor(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
			log.debug("Processing bean injection for field '" + field.getName() + "' ");
			Object bean = applicationContext.getBean(field.getName(), field.getDeclaringClass());
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, activity, bean);
		}
	};

	private static final Logger log = LoggerFactory.getLogger(InjectFieldsPlugin.class);

	ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void onActivityCreate(Activity activity) {
		ReflectionUtils.doWithFields(activity.getClass(), new FieldInjectProcessor(activity), fieldInjectFilter);
	}
}
