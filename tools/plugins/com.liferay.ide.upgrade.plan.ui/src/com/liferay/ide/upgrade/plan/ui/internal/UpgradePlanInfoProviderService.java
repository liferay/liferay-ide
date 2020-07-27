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

package com.liferay.ide.upgrade.plan.ui.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.upgrade.plan.core.IUpgradePlanOutline;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.ui.UpgradeInfoProvider;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

import java.net.URL;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.ClientProtocolException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.service.component.annotations.Component;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Seiphon Wang
 */
@Component
public class UpgradePlanInfoProviderService implements UpgradeInfoProvider {

	public UpgradePlanInfoProviderService() {
		_promiseFactory = new PromiseFactory(null);
	}

	@Override
	public Promise<String> getDetail(Object element) {
		Deferred<String> deferred = _promiseFactory.deferred();

		if (element instanceof UpgradeStep) {
			UpgradeStep upgradeStep = (UpgradeStep)element;

			new Job(
				"Retrieving " + upgradeStep.getTitle() + " detail..."
			) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					_doUpgradeStepDetail(upgradeStep, deferred);

					Promise<String> promise = deferred.getPromise();

					try {
						Throwable failure = promise.getFailure();

						if (failure != null) {
							UpgradePlanUIPlugin.logError(
								"Error retrieving " + upgradeStep.getTitle() + " detail.", failure);
						}
					}
					catch (InterruptedException ie) {
					}

					return Status.OK_STATUS;
				}

			}.schedule();
		}
		else {
			deferred.fail(new NoSuchElementException());
		}

		return deferred.getPromise();
	}

	@Override
	public String getLabel(Object element) {
		if (element instanceof UpgradeStep) {
			return _doUpgradeStepLabel((UpgradeStep)element);
		}

		return null;
	}

	@Override
	public boolean provides(Object element) {
		return element instanceof UpgradeStep;
	}

	private void _doUpgradeStepDetail(UpgradeStep upgradeStep, Deferred<String> deferred) {
		String detail = "about:blank";

		String upgradeStepUrl = upgradeStep.getUrl();

		if (CoreUtil.isNotNullOrEmpty(upgradeStepUrl)) {
			try {
				UpgradePlan currentUpgradePlan = upgradeStep.getCurrentUpgradePlan();

				IUpgradePlanOutline upgradePlanOutline = currentUpgradePlan.getUpgradePlanOutline();

				if (upgradePlanOutline.isOffline()) {
					detail = _renderArticleMainContent(upgradeStepUrl, upgradePlanOutline);
				}
				else {
					detail = _renderKBMainContent(upgradeStepUrl);
				}
			}
			catch (Throwable t) {
				deferred.fail(t);

				return;
			}
		}

		deferred.resolve(detail);
	}

	private String _doUpgradeStepLabel(UpgradeStep upgradeStep) {
		return upgradeStep.getTitle();
	}

	private File _getEntryFile(File entryFile) {
		File[] entryFiles = entryFile.listFiles(
			new FileFilter() {

				@Override
				public boolean accept(File file) {
					if (file.isDirectory()) {
						_getEntryFile(file);
					}
					else {
						String name = file.getName();

						if (name.startsWith("01-")) {
							return true;
						}

						return false;
					}

					return false;
				}

			});

		if (ListUtil.isNotEmpty(entryFiles)) {
			return entryFiles[0];
		}
		else {
			return null;
		}
	}

	private File _getEntryFile(String url, String splitor, File entryLocation) {
		List<String> urls = new CopyOnWriteArrayList<>(StringUtil.stringToList(url, splitor));

		String[] urlsArray = urls.toArray(new String[0]);

		if (ListUtil.isEmpty(urlsArray)) {
			return null;
		}

		File[] entryFiles = entryLocation.listFiles(
			new FileFilter() {

				@Override
				public boolean accept(File file) {
					String fileName = FilenameUtils.removeExtension(file.getName());

					if (urlsArray[0].contains(fileName)) {
						return true;
					}

					return false;
				}

			});

		if (ListUtil.isEmpty(entryFiles)) {
			return null;
		}

		if (entryFiles[0].isFile()) {
			return entryFiles[0];
		}
		else {
			urls.remove(0);

			String retainedUrls = urls.stream(
			).collect(
				Collectors.joining(File.separator)
			);

			return _getEntryFile(retainedUrls, splitor, entryFiles[0]);
		}
	}

	private String _getFileContents(File inputFile) {
		String detail = "about:blank";

		try {
			if (inputFile == null) {
				return detail;
			}

			if (inputFile.isFile()) {
				Document document = Jsoup.parse(inputFile, "UTF-8");

				Elements elements = document.select("a[class=go-link btn btn-primary]");

				elements.forEach(
					element -> {
						Element pTag = element.parent();

						pTag.remove();
					});

				return document.toString();
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return detail;
	}

	private String _getFileContents(File inputFile, String key, String value) {
		String detail = "about:blank";

		try {
			if (inputFile == null) {
				return detail;
			}

			if (inputFile.isFile()) {
				StringBuffer sb = new StringBuffer();

				StringBuilder stepContents = new StringBuilder();

				Document document = Jsoup.parse(inputFile, "UTF-8");

				Elements elements = document.select("a[class=go-link btn btn-primary]");

				elements.forEach(
					element -> {
						Element pTag = element.parent();

						pTag.remove();
					});

				Elements allElements = document.getAllElements();

				boolean findTag = false;

				for (Element element : allElements) {
					String nodeName = element.nodeName();

					if (!findTag) {
						if (element.hasAttr(key) && nodeName.startsWith("h")) {
							String idValue = element.attr(key);

							if (idValue.equals(value)) {
								findTag = true;
							}
							else {
								continue;
							}
						}
						else {
							continue;
						}
					}

					if (findTag && !nodeName.equals("a") && !nodeName.equals("li")) {
						stepContents.append(element.toString());
					}

					Element nextElementSibling = element.nextElementSibling();

					if (nextElementSibling != null) {
						String nextElementContent = nextElementSibling.toString();

						if (nextElementContent.startsWith("<h")) {
							break;
						}
					}
				}

				sb.append("<html><head><body>");
				sb.append(stepContents);
				sb.append("</body></head></html>");

				return sb.toString();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return detail;
	}

	private String _renderArticleMainContent(String upgradeStepUrl, IUpgradePlanOutline upgradePlanOutline)
		throws ClientProtocolException, IOException {

		File outlineDir = new File(upgradePlanOutline.getLocation());

		List<String> urlsIn = StringUtil.stringToList(upgradeStepUrl, "#");

		String detail = "about:blank";

		if (urlsIn.size() > 1) {
			String[] urlsArray = urlsIn.toArray(new String[0]);

			List<String> urlsList = StringUtil.stringToList(urlsArray[0], File.separator);

			String url = urlsList.get(0);

			File[] listFiles = outlineDir.listFiles(
				new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						if (url.contains(name)) {
							return true;
						}

						return false;
					}

				});

			if (ListUtil.isNotEmpty(listFiles)) {
				if (listFiles[0].isDirectory()) {
					detail = _getFileContents(_getEntryFile(listFiles[0]), "id", urlsArray[1]);
				}
				else {
					detail = _getFileContents(listFiles[0], "id", urlsArray[1]);
				}
			}
		}
		else {
			List<String> urlsOut = StringUtil.stringToList(upgradeStepUrl, File.separator);

			if (ListUtil.isNotEmpty(urlsOut)) {
				File entryFile = _getEntryFile(upgradeStepUrl, File.separator, outlineDir);

				if (entryFile != null) {
					detail = _getFileContents(entryFile);
				}
			}
		}

		return detail;
	}

	private String _renderKBMainContent(String upgradeStepUrl) throws ClientProtocolException, IOException {
		Connection connection = Jsoup.connect(upgradeStepUrl);

		connection = connection.timeout(10000);

		connection = connection.validateTLSCertificates(false);

		Document document = connection.get();

		StringBuffer sb = new StringBuffer();

		sb.append("<html>");

		Elements heads = document.getElementsByTag("head");

		sb.append(heads.get(0));

		if (upgradeStepUrl.contains("#")) {
			sb.append("<script type='text/javascript'>");
			sb.append("window.onload=function(){location.href='");
			sb.append(upgradeStepUrl.substring(upgradeStepUrl.lastIndexOf("#")));
			sb.append("'}");
			sb.append("</script>");
		}

		Elements articleBodies = document.getElementsByClass("article-body");

		Element articleBody = articleBodies.get(0);

		try {
			Elements h1s = articleBody.getElementsByTag("h1");

			Element h1 = h1s.get(0);

			h1.remove();
		}
		catch (Exception e) {
		}

		try {
			Elements uls = articleBody.getElementsByTag("ul");

			Element ul = uls.get(0);

			ul.remove();
		}
		catch (Exception e) {
		}

		try {
			Elements learnPathSteps = articleBody.getElementsByClass("learn-path-step");

			Element learnPathStep = learnPathSteps.get(0);

			learnPathStep.remove();
		}
		catch (Exception e) {
		}

		try {
			Elements learnPathSteps = articleBody.getElementsByClass("article-siblings");

			Element learnPathStep = learnPathSteps.get(0);

			learnPathStep.remove();
		}
		catch (Exception e) {
		}

		URL url = new URL(upgradeStepUrl);

		String protocol = url.getProtocol();

		String authority = url.getAuthority();

		String prefix = protocol + "://" + authority;

		for (Element element : articleBody.getAllElements()) {
			if ("a".equals(element.tagName())) {
				String href = element.attr("href");

				if (href.startsWith("/")) {
					element.attr("href", prefix + href);
				}
			}
		}

		sb.append(articleBody.toString());

		sb.append("</html>");

		return sb.toString();
	}

	private final PromiseFactory _promiseFactory;

}