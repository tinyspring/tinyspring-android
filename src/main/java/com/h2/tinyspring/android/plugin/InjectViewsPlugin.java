package com.h2.tinyspring.android.plugin;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

import android.app.Activity;

import com.h2.tinyspring.android.annotation.AndroidView;

/**
 * This plugin is responsible for injecting all android views declared in
 * standard res configuration to variables annotated with @InjectView 
 * 
 * @author 35pr17
 *
 */
public class InjectViewsPlugin extends APlugin {

	private FieldFilter fieldAndroidViewFilter = new FieldFilter() {

		@Override
		public boolean matches(Field field) {
			return field.getAnnotation(AndroidView.class) != null;
		}
	};

	private class FieldAndroidViewProcessor implements FieldCallback {

		Activity activity;

		public FieldAndroidViewProcessor(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
			log.debug("Processing android view injection for field '" + field.getName() + "'");
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, activity, this.activity.findViewById(field.getAnnotation(AndroidView.class).value()));
		}
	};

	private static final Logger log = LoggerFactory.getLogger(InjectViewsPlugin.class);

	@Override
	public void onActivityCreate(Activity activity) {
		ReflectionUtils.doWithFields(activity.getClass(), new FieldAndroidViewProcessor(activity), fieldAndroidViewFilter);
	}
}
