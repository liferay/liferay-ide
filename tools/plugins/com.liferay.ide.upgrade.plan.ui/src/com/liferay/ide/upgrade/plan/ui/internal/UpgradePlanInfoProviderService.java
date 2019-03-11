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
import com.liferay.ide.upgrade.plan.core.UpgradePlanElement;
import com.liferay.ide.upgrade.plan.ui.UpgradeInfoProvider;

import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.Parser.Builder;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.io.IOException;

import java.util.NoSuchElementException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.osgi.service.component.annotations.Component;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;

/**
 * @author Gregory Amerson
 */
@Component
public class UpgradePlanInfoProviderService implements UpgradeInfoProvider {

	public UpgradePlanInfoProviderService() {
		_promiseFactory = new PromiseFactory(null);
	}

	@Override
	public Promise<String> getDetail(Object element) {
		Deferred<String> deferred = _promiseFactory.deferred();

		if (element instanceof UpgradePlanElement) {
			UpgradePlanElement upgradePlanElement = (UpgradePlanElement)element;

			new Job("Retrieving " + upgradePlanElement.getTitle() + " detail...") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					_doUpgradePlanElementDetail(upgradePlanElement, deferred);

					Promise<String> promise = deferred.getPromise();

					try {
						Throwable failure = promise.getFailure();

						if (failure != null) {
							return UpgradePlanUIPlugin.createErrorStatus(
								"Error retrieving " + upgradePlanElement.getTitle() + " detail.", failure);
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
		if (element instanceof UpgradePlanElement) {
			return _doUpgradePlanElementLabel((UpgradePlanElement)element);
		}

		return null;
	}

	@Override
	public boolean provides(Object element) {
		return element instanceof UpgradePlanElement;
	}

	private void _doUpgradePlanElementDetail(UpgradePlanElement upgradePlanElement, Deferred<String> deferred) {
		String detail = null;

		String url = upgradePlanElement.getUrl();

		if (CoreUtil.isNotNullOrEmpty(url)) {
			if (url.endsWith(".markdown")) {
				try {
					detail = _downloadAndRenderMarkdown(url);
				}
				catch (Throwable t) {
					deferred.fail(t);
				}
			}
			else if (url.startsWith("https://")) {
				detail = url;
			}
		}
		else {
			StringBuffer sb = new StringBuffer();

			sb.append(upgradePlanElement.getTitle());

			sb.append("<br />");

			sb.append(upgradePlanElement.getDescription());

			detail = sb.toString();
		}

		deferred.resolve(detail);
	}

	private String _doUpgradePlanElementLabel(UpgradePlanElement upgradePlanElement) {
		return upgradePlanElement.getTitle();
	}

	private String _downloadAndRenderMarkdown(String url) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();

		HttpGet get = new HttpGet(url);

		HttpResponse response = httpClient.execute(get);

		HttpEntity entity = response.getEntity();

		String content = EntityUtils.toString(entity);

		MutableDataSet options = new MutableDataSet();

		Builder parserBuilder = Parser.builder(options);

		Parser parser = parserBuilder.build();

		HtmlRenderer.Builder rendererBuilder = HtmlRenderer.builder(options);

		HtmlRenderer renderer = rendererBuilder.build();

		Document document = parser.parse(content);

		return renderer.render(document);
	}

	private final PromiseFactory _promiseFactory;

}