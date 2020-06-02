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
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.ui.UpgradeInfoProvider;

import java.io.IOException;

import java.net.URL;

import java.util.NoSuchElementException;
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

		String url = upgradeStep.getUrl();

		if (CoreUtil.isNotNullOrEmpty(url)) {
			try {
				detail = _renderArticleMainContent(url);
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

	private String _renderArticleMainContent(String upgradeStepUrl) throws ClientProtocolException, IOException {
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