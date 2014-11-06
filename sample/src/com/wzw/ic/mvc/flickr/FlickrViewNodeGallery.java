package com.wzw.ic.mvc.flickr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.galleries.GalleriesInterface;
import com.googlecode.flickrjandroid.people.User;
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
	public List<ViewItem> reload() {
		return doLoad(true);
	}

	private List<ViewItem> doLoad(boolean reload) {
		List<ViewItem> pageViewItems = null;
		int newPageNo = reload ? 1 : pageNo + 1;
		
		Flickr f = new Flickr(FLICKR_API_KEY);
		GalleriesInterface galleriesInterface = f.getGalleriesInterface();
		PhotoList photoList = null;
		try {
			photoList = galleriesInterface.getPhotos(sourceUrl, EXTRAS, 30, newPageNo);
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
				return pageViewItems;
			}
			
			pageViewItems = new ArrayList<ViewItem> (photoList.size());
			
			for (Photo photo: photoList) {
				ViewItem viewItem = new ViewItem(photo.getTitle(), photo.getUrl(), photo.getLargeUrl(), ViewItem.VIEW_TYPE_IMAGE_PAGER, this);
				viewItem.setOrigin(FLICKR_NAME);
				viewItem.setStory(photo.getDescription());
				User owner = photo.getOwner();
				if (null != owner) {
					ViewItem ownerItem = new ViewItem(owner.getUsername(), owner.getPhotosurl(), owner.getBuddyIconUrl(), ViewItem.VIEW_TYPE_GRID, new FlickrViewNodePeoplePhotosets(owner.getId()));
					ownerItem.setInitialZoomLevel(2);
					ownerItem.setOrigin(FLICKR_NAME);
					viewItem.setAuthor(ownerItem);
				}
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
