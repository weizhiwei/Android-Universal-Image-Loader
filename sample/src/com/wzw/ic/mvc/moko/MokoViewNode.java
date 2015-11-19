package com.wzw.ic.mvc.moko;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.nostra13.example.universalimageloader.MyVolley;
import com.wzw.ic.mvc.ViewNode;

import java.util.List;

public abstract class MokoViewNode extends ViewNode {

    protected String sourceUrl;
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
    public void load(final int pageNo, final Callback<List<ViewNode>> callback) {
        MyVolley.getRequestQueue().add(new StringRequest(Request.Method.GET,
                String.format(sourceUrl, pageNo).replace("|", "%7C"),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String page) {
                        List<ViewNode> viewNodes = extractViewNodesFromPage(pageNo, page);
                        if (null != viewNodes) {
                            callback.onSuccess(viewNodes);
                        } else {
                            callback.onFailure(0, "Invalid Page HTML");
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure(1, "VolleyError " + error);
                    }
                }));
    }

    @Override
    public boolean supportPaging() {
        return supportPaging;
    }

    protected abstract List<ViewNode> extractViewNodesFromPage(int pageNo, String page);
}
