package com.wzw.ic.mvc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.example.universalimageloader.R;

public class ViewNode extends IcObject {

    public static interface LoadListener {
        public void onLoadDone(ViewNode model);
    }

    public static interface ViewItemActivityStarter {
        public void startViewItemActivity(ViewNode parent, ViewItem viewItem);
    }

    public static class WrapperViewHolder {
        public View wrapperView;

        public FrameLayout placeholder;

        public TextView textView;
        public ImageView imageView;

        public WrapperViewHolder(View wrapperView) {
            this.wrapperView = wrapperView;

            placeholder = (FrameLayout)wrapperView.findViewById(R.id.placeholder);

            textView = (TextView)wrapperView.findViewById(R.id.text);
            imageView = (ImageView)wrapperView.findViewById(R.id.image);
        }
    }

	protected String sourceUrl;
	
	protected List<ViewItem> viewItems, viewItemsCopy;
	protected List<ViewNodeAction> actions, actionsCopy;

    protected boolean isDetached = false;
	
	public ViewNode(String sourceUrl) {
		this.sourceUrl = sourceUrl;
		this.viewItems = new ArrayList<ViewItem>();
		this.actions = new ArrayList<ViewNodeAction>();
	}

	public ViewNode(String sourceUrl, List<ViewItem> viewItems) {
		this.sourceUrl = sourceUrl;
		this.viewItems = viewItems;
		this.actions = new ArrayList<ViewNodeAction>();
	}
	
	public void detach() {
		viewItemsCopy = new ArrayList<ViewItem>(viewItems.size());
		viewItemsCopy.addAll(viewItems);
		actionsCopy = new ArrayList<ViewNodeAction>(actions.size());
		actionsCopy.addAll(actions);
        isDetached = true;
	}

	public void attach() {
		isDetached = false;
		viewItemsCopy = null;
		actionsCopy = null;
	}
	
	public String getSourceUrl() {
		return sourceUrl;
	}
	
	public List<ViewItem> getViewItems() {
		return isDetached ? viewItemsCopy : viewItems;
	}

	public List<ViewNodeAction> getActions() {
		return isDetached ? actionsCopy : actions;
	}

    public boolean supportReloading() {
		return false;
	}
	
	public List<ViewItem> load(boolean reload, LoadListener loadListener) {
		return null;
	}
	
	public boolean supportPaging() {
		return false;
	}

	public Object onAction(ViewNodeAction action) {
		return null;
	}

    public int getWrapperViewResId(int position) {
        return R.layout.wrapper;
    }

    public WrapperViewHolder createHolderFromWrapperView(View headerView) {
        return new WrapperViewHolder(headerView);
    }
	
	public void updateWrapperView(View headerView, WrapperViewHolder holder, int position) {
	}
	
	public void onViewItemClicked(ViewItem viewItem, ViewItemActivityStarter starter) {
		starter.startViewItemActivity(this, viewItem);
	}
}
