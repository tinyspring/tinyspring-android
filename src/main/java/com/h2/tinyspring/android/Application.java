package com.h2.tinyspring.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import android.app.Activity;
import android.content.Context;

import com.h2.org.springframework.beans.Bean;
import com.h2.org.springframework.beans.factory.IResourceManager;
import com.h2.org.springframework.beans.factory.SimpleBeanFactory;
import com.h2.tinyspring.android.plugin.IPlugin;
import com.h2.util.resource.android.AndroidResourceManager;

public class Application extends android.app.Application {

	private List<IPlugin> plugins;
	
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
		log.debug("Creating Application");
		super.onCreate();

		final AndroidResourceManager resManager = new AndroidResourceManager();
		resManager.setAssetManager(getAssets());

		log.debug("Initializing Spring application context from tiny-android.xml");
		this.springContext = new ClassPathXmlApplicationContext("tinyspring-android.xml", "tiny-android.xml") {
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
		
		List<IPlugin> defaultPlugins = this.springContext.getBean("defaultPlugins", List.class);
		
		this.plugins = new ArrayList<IPlugin>();
		this.plugins.addAll(defaultPlugins);
		
		ApplicationConfiguration configuration = null;
		try {
			 Map<String,ApplicationConfiguration> configurations = this.springContext.getBeansOfType(ApplicationConfiguration.class);
			 if (configurations != null && configurations.size() == 1) {
				 configuration = configurations.values().iterator().next();
			 }
		} catch (Exception e) {
			log.debug("No Application Configuration bean found in the context");
		}
		
		if (configuration != null) {
			log.debug("Processing configuration");
			if (configuration.getPlugins() != null) {
				for (IPlugin plugin : configuration.getPlugins()) {
					this.plugins.add(plugin);
				}
			}
		}
		
		log.debug("On Application Create detected - calling each registered plugin");
		for (IPlugin plugin : this.plugins) {
			plugin.onApplicationCreate(this);
		}
	}

	public void onActivityCreate(Activity activity) {
		log.debug("On Activity Create detected - calling each registered plugin");
		for (IPlugin plugin : this.plugins) {
			plugin.onActivityCreate(activity);
		}
	}
}
