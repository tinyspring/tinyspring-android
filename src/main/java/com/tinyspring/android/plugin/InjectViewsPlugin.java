package com.tinyspring.android.plugin;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinyspring.android.annotation.AndroidLayout;
import com.tinyspring.android.annotation.AndroidView;
import com.tinyspring.android.util.AnnotatedFieldResolver;
import com.tinyspring.springframework.util.ReflectionUtils;

/**
 * This plugin is responsible for injecting all android views declared in
 * standard res configuration to variables annotated with @InjectView
 * 
 * @author 35pr17
 * 
 */
public class InjectViewsPlugin implements IActivityPlugin, IFragmentPlugin {
	private ReflectionUtils.FieldFilter fieldAndroidViewFilter;
	private static final Logger log = LoggerFactory.getLogger(InjectViewsPlugin.class);

	public InjectViewsPlugin() {
		this.fieldAndroidViewFilter = new ReflectionUtils.FieldFilter() {
			public boolean matches(Field field) {
				return field.getAnnotation(AndroidView.class) != null;
			}
		};
	}

	private View findView(Object object) {
		AnnotatedFieldResolver resolver = new AnnotatedFieldResolver(object);
		return (View) resolver.getValues(View.class, AndroidLayout.class).get(0);
	}

	public void onActivityCreate(Activity activity) {
		ReflectionUtils.doWithFields(activity.getClass(), new FieldAndroidViewProcessor(activity), this.fieldAndroidViewFilter);
	}

	public void onFragmentCreate(Fragment fragment, LayoutInflater inflater, ViewGroup container, Boolean create) {
		ReflectionUtils.doWithFields(fragment.getClass(), new FieldAndroidViewProcessor(fragment), this.fieldAndroidViewFilter);
	}

	private class FieldAndroidViewProcessor implements ReflectionUtils.FieldCallback {
		Object object;

		public FieldAndroidViewProcessor(Object object) {
			this.object = object;
		}

		public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
			InjectViewsPlugin.log.debug("Processing android view injection for field '" + field.getName() + "'");
			ReflectionUtils.makeAccessible(field);
			View view = null;
			if ((this.object instanceof Activity))
				view = ((Activity) this.object).getWindow().getDecorView().getRootView();
			else {
				view = InjectViewsPlugin.this.findView(this.object);
			}
			int id = ((AndroidView) field.getAnnotation(AndroidView.class)).value();
			View injection = view.findViewById(id);
			InjectViewsPlugin.log.debug("Injecting {} for id {}", injection, Integer.valueOf(id));
			ReflectionUtils.setField(field, this.object, injection);
		}
	}
}