/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.project.ui;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;
import com.liferay.ide.eclipse.sdk.pref.SDKsPreferencePage;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author Greg Amerson
 */
public class LiferayProjectPropertyPage extends PropertyPage
	implements IWorkbenchPropertyPage, IPluginProjectDataModelProperties {

	protected Combo sdkCombo;

	public LiferayProjectPropertyPage() {
		super();

		setImageDescriptor(ProjectUIPlugin.imageDescriptorFromPlugin(
			ProjectUIPlugin.PLUGIN_ID, "/icons/e16/liferay.png"));
	}

	@Override
	public boolean isValid() {
		boolean valid = super.isValid();

		if (!valid) {
			return valid;
		}

		int index = sdkCombo.getSelectionIndex();

		if (index < 0) {
			valid = false;
		}
		else {
			String sdkName = sdkCombo.getItem(sdkCombo.getSelectionIndex());

			SDK sdk = SDKManager.getInstance().getSDK(sdkName);

			valid = sdk != null;
		}

		return valid;
	}

	@Override
	public boolean performOk() {
		if (!isValid()) {
			setErrorMessage("Current values are invalid.");

			return false;
		}

		boolean retval = false;

		String sdkName = sdkCombo.getItem(sdkCombo.getSelectionIndex());

		SDK sdk = SDKManager.getInstance().getSDK(sdkName);

		if (sdk != null) {
			IFacetedProject facetedProject = getFacetedProject();

			IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet(facetedProject);

			try {
				ProjectUtil.setSDK(facetedProject.getProject(), liferayFacet, sdk);

				retval = true;
			}
			catch (Exception e) {
				ProjectUIPlugin.logError(e);
			}
		}

		return retval;
	}

	protected void configureSDKsLinkSelected(SelectionEvent e) {
		// boolean noSDKs = SDKManager.getAllSDKs().length == 0;

		String[] id = new String[] {
			SDKsPreferencePage.ID
		};

		PreferenceDialog dialog =
			PreferencesUtil.createPreferenceDialogOn(this.getShell(), SDKsPreferencePage.ID, id, null);

		int retval = dialog.open();

		if (retval == Window.OK) {
			setupSDKCombo();

			getContainer().updateButtons();
		}

		// if
		// (getModel().getProperty(IPortalPluginProjectDataModelProperties.LIFERAY_SDK_NAME).equals(
		// IPortalPluginProjectDataModelProperties.LIFERAY_SDK_NAME_DEFAULT_VALUE))
		// {
		// no default sdk set, lets set one if it exists
		// SDK sdk = LiferayCore.getDefaultSDK();
		// if (sdk != null) {
		// getModel().setProperty(IPortalPluginProjectDataModelProperties.LIFERAY_SDK_NAME,
		// sdk.getName());
		// sdkCombo.setItems(new String[0]);//refreish items
		// }
		// }
		// modelHelper.synchAllUIWithModel();
		// }
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite top = SWTUtil.createTopComposite(parent, 1);

		createSDKGroup(top);

		return top;
	}

	protected Group createDefaultGroup(Composite parent, String text, int columns) {
		GridLayout gl = new GridLayout(columns, false);

		Group group = new Group(parent, SWT.NONE);
		group.setText(text);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setLayout(gl);

		return group;
	}

	protected void createSDKGroup(Composite parent) {
		Group group = createDefaultGroup(parent, "Liferay Plugin SDK", 2);

		((GridData) group.getLayoutData()).grabExcessVerticalSpace = false;

		// Composite labelContainer = new Composite(group, SWT.NONE);

		// GridData gd = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);

		// labelContainer.setLayoutData(gd);

		GridLayout gl = new GridLayout(1, false);

		gl.marginHeight = 3;
		gl.marginWidth = 0;

		sdkCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);

		sdkCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sdkCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getContainer().updateButtons();
			}

		});

		// labelContainer = new Composite(group, SWT.NONE);
		// labelContainer.setLayout(gl);
		// labelContainer.setLayoutData(gd);

		Link configureSDKsLink = new Link(group, SWT.UNDERLINE_LINK);

		configureSDKsLink.setText("<a href=\"#\">Configure SDKs</a>");
		configureSDKsLink.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		configureSDKsLink.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				configureSDKsLinkSelected(e);
			}

		});

		setupSDKCombo();

		// modelHelper.synchCombo(sdkCombo, LIFERAY_SDK_NAME, new Control[] {
		// configureSDKsLink });
	}

	protected IFacetedProject getFacetedProject() {
		IFacetedProject retval = null;

		IAdaptable adaptable = getElement();

		IProject project = (IProject) adaptable.getAdapter(IProject.class);

		if (project != null) {
			retval = ProjectUtil.getFacetedProject(project);
		}

		return retval;
	}

	@Override
	protected void performDefaults() {
		SDK defaultSdk = SDKManager.getInstance().getDefaultSDK();

		if (defaultSdk != null) {
			setupSDKCombo();

			String[] items = sdkCombo.getItems();

			for (int i = 0; i < items.length; i++) {
				String sdkItemName = sdkCombo.getItem(i);

				if (defaultSdk.getName().equals(sdkItemName)) {
					sdkCombo.select(i);

					break;
				}
			}
		}

		super.performDefaults();
	}

	protected void setupSDKCombo() {
		List<String> sdkNames = new ArrayList<String>();

		SDK[] allSDKs = SDKManager.getInstance().getSDKs();

		for (SDK sdk : allSDKs) {
			sdkNames.add(sdk.getName());
		}

		sdkCombo.setItems(sdkNames.toArray(new String[0]));

		IFacetedProject facetedProject = getFacetedProject();

		if (facetedProject != null) {
			IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet(facetedProject);

			try {
				Preferences prefs = facetedProject.getPreferences(liferayFacet).node("liferay-plugin-project");

				String sdkName = prefs.get(ISDKConstants.PROPERTY_NAME, "");

				SDK sdk = SDKManager.getInstance().getSDK(sdkName);

				if (!CoreUtil.isNullOrEmpty(sdkName) && sdk != null) {
					sdkCombo.select(sdkNames.indexOf(sdk.getName()));
				}
			}
			catch (BackingStoreException e) {
				ProjectUIPlugin.logError(e);
			}
		}
	}
}
