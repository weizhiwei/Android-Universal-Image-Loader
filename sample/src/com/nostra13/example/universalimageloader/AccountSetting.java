package com.nostra13.example.universalimageloader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
    public void onClick(DialogInterface dialog, int which) {
        Dialog dlg = getDialog();
        EditText username = (EditText) dlg.findViewById(R.id.username);
        EditText password = (EditText) dlg.findViewById(R.id.password);
        AccountManager.login(getContext(), username.getText().toString(), password.getText().toString());
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

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();
        AccountManager.decorateLoginDialog(view);
        return view;
    }
}
