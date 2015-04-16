package com.wzw.ic.mvc.lonelyplanet;

import com.wzw.ic.mvc.ViewItem;

import java.util.List;
import java.util.Random;

/**
 * Created by zhiweiwei on 4/10/15.
 */
public class LonelyPlanetViewNodeRandomSights extends LonelyPlanetViewNodeSights {

    public LonelyPlanetViewNodeRandomSights(String sourceUrl) {
        super(sourceUrl);
        supportPaging = false;
    }

    @Override
    public List<ViewItem> reload()  {
        List<ViewItem> page = null;
        pageNo = (new Random()).nextInt(20);
        while ((null == page || page.size() < 5) &&
                pageNo > 1) {
            pageNo = Math.max(1, pageNo >> 1);
            page = loadOneMorePage(); // for pageNo to take effect
        }

        if (null != page && page.size() > 0) {
            viewItems.clear();
            viewItems.addAll(page);
        }

        return page;
    }
}
