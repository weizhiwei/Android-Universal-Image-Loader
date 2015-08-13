package com.nostra13.example.universalimageloader;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class WallpaperAlarmReceiver extends BroadcastReceiver {
	
	public static void enableWallpaperAlarms(Context context, boolean enabled) {
		AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, WallpaperAlarmReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		if (enabled) {
			alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, 60*1000, alarmIntent);
		} else {
			alarmMgr.cancel(alarmIntent);
		}
	}
	
	public static void setWallpaper(final Context context, String imageUrl) {
//        Ion.with(context).load(imageUrl).asBitmap().setCallback(new FutureCallback<Bitmap>() {
//            @Override
//            public void onCompleted(Exception e, Bitmap loadedImage) {
//                if (null == loadedImage) {
//                    return;
//                }
//
//                int imageWidth = loadedImage.getWidth();
//                int imageHeight = loadedImage.getHeight();
//
//                DisplayMetrics metrics = new DisplayMetrics();
//                ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
//                int screenWidth = metrics.widthPixels;
//                int screenHeight = metrics.heightPixels;
//
//                if (screenWidth*imageHeight > imageWidth*screenHeight) {
//                    loadedImage = Bitmap.createScaledBitmap(loadedImage, screenHeight*imageWidth/imageHeight, screenHeight, false);
//                } else {
//                    loadedImage = Bitmap.createScaledBitmap(loadedImage, screenWidth, screenWidth*imageHeight/imageWidth, false);
//                }
//
//                WallpaperManager wallpaperMgr = WallpaperManager.getInstance(context);
//                Bitmap wallpaper = null;
//
//                // For compatibility
//                wallpaperMgr.suggestDesiredDimensions(screenWidth, screenHeight);
//
//                int desiredWidth = wallpaperMgr.getDesiredMinimumWidth();
//                int desiredHeight = wallpaperMgr.getDesiredMinimumHeight();
//                if (desiredWidth > 0 && desiredHeight > 0) {
//                    wallpaper = Bitmap.createBitmap(desiredWidth, desiredHeight, Config.ARGB_8888);
//                    Canvas canvas = new Canvas(wallpaper);
//                    canvas.drawBitmap(loadedImage, (desiredWidth - loadedImage.getWidth())/2, (desiredHeight - loadedImage.getHeight())/2, null);
//                } else {
//                    wallpaper = loadedImage;
//                }
//
//                try {
//                    wallpaperMgr.setBitmap(wallpaper);
//                } catch (IOException ioe) {
//                    // TODO Auto-generated catch block
//                    ioe.printStackTrace();
//                }
//            }
//        });
	}
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		
//		int heartCount = IcDatabase.getInstance().getViewItemsInHeartsCount();
//		if (heartCount <= 0) {
//			return;
//		}
//
//		List<ViewItem> pageViewItems = IcDatabase.getInstance().fetchAllViewItemsInHearts(
//				(new Random()).nextInt(heartCount), 1);
//
//		if (null != pageViewItems && pageViewItems.size() > 0) {
//			setWallpaper(context, pageViewItems.get(0).getImageUrl());
//		}
	}
}
