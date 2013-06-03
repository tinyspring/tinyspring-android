package com.tinyspring.android.plugin;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

import android.app.Activity;
import android.view.View;

import com.tinyspring.android.annotation.OnClick;

/**
 * This plugin is responsible for injecting click event declared in method with @OnClick
 * annotation to UI components.
 * 
 * @author 35pr17
 * 
 */
public class InjectOnClickEventsPlugin extends APlugin implements ApplicationContextAware {

	private MethodFilter methodInjectFilter = new MethodFilter() {

		@Override
		public boolean matches(Method method) {
			return method.getAnnotation(OnClick.class) != null;
		}
	};

	private class MethodInjectProcessor implements MethodCallback {

		Activity activity;

		public MethodInjectProcessor(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
			final String id = activity.getComponentName() + ":" + method.getName();
			log.debug("Processing event injection for method '" + id + "' ");
			View view = this.activity.findViewById(method.getAnnotation(OnClick.class).value());

			view.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						ReflectionUtils.makeAccessible(method);
						method.invoke(activity, v);
					} catch (Exception e) {
						InjectOnClickEventsPlugin.log.error("Problem when invoking OnClick activity method {} with error {}", id, e.toString());
					}
				}
			});
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
		ReflectionUtils.doWithMethods(activity.getClass(), new MethodInjectProcessor(activity), methodInjectFilter);
	}
}
