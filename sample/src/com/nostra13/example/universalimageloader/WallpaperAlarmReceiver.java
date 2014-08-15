package com.nostra13.example.universalimageloader;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wzw.ic.mvc.ViewItem;

public class WallpaperAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		
		List<ViewItem> pageViewItems = IcDatabase.getInstance().fetchAllViewItemsInHearts(0, 100);
		
		if (null == pageViewItems || pageViewItems.size() == 0) {
			return;
		}
		
		String randomImage = pageViewItems.get((new Random()).nextInt(pageViewItems.size())).getImageUrl();
		ImageLoader.getInstance().loadImage(randomImage, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (null == loadedImage) {
					return;
				}
								
				int imageWidth = loadedImage.getWidth();
				int imageHeight = loadedImage.getHeight();
				
				DisplayMetrics metrics = new DisplayMetrics();
				((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
		        int screenWidth = metrics.widthPixels;
		        int screenHeight = metrics.heightPixels;		        
		        
		        if (screenWidth*imageHeight > imageWidth*screenHeight) {
		        	loadedImage = Bitmap.createScaledBitmap(loadedImage, screenHeight*imageWidth/imageHeight, screenHeight, false);
		        } else {
		        	loadedImage = Bitmap.createScaledBitmap(loadedImage, screenWidth, screenWidth*imageHeight/imageWidth, false);			        	
		        }
		        
		        WallpaperManager wallpaperMgr = WallpaperManager.getInstance(context);
				Bitmap wallpaper = null;
				
				// For compatibility
				wallpaperMgr.suggestDesiredDimensions(screenWidth, screenHeight);
				
				int desiredWidth = wallpaperMgr.getDesiredMinimumWidth();
				int desiredHeight = wallpaperMgr.getDesiredMinimumHeight();
				if (desiredWidth > 0 && desiredHeight > 0) {
					wallpaper = Bitmap.createBitmap(desiredWidth, desiredHeight, Config.ARGB_8888);
					Canvas canvas = new Canvas(wallpaper);
					canvas.drawBitmap(loadedImage, (desiredWidth - loadedImage.getWidth())/2, (desiredHeight - loadedImage.getHeight())/2, null);
				} else {
					wallpaper = loadedImage;
				}

				try {
					wallpaperMgr.setBitmap(wallpaper);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
