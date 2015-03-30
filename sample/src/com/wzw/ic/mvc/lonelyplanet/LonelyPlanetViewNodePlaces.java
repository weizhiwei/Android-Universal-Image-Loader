package com.wzw.ic.mvc.lonelyplanet;

import com.wzw.ic.mvc.ViewItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LonelyPlanetViewNodePlaces extends LonelyPlanetViewNodeSights {

	public LonelyPlanetViewNodePlaces(String sourceUrl) {
		super(sourceUrl);
        supportPaging = true;
	}
}
