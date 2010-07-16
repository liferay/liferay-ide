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

import com.liferay.ide.eclipse.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.eclipse.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.eclipse.project.core.facet.PluginFacetProjectCreationDataModelProvider;
import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.ui.LiferayPerspectiveFactory;
import com.liferay.ide.eclipse.ui.wizard.INewProjectWizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard;

/**
 * @author Greg Amerson
 */
public class NewPluginProjectWizard extends NewProjectDataModelFacetWizard
	implements INewProjectWizard, IPluginProjectDataModelProperties {

	private PluginProjectFirstPage firstPage;
	
	protected ImageDescriptor liferayWizardImageDescriptor;
	
	protected String projectType;

	public NewPluginProjectWizard() {
		super();
		
		setupWizard();
	}

	public NewPluginProjectWizard(IDataModel model) {
		super(model);
		
		setupWizard();
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public String getProjectType() {
		return this.projectType;
	}

	@Override
	protected IWizardPage[] createBeginingPages() {
		this.firstPage = createFirstPage();
		
		return new IWizardPage[] {
			firstPage
		// , new PluginProjectAdvPage(this, model)
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
	protected PluginProjectFirstPage createFirstPage() {
		return new PluginProjectFirstPage(this, model, "first.page"); //$NON-NLS-1$
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

	@SuppressWarnings("restriction")
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

	protected void setupWizard() {
		setWindowTitle("New Plug-in Project");
		setShowFacetsSelectionPage(false);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		// TODO Implement getNextPage method on class NewPluginProjectWizard
		return super.getNextPage(page);
	}

	@Override
	public IWizardPage getPage(String pageName) {
		// TODO Implement getPage method on class NewPluginProjectWizard
		return super.getPage(pageName);
	}

	@Override
	public int getPageCount() {
		// TODO Implement getPageCount method on class NewPluginProjectWizard
		return super.getPageCount();
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		// TODO Implement getPreviousPage method on class NewPluginProjectWizard
		return super.getPreviousPage(page);
	}

}
