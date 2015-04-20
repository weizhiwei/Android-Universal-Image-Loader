package com.wzw.ic.mvc.google;

import com.wzw.ic.mvc.ViewItem;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class GoogleViewNodeGeocoder extends GoogleViewNode {

    protected static String URL_PREFIX = "https://maps.googleapis.com/maps/api/geocode/xml?address=%s";

    public GoogleViewNodeGeocoder(String sourceUrl) {
		super(String.format(URL_PREFIX, sourceUrl));
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;
		Elements articleElems = page.select("geometry location");
		if (null != articleElems && articleElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
            Element resultElem = articleElems.get(0);
            ViewItem viewItem = new ViewItem(null, null, null, 0, null);
            viewItem.setLat(Double.parseDouble(resultElem.getElementsByTag("lat").get(0).text()));
            viewItem.setLng(Double.parseDouble(resultElem.getElementsByTag("lng").get(0).text()));
            viewItems.add(viewItem);

            Elements viewportElems = page.select("geometry viewport");
            if (null != viewportElems && viewportElems.size() > 0) {
                Element viewportElem = viewportElems.get(0);
                Element southwest = viewportElem.getElementsByTag("southwest").get(0);
                Element northeast = viewportElem.getElementsByTag("northeast").get(0);
                viewItem.setViewport(new double[] {
                        Double.parseDouble(southwest.getElementsByTag("lat").get(0).text()),
                        Double.parseDouble(southwest.getElementsByTag("lng").get(0).text()),
                        Double.parseDouble(northeast.getElementsByTag("lat").get(0).text()),
                        Double.parseDouble(northeast.getElementsByTag("lng").get(0).text()),
                });
            }
		}

		return viewItems;
	}
}
