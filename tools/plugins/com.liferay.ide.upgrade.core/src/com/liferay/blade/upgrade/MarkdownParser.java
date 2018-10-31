/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.blade.upgrade;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.knowledge.base.markdown.converter.MarkdownConverter;
import com.liferay.knowledge.base.markdown.converter.factory.MarkdownConverterFactoryUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 * @author Gregory Amerson
 */
public class MarkdownParser {

	public static String getSection(String fileName, String sectionKey) {
		String retval = null;

		if (sectionKey.equals("#legacy")) {
			retval = "#legacy";
		}
		else {
			Map<String, String> sections = parse(fileName);

			if (sections != null) {
				retval = sections.get(sectionKey);
			}
		}

		return retval;
	}

	public static Map<String, String> parse(String fileName) {
		Map<String, String> retval = _markdowns.get(fileName);

		if (retval == null) {
			try {
				String markdown = CoreUtil.readStreamToString(MarkdownParser.class.getResourceAsStream(fileName));

				MarkdownConverter markdownConverter = MarkdownConverterFactoryUtil.create();

				String html = markdownConverter.convert(markdown);

				Map<String, String> sections = _parseHtml(html);

				_markdowns.put(fileName, sections);

				retval = sections;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return retval;
	}

	private static Map<String, String> _parseHtml(String html) {
		Map<String, String> retval = new HashMap<>();

		Document document = Jsoup.parse(html);

		Elements elements = document.select("a[href] > h3");

		for (Element h3 : elements) {
			Element a = h3.parent();

			int index = a.siblingIndex();
			List<Node> siblings = a.siblingNodes();

			StringBuilder sb = new StringBuilder();

			List<Node> interesting = new ArrayList<>();

			for (int i = index; i < siblings.size(); i++) {
				Node sibling = siblings.get(i);

				if (StringUtil.startsWith(sibling.toString(), "<hr")) {
					break;
				}
				else {
					interesting.add(sibling);
				}
			}

			for (Node node : interesting) {
				sb.append(node.toString());
			}

			String href = a.attr("href");

			String content = sb.toString();
			String idReg = "\\[\\]\\(id=[^\\s]+?\\)";
			String replace = "";
			Pattern idPattern = Pattern.compile(idReg);

			Matcher idMatcher = idPattern.matcher(content);

			content = idMatcher.replaceAll(replace);

			String hyperLinkReg = "<a\\s+href\\s*=\\s*\"(http.+?)\"\\s*>.+?</a>";

			Pattern hyperLinkPattern = Pattern.compile(hyperLinkReg);

			Matcher hyperLinkMatcher = hyperLinkPattern.matcher(content);

			while (hyperLinkMatcher.find()) {
				content = content.replace(hyperLinkMatcher.group(), hyperLinkMatcher.group(1));
			}

			retval.put(href, content);
		}

		return retval;
	}

	private static final Map<String, Map<String, String>> _markdowns = new HashMap<>();

}