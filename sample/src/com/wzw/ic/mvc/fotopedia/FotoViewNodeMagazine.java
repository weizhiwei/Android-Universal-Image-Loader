package com.wzw.ic.mvc.fotopedia;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wzw.ic.mvc.ViewItem;

public class FotoViewNodeMagazine extends FotoViewNode {
	
	public FotoViewNodeMagazine() {
		super("magazine");
	}

	protected int pageNo;
	protected String dataSourceStateId;
	
	private void doLoad(boolean reload) {
		int newPageNo = reload ? 0 : pageNo + 1;
		final int PER_PAGE = 30;
		String newDataSourceStateId = reload ? null : dataSourceStateId;
		
		JSONObject jsonObj = null;
		try {
			URL url = new URL(
				String.format("http://www.fotopedia.com/homePageCellItems.json?source=editorial&offset=%d&limit=%d%s",
						newPageNo*PER_PAGE, PER_PAGE, newDataSourceStateId == null ? "" : "&dataSourceStateId=" + newDataSourceStateId)
			);
	        URLConnection yc = url.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	                                    yc.getInputStream()));
	        String inputLine;
	        StringBuffer sb = new StringBuffer();
	        while ((inputLine = in.readLine()) != null) {
	            sb.append(inputLine);
	        }
	        in.close();
			
	        jsonObj = new JSONObject(sb.toString());
		} catch (Exception e) {
			
		}
		if (null != jsonObj) {
	        JSONArray items = jsonObj.optJSONArray("items");
			if (null != items && items.length() > 0) {			
				pageNo = newPageNo;
				dataSourceStateId = jsonObj.optString("dataSourceStateId");
				if (reload) {
					viewItems.clear();
				}
				for (int i = 0; i < items.length(); ++i) {
					JSONObject item = items.optJSONObject(i);
					if (null != item) {
						String title = item.optString("title");
						String nodeUrl = item.optString("staticWebURL");
						JSONObject cover = item.optJSONObject("cover");
						String image = null;
						if (null != cover) {
							String picId = cover.optString("picture_id");
							String format = cover.optString("format");
							if (null != picId && null != format) {
								image = "http://images.cdn.fotopedia.com/" + picId + "-max_480." + format;
							}
						}
						if (null != nodeUrl && null != image) {
							ViewItem viewItem = new ViewItem(
									title,
									"http://www.fotopedia.com" + nodeUrl,
									image,
									ViewItem.VIEW_TYPE_LIST,
									new FotoViewNodeStory("http://www.fotopedia.com" + nodeUrl));
							viewItems.add(viewItem);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean supportReloading() {
		return true;
	}

	@Override
	public void reload() {
		doLoad(true);
	}

	@Override
	public boolean supportPaging() {
		return true;
	}

	@Override
	public void loadOneMorePage() {
		doLoad(false);
	}
}
