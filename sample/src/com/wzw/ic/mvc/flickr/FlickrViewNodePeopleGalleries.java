package com.wzw.ic.mvc.flickr;

import java.io.IOException;

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
	public void reload() {
		doLoad(true);
	}
	
	private void doLoad(boolean reload) {
		int newPageNo = reload ? 1 : pageNo + 1;
		
		Flickr f = new Flickr(FlickrController.FLICKR_API_KEY);
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
				return;
			}
			
			pageNo = newPageNo;
			if (reload) {
				viewItems.clear();
			}
			
			for (Gallery gallery: galleries) {
				ViewItem viewItem = new ViewItem(gallery.getTitle(), String.format("https://www.flickr.com/photos/flickr/galleries/%s/", gallery.getGalleryId()), gallery.getPrimaryPhoto().getLargeSquareUrl(), 0);
				viewItems.add(viewItem);
			}
		}
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
