package com.wzw.ic.mvc.root;

import java.util.Arrays;
import java.util.List;

import com.nostra13.example.universalimageloader.R;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.ViewNodeRoot;
import com.wzw.ic.mvc.flickr.FlickrViewNode;
import com.wzw.ic.mvc.flickr.FlickrViewNodePeopleGalleries;
import com.wzw.ic.mvc.flickr.FlickrViewNodeRoot;
import com.wzw.ic.mvc.flickr.FlickrViewNodeStream;
import com.wzw.ic.mvc.moko.MokoViewNode;
import com.wzw.ic.mvc.moko.MokoViewNodeRoot;
import com.wzw.ic.mvc.nationalgeographic.NGViewNode;
import com.wzw.ic.mvc.nationalgeographic.NGViewNodePhotoOfTheDay;
import com.wzw.ic.mvc.nationalgeographic.NGViewNodeRoot;

public class RootViewNode extends ViewNode {

	private static RootViewNode theRootNode = new RootViewNode();
	
	private ViewItem gallery;
	
	protected RootViewNode() {
		
		super("root", null);
		
		ViewItem moko = new ViewItem("MOKO!", MokoViewNode.MOKO_NAME, null, ViewItem.VIEW_TYPE_GRID, new MokoViewNodeRoot());
		moko.setViewItemType(ViewItem.VIEW_ITEM_TYPE_IMAGE_RES);
		moko.setViewItemImageResId(R.drawable.moko);
		
		ViewItem flickr = new ViewItem("Flickr", FlickrViewNode.FLICKR_NAME, null, ViewItem.VIEW_TYPE_GRID, new FlickrViewNodeRoot());
		flickr.setViewItemType(ViewItem.VIEW_ITEM_TYPE_IMAGE_RES);
		flickr.setViewItemImageResId(R.drawable.flickr);
		
//		ViewItem foto = new ViewItem("Fotopedia", "fotopedia", FotoViewNode.FOTO_ICON, ViewItem.VIEW_TYPE_LIST, new FotoViewNodeRoot());
		
		ViewItem ng = new ViewItem("National Geographic", NGViewNode.NG_NAME, null, ViewItem.VIEW_TYPE_GRID, new NGViewNodeRoot());
		ng.setViewItemType(ViewItem.VIEW_ITEM_TYPE_IMAGE_RES);
		ng.setViewItemImageResId(R.drawable.ngraphic);
		
		gallery = new ViewItem("Gallery", "gallery", null, ViewItem.VIEW_TYPE_GRID, new ViewNode("", Arrays.asList(
//				moko,
              flickr,
              ng
                )));
		gallery.setViewItemImageResId(R.drawable.ic_gallery);

        ViewItem stream = new ViewItem("Interestingness", "stream", null, ViewItem.VIEW_TYPE_CARD_LIST, new StreamViewNode2(gallery));
        stream.setViewItemImageResId(R.drawable.ic_pictures);


        ViewItem albums = new ViewItem("Albums", "stream", null, ViewItem.VIEW_TYPE_CARD_LIST, new StreamViewNode2(
                new ViewItem(null, null, null, 0, new ViewNode("", Arrays.asList(new ViewItem(null, null, null, 0, new TempViewNode(null)
        ))))));

        ViewItem feeds = new ViewItem("Following", "feeds", null, ViewItem.VIEW_TYPE_CARD_LIST, new FeedsViewNode());
		feeds.setViewItemImageResId(R.drawable.ic_user);

        ViewItem stories = new ViewItem("Stories", "stories", null, ViewItem.VIEW_TYPE_STORY_LIST, new NGViewNodePhotoOfTheDay());

        this.viewItems = Arrays.asList(gallery, stream, albums, feeds, stories);
	}
	
	public static RootViewNode getInstance() {
		return theRootNode;
	}
	
	public ViewItem findGalleryViewItem(String name) {
		for (ViewItem viewItem: gallery.getViewNode().getViewItems()) {
			if (viewItem.getNodeUrl().equals(name)) {
				return viewItem;
			}
		}
		return null;
	}
	
	public ViewItem getGalleryViewItem() {
		return gallery;
	}
}

class TempViewNode extends ViewNode implements ViewNodeRoot {

    public TempViewNode(String sourceUrl) {
        super(sourceUrl);
    }

    @Override
    public List<ViewItem> getStream() {
        return Arrays.asList(
                new ViewItem("Albums", "https://www.flickr.com/photos/66956608@N06/galleries/", null, ViewItem.VIEW_TYPE_CARD_LIST, new FlickrViewNodePeopleGalleries("66956608@N06"))
        );
    }
}