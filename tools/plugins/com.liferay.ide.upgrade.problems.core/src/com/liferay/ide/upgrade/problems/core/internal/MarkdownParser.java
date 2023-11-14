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

package com.liferay.ide.upgrade.problems.core.internal;

import com.liferay.knowledge.base.markdown.converter.MarkdownConverter;
import com.liferay.knowledge.base.markdown.converter.factory.MarkdownConverterFactoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
 * @author Simon Jiang
 */
public class MarkdownParser {

	public static String getSection(String fileName, String sectionKey) {
		String retval = "";

		if (sectionKey.equals("#legacy")) {
			retval = "#legacy";
		}
		else {
			try {
				Map<String, String> sections = parse(fileName);

				if (sections != null) {
					retval = sections.get(sectionKey);
				}
			}
			catch (IOException ioe) {
			}
		}

		return retval;
	}

	public static Map<String, String> parse(String fileName) throws IOException {
		Map<String, String> retval = _markdowns.get(fileName);

		if (retval == null) {
			String markdown = _readStreamToString(MarkdownParser.class.getResourceAsStream(fileName), true);

			//MarkdownConverter markdownConverter = MarkdownConverterFactoryUtil.create();

			//String html = markdownConverter.convert(markdown);
			
			String html = "";

			Map<String, String> sections = _parseHtml(html);

			_markdowns.put(fileName, sections);

			retval = sections;
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

				String text = sibling.toString();

				if ((text != null) && text.startsWith("<hr")) {
					break;
				}

				interesting.add(sibling);
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

	private static String _readStreamToString(InputStream contents, boolean closeStream) throws IOException {
		if (contents == null) {
			return null;
		}

		char[] buffer = new char[0x10000];

		StringBuilder out = new StringBuilder();

		try (Reader in = new InputStreamReader(contents, "UTF-8")) {
			int read;

			do {
				read = in.read(buffer, 0, buffer.length);

				if (read > 0) {
					out.append(buffer, 0, read);
				}
			}
			while (read >= 0);
		}

		if (closeStream) {
			contents.close();
		}

		return out.toString();
	}

	private static final Map<String, Map<String, String>> _markdowns = new HashMap<>();

}