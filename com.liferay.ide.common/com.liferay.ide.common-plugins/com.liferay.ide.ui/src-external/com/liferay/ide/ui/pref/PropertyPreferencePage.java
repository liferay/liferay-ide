/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - bug 230072 - initial API and implementation
 *                                        based on org.eclipse.wst.html.ui class of
 *                                        the same name.
 *******************************************************************************/
package com.liferay.ide.ui.pref;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceSorter;
import org.eclipse.wst.sse.core.internal.tasks.TaskTagPreferenceKeys;
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;

/**
 * Based loosely on org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage
 */
abstract class PropertyPreferencePage extends PropertyPage implements IWorkbenchPreferencePage {
	private static final boolean _debugPreferences = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.ui/preferences-properties")); //$NON-NLS-1$ //$NON-NLS-2$
	/*
	 * Disable link data, prevents the display of a "workspace" or "project"
	 * settings link to prevent recursive dialog launching
	 */
	private static final Object DISABLE_LINK = "DISABLE_LINK"; //$NON-NLS-1$

	private Map Data = null;

	private Button EnableProjectSettings;

	private Link ProjectSettingsLink;
	
	private Control Common;
	
	private ControlEnableState Enablements;

	public PropertyPreferencePage() {
		super();
	}

	@Override
	public final void applyData(Object data) {
		super.applyData(data);
		if (data instanceof Map) {
			Data = (Map) data;
			updateLinkEnablement();
		}
	}

