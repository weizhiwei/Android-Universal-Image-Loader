package com.wzw.ic.mvc.flickr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	public List<ViewItem> reload() {
		return doLoad(true);
	}
	
	private List<ViewItem> doLoad(boolean reload) {
		List<ViewItem> pageViewItems = null;
		int newPageNo = reload ? 1 : pageNo + 1;
		
		Flickr f = new Flickr(FLICKR_API_KEY);
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
				return pageViewItems;
			}
			
			User owner = new User();
			owner.setId(sourceUrl);
			
			pageViewItems = new ArrayList<ViewItem> (photosetsCollection.size());
			for (Photoset photoset: photosetsCollection) {
				photoset.setOwner(owner);
				ViewItem viewItem = new ViewItem(
						photoset.getTitle(),
						photoset.getUrl(),
						photoset.getPrimaryPhoto().getLargeSquareUrl(),
						ViewItem.VIEW_TYPE_GRID,
						new FlickrViewNodePhotoset(photoset.getId()));
				viewItem.setOrigin(FLICKR_NAME);
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
