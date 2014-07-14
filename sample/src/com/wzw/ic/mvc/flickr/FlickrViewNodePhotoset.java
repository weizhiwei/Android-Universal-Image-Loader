package com.wzw.ic.mvc.flickr;

import java.io.IOException;

import org.json.JSONException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;
import com.wzw.ic.mvc.ViewItem;

public class FlickrViewNodePhotoset extends FlickrViewNode {
	protected int pageNo;
	
	public FlickrViewNodePhotoset(String sourceUrl) {
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
		
		String apiKey = "6076b3fca0851330568d880610c70267";
		Flickr f = new Flickr(apiKey);
		PhotosetsInterface photosetsInterface = f.getPhotosetsInterface();
		Photoset photoset = null;
		try {
			photoset = photosetsInterface.getPhotos(sourceUrl, 30, newPageNo);
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
		
		if (null != photoset &&
			null != photoset.getPhotoList() &&
			photoset.getPhotoList().size() > 0) {
			
			PhotoList photoList = photoset.getPhotoList();
			
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
