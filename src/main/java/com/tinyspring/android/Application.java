package com.tinyspring.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tinyspring.android.plugin.IActivityPlugin;
import com.tinyspring.android.plugin.IApplicationPlugin;
import com.tinyspring.android.plugin.IFragmentPlugin;
import com.tinyspring.android.plugin.IPlugin;
import com.tinyspring.android.util.AndroidResourceManager;
import com.tinyspring.beans.Bean;
import com.tinyspring.beans.factory.IResourceManager;
import com.tinyspring.beans.factory.SimpleBeanFactory;
import com.tinyspring.springframework.context.ApplicationContext;
import com.tinyspring.springframework.context.support.ClassPathXmlApplicationContext;

public class Application extends android.app.Application {
	private List<IPlugin> plugins;
	private Map<Class<? extends IPlugin>, List<IPlugin>> filteredPlugins;
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	private ApplicationContext springContext;

	public ApplicationContext getSpringContext() {
		return this.springContext;
	}

	public void onCreate() {
		onApplicationCreate();
	}

	public void onCreate(Activity activity) {
		onActivityCreate(activity);
	}

	public void onCreate(Fragment fragment, LayoutInflater inflater, ViewGroup container, Boolean create) {
		onFragmentCreate(fragment, inflater, container, create);
	}

	protected void processPlugin(Class<? extends IPlugin> c, IPlugin plugin) {
		log.debug("Processing {} with plugin {}", c, plugin);
		((List<IPlugin>) this.filteredPlugins.get(c)).add(plugin);
	}

	protected void processPlugins() {
		this.filteredPlugins = new HashMap<Class<? extends IPlugin>, List<IPlugin>>();
		this.filteredPlugins.put(IApplicationPlugin.class, new ArrayList<IPlugin>());
		this.filteredPlugins.put(IActivityPlugin.class, new ArrayList<IPlugin>());
		this.filteredPlugins.put(IFragmentPlugin.class, new ArrayList<IPlugin>());

		for (IPlugin plugin : this.plugins) {
			if ((plugin instanceof IApplicationPlugin)) {
				processPlugin(IApplicationPlugin.class, plugin);
			}
			if ((plugin instanceof IActivityPlugin)) {
				processPlugin(IActivityPlugin.class, plugin);
			}
			if ((plugin instanceof IFragmentPlugin))
				processPlugin(IFragmentPlugin.class, plugin);
		}
	}

	protected void onApplicationCreate() {
		log.debug("Creating Application");
		super.onCreate();

		final AndroidResourceManager resManager = new AndroidResourceManager();
		resManager.setAssetManager(getAssets());

		log.debug("Initializing Spring application context from tinyspring-android.xml");
		this.springContext = new ClassPathXmlApplicationContext(new String[] { "taac.xml", "tinyspring-android.xml" }) {
			public IResourceManager getResorceManager() {
				return resManager;
			}

			protected void postProcess(SimpleBeanFactory factory) {
				Bean bean = new Bean("context", Context.class.getName());
				bean.setInstantiatedObject(Application.this.getApplicationContext());
				((SimpleBeanFactory) getBeanFactory()).addBean(bean);

				super.postProcess(factory);
			}
		};
		List<IPlugin> defaultPlugins = (List<IPlugin>) this.springContext.getBean("defaultPlugins", List.class);

		this.plugins = new ArrayList<IPlugin>();
		this.plugins.addAll(defaultPlugins);

		ApplicationConfiguration configuration = null;
		try {
			Map<String, ApplicationConfiguration> configurations = this.springContext.getBeansOfType(ApplicationConfiguration.class);
			if ((configurations != null) && (configurations.size() == 1))
				configuration = (ApplicationConfiguration) configurations.values().iterator().next();
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

		processPlugins();

		log.debug("On Application Create detected - calling each registered plugin");
		for (IPlugin plugin : this.filteredPlugins.get(IApplicationPlugin.class)) {

			((IApplicationPlugin) plugin).onApplicationCreate(this);
		}
	}

	protected void onActivityCreate(Activity activity) {
		log.debug("On Activity Create detected - calling each registered plugin");
		for (IPlugin plugin : this.filteredPlugins.get(IActivityPlugin.class)) {
			((IActivityPlugin) plugin).onActivityCreate(activity);
		}

	}

	protected void onFragmentCreate(Fragment fragment, LayoutInflater inflater, ViewGroup container, Boolean create) {
		log.debug("On Fragment Create detected");
		for (IPlugin plugin : this.filteredPlugins.get(IFragmentPlugin.class)) {
			((IFragmentPlugin) plugin).onFragmentCreate(fragment, inflater, container, create);
		}
	}
}