package com.zoco.obj;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/*
 * <rss version="2.0">
<channel>
<title>Naver Open API - book_adv ::'all'</title>
<link>http://search.naver.com</link>
<description>Naver Search Result</description>
<lastBuildDate>Sun, 25 Jan 2015 20:07:11 +0900</lastBuildDate>
<total>1</total>
<start>1</start>
<display>1</display>
<item>
<title>알고리즘 문제 해결 전략 세트 (프로그래밍 대회에서 배우는)</title>
<link>
http://openapi.naver.com/l?AAAC2NywqCQBhGn8ZZytzUaTGLpCwJol1Lmcs/KWpjMkm+fRMJ3+LwweG83jCvUg0DCusEUnvfN8ouqIdVgiAsN7oAyJ3iGcFAubFCCWIcznJA7QxOtiFMCdsntIr7+elTLTCnxo/bYfUGjYWguiGd2mhUurMJOxQ4E0XOUZCEU0oEx4zsMEOjPLrqdN5fb58g7pbVtU9oeYmR8vFv9RZH/wswyr5qwQAAAA==
</link>
<image>
http://bookthumb.phinf.naver.net/cover/070/587/07058764.jpg?type=m1&udate=20121129
</image>
<author>구종만</author>
<price>50000</price>
<discount>45000</discount>
<publisher>인사이트</publisher>
<pubdate>20121101</pubdate>
<isbn>8966260543 9788966260546</isbn>
<description>
프로그래밍 대회에서 배우는『알고리즘 문제 해결 전략 세트』. 프로그래밍 대회 문제를 풀면서 각종 알고리즘 설계 기법과 자료 구조에 대해 배우고, 나아가 문제 해결 능력까지 키울 수 있도록 구성된 책이다. 각 장에는 독자가 스스로 프로그램을 작성해서 채점받을 수 있는 연습 문제들을 수록하였고, 모든...
</description>
</item>
</channel>
</rss>
 */

//이름을 따로 지정할 수 있음 rss로 지정한 것
@Root(name="rss")
public class Book {
	@Element
	public Channel channel;
	@Attribute
	public String version;
	public Book() {
		// TODO Auto-generated constructor stub
	}
	
	public Item getBookContent() {
		return channel.item;
	}
	
	
}
