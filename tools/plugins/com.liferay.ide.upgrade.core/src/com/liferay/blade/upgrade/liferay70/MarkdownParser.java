/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.upgrade.liferay70;

import com.liferay.knowledge.base.markdown.converter.factory.MarkdownConverterFactoryUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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


public class MarkdownParser {

	private final static Map<String, Map<String, String>> _markdowns = new HashMap<>();

	public static Map<String, String> parse(String fileName) {
		Map<String, String> retval = _markdowns.get(fileName);

		if (retval == null) {
			try {
				final String markdown = new String(Files.readAllBytes(Paths.get(fileName)));
				final String html = MarkdownConverterFactoryUtil.create().convert(markdown);

				Map<String, String> sections = parseHtml(html);

				_markdowns.put(fileName, sections);

				retval = sections;
			} catch (IOException e) {
			}
		}

		return retval;
	}

	private static Map<String, String> parseHtml(String html) {
		Map<String, String> retval = new HashMap<>();

		Document document = Jsoup.parse(html);
		Elements elements = document.select("a[href] > h3");

		for (Element h3 : elements)  {
			Element a = h3.parent();
			int index = a.siblingIndex();
			List<Node> siblings = a.siblingNodes();

			StringBuilder sb = new StringBuilder();
			List<Node> interesting = new ArrayList<>();

			for (int i = index; i < siblings.size(); i++) {
				Node sibling = siblings.get(i);

				if (sibling.toString().startsWith("<hr")) {
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

			while(hyperLinkMatcher.find()) {
				content = content.replace(hyperLinkMatcher.group(), hyperLinkMatcher.group(1));
			}

			retval.put(href, content);
		}

		return retval;
	}

	public static String getSection(String fileName, String sectionKey) {
		String retval = null;

		if (sectionKey.equals("#legacy")) {
			retval = "#legacy";
		}
		else {
			final Map<String, String> sections = parse(fileName);

			if (sections != null) {
				retval = sections.get(sectionKey);
			}
		}

		return retval;
	}
}
