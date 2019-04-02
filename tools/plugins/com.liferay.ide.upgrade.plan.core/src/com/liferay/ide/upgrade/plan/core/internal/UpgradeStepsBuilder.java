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

package com.liferay.ide.upgrade.plan.core.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatus;
import com.liferay.knowledge.base.markdown.converter.MarkdownConverter;
import com.liferay.knowledge.base.markdown.converter.factory.MarkdownConverterFactoryUtil;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Terry Jia
 */
public class UpgradeStepsBuilder {

	public UpgradeStepsBuilder() {
	}

	public UpgradeStepsBuilder(InputStream builderInputStream) {
		inputStream(builderInputStream);
	}

	public List<UpgradeStep> build() throws IOException {
		List<UpgradeStep> upgradeSteps = new ArrayList<>();

		String markdown = CoreUtil.readStreamToString(_builderInputStream);

		MarkdownConverter markdownConverter = MarkdownConverterFactoryUtil.create();

		String html = markdownConverter.convert(markdown);

		Document document = Jsoup.parse(html);

		Elements roots = document.select("#root");

		Element root = roots.get(0);

		_loopChildren(upgradeSteps, null, root);

		return upgradeSteps;
	}

	public void inputStream(InputStream builderInputStream) {
		_builderInputStream = builderInputStream;
	}

	private static void _loopChildren(List<UpgradeStep> upgradeSteps, UpgradeStep parent, Element olElement) {
		Elements children = olElement.children();

		UpgradeStep upgradeStep = null;

		for (Element child : children) {
			String html = child.toString();

			if (html.startsWith("<li")) {
				String imagePath = child.attr("imagePath");
				String requirement = child.attr("requirement");
				String commandId = child.attr("commandId");

				Elements titleElements = child.getElementsByClass("title");

				String title = "";

				String url = "";

				if (titleElements.size() > 0) {
					Element titleElement = titleElements.get(0);

					Elements aTags = titleElement.getElementsByTag("a");

					if (aTags.size() > 0) {
						Element aTag = aTags.get(0);

						url = aTag.attr("href");

						title = aTag.html();
					}
					else {
						title = titleElement.html();
					}
				}

				Elements descriptionElements = child.getElementsByClass("description");

				String description = "";

				if (descriptionElements.size() > 0) {
					Element descriptionElement = descriptionElements.get(0);

					description = descriptionElement.html();
				}

				upgradeStep = new UpgradeStep(
					title, description, imagePath, url, requirement, UpgradeStepStatus.INCOMPLETE, commandId, parent);

				if (parent == null) {
					upgradeSteps.add(upgradeStep);
				}
				else {
					parent.appendChild(upgradeStep);
				}
			}
			else if (html.startsWith("<ol")) {
				_loopChildren(upgradeSteps, upgradeStep, child);
			}
		}
	}

	private InputStream _builderInputStream;

}