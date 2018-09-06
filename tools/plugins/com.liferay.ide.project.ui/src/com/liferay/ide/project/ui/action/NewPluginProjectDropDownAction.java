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
 * @author Simon Jiang
 */
public class NewPluginProjectDropDownAction extends Action implements IMenuCreator, IWorkbenchWindowPulldownDelegate2 {

	public static Action getDefaultAction() {
		return getWizardAction(DEFAULT_WIZARD_ID);
	}

	public static NewWizardAction[] getNewProjectActions() {
		ArrayList<NewWizardAction> containers = new ArrayList<>();

		IExtensionPoint extensionPoint = CoreUtil.getExtensionPoint(PlatformUI.PLUGIN_ID, PL_NEW);

		if (extensionPoint != null) {
			IConfigurationElement[] elements = extensionPoint.getConfigurationElements();

			for (IConfigurationElement element : elements) {
				if (TAG_WIZARD.equals(element.getName()) && _isProjectWizard(element, getTypeAttribute())) {
					containers.add(new NewWizardAction(element));
				}
			}
		}

		NewWizardAction[] actions = (NewWizardAction[])containers.toArray(new NewWizardAction[containers.size()]);

		Arrays.sort(actions);

		return actions;
	}

	public static Action getPluginProjectAction() {
		return getWizardAction(DEFAULT_PLUGIN_WIZARD_ID);
	}

	public static Action getWizardAction(final String wizardId) {
		Action[] actions = getNewProjectActions();

		if (ListUtil.isNotEmpty(actions)) {
			for (Action action : actions) {
				if (action instanceof NewWizardAction && wizardId.equals(action.getId())) {
					return action;
				}
			}
		}

		return null;
	}

	public NewPluginProjectDropDownAction() {
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

		NewWizardAction[] actions = (NewWizardAction[])containers.toArray(new NewWizardAction[containers.size()]);

		Arrays.sort(actions);

		return actions;
	}

	public NewWizardAction[] getExtraProjectActions() {
		ArrayList<NewWizardAction> containers = new ArrayList<>();

		IExtensionPoint extensionPoint = CoreUtil.getExtensionPoint(PlatformUI.PLUGIN_ID, PL_NEW);

		if (extensionPoint != null) {
			IConfigurationElement[] elements = extensionPoint.getConfigurationElements();

			for (IConfigurationElement element : elements) {
				if (TAG_WIZARD.equals(element.getName()) && _isProjectWizard(element, getExtraTypeAttribute())) {
					containers.add(new NewWizardAction(element));
				}
			}
		}

		NewWizardAction[] actions = (NewWizardAction[])containers.toArray(new NewWizardAction[containers.size()]);

		Arrays.sort(actions);

		return actions;
	}

	public Menu getMenu(Control parent) {
		if (fMenu == null) {
			fMenu = new Menu(parent);

			NewWizardAction[] actions = getNewProjectActions();

			// only do the first project action (not the 5 separate ones)

			for (NewWizardAction action : actions) {
				action.setShell(fWizardShell);

				ActionContributionItem projectItem = new ActionContributionItem(action);

				projectItem.fill(fMenu, -1);
			}

			NewWizardAction importAction = new ImportLiferayProjectsWizardAction();

			importAction.setShell(fWizardShell);

			ActionContributionItem item = new ActionContributionItem(importAction);

			item.fill(fMenu, -1);

			NewWizardAction[] projectExtraActions = getExtraProjectActions();

			for (NewWizardAction extraAction : projectExtraActions) {
				extraAction.setShell(fWizardShell);

				ActionContributionItem extraItem = new ActionContributionItem(extraAction);

				extraItem.fill(fMenu, -1);
			}

			new Separator().fill(fMenu, -1);

			// add non project items

			NewWizardAction[] nonProjectActions = getActionFromDescriptors(getNonProjectTypeAttribute());

			for (NewWizardAction action : nonProjectActions) {
				action.setShell(fWizardShell);

				ActionContributionItem noProjectitem = new ActionContributionItem(action);

				noProjectitem.fill(fMenu, -1);
			}

			new Separator().fill(fMenu, -1);

			NewWizardAction[] noProjectExtraActions = getActionFromDescriptors(getNonProjectExtraTypeAttribute());

			for (NewWizardAction action : noProjectExtraActions) {
				action.setShell(fWizardShell);

				ActionContributionItem noProjectExtraitem = new ActionContributionItem(action);

				noProjectExtraitem.fill(fMenu, -1);
			}

			new Separator().fill(fMenu, -1);

			Action[] sdkActions = getServerActions(parent.getShell());

			for (Action action : sdkActions) {
				ActionContributionItem sdkItem = new ActionContributionItem(action);

				sdkItem.fill(fMenu, -1);
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

	protected static String getExtraTypeAttribute() {
		return "liferay_extra_project";
	}

	protected static String getTypeAttribute() {
		return "liferay_project";
	}

	protected String getNonProjectExtraTypeAttribute() {
		return "liferay_extra_artifact";
	}

	protected String getNonProjectTypeAttribute() {
		return "liferay_artifact";
	}

	protected Action[] getServerActions(Shell shell) {
		return new Action[] {new NewServerAction(shell)};
	}

	protected static final String DEFAULT_PLUGIN_WIZARD_ID = "com.liferay.ide.project.ui.newPluginProjectWizard";

	protected static final String DEFAULT_WIZARD_ID = "com.liferay.ide.project.ui.newModuleProjectWizard";

	protected static final String PL_NEW = "newWizards";

	protected static final String TAG_CLASS = "class";

	protected static final String TAG_NAME = "name";

	protected static final String TAG_PARAMETER = "parameter";

	protected static final String TAG_VALUE = "value";

	protected static final String TAG_WIZARD = "wizard";

	protected Menu fMenu;
	protected Shell fWizardShell;

	private static boolean _isProjectWizard(IConfigurationElement element, String typeAttribute) {
		IConfigurationElement[] classElements = element.getChildren(TAG_CLASS);

		if (!CoreUtil.isNullOrEmpty(typeAttribute) && ListUtil.isNotEmpty(classElements)) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);

				for (IConfigurationElement paramElement : paramElements) {
					if (typeAttribute.equals(paramElement.getAttribute(TAG_NAME))) {
						return Boolean.valueOf(paramElement.getAttribute(TAG_VALUE));
					}
				}
			}
		}

		// old way, deprecated

		return Boolean.valueOf(element.getAttribute(getTypeAttribute()));
	}

	private boolean _isLiferayArtifactWizard(IConfigurationElement element, String typeAttribute) {
		IConfigurationElement[] classElements = element.getChildren(TAG_CLASS);

		if (ListUtil.isNotEmpty(classElements)) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);

				for (IConfigurationElement paramElement : paramElements) {
					String tagName = paramElement.getAttribute(TAG_NAME);

					if (typeAttribute.equals(tagName)) {
						return Boolean.valueOf(paramElement.getAttribute(TAG_VALUE));
					}
				}
			}
		}

		// old way, deprecated

		return Boolean.valueOf(element.getAttribute(getTypeAttribute()));
	}

}