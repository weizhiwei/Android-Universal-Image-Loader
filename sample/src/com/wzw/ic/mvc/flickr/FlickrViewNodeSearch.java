package com.wzw.ic.mvc.flickr;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.galleries.GalleriesInterface;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;
import com.wzw.ic.mvc.ViewItem;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlickrViewNodeSearch extends FlickrViewNode {
	protected int pageNo;
    protected SearchParameters searchParams;
    protected int perPage;

	public FlickrViewNodeSearch(String sourceUrl) {
		super(sourceUrl);
        searchParams = new SearchParameters();

        searchParams.setText(sourceUrl);
        searchParams.setSort(SearchParameters.RELEVANCE);
        searchParams.setHasGeo(true);
        searchParams.setExtras(EXTRAS);

        perPage = 30;
	}

    public SearchParameters getSearchParameters() {
        return searchParams;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
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
        PhotosInterface photosInterface = f.getPhotosInterface();
        PhotoList photoList = null;
		try {
			photoList = photosInterface.search(searchParams, perPage, newPageNo);
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
				ViewItem viewItem = new ViewItem(photo.getTitle(), photo.getUrl(), photo.getMediumUrl(), ViewItem.VIEW_TYPE_IMAGE_PAGER, this);
				viewItem.setOrigin(FLICKR_NAME);
				viewItem.setStory(photo.getDescription());
				viewItem.setPostedDate(photo.getDatePosted());
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
