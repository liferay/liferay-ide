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

package com.liferay.ide.project.ui.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.project.ui.wizard.WorkingSetCustomPart;
import com.liferay.ide.ui.LiferayWorkspacePerspectiveFactory;
import com.liferay.ide.ui.util.UIUtil;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.forms.ContainerPart.Children;
import org.eclipse.sapphire.ui.forms.WizardDef;
import org.eclipse.sapphire.ui.forms.WizardPagePart;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.eclipse.wst.web.internal.DelegateConfigurationElement;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class BaseProjectWizard<T extends Element> extends SapphireWizard<T> implements IWorkbenchWizard, INewWizard {

	public BaseProjectWizard(T t, Reference<WizardDef> wizard) {
		super(t, wizard);
	}

	@Override
	public IWizardPage[] getPages() {
		final IWizardPage[] wizardPages = super.getPages();

		if (!_firstErrorMessageRemoved && (wizardPages != null)) {
			final SapphireWizardPage wizardPage = (SapphireWizardPage)wizardPages[0];

			final String message = wizardPage.getMessage();
			final int messageType = wizardPage.getMessageType();

			if ((messageType == IMessageProvider.ERROR) && !CoreUtil.isNullOrEmpty(message)) {
				wizardPage.setMessage("Please enter a project name.", SapphireWizardPage.NONE);
				_firstErrorMessageRemoved = true;
			}
		}

		return wizardPages;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if (selection != null) {
			Object element = selection.getFirstElement();

			if (element instanceof IResource) {
				IResource resource = (IResource)element;

				IProject project = resource.getProject();

				IPath location = project.getLocation();

				if (location != null) {
					((BaseModuleOp)element()).setInitialSelectionPath(PathBridge.create(location));
				}
			}
		}
	}

	protected void addToWorkingSets(IProject newProject) throws Exception {
		if (newProject != null) {
			List<WizardPagePart> wizardPages = part().getPages();

			WizardPagePart wizardPagePart = wizardPages.get(0);

			Children children = wizardPagePart.children();

			for (final Object formPart : children.all()) {
				if (formPart instanceof WorkingSetCustomPart) {
					WorkingSetCustomPart workingSetPart = (WorkingSetCustomPart)formPart;

					IWorkingSet[] workingSets = workingSetPart.getWorkingSets();

					if (ListUtil.isNotEmpty(workingSets)) {
						IWorkingSetManager workingSetManager = UIUtil.getWorkingSetManager();

						workingSetManager.addToWorkingSets(newProject, workingSets);
					}
				}
			}
		}
	}

	protected void openLiferayPerspective(IProject newProject) {
		IWorkbenchPage activePage = UIUtil.getActivePage();

		IPerspectiveDescriptor perspective = activePage.getPerspective();

		if (LiferayWorkspacePerspectiveFactory.ID.equals(perspective.getId())) {
			return;
		}

		// open the "final" perspective

		final IConfigurationElement element = new DelegateConfigurationElement(null) {

			@Override
			public String getAttribute(String aName) {
				if (aName.equals("finalPerspective")) {
					return LiferayWorkspacePerspectiveFactory.ID;
				}

				return super.getAttribute(aName);
			}

		};

		BasicNewProjectResourceWizard.updatePerspective(element);

		// select and reveal

		BasicNewResourceWizard.selectAndReveal(newProject, UIUtil.getActiveWorkbenchWindow());
	}

	@Override
	protected void performPostFinish() {
		super.performPostFinish();

		UIUtil.refreshCommonView("org.eclipse.wst.server.ui.ServersView");
	}

	private boolean _firstErrorMessageRemoved = false;

}