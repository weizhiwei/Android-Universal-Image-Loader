package com.wzw.ic.mvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.nostra13.example.universalimageloader.MyVolley;
import com.nostra13.example.universalimageloader.R;

public class ViewNode extends IcObject {

    public static final int VIEW_TYPE_PAGER = 0;
    public static final int VIEW_TYPE_LIST = 1;
    public static final int VIEW_TYPE_GRID = 2;
    public static final int VIEW_TYPE_IMAGE = 3;
    public static final int VIEW_TYPE_WEBVIEW = 4;
    public static final int VIEW_TYPE_SIMPLE = 5;
    public static final int VIEW_TYPE_TILE = 6;
    public static final int VIEW_TYPE_COUNT = 7;

    public static final int VIEW_ITEM_TYPE_COLOR = 1;
    public static final int VIEW_ITEM_TYPE_IMAGE_RES = 2;
    public static final int VIEW_ITEM_TYPE_IMAGE_URL = 3;

    public static interface LoadListener {
        public void onLoadDone(ViewNode model);
    }

    public static class WrapperViewHolder {
        public View wrapperView;

        public FrameLayout body;

        public TextView textView;
        public ImageView imageView;

        public WrapperViewHolder(View wrapperView) {
            this.wrapperView = wrapperView;

            body = (FrameLayout)wrapperView.findViewById(R.id.body);

            textView = (TextView)wrapperView.findViewById(R.id.text);
            imageView = (ImageView)wrapperView.findViewById(R.id.image);
        }
    }

    protected ViewNode parent;
	protected List<ViewNode> children, viewItemsCopy;
	protected List<ViewNodeAction> actions, actionsCopy;

    protected boolean isDetached = false;
	
	public ViewNode(ViewNode parent) {
        this.parent = parent;
		this.children = new ArrayList<ViewNode>();
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

    public void updateWrapperView(WrapperViewHolder holder) {
        holder.textView.setVisibility(View.INVISIBLE);
        String authorName = (author == null ? null : author.getTitle());
        if (!TextUtils.isEmpty(authorName)) {
            String posted = TextUtils.isEmpty(title) ?
                    String.format("%d %s", children.size(), children.size() > 1 ? "pictures" : "picture") :
                    String.format("%s (%dP)", title, children.size());
            String caption = String.format(
                    "<b>%s</b> posted %s", authorName, posted);
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(new SpannableString(Html.fromHtml(caption)));
        }

        holder.imageView.setVisibility(View.INVISIBLE);
        if (null != author) {
            if (!TextUtils.isEmpty(author.getImageUrl())) {
                holder.imageView.setVisibility(View.VISIBLE);
                MyVolley.getImageLoader().get(author.getImageUrl(),
                        ImageLoader.getImageListener(holder.imageView,
                                R.drawable.ic_stub,
                                R.drawable.ic_error));
            }
        }
    }

    protected String title;
    protected String imageUrl;
    protected boolean showingLabelInGrid;
    protected int viewItemType = VIEW_ITEM_TYPE_IMAGE_URL;
    protected int viewItemColor;
    protected int viewItemImageResId;
    protected String story;
    protected boolean heartsOn;
    protected ViewNode author;
    protected String webPageUrl;
    protected int initialZoomLevel;
    protected Date postedDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getViewType(int container) {
        switch (container) {
            case VIEW_TYPE_PAGER:
                return VIEW_TYPE_LIST;
            case VIEW_TYPE_LIST:
                return VIEW_TYPE_SIMPLE;
        }
        return VIEW_TYPE_SIMPLE;
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
