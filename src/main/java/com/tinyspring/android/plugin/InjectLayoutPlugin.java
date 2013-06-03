package com.tinyspring.android.plugin;

import android.app.Activity;

import com.tinyspring.android.annotation.AndroidLayout;

/**
 * This plugin is responsible for injecting android layout declared in
 * standard res configuration to activity class annotated with @InjectLayout 
 * 
 * @author 35pr17
 *
 */
public class InjectLayoutPlugin extends APlugin {

	@Override
	public void onActivityCreate(Activity activity) {
		AndroidLayout layout = activity.getClass().getAnnotation(AndroidLayout.class);
		if (layout != null) {
			activity.setContentView(layout.value());
		}
	}
}
