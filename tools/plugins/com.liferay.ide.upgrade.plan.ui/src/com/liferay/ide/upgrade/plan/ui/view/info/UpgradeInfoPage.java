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

package com.liferay.ide.upgrade.plan.ui.view.info;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.api.Summary;
import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.ui.view.plan.TasksViewer;
import com.liferay.ide.upgrade.plan.ui.view.plan.UpgradePlanView;

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
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.Page;

/**
 * @author Terry Jia
 */
public class UpgradeInfoPage extends Page implements ISelectionChangedListener, IExpansionListener {

	@Override
	public void createControl(Composite parent) {
		_composite = new Composite(parent, SWT.NULL);

		_composite.setLayout(new FillLayout());

		_browser = new Browser(_composite, SWT.BORDER);

		_browser.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void dispose() {
		super.dispose();

		IViewPart projectExplorer = UIUtil.findView("org.eclipse.ui.navigator.ProjectExplorer");

		if (projectExplorer != null) {
			CommonNavigator navigator = (CommonNavigator)projectExplorer;

			CommonViewer commonViewer = navigator.getCommonViewer();

			commonViewer.removePostSelectionChangedListener(this);
		}
	}

	@Override
	public void expansionStateChanged(ExpansionEvent event) {
		if ((_browser == null) || _browser.isDisposed()) {
			return;
		}

		Object source = event.getSource();

		if (source instanceof ExpandableComposite) {
			ExpandableComposite composite = (ExpandableComposite)source;

			UpgradeTaskStep step = (UpgradeTaskStep)composite.getData("step");

			String url = (String)step.getProperty("step.md");

			if (CoreUtil.isNotNullOrEmpty(url)) {
				ReadMarkdownJob job = new ReadMarkdownJob(url);

				job.schedule();
			}
			else {
				StringBuffer sb = new StringBuffer();

				Object title = step.getProperty("step.title");

				sb.append(title);

				sb.append("<br />");

				Object description = step.getProperty("step.description");

				sb.append(description);

				_browser.setText(sb.toString());

				getControl().redraw();
			}
		}
	}

	@Override
	public void expansionStateChanging(ExpansionEvent event) {
	}

	@Override
	public Control getControl() {
		return _composite;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if ((_browser == null) || _browser.isDisposed()) {
			return;
		}

		ISelection selection = event.getSelection();

		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection)selection;

			Object element = structuredSelection.getFirstElement();

			if (element instanceof Summary) {
				Summary summary = (Summary)element;

				String detail = summary.doDetail();

				if (detail != null) {
					if (detail.endsWith("markdown") || detail.endsWith("md")) {
						ReadMarkdownJob job = new ReadMarkdownJob(detail);

						job.schedule();
					}
					else {
						_browser.setText(detail);
					}
				}
			}
			else {
				_browser.setUrl("about:blank");
			}

			getControl().redraw();
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

	protected UpgradeInfoPage() {
		IViewPart projectExplorer = UIUtil.findView("org.eclipse.ui.navigator.ProjectExplorer");

		if (projectExplorer != null) {
			CommonNavigator navigator = (CommonNavigator)projectExplorer;

			CommonViewer commonViewer = navigator.getCommonViewer();

			commonViewer.addPostSelectionChangedListener(this);
		}

		IViewPart upgradePlanView = UIUtil.findView(UpgradePlanView.ID);

		if (upgradePlanView != null) {
			TasksViewer tasksViewer = ((UpgradePlanView)upgradePlanView).getTasksViewer();

			tasksViewer.addSelectionChangedListener(this);
		}
	}

	private Browser _browser;
	private Composite _composite;

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

						getControl().redraw();
					});
			}
			catch (IOException ioe) {
				UIUtil.sync(
					() -> {
						_browser.setText("Unable to fetch markdown");

						getControl().redraw();
					});
			}

			return Status.OK_STATUS;
		}

		private final String _url;

	}

}