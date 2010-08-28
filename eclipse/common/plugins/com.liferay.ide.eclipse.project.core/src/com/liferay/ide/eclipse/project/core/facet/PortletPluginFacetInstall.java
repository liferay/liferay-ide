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

package com.liferay.ide.eclipse.project.core.facet;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.core.util.FileUtil;
import com.liferay.ide.eclipse.project.core.IPluginWizardFragmentProperties;
import com.liferay.ide.eclipse.project.core.ProjectCorePlugin;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author Greg Amerson
 */
public class PortletPluginFacetInstall extends PluginFacetInstall {

	public static final IProjectFacet LIFERAY_PORTLET_PLUGIN_FACET =
		ProjectFacetsManager.getProjectFacet(IPluginFacetConstants.LIFERAY_PORTLET_PLUGIN_FACET_ID);

	@Override
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
		throws CoreException {
		
		super.execute(project, fv, config, monitor);

		IDataModel model = (IDataModel) config;
		
		IDataModel masterModel = (IDataModel) model.getProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM);
		
		if (masterModel != null && masterModel.getBooleanProperty(CREATE_PROJECT_OPERATION)) {
			// get the template zip for portlets and extract into the project
			SDK sdk = getSDK();

			String portletName = this.masterModel.getStringProperty(PORTLET_NAME);
			
			String displayName = this.masterModel.getStringProperty(DISPLAY_NAME);

			IPath newPortletPath = sdk.createNewPortlet(portletName, displayName, getRuntimeLocation());
			
			processNewFiles(newPortletPath.append(portletName + ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX), false);
			
			// cleanup portlet files
			FileUtil.deleteDir(newPortletPath.toFile(), true);

			this.project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			if (masterModel.getBooleanProperty(PLUGIN_FRAGMENT_ENABLED)) {
				final IDataModel fragmentModel = masterModel.getNestedModel(PLUGIN_FRAGMENT_DM);
				if (fragmentModel != null) {
					// IDE-205 remove view.jsp
					try {
						if (fragmentModel.getBooleanProperty(IPluginWizardFragmentProperties.REMOVE_EXISTING_ARTIFACTS)) {
							IFolder docroot = ProjectUtil.getDocroot(this.project);
							IFile viewJsp = docroot.getFile("view.jsp");
							if (viewJsp.exists()) {
								viewJsp.delete(true, monitor);
							}
						}
					}
					catch (Exception ex) {
						ProjectCorePlugin.logError("Error deleting view.jsp", ex);
					}
				}
			}

		}
		else {
			setupDefaultOutputLocation();
		}

		// modify the web.xml and add <jsp-config><taglib> for liferay tlds
		copyPortletTLD();
		
		configWebXML();
	}

	protected void copyPortletTLD()
		throws CoreException {
		
		IPath portalRoot = getPortalRoot();
		
		IPath portletTld = portalRoot.append("WEB-INF/tld/liferay-portlet.tld");
		
		if (portletTld.toFile().exists()) {
			IFolder tldFolder = getWebRootFolder().getFolder("WEB-INF/tld");
			
			CoreUtil.prepareFolder(tldFolder);
			
			IFile tldFile = tldFolder.getFile("liferay-portlet.tld");
			
			if (!tldFile.exists()) {
				try {
					tldFile.create(new FileInputStream(portletTld.toFile()), true, null);
				}
				catch (FileNotFoundException e) {
					throw new CoreException(ProjectCorePlugin.createErrorStatus(e));
				}
			}
		}
	}

	// @SuppressWarnings("unchecked")
	// protected void configWebXML() {
	// WebArtifactEdit webArtifactEdit =
	// WebArtifactEdit.getWebArtifactEditForWrite(this.project);
	// int j2eeVersion = webArtifactEdit.getJ2EEVersion();
	// WebApp webApp = webArtifactEdit.getWebApp();
	// webApp.setFileList(null);
	// JSPConfig jspConfig = webApp.getJspConfig();
	// if (jspConfig == null && webApp.getVersionID() != 23) {
	// jspConfig = JspFactory.eINSTANCE.createJSPConfig();
	// }
	// TagLibRefType tagLibRefType = JspFactory.eINSTANCE.createTagLibRefType();
	// tagLibRefType.setTaglibURI("http://java.sun.com/portlet_2_0");
	// tagLibRefType.setTaglibLocation("/WEB-INF/tld/liferay-portlet.tld");
	// if (jspConfig != null) {
	// jspConfig.getTagLibs().add(tagLibRefType);
	// } else {
	// EList tagLibs = webApp.getTagLibs();
	// tagLibs.add(tagLibRefType);
	// }
	// if (jspConfig != null) {
	// webApp.setJspConfig(jspConfig);
	// }
	// webArtifactEdit.saveIfNecessary(null);
	// webArtifactEdit.dispose();
	// }

}
