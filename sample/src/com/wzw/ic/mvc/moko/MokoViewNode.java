package com.wzw.ic.mvc.moko;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.text.TextUtils;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public abstract class MokoViewNode extends ViewNode {

	public static String MOKO_NAME = "moko";
	public static String MOKO_ICON = "http://www.vitbbs.cn/uploads/allimg/c101125/12ZEK63410-14106.gif";
	
	protected int pageNo;
	protected boolean supportPaging;
	protected static String URL_PREFIX = "http://www.moko.cc";
	
	private static String loginKey;
	private static String LOGIN_KEY_COOKIE = "NEWMOKO_USER_LOGINKEY";
	
	public MokoViewNode(String sourceUrl) {
		super(sourceUrl);
	}

	@Override
	public boolean supportReloading() {
		return true;
	}

	private String getLoginKey(Context context) {
		if (TextUtils.isEmpty(loginKey)) {
			// login
//			Response resp = null;
			try {
//				resp = Jsoup
//						.connect("http://www.moko.cc/jsps/common/login.action")
//						.method(Connection.Method.POST)
//						.data("usermingzi", "weizhiwei@gmail.com", "userkey", "85148415")
//						.execute();
            Ion.with(context)
                    .load("http://www.moko.cc/jsps/common/login.action")
                    .setBodyParameter("usermingzi", "weizhiwei@gmail.com")
                    .setBodyParameter("userkey", "85148415")
                    .asString()
                    .get();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			if (null != resp) {
//				loginKey = resp.cookie(LOGIN_KEY_COOKIE);
//			}

            for (HttpCookie cookie: Ion.getDefault(context).getCookieMiddleware().getCookieStore().get(URI.create(URL_PREFIX))) {
                if (LOGIN_KEY_COOKIE.equals(cookie.getName())) {
                    loginKey = cookie.getValue();
                }
            }
		}
		return loginKey;
	}

    @Override
	public List<ViewItem> load(Context context, final boolean reload, final LoadListener loadListener) {
		final int newPageNo = reload ? 1 : pageNo + 1;

		try {
            Ion.getDefault(context).getCookieMiddleware().getCookieManager().getCookieStore().add(
                    URI.create(URL_PREFIX), new HttpCookie(LOGIN_KEY_COOKIE, null == getLoginKey(context) ? "" : getLoginKey(context)));
            Ion.with(context)
                    .load(String.format(sourceUrl, perturbPageNo(newPageNo, reload)))
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            Document doc = Jsoup.parse(result);
                            if (doc != null) {
                                List<ViewItem> pageViewItems = extractViewItemsFromPage(doc);
                                if (null != pageViewItems && pageViewItems.size() > 0) {
                                    pageNo = newPageNo;
                                    if (reload) {
                                        viewItems.clear();
                                    }
                                    viewItems.addAll(pageViewItems);
                                }
                            }
                            loadListener.onLoadDone(MokoViewNode.this);
                        }
                    });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean supportPaging() {
		return supportPaging;
	}

	protected abstract List<ViewItem> extractViewItemsFromPage(Document page);
}
