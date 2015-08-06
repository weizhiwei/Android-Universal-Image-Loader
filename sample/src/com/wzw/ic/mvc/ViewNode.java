package com.wzw.ic.mvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.example.universalimageloader.R;

public class ViewNode extends IcObject {

    public static final int VIEW_TYPE_LIST_SIMPLE = 0;
    public static final int VIEW_TYPE_LIST_TILES = 1;
    public static final int VIEW_TYPE_LIST_COUNT = 2;
    public static final int VIEW_TYPE_GRID = 10;
    public static final int VIEW_TYPE_IMAGE_PAGER = 20;
    public static final int VIEW_TYPE_WEBVIEW = 30;
    public static final int VIEW_ITEM_TYPE_COLOR = 1;
    public static final int VIEW_ITEM_TYPE_IMAGE_RES = 2;
    public static final int VIEW_ITEM_TYPE_IMAGE_URL = 3;

    public static interface LoadListener {
        public void onLoadDone(ViewNode model);
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

    protected ViewNode parent;
	protected List<ViewNode> children, viewItemsCopy;
	protected List<ViewNodeAction> actions, actionsCopy;

    protected boolean isDetached = false;
	
	public ViewNode(String sourceUrl) {
		this.sourceUrl = sourceUrl;
		this.children = new ArrayList<ViewNode>();
		this.actions = new ArrayList<ViewNodeAction>();
	}

	public ViewNode(String sourceUrl, List<ViewNode> children) {
		this.sourceUrl = sourceUrl;
		this.children = children;
		this.actions = new ArrayList<ViewNodeAction>();
	}
	
	public void detach() {
		viewItemsCopy = new ArrayList<ViewNode>(children.size());
		viewItemsCopy.addAll(children);
		actionsCopy = new ArrayList<ViewNodeAction>(actions.size());
		actionsCopy.addAll(actions);
        isDetached = true;
	}

	public void attach() {
		isDetached = false;
		viewItemsCopy = null;
		actionsCopy = null;
	}

    public ViewNode getParent() { return parent; }

    public int getSiblingCount() {
        return null == parent ? 0 : parent.getChildren().size();
    }

    public ViewNode getSibling(int i) {
        return parent.getChildren().get(i);
    }

	public List<ViewNode> getChildren() {
		return isDetached ? viewItemsCopy : children;
	}

	public List<ViewNodeAction> getActions() {
		return isDetached ? actionsCopy : actions;
	}

    public boolean supportReloading() {
		return false;
	}
	
	public void load(boolean reload, LoadListener loadListener) {
	}
	
	public boolean supportPaging() {
		return false;
	}

	public Object onAction(ViewNodeAction action) {
		return null;
	}

    public int getWrapperViewResId() {
        return R.layout.wrapper;
    }

    public WrapperViewHolder createWrapperView(View headerView) {
        return new WrapperViewHolder(headerView);
    }
	
	public void updateWrapperView(View headerView, WrapperViewHolder holder, int position) {
	}

    private String title;
    private String nodeUrl;
    private String imageUrl;
    private boolean showingLabelInGrid;
    private int viewItemType;
    private int viewItemColor;
    private int viewItemImageResId;
    private String story;
    private boolean heartsOn;
    private ViewNode author;
    private String webPageUrl;
    private int initialZoomLevel;
    private Date postedDate;

    private int viewType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isShowingLabelInGrid() {
        return showingLabelInGrid;
    }

    public void setShowingLabelInGrid(boolean showingLabelInGrid) {
        this.showingLabelInGrid = showingLabelInGrid;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public boolean isHeartsOn() {
        return heartsOn;
    }

    public void setHeartsOn(boolean heartsOn) {
        this.heartsOn = heartsOn;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public ViewNode getAuthor() {
        return author;
    }

    public void setAuthor(ViewNode author) {
        this.author = author;
    }

    public String getWebPageUrl() {
        return webPageUrl;
    }

    public void setWebPageUrl(String webPageUrl) {
        this.webPageUrl = webPageUrl;
    }

    public int getViewItemType() {
        return viewItemType;
    }

    public void setViewItemType(int viewItemType) {
        this.viewItemType = viewItemType;
    }

    public int getViewItemColor() {
        return viewItemColor;
    }

    public void setViewItemColor(int viewItemColor) {
        this.viewItemColor = viewItemColor;
    }

    public int getViewItemImageResId() {
        return viewItemImageResId;
    }

    public void setViewItemImageResId(int viewItemImageResId) {
        this.viewItemImageResId = viewItemImageResId;
    }

    public int getInitialZoomLevel() {
        return initialZoomLevel;
    }

    public void setInitialZoomLevel(int initialZoomLevel) {
        this.initialZoomLevel = initialZoomLevel;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }
}
