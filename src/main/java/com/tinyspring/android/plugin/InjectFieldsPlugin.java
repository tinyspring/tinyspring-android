package com.tinyspring.android.plugin;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tinyspring.android.annotation.Inject;
import com.tinyspring.springframework.context.ApplicationContext;
import com.tinyspring.springframework.context.ApplicationContextAware;
import com.tinyspring.springframework.util.ReflectionUtils;

/**
 * This plugin is responsible for injecting all instantiated beans into activity
 * class with fields annotated with @Inject
 * 
 * @author 35pr17
 * 
 */
public class InjectFieldsPlugin implements IFragmentPlugin, IActivityPlugin, ApplicationContextAware {
	private ReflectionUtils.FieldFilter fieldInjectFilter;
	private static final Logger log = LoggerFactory.getLogger(InjectFieldsPlugin.class);
	ApplicationContext applicationContext;

	public InjectFieldsPlugin() {
		this.fieldInjectFilter = new ReflectionUtils.FieldFilter() {
			public boolean matches(Field field) {
				return field.getAnnotation(Inject.class) != null;
			}
		};
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void onActivityCreate(Activity activity) {
		ReflectionUtils.doWithFields(activity.getClass(), new FieldInjectProcessor(activity), this.fieldInjectFilter);
	}

	public void onFragmentCreate(Fragment fragment, LayoutInflater inflater, ViewGroup container, Boolean create) {
		ReflectionUtils.doWithFields(fragment.getClass(), new FieldInjectProcessor(fragment), this.fieldInjectFilter);
	}

	private class FieldInjectProcessor implements ReflectionUtils.FieldCallback {
		Object object;

		public FieldInjectProcessor(Object object) {
			this.object = object;
		}

		public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
			InjectFieldsPlugin.log.debug("Processing bean injection for field '" + field.getName() + "' ");
			Object bean = InjectFieldsPlugin.this.applicationContext.getBean(field.getName(), field.getDeclaringClass());
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, this.object, bean);
		}
	}
}