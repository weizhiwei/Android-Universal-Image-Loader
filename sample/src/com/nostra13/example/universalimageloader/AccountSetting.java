package com.nostra13.example.universalimageloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by zhiweiwei on 10/15/15.
 */
public class AccountSetting extends DialogPreference {
    public AccountSetting(Context context, AttributeSet attrs) {
        super(context, attrs);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        String currentUsername = settings.getString("pref_key_account_username", "");

        if (TextUtils.isEmpty(currentUsername)) {
            setDialogTitle(R.string.sign_in_to_moko);
            setDialogLayoutResource(R.layout.login);
            setTitle(R.string.not_signed_in);
            setSummary(R.string.click_to_sign_in);
        } else {
            setDialogMessage(R.string.confirm_sign_out);
            setTitle(getContext().getResources().getText(R.string.current_user) + currentUsername);
            setSummary(R.string.click_to_sign_out);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            setDialogLayoutResource(0);
            setDialogTitle(null);
            setDialogMessage(R.string.confirm_sign_out);
            setTitle(getContext().getResources().getText(R.string.current_user) + String.valueOf(System.currentTimeMillis()));
            setSummary(R.string.click_to_sign_out);
        }
    }
}
