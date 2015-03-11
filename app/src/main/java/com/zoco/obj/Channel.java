package com.zoco.obj;

import org.simpleframework.xml.Element;

/*
 * <channel>
<title>Naver Open API - book_adv ::'all'</title>
<link>http://search.naver.com</link>
<description>Naver Search Result</description>
<lastBuildDate>Sun, 25 Jan 2015 20:07:11 +0900</lastBuildDate>
<total>1</total>
<start>1</start>
<display>1</display>
	
</channel>
 */
public class Channel {
	@Element
	String title;
	@Element
	String link;
	@Element
	String description;
	@Element
	String lastBuildDate;
	@Element
	int total;
	@Element
	int start;
	@Element
	int display;
	@Element
	public Item item;
	
	public Channel() {
		// TODO Auto-generated constructor stub
	}
}
