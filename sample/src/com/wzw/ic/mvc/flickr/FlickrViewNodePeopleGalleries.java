package com.wzw.ic.mvc.flickr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.SearchResultList;
import com.googlecode.flickrjandroid.galleries.GalleriesInterface;
import com.googlecode.flickrjandroid.galleries.Gallery;
import com.wzw.ic.mvc.ViewItem;

public class FlickrViewNodePeopleGalleries extends FlickrViewNode {
	protected int pageNo;
	
	public FlickrViewNodePeopleGalleries(String sourceUrl) {
		super(sourceUrl);
	}

	@Override
	public boolean supportReloading() {
		return true;
	}

	@Override
	public List<ViewItem> reload() {
		return doLoad(true);
	}
	
	private List<ViewItem> doLoad(boolean reload) {
		List<ViewItem> pageViewItems = null;
		int newPageNo = reload ? 1 : pageNo + 1;
		
		Flickr f = new Flickr(FLICKR_API_KEY);
		GalleriesInterface galleriesInterface = f.getGalleriesInterface();
		SearchResultList<Gallery> galleries = null;
		try {
			galleries = galleriesInterface.getList(sourceUrl, 30, newPageNo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FlickrException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (null != galleries &&
			galleries.size() > 0) {
			
			// hit the end
			if (!reload &&
				galleries.getPages() <= pageNo) {
				pageNo = galleries.getPages();
				return pageViewItems;
			}
			
			pageViewItems = new ArrayList<ViewItem> (galleries.size());
			for (Gallery gallery: galleries) {
				ViewItem viewItem = new ViewItem(
						gallery.getTitle(),
						String.format("https://www.flickr.com/photos/flickr/galleries/%s/",
								gallery.getGalleryId()),
								gallery.getPrimaryPhoto().getLargeSquareUrl(),
								ViewItem.VIEW_TYPE_GRID,
								new FlickrViewNodeGallery(gallery.getGalleryId()));
				viewItem.setOrigin(FLICKR_NAME);
				viewItem.setInitialZoomLevel(1);
				pageViewItems.add(viewItem);
			}
			
			if (null != pageViewItems && pageViewItems.size() > 0) {
				pageNo = newPageNo;
				if (reload) {
					viewItems.clear();
				}
				viewItems.addAll(pageViewItems);
			}
		}
		return pageViewItems;
	}
	
	@Override
	public boolean supportPaging() {
		return true;
	}

	@Override
	public List<ViewItem> loadOneMorePage() {
		return doLoad(false);
	}
}
