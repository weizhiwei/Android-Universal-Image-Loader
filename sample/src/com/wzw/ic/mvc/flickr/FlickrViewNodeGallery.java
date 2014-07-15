package com.wzw.ic.mvc.flickr;

import java.io.IOException;

import org.json.JSONException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.galleries.GalleriesInterface;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.wzw.ic.mvc.ViewItem;

public class FlickrViewNodeGallery extends FlickrViewNode {
	protected int pageNo;
	
	public FlickrViewNodeGallery(String sourceUrl) {
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
		PhotoList photoList = null;
		try {
			photoList = galleriesInterface.getPhotos(sourceUrl, null, 30, newPageNo);
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
		
		if (null != photoList &&
			photoList.size() > 0) {
			
			// hit the end
			if (!reload &&
				photoList.getPages() <= pageNo) {
				pageNo = photoList.getPages();
				return;
			}
			
			pageNo = newPageNo;
			if (reload) {
				viewItems.clear();
			}
			for (Photo photo: photoList) {
				ViewItem viewItem = new ViewItem(photo.getTitle(), "", photo.getLargeUrl(), 0);
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
