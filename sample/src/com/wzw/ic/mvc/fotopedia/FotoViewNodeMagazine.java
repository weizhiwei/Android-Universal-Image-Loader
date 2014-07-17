package com.wzw.ic.mvc.fotopedia;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.flickr.FlickrController;

public class FotoViewNodeMagazine extends FotoViewNode {
	protected int pageNo;
	
	private void doLoad(boolean reload) {
		int newPageNo = reload ? 1 : pageNo + 1;
		
		Flickr f = new Flickr(FlickrController.FLICKR_API_KEY);
		InterestingnessInterface interestingnessInterface = f.getInterestingnessInterface();
		PhotoList photoList = null;
		try {
			photoList = interestingnessInterface.getList((String)null, null, 30, newPageNo);
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
	
	public FlickrViewNodeInterestingness() {
		super("interestingness");
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
