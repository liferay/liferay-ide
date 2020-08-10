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
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSection;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Gregory Amerson
 */
public class PropertyOverridesTableWizardSection extends StringArrayTableWizardSection {

	public PropertyOverridesTableWizardSection(
		Composite parent, String componentLabel, String dialogTitle, String addButtonLabel, String editButtonLabel,
		String removeButtonLabel, String[] columnTitles, String[] fieldLabels, Image labelProviderImage,
		IDataModel model, String propertyName) {

		super(
			parent, componentLabel, dialogTitle, addButtonLabel, editButtonLabel, removeButtonLabel, columnTitles,
			fieldLabels, labelProviderImage, model, propertyName);

		buttonLabels = new String[] {Msgs.select, null};
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public class AddPropertyOverrideDialog extends AddStringArrayDialog {

		public AddPropertyOverrideDialog(
			Shell shell, String windowTitle, String[] labelsForTextField, String[] buttonLabels) {

			super(shell, windowTitle, labelsForTextField);

			setShellStyle(getShellStyle() | SWT.RESIZE);

			this.buttonLabels = buttonLabels;

			setWidthHint(450);
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

			if (buttonLabels[index] != null) {
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
			}

			return text;
		}

		protected void handleArrayDialogButtonSelected(int index, String label, Text text) {
			if (index == 0) {
				handleSelectPropertyButton(text);
			}
		}

		protected void handleSelectPropertyButton(Text text) {
			String[] hookProperties = {};

			final ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

			if (liferayProject != null) {
				final ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

				if (portal != null) {
					hookProperties = portal.getHookSupportedProperties();
				}
			}

			PropertiesFilteredDialog dialog = new PropertiesFilteredDialog(getParentShell());

			dialog.setTitle(Msgs.propertySelection);
			dialog.setMessage(Msgs.selectProperty);
			dialog.setInput(hookProperties);

			if (dialog.open() == Window.OK) {
				Object[] selected = dialog.getResult();

				text.setText(selected[0].toString());
			}
		}

		protected String[] buttonLabels;

	}

	@Override
	protected void handleAddButtonSelected() {
		AddPropertyOverrideDialog dialog = new AddPropertyOverrideDialog(
			getShell(), dialogTitle, fieldLabels, buttonLabels);

		if (dialog.open() == Window.OK) {
			addStringArray(dialog.getStringArray());
		}
	}

	protected String[] buttonLabels;
	protected IProject project;

	private static class Msgs extends NLS {

		public static String propertySelection;
		public static String select;
		public static String selectProperty;

		static {
			initializeMessages(PropertyOverridesTableWizardSection.class.getName(), Msgs.class);
		}

	}

}