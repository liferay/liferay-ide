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
							return UpgradePlanUIPlugin.createErrorStatus(
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

		deferred.resolve(detail);
	}

	private String _doUpgradeStepLabel(UpgradeStep upgradeStep) {
		return upgradeStep.getTitle();
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