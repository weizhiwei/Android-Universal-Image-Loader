package com.wzw.ic.mvc.wikipedia;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiweiwei on 4/17/15.
 */
public class WikipediaViewNode extends ViewNode {
    protected static String URL_PREFIX = "http://en.m.wikipedia.org";
    protected static String URL_SEARCH = URL_PREFIX + "/w/index.php?title=Special%%3ASearch&profile=default&search=%s&fulltext=Search";

    public WikipediaViewNode(String sourceUrl) {
        super(sourceUrl);
    }

    @Override
    public boolean supportReloading() {
        return true;
    }

    @Override
    public List<ViewItem> reload()  {
        return doLoad(true);
    }

    private List<ViewItem> doLoad(boolean reload) {
        String searchUrl = String.format(URL_SEARCH, sourceUrl);

        ViewItem viewItem = new ViewItem(null, searchUrl, null, 0, null);
        List<ViewItem> pageViewItems = new ArrayList<ViewItem>(1);
        pageViewItems.add(viewItem);

        Document doc = null;
        try {
            doc = Jsoup
                    .connect(searchUrl)
                    .get();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (doc != null) {
            Elements articleElems = doc.select(".mw-search-exists a");
            if (null != articleElems && articleElems.size() > 0) {
                viewItem.setNodeUrl(URL_PREFIX + articleElems.get(0).attr("href"));
            }
        }
        if (null != pageViewItems && pageViewItems.size() > 0) {
            if (reload) {
                viewItems.clear();
            }
            viewItems.addAll(pageViewItems);
        }
        return pageViewItems;
    }

    @Override
    public boolean supportPaging() {
        return false;
    }
}
