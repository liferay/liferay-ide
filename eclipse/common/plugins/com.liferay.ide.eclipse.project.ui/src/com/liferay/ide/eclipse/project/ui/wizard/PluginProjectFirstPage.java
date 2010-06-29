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

package com.liferay.ide.eclipse.project.ui.wizard;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.eclipse.project.core.facet.PortletPluginFacetInstall;
import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.sdk.SDKManager;
import com.liferay.ide.eclipse.sdk.pref.SDKsPreferencePage;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectFirstPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PluginProjectFirstPage extends WebProjectFirstPage implements IPluginProjectDataModelProperties {

	protected Button extType;

	protected Button hookType;

	protected Button layoutTemplateType;

	// private DataModelSynchHelper modelHelper;

	protected Button portletType;

	protected boolean shouldValidatePage = true;

	protected Button themeType;

	public PluginProjectFirstPage(NewPluginProjectWizard wizard, IDataModel model, String pageName) {
		super(model, pageName);

		setImageDescriptor(wizard.getDefaultPageImageDescriptor());

		setTitle("Liferay Plug-in Project");

		setDescription("Create a new plug-in project for Liferay Portal.");

		primaryProjectFacet = PortletPluginFacetInstall.LIFERAY_PORTLET_PLUGIN_FACET;

	}

	@Override
	public boolean canFlipToNextPage() {
		boolean canFlip = super.canFlipToNextPage();

		// if (canFlip) {
		// check to only allow the flip is the user has pressed the "advanced"
		// button
		// return getModel().getBooleanProperty(LIFERAY_ADV_CONFIG);
		// }

		return canFlip;
	}

	public void setShouldValidatePage(boolean shouldValidate) {
		this.shouldValidatePage = shouldValidate;
	}

	protected void configureSDKsLinkSelected(SelectionEvent e) {
		boolean noSDKs = SDKManager.getAllSDKs().length == 0;

		int retval = PreferencesUtil.createPreferenceDialogOn(this.getShell(), SDKsPreferencePage.ID, new String[] {
			SDKsPreferencePage.ID
		}, null).open();

		if (retval == Window.OK) {
			getModel().notifyPropertyChange(LIFERAY_SDK_NAME, IDataModel.VALID_VALUES_CHG);

			if (noSDKs && SDKManager.getDefaultSDK() != null && getModel().getBooleanProperty(LIFERAY_USE_SDK_LOCATION)) {
				// toggling the location property will get the location field to
				// update
				getModel().setBooleanProperty(LIFERAY_USE_SDK_LOCATION, false);
				getModel().setBooleanProperty(LIFERAY_USE_SDK_LOCATION, true);
			}

			validatePage(true);
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

	protected Group createDefaultGroup(Composite parent, String text, int columns) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(text);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		GridLayout gl = new GridLayout(columns, false);

		group.setLayout(gl);

		return group;
	}

	protected void createPluginTypeGroup(Composite parent) {
		Group group = SWTUtil.createGroup(parent, "Plug-in Type", 2);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		portletType =
			SWTUtil.createRadioButton(
				group, "Portlet", getPluginImageDescriptor("/icons/e16/portlet.png").createImage(), false, 1);
		Label l =
			SWTUtil.createLabel(group, SWT.WRAP, "A plug-in for implementing Liferay's portlet modes and actions", 1);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.synchHelper.synchCheckbox(portletType, PLUGIN_TYPE_PORTLET, null);

		hookType =
			SWTUtil.createRadioButton(
				group, "Hook", getPluginImageDescriptor("/icons/e16/hook.png").createImage(), false, 1);
		l =
			SWTUtil.createLabel(
				group, SWT.WRAP, "A hot-deployable plug-in for extending or customizing Liferay's functionality.", 1);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.synchHelper.synchCheckbox(hookType, PLUGIN_TYPE_HOOK, null);

		extType =
			SWTUtil.createRadioButton(
				group, "Ext", getPluginImageDescriptor("/icons/e16/ext.png").createImage(), false, 1);
		l =
			SWTUtil.createLabel(group, SWT.WRAP, "A monolithic plug-in allowing for complete Liferay customization.", 1);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.synchHelper.synchCheckbox(extType, PLUGIN_TYPE_EXT, null);

		layoutTemplateType =
			SWTUtil.createRadioButton(
				group, "Layout", getPluginImageDescriptor("/icons/e16/layout.png").createImage(), false, 1);
		l = SWTUtil.createLabel(group, SWT.WRAP, "A plug-in for customizing Liferay's page layouts.", 1);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.synchHelper.synchCheckbox(layoutTemplateType, PLUGIN_TYPE_LAYOUTTPL, null);

		themeType =
			SWTUtil.createRadioButton(
				group, "Theme", getPluginImageDescriptor("/icons/e16/theme.png").createImage(), false, 1);
		l = SWTUtil.createLabel(group, SWT.WRAP, "A plug-in for customizing Liferay's look and feel.", 1);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.synchHelper.synchCheckbox(themeType, PLUGIN_TYPE_THEME, null);
	}

	protected void createProjectGroup(Composite parent) {
		IDataModel nestedProjectDM = model.getNestedModel(NESTED_PROJECT_DM);

		nestedProjectDM.addListener(this);

		projectNameGroup = new NewPluginProjectGroup(parent, getModel(), nestedProjectDM);
	}

	protected void createSDKGroup(Composite parent) {
		Group group = createDefaultGroup(parent, "Liferay SDK", 2);
		((GridData) group.getLayoutData()).grabExcessVerticalSpace = false;

		// Composite labelContainer = new Composite(group, SWT.NONE);

		// GridData gd = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);

		// labelContainer.setLayoutData(gd);
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = 3;
		gl.marginWidth = 0;

		// labelContainer.setLayout(gl);
		// Label label = SWTUtil.createLabel(labelContainer,
		// "Use Liferay SDK: ", 1);
		// label.setLayoutData(gd);

		// IDataModel nestedProjectModel =
		// getModel().getNestedModel(NESTED_PROJECT_DM);
		// nestedProjectModel.addListener(this);
		// modelHelper = new DataModelSynchHelper(nestedProjectModel);
		// Collection props = getModel().getAllProperties();
		// for (Object prop : props) {
		// System.out.println(prop);
		// }

		getModel().addListener(this);

		// modelHelper = new DataModelSynchHelper(getModel());

		Combo sdkCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		sdkCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

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
		this.synchHelper.synchCombo(sdkCombo, LIFERAY_SDK_NAME, new Control[] {
			configureSDKsLink
		});

		// Composite buttonContainer = new Composite(group, SWT.NONE);
		// gl = new GridLayout(1, false);
		// gl.marginWidth = 5;
		// gl.marginHeight = 0;
		// buttonContainer.setLayout(gl);
		// buttonContainer.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false,
		// false, 3, 1));
		// Button advButton = SWTUtil.createCheckButton(buttonContainer,
		// "Configure advanced project settings", null,
		// false, 3);

		// modelHelper.synchCheckbox(advButton, LIFERAY_ADV_CONFIG, null);
	}

	// protected String[] getSDKNames() {
	// List<String> names = new ArrayList<String>();
	// for (SDK sdk : LiferayCore.getSDKs()) {
	// names.add(sdk.getName());
	// }
	// return names.toArray(new String[names.size()]);
	// }

	protected void createServerTargetComposite(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText("Liferay runtime");
		group.setLayoutData(gdhfill());
		group.setLayout(new GridLayout(2, false));

		serverTargetCombo = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
		serverTargetCombo.setLayoutData(gdhfill());

		Button newServerTargetButton = new Button(group, SWT.NONE);
		newServerTargetButton.setText("New ...");
		newServerTargetButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				final DataModelPropertyDescriptor[] preAdditionDescriptors =
					model.getValidPropertyDescriptors(FACET_RUNTIME);

				boolean isOK =
					ServerUIUtil.showNewRuntimeWizard(
						getShell(), getModuleTypeID(), null, "com.liferay.ide.eclipse.server");

				if (isOK) {
					DataModelPropertyDescriptor[] postAdditionDescriptors =
						model.getValidPropertyDescriptors(FACET_RUNTIME);

					Object[] preAddition = new Object[preAdditionDescriptors.length];

					for (int i = 0; i < preAddition.length; i++) {
						preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
					}

					Object[] postAddition = new Object[postAdditionDescriptors.length];

					for (int i = 0; i < postAddition.length; i++) {
						postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
					}

					Object newAddition = CoreUtil.getNewObject(preAddition, postAddition);

					if (newAddition != null) // can this ever be null?
						model.setProperty(FACET_RUNTIME, newAddition);
				}
			}
		});

		Control[] deps = new Control[] {
			newServerTargetButton
		};
		synchHelper.synchCombo(serverTargetCombo, FACET_RUNTIME, deps);
		if (serverTargetCombo.getSelectionIndex() == -1 && serverTargetCombo.getVisibleItemCount() != 0) {
			serverTargetCombo.select(0);
		}
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(GridData.FILL_BOTH));

		createProjectGroup(top);

		createSDKGroup(top);

		createServerTargetComposite(top);

		createPluginTypeGroup(top);

		updateControls();

		getShell().setImage(
			ImageDescriptor.createFromURL(ProjectUIPlugin.getDefault().getBundle().getEntry("/icons/e16/plugin.png")).createImage());

		return top;
	}

	protected IDataModel getModel() {
		return model;
	}

	protected ImageDescriptor getPluginImageDescriptor(String path) {
		return ImageDescriptor.createFromURL(ProjectUIPlugin.getDefault().getBundle().getEntry(path));
	}

	@Override
	protected String[] getValidationPropertyNames() {
		String[] superProperties = super.getValidationPropertyNames();

		List<String> list = Arrays.asList(superProperties);

		ArrayList<String> arrayList = new ArrayList<String>();

		arrayList.addAll(list);

		arrayList.add(LIFERAY_SDK_NAME);
		arrayList.add(LIFERAY_ADV_CONFIG);
		arrayList.add(FACET_RUNTIME);
		arrayList.add(PROJECT_NAME);
		arrayList.add(DISPLAY_NAME);
		arrayList.add(FACET_PROJECT_NAME);
		arrayList.add(PLUGIN_TYPE_PORTLET);
		arrayList.add(PLUGIN_TYPE_HOOK);
		arrayList.add(PLUGIN_TYPE_EXT);
		arrayList.add(PLUGIN_TYPE_THEME);
		arrayList.add(PLUGIN_TYPE_LAYOUTTPL);

		return (String[]) arrayList.toArray(new String[0]);
	}

	@Override
	protected void validatePage(boolean showMessage) {
		if (shouldValidatePage) {
			super.validatePage(showMessage);
		}
	}

	// protected void createSimpleProjectGroup(Composite parent) {
	// IDataModel nestedProjectDM =
	// getDataModel().getNestedModel(NESTED_PROJECT_DM);
	// nestedProjectDM.addListener(this);
	// this.simpleProjectGroup = new NewSimpleProjectGroup(parent,
	// nestedProjectDM);
	// }

}
