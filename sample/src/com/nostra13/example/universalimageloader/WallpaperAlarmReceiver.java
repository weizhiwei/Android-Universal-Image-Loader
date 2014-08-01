package com.nostra13.example.universalimageloader;

import java.io.IOException;
import java.util.Random;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

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
					wallpaperMgr.setBitmap(loadedImage);
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
