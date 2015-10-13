package com.nostra13.example.universalimageloader;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by zhiweiwei on 10/13/15.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
