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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.BinaryProjectRecord;
import com.liferay.ide.project.core.ISDKProjectsImportDataModelProperties;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.ui.util.SWTUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.web.ui.internal.wizards.DataModelFacetCreationWizardPage;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings("restriction")
public class BinaryProjectImportWizardPage
	extends DataModelFacetCreationWizardPage implements ISDKProjectsImportDataModelProperties {

	public BinaryProjectImportWizardPage(IDataModel model, String pageName) {
		super(model, pageName);

		setTitle(Msgs.importLiferayBinaryPlugin);
		setDescription(Msgs.selectBinaryPlugin);
	}

	protected void createBinaryLocationField(Composite parent) {
		Label label = new Label(parent, SWT.NONE);

		label.setText(Msgs.binaryPluginFile);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		binariesLocation = SWTUtil.createSingleText(parent, 1);

		binariesLocation.addModifyListener(
			new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					if (binariesLocation.isFocusControl() && CoreUtil.isNullOrEmpty(binariesLocation.getText())) {
						setErrorMessage("Select a binary to import.");
						getDataModel().setProperty(SELECTED_PROJECTS, null);
					}
					else {
						File binaryFile = new File(binariesLocation.getText());

						if (ProjectImportUtil.isValidLiferayPlugin(binaryFile)) {
							selectedBinary = new BinaryProjectRecord(new File(binariesLocation.getText()));

							getDataModel().setProperty(SELECTED_PROJECTS, new Object[] {selectedBinary});
						}
						else {
							setErrorMessage(Msgs.selectValidLiferayPluginBinary);
							getDataModel().setProperty(SELECTED_PROJECTS, null);
						}
					}
				}

			});

		Button browse = SWTUtil.createButton(parent, Msgs.browse);

		browse.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					doBrowse();
				}

			});
	}

	protected void createPluginsSDKField(Composite parent) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				synchHelper.synchAllUIWithModel();
				validatePage(true);
			}

		};

		new LiferaySDKField(
			parent, getDataModel(), selectionAdapter, LIFERAY_SDK_NAME, synchHelper, Msgs.selectSDKLabel);
	}

	protected void createSDKLocationField(Composite topComposite) {
		SWTUtil.createLabel(topComposite, SWT.LEAD, Msgs.liferayPluginSDKLocationLabel, 1);

		sdkLocation = SWTUtil.createText(topComposite, 1);

		((GridData)sdkLocation.getLayoutData()).widthHint = 300;

		synchHelper.synchText(sdkLocation, SDK_LOCATION, null);

		SWTUtil.createLabel(topComposite, SWT.LEAD, StringPool.EMPTY, 1);
	}

	protected void createSDKVersionField(Composite topComposite) {
		SWTUtil.createLabel(topComposite, SWT.LEAD, Msgs.liferayPluginSDKVersionLabel, 1);

		sdkVersion = SWTUtil.createText(topComposite, 1);

		synchHelper.synchText(sdkVersion, SDK_VERSION, null);

		SWTUtil.createLabel(topComposite, StringPool.EMPTY, 1);
	}

	protected void createTargetRuntimeGroup(Composite parent) {
		Label label = new Label(parent, SWT.NONE);

		label.setText(Msgs.liferayTargetRuntimeLabel);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		serverTargetCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);

		serverTargetCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button newServerTargetButton = new Button(parent, SWT.NONE);

		newServerTargetButton.setText(Msgs.newButton);
		newServerTargetButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					final DataModelPropertyDescriptor[] preAdditionDescriptors = model.getValidPropertyDescriptors(
						FACET_RUNTIME);

					boolean oK = ServerUIUtil.showNewRuntimeWizard(getShell(), getModuleTypeID(), null, "com.liferay.");

					if (oK) {
						DataModelPropertyDescriptor[] postAdditionDescriptors = model.getValidPropertyDescriptors(
							FACET_RUNTIME);

						Object[] preAddition = new Object[preAdditionDescriptors.length];

						for (int i = 0; i < preAddition.length; i++) {
							preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
						}

						Object[] postAddition = new Object[postAdditionDescriptors.length];

						for (int i = 0; i < postAddition.length; i++) {
							postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
						}

						Object newAddition = CoreUtil.getNewObject(preAddition, postAddition);

						if (newAddition != null) {
							model.setProperty(FACET_RUNTIME, newAddition);
						}
					}
				}

			});

		Control[] deps = {newServerTargetButton};

		synchHelper.synchCombo(serverTargetCombo, FACET_RUNTIME, deps);

		if ((serverTargetCombo.getSelectionIndex() == -1) && (serverTargetCombo.getVisibleItemCount() != 0)) {
			serverTargetCombo.select(0);
		}
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 3);

		GridLayout gl = new GridLayout(3, false);

		// gl.marginLeft = 5;

		topComposite.setLayout(gl);

		topComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		createBinaryLocationField(topComposite);

		createPluginsSDKField(topComposite);

		SWTUtil.createVerticalSpacer(topComposite, 1, 3);

		createSDKLocationField(topComposite);
		createSDKVersionField(topComposite);

		SWTUtil.createVerticalSpacer(topComposite, 1, 3);

		createTargetRuntimeGroup(topComposite);

		return topComposite;
	}

	protected void doBrowse() {
		FileDialog fd = new FileDialog(getShell(), SWT.OPEN);

		fd.setFilterExtensions(ISDKConstants.BINARY_PLUGIN_EXTENSIONS);

		String filterPath = binariesLocation.getText();

		if (filterPath != null) {
			fd.setFilterPath(filterPath);
			fd.setText(NLS.bind(Msgs.selectLiferayPluginBinaryFolderPath, filterPath));
		}
		else {
			fd.setText(Msgs.selectLiferayPluginBinaryFolder);
		}

		if (CoreUtil.isNullOrEmpty(binariesLocation.getText())) {
			fd.setFilterPath(binariesLocation.getText());
		}

		String binaryfile = fd.open();

		if (!CoreUtil.isNullOrEmpty(binaryfile)) {
			binariesLocation.setText(binaryfile);
			File binaryFile = new File(binaryfile);

			if (ProjectImportUtil.isValidLiferayPlugin(binaryFile)) {
				selectedBinary = new BinaryProjectRecord(new File(binaryfile));

				getDataModel().setProperty(SELECTED_PROJECTS, new Object[] {selectedBinary});
			}
			else {
				setErrorMessage(Msgs.selectValidLiferayPluginBinary);
			}
		}
	}

	protected IProject[] getProjectsInWorkspace() {
		if (wsProjects == null) {
			IWorkspace pluginWorkspace = IDEWorkbenchPlugin.getPluginWorkspace();

			IWorkspaceRoot root = pluginWorkspace.getRoot();

			wsProjects = root.getProjects();
		}

		return wsProjects;
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] {SDK_LOCATION, SDK_VERSION, SELECTED_PROJECTS, FACET_RUNTIME};
	}

	protected void handleFileBrowseButton(final Text text) {
		DirectoryDialog dd = new DirectoryDialog(getShell(), SWT.OPEN);

		dd.setText(Msgs.selectLiferayPluginSDKFolder);

		if (!CoreUtil.isNullOrEmpty(sdkLocation.getText())) {
			dd.setFilterPath(sdkLocation.getText());
		}

		String dir = dd.open();

		if (!CoreUtil.isNullOrEmpty(dir)) {
			sdkLocation.setText(dir);

			synchHelper.synchAllUIWithModel();

			validatePage();
		}
	}

	protected boolean isProjectInWorkspace(String projectName) {
		if (projectName == null) {
			return false;
		}

		IProject[] workspaceProjects = getProjectsInWorkspace();

		for (IProject project : workspaceProjects) {
			if (FileUtil.nameEquals(project, projectName)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean showValidationErrorsOnEnter() {
		return true;
	}

	protected Text binariesLocation;
	protected long lastModified;
	protected String lastPath;
	protected Text sdkLocation;
	protected Text sdkVersion;
	protected BinaryProjectRecord selectedBinary;
	protected Combo serverTargetCombo;
	protected IProject[] wsProjects;

	private static class Msgs extends NLS {

		public static String binaryPluginFile;
		public static String browse;
		public static String importLiferayBinaryPlugin;
		public static String liferayPluginSDKLocationLabel;
		public static String liferayPluginSDKVersionLabel;
		public static String liferayTargetRuntimeLabel;
		public static String newButton;
		public static String selectBinaryPlugin;
		public static String selectLiferayPluginBinaryFolder;
		public static String selectLiferayPluginBinaryFolderPath;
		public static String selectLiferayPluginSDKFolder;
		public static String selectSDKLabel;
		public static String selectValidLiferayPluginBinary;

		static {
			initializeMessages(BinaryProjectImportWizardPage.class.getName(), Msgs.class);
		}

	}

}