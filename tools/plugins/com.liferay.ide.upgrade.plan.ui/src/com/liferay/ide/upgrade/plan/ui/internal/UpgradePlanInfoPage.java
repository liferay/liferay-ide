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
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.ui.UpgradeInfoProvider;
import com.liferay.ide.upgrade.plan.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Ethan Sun
 */
public class UpgradePlanInfoPage extends Page implements ISelectionChangedListener {

	public UpgradePlanInfoPage(IWorkbenchPart workbenchPart) {
		_workbenchPart = workbenchPart;

		Bundle upgradePlanInfoPageBundle = FrameworkUtil.getBundle(UpgradePlanInfoPage.class);

		_upgradeInfoProviderServiceTracker = new ServiceTracker<>(
			upgradePlanInfoPageBundle.getBundleContext(), UpgradeInfoProvider.class, null);

		_upgradeInfoProviderServiceTracker.open();

		Bundle upgradePlannerBundle = FrameworkUtil.getBundle(UpgradePlanner.class);

		_upgradePlannerServiceTracker = new ServiceTracker<>(
			upgradePlannerBundle.getBundleContext(), UpgradePlanner.class, null);

		_upgradePlannerServiceTracker.open();

		UpgradePlanner upgradePlannerService = _upgradePlannerServiceTracker.getService();

		_upgradePlan = upgradePlannerService.getCurrentUpgradePlan();
	}

	@Override
	public void createControl(Composite parent) {
		_composite = new Composite(parent, SWT.NULL);

		_composite.setLayout(new FillLayout());

		_browser = new Browser(_composite, SWT.BORDER);

		_browser.setLayoutData(new GridData(GridData.FILL_BOTH));

		List<UpgradeStep> upgradeStepList = new ArrayList<>();

		if (!Objects.isNull(_upgradePlan)) {
			upgradeStepList.addAll(_upgradePlan.getUpgradeSteps());
		}

		_browser.addLocationListener(
			new LocationListener() {

				@Override
				public void changed(LocationEvent event) {
				}

				@Override
				public void changing(LocationEvent event) {
					String url = event.location;

					if (!url.startsWith("about:blank") && !url.startsWith("http")) {
						_browser.setUrl("about:blank");

						String replacement = "";

						if (CoreUtil.isWindows()) {
							replacement = "about:";
						}
						else {
							replacement = "file:///";
						}

						final String targetUrl = url.replaceFirst(replacement, "");

						Stream<UpgradeStep> upgradeStepStream = upgradeStepList.stream();

						upgradeStepStream.forEach(
							upgradeStep -> {
								List<UpgradeStep> childrenStepList = upgradeStep.getChildren();

								Stream<UpgradeStep> childStepStream = childrenStepList.stream();

								childStepStream.filter(
									currentStep -> {
										String stepUrl = currentStep.getUrl();

										if (CoreUtil.isWindows()) {
											stepUrl = stepUrl.replace('\\', '/');
										}

										return stepUrl.endsWith(targetUrl);
									}
								).findFirst(
								).ifPresent(
									UpgradePlanInfoPage.this::renderBrowser
								);
							});
					}
				}

			});
	}

	@Override
	public void dispose() {
		super.dispose();

		_upgradeInfoProviderServiceTracker.close();
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

	public void renderBrowser(Object element) {
		Stream.of(
			_upgradeInfoProviderServiceTracker.getServices(new UpgradeInfoProvider[0])
		).filter(
			upgradeInfoProvider -> upgradeInfoProvider.provides(element)
		).findFirst(
		).ifPresent(
			upgradeInfoProvider -> {
				Promise<String> detail = upgradeInfoProvider.getDetail(element);

				detail.onResolve(
					() -> UIUtil.async(
						() -> {
							try {
								if (detail.getFailure() != null) {
									_browser.setText("about:blank");
								}
								else {
									String detailValue = detail.getValue();

									if (detailValue.startsWith("https://") || detailValue.equals("about:blank")) {
										_browser.setUrl(detailValue);
									}
									else {
										_browser.setText(detailValue, true);
									}
								}

								_composite.redraw();
							}
							catch (Exception e) {
							}
						}));
			}
		);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent selectionChangedEvent) {
		if ((_browser == null) || _browser.isDisposed()) {
			return;
		}

		ISelection selection = selectionChangedEvent.getSelection();

		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection)selection;

			renderBrowser(structuredSelection.getFirstElement());
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
	private UpgradePlan _upgradePlan;
	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _upgradePlannerServiceTracker;
	private IWorkbenchPart _workbenchPart;

}