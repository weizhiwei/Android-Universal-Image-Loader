package com.wzw.ic.mvc.flickr;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.wzw.ic.mvc.ViewItem;

import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlickrViewNodeStream extends FlickrViewNodeInterestingness {

    private static final int MAX_PAGES = 20;
    private Integer[] randomPageNumbers = new Integer[MAX_PAGES];

    public FlickrViewNodeStream() {
        for (int i = 0; i < MAX_PAGES; ++i) {
            randomPageNumbers[i] = i+1;
        }
        Collections.shuffle(Arrays.asList(randomPageNumbers));
    }

    @Override
    protected int perturbPageNo(int pageNo, boolean reload) {
        if (reload) {
            Collections.shuffle(Arrays.asList(randomPageNumbers));
            return randomPageNumbers[0];
        } else {
            if (pageNo > MAX_PAGES) {
                return pageNo;
            }
            return randomPageNumbers[pageNo - 1];
        }
    }
}
