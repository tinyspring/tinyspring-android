package com.h2.util.resource.android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.content.res.AssetManager;

import com.h2.org.springframework.beans.factory.IResourceManager;

public class AndroidResourceManager implements IResourceManager {
   
   private AssetManager _assetManager;
   
   ////
   ////

   @Override
   public InputStream getResource(File file) throws IOException {
      String path = file.getPath();
      return getAssetManager().open(path);
   }

   @Override
   public InputStream getResource(String file) throws IOException {
      return getAssetManager().open(file);
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
