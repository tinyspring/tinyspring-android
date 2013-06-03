package com.tinyspring.android.util;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import android.content.res.AssetManager;

import com.tinyspring.beans.factory.ResourceResolver;

public class AndroidResourceResolver extends ResourceResolver {
   
   private static final Logger LOG = LoggerFactory.getLogger(
         AndroidResourceResolver.class);

   private AssetManager _assetManager;
   
   protected String resolveClassPath(String path,
         boolean searchSubDirectories) {
      
      try {
         if(path.startsWith("/") || path.startsWith("\\")) {
            return resolveClassPathHelper(path.substring(1),
                  "", searchSubDirectories);
         }
         else {
            return resolveClassPathHelper(path, "", searchSubDirectories);
         }
      } catch (IOException exp) {
         LOG.error(exp.getMessage());
         LOG.debug("Details: ", exp);
      }
      return path;
   }
   
   protected String resolveClassPathHelper(String path, String topDir,
         boolean searchSubDirectories) throws IOException {
      
      
      String[] subDirs = getAssetManager().list(topDir);
      AntPathMatcher pathMatcher = new AntPathMatcher();
      String result = null;
      for (String file : subDirs) {
         
         String fullname = ("".equals(topDir)) ?
               file : topDir + File.separatorChar + file;
         if(pathMatcher.matchStart(path, fullname)) {
            result = fullname;
            if(getAssetManager().list(file).length != 0) {
               return resolveClassPathHelper(path, fullname,
                     searchSubDirectories);
            }
         }
         else if(searchSubDirectories) {
            result = resolveClassPathHelper(path, fullname,
                  searchSubDirectories);
         }
         
         if(result != null) {
            break;
         }
      }
      return result;
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
