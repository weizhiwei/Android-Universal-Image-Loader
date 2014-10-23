package com.wzw.ic.mvc.flickr;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.wzw.ic.mvc.ViewItem;

public class FlickrViewNodeInterestingness extends FlickrViewNode {

	protected int pageNo;
	
	private List<ViewItem> doLoad(boolean reload) {
		List<ViewItem> pageViewItems = null;
		int newPageNo = reload ? 1 : pageNo + 1;
		
		Flickr f = new Flickr(FLICKR_API_KEY);
		InterestingnessInterface interestingnessInterface = f.getInterestingnessInterface();
		PhotoList photoList = null;
		try {
			photoList = interestingnessInterface.getList((String)null, EXTRAS, 30, newPageNo);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FlickrException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null != photoList && photoList.size() > 0) {
			
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
					ViewItem ownerItem = new ViewItem(owner.getUsername(), owner.getPhotosurl(), owner.getBuddyIconUrl(), ViewItem.VIEW_TYPE_LIST, new FlickrViewNodePeoplePhotosets(owner.getId()));
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
	
	public FlickrViewNodeInterestingness() {
		super("interestingness");
	}

	@Override
	public boolean supportReloading() {
		return true;
	}

	@Override
	public List<ViewItem> reload() {
		return doLoad(true);
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
