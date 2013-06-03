package com.tinyspring.android;

import java.util.List;

import com.tinyspring.android.plugin.IPlugin;

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
