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

package com.liferay.ide.project.ui.action;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.LiferayUIPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
@SuppressWarnings({"restriction", "rawtypes"})
public class NewWizardAction extends Action implements Comparable {

	public static final String ATT_CLASS = "class";

	public static final String ATT_ICON = "icon";

	public static final String ATT_ID = "id";

	public static final String ATT_MENUINDEX = "menuIndex";

	public static final String ATT_NAME = "name";

	public static final String ATT_PROJECTTYPE = "project_type";

	public static final String ATT_VALID_PROJECT_TYPES = "validProjectTypes";

	public static final String TAG_CLASS = "class";

	public static final String TAG_DESCRIPTION = "description";

	public static final String TAG_NAME = "name";

	public static final String TAG_PARAMETER = "parameter";

	public static final String TAG_VALUE = "value";

	public NewWizardAction(IConfigurationElement element) {
		fConfigurationElement = element;

		String description = _getDescriptionFromConfig(fConfigurationElement);

		setText(NLS.bind(Msgs.newAction, element.getAttribute(ATT_NAME)));
		setDescription(description);
		setToolTipText(description);
		setImageDescriptor(_getIconFromConfig(fConfigurationElement));
		setMenuIndex(_getMenuIndexFromConfig(fConfigurationElement));
		setId(element.getAttribute(ATT_ID));
	}

	public int compareTo(Object o) {
		NewWizardAction action = (NewWizardAction)o;

		return getMenuIndex() - action.getMenuIndex();
	}

	public int getMenuIndex() {
		return menuIndex;
	}

	public String getProjectType() {
		return projectType;
	}

	public void run() {
		Shell shell = getShell();

		try {
			INewWizard wizard = createWizard();

			wizard.init(PlatformUI.getWorkbench(), getSelection());

			WizardDialog dialog = new WizardDialog(shell, wizard);

			PixelConverter converter = new PixelConverter(JFaceResources.getDialogFont());

			dialog.setMinimumPageSize(
				converter.convertWidthInCharsToPixels(70), converter.convertHeightInCharsToPixels(20));

			dialog.create();

			int res = dialog.open();

			notifyResult(res == Window.OK);
		}
		catch (CoreException ce) {
		}
	}

	public void setMenuIndex(int menuIndex) {
		this.menuIndex = menuIndex;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public void setShell(Shell shell) {
		fShell = shell;
	}

	protected INewWizard createWizard() throws CoreException {
		return (INewWizard)CoreUtility.createExtension(fConfigurationElement, ATT_CLASS);
	}

	protected IStructuredSelection getSelection() {
		if (fSelection == null) {
			return _evaluateCurrentSelection();
		}

		return fSelection;
	}

	protected Shell getShell() {
		if (fShell == null) {
			return ProjectUI.getActiveWindow().getShell();
		}

		return fShell;
	}

	protected IConfigurationElement fConfigurationElement;
	protected IStructuredSelection fSelection;
	protected Shell fShell;
	protected int menuIndex;
	protected String projectType = null;

	private IStructuredSelection _evaluateCurrentSelection() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (window != null) {
			ISelection selection = window.getSelectionService().getSelection();

			if (selection instanceof IStructuredSelection) {
				return (IStructuredSelection)selection;
			}
		}

		return StructuredSelection.EMPTY;
	}

	private String _getDescriptionFromConfig(IConfigurationElement config) {
		IConfigurationElement[] children = config.getChildren(TAG_DESCRIPTION);

		if (children.length >= 1) {
			return children[0].getValue();
		}

		return StringPool.EMPTY;
	}

	private ImageDescriptor _getIconFromConfig(IConfigurationElement config) {
		String iconName = config.getAttribute(ATT_ICON);

		if (iconName != null) {
			return LiferayUIPlugin.imageDescriptorFromPlugin(config.getContributor().getName(), iconName);
		}

		return null;
	}

	private int _getMenuIndexFromConfig(IConfigurationElement config) {
		IConfigurationElement[] classElements = config.getChildren(TAG_CLASS);

		if (ListUtil.isNotEmpty(classElements)) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);

				for (IConfigurationElement paramElement : paramElements) {
					if (ATT_MENUINDEX.equals(paramElement.getAttribute(TAG_NAME))) {
						return Integer.parseInt(paramElement.getAttribute(TAG_VALUE));
					}
				}
			}
		}

		return Integer.MAX_VALUE;
	}

	private static class Msgs extends NLS {

		public static String newAction;

		static {
			initializeMessages(NewWizardAction.class.getName(), Msgs.class);
		}

	}

}