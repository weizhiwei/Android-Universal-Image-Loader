/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.example.universalimageloader;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.ViewNodeAction;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public abstract class BaseActivity extends ActionBarActivity {

	protected Menu menu;
	private boolean wallpaperServiceEnabled = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
		
	protected static void setHasEmbeddedTabs(Object inActionBar, final boolean inHasEmbeddedTabs)
	{
	    // get the ActionBar class
	    Class<?> actionBarClass = inActionBar.getClass();

	    // if it is a Jelly Bean implementation (ActionBarImplJB), get the super class (ActionBarImplICS)
	    if ("android.support.v7.app.ActionBarImplJB".equals(actionBarClass.getName()))
	    {
	            actionBarClass = actionBarClass.getSuperclass();
	    }

	    try
	    {
	            // try to get the mActionBar field, because the current ActionBar is probably just a wrapper Class
	            // if this fails, no worries, this will be an instance of the native ActionBar class or from the ActionBarImplBase class
	            final Field actionBarField = actionBarClass.getDeclaredField("mActionBar");
	            actionBarField.setAccessible(true);
	            inActionBar = actionBarField.get(inActionBar);
	            actionBarClass = inActionBar.getClass();
	    }
	    catch (IllegalAccessException e) {}
	    catch (IllegalArgumentException e) {}
	    catch (NoSuchFieldException e) {}

	    try
	    {
	            // now call the method setHasEmbeddedTabs, this will put the tabs inside the ActionBar
	            // if this fails, you're on you own <img src="http://www.blogc.at/wp-includes/images/smilies/icon_wink.gif" alt=";-)" class="wp-smiley">
	            final Method method = actionBarClass.getDeclaredMethod("setHasEmbeddedTabs", new Class[] { Boolean.TYPE });
	            method.setAccessible(true);
	            method.invoke(inActionBar, new Object[]{ inHasEmbeddedTabs });
	    }
	    catch (NoSuchMethodException e)        {}
	    catch (InvocationTargetException e) {}
	    catch (IllegalAccessException e) {}
	    catch (IllegalArgumentException e) {}
	}

	public boolean isFullscreen() {
		return (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
	}
	
	@SuppressLint("NewApi")
	public void setFullscreen(boolean fullscreen) {
		if (fullscreen) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
			
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		}
		
		if (Build.VERSION.SDK_INT >= 16) {
			View decorView = getWindow().getDecorView();
			if (fullscreen) {
	        	// Hide the status bar.
	        	int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
	        	decorView.setSystemUiVisibility(uiOptions);
	        } else {
			    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			    decorView.setSystemUiVisibility(uiOptions);
	        }
		}
		
		ActionBar actionBar = getSupportActionBar();
		if (fullscreen) {
	        actionBar.hide();
	    } else {
			actionBar.show();
	    }
	}
	
	public void toggleFullscreen() {
		setFullscreen(!isFullscreen());
	}
	
	protected void updateMenu(ViewNode model) {
		if (null != menu && null != model && null != model.getActions()) {
			for (ViewNodeAction action: model.getActions()) {
				MenuItem item = menu.findItem(action.getId());
				item.setTitle(action.getTitle());
				item.setVisible(action.isVisible());
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		this.menu = menu;
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_wallpaper_toggle:
				wallpaperServiceEnabled = !wallpaperServiceEnabled;
				item.setChecked(wallpaperServiceEnabled);
				enableWallpaperService(wallpaperServiceEnabled);
				return true;
            case R.id.item_settings:
                return true;
			default:
				return false;
		}
	}
	
	private void enableWallpaperService(boolean enabled) {
		WallpaperAlarmReceiver.enableWallpaperAlarms(this, enabled);
		
		ComponentName receiver = new ComponentName(this, BootReceiver.class);
		PackageManager pm = getPackageManager();
		pm.setComponentEnabledSetting(receiver,
		        enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		        PackageManager.DONT_KILL_APP);
	}
	
	protected static SpannableString buildPictureText(final ViewNode viewItem, boolean needTitle, boolean needAuthor, boolean needStory, boolean bigFont, boolean labelLinkOn, boolean ownerLinkOn) {
		String story = "";
		if (needTitle && !TextUtils.isEmpty(viewItem.getTitle())) {
			String LINK = labelLinkOn ? "<a href=\"%s\">%s</a>" : "%2$s";
			String FONT = String.format(bigFont ? "<big><b>%s</b></big>" : "%s", LINK);
			story += String.format(FONT, viewItem.getWebPageUrl(), TextUtils.htmlEncode(viewItem.getTitle()));
		}
		String authorName = (viewItem.getAuthor() == null ? null : viewItem.getAuthor().getTitle());
		if (needAuthor && !TextUtils.isEmpty(authorName)) {
			String FONT = String.format(bigFont ? "<big>%s</big>" : "%s", "<i>%s</i>");
			story += String.format(" by " + FONT, TextUtils.htmlEncode(authorName));
		}
		if (needStory && !TextUtils.isEmpty(viewItem.getStory())) {
			if (!TextUtils.isEmpty(story)) {
				story += "<br/><br/>";
			}
//			story += TextUtils.htmlEncode(viewItem.getStory());
            story += viewItem.getStory();
		}
		
		SpannableString ss = null;
		if (!TextUtils.isEmpty(story)) {
			ss = new SpannableString(Html.fromHtml(story));
			if (needAuthor && ownerLinkOn && !TextUtils.isEmpty(authorName)) {
				int start = ss.toString().indexOf("by " + authorName) + 3;
				int end = start + authorName.length();
				ss.setSpan(new ClickableSpan () {

					@Override
					public void onClick(View arg0) {
//						startViewItemActivity(null, viewItem.getAuthor());
					}
					
				}, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		
		return ss;
	}
}
