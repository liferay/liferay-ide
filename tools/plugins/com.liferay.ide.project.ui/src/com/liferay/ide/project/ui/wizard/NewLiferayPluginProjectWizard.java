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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.ui.BaseProjectWizard;
import com.liferay.ide.project.ui.IvyUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.ui.LiferayPerspectiveFactory;
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ant.internal.ui.model.AntProjectNode;
import org.eclipse.ant.internal.ui.model.AntProjectNodeProxy;
import org.eclipse.ant.internal.ui.views.AntView;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.eclipse.wst.web.internal.DelegateConfigurationElement;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Simon Jiang
 * @author Eric Min
 */
@SuppressWarnings("restriction")
public class NewLiferayPluginProjectWizard extends BaseProjectWizard<NewLiferayPluginProjectOp> {

	public static void checkAndConfigureIvy(final IProject project) {
		if ((project != null) && FileUtil.exists(project.getFile(ISDKConstants.IVY_XML_FILE))) {
			new WorkspaceJob(
				"Configuring project with Ivy dependencies"
			) {

				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					try {
						IvyUtil.configureIvyProject(project, monitor);
					}
					catch (CoreException ce) {
						return ProjectCore.createErrorStatus(
							ProjectCore.PLUGIN_ID, "Failed to configured ivy project.", ce);
					}

					return Status.OK_STATUS;
				}

			}.schedule();
		}
	}

	public NewLiferayPluginProjectWizard() {
		super(_createDefaultOp(), DefinitionLoader.sdef(NewLiferayPluginProjectWizard.class).wizard());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	protected void openLiferayPerspective(IProject newProject) {
		final IWorkbench workbench = PlatformUI.getWorkbench();

		// open the "final" perspective

		final IConfigurationElement element = new DelegateConfigurationElement(null) {

			@Override
			public String getAttribute(String aName) {
				if (aName.equals("finalPerspective")) {
					return LiferayPerspectiveFactory.ID;
				}

				return super.getAttribute(aName);
			}

		};

		BasicNewProjectResourceWizard.updatePerspective(element);

		// select and reveal

		BasicNewResourceWizard.selectAndReveal(newProject, workbench.getActiveWorkbenchWindow());
	}

	@Override
	protected void performPostFinish() {
		super.performPostFinish();

		final List<IProject> projects = new ArrayList<>();

		final NewLiferayPluginProjectOp op = element().nearest(NewLiferayPluginProjectOp.class);

		ElementList<ProjectName> projectNames = op.getProjectNames();

		for (ProjectName projectName : projectNames) {
			IProject newProject = CoreUtil.getProject(get(projectName.getName()));

			if (newProject != null) {
				projects.add(newProject);
			}
		}

		for (final IProject project : projects) {
			try {
				addToWorkingSets(project);
			}
			catch (Exception ex) {
				ProjectUI.logError("Unable to add project to working set", ex);
			}
		}

		final IProject finalProject = projects.get(0);

		openLiferayPerspective(finalProject);

		_showInAntView(finalProject);

		checkAndConfigureIvy(finalProject);

		// check if a new portlet wizard is needed, available for portlet projects.

		boolean createNewPortlet = get(op.getCreateNewPortlet());

		if (createNewPortlet && PluginType.portlet.equals(get(op.getPluginType()))) {
			final IPortletFramework portletFramework = get(op.getPortletFramework());
			String wizardId = null;

			if ("mvc".equals(portletFramework.getShortName())) {
				wizardId = "com.liferay.ide.portlet.ui.newPortletWizard";
			}
			else if ("jsf-2.x".equals(portletFramework.getShortName())) {
				wizardId = "com.liferay.ide.portlet.ui.newJSFPortletWizard";
			}
			else if ("vaadin".equals(portletFramework.getShortName())) {
				wizardId = "com.liferay.ide.portlet.vaadin.ui.newVaadinPortletWizard";
			}

			if (wizardId != null) {
				_openNewPortletWizard(wizardId, finalProject);
			}
		}
	}

	private static NewLiferayPluginProjectOp _createDefaultOp() {
		return NewLiferayPluginProjectOp.TYPE.instantiate();
	}

	private void _openNewPortletWizard(String wizardId, final IProject project) {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();

		final IExtension extension = registry.getExtension(wizardId);

		final IConfigurationElement[] elements = extension.getConfigurationElements();

		for (final IConfigurationElement element : elements) {
			if ("wizard".equals(element.getName())) {
				UIUtil.async(
					new Runnable() {

						@Override
						public void run() {
							try {
								final INewWizard wizard = (INewWizard)CoreUtility.createExtension(element, "class");

								IWorkbenchWindow activeWorkbenchWindow = UIUtil.getActiveWorkbenchWindow();

								final Shell shell = activeWorkbenchWindow.getShell();

								wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(project));

								WizardDialog dialog = new WizardDialog(shell, wizard);

								dialog.create();

								dialog.open();
							}
							catch (CoreException ce) {
								ProjectCore.createErrorStatus(ce);
							}
						}

					});
			}
		}
	}

	private void _showInAntView(final IProject project) {
		Display display = Display.getDefault();

		display.asyncExec(
			new Runnable() {

				@Override
				public void run() {
					_refreshProjectExplorer();
					_addBuildInAntView();
				}

				private void _addBuildInAntView() {
					if (project != null) {
						IFile buildXmlFile = project.getFile("build.xml");

						if (buildXmlFile.exists()) {
							IPath fullPath = buildXmlFile.getFullPath();

							AntProjectNode antProject = new AntProjectNodeProxy(fullPath.toString());

							IWorkbenchWindow workbenchWindow = UIUtil.getWorkbenchWindows()[0];

							IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();

							IViewPart antView = workbenchPage.findView("org.eclipse.ant.ui.views.AntView");

							if (antView instanceof AntView) {
								((AntView)antView).addProject(antProject);
							}
						}
					}
				}

				private void _refreshProjectExplorer() {
					IViewPart view = null;

					IWorkbenchWindow workbenchWindow = UIUtil.getWorkbenchWindows()[0];

					IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();

					try {
						view = workbenchPage.findView(IPageLayout.ID_PROJECT_EXPLORER);
					}
					catch (Exception e) {

						// Just bail and return if there is no view

					}

					if (view == null) {
						return;
					}

					CommonViewer viewer = (CommonViewer)view.getAdapter(CommonViewer.class);

					viewer.refresh(true);
				}

			});
	}

}