package com.h2.tinyspring.android.plugin;

import com.h2.tinyspring.android.Application;

import android.app.Activity;

/**
 * Implement this interface if you would like to add any specific functionality
 * to you android application.
 * 
 * Don't forget to add you implementation into application context configuration
 * file.
 * 
 * @author 35pr17
 *
 */
public interface IPlugin {

	/**
	 * This method is called every time android application is created.
	 * 
	 * This is a good place to put any logic you need to run only once
	 * in application lifetime.
	 * 
	 * @param application
	 */
	public void onApplicationCreate(Application application);
	
	/**
	 * This method is called everytime an activity is created.
	 * 
	 * This is a good place to put any logic which needs to configure
	 * activity before is actually used. 
	 * 
	 * @param activity
	 */
	public void onActivityCreate(Activity activity);
}
