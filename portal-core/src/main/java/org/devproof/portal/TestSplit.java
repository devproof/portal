package org.devproof.portal;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 
 */

/**
 * @author Carsten
 * 
 */
public class TestSplit {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// String a = "cdcdjlö";
		// a page-break-after utrurztutr</p> ds page-break-after<p> &nbsp;</p>
		// <p> ghjghjkghkkdghdgk page-break-after eewzew</p>
		String a = "	a  page-break-after utrurztutr</p> ds page-break-after<p>	&nbsp;</p><div style=\"page-break-after: always;\">	<span style=\"display: none;\">&nbsp;</span></div><p>	ghjghjkghkkdghdgk page-break-after eewzew</p>";
		String[] splitted = StringUtils.splitByWholeSeparator(a, "page-break-after");
		List<String> result = new ArrayList<String>();
		if (splitted.length > 1) {
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < splitted.length; i++) {
				String actual = splitted[i];
				int open = actual.lastIndexOf('<');
				int close = actual.lastIndexOf('>');
				if (open < 0 || close > open) {
					// kein tag
					buf.append(actual);
					if (splitted.length - 1 != i)
						buf.append("page-break-after");
				} else {
					// tag
					buf.append(StringUtils.substringBeforeLast(actual, "<"));
					result.add(buf.toString());
					buf = new StringBuilder();
					String closeTag = StringUtils.substringAfterLast(actual, "<");
					closeTag = "</" + StringUtils.substringBefore(closeTag, " ") + ">";
					splitted[i + 1] = StringUtils.substringAfter(splitted[i + 1], closeTag);
				}
			}
			if (buf.length() > 0) {
				result.add(buf.toString());
			}
		} else {
			result.add(a);
		}
		for (String r : result) {
			System.out.println(r);
		}
	}
}
