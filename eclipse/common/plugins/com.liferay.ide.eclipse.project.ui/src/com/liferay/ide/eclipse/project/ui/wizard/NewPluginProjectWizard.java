/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.project.ui.wizard;

import com.liferay.ide.eclipse.project.core.IPortletFramework;
import com.liferay.ide.eclipse.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.eclipse.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.eclipse.project.core.facet.PluginFacetProjectCreationDataModelProvider;
import com.liferay.ide.eclipse.project.ui.IPortletFrameworkDelegate;
import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.ui.LiferayPerspectiveFactory;
import com.liferay.ide.eclipse.ui.wizard.INewProjectWizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class NewPluginProjectWizard extends NewProjectDataModelFacetWizard
	implements INewProjectWizard, IPluginProjectDataModelProperties {

	protected NewPluginProjectFirstPage firstPage;

	protected ImageDescriptor liferayWizardImageDescriptor;

	protected NewPortletPluginProjectPage portletPluginPage;

	protected String projectType;

	public NewPluginProjectWizard() {
		super();

		setupWizard();
	}

	public NewPluginProjectWizard(IDataModel model) {
		super(model);

		setupWizard();
	}

	@Override
	public boolean canFinish() {
		if (getContainer().getCurrentPage().equals(portletPluginPage) && isPluginWizardFragmentEnabled()) {
			return false;
		}

		return super.canFinish();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (this.portletPluginPage.equals(page)) {
			if (isPluginWizardFragmentEnabled()) {
				IPortletFramework selectedFramework = this.portletPluginPage.getSelectedPortletFramework();
				IPortletFrameworkDelegate delegate =
					ProjectUIPlugin.getPortletFrameworkDelegate(selectedFramework.getId());

				IPluginWizardFragment pluginFragment = delegate.getWizardFragment();
				IDataModel dm = DataModelFactory.createDataModel(pluginFragment.getDataModelProvider());

				getDataModel().addNestedModel(PLUGIN_FRAGMENT_DM, dm);
				pluginFragment.setDataModel(dm);
				pluginFragment.initFragmentDataModel(getDataModel(), getProjectName());
				pluginFragment.addPages();
				pluginFragment.setHostPage(this.portletPluginPage);
				return pluginFragment.getNextPage(page);
			}
			else {
				return null;
			}
		}

		return super.getNextPage(page);
	}

	public String getProjectType() {
		return this.projectType;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);

		getDataModel().setBooleanProperty(PLUGIN_TYPE_PORTLET, true);
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	@Override
	protected IWizardPage[] createBeginingPages() {
		this.firstPage = createFirstPage();
		this.portletPluginPage = new NewPortletPluginProjectPage(this, model);

		return new IWizardPage[] {
			firstPage, portletPluginPage
		};
	}

	protected IDataModel createDataModel() {
		try {
			return DataModelFactory.createDataModel(new PluginFacetProjectCreationDataModelProvider());
		}
		catch (Exception e) {
			ProjectUIPlugin.logError(e);

			return null;
		}
	}

	@Override
	protected NewPluginProjectFirstPage createFirstPage() {
		return new NewPluginProjectFirstPage(this, model, "first.page"); //$NON-NLS-1$
	}

	@Override
	protected ImageDescriptor getDefaultPageImageDescriptor() {
		if (liferayWizardImageDescriptor == null) {
			liferayWizardImageDescriptor =
				ImageDescriptor.createFromURL(ProjectUIPlugin.getDefault().getBundle().getEntry(
					"/icons/wizban/plugin_project.png"));
		}

		return liferayWizardImageDescriptor;
	}

	protected String getFinalPerspectiveID() {
		return LiferayPerspectiveFactory.ID;
	}

	protected IDataModel getNestedModel() {
		return getDataModel().getNestedModel(NESTED_PROJECT_DM);
	}

	protected String getPluginFacetId() {
		IDataModel dm = getDataModel();

		if (dm.getBooleanProperty(PLUGIN_TYPE_PORTLET)) {
			return IPluginFacetConstants.LIFERAY_PORTLET_PLUGIN_FACET_ID;
		}
		else if (dm.getBooleanProperty(PLUGIN_TYPE_HOOK)) {
			return IPluginFacetConstants.LIFERAY_HOOK_PLUGIN_FACET_ID;
		}
		else if (dm.getBooleanProperty(PLUGIN_TYPE_EXT)) {
			return IPluginFacetConstants.LIFERAY_EXT_PLUGIN_FACET_ID;
		}
		else if (dm.getBooleanProperty(PLUGIN_TYPE_LAYOUTTPL)) {
			return IPluginFacetConstants.LIFERAY_LAYOUTTPL_PLUGIN_FACET_ID;
		}
		else if (dm.getBooleanProperty(PLUGIN_TYPE_THEME)) {
			return IPluginFacetConstants.LIFERAY_THEME_PLUGIN_FACET_ID;
		}
		else {
			return null;
		}
	}

	protected String getProjectSuffix() {
		if (getDataModel().getBooleanProperty(PLUGIN_TYPE_PORTLET)) {
			return ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
		}
		else if (getDataModel().getBooleanProperty(PLUGIN_TYPE_HOOK)) {
			return ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX;
		}
		else if (getDataModel().getBooleanProperty(PLUGIN_TYPE_EXT)) {
			return ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;
		}
		else if (getDataModel().getBooleanProperty(PLUGIN_TYPE_THEME)) {
			return ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX;
		}
		else if (getDataModel().getBooleanProperty(PLUGIN_TYPE_LAYOUTTPL)) {
			return ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
		}

		return null;
	}

	@Override
	protected IFacetedProjectTemplate getTemplate() {
		return ProjectFacetsManager.getTemplate(IPluginFacetConstants.LIFERAY_DEFAULT_FACET_TEMPLATE); //$NON-NLS-1$
	}

	protected boolean isPluginWizardFragmentEnabled() {
		IPortletFramework portletFramework = this.portletPluginPage.getSelectedPortletFramework();

		if (portletFramework != null) {
			IPortletFrameworkDelegate delegate = ProjectUIPlugin.getPortletFrameworkDelegate(portletFramework.getId());

			if (delegate != null && delegate.getWizardFragment() != null && delegate.isFragmentEnabled()) {

				return true;
			}
		}

		return false;
	}

	@Override
	protected void performFinish(IProgressMonitor monitor)
		throws CoreException {

		// String projectName = getFacetedProjectWorkingCopy().getProjectName();
		// getFacetedProjectWorkingCopy().setProjectName(projectName +
		// getProjectSuffix());
		// String projectName =
		// getDataModel().getStringProperty(FACET_PROJECT_NAME);
		// getDataModel().setStringProperty(FACET_PROJECT_NAME, projectName +
		// getProjectSuffix());

		firstPage.setShouldValidatePage(false);

		// model.setProperty(SETUP_PROJECT_FLAG, "");

		String projectName = getNestedModel().getStringProperty(PROJECT_NAME);

		if (!projectName.endsWith(getProjectSuffix())) {
			getNestedModel().setStringProperty(PROJECT_NAME, projectName + getProjectSuffix());
		}

		model.setProperty(PORTLET_NAME, projectName);
		model.setProperty(THEME_NAME, projectName);
		model.setProperty(LAYOUTTPL_NAME, projectName);

		super.performFinish(monitor);

		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				IViewPart view = null;

				try {
					view =
						PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().findView(
							IPageLayout.ID_PROJECT_EXPLORER);
				}
				catch (Exception e) {
					// Just bail and return if there is no view
				}
				if (view == null) {
					return;
				}

				CommonViewer viewer = (CommonViewer) view.getAdapter(CommonViewer.class);

				viewer.refresh(true);
			}
		});;
	}

	@Override
	protected void postPerformFinish()
		throws InvocationTargetException {

		// if we have a wizard fragment execute its operation after project is created
		if (isPluginWizardFragmentEnabled()) {
			final IDataModel fragmentModel = getDataModel().getNestedModel(PLUGIN_FRAGMENT_DM);
			fragmentModel.setStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME, getProjectName());

			try {
				getContainer().run(false, false, new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {

						try {
							fragmentModel.getDefaultOperation().execute(monitor, null);
						}
						catch (ExecutionException e) {
							ProjectUIPlugin.logError("Error executing wizard fragment", e);
						}

					}
				});
			}
			catch (InterruptedException e) {
				ProjectUIPlugin.logError("Error executing wizard fragment", e);
			}
		}

		super.postPerformFinish();
	}

	protected void setupWizard() {
		setWindowTitle("New Liferay Plug-in Project");
		setShowFacetsSelectionPage(false);
	}

}
