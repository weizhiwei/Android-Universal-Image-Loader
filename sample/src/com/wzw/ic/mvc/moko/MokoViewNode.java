package com.wzw.ic.mvc.moko;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.nostra13.example.universalimageloader.MyVolley;
import com.wzw.ic.mvc.ViewNode;

import org.apache.http.cookie.Cookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MokoViewNode extends ViewNode {

    protected String sourceUrl;
    protected int pageNo, newPageNo;
    protected boolean supportPaging;
    protected static String URL_PREFIX = "http://www.moko.cc";

    public MokoViewNode(ViewNode parent, String sourceUrl) {
        super(parent);
        this.sourceUrl = sourceUrl;
    }

    @Override
    public boolean supportReloading() {
        return true;
    }

    @Override
    public void load(final boolean reload, final LoadListener loadListener) {
        newPageNo = reload ? 1 : pageNo + 1;

        final StringRequest myReq = new StringRequest(Request.Method.GET,
                String.format(sourceUrl, newPageNo).replace("|", "%7C"),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Document doc = Jsoup.parse(result);
                        if (doc != null) {
                            List<ViewNode> pageViewItems = extractViewItemsFromPage(doc);
                            if (null != pageViewItems && pageViewItems.size() > 0) {
                                pageNo = newPageNo;
                                if (reload) {
                                    children.clear();
                                }
                                children.addAll(pageViewItems);
                            }
                        }
                        loadListener.onLoadDone(MokoViewNode.this);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //
                        loadListener.onLoadDone(MokoViewNode.this);
                    }
                });

            MyVolley.getRequestQueue().add(myReq);
    }

    @Override
    public boolean supportPaging() {
        return supportPaging;
    }

    protected abstract List<ViewNode> extractViewItemsFromPage(Document page);
}
