package com.wzw.ic.mvc.nationalgeographic;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NGViewNodePhotoOfTheDayPost extends NGViewNode {

    static final Pattern AUTHOR_PATTERN = Pattern.compile(".*Photograph by ([^,]+),.*");

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

                        Elements authorElems = elem.select("p.credit");
                        if (null != authorElems && authorElems.size() > 0) {
                            Matcher m = AUTHOR_PATTERN.matcher(authorElems.get(0).text());
                            if (m.matches()) {
                                ViewItem authorItem = new ViewItem(m.group(1), null,
                                        "http://media-members.nationalgeographic.com/static-media/images/css_images/nationalGeographic_default_avatar.jpg", ViewItem.VIEW_TYPE_GRID, null);
                                viewItem.setAuthor(authorItem);

                                Elements authorLinkElems = authorElems.select("a");
                                if (null != authorLinkElems && authorLinkElems.size() > 0) {
                                    authorItem.setNodeUrl(authorLinkElems.get(0).attr("href"));
                                    Document doc = null;
                                    try {
                                        doc = Jsoup
                                                .connect(authorItem.getNodeUrl())
                                                .get();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    if (doc != null) {
                                        Elements avatarElems = doc.select("div.avatar img");
                                        if (null != avatarElems && avatarElems.size() > 0) {
                                            String avatarUrl = avatarElems.get(0).attr("src");
                                            if (!TextUtils.isEmpty(avatarUrl)) {
                                                if (avatarUrl.startsWith("//")) {
                                                    avatarUrl = "http:" + avatarUrl;
                                                }
                                                authorItem.setImageUrl(avatarUrl);
                                            }
                                        }
                                    }
                                }
                            }
                        }

//                        Elements pubDateElems = elem.select("p.publication_time");
//                        if (null != pubDateElems && pubDateElems.size() > 0) {
//                            String pubDateStr = pubDateElems.get(0).ownText();
//                            viewItem.setPostedDate(parsePubDate(pubDateStr));
//                        }

                        Elements descElems = elem.select("p:not([class], :has(em:only-child))");
                        if (null != descElems && descElems.size() > 0) {
                            StringBuilder sb = new StringBuilder();
                            int i = 0;
                            for (Element e: descElems) {
                                if (0 != i++) {
                                    sb.append("<br/><br/>");
                                }
                                sb.append(e.ownText());
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
