package com.h2.tinyspring.android;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

import android.app.Activity;
import android.content.Context;

import com.h2.org.springframework.beans.Bean;
import com.h2.org.springframework.beans.factory.IResourceManager;
import com.h2.org.springframework.beans.factory.SimpleBeanFactory;
import com.h2.tinyspring.android.annotation.AndroidLayout;
import com.h2.tinyspring.android.annotation.AndroidView;
import com.h2.tinyspring.android.annotation.Inject;
import com.h2.util.resource.android.AndroidResourceManager;

public class Application extends android.app.Application {

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

	private class FieldInjectProcessor implements FieldCallback {

		Activity activity;

		public FieldInjectProcessor(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
			log.debug("Processing bean injection for field '" + field.getName() + "' ");
			Object bean = springContext.getBean(field.getName(), field.getDeclaringClass());
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, activity, bean);
		}
	};

	private FieldFilter fieldAndroidViewFilter = new FieldFilter() {

		@Override
		public boolean matches(Field field) {
			return field.getAnnotation(AndroidView.class) != null;
		}
	};

	private FieldFilter fieldInjectFilter = new FieldFilter() {

		@Override
		public boolean matches(Field field) {
			return field.getAnnotation(Inject.class) != null;
		}
	};

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	private ApplicationContext springContext;

	public Application() {
		super();
	}

	public ApplicationContext getSpringContext() {
		return this.springContext;
	}
	
	/**
	 * Loads application context and saves it into private variable.
	 * 
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		final AndroidResourceManager resManager = new AndroidResourceManager();
		resManager.setAssetManager(getAssets());

		this.springContext = new ClassPathXmlApplicationContext("tiny-android.xml") {
			@Override
			public IResourceManager getResorceManager() {
				return resManager;
			}
			
			protected void postProcess(SimpleBeanFactory factory) {
				// add android context bean into bean factory
				Bean bean = new Bean("context", Context.class.getName());
				bean.setInstantiatedObject(getApplicationContext());
				((SimpleBeanFactory)this.getBeanFactory()).addBean(bean);
				
				super.postProcess(factory);
			}
		};
	}

	public void inject(Object object) {
		if (!(object instanceof Activity)) {
			return;
		}

		// first process layout injection
		Activity activity = (Activity) object;
		AndroidLayout layout = activity.getClass().getAnnotation(AndroidLayout.class);
		if (layout != null) {
			activity.setContentView(layout.value());
		}

		// process android views injections
		ReflectionUtils.doWithFields(activity.getClass(), new FieldAndroidViewProcessor(activity), fieldAndroidViewFilter);

		// process bean injections
		ReflectionUtils.doWithFields(activity.getClass(), new FieldInjectProcessor(activity), fieldInjectFilter);
	}
}
