package com.wzw.ic.mvc.lonelyplanet;

import android.util.Log;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

import java.util.List;

/**
 * Created by zhiweiwei on 4/16/15.
 */
public class LonelyPlanetDumper {
    public static void dump() {
//        ViewNode viewNode = new LonelyPlanetViewNodeSights("http://www.lonelyplanet.com/china/sights.html?page=%d");
        ViewNode viewNode = new LonelyPlanetViewNodePlaces("http://www.lonelyplanet.com/china/places.html?page=%d");
        List<ViewItem> page = viewNode.reload();
        while (null != page && !page.isEmpty()) {
            for (ViewItem item: page) {
                Log.i(LonelyPlanetDumper.class.getSimpleName(), item.getLabel());
            }
            page = viewNode.loadOneMorePage();
        }
    }
}
