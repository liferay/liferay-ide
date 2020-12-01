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

package com.liferay.ide.portlet.core.jsf;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.portlet.core.BasePortletFramework;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.jsf.core.IJSFCoreConstants;
import org.eclipse.jst.jsf.core.internal.project.facet.IJSFFacetInstallDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Gregory Amerson
 * @author Tao Tao
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class JSFPortletFramework
	extends BasePortletFramework implements IJSFFacetInstallDataModelProperties, IJSFPortletFrameworkProperties {

	public static final String DEFAULT_FRAMEWORK_NAME = "jsf-2.x";

	public static final String JSF_FACET_SUPPORTED_VERSION = "2.0";

	public JSFPortletFramework() {
	}

	@Override
	public IStatus configureNewProject(IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject) {
		IProjectFacetVersion jsfFacetVersion = getJSFProjectFacet(facetedProject);
		IProjectFacet jsfFacet = PortletCore.JSF_FACET;

		if (jsfFacetVersion == null) {
			jsfFacetVersion = jsfFacet.getVersion(JSF_FACET_SUPPORTED_VERSION);

			facetedProject.addProjectFacet(jsfFacetVersion);
		}

		IFacetedProject.Action action = facetedProject.getProjectFacetAction(jsfFacet);

		IDataModel jsfFacetDataModel = (IDataModel)action.getConfig();

		// TODO IDE-648 IDE-110

		jsfFacetDataModel.setProperty(SERVLET_URL_PATTERNS, null);
		jsfFacetDataModel.setProperty(WEBCONTENT_DIR, ISDKConstants.DEFAULT_DOCROOT_FOLDER);

		LibraryInstallDelegate libraryInstallDelegate = (LibraryInstallDelegate)jsfFacetDataModel.getProperty(
			LIBRARY_PROVIDER_DELEGATE);

		List<ILibraryProvider> providers = libraryInstallDelegate.getLibraryProviders();

		ILibraryProvider noOpProvider = null;

		for (ILibraryProvider provider : providers) {
			if (Objects.equals("jsf-no-op-library-provider", provider.getId())) {
				noOpProvider = provider;

				break;
			}
		}

		if (noOpProvider != null) {
			libraryInstallDelegate.setLibraryProvider(noOpProvider);
		}

		return Status.OK_STATUS;
	}

	@Override
	public IProjectFacet[] getFacets() {
		return new IProjectFacet[] {PortletCore.JSF_FACET};
	}

	@Override
	public IStatus postProjectCreated(
		IProject project, String frameworkName, String portletName, IProgressMonitor monitor) {

		/**
		 * we need to copy the original web.xml from the project template
		 * because of bugs in the JSF facet installer will overwrite our web.xml
		 * that comes with in the template
		 */
		super.postProjectCreated(project, frameworkName, portletName, monitor);

		SDK sdk = SDKUtil.getSDK(project);

		if (sdk != null) {
			try {

				// TODO IDE-648

				IPath sdkLocation = sdk.getLocation();

				IPath location = sdkLocation.append("tools/portlet_" + frameworkName + "_tmpl/docroot/WEB-INF/web.xml");

				File originalWebXmlFile = location.toFile();

				if (originalWebXmlFile.exists()) {
					IWebProject webproject = LiferayCore.create(IWebProject.class, project);

					if (webproject != null) {
						IFolder defaultDocroot = webproject.getDefaultDocrootFolder();

						try (InputStream newInputStream = Files.newInputStream(originalWebXmlFile.toPath())) {
							IFile webFile = defaultDocroot.getFile("WEB-INF/web.xml");

							webFile.setContents(newInputStream, IResource.FORCE, null);
						}
					}
				}
			}
			catch (Exception e) {
				return PortletCore.createErrorStatus("Could not copy original web.xml from JSF template in SDK.", e);
			}
		}

		try {
			IFolder docroot = CoreUtil.getDefaultDocrootFolder(project);

			IFolder views = docroot.getFolder("views");

			if (views.exists()) {
				IFolder versFolder = docroot.getFolder("WEB-INF/views");

				views.move(versFolder.getFullPath(), true, monitor);

				IFile portletXml = docroot.getFile("WEB-INF/portlet.xml");

				File portletXmlFile = FileUtil.getFile(portletXml);

				String contents = FileUtil.readContents(portletXmlFile, true);

				if (contents.contains("init-param")) {
					contents = contents.replaceAll("/views/view.xhtml", "/WEB-INF/views/view.xhtml");

					try (InputStream inputStream = new ByteArrayInputStream(contents.getBytes("UTF-8"))) {
						portletXml.setContents(inputStream, IResource.FORCE, null);
					}
				}
			}
		}
		catch (Exception e) {
			return PortletCore.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	// TODO add support for maven projects
	// TODO IDE-1334 check the web.xml for jsf frameworks.

	@Override
	public boolean supports(ILiferayProjectProvider provider) {
		if (((provider != null) && Objects.equals("ant", provider.getShortName())) ||
			Objects.equals("maven", provider.getShortName())) {

			return true;
		}

		return false;
	}

	protected IProjectFacetVersion getJSFProjectFacet(IFacetedProjectWorkingCopy project) {
		Set<IProjectFacetVersion> facets = project.getProjectFacets();

		for (IProjectFacetVersion facet : facets) {
			IProjectFacet projectFacet = facet.getProjectFacet();

			String projectFacetId = projectFacet.getId();

			if (projectFacetId.equals(IJSFCoreConstants.JSF_CORE_FACET_ID)) {
				return facet;
			}
		}

		return null;
	}

}