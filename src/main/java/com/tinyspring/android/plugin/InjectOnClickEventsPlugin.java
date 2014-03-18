package com.tinyspring.android.plugin;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinyspring.android.annotation.OnClick;
import com.tinyspring.springframework.context.ApplicationContext;
import com.tinyspring.springframework.context.ApplicationContextAware;
import com.tinyspring.springframework.util.ReflectionUtils;

/**
 * This plugin is responsible for injecting click event declared in method with @OnClick
 * annotation to UI components.
 * 
 * @author 35pr17
 * 
 */
public class InjectOnClickEventsPlugin implements ApplicationContextAware, IActivityPlugin, IFragmentPlugin {
	private ReflectionUtils.MethodFilter methodInjectFilter;
	private static final Logger log = LoggerFactory.getLogger(InjectFieldsPlugin.class);
	ApplicationContext applicationContext;

	public InjectOnClickEventsPlugin() {
		this.methodInjectFilter = new ReflectionUtils.MethodFilter() {
			public boolean matches(Method method) {
				return method.getAnnotation(OnClick.class) != null;
			}
		};
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void onActivityCreate(Activity activity) {
		ReflectionUtils.doWithMethods(activity.getClass(), new MethodInjectProcessor(activity), this.methodInjectFilter);
	}

	public void onFragmentCreate(Fragment fragment, LayoutInflater inflater, ViewGroup container, Boolean create) {
		ReflectionUtils.doWithMethods(fragment.getClass(), new MethodInjectProcessor(fragment), this.methodInjectFilter);
	}

	private class MethodInjectProcessor implements ReflectionUtils.MethodCallback {
		Object object;

		public MethodInjectProcessor(Object object) {
			this.object = object;
		}

		public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
			View view = null;
			if ((this.object instanceof Activity))
				view = ((Activity) this.object).getWindow().getDecorView().getRootView();
			else {
				view = ((Fragment) this.object).getView();
			}

			view = view.findViewById(((OnClick) method.getAnnotation(OnClick.class)).value());

			view.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						ReflectionUtils.makeAccessible(method);
						method.invoke(InjectOnClickEventsPlugin.MethodInjectProcessor.this.object, new Object[] { v });
					} catch (Exception e) {
						InjectOnClickEventsPlugin.log.error("Problem when invoking OnClick activity method {} with error {}", InjectOnClickEventsPlugin.MethodInjectProcessor.this.object, e.toString());
					}
				}
			});
		}
	}
}