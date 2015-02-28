package com.wzw.ic.mvc.nationalgeographic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewItem;

public class NGViewNodePhotoOfTheDay extends NGViewNode {

	public NGViewNodePhotoOfTheDay() {
		super(URL_PREFIX + "/photography/photo-of-the-day/archive/?page=%d");
		supportPaging = true;
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;
		Elements searchResultsElems = page.select("#search_results div");
		if (null != searchResultsElems && searchResultsElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
			for (int i = 0; i < searchResultsElems.size(); ++i) {
				Element elem = searchResultsElems.get(i);
				
				Elements imgElems = elem.select("img");
				String imgUrl = null;
				if (null != imgElems && imgElems.size() > 0) {
					imgUrl = imgElems.get(0).attr("src");
				}
				
				if (!TextUtils.isEmpty(imgUrl)) {
					ViewItem viewItem = new ViewItem("", "", "http:" + imgUrl.replace("100x75", "990x742").replace("/overrides/", "/cache/"), ViewItem.VIEW_TYPE_GRID, null);
					Elements titleElems = elem.select(".photo_info h4");
					if (null != titleElems && titleElems.size() > 0) {
						viewItem.setLabel(titleElems.get(0).ownText());
					}
					Elements aElems = elem.select("a");
					if (null != aElems && aElems.size() > 0) {
						viewItem.setNodeUrl(URL_PREFIX + aElems.get(0).attr("href"));
                        viewItem.setViewNode(new NGViewNodePhotoOfTheDayPost(viewItem.getNodeUrl()));
					}
					Elements descElems = elem.select(".photo_info p");
					if (null != descElems && descElems.size() > 1) {
						viewItem.setStory(descElems.get(1).ownText());
					}
					Elements pubDateElems = elem.select("p.publication_time");
					if (null != pubDateElems && pubDateElems.size() > 0) {
						String pubDateStr = pubDateElems.get(0).ownText();
						viewItem.setPostedDate(parsePubDate(pubDateStr));
					}
					viewItem.setOrigin(NG_NAME);
					viewItems.add(viewItem);
				}
			}
		}
		return viewItems;
	}

	private static Date parsePubDate(String pubDateStr) {
		// Photo of the Day, November 28, 2014
		try {
			String[] s = pubDateStr.split(" ");
			String month = s[4].toUpperCase(), date = s[5], year = s[6];
			int m, d, y;
			if (month.startsWith("JAN")) {
				m = 1;
			} else if (month.startsWith("FEB")) {
				m = 2;
			} else if (month.startsWith("MAR")) {
				m = 3;
			} else if (month.startsWith("APR")) {
				m = 4;
			} else if (month.startsWith("MAY")) {
				m = 5;
			} else if (month.startsWith("JUN")) {
				m = 6;
			} else if (month.startsWith("JUL")) {
				m = 7;
			} else if (month.startsWith("AUG")) {
				m = 8;
			} else if (month.startsWith("SEP")) {
				m = 9;
			} else if (month.startsWith("OCT")) {
				m = 10;
			} else if (month.startsWith("NOV")) {
				m = 11;
			} else if (month.startsWith("DEC")) {
				m = 12;
			} else {
				return null;
			}
			d = Integer.parseInt(date.substring(0, date.length()-1));
			y = Integer.parseInt(year);
			return new Date(y-1900,m-1,d);
		} catch (Exception e) {
		}
		return null;
	}
}
