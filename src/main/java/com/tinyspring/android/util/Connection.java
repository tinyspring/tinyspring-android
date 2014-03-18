package com.tinyspring.android.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connection {
	public static boolean isNetworkAvailable(Context context) {
		Logger logger = LoggerFactory.getLogger(Connection.class);

		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");

		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();

			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					logger.debug("Network info: {}", info[i].getState().toString());
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}