package com.tinyspring.android.plugin;

import com.tinyspring.android.Application;

public abstract interface IApplicationPlugin extends IPlugin {
	public abstract void onApplicationCreate(Application paramApplication);
}