package com.wzw.ic.mvc.flickr;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.commons.CommonsInterface;
import com.googlecode.flickrjandroid.commons.Institution;
import com.wzw.ic.mvc.ViewItem;

public class FlickrViewNodeCommons extends FlickrViewNode {
	protected int pageNo;
	
	public FlickrViewNodeCommons() {
		super("commons");
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
		Flickr f = new Flickr(FlickrController.FLICKR_API_KEY);
		CommonsInterface commonsInterface = f.getCommonsInterface();
		List<Institution> institutions = null;
		try {
			institutions = commonsInterface.getInstitutions();
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
		
		if (null != institutions &&
			institutions.size() > 0) {
			
			viewItems.clear();
			for (Institution institution: institutions) {
				ViewItem viewItem = new ViewItem(institution.getName(), String.format("https://www.flickr.com/people/%s/", institution.getId()), "", 0);
				viewItems.add(viewItem);
			}
		}
	}
	
	@Override
	public boolean supportPaging() {
		return false;
	}

	@Override
	public void loadOneMorePage() {
	}
}
