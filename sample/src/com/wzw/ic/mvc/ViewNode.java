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
    public static final int VIEW_TYPE_SIMPLE = 4;
    public static final int VIEW_TYPE_COUNT = 5;

    public static final int VIEW_ITEM_TYPE_COLOR = 1;
    public static final int VIEW_ITEM_TYPE_IMAGE_RES = 2;
    public static final int VIEW_ITEM_TYPE_IMAGE_URL = 3;

    public static interface Callback<T> {
        public void onSuccess(T result);
        public void onFailure(int errCode, String errMsg);
    }

    public static class WrapperViewHolder {
        public View wrapperView;

        public FrameLayout body;

        public TextView textView;
        public ImageView imageView;

        public ImageView button1, button2, button3;

        public WrapperViewHolder(View wrapperView) {
            this.wrapperView = wrapperView;

            body = (FrameLayout)wrapperView.findViewById(R.id.body);

            textView = (TextView)wrapperView.findViewById(R.id.text);
            imageView = (ImageView)wrapperView.findViewById(R.id.image);

            button1 = (ImageView)wrapperView.findViewById(R.id.button1);
            button2 = (ImageView)wrapperView.findViewById(R.id.button2);
            button3 = (ImageView)wrapperView.findViewById(R.id.button3);
        }
    }

    // graph
    protected ViewNode parent;
    protected ViewNode author;

    protected boolean isDetached = false;
	
	public ViewNode(ViewNode parent) {
        this.parent = parent;
	}

    public ViewNode getParent() { return parent; }

    public boolean supportReloading() {
		return false;
	}
	
	public void load(int page, Callback<List<ViewNode>> callback) {
	}
	
	public boolean supportPaging() {
		return false;
	}

    public int getWrapperViewResId() {
        return 0;
    }

    public WrapperViewHolder createWrapperView(View headerView) {
        return new WrapperViewHolder(headerView);
    }

    public void updateWrapperView(WrapperViewHolder holder) {
        holder.textView.setVisibility(View.INVISIBLE);
        String authorName = (author == null ? null : author.getTitle());
        if (!TextUtils.isEmpty(authorName)) {
            int postCount = 3;
            String posted = TextUtils.isEmpty(title) ?
                    String.format("%d %s", postCount, postCount > 1 ? "pictures" : "picture") :
                    String.format("<u>%s</u> (%dP)", title, postCount);
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

        holder.button1.setImageResource(android.R.drawable.btn_star);
        holder.button2.setImageResource(android.R.drawable.ic_menu_share);
        holder.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.button3.setVisibility(View.GONE);
    }

    protected String title;
    protected String imageUrl;
    protected boolean showingLabelInGrid;
    protected int viewItemType = VIEW_ITEM_TYPE_IMAGE_URL;
    protected int viewItemColor;
    protected int viewItemImageResId;
    protected String story;
    protected boolean heartsOn;
    protected String webPageUrl;
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

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }
}
