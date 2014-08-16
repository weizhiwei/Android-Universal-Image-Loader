package com.wzw.ic.mvc.moko;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.example.universalimageloader.ImageGridActivity;
import com.nostra13.example.universalimageloader.ImagePagerActivity;
import com.nostra13.example.universalimageloader.R;
import com.wzw.ic.mvc.BaseController;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.ViewNodeAction;

public class MokoController extends BaseController {
	
	public static String MOKO_ICON = "http://www.vitbbs.cn/uploads/allimg/c101125/12ZEK63410-14106.gif";
	
	@Override
	public void startItemView(Activity parentActivity, ViewNode node, int position) {
		Intent intent = null;
		ViewNode newNode = null;
		
		String nodeUrl = node.getSourceUrl();
		ViewItem viewItem = node.getViewItems().get(position);
		String itemUrl = viewItem.getNodeUrl();
		
		if (node instanceof MokoViewNodeRoot) {
			intent = new Intent(parentActivity, ImageGridActivity.class);
			newNode = new MokoViewNodeChannel(itemUrl);
			
		} else if (node instanceof MokoViewNodeChannel) {
			intent = new Intent(parentActivity, ImageGridActivity.class);
			newNode = new MokoViewNodePost(itemUrl);
			
		} else if (node instanceof MokoViewNodeUser) { // user page
			intent = new Intent(parentActivity, ImageGridActivity.class);
			newNode = new MokoViewNodePost(itemUrl);
			
		} else if (node instanceof MokoViewNodePost) {
			intent = new Intent(parentActivity, ImagePagerActivity.class);
			newNode = node;
			intent.putExtra(Extra.IMAGE_POSITION, position);
		}
		
		intent.putExtra(Extra.MODEL, newNode);
		intent.putExtra(Extra.CONTROLLER, this);
		intent.putExtra(Extra.VIEW_ITEM, viewItem);

		parentActivity.startActivity(intent);
	}

	@Override
	public void startAction(Activity parentActivity, ViewNode node, ViewNodeAction action) {
		switch (action.getId()) {
		case R.id.action_moko_see_user:
			// user
			MokoViewNodePost nodePost = (MokoViewNodePost)node;
			ViewItem user = nodePost.getUserViewItem();
			if (null != user) {
				Intent intent = new Intent(parentActivity, ImageGridActivity.class);
				intent.putExtra(Extra.MODEL, new MokoViewNodeUser(user.getNodeUrl()));
				intent.putExtra(Extra.CONTROLLER, this);
				intent.putExtra(Extra.VIEW_ITEM, user);
				parentActivity.startActivity(intent);
			}
			break;
		default:
			break;
		}
	}
}
