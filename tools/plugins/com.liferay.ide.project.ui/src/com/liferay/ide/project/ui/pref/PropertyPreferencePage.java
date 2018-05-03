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

package com.liferay.ide.project.ui.pref;

import com.liferay.ide.core.util.ListUtil;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceSorter;
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings({"rawtypes", "deprecation", "restriction", "unchecked"})
public abstract class PropertyPreferencePage extends PropertyPage implements IWorkbenchPreferencePage {

	public PropertyPreferencePage() {
	}

	@Override
	public final void applyData(Object data) {
		super.applyData(data);

		if (data instanceof Map) {
			_data = (Map)data;
			_updateLinkEnablement();
		}
	}

	@Override
	public final Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();

		composite.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);

		composite.setLayoutData(data);

		Composite checkLinkComposite = new Composite(composite, SWT.NONE);

		checkLinkComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		checkLinkComposite.setLayout(new GridLayout(2, false));

		if (getProject() != null) {
			_enableProjectSettings = new Button(checkLinkComposite, SWT.CHECK);

			_enableProjectSettings.setText(SSEUIMessages.EnableProjectSettings);
			_enableProjectSettings.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

			boolean enabledForProject = createPreferenceScopes()[0].getNode(
				getPreferenceNodeQualifier()).getBoolean(getProjectSettingsKey(), false);

			_enableProjectSettings.setSelection(enabledForProject);
		}
		else {
			Label spacer = new Label(checkLinkComposite, SWT.CHECK);

			spacer.setLayoutData(new GridData());
		}

		_projectSettingsLink = new Link(checkLinkComposite, SWT.NONE);

		_projectSettingsLink.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, true, false));

		/*
		 * "element" should be a project, if null, link to per-project properties
		 */
		if (getProject() != null) {
			_projectSettingsLink.setText("<a>" + SSEUIMessages.ConfigureWorkspaceSettings + "</a>");
		}
		else {
			_projectSettingsLink.setText("<a>" + SSEUIMessages.ConfigureProjectSettings + "</a>");
		}

		_updateLinkEnablement();

		_projectSettingsLink.addSelectionListener(
			new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e) {
					if (getProject() == null) {
						_openProjectSettings();
					}
					else {
						_openWorkspaceSettings();
					}
				}

			});

		if (getProject() != null) {
			Label line = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);

			line.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		}

		_common = createCommonContents(composite);

		_common.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (_enableProjectSettings != null) {
			SelectionAdapter selectionAdapter = new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					super.widgetSelected(e);

					enablePreferenceContent(_enableProjectSettings.getSelection());
				}

			};

			selectionAdapter.widgetSelected(null);
			_enableProjectSettings.addSelectionListener(selectionAdapter);
		}

		applyDialogFont(composite);

		return composite;
	}

	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		IScopeContext[] preferenceScopes = createPreferenceScopes();

		if (getProject() != null) {
			if (isElementSettingsEnabled()) {
				preferenceScopes[0].getNode(
					getPreferenceNodeQualifier()).putBoolean(getProjectSettingsKey(),
					_enableProjectSettings.getSelection());
			}
			else {
				preferenceScopes[0].getNode(getPreferenceNodeQualifier()).remove(getProjectSettingsKey());
			}
		}

		return ok;
	}

	protected abstract Control createCommonContents(Composite composite);

	protected IScopeContext[] createPreferenceScopes() {
		IProject project = getProject();

		if (project != null) {
			return new IScopeContext[] {new ProjectScope(project), new InstanceScope(), new DefaultScope()};
		}

		return new IScopeContext[] {new InstanceScope(), new DefaultScope()};
	}

	protected void enablePreferenceContent(boolean enable) {
		if (enable) {
			if (_enablements != null) {
				_enablements.restore();
				_enablements = null;
			}
		}
		else {
			if (_enablements == null) {
				_enablements = ControlEnableState.disable(_common);
			}
		}
	}

	protected abstract String getPreferenceNodeQualifier();

	protected abstract String getPreferencePageID();

	protected IProject getProject() {
		if (getElement() != null) {
			if (getElement() instanceof IProject) {
				return (IProject)getElement();
			}

			Object adapter = getElement().getAdapter(IProject.class);

			if (adapter instanceof IProject) {
				return (IProject)adapter;
			}

			adapter = getElement().getAdapter(IResource.class);

			if (adapter instanceof IProject) {
				return (IProject)adapter;
			}
		}

		return null;
	}

	protected abstract String getProjectSettingsKey();

	protected abstract String getPropertyPageID();

	protected boolean isElementSettingsEnabled() {
		if ((_enableProjectSettings != null) && _enableProjectSettings.getSelection()) {
			return true;
		}

		return false;
	}

	@Override
	protected void performDefaults() {
		if ((getProject() != null) && (_enableProjectSettings != null)) {
			_enableProjectSettings.setSelection(false);
			enablePreferenceContent(false);
		}

		super.performDefaults();
	}

	private void _openProjectSettings() {
		ListDialog dialog = new ListDialog(getShell()) {

			@Override
			protected Control createDialogArea(Composite container) {
				Control area = super.createDialogArea(container);
				getTableViewer().setSorter(new ResourceSorter(ResourceSorter.NAME));

				return area;
			}

		};

		dialog.setMessage(SSEUIMessages.PropertyPreferencePage_02);
		dialog.setContentProvider(
			new IStructuredContentProvider() {

				public void dispose() {
				}

				public Object[] getElements(Object inputElement) {
					return ((IWorkspace)inputElement).getRoot().getProjects();
				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}

			});

		IWorkbench workbench = SSEUIPlugin.getDefault().getWorkbench();

		dialog.setLabelProvider(
			new DecoratingLabelProvider(
				new WorkbenchLabelProvider(), workbench.getDecoratorManager().getLabelDecorator()));

		dialog.setInput(ResourcesPlugin.getWorkspace());
		dialog.setTitle(SSEUIMessages.PropertyPreferencePage_01);

		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();

			if (ListUtil.isNotEmpty(result)) {
				IProject project = (IProject)dialog.getResult()[0];
				Map data = new HashMap();

				data.put(_disableLink, Boolean.TRUE);
				PreferencesUtil.createPropertyDialogOn(
					getShell(), project, getPropertyPageID(), new String[] {getPropertyPageID()}, data).open();
			}
		}
	}

	private void _openWorkspaceSettings() {
		Map data = new HashMap();

		data.put(_disableLink, Boolean.TRUE);
		PreferencesUtil.createPreferenceDialogOn(
			getShell(), getPreferencePageID(), new String[] {getPreferencePageID()}, data).open();
	}

	private void _updateLinkEnablement() {
		if ((_data != null) && (_projectSettingsLink != null)) {
			_projectSettingsLink.setEnabled(!Boolean.TRUE.equals(_data.get(_disableLink)));
		}
	}

	/**
	 * Disable link data, prevents the display of a "workspace" or "project"
	 * settings link to prevent recursive dialog launching
	 */
	private static final Object _disableLink = "DISABLE_LINK";

	private Control _common;
	private Map _data = null;
	private ControlEnableState _enablements;
	private Button _enableProjectSettings;
	private Link _projectSettingsLink;

}