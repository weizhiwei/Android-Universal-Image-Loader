package com.wzw.ic.mvc.moko;

import java.util.Arrays;
import java.util.Collections;

public class MokoViewNodeStream extends MokoViewNodeChannel {

    private static final int MAX_PAGES = 20;
    private Integer[] randomPageNumbers = new Integer[MAX_PAGES];

	public MokoViewNodeStream() {
		super(URL_PREFIX + "/moko/post/%d.html");
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
