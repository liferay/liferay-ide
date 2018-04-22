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

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSection;
import com.liferay.ide.ui.dialog.FilteredTypesSelectionDialogEx;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class ServicesTableWizardSection extends StringArrayTableWizardSection {

	public ServicesTableWizardSection(
		Composite parent, String componentLabel, String dialogTitle, String addButtonLabel, String editButtonLabel,
		String removeButtonLabel, String[] columnTitles, String[] fieldLabels, Image labelProviderImage,
		IDataModel model, String propertyName) {

		super(
			parent, componentLabel, dialogTitle, addButtonLabel, editButtonLabel, removeButtonLabel, columnTitles,
			fieldLabels, labelProviderImage, model, propertyName);

		buttonLabels = new String[] {Msgs.select, Msgs.selectNew};

		servicesPropertiesFile = null;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public class AddServiceDialog extends AddStringArrayDialog {

		public AddServiceDialog(Shell shell, String windowTitle, String[] labelsForTextField, String[] buttonLabels) {
			super(shell, windowTitle, labelsForTextField);

			setShellStyle(getShellStyle() | SWT.RESIZE);

			this.buttonLabels = buttonLabels;

			setWidthHint(450);
		}

		@Override
		public Control createDialogArea(Composite parent) {
			super.createDialogArea(parent);

			ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();

			errorMessageLabel = new CLabel(parent, SWT.LEFT_TO_RIGHT);

			errorMessageLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
			errorMessageLabel.setImage(sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
			errorMessageLabel.setVisible(false);

			return parent;
		}

		@Override
		public void modifyText(ModifyEvent e) {
			boolean serviceTypeValid = false;
			boolean implClassValid = false;

			String text0 = texts[0].getText().trim();

			if (text0.length() > 0) {
				IStatus status = JavaConventions.validateJavaTypeName(
					text0, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

				int serviceTypeStatus = status.getSeverity();

				serviceTypeValid = (serviceTypeStatus != IStatus.ERROR) ? true : false;
			}

			String text1 = texts[1].getText().trim();

			if (text1.length() > 0) {
				IStatus status = JavaConventions.validateJavaTypeName(
					text1, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

				int implClasseStatus = status.getSeverity();

				implClassValid = (implClasseStatus != IStatus.ERROR) ? true : false;
			}

			if (!(serviceTypeValid ^ implClassValid)) {
				errorMessageLabel.setText("Invalid service name and class name");
			}
			else if (!serviceTypeValid) {
				errorMessageLabel.setText("Invalid service type name");
			}
			else if (!implClassValid) {
				errorMessageLabel.setText("Invalid class name");
			}

			this.errorMessageLabel.setVisible(!(serviceTypeValid && implClassValid));
			getButton(IDialogConstants.OK_ID).setEnabled(serviceTypeValid && implClassValid);
		}

		@Override
		protected Control createContents(Composite parent) {
			Composite composite = (Composite)super.createContents(parent);
			getButton(IDialogConstants.OK_ID).setEnabled(false);

			return composite;
		}

		@Override
		protected Text createField(Composite parent, int index) {
			Label label = new Label(parent, SWT.LEFT);

			label.setText(labelsForTextField[index]);
			label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

			// Composite composite = new Composite(parent, SWT.NONE);
			// GridData data = new GridData(GridData.FILL_HORIZONTAL);
			// composite.setLayoutData(data);
			// composite.setLayout(new GridLayout(2, false));

			Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);

			GridData data = new GridData(GridData.FILL_HORIZONTAL);

			// data.widthHint = 200;

			text.setLayoutData(data);

			Composite buttonComposite = new Composite(parent, SWT.NONE);

			String[] buttonLbls = buttonLabels[index].split(",");

			GridLayout gl = new GridLayout(buttonLbls.length, true);

			gl.marginWidth = 0;
			gl.horizontalSpacing = 1;

			buttonComposite.setLayout(gl);

			for (String lbl : buttonLbls) {
				Button button = new Button(buttonComposite, SWT.PUSH);

				button.setText(lbl);

				button.addSelectionListener(
					new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							handleArrayDialogButtonSelected(index, lbl, text);
						}

					});
			}

			return text;
		}

		protected void handleArrayDialogButtonSelected(int index, String label, Text text) {
			if (index == 0) {
				handleSelectServiceButton(text);
			}
			else if ((index == 1) && Msgs.select.equals(label)) {
				handleSelectImplClassButton(text);
			}
			else if ((index == 1) && Msgs.newLabel.equals(label)) {
				handleNewImplClassButton(text);
			}
		}

		protected void handleNewImplClassButton(Text text) {
			if (CoreUtil.isNullOrEmpty(texts[0].getText())) {
				MessageDialog.openWarning(getParentShell(), Msgs.addService, Msgs.specifyServiceType);

				return;
			}

			String serviceType = texts[0].getText();

			String wrapperType = StringPool.EMPTY;

			if (serviceType.endsWith("Service")) {
				wrapperType = serviceType + "Wrapper";
			}

			NewEventActionClassDialog dialog = new NewServiceWrapperClassDialog(
				getShell(), model, serviceType, wrapperType);

			if (dialog.open() == Window.OK) {
				String qualifiedClassname = dialog.getQualifiedClassname();

				text.setText(qualifiedClassname);
			}
		}

		protected void handleSelectImplClassButton(Text text) {
			if (CoreUtil.isNullOrEmpty(texts[0].getText())) {
				MessageDialog.openWarning(getParentShell(), Msgs.addService, Msgs.specifyServiceType);

				return;
			}

			IPackageFragmentRoot packRoot = (IPackageFragmentRoot)model.getProperty(
				INewJavaClassDataModelProperties.JAVA_PACKAGE_FRAGMENT_ROOT);

			if (packRoot == null) {
				return;
			}

			IJavaSearchScope scope = null;

			try {

				// get the Service type and replace Service with Wrapper and
				// make it the supertype

				String serviceType = texts[0].getText();

				if (serviceType.endsWith("Service")) {
					String wrapperType = serviceType + "Wrapper";

					scope = BasicSearchEngine.createHierarchyScope(packRoot.getJavaProject().findType(wrapperType));
				}
			}
			catch (JavaModelException jme) {
				HookUI.logError(jme);

				return;
			}

			FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialogEx(
				getShell(), false, null, scope, IJavaSearchConstants.CLASS);

			dialog.setTitle(J2EEUIMessages.SUPERCLASS_SELECTION_DIALOG_TITLE);
			dialog.setMessage(J2EEUIMessages.SUPERCLASS_SELECTION_DIALOG_DESC);

			if (dialog.open() == Window.OK) {
				IType type = (IType)dialog.getFirstResult();

				String classFullPath = J2EEUIMessages.EMPTY_STRING;

				if (type != null) {
					classFullPath = type.getFullyQualifiedName();
				}

				text.setText(classFullPath);
			}
		}

		protected void handleSelectServiceButton(Text text) {
			PortalServiceSearchScope scope = new PortalServiceSearchScope();

			scope.setResourcePattern(new String[] {".*Service.class$"});

			IProject project = ProjectUtil.getProject(model);

			IWebProject liferayProject = LiferayCore.create(IWebProject.class, project);

			IPath serviceJarPathService = liferayProject.getLibraryPath("portal-service");

			IPath serviceJarPathKernel = liferayProject.getLibraryPath("portal-kernel");

			scope.setEnclosingJarPaths(
				new IPath[] {serviceJarPathService != null ? serviceJarPathService : serviceJarPathKernel});

			FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialogEx(
				getShell(), false, null, scope, IJavaSearchConstants.INTERFACE);

			dialog.setTitle(J2EEUIMessages.SUPERCLASS_SELECTION_DIALOG_TITLE);
			dialog.setMessage(J2EEUIMessages.SUPERCLASS_SELECTION_DIALOG_DESC);

			if (dialog.open() == Window.OK) {
				IType type = (IType)dialog.getFirstResult();

				String classFullPath = J2EEUIMessages.EMPTY_STRING;

				if (type != null) {
					classFullPath = type.getFullyQualifiedName();
				}

				text.setText(classFullPath);
			}
		}

		protected String[] buttonLabels;
		protected CLabel errorMessageLabel;

	}

	public class EditServiceDialog extends EditStringArrayDialog {

		public EditServiceDialog(
			Shell shell, String windowTitle, String[] labelsForTextField, String[] valuesForTextField) {

			super(shell, windowTitle, labelsForTextField, valuesForTextField);
		}

		@Override
		public Control createDialogArea(Composite parent) {
			super.createDialogArea(parent);

			ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();

			errorMessageLabel = new CLabel(parent, SWT.LEFT_TO_RIGHT);

			errorMessageLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
			errorMessageLabel.setImage(sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
			errorMessageLabel.setVisible(false);

			return parent;
		}

		@Override
		public void modifyText(ModifyEvent e) {
			boolean serviceTypeValid = false;
			boolean implClassValid = false;

			String text0 = texts[0].getText().trim();

			if (text0.length() > 0) {
				IStatus status = JavaConventions.validateJavaTypeName(
					text0, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

				int serviceTypeStatus = status.getSeverity();

				serviceTypeValid = (serviceTypeStatus != IStatus.ERROR) ? true : false;
			}

			String text1 = texts[1].getText().trim();

			if (text1.length() > 0) {
				IStatus status = JavaConventions.validateJavaTypeName(
					text1, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

				int implClasseStatus = status.getSeverity();

				implClassValid = (implClasseStatus != IStatus.ERROR) ? true : false;
			}

			if (!(serviceTypeValid ^ implClassValid)) {
				errorMessageLabel.setText("Invalid service name and class name");
			}
			else if (!serviceTypeValid) {
				errorMessageLabel.setText("Invalid service type name");
			}
			else if (!implClassValid) {
				errorMessageLabel.setText("Invalid class name");
			}

			this.errorMessageLabel.setVisible(!(serviceTypeValid && implClassValid));

			getButton(IDialogConstants.OK_ID).setEnabled(serviceTypeValid && implClassValid);
		}

		protected CLabel errorMessageLabel;

	}

	@Override
	protected void handleAddButtonSelected() {
		AddServiceDialog dialog = new AddServiceDialog(getShell(), dialogTitle, fieldLabels, buttonLabels);

		if (dialog.open() == Window.OK) {
			String[] stringArray = dialog.getStringArray();

			addStringArray(stringArray);
		}
	}

	@Override
	protected void handleEditButtonSelected() {
		ISelection s = viewer.getSelection();

		if (!(s instanceof IStructuredSelection)) {
			return;
		}

		IStructuredSelection selection = (IStructuredSelection)s;

		if (selection.size() != 1) {
			return;
		}

		Object selectedObj = selection.getFirstElement();

		String[] valuesForText = (String[])selectedObj;

		EditServiceDialog dialog = new EditServiceDialog(getShell(), dialogTitle, fieldLabels, valuesForText);

		dialog.open();

		String[] stringArray = dialog.getStringArray();

		editStringArray(valuesForText, stringArray);
	}

	protected String[] buttonLabels;
	protected IProject project;
	protected File servicesPropertiesFile;

	private static class Msgs extends NLS {

		public static String addService;
		public static String newLabel;
		public static String select;
		public static String selectNew;
		public static String specifyServiceType;

		static {
			initializeMessages(ServicesTableWizardSection.class.getName(), Msgs.class);
		}

	}

}