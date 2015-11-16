package com.nostra13.example.universalimageloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

import org.apache.http.cookie.Cookie;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhiweiwei on 11/4/15.
 */
public class AccountManager {

    public static void decorateLoginDialog(View view) {
        final TextView signUpLink = (TextView) view.findViewById(R.id.sign_up);
        signUpLink.setMovementMethod(LinkMovementMethod.getInstance());
        final TextView forgotPasswordLink = (TextView) view.findViewById(R.id.forgot_password);
        forgotPasswordLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void checkSession(final Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        final String value = settings.getString("pref_key_cookie_value", null);
        final Date expiry = new Date(settings.getLong("pref_key_cookie_expiry", 0));
        final String domain = settings.getString("pref_key_cookie_domain", null);
        final String path = settings.getString("pref_key_cookie_path", null);

        if (!TextUtils.isEmpty(value)) {
            MyVolley.getHttpClient().getCookieStore().addCookie(new Cookie() {
                @Override
                public String getName() {
                    return context.getString(R.string.moko_cookie_key);
                }

                @Override
                public String getValue() {
                    return value;
                }

                @Override
                public String getComment() {
                    return null;
                }

                @Override
                public String getCommentURL() {
                    return null;
                }

                @Override
                public Date getExpiryDate() {
                    return expiry;
                }

                @Override
                public boolean isPersistent() {
                    return false;
                }

                @Override
                public String getDomain() {
                    return domain;
                }

                @Override
                public String getPath() {
                    return path;
                }

                @Override
                public int[] getPorts() {
                    return new int[0];
                }

                @Override
                public boolean isSecure() {
                    return false;
                }

                @Override
                public int getVersion() {
                    return 0;
                }

                @Override
                public boolean isExpired(Date date) {
                    return date.after(getExpiryDate());
                }
            });
        }
    }

    public static void logout(final Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("pref_key_account_username");
        editor.remove("pref_key_account_username");
        editor.remove("pref_key_account_password");
        editor.remove("pref_key_cookie_expiry");
        editor.remove("pref_key_cookie_value");
        editor.remove("pref_key_cookie_domain");
        editor.remove("pref_key_cookie_path");
        editor.commit();

        Cookie cookie = null;
        for (Cookie c : MyVolley.getHttpClient().getCookieStore().getCookies()) {
            if (context.getString(R.string.moko_cookie_key).equals(c.getName())) {
                cookie = c;
                break;
            }
        }
        if (null != cookie) {
            MyVolley.getHttpClient().getCookieStore().getCookies().remove(cookie);
        }
    }

    public static void login(final Context context, final String username, final String password) {

        MyVolley.getRequestQueue().add(new StringRequest(Request.Method.POST,
                String.format("http://www.moko.cc/jsps/common/login.action"),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        for (Cookie cookie : MyVolley.getHttpClient().getCookieStore().getCookies()) {
                            if (context.getString(R.string.moko_cookie_key).equals(cookie.getName())) {

                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("pref_key_account_username", username);
                                editor.putString("pref_key_account_password", password);
                                editor.putLong("pref_key_cookie_expiry", cookie.getExpiryDate().getTime());
                                editor.putString("pref_key_cookie_value", cookie.getValue());
                                editor.putString("pref_key_cookie_domain", cookie.getDomain());
                                editor.putString("pref_key_cookie_path", cookie.getPath());
                                editor.commit();

                                break;
                            }
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // login error
                        System.out.println(error);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws com.android.volley.error.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("usermingzi", username);
                params.put("userkey", password);
                params.put("isremember", "on");
                return params;
            }
        });
    }
}
