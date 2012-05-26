package com.h2.tinyspring.android;

import java.util.List;

import com.h2.tinyspring.android.plugin.IPlugin;

public class ApplicationConfiguration {

	private List<IPlugin> plugins;

	public List<IPlugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<IPlugin> plugins) {
		this.plugins = plugins;
	}

	public ApplicationConfiguration() {
		super();
	}
}
