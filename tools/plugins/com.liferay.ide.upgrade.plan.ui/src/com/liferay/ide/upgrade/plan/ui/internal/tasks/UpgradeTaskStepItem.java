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

package com.liferay.ide.upgrade.plan.ui.internal.tasks;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.FolderSelectionTaskStep;
import com.liferay.ide.upgrade.plan.core.ProjectSelectionTaskStep;
import com.liferay.ide.upgrade.plan.core.ProjectsSelectionTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepRequirement;
import com.liferay.ide.upgrade.plan.ui.Disposable;
import com.liferay.ide.upgrade.plan.ui.UpgradePlanUIPlugin;
import com.liferay.ide.upgrade.plan.ui.dialogs.ProjectSelectionDialog;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeTaskStepItem implements Disposable, ISelectionProvider, IExpansionListener {

	public UpgradeTaskStepItem(ScrolledForm scrolledForm, UpgradeTaskStep upgradeTaskStep) {
		_scrolledForm = scrolledForm;
		_upgradeTaskStep = upgradeTaskStep;

		Display display = _scrolledForm.getDisplay();

		Composite parentComposite = _scrolledForm.getBody();

		_formToolkit = new FormToolkit(display);

		_checkDoneLabel = _formToolkit.createLabel(parentComposite, " ");

		_disposables.add(() -> _checkDoneLabel.dispose());

		_mainItemComposite = _formToolkit.createExpandableComposite(
			parentComposite, ExpandableComposite.COMPACT | ExpandableComposite.TWISTIE);

		_mainItemComposite.setData("upgradeTaskStep", _upgradeTaskStep);

		_mainItemComposite.addExpansionListener(this);

		String title = _upgradeTaskStep.getTitle();

		UpgradeTaskStepRequirement upgradeStepRequirement = _upgradeTaskStep.getRequirement();

		if (upgradeStepRequirement != null) {
			String requirement = upgradeStepRequirement.toString();

			title = title + " (" + requirement.toLowerCase() + ")";
		}

		_mainItemComposite.setText(title);

		_disposables.add(() -> _mainItemComposite.dispose());

		_titleComposite = _formToolkit.createComposite(_mainItemComposite);

		GridLayout gridLayout = new GridLayout(1, false);

		GridData gridData = new GridData(GridData.FILL_BOTH);

		_titleComposite.setLayout(gridLayout);
		_titleComposite.setLayoutData(gridData);

		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;

		_disposables.add(() -> _titleComposite.dispose());

		Composite bodyComposite = _formToolkit.createComposite(_mainItemComposite);

		_mainItemComposite.setClient(bodyComposite);

		bodyComposite.setLayout(new TableWrapLayout());

		bodyComposite.setLayoutData(new TableWrapData(TableWrapData.FILL));

		_disposables.add(() -> bodyComposite.dispose());

		String description = _upgradeTaskStep.getDescription();

		Label label = _formToolkit.createLabel(bodyComposite, description);

		_disposables.add(() -> label.dispose());

		if (_upgradeTaskStep == null) {
			return;
		}

		_buttonComposite = _formToolkit.createComposite(bodyComposite);

		GridLayout buttonGridLayout = new GridLayout(4, false);

		buttonGridLayout.marginHeight = 2;
		buttonGridLayout.marginWidth = 2;
		buttonGridLayout.verticalSpacing = 2;

		_buttonComposite.setLayout(buttonGridLayout);

		_buttonComposite.setLayoutData(new TableWrapData(TableWrapData.FILL));

		_disposables.add(() -> _buttonComposite.dispose());

		Label fillLabel = _formToolkit.createLabel(_buttonComposite, null);

		gridData = new GridData();

		gridData.widthHint = 16;

		fillLabel.setLayoutData(gridData);

		_disposables.add(() -> fillLabel.dispose());

		Image taskStartImage = UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.COMPOSITE_TASK_START_IMAGE);

		ImageHyperlink performImageHyperlink = _createImageHyperlink(_buttonComposite, taskStartImage, this, "Perform");

		performImageHyperlink.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		performImageHyperlink.addHyperlinkListener(
			new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					_execute();
				}

			});

		_disposables.add(() -> performImageHyperlink.dispose());

		String url = _upgradeTaskStep.getUrl();

		if (CoreUtil.isNotNullOrEmpty(url)) {
			ImageHyperlink openDocumentImageHyperlink = _createImageHyperlink(
				_buttonComposite, taskStartImage, this, "Open document");

			openDocumentImageHyperlink.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

			openDocumentImageHyperlink.addHyperlinkListener(
				new HyperlinkAdapter() {

					@Override
					public void linkActivated(HyperlinkEvent e) {
						try {
							UIUtil.openURL(new URL(url));
						}
						catch (Exception ex) {
							UpgradePlanUIPlugin.logError("Could not open external browser.", ex);
						}
					}

				});
		}

		_boldFont = _mainItemComposite.getFont();

		FontData[] fontDatas = _boldFont.getFontData();

		for (FontData fontData : fontDatas) {
			fontData.setStyle(fontData.getStyle() ^ SWT.BOLD);
		}

		_regularFont = new Font(display, fontDatas);

		setBold(false);
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.add(listener);
	}

	public void dispose() {
		for (Disposable disposable : _disposables) {
			try {
				disposable.dispose();
			}
			catch (Throwable t) {
			}
		}
	}

	@Override
	public void expansionStateChanged(ExpansionEvent expansionEvent) {
		ISelection selection = new StructuredSelection(_upgradeTaskStep);

		SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(this, selection);

		_listeners.forEach(
			selectionChangedListener -> {
				selectionChangedListener.selectionChanged(selectionChangedEvent);
			});
	}

	@Override
	public void expansionStateChanging(ExpansionEvent expansionEvent) {
	}

	@Override
	public ISelection getSelection() {
		return null;
	}

	public void initialized() {
		_initialized = true;
	}

	public boolean isBold() {
		return _bold;
	}

	public boolean isCompleted() {
		return _completed;
	}

	public boolean isExpanded() {
		return _mainItemComposite.isExpanded();
	}

	public boolean isSkipped() {
		return _skipped;
	}

	public void redraw() {
		_scrolledForm.redraw();
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	public void setAsCurrentActiveItem() {
		setButtonsVisible(true);
		setBold(true);
		setExpanded();
		setFocus();
	}

	public void setBold(boolean value) {
		if (value) {
			_mainItemComposite.setFont(_boldFont);

			if (_initialized) {
				_mainItemComposite.layout();
			}
		}
		else {
			_mainItemComposite.setFont(_regularFont);

			if (_initialized) {
				_mainItemComposite.layout();
			}
		}

		_bold = value;
	}

	public void setButtonsVisible(boolean visible) {
		if ((_buttonExpanded != visible) && (_buttonComposite != null)) {
			_buttonComposite.setVisible(visible);
		}

		if (visible && _initialized) {
			FormToolkit.ensureVisible(_mainItemComposite);
		}

		_buttonExpanded = visible;
	}

	public void setCollapsed() {
		if (_mainItemComposite.isExpanded()) {
			_mainItemComposite.setExpanded(false);

			if (_initialized) {
				_scrolledForm.reflow(true);

				FormToolkit.ensureVisible(_mainItemComposite);
			}
		}
	}

	public void setComplete() {
		_completed = true;
		_checkDoneLabel.setImage(_getCompleteImage());

		if (_initialized) {
			Composite parent = _checkDoneLabel.getParent();

			parent.layout();
		}
	}

	public void setExpanded() {
		if (!_mainItemComposite.isExpanded()) {
			_mainItemComposite.setExpanded(true);

			if (_initialized) {
				_scrolledForm.reflow(true);

				FormToolkit.ensureVisible(_mainItemComposite);
			}
		}
	}

	public void setIncomplete() {
		_checkDoneLabel.setImage(null);
		_completed = false;
	}

	@Override
	public void setSelection(ISelection selection) {
	}

	public void setSkipped() {
		_skipped = true;
		_checkDoneLabel.setImage(_getSkipImage());

		if (_initialized) {
			Composite parent = _checkDoneLabel.getParent();

			parent.layout();
		}
	}

	protected void setCompletionMessageCollapsed() {
		if ((_completionComposite != null) && _completionMessageExpanded) {
			_completionComposite.dispose();

			_completionComposite = null;

			_scrolledForm.reflow(true);
		}

		_completionMessageExpanded = false;
	}

	protected void setFocus() {
		_mainItemComposite.setFocus();

		FormToolkit.ensureVisible(_mainItemComposite);
	}

	private ImageHyperlink _createImageHyperlink(Composite parentComposite, Image image, Object data, String linkText) {
		ImageHyperlink imageHyperlink = _formToolkit.createImageHyperlink(parentComposite, SWT.NULL);

		imageHyperlink.setData(data);
		imageHyperlink.setImage(image);
		imageHyperlink.setText(linkText);
		imageHyperlink.setToolTipText(linkText);

		return imageHyperlink;
	}

	private IStatus _execute() {
		if (_upgradeTaskStep instanceof ProjectSelectionTaskStep) {
			ProjectSelectionTaskStep projectSelectionTaskStep = (ProjectSelectionTaskStep)_upgradeTaskStep;

			ViewerFilter viewerFilter = new ViewerFilter() {

				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					return projectSelectionTaskStep.selectFilter(parentElement, element);
				}

			};

			boolean selectAllDefault = projectSelectionTaskStep.selectAllDefault();

			ProjectSelectionDialog dialog = new ProjectSelectionDialog(
				UIUtil.getActiveShell(), viewerFilter, selectAllDefault);

			if (dialog.open() == Window.OK) {
				Object[] projects = dialog.getResult();

				return projectSelectionTaskStep.execute((IProject)projects[0], new NullProgressMonitor());
			}
			else {
				return Status.CANCEL_STATUS;
			}
		}

		if (_upgradeTaskStep instanceof ProjectsSelectionTaskStep) {
			ProjectsSelectionTaskStep projectsSelectionTaskStep = (ProjectsSelectionTaskStep)_upgradeTaskStep;

			ViewerFilter viewerFilter = new ViewerFilter() {

				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					return projectsSelectionTaskStep.selectFilter(parentElement, element);
				}

			};

			boolean selectAllDefault = projectsSelectionTaskStep.selectAllDefault();

			ProjectSelectionDialog dialog = new ProjectSelectionDialog(
				UIUtil.getActiveShell(), viewerFilter, selectAllDefault);

			if (dialog.open() == Window.OK) {
				Object[] result = dialog.getResult();

				IProject[] projects = Stream.of(
					result
				).toArray(
					IProject[]::new
				);

				return projectsSelectionTaskStep.execute(projects, new NullProgressMonitor());
			}
			else {
				return Status.CANCEL_STATUS;
			}
		}
		else if (_upgradeTaskStep instanceof FolderSelectionTaskStep) {
			FolderSelectionTaskStep fileUpgradeTaskStep = (FolderSelectionTaskStep)_upgradeTaskStep;

			DirectoryDialog dialog = new DirectoryDialog(UIUtil.getActiveShell());

			String result = dialog.open();

			if (result == null) {
				return Status.CANCEL_STATUS;
			}

			File folder = new File(result);

			if (FileUtil.exists(folder)) {
				fileUpgradeTaskStep.execute(folder, new NullProgressMonitor());
			}

			return Status.OK_STATUS;
		}
		else {
			return _upgradeTaskStep.execute(new NullProgressMonitor());
		}
	}

	private Image _getCompleteImage() {
		return UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.ITEM_COMPLETE_IMAGE);
	}

	private Image _getSkipImage() {
		return UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.ITEM_SKIP_IMAGE);
	}

	private boolean _bold = true;
	private Font _boldFont;
	private Composite _buttonComposite;
	private boolean _buttonExpanded = true;
	private Label _checkDoneLabel;
	private boolean _completed;
	private Composite _completionComposite;
	private boolean _completionMessageExpanded = false;
	private List<Disposable> _disposables = new ArrayList<>();
	private FormToolkit _formToolkit;
	private boolean _initialized;
	private ListenerList<ISelectionChangedListener> _listeners = new ListenerList<>();
	private ExpandableComposite _mainItemComposite;
	private Font _regularFont;
	private ScrolledForm _scrolledForm;
	private boolean _skipped;
	private Composite _titleComposite;
	private final UpgradeTaskStep _upgradeTaskStep;

}