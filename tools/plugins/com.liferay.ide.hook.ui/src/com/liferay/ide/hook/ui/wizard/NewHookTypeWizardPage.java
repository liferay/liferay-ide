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

package com.liferay.ide.hook.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.hook.core.operation.INewHookDataModelProperties;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.wizard.LiferayDataModelWizardPage;
import com.liferay.ide.ui.util.SWTUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class NewHookTypeWizardPage extends LiferayDataModelWizardPage implements INewHookDataModelProperties {

	public NewHookTypeWizardPage(IDataModel model, String pageName) {
		super(
			model, pageName, Msgs.createLiferayHook,
			HookUI.imageDescriptorFromPlugin(HookUI.PLUGIN_ID, "/icons/wizban/hook_wiz.png"));

		setDescription(Msgs.defineNewHookPlugin);
	}

	protected void createHookTypesGroup(Composite parent) {
		Group group = SWTUtil.createGroup(parent, Msgs.selectHookTypes, 1);

		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		customJSPButton = SWTUtil.createCheckButton(group, Msgs.customJSPs, null, false, 1);

		this.synchHelper.synchCheckbox(customJSPButton, CREATE_CUSTOM_JSPS, null);

		portalPropertiesButton = SWTUtil.createCheckButton(group, Msgs.portalProperties, null, false, 1);

		this.synchHelper.synchCheckbox(portalPropertiesButton, CREATE_PORTAL_PROPERTIES, null);

		servicesButton = SWTUtil.createCheckButton(group, Msgs.services, null, false, 1);

		this.synchHelper.synchCheckbox(servicesButton, CREATE_SERVICES, null);

		languagePropertiesButton = SWTUtil.createCheckButton(group, Msgs.languageProperties, null, false, 1);

		this.synchHelper.synchCheckbox(languagePropertiesButton, CREATE_LANGUAGE_PROPERTIES, null);
	}

	/**
	 * Add project group
	 */
	protected void createProjectNameGroup(Composite parent) {

		// set up project name label

		projectNameLabel = new Label(parent, SWT.NONE);

		projectNameLabel.setText("Hook plugin project:");
		projectNameLabel.setLayoutData(new GridData());

		GridData data = new GridData(GridData.FILL_HORIZONTAL);

		data.widthHint = 300;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;

		// set up project name entry field

		projectNameCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);

		projectNameCombo.setLayoutData(data);

		synchHelper.synchCombo(projectNameCombo, PROJECT_NAME, null);

		initializeProjectList();
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 2);

		createProjectNameGroup(topComposite);

		// SWTUtil.createSeparator(composite, 2);

		SWTUtil.createVerticalSpacer(topComposite, 2, 2);

		createHookTypesGroup(topComposite);

		setShellImage();

		return topComposite;
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] {
			PROJECT_NAME, CREATE_CUSTOM_JSPS, CREATE_PORTAL_PROPERTIES, CREATE_SERVICES, CREATE_LANGUAGE_PROPERTIES
		};
	}

	protected void initializeProjectList() {
		IProject[] workspaceProjects = CoreUtil.getAllProjects();

		List<String> items = new ArrayList<>();

		for (IProject project : workspaceProjects) {
			if (isProjectValid(project)) {
				items.add(project.getName());
			}
		}

		if (ListUtil.isEmpty(items)) {
			return;
		}

		String[] names = new String[items.size()];

		for (int i = 0; i < items.size(); i++) {
			names[i] = items.get(i);
		}

		projectNameCombo.setItems(names);

		IProject selectedProject = null;

		try {
			if (model != null) {
				String projectNameFromModel = model.getStringProperty(
					IArtifactEditOperationDataModelProperties.COMPONENT_NAME);

				if ((projectNameFromModel != null) && (projectNameFromModel.length() > 0)) {
					selectedProject = CoreUtil.getProject(projectNameFromModel);
				}
			}
		}
		catch (Exception e) {
		}

		try {
			if (selectedProject == null) {
				selectedProject = getSelectedProject();
			}

			if ((selectedProject != null) && selectedProject.isAccessible() &&
				selectedProject.hasNature(IModuleConstants.MODULE_NATURE_ID)) {

				projectNameCombo.setText(selectedProject.getName());

				validateProjectRequirements(selectedProject);

				model.setProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME, selectedProject.getName());
			}
		}
		catch (CoreException ce) {

			// Ignore

		}

		if ((projectName == null) && ListUtil.isNotEmpty(names)) {
			projectName = names[0];
		}

		if (CoreUtil.isNullOrEmpty(projectNameCombo.getText()) && (projectName != null)) {
			projectNameCombo.setText(projectName);

			validateProjectRequirements(CoreUtil.getProject(projectName));

			model.setProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME, projectName);
		}
	}

	protected boolean isProjectValid(IProject project) {
		IFile liferayHookXml = project.getFile("src/main/webapp/WEB-INF/liferay-hook.xml");

		if (ProjectUtil.isHookProject(project) || ProjectUtil.isPortletProject(project) || liferayHookXml.exists()) {
			return true;
		}

		return false;
	}

	protected void setShellImage() {
		HookUI hookUI = HookUI.getDefault();

		Bundle bundle = hookUI.getBundle();

		URL url = bundle.getEntry("/icons/e16/hook.png");

		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);

		Image shellImage = imageDescriptor.createImage();

		getShell().setImage(shellImage);
	}

	protected Button customJSPButton;
	protected Button folderButton;
	protected Text folderText;
	protected Button languagePropertiesButton;
	protected Button portalPropertiesButton;
	protected String projectName;
	protected Combo projectNameCombo;
	protected Label projectNameLabel;
	protected Button servicesButton;

	private static class Msgs extends NLS {

		public static String createLiferayHook;
		public static String customJSPs;
		public static String defineNewHookPlugin;
		public static String languageProperties;
		public static String portalProperties;
		public static String selectHookTypes;
		public static String services;

		static {
			initializeMessages(NewHookTypeWizardPage.class.getName(), Msgs.class);
		}

	}

}