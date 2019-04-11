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
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.hook.core.operation.INewHookDataModelProperties;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSectionCallback;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
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
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class NewPortalPropertiesHookWizardPage extends DataModelWizardPage implements INewHookDataModelProperties {

	public NewPortalPropertiesHookWizardPage(IDataModel dataModel, String pageName) {
		super(
			dataModel, pageName, Msgs.createPortalProperties,
			HookUI.imageDescriptorFromPlugin(HookUI.PLUGIN_ID, "/icons/wizban/hook_wiz.png"));

		setDescription(Msgs.specifyPortalProperties);
	}

	protected void createEventActionsGroup(Composite topComposite) {
		Composite composite = SWTUtil.createTopComposite(topComposite, 2);

		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		String[] titles = {Msgs.eventTitle, Msgs.classTitle};
		String[] labels = {Msgs.eventLabel, Msgs.classLabel};

		eventActionsSection = new EventActionsTableWizardSection(
			composite, Msgs.defineActions, Msgs.addEventAction, Msgs.add, Msgs.edit, Msgs.remove, titles, labels, null,
			getDataModel(), PORTAL_PROPERTIES_ACTION_ITEMS);

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);

		gd.heightHint = 150;

		eventActionsSection.setLayoutData(gd);

		eventActionsSection.setCallback(new StringArrayTableWizardSectionCallback());

		IProject project = CoreUtil.getProject(getDataModel().getStringProperty(PROJECT_NAME));

		if (project != null) {
			eventActionsSection.setProject(project);
		}
	}

	protected void createPortalPropertiesFileGroup(Composite topComposite) {
		Composite composite = SWTUtil.createTopComposite(topComposite, 3);

		GridLayout gl = new GridLayout(3, false);

		gl.marginLeft = 5;

		composite.setLayout(gl);

		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		SWTUtil.createLabel(composite, SWT.LEAD, Msgs.portalPropertiesFileLabel, 1);

		portalPropertiesFile = SWTUtil.createText(composite, 1);

		this.synchHelper.synchText(portalPropertiesFile, PORTAL_PROPERTIES_FILE, null);

		Button iconFileBrowse = SWTUtil.createPushButton(composite, Msgs.browse, null);

		iconFileBrowse.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					handleBrowseButton(NewPortalPropertiesHookWizardPage.this.portalPropertiesFile);
				}

			});
	}

	protected void createPropertiesOverridesGroup(Composite topComposite) {
		Composite composite = SWTUtil.createTopComposite(topComposite, 2);

		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		String[] titles = {Msgs.propertyTitle, Msgs.valueTitle};
		String[] labels = {Msgs.propertyLabel, Msgs.valueLabel};

		propertyOverridesSection = new PropertyOverridesTableWizardSection(
			composite, Msgs.specifyProperties, Msgs.addPropertyOverride, Msgs.add, Msgs.edit, Msgs.remove, titles,
			labels, null, getDataModel(), PORTAL_PROPERTIES_OVERRIDE_ITEMS);

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);

		gd.heightHint = 150;

		propertyOverridesSection.setLayoutData(gd);

		propertyOverridesSection.setCallback(new StringArrayTableWizardSectionCallback());

		IProject project = CoreUtil.getProject(getDataModel().getStringProperty(PROJECT_NAME));

		if (project != null) {
			propertyOverridesSection.setProject(project);
		}
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 3);

		topComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		createPortalPropertiesFileGroup(topComposite);

		createEventActionsGroup(topComposite);

		createPropertiesOverridesGroup(topComposite);

		return topComposite;
	}

	protected ISelectionStatusValidator getContainerDialogSelectionValidator() {
		return new ISelectionStatusValidator() {

			public IStatus validate(Object[] selection) {
				if (ListUtil.isNotEmpty(selection) && (selection[0] != null) && !(selection[0] instanceof IProject)) {
					return Status.OK_STATUS;
				}

				return HookUI.createErrorStatus(Msgs.chooseValidFileFolder);
			}

		};
	}

	protected ViewerFilter getContainerDialogViewerFilter() {
		return new ViewerFilter() {

			@SuppressWarnings("deprecation")
			public boolean select(Viewer viewer, Object parent, Object element) {
				if (element instanceof IProject) {
					IProject project = (IProject)element;

					Object property = model.getProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);

					return StringUtil.equals(project.getName(), property);
				}
				else if (element instanceof IFolder) {
					IFolder folder = (IFolder)element;

					// only show source folders

					IProject project = CoreUtil.getProject(
						model.getStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME));

					IPackageFragmentRoot[] sourceFolders = J2EEProjectUtilities.getSourceContainers(project);

					for (IPackageFragmentRoot sourceFolder : sourceFolders) {
						IResource resource = sourceFolder.getResource();

						if ((resource != null) && resource.equals(folder)) {
							return true;
						}
						else if (ProjectUtil.isParent(folder, resource)) {
							return true;
						}
					}
				}
				else if (element instanceof IFile) {
					IFile file = (IFile)element;

					if (FileUtil.exists(file) && StringUtil.equals(file.getName(), "portal.properties")) {
						return true;
					}

					return false;
				}

				return false;
			}

		};
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] {PORTAL_PROPERTIES_FILE, PORTAL_PROPERTIES_ACTION_ITEMS, PORTAL_PROPERTIES_OVERRIDE_ITEMS};
	}

	protected void handleBrowseButton(Text text) {
		ISelectionStatusValidator validator = getContainerDialogSelectionValidator();

		ViewerFilter filter = getContainerDialogViewerFilter();

		ITreeContentProvider contentProvider = new WorkbenchContentProvider();

		IDecoratorManager decoratorManager = UIUtil.getDecoratorManager();

		ILabelProvider labelProvider = new DecoratingLabelProvider(
			new WorkbenchLabelProvider(), decoratorManager.getLabelDecorator());

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), labelProvider, contentProvider);

		dialog.setValidator(validator);
		dialog.setTitle(Msgs.portalPropertiesFile);
		dialog.setMessage(Msgs.portalPropertiesFile);
		dialog.addFilter(filter);
		dialog.setInput(CoreUtil.getWorkspaceRoot());

		if (dialog.open() == Window.OK) {
			Object element = dialog.getFirstResult();

			try {
				if (element instanceof IFile) {
					IFile file = (IFile)element;

					text.setText(FileUtil.toPortableString(file.getFullPath()));
				}
				else if (element instanceof IFolder) {
					IFolder folder = (IFolder)element;

					IPath fullPath = folder.getFullPath();

					IPath portalPropertiesPath = fullPath.append("portal.properties");

					text.setText(portalPropertiesPath.toPortableString());
				}
			}
			catch (Exception ex) {

				// Do nothing

			}
		}
	}

	protected EventActionsTableWizardSection eventActionsSection;
	protected Text portalPropertiesFile;
	protected PropertyOverridesTableWizardSection propertyOverridesSection;

	private static class Msgs extends NLS {

		public static String add;
		public static String addEventAction;
		public static String addPropertyOverride;
		public static String browse;
		public static String chooseValidFileFolder;
		public static String classLabel;
		public static String classTitle;
		public static String createPortalProperties;
		public static String defineActions;
		public static String edit;
		public static String eventLabel;
		public static String eventTitle;
		public static String portalPropertiesFile;
		public static String portalPropertiesFileLabel;
		public static String propertyLabel;
		public static String propertyTitle;
		public static String remove;
		public static String specifyPortalProperties;
		public static String specifyProperties;
		public static String valueLabel;
		public static String valueTitle;

		static {
			initializeMessages(NewPortalPropertiesHookWizardPage.class.getName(), Msgs.class);
		}

	}

}