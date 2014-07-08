package com.wzw.ic.mvc.moko;

import java.io.IOException;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public abstract class MokoViewNode extends ViewNode {

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
	
	@Override
	public void reload()  {
		doLoad(true);
	}

	private String getLoginKey() {
		if (TextUtils.isEmpty(loginKey)) {
			// login
			Response resp = null;
			try {
				resp = Jsoup
						.connect("http://www.moko.cc/jsps/common/login.action")
						.method(Connection.Method.POST)
						.data("usermingzi", "weizhiwei@gmail.com", "userkey", "85148415")
						.execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (null != resp) {
				loginKey = resp.cookie(LOGIN_KEY_COOKIE);
			}
		}
		return loginKey;
	}
	
	private void doLoad(boolean reload) {
		Document doc = null;
		int newPageNo = reload ? 1 : pageNo + 1;
		
		try {
			doc = Jsoup
					.connect(String.format(sourceUrl, newPageNo))
					.cookie(LOGIN_KEY_COOKIE, null == getLoginKey() ? "" : getLoginKey())
					.get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	}
	
	@Override
	public void loadOneMorePage() {
		doLoad(false);
	}

	@Override
	public boolean supportPaging() {
		return supportPaging;
	}
}
