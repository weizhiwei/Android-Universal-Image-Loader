package com.wzw.ic.mvc.root;

import java.util.Arrays;

import com.nostra13.example.universalimageloader.R;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.flickr.FlickrViewNode;
import com.wzw.ic.mvc.flickr.FlickrViewNodeRoot;
import com.wzw.ic.mvc.hearts.HeartsViewNodeRoot;
import com.wzw.ic.mvc.moko.MokoViewNode;
import com.wzw.ic.mvc.moko.MokoViewNodeRoot;
import com.wzw.ic.mvc.nationalgeographic.NGViewNode;
import com.wzw.ic.mvc.nationalgeographic.NGViewNodeRoot;
import com.wzw.ic.mvc.stream.StreamViewNodeRoot;

public class RootViewNode extends ViewNode {

	public RootViewNode() {
		
		super("root", null);
		
		ViewItem moko = new ViewItem("MOKO!", "moko", MokoViewNode.MOKO_ICON, ViewItem.VIEW_TYPE_GRID, new MokoViewNodeRoot());
		moko.setViewItemImageResId(R.drawable.moko);
		
		ViewItem flickr = new ViewItem("Flickr", "flickr", FlickrViewNode.FLICKR_ICON, ViewItem.VIEW_TYPE_GRID, new FlickrViewNodeRoot());
		flickr.setViewItemImageResId(R.drawable.flickr);
		
//		ViewItem foto = new ViewItem("Fotopedia", "fotopedia", FotoViewNode.FOTO_ICON, ViewItem.VIEW_TYPE_LIST, new FotoViewNodeRoot());
		
		ViewItem ng = new ViewItem("National Geographic", "nationalgeographic", NGViewNode.NG_ICON, ViewItem.VIEW_TYPE_GRID, new NGViewNodeRoot());
		ng.setViewItemImageResId(R.drawable.ngraphic);
		
		this.viewItems = Arrays.asList(
				new ViewItem("Stream", "stream", null, ViewItem.VIEW_TYPE_GRID, new StreamViewNodeRoot()),	
				new ViewItem("Feeds",  "feeds", null, ViewItem.VIEW_TYPE_GRID, new HeartsViewNodeRoot()),
				new ViewItem("Hearts", "hearts", null, ViewItem.VIEW_TYPE_GRID, new HeartsViewNodeRoot()),
				new ViewItem("Stars",  "stars", null, ViewItem.VIEW_TYPE_GRID, new HeartsViewNodeRoot()),
				new ViewItem("Gallery", "gallery", null, ViewItem.VIEW_TYPE_GRID, new ViewNode("", Arrays.asList(
						moko, flickr, ng)))
				);
	}
}
