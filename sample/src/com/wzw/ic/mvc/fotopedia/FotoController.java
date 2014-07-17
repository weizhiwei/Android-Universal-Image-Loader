package com.wzw.ic.mvc.fotopedia;

import android.app.Activity;
import android.content.Intent;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.example.universalimageloader.ImageListActivity;
import com.wzw.ic.mvc.BaseController;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public class FotoController extends BaseController {
	public static String FOTO_ICON = "http://images.cdn.fotopedia.com/fotopedia-af3MHAL54rw-original.png";

	@Override
	public void startItemView(Activity parentActivity, ViewNode node, int index) {
		Intent intent = null;
		ViewNode newNode = null;
		
		String nodeUrl = node.getSourceUrl();
		ViewItem viewItem = node.getViewItems().get(index);
		String itemUrl = viewItem.getNodeUrl();
		
		// magazine
		if (itemUrl.equals("magazine")) {
			intent = new Intent(parentActivity, ImageListActivity.class);
			newNode = new FotoViewNodeMagazine();
		}
		
		intent.putExtra(Extra.MODEL, newNode);
		intent.putExtra(Extra.CONTROLLER, this);
		parentActivity.startActivity(intent);
	}
}
