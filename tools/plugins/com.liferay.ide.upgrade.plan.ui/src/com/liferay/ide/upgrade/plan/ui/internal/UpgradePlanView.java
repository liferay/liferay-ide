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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeListener;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanStartedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatusChangedEvent;
import com.liferay.ide.upgrade.plan.ui.internal.steps.UpgradeStepViewer;
import com.liferay.ide.upgrade.plan.ui.util.SWTUtil;
import com.liferay.ide.upgrade.plan.ui.util.UIUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
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
 * @author Simon Jiang
 */
public class UpgradePlanView extends ViewPart implements ISelectionProvider, UpgradeListener {

	public static final String ID = "com.liferay.ide.upgrade.plan.view";

	public UpgradePlanView() {
		Bundle bundle = FrameworkUtil.getBundle(UpgradePlanView.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();

		_upgradePlanSettings = UpgradePlanUIPlugin.getUpgradePlanSettings();
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
		if (_upgradePlanner != null) {
			_upgradePlanner.dispose(_upgradePlanner.getCurrentUpgradePlan());
		}

		_upgradePlanner.removeListener(this);
		_serviceTracker.close();

		if (_upgradePlanViewer != null) {
			_upgradePlanViewer.dispose();
		}

		if (_upgradeStepViewer != null) {
			_upgradeStepViewer.dispose();
		}

		if ((_upgradeViewProgressBar != null) && !_upgradeViewProgressBar.isDisposed()) {
			_upgradeViewProgressBar.dispose();
		}

		super.dispose();
	}

	@Override
	public ISelection getSelection() {
		return _upgradeStepViewer.getSelection();
	}

	public UpgradePlanViewer getUpgradePlanViewer() {
		return _upgradePlanViewer;
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		_upgradePlanner = _serviceTracker.getService();

		_upgradePlanner.addListener(this);

		Optional.ofNullable(
			_loadActiveUpgradePlanName()
		).filter(
			Objects::nonNull
		).ifPresent(
			upgradePlanName -> {
				UpgradePlan upgradePlan = _upgradePlanner.loadUpgradePlan(upgradePlanName);

				_upgradePlanner.startUpgradePlan(upgradePlan);
			}
		);
	}

	@Override
	public void onUpgradeEvent(UpgradeEvent upgradeEvent) {
		if (upgradeEvent instanceof UpgradePlanStartedEvent) {
			UpgradePlanStartedEvent upgradePlanStartedEvent = (UpgradePlanStartedEvent)upgradeEvent;

			UpgradePlan upgradePlan = upgradePlanStartedEvent.getUpgradePlan();

			if (upgradePlan != null) {
				UIUtil.async(
					() -> {
						Object oldUpgradePlanObject = _upgradePlanViewer.getInput();

						UpgradePlan oldUpgradePlan = Adapters.adapt(oldUpgradePlanObject, UpgradePlan.class);

						if (oldUpgradePlan != null) {
							_saveTreeExpansion(oldUpgradePlan.getName(), _upgradePlanViewer.getTreeExpansion());
						}

						TreeViewer treeViewer = _upgradePlanViewer.getTreeViewer();

						treeViewer.setInput(upgradePlan);

						setContentDescription("Active upgrade plan: " + upgradePlan.getName());

						_upgradePlanner.saveUpgradePlan(upgradePlan);

						_upgradePlanViewer.initTreeExpansion(upgradePlan, _loadTreeExpansion(upgradePlan.getName()));

						_saveActiveUpgradePlanSettings(upgradePlan.getName());

						UpgradePlanUIPlugin.saveUpgradePlanSettings();
					});
			}
		}
		else if (upgradeEvent instanceof UpgradeCommandPerformedEvent) {
			UIUtil.refreshCommonView("org.eclipse.ui.navigator.ProjectExplorer");
		}
		else if (upgradeEvent instanceof UpgradeStepStatusChangedEvent) {
			Object upgradePlanViewerInput = _upgradePlanViewer.getInput();

			UpgradePlan upgradePlan = Adapters.adapt(upgradePlanViewerInput, UpgradePlan.class);

			_upgradePlanner.saveUpgradePlan(upgradePlan);

			UpgradePlanUIPlugin.saveUpgradePlanSettings();
		}
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);

		Object upgradePlanViewerInput = _upgradePlanViewer.getInput();

		if (upgradePlanViewerInput instanceof UpgradePlan) {
			UpgradePlan upgradePlan = (UpgradePlan)upgradePlanViewerInput;

			String upgradePlanName = upgradePlan.getName();

			_saveTreeExpansion(upgradePlanName, _upgradePlanViewer.getTreeExpansion());

			_upgradePlanner.saveUpgradePlan(upgradePlan);

			_saveActiveUpgradePlanSettings(upgradePlanName);

			UpgradePlanUIPlugin.saveUpgradePlanSettings();
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void setSelection(ISelection selection) {
		_upgradeStepViewer.setSelection(selection);
	}

	private void _createPartControl(Composite parentComposite) {
		Composite upgradeViewComposite = SWTUtil.createComposite(parentComposite, 1, 2, GridData.FILL_HORIZONTAL);

		_upgradeViewProgressBar = new UpgradeViewProgressBar(upgradeViewComposite);

		_upgradeViewProgressBar.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		SashForm sashForm = new SashForm(upgradeViewComposite, SWT.HORIZONTAL);

		sashForm.setLayoutData(
			new GridData(
				GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL |
				GridData.HORIZONTAL_ALIGN_FILL));

		_upgradePlanViewer = new UpgradePlanViewer(sashForm);

		_upgradePlanViewer.addPostSelectionChangedListener(this::_fireSelectionChanged);

		_upgradeStepViewer = new UpgradeStepViewer(sashForm, _upgradePlanViewer);

		_upgradeStepViewer.addSelectionChangedListener(this::_fireSelectionChanged);

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

	private String _loadActiveUpgradePlanName() {
		IDialogSettings activeUpgradePlanSection = _upgradePlanSettings.getSection("activeUpgradePlanName");

		if (activeUpgradePlanSection == null) {
			activeUpgradePlanSection = _upgradePlanSettings.addNewSection("activeUpgradePlanName");
		}

		return activeUpgradePlanSection.get("activeUpgradePlanName");
	}

	private List<String> _loadTreeExpansion(String upgradePlanName) {
		IDialogSettings upgradePlanSection = _upgradePlanSettings.getSection(upgradePlanName);

		if (upgradePlanSection == null) {
			return Collections.emptyList();
		}

		String stepTitles = upgradePlanSection.get("stepTitles");

		if (stepTitles == null) {
			return Collections.emptyList();
		}

		String[] titles = stepTitles.split(",");

		return Arrays.asList(titles);
	}

	private void _saveActiveUpgradePlanSettings(String upgradePlanName) {
		IDialogSettings activeUpgradePlanSection = _upgradePlanSettings.getSection("activeUpgradePlanName");

		if (activeUpgradePlanSection == null) {
			activeUpgradePlanSection = _upgradePlanSettings.addNewSection("activeUpgradePlanName");
		}

		activeUpgradePlanSection.put("activeUpgradePlanName", upgradePlanName);
	}

	private void _saveTreeExpansion(String upgradePlanName, Object[] expansions) {
		if (ListUtil.isEmpty(expansions)) {
			return;
		}

		IDialogSettings upgradePlanSection = _upgradePlanSettings.getSection(upgradePlanName);

		if (upgradePlanSection == null) {
			upgradePlanSection = _upgradePlanSettings.addNewSection(upgradePlanName);
		}

		String[] stepTitles = Stream.of(
			expansions
		).map(
			expansion -> (UpgradeStep)expansion
		).map(
			upgradeStep -> upgradeStep.getTitle()
		).filter(
			Objects::nonNull
		).toArray(
			String[]::new
		);

		upgradePlanSection.put("stepTitles", StringUtil.merge(stepTitles, ","));
	}

	private ListenerList<ISelectionChangedListener> _listeners = new ListenerList<>();
	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;
	private UpgradePlanner _upgradePlanner;
	private IDialogSettings _upgradePlanSettings;
	private UpgradePlanViewer _upgradePlanViewer;
	private UpgradeStepViewer _upgradeStepViewer;
	private UpgradeViewProgressBar _upgradeViewProgressBar;

}