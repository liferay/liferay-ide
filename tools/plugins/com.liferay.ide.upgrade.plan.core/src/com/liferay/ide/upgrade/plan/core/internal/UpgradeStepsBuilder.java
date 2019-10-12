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

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
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

		if (_upgradePlanOutline.isOffline()) {
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
		}
		else {
			_url = new URL(_upgradePlanOutline.getLocation());

			Connection connection = Jsoup.connect(_url.toString());

			connection = connection.timeout(10000);

			connection = connection.validateTLSCertificates(false);

			document = connection.get();
		}

		Elements roots = document.select("ol");

		for (int i = 0; i <= roots.size(); i++) {
			Element root = roots.get(i);

			String attributeName = root.attr("class");

			if ("root".equals(attributeName)) {
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

//					url = aTag.attr("href");
					if (_upgradePlanOutline.isOffline()) {
						String href = aTag.attr("href");

						if (parent != null) {
							String selfHref = href.replace(_href_prefix, "");

							if (!selfHref.contains(parent.getUrl()) && !selfHref.contains("#")) {
								url = parent.getUrl() + "/" + selfHref;
							}
							else {
								url = selfHref;
							}
						}
						else {
							url = href.replace(_href_prefix, "");
						}
					}
					else {
						String protocol = _url.getProtocol();

						String authority = _url.getAuthority();

						url = protocol + "://" + authority + aTag.attr("href");
					}

					title = aTag.text();

					commandId = aTag.attr("commandid");
				}
				else {
					title = titleElement.text();
				}

				String summary = "";

				Element titleNextElement = titleElement.nextElementSibling();

				if ((titleNextElement != null) && "p".equals(titleNextElement.nodeName())) {
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
					if ("p".equals(titleNextElement.nodeName())) {
						titleNextElement = titleNextElement.nextElementSibling();
					}

					if ((titleNextElement != null) && "ol".equals(titleNextElement.nodeName())) {
						_loopChildren(upgradeSteps, upgradeStep, titleNextElement);
					}
				}
			}
		}
	}

	private static String _href_prefix = "/docs/7-2/tutorials/-/knowledge_base/t/";

	private IUpgradePlanOutline _upgradePlanOutline;
	private URL _url;

}