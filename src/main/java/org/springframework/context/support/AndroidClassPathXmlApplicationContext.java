package org.springframework.context.support;

import android.content.res.AssetManager;

import com.tinyspring.android.util.AndroidResourceManager;
import com.tinyspring.springframework.context.support.ClassPathXmlApplicationContext;

public class AndroidClassPathXmlApplicationContext extends ClassPathXmlApplicationContext {

	public AndroidClassPathXmlApplicationContext(String config, AssetManager assetManager) {
		// resourceManager.setAssetManager(activity.getAssets());
		AndroidResourceManager resourceManager = new AndroidResourceManager();
		resourceManager.setAssetManager(assetManager);

		this._resorceManager = resourceManager;
	}
}
