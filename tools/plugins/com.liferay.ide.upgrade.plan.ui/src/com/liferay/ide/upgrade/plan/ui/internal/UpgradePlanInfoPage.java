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
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.ui.UpgradeInfoProvider;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.Parser.Builder;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.io.IOException;

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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradePlanInfoPage extends Page implements ISelectionChangedListener {

	public UpgradePlanInfoPage(IWorkbenchPart workbenchPart) {
		_workbenchPart = workbenchPart;

		Bundle bundle = FrameworkUtil.getBundle(UpgradePlanInfoPage.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_upgradeInfoProviderServiceTracker = new ServiceTracker<>(bundleContext, UpgradeInfoProvider.class, null);

		_upgradeInfoProviderServiceTracker.open();
	}

	@Override
	public void createControl(Composite parent) {
		_composite = new Composite(parent, SWT.NULL);

		_composite.setLayout(new FillLayout());

		_browser = new Browser(_composite, SWT.BORDER);

		_browser.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void expansionStateChanged(ExpansionEvent event) {
		if ((_browser == null) || _browser.isDisposed()) {
			return;
		}

		Object source = event.getSource();

		if (source instanceof ExpandableComposite) {
			ExpandableComposite expandableComposite = (ExpandableComposite)source;

			UpgradeTaskStep upgradeTaskStep = (UpgradeTaskStep)expandableComposite.getData("upgradeTaskStep");

			String url = upgradeTaskStep.getUrl();

			if (CoreUtil.isNotNullOrEmpty(url)) {
				ReadMarkdownJob job = new ReadMarkdownJob(url);

				job.schedule();
			}
			else {
				StringBuffer sb = new StringBuffer();

				String title = upgradeTaskStep.getTitle();

				sb.append(title);

				sb.append("<br />");

				String description = upgradeTaskStep.getDescription();

				sb.append(description);

				_browser.setText(sb.toString());

				getControl().redraw();
			}
		}
	}

	@Override
	public Control getControl() {
		return _composite;
	}

	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);

		IWorkbenchPartSite workbenchPartSite = _workbenchPart.getSite();

		ISelectionProvider selectionProvider = workbenchPartSite.getSelectionProvider();

		selectionProvider.addSelectionChangedListener(this);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent selectionChangedEvent) {
		if ((_browser == null) || _browser.isDisposed()) {
			return;
		}

		ISelection selection = selectionChangedEvent.getSelection();

		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection)selection;

			Object firstElement = structuredSelection.getFirstElement();

			UpgradeInfoProvider upgradeInfoProvider = _upgradeInfoProviderServiceTracker.getService();

			Promise<String> detail = upgradeInfoProvider.getDetail(firstElement);

			detail.onResolve(
				() -> {
					UIUtil.async(
						() -> {
							try {
								if (detail.getFailure() != null) {
									_browser.setText("about:blank");
								}
								else {
									_browser.setText(detail.getValue());
								}

								_composite.redraw();
							}
							catch (Exception e) {
							}
						});
				});

			_composite.redraw();
		}
	}

	@Override
	public void setFocus() {
		_composite.setFocus();
	}

	public void setMessage(String message) {
		if (_browser != null) {
			_browser.setText(message);
		}
	}

	private Browser _browser;
	private Composite _composite;
	private final ServiceTracker<UpgradeInfoProvider, UpgradeInfoProvider> _upgradeInfoProviderServiceTracker;
	private IWorkbenchPart _workbenchPart;

	private class ReadMarkdownJob extends Job {

		public ReadMarkdownJob(String url) {
			super("fetching markdown");

			_url = url;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			HttpClient httpClient = new DefaultHttpClient();

			HttpGet get = new HttpGet(_url);

			try {
				HttpResponse response = httpClient.execute(get);

				HttpEntity entity = response.getEntity();

				String content = EntityUtils.toString(entity);

				MutableDataSet options = new MutableDataSet();

				Builder parserBuilder = Parser.builder(options);

				Parser parser = parserBuilder.build();

				HtmlRenderer.Builder rendererBuilder = HtmlRenderer.builder(options);

				HtmlRenderer renderer = rendererBuilder.build();

				Node document = parser.parse(content);

				String md = renderer.render(document);

				UIUtil.sync(
					() -> {
						_browser.setText(md);

						_composite.redraw();
					});
			}
			catch (IOException ioe) {
				UIUtil.sync(
					() -> {
						_browser.setText("Unable to fetch markdown");

						_composite.redraw();
					});
			}

			return Status.OK_STATUS;
		}

		private final String _url;

	}

}