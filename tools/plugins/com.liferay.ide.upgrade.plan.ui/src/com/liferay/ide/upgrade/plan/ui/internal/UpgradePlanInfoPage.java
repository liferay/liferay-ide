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

import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.ui.UpgradeInfoProvider;

import java.util.stream.Stream;

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

			Stream.of(
				_upgradeInfoProviderServiceTracker.getServices(new UpgradeInfoProvider[0])
			).filter(
				upgradeInfoProvider -> upgradeInfoProvider.provides(firstElement)
			).findFirst(
			).ifPresent(
				upgradeInfoProvider -> {
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
											String detailValue = detail.getValue();

											if (detailValue.startsWith("https://")) {
												_browser.setUrl(detailValue);
											}
											else {
												_browser.setText(detail.getValue(), true);
											}
										}

										_composite.redraw();
									}
									catch (Exception e) {
									}
								});
						});
				}
			);
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

}