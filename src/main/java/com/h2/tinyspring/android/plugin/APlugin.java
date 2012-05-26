package com.h2.tinyspring.android.plugin;

import android.app.Activity;

import com.h2.tinyspring.android.Application;

/**
 * An empty implementation of the plugin interface so extending plugins can implement
 * only those methods they are actually going to support.
 * 
 * @author 35pr17
 *
 */
public class APlugin implements IPlugin {

	@Override
	public void onApplicationCreate(Application application) {
		// empty implementation as a helper
	}

	@Override
	public void onActivityCreate(Activity activity) {
		// empty implementation as a helper
	}
}
