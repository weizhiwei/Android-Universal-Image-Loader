package com.wzw.ic.mvc.nationalgeographic;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewItem;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NGViewNodePhotoOfTheDayPost extends NGViewNode {

	public NGViewNodePhotoOfTheDayPost(String sourceUrl) {
        super(sourceUrl);
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;

        		Elements imgElems = page.select("div.primary_photo img");
				String imgUrl = null;
				if (null != imgElems && imgElems.size() > 0) {
					imgUrl = imgElems.get(0).attr("src");
				}
				
				if (!TextUtils.isEmpty(imgUrl)) {
                    viewItems = new ArrayList<ViewItem>();
                    ViewItem viewItem = new ViewItem("", "", "http:" + imgUrl, ViewItem.VIEW_TYPE_IMAGE_PAGER, this);
                    Elements captionElems = page.select("#caption");
                    if (null != captionElems && captionElems.size() > 0) {
                        Element elem = captionElems.get(0);
                        Elements titleElems = elem.select("h2");
                        if (null != titleElems && titleElems.size() > 0) {
                            viewItem.setLabel(titleElems.get(0).ownText());
                        }

                        Elements authorElems = elem.select("p.credit a");
                        if (null != authorElems && authorElems.size() > 0) {
                            ViewItem authorItem = new ViewItem(authorElems.get(0).ownText(), authorElems.get(0).attr("href"), "", ViewItem.VIEW_TYPE_GRID, null);
                            viewItem.setAuthor(authorItem);
                        }

//                        Elements pubDateElems = elem.select("p.publication_time");
//                        if (null != pubDateElems && pubDateElems.size() > 0) {
//                            String pubDateStr = pubDateElems.get(0).ownText();
//                            viewItem.setPostedDate(parsePubDate(pubDateStr));
//                        }

                        Elements descElems = elem.select("p:not([class], :has(em:only-child))");
                        if (null != descElems && descElems.size() > 0) {
                            StringBuilder sb = new StringBuilder();
                            for (Element e: descElems) {
                                sb.append(e.ownText());
                                sb.append("<br/><br/>");
                            }
                            viewItem.setStory(sb.toString());
                        }
                        viewItem.setOrigin(NG_NAME);
                        viewItems.add(viewItem);
                    }
                }
		return viewItems;
	}
}
