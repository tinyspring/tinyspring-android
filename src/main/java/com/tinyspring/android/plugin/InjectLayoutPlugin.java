package com.tinyspring.android.plugin;

import java.lang.reflect.Field;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tinyspring.android.annotation.AndroidLayout;
import com.tinyspring.springframework.util.ReflectionUtils;

/**
 * This plugin is responsible for injecting android layout declared in standard
 * res configuration to activity class annotated with @InjectLayout
 * 
 * @author 35pr17
 * 
 */
public class InjectLayoutPlugin implements IActivityPlugin, IFragmentPlugin {
	private ReflectionUtils.FieldFilter fieldInjectFilter;

	public InjectLayoutPlugin() {
		this.fieldInjectFilter = new ReflectionUtils.FieldFilter() {
			public boolean matches(Field field) {
				return field.getAnnotation(AndroidLayout.class) != null;
			}
		};
	}

	public void onActivityCreate(Activity activity) {
		AndroidLayout layout = (AndroidLayout) activity.getClass().getAnnotation(AndroidLayout.class);
		if (layout != null)
			activity.setContentView(layout.value());
	}

	public void onFragmentCreate(Fragment fragment, LayoutInflater inflater, ViewGroup container, Boolean create) {
		ReflectionUtils.doWithFields(fragment.getClass(), new FieldInjectProcessor(fragment, inflater, container, create), this.fieldInjectFilter);
	}

	private class FieldInjectProcessor implements ReflectionUtils.FieldCallback {
		Object object;
		LayoutInflater inflater;
		ViewGroup container;
		Boolean create;

		public FieldInjectProcessor(Object object, LayoutInflater inflater, ViewGroup container, Boolean create) {
			this.object = object;
			this.inflater = inflater;
			this.container = container;
			this.create = create;
		}

		public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, this.object, this.inflater.inflate(((AndroidLayout) field.getAnnotation(AndroidLayout.class)).value(), this.container, this.create.booleanValue()));
		}
	}
}