	protected abstract Control createCommonContents(Composite composite);

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
			EnableProjectSettings = new Button(checkLinkComposite, SWT.CHECK);
			EnableProjectSettings.setText(SSEUIMessages.EnableProjectSettings); 
			EnableProjectSettings.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			boolean enabledForProject = createPreferenceScopes()[0].getNode(getPreferenceNodeQualifier()).getBoolean(getProjectSettingsKey(), false);
			EnableProjectSettings.setSelection(enabledForProject);
		}
		else {
			Label spacer = new Label(checkLinkComposite, SWT.CHECK);
			spacer.setLayoutData(new GridData());
		}

		ProjectSettingsLink = new Link(checkLinkComposite, SWT.NONE);
		ProjectSettingsLink.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, true, false));

		/*
		 * "element" should be a project, if null, link to per-project
		 * properties
		 */
		if (getProject() != null) {
			ProjectSettingsLink.setText("<a>" + SSEUIMessages.ConfigureWorkspaceSettings + "</a>"); //$NON-NLS-1$//$NON-NLS-2$
		}
		else {
			ProjectSettingsLink.setText("<a>" + SSEUIMessages.ConfigureProjectSettings + "</a>"); //$NON-NLS-1$//$NON-NLS-2$
		}

		updateLinkEnablement();

		ProjectSettingsLink.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				if (getProject() == null) {
					openProjectSettings();
				}
				else {
					openWorkspaceSettings();
				}
			}

		});

		if (getProject() != null) {
			Label line = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
			line.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		}

		Common = createCommonContents(composite);
		
		Common.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (EnableProjectSettings != null) {
			SelectionAdapter selectionAdapter = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					super.widgetSelected(e);
					enablePreferenceContent(EnableProjectSettings.getSelection());
				}
			};
			selectionAdapter.widgetSelected(null);
			EnableProjectSettings.addSelectionListener(selectionAdapter);
		}
		
		applyDialogFont(composite);
		return composite;
	}

	protected IScopeContext[] createPreferenceScopes() {
		IProject project = getProject();
		if (project != null) {
			return new IScopeContext[]{new ProjectScope(project), new InstanceScope(), new DefaultScope()};
		}
		return new IScopeContext[]{new InstanceScope(), new DefaultScope()};
	}

	protected abstract String getPreferenceNodeQualifier();

	protected abstract String getPreferencePageID();

	protected IProject getProject() {
		if (getElement() != null) {
			if (getElement() instanceof IProject) {
				return (IProject) getElement();
			}
			Object adapter = getElement().getAdapter(IProject.class);
			if (adapter instanceof IProject) {
				return (IProject) adapter;
			}
			adapter = getElement().getAdapter(IResource.class);
			if (adapter instanceof IProject) {
				return (IProject) adapter;
			}
		}
		return null;
	}

	protected abstract String getProjectSettingsKey();

	protected abstract String getPropertyPageID();

	protected boolean isElementSettingsEnabled() {
		return EnableProjectSettings != null && EnableProjectSettings.getSelection();
	}

	void openProjectSettings() {
		ListDialog dialog = new ListDialog(getShell()) {

			@Override
			protected Control createDialogArea(Composite container) {
				Control area = super.createDialogArea(container);
				getTableViewer().setSorter(new ResourceSorter(ResourceSorter.NAME));
				return area;
			}
		};
		dialog.setMessage(SSEUIMessages.PropertyPreferencePage_02);
		dialog.setContentProvider(new IStructuredContentProvider() {
			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				return ((IWorkspace) inputElement).getRoot().getProjects();
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});
		dialog.setLabelProvider(new DecoratingLabelProvider(new WorkbenchLabelProvider(), SSEUIPlugin.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator()));
		dialog.setInput(ResourcesPlugin.getWorkspace());
		dialog.setTitle(SSEUIMessages.PropertyPreferencePage_01);
		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length > 0) {
				IProject project = (IProject) dialog.getResult()[0];
				Map data = new HashMap();
				data.put(DISABLE_LINK, Boolean.TRUE);
				PreferencesUtil.createPropertyDialogOn(getShell(), project, getPropertyPageID(), new String[]{getPropertyPageID()}, data).open();
			}
		}
	}

	void openWorkspaceSettings() {
		Map data = new HashMap();
		data.put(DISABLE_LINK, Boolean.TRUE);
		PreferencesUtil.createPreferenceDialogOn(getShell(), getPreferencePageID(), new String[]{getPreferencePageID()}, data).open();
	}

	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		IScopeContext[] preferenceScopes = createPreferenceScopes();
		if (getProject() != null) {
			if (isElementSettingsEnabled()) {
				if (_debugPreferences) {
					System.out.println(getClass().getName() + " setting " + TaskTagPreferenceKeys.TASK_TAG_PER_PROJECT + " (" + true + ") in scope " + preferenceScopes[0].getName() + ":" + preferenceScopes[0].getLocation()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$  
				}
				preferenceScopes[0].getNode(getPreferenceNodeQualifier()).putBoolean(getProjectSettingsKey(), EnableProjectSettings.getSelection());
			}
			else {
				if (_debugPreferences) {
					System.out.println(getClass().getName() + " removing " + TaskTagPreferenceKeys.TASK_TAG_PER_PROJECT + " from scope " + preferenceScopes[0].getName() + ":" + preferenceScopes[0].getLocation()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
				}
				preferenceScopes[0].getNode(getPreferenceNodeQualifier()).remove(getProjectSettingsKey());
			}
		}
		return ok;
	}
	
	@Override
	protected void performDefaults() {
		if(getProject() != null && EnableProjectSettings != null) {
			EnableProjectSettings.setSelection(false);
			enablePreferenceContent(false);
		}
		super.performDefaults();
	}

	private void updateLinkEnablement() {
		if (Data != null && ProjectSettingsLink != null) {
			ProjectSettingsLink.setEnabled(!Boolean.TRUE.equals(Data.get(DISABLE_LINK)));
		}
	}
	
	/**
	 * Controls the enablement of the common content region
	 * of a property or preference page
	 * 
	 * @param enable the enabled state of the common content
	 * area
	 */
	protected void enablePreferenceContent(boolean enable) {
		if(enable) {
			if(Enablements != null) {
				Enablements.restore();
				Enablements = null;
			}
		}
		else {
			if(Enablements == null)
				Enablements = ControlEnableState.disable(Common);
		}
	}
}
