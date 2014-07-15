package com.wzw.ic.mvc.flickr;

import java.io.IOException;
import java.util.Collection;

import org.json.JSONException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.Photosets;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;
import com.wzw.ic.mvc.ViewItem;

public class FlickrViewNodePeoplePhotosets extends FlickrViewNode {

	protected int pageNo;
	
	public FlickrViewNodePeoplePhotosets(String sourceUrl) {
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
		PhotosetsInterface photosetsInterface = f.getPhotosetsInterface();
		Photosets photosets = null;
		try {
			photosets = photosetsInterface.getList(sourceUrl, 30, newPageNo);
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
		
		if (null != photosets &&
			null != photosets.getPhotosets() &&
			photosets.getPhotosets().size() > 0) {
			
			Collection<Photoset> photosetsCollection = photosets.getPhotosets();
			
			// hit the end
			if (!reload &&
				photosets.getPages() <= pageNo) {
				pageNo = photosets.getPages();
				return;
			}
			
			pageNo = newPageNo;
			if (reload) {
				viewItems.clear();
			}
			
			User owner = new User();
			owner.setId(sourceUrl);
			
			for (Photoset photoset: photosetsCollection) {
				photoset.setOwner(owner);
				ViewItem viewItem = new ViewItem(photoset.getTitle(), photoset.getUrl(), photoset.getPrimaryPhoto().getLargeSquareUrl(), 0);
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
