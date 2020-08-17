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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.PlatformUI;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class NewPortletDropDownAction extends Action implements IMenuCreator, IWorkbenchWindowPulldownDelegate2 {

	public NewPortletDropDownAction() {
		fMenu = null;

		setMenuCreator(this);
	}

	public void dispose() {
	}

	public NewWizardAction[] getActionFromDescriptors(String typeAttribute) {
		ArrayList<NewWizardAction> containers = new ArrayList<>();

		IExtensionPoint extensionPoint = CoreUtil.getExtensionPoint(PlatformUI.PLUGIN_ID, PL_NEW);

		if (extensionPoint != null) {
			IConfigurationElement[] elements = extensionPoint.getConfigurationElements();

			for (IConfigurationElement element : elements) {
				if (TAG_WIZARD.equals(element.getName()) && _isLiferayArtifactWizard(element, typeAttribute)) {
					containers.add(new NewWizardAction(element));
				}
			}
		}

		NewWizardAction[] actions = (NewWizardAction[])containers.toArray(new NewWizardAction[0]);

		Arrays.sort(actions);

		return actions;
	}

	public Action getDefaultAction() {
		Action[] actions = getActionFromDescriptors(getTypeAttribute());

		if (ListUtil.isNotEmpty(actions)) {
			for (Action action : actions) {
				if ((action instanceof NewWizardAction) && DEFAULT_WIZARD_ID.equals(action.getId())) {
					return action;
				}
			}
		}

		return null;
	}

	public Menu getMenu(Control parent) {
		if (fMenu == null) {
			fMenu = new Menu(parent);

			NewWizardAction[] actions = getActionFromDescriptors(getTypeAttribute());

			for (NewWizardAction action : actions) {
				action.setShell(fWizardShell);

				ActionContributionItem item = new ActionContributionItem(action);

				item.fill(fMenu, -1);
			}

			new Separator().fill(fMenu, -1);

			NewWizardAction[] extraActions = getActionFromDescriptors(getExtraTypeAttribute());

			for (NewWizardAction action : extraActions) {
				action.setShell(fWizardShell);

				ActionContributionItem item = new ActionContributionItem(action);

				item.fill(fMenu, -1);
			}
		}

		return fMenu;
	}

	public Menu getMenu(Menu parent) {
		return null;
	}

	public void init(IWorkbenchWindow window) {
		fWizardShell = window.getShell();
	}

	public void run(IAction action) {
		getDefaultAction().run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	protected String getExtraTypeAttribute() {
		return "liferay_extra_artifact";
	}

	protected String getTypeAttribute() {
		return "liferay_artifact";
	}

	protected static final String DEFAULT_WIZARD_ID = "com.liferay.ide.eclipse.portlet.ui.wizard.portlet";

	protected static final String PL_NEW = "newWizards";

	protected static final String TAG_CLASS = "class";

	protected static final String TAG_NAME = "name";

	protected static final String TAG_PARAMETER = "parameter";

	protected static final String TAG_VALUE = "value";

	protected static final String TAG_WIZARD = "wizard";

	protected Menu fMenu;
	protected Shell fWizardShell;

	private boolean _isLiferayArtifactWizard(IConfigurationElement element, String typeAttribute) {
		IConfigurationElement[] classElements = element.getChildren(TAG_CLASS);

		if (ListUtil.isNotEmpty(classElements)) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);

				for (IConfigurationElement paramElement : paramElements) {
					String tagName = paramElement.getAttribute(TAG_NAME);

					if ((tagName != null) && tagName.equals(typeAttribute)) {
						return Boolean.valueOf(paramElement.getAttribute(TAG_VALUE));
					}
				}
			}
		}

		// old way, deprecated

		return Boolean.valueOf(element.getAttribute(getTypeAttribute()));
	}

}