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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.IUpgradePlanOutline;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepRequirement;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatus;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Terry Jia
 * @author Seiphon Wang
 * @author Simon Jiang
 */
public class UpgradeStepsBuilder {

	public UpgradeStepsBuilder(IUpgradePlanOutline upgradePlanOutline) {
		_upgradePlanOutline = upgradePlanOutline;
	}

	public List<UpgradeStep> build() throws IOException {
		List<UpgradeStep> upgradeSteps = new ArrayList<>();

		Document document = null;

		String outlineLocation = _upgradePlanOutline.getLocation();

		File outlineIndexFile = new File(outlineLocation);

		File[] indexFiles = outlineIndexFile.listFiles(
			new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					if (name.startsWith("01-")) {
						return true;
					}

					return false;
				}

			});

		if (FileUtil.exists(indexFiles[0])) {
			document = Jsoup.parse(indexFiles[0], "UTF-8");
		}

		Elements roots = document.select("ol");

		for (int i = 0; i <= roots.size(); i++) {
			Element root = roots.get(i);

			String attributeName = root.attr("class");

			if (Objects.equals("root", attributeName)) {
				_loopChildren(upgradeSteps, null, root);

				break;
			}
		}

		return upgradeSteps;
	}

	private void _loopChildren(List<UpgradeStep> upgradeSteps, UpgradeStep parent, Element olElement) {
		Elements children = olElement.children();

		UpgradeStep upgradeStep = null;

		for (Element child : children) {
			String html = child.toString();

			if (html.startsWith("<li")) {
				String requirement = UpgradeStepRequirement.REQUIRED.toString();

				Elements pTags = child.getElementsByTag("p");

				String title = "";

				String url = "";

				Element titleElement = pTags.get(0);

				Elements aTags = titleElement.getElementsByTag("a");

				String commandId = "";

				if (aTags.size() > 0) {
					Element aTag = aTags.get(0);

					url = FileUtil.separatorsToSystem(aTag.attr("href"));

					title = aTag.text();

					commandId = aTag.attr("commandid");
				}
				else {
					title = titleElement.text();
				}

				String summary = "";

				Element titleNextElement = titleElement.nextElementSibling();

				if ((titleNextElement != null) && Objects.equals("p", titleNextElement.nodeName())) {
					summary = titleNextElement.text();
				}
				else {
					summary = title;
				}

				String imagePath = "";

				upgradeStep = new UpgradeStep(
					title, summary, imagePath, url, requirement, UpgradeStepStatus.INCOMPLETE, commandId, parent);

				if (parent == null) {
					upgradeSteps.add(upgradeStep);
				}
				else {
					parent.appendChild(upgradeStep);
				}

				if (titleNextElement != null) {
					if (Objects.equals("p", titleNextElement.nodeName())) {
						titleNextElement = titleNextElement.nextElementSibling();
					}

					if ((titleNextElement != null) && Objects.equals("ol", titleNextElement.nodeName())) {
						_loopChildren(upgradeSteps, upgradeStep, titleNextElement);
					}
				}
			}
		}
	}

	private IUpgradePlanOutline _upgradePlanOutline;

}