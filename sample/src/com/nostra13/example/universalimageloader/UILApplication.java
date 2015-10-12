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

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import com.nostra13.example.universalimageloader.Constants.Config;
import com.wzw.ic.mvc.ViewNode;

import java.util.WeakHashMap;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class UILApplication extends Application {

    private WeakHashMap<String, ViewNode> viewNodeRegistry;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
//		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
//			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
//		}

		super.onCreate();

		IcDatabase.getInstance().open(getApplicationContext());

        MyVolley.init(getApplicationContext());

        viewNodeRegistry = new WeakHashMap<>();
    }

    public String registerViewNode(ViewNode viewNode) {
        String key = String.valueOf(System.currentTimeMillis());
        viewNodeRegistry.put(key, viewNode);
        return key;
    }

    public ViewNode getRegisteredViewNode(String key) {
        return viewNodeRegistry.get(key);
    }
}