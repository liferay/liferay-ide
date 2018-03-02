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

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.hook.core.operation.INewHookDataModelProperties;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSectionCallback;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

/**
 * @author Greg Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class NewCustomJSPsHookWizardPage extends DataModelWizardPage implements INewHookDataModelProperties {

	public NewCustomJSPsHookWizardPage(IDataModel dataModel, String pageName) {
		super(
			dataModel, pageName, Msgs.createCustomJSPs,
			HookUI.imageDescriptorFromPlugin(HookUI.PLUGIN_ID, "/icons/wizban/hook_wiz.png"));

		setDescription(Msgs.createCustomsJSPFolderSelectJSPs);
	}

	protected void createCustomJSPsGroup(Composite parent) {
		Composite composite = SWTUtil.createTopComposite(parent, 2);

		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		jspItemsSection = new CustomJSPsTableWizardSection(
			composite, Msgs.jspFilesOverride, Msgs.jspFilePath, Msgs.add, Msgs.edit, Msgs.remove,
			new String[] {Msgs.add}, new String[] {Msgs.jspFilePath}, null, getDataModel(), CUSTOM_JSPS_ITEMS);

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);

		gd.heightHint = 175;

		jspItemsSection.setLayoutData(gd);

		jspItemsSection.setCallback(new StringArrayTableWizardSectionCallback());

		IProject project = CoreUtil.getProject(getDataModel().getStringProperty(PROJECT_NAME));

		ILiferayProject liferayProject = LiferayCore.create(project);

		if (liferayProject != null) {
			ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

			if (portal != null) {
				IPath portalDir = portal.getAppServerPortalDir();

				if ((portalDir != null) && portalDir.toFile().exists()) {
					jspItemsSection.setPortalDir(portalDir.toFile());
				}
			}
		}
	}

	protected void createDisableJSPFolderValidation(Composite topComposite) {
		Composite composite = SWTUtil.createTopComposite(topComposite, 3);

		GridLayout gl = new GridLayout(1, false);

		// gl.marginLeft = 5;

		composite.setLayout(gl);

		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		disableJSPFolderValidation = new Button(composite, SWT.CHECK);

		disableJSPFolderValidation.setText(Msgs.disableJSPSyntaxValidation);
		this.synchHelper.synchCheckbox(disableJSPFolderValidation, DISABLE_CUSTOM_JSP_FOLDER_VALIDATION, null);
	}

	protected void createJSPFolderGroup(Composite topComposite) {
		Composite composite = SWTUtil.createTopComposite(topComposite, 3);

		GridLayout gl = new GridLayout(3, false);

		// gl.marginLeft = 5;

		composite.setLayout(gl);

		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		SWTUtil.createLabel(composite, SWT.LEAD, Msgs.customJSPFolder, 1);

		customJSPsFolder = SWTUtil.createText(composite, 1);

		this.synchHelper.synchText(customJSPsFolder, CUSTOM_JSPS_FOLDER, null);

		Button iconFileBrowse = SWTUtil.createPushButton(composite, Msgs.browse, null);

		iconFileBrowse.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					handleFileBrowseButton(NewCustomJSPsHookWizardPage.this.customJSPsFolder);
				}

			});
	}

	protected void createSelectedProjectGroup(Composite topComposite) {
		Composite composite = SWTUtil.createTopComposite(topComposite, 3);

		GridLayout gl = new GridLayout(3, false);

		composite.setLayout(gl);

		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		SWTUtil.createLabel(composite, SWT.LEAD, Msgs.selectedProject, 1);

		selectedProject = SWTUtil.createText(composite, 2);

		selectedProject.setEditable(false);

		this.synchHelper.synchText(selectedProject, SELECTED_PROJECT, null);

		SWTUtil.createLabel(composite, SWT.LEAD, Msgs.webRootFolder, 1);

		webRootFolder = SWTUtil.createText(composite, 2);

		webRootFolder.setEditable(false);

		this.synchHelper.synchText(webRootFolder, WEB_ROOT_FOLDER, null);
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 3);

		createSelectedProjectGroup(topComposite);

		createJSPFolderGroup(topComposite);

		createCustomJSPsGroup(topComposite);

		createDisableJSPFolderValidation(topComposite);

		return topComposite;
	}

	@Override
	protected void enter() {
		super.enter();

		this.synchHelper.synchAllUIWithModel();
	}

	protected ISelectionStatusValidator getContainerDialogSelectionValidator() {
		return new ISelectionStatusValidator() {

			public IStatus validate(Object[] selection) {
				if (ListUtil.isNotEmpty(selection) && (selection[0] != null) &&
					!(selection[0] instanceof IProject) && !(selection[0] instanceof IFile)) {

					return Status.OK_STATUS;
				}

				return HookUI.createErrorStatus(Msgs.chooseValidFolder);
			}

		};
	}

	protected ViewerFilter getContainerDialogViewerFilter() {
		return new ViewerFilter() {

			public boolean select(Viewer viewer, Object parent, Object element) {
				if (element instanceof IProject) {
					IProject project = (IProject)element;

					Object projectName = model.getProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);

					return project.getName().equals(projectName);
				}
				else if (element instanceof IFolder) {
					IFolder folder = (IFolder)element;

					IWebProject webproject = LiferayCore.create(IWebProject.class, folder.getProject());

					if (((webproject != null) && webproject.getDefaultDocrootFolder().contains(folder)) ||
						folder.contains(webproject.getDefaultDocrootFolder())) {

						return true;
					}
				}

				return false;
			}

		};
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] {CUSTOM_JSPS_FOLDER, CUSTOM_JSPS_ITEMS};
	}

	protected void handleFileBrowseButton(Text text) {
		ISelectionStatusValidator validator = getContainerDialogSelectionValidator();

		ViewerFilter filter = getContainerDialogViewerFilter();

		ITreeContentProvider contentProvider = new WorkbenchContentProvider();

		IDecoratorManager decoratorManager = PlatformUI.getWorkbench().getDecoratorManager();

		ILabelProvider labelProvider = new DecoratingLabelProvider(
			new WorkbenchLabelProvider(), decoratorManager.getLabelDecorator());

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), labelProvider, contentProvider);

		dialog.setValidator(validator);
		dialog.setTitle(J2EEUIMessages.CONTAINER_SELECTION_DIALOG_TITLE);
		dialog.setMessage(J2EEUIMessages.CONTAINER_SELECTION_DIALOG_DESC);
		dialog.addFilter(filter);
		dialog.setInput(CoreUtil.getWorkspaceRoot());

		if (dialog.open() == Window.OK) {
			Object element = dialog.getFirstResult();

			try {
				if (element instanceof IFolder) {
					IFolder folder = (IFolder)element;

					IProject project = CoreUtil.getProject(getDataModel().getStringProperty(PROJECT_NAME));

					IWebProject webproject = LiferayCore.create(IWebProject.class, project);

					if (webproject != null) {
						IFolder defaultWebappRootFolder = webproject.getDefaultDocrootFolder();

						if (folder.equals(defaultWebappRootFolder)) {
							folder = folder.getFolder("custom_jsps");
						}

						String defaultWebappRootFolderFullPath =
							defaultWebappRootFolder.getFullPath().toPortableString();

						String folderFullPath = folder.getFullPath().toPortableString();

						int index = folderFullPath.indexOf(defaultWebappRootFolderFullPath);

						if (index != -1) {
							folderFullPath = folderFullPath.substring(index + defaultWebappRootFolderFullPath.length());
						}

						text.setText(folderFullPath);
					}
				}
			}
			catch (Exception ex) {

				// Do nothing

			}
		}
	}

	protected Text customJSPsFolder;
	protected Button disableJSPFolderValidation;
	protected CustomJSPsTableWizardSection jspItemsSection;
	protected Text selectedProject;
	protected Text webRootFolder;

	private static class Msgs extends NLS {

		public static String add;
		public static String browse;
		public static String chooseValidFolder;
		public static String createCustomJSPs;
		public static String createCustomsJSPFolderSelectJSPs;
		public static String customJSPFolder;
		public static String disableJSPSyntaxValidation;
		public static String edit;
		public static String jspFilePath;
		public static String jspFilesOverride;
		public static String remove;
		public static String selectedProject;
		public static String webRootFolder;

		static {
			initializeMessages(NewCustomJSPsHookWizardPage.class.getName(), Msgs.class);
		}

	}

}