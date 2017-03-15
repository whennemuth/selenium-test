package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Calls to SearchContext.find(By) and subsequent processing of WebElement instances is expensive in
 * terms of time. There is no need to repeat a search if an identical search has already been made.
 * To avoid this, search results are stored in this cache which is always checked first if another search
 * is about to be run via xpath. A match is found in the cache if the SearchContext, xpath and frame 
 * presense are all equal.
 * 
 * @author wrh
 *
 */
public class XpathElementCache {
	
	private static final Map<XpathElementCache.XpathAndContext, List<WebElement>> cache = 
			new HashMap<XpathElementCache.XpathAndContext, List<WebElement>>();
	
	public static void put(SearchContext searchContext, String xpath, boolean frame, List<WebElement> webElements) {
		cache.put(new XpathAndContext(searchContext, xpath, frame), new ArrayList<WebElement>(webElements));
	}
	
	public static List<WebElement> get(SearchContext searchContext, String xpath, boolean frame) {
		List<WebElement> cached = cache.get(new XpathAndContext(searchContext, xpath, frame));
		if(cached == null)
			return new ArrayList<WebElement>();
		return cached;
	}
	
	public static void clear() {
		cache.clear();
	}
	
	public static String cacheToString() {
		StringBuilder s = new StringBuilder();
		for(XpathAndContext xctx : cache.keySet()) {
			s.append(xctx);
		}
		return s.toString();
	}
	
	private static class XpathAndContext {
		private SearchContext searchContext;
		private String xpath;
		private boolean frame;
		
		public XpathAndContext(SearchContext searchContext, String xpath, boolean frame) {
			this.searchContext = searchContext;
			this.xpath = xpath;
			this.frame = frame;
		}
		public SearchContext getSearchContext() {
			return searchContext;
		}
		public String getXpath() {
			return xpath;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof XpathAndContext))
				return false;
			XpathAndContext other = (XpathAndContext) obj;
			if (frame != other.frame)
				return false;
			if (xpath == null || other.xpath == null) {
				return false;
			} else if (!xpath.equals(other.xpath))
				return false;
			if (searchContext == null || other.searchContext == null) {
				return false;
			} else if (!sameSearchContexts(searchContext, other.searchContext))
				return false;
			return true;
		}
		private boolean sameSearchContexts(SearchContext sc1, SearchContext sc2) {
			if(sc1 instanceof WebDriver && sc1 instanceof WebDriver) {
				return true; // Not accounting for frames yet.				
			}
			if(sc1 instanceof WebElement && sc2 instanceof WebElement) {
				return ((WebElement)sc1).equals((WebElement)sc2);
			}
			return false;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("XpathAndContext [\n\tsearchContext=").append(searchContext).append(", \n\txpath=").append(xpath)
					.append(", \n\tframe=").append(frame).append("]\n");
			return builder.toString();
		}
	}
}
