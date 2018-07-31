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
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSection;
import com.liferay.ide.ui.dialog.FilteredTypesSelectionDialogEx;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class EventActionsTableWizardSection extends StringArrayTableWizardSection {

	public EventActionsTableWizardSection(
		Composite parent, String componentLabel, String dialogTitle, String addButtonLabel, String editButtonLabel,
		String removeButtonLabel, String[] columnTitles, String[] fieldLabels, Image labelProviderImage,
		IDataModel model, String propertyName) {

		super(
			parent, componentLabel, dialogTitle, addButtonLabel, editButtonLabel, removeButtonLabel, columnTitles,
			fieldLabels, labelProviderImage, model, propertyName);

		buttonLabels = new String[] {Msgs.select, Msgs.selectNew};
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public class EditEventActionDialog extends EditStringArrayDialog {

		public EditEventActionDialog(
			Shell shell, String windowTitle, String[] labelsForTextField, String[] valuesForTextField) {

			super(shell, windowTitle, labelsForTextField, valuesForTextField);
		}

		@Override
		public Control createDialogArea(Composite parent) {
			super.createDialogArea(parent);

			ISharedImages sharedImage = UIUtil.getSharedImages();

			errorMessageLabel = new CLabel(parent, SWT.LEFT_TO_RIGHT);

			errorMessageLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
			errorMessageLabel.setImage(sharedImage.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
			errorMessageLabel.setVisible(false);

			return parent;
		}

		@Override
		public void modifyText(ModifyEvent e) {
			boolean classNameValid = false;

			String test = StringUtil.trim(texts[1].getText());

			if (test.length() > 0) {
				IStatus status = JavaConventions.validateJavaTypeName(
					test, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

				int classNameSeverity = status.getSeverity();

				classNameValid = (classNameSeverity != IStatus.ERROR) ? true : false;
			}

			if (!classNameValid) {
				errorMessageLabel.setText("Invalid class name");
			}

			this.errorMessageLabel.setVisible(!classNameValid);

			getButton(IDialogConstants.OK_ID).setEnabled(classNameValid);
		}

		protected CLabel errorMessageLabel;

	}

	@Override
	protected void handleAddButtonSelected() {
		AddEventActionDialog dialog = new AddEventActionDialog(getShell(), dialogTitle, fieldLabels, buttonLabels);

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

		EditEventActionDialog dialog = new EditEventActionDialog(getShell(), dialogTitle, fieldLabels, valuesForText);

		dialog.open();

		String[] stringArray = dialog.getStringArray();

		editStringArray(valuesForText, stringArray);
	}

	protected String[] buttonLabels;
	protected IProject project;

	protected class AddEventActionDialog extends AddStringArrayDialog {

		public AddEventActionDialog(
			Shell shell, String windowTitle, String[] labelsForTextField, String[] buttonLabels) {

			super(shell, windowTitle, labelsForTextField);

			setShellStyle(getShellStyle() | SWT.RESIZE);

			this.buttonLabels = buttonLabels;

			setWidthHint(450);
		}

		@Override
		public Control createDialogArea(Composite parent) {
			super.createDialogArea(parent);

			ISharedImages images = UIUtil.getSharedImages();

			errorMessageLabel = new CLabel(parent, SWT.LEFT_TO_RIGHT);

			errorMessageLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
			errorMessageLabel.setImage(images.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
			errorMessageLabel.setVisible(false);

			return parent;
		}

		@Override
		public void modifyText(ModifyEvent e) {
			boolean classNameValid = false;

			String text = StringUtil.trim(texts[1].getText());

			if (text.length() > 0) {
				IStatus status = JavaConventions.validateJavaTypeName(
					text, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

				int classNameSeverity = status.getSeverity();

				classNameValid = (classNameSeverity != IStatus.ERROR) ? true : false;
			}

			if (!classNameValid) {
				errorMessageLabel.setText("Invalid class name");
			}

			this.errorMessageLabel.setVisible(!classNameValid);

			getButton(IDialogConstants.OK_ID).setEnabled(classNameValid);
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

			Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);

			GridData data = new GridData(GridData.FILL_HORIZONTAL);

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
				handleSelectEventButton(text);
			}
			else if ((index == 1) && Msgs.select.equals(label)) {
				handleSelectClassButton(text);
			}
			else if ((index == 1) && Msgs.newLabel.equals(label)) {
				handleNewClassButton(text);
			}
		}

		protected void handleNewClassButton(Text text) {
			NewEventActionClassDialog dialog = new NewEventActionClassDialog(getShell(), model);

			if (dialog.open() == Window.OK) {
				String qualifiedClassname = dialog.getQualifiedClassname();

				text.setText(qualifiedClassname);
			}
		}

		protected void handleSelectClassButton(Text text) {
			Control control = text;

			IJavaSearchScope scope = null;

			try {
				String projectName = model.getStringProperty(INewJavaClassDataModelProperties.PROJECT_NAME);

				IProject project = CoreUtil.getProject(projectName);

				scope = BasicSearchEngine.createJavaSearchScope(new IJavaElement[] {JavaCore.create(project)});
			}
			catch (Exception e) {
				HookUI.logError(e);

				return;
			}

			/*
			 * This includes all entries on the classpath.
			 * This behavior is identical to the Super Class Browse Button on the Create new Java Class Wizard.
			 */
			FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialogEx(
				getShell(), false, null, scope, IJavaSearchConstants.CLASS);

			dialog.setTitle(Msgs.eventSelection);
			dialog.setMessage(Msgs.selectEventAction);

			if (dialog.open() == Window.OK) {
				IType type = (IType)dialog.getFirstResult();

				String classFullPath = J2EEUIMessages.EMPTY_STRING;

				if (type != null) {
					classFullPath = type.getFullyQualifiedName();
				}

				if (control instanceof Text) {
					((Text)control).setText(classFullPath);
				}
				else if (control instanceof Combo) {
					((Combo)control).setText(classFullPath);
				}

				return;
			}
		}

		protected void handleSelectEventButton(Text text) {
			String[] hookProperties = {};

			ILiferayProject liferayProject = LiferayCore.create(project);

			if (liferayProject != null) {
				ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

				if (portal != null) {
					hookProperties = portal.getHookSupportedProperties();
				}
			}

			PropertiesFilteredDialog dialog = new PropertiesFilteredDialog(getParentShell(), ".*events.*");

			dialog.setTitle(Msgs.propertySelection);
			dialog.setMessage(Msgs.selectProperty);
			dialog.setInput(hookProperties);

			if (dialog.open() == Window.OK) {
				Object[] selected = dialog.getResult();

				text.setText(selected[0].toString());
			}
		}

		protected String[] buttonLabels;
		protected CLabel errorMessageLabel;

	}

	private static class Msgs extends NLS {

		public static String eventSelection;
		public static String newLabel;
		public static String propertySelection;
		public static String select;
		public static String selectEventAction;
		public static String selectNew;
		public static String selectProperty;

		static {
			initializeMessages(EventActionsTableWizardSection.class.getName(), Msgs.class);
		}

	}

}