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

package com.liferay.ide.upgrade.plan.ui.internal.steps;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeListener;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradePreview;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatus;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatusChangedEvent;
import com.liferay.ide.upgrade.plan.core.util.ServicesLookup;
import com.liferay.ide.upgrade.plan.ui.Disposable;
import com.liferay.ide.upgrade.plan.ui.internal.UpgradePlanUIPlugin;
import com.liferay.ide.upgrade.plan.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class UpgradeStepItem implements ServicesLookup, UpgradeItem, UpgradeListener {

	public UpgradeStepItem(FormToolkit formToolkit, ScrolledForm scrolledForm, UpgradeStep upgradeStep) {
		_formToolkit = formToolkit;
		_scrolledForm = scrolledForm;
		_upgradeStep = upgradeStep;

		_parentComposite = _scrolledForm.getBody();

		GridDataFactory gridDataFactory = GridDataFactory.fillDefaults();

		gridDataFactory.grab(true, true);

		_parentComposite.setLayoutData(gridDataFactory.create());

		_parentComposite.setLayout(new TableWrapLayout());

		Bundle bundle = FrameworkUtil.getBundle(UpgradeStepItem.class);

		_serviceTracker = new ServiceTracker<>(bundle.getBundleContext(), UpgradePlanner.class, null);

		_serviceTracker.open();

		_upgradePlanner = _serviceTracker.getService();

		_upgradePlanner.addListener(this);

		if (_upgradeStep == null) {
			return;
		}

		FormText summary = _formToolkit.createFormText(_parentComposite, true);

		String summaryValue = _upgradeStep.getSummary();

		if (CoreUtil.isNotNullOrEmpty(summaryValue)) {
			summary.setText(summaryValue, true, false);
		}
		else {
			summary.setText("", true, false);
		}

		_disposables.add(summary::dispose);

		_buttonComposite = _formToolkit.createComposite(_parentComposite);

		GridLayout buttonGridLayout = new GridLayout(1, false);

		buttonGridLayout.marginHeight = 2;
		buttonGridLayout.marginWidth = 2;
		buttonGridLayout.verticalSpacing = 2;

		_buttonComposite.setLayout(buttonGridLayout);

		_buttonComposite.setLayoutData(new TableWrapData(TableWrapData.FILL));

		List<UpgradeStep> children = _upgradeStep.getChildren();

		if (children.isEmpty()) {
			if (CoreUtil.isNotNullOrEmpty(_upgradeStep.getCommandId())) {
				if (_previewable()) {
					Image stepPreviewImage = UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.STEP_PERVIEW_IMAGE);

					ImageHyperlink previewImageHyperlink = createImageHyperlink(
						_formToolkit, _buttonComposite, stepPreviewImage, this, "Click to preview",
						"Performing " + _upgradeStep.getTitle() + "...", this::_preview, _upgradeStep);

					_disposables.add(previewImageHyperlink::dispose);

					_enables.add(previewImageHyperlink);
				}

				Image stepPerformImage = UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.STEP_PERFORM_IMAGE);

				ImageHyperlink performImageHyperlink = createImageHyperlink(
					_formToolkit, _buttonComposite, stepPerformImage, this, "Click to perform",
					"Performing " + _upgradeStep.getTitle() + "...", this::_perform, _upgradeStep);

				_disposables.add(performImageHyperlink::dispose);

				_enables.add(performImageHyperlink);
			}
			else {
				Image stepCompleteImage = UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.STEP_COMPLETE_IMAGE);

				ImageHyperlink completeImageHyperlink = createNoneJobImageHyperlink(
					_formToolkit, _buttonComposite, stepCompleteImage, this, "Click when complete",
					"Completing " + _upgradeStep.getTitle() + "...", this::_complete, _upgradeStep);

				_disposables.add(completeImageHyperlink::dispose);

				_enables.add(completeImageHyperlink);
			}
		}

		Image stepRestartImage = UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.STEP_RESTART_IMAGE);

		_restartImageHyperlink = createNoneJobImageHyperlink(
			_formToolkit, _buttonComposite, stepRestartImage, this, "Restart",
			"Restarting " + _upgradeStep.getTitle() + "...", this::_restart, _upgradeStep);

		_updateEnablementRestart(_upgradeStep, _restartImageHyperlink);

		_disposables.add(_restartImageHyperlink::dispose);

		Image stepSkipImage = UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.STEP_SKIP_IMAGE);

		ImageHyperlink skipImageHyperlink = createNoneJobImageHyperlink(
			_formToolkit, _buttonComposite, stepSkipImage, this, "Skip", "Skipping " + _upgradeStep.getTitle() + "...",
			this::_skip, _upgradeStep);

		_disposables.add(skipImageHyperlink::dispose);

		_enables.add(skipImageHyperlink);

		_updateEnablement(_upgradeStep, _enables);
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.add(listener);
	}

	public void dispose() {
		_upgradePlanner.removeListener(this);
		_serviceTracker.close();

		for (Disposable disposable : _disposables) {
			try {
				disposable.dispose();
			}
			catch (Throwable t) {
			}
		}

		if (_parentComposite.isDisposed()) {
			return;
		}

		Stream.of(
			_parentComposite.getChildren()
		).filter(
			control -> !control.isDisposed()
		).forEach(
			control -> control.dispose()
		);
	}

	@Override
	public ISelection getSelection() {
		return TreeSelection.EMPTY;
	}

	@Override
	public void onUpgradeEvent(UpgradeEvent upgradeEvent) {
		if (upgradeEvent instanceof UpgradeStepStatusChangedEvent) {
			UpgradeStepStatusChangedEvent upgradeStepStatusChangedEvent = (UpgradeStepStatusChangedEvent)upgradeEvent;

			UpgradeStepStatus newStatus = upgradeStepStatusChangedEvent.getNewStatus();

			if (newStatus.equals(UpgradeStepStatus.FAILED) || newStatus.equals(UpgradeStepStatus.INCOMPLETE)) {
				UpgradeStep upgradeStep = upgradeStepStatusChangedEvent.getUpgradeStep();

				if (upgradeStep.equals(_upgradeStep)) {
					UIUtil.async(() -> _updateEnablement(_upgradeStep, _enables));
					UIUtil.async(() -> _updateEnablementRestart(_upgradeStep, _restartImageHyperlink));
				}
			}
		}
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
	}

	private static void _updateEnablement(UpgradeStep upgradeStep, Collection<Control> enables) {
		AtomicBoolean enabled = new AtomicBoolean(false);

		if (!upgradeStep.completed()) {
			enabled.set(true);
		}

		Stream<Control> stream = enables.stream();

		stream.filter(
			c -> !c.isDisposed()
		).forEach(
			c -> c.setEnabled(enabled.get() && upgradeStep.enabled())
		);
	}

	private static void _updateEnablementRestart(UpgradeStep upgradeStep, Control restartLink) {
		if (!restartLink.isDisposed()) {
			restartLink.setEnabled(upgradeStep.restartable());
		}
	}

	private IStatus _complete(IProgressMonitor progressMonitor) {
		_upgradePlanner.completeStep(_upgradeStep);

		return Status.OK_STATUS;
	}

	private IStatus _perform(IProgressMonitor progressMonitor) {
		String commandId = _upgradeStep.getCommandId();

		UpgradeCommand upgradeCommand = getSingleService(UpgradeCommand.class, "(id=" + commandId + ")");

		IStatus performStatus = Status.CANCEL_STATUS;

		if (upgradeCommand != null) {
			performStatus = upgradeCommand.perform(progressMonitor);

			if (performStatus.isOK() || (performStatus.getSeverity() == Status.INFO) ||
				(performStatus.getSeverity() == Status.WARNING)) {

				_upgradeStep.setStatus(UpgradeStepStatus.COMPLETED);
			}
			else if (performStatus.getSeverity() == Status.CANCEL) {
				_upgradeStep.setStatus(UpgradeStepStatus.INCOMPLETE);
			}
			else {
				_upgradeStep.setStatus(UpgradeStepStatus.FAILED);
			}
		}

		return performStatus;
	}

	private IStatus _preview(IProgressMonitor progressMonitor) {
		String commandId = _upgradeStep.getCommandId();

		UpgradePreview upgradePreview = getSingleService(UpgradePreview.class, "(id=" + commandId + ")");

		upgradePreview.preview(progressMonitor);

		return Status.OK_STATUS;
	}

	private boolean _previewable() {
		String commandId = _upgradeStep.getCommandId();

		UpgradePreview upgradePreview = getSingleService(UpgradePreview.class, "(id=" + commandId + ")");

		if (upgradePreview != null) {
			return true;
		}

		return false;
	}

	private IStatus _restart(IProgressMonitor progressMonitor) {
		_upgradePlanner.restartStep(_upgradeStep);

		return Status.OK_STATUS;
	}

	private IStatus _skip(IProgressMonitor progressMonitor) {
		_upgradePlanner.skipStep(_upgradeStep);

		return Status.OK_STATUS;
	}

	private Composite _buttonComposite;
	private List<Disposable> _disposables = new ArrayList<>();
	private List<Control> _enables = new ArrayList<>();
	private FormToolkit _formToolkit;
	private ListenerList<ISelectionChangedListener> _listeners = new ListenerList<>();
	private Composite _parentComposite;
	private ImageHyperlink _restartImageHyperlink;
	private ScrolledForm _scrolledForm;
	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;
	private UpgradePlanner _upgradePlanner;
	private final UpgradeStep _upgradeStep;

}