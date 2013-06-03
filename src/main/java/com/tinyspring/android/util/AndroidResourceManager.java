package com.tinyspring.android.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.res.AssetManager;

import com.tinyspring.beans.factory.IResourceManager;

public class AndroidResourceManager implements IResourceManager {
   
   private AssetManager _assetManager;
   
   private static final Logger log = LoggerFactory.getLogger(AndroidResourceManager.class);
   ////
   ////

   @Override
   public InputStream getResource(File file) throws IOException {
      return this.getResource(file.getPath());
   }

   @Override
   public InputStream getResource(String file) throws IOException {
      InputStream is = null;
      try {
    	  is = getAssetManager().open(file);
      } catch (Exception e) {
    	  log.debug("Couldn't open the resource '" + file + "' in default asset path");
      }

      if (is == null) {
    	// try to find the resource in the apk
    	try {
    		is = (InputStream)getClass().getClassLoader().getResource(file).getContent();
    	} catch (Exception e) {
      	  log.debug("Couldn't open the resource '" + file + "' through dalvik class loader");
    	}
      }
      
      if (is == null) {
    	  throw new IOException("The resource '" + file + "' coulnd't be find in application package");
      }
      return is;
   }

   @Override
   public Reader getReader(File file) throws IOException {
      return new InputStreamReader(getResource(file)); 
   }

   /**
    * @return the assetManager
    */
   public AssetManager getAssetManager() {
      return _assetManager;
   }

   /**
    * @param assetManager the assetManager to set
    */
   public void setAssetManager(AssetManager assetManager) {
      _assetManager = assetManager;
   }

}
