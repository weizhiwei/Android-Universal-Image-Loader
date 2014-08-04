package com.nostra13.example.universalimageloader;

import java.io.IOException;
import java.util.Random;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.view.Display;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class WallpaperAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		Object[] images = imageLoader.getMemoryCache().keys().toArray();
		
		if (images.length == 0) {
			return;
		}
		
		String randomImage = (String)images[(new Random()).nextInt(images.length)];
		randomImage = randomImage.substring(0, randomImage.lastIndexOf("_"));
		imageLoader.loadImage(randomImage, new ImageLoadingListener() {
			
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
				WallpaperManager wallpaperMgr = WallpaperManager.getInstance(context);
				try {
					Bitmap wallpaper = null;
					
					Display display = getWindowManager().getDefaultDisplay(); bmp = Bitmap.createScaledBitmap(bmp, display.getWidth(), display.getHeight(), false);
					
					int desiredWidth = wallpaperMgr.getDesiredMinimumWidth();
					int desiredHeight = wallpaperMgr.getDesiredMinimumHeight();
					if (desiredWidth > 0 && desiredHeight > 0) {
						wallpaper = Bitmap.createBitmap(desiredWidth, desiredHeight, Config.ARGB_8888);
						Canvas canvas = new Canvas(wallpaper);
						
						canvas.drawBitmap(loadedImage, 0, (desiredHeight - loadedImage.getHeight())/2, null);
					} else {
						wallpaper = loadedImage;
					}
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
