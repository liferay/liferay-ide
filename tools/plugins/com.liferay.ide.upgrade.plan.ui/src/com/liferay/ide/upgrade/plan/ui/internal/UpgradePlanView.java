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
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanStartedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepDoneEvent;
import com.liferay.ide.upgrade.plan.ui.internal.tasks.UpgradePlanViewer;
import com.liferay.ide.upgrade.plan.ui.internal.tasks.UpgradeTaskStepsViewer;

import java.util.Objects;
import java.util.Optional;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradePlanView extends ViewPart implements ISelectionProvider {

	public static final String ID = "com.liferay.ide.upgrade.plan.view";

	public UpgradePlanView() {
		Bundle bundle = FrameworkUtil.getBundle(UpgradePlanView.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_upgradePlannerServiceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_upgradePlannerServiceTracker.open();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.add(listener);
	}

	@Override
	public void createPartControl(Composite parentComposite) {
		_createPartControl(parentComposite);

		IViewSite viewSite = getViewSite();

		viewSite.setSelectionProvider(this);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (_upgradePlanViewer != null) {
			_upgradePlanViewer.dispose();
		}

		if (_upgradeTaskStepsViewer != null) {
			_upgradeTaskStepsViewer.dispose();
		}
	}

	@Override
	public ISelection getSelection() {
		return _upgradeTaskStepsViewer.getSelection();
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		Optional.ofNullable(
			memento
		).map(
			m -> m.getString("upgradePlanName")
		).filter(
			Objects::nonNull
		).ifPresent(
			upgradePlanName -> {
				UpgradePlanner upgradePlanner = _upgradePlannerServiceTracker.getService();

				upgradePlanner.startUpgradePlan(upgradePlanName);
			}
		);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);

		Object upgradeTaskViewerInput = _upgradePlanViewer.getInput();

		if (upgradeTaskViewerInput instanceof UpgradePlan) {
			UpgradePlan upgradePlan = (UpgradePlan)upgradeTaskViewerInput;

			memento.putString("upgradePlanName", upgradePlan.getName());
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void setSelection(ISelection selection) {
		_upgradeTaskStepsViewer.setSelection(selection);
	}

	private void _createPartControl(Composite parentComposite) {
		parentComposite.setLayout(new FillLayout());

		_upgradePlanViewer = new UpgradePlanViewer(parentComposite);

		UpgradePlanner upgradePlanner = _upgradePlannerServiceTracker.getService();

		upgradePlanner.addListener(
			upgradeEvent -> {
				if (upgradeEvent instanceof UpgradePlanStartedEvent) {
					UpgradePlanStartedEvent upgradePlanStartedEvent = (UpgradePlanStartedEvent)upgradeEvent;

					UpgradePlan upgradePlan = upgradePlanStartedEvent.getUpgradePlan();

					setContentDescription("Active upgrade plan: " + upgradePlan.getName());
				}
			});

		upgradePlanner.addListener(
			upgradeEvent -> {
				if (upgradeEvent instanceof UpgradeTaskStepDoneEvent) {
					UIUtil.refreshCommonView("org.eclipse.ui.navigator.ProjectExplorer");
				}
			});

		_upgradeTaskStepsViewer = new UpgradeTaskStepsViewer(parentComposite, _upgradePlanViewer);

		_upgradeTaskStepsViewer.addSelectionChangedListener(this::_fireSelectionChanged);

		setContentDescription(
			"No active upgrade plan. Use view menu 'New Upgrade Plan' action to start a new upgrade.");
	}

	private void _fireSelectionChanged(SelectionChangedEvent selectionChangedEvent) {
		_listeners.forEach(
			selectionChangedListener -> {
				try {
					selectionChangedListener.selectionChanged(selectionChangedEvent);
				}
				catch (Exception e) {
					UpgradePlanUIPlugin.logError("Error in selection changed listener.", e);
				}
			});
	}

	private ListenerList<ISelectionChangedListener> _listeners = new ListenerList<>();
	private ServiceTracker<UpgradePlanner, UpgradePlanner> _upgradePlannerServiceTracker;
	private UpgradePlanViewer _upgradePlanViewer;
	private UpgradeTaskStepsViewer _upgradeTaskStepsViewer;

}