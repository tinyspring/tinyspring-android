package com.tinyspring.android.plugin;

import android.app.Activity;

public abstract interface IActivityPlugin extends IPlugin {
	public abstract void onActivityCreate(Activity paramActivity);
}