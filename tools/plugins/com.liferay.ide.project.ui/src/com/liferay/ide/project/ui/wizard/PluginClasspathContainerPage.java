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

import com.liferay.ide.project.core.PluginClasspathContainerInitializer;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class PluginClasspathContainerPage
	extends NewElementWizardPage implements IClasspathContainerPage, IClasspathContainerPageExtension {

	public PluginClasspathContainerPage() {
		super("PluginClasspathContainerPage");

		setTitle(Msgs.liferayPluginAPILibrary);
		setDescription(Msgs.containerManagesClasspathEntries);
	}

	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);

		composite.setLayout(new GridLayout(2, false));

		final Label label = new Label(composite, SWT.NONE);

		label.setText(Msgs.liferayPluginTypeLabel);

		final String[] types = {"portlet", "hook", "ext", "theme", "web"};

		_typeCombo = new Combo(composite, SWT.READ_ONLY);

		_typeCombo.setItems(types);

		final int index;

		if (_type != null) {
			index = _indexOf(types, _type);
		}
		else {
			if (ProjectUtil.isPortletProject(_ownerProject)) {
				index = 0;
			}
			else if (ProjectUtil.isHookProject(_ownerProject)) {
				index = 1;
			}
			else if (ProjectUtil.isExtProject(_ownerProject)) {
				index = 2;
			}
			else if (ProjectUtil.isThemeProject(_ownerProject)) {
				index = 3;
			}
			else if (ProjectUtil.isWebProject(_ownerProject)) {
				index = 4;
			}
			else {
				index = -1;
			}
		}

		if (index != -1) {
			_typeCombo.select(index);
		}

		final GridData gd = new GridData();

		gd.grabExcessHorizontalSpace = true;
		gd.minimumWidth = 100;

		_typeCombo.setLayoutData(gd);

		setControl(composite);
	}

	public boolean finish() {
		if ((_ownerProject != null) && ProjectUtil.isLiferayFacetedProject(_ownerProject)) {
			return true;
		}
		else {
			setErrorMessage(Msgs.selectedProjectNotLiferayProject);

			return false;
		}
	}

	public IClasspathEntry getSelection() {
		IPath path = new Path(PluginClasspathContainerInitializer.ID + "/");

		final int index = _typeCombo.getSelectionIndex();

		if (index != -1) {
			final String type = _typeCombo.getItem(index);

			path = path.append(type);
		}

		return JavaCore.newContainerEntry(path);
	}

	public void initialize(IJavaProject project, IClasspathEntry[] currentEntries) {
		_ownerProject = (project == null) ? null : project.getProject();
	}

	public void setSelection(IClasspathEntry entry) {
		final IPath path = (entry == null) ? null : entry.getPath();

		if ((path != null) && (path.segmentCount() == 2)) {
			_type = path.segment(1);
		}
	}

	private static int _indexOf(final String[] array, final String str) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(str)) {
				return i;
			}
		}

		return -1;
	}

	private IProject _ownerProject;
	private String _type;
	private Combo _typeCombo;

	private static class Msgs extends NLS {

		public static String containerManagesClasspathEntries;
		public static String liferayPluginAPILibrary;
		public static String liferayPluginTypeLabel;
		public static String selectedProjectNotLiferayProject;

		static {
			initializeMessages(PluginClasspathContainerPage.class.getName(), Msgs.class);
		}

	}

}