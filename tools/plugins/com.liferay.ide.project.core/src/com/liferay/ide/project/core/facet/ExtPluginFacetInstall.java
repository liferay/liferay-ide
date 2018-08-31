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

package com.liferay.ide.project.core.facet;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.ISDKConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;

import org.w3c.dom.Element;

/**
 * @author Gregory Amerson
 * @author Kamesh Sampath
 */
@SuppressWarnings("restriction")
public class ExtPluginFacetInstall extends PluginFacetInstall {

	@Override
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
		throws CoreException {

		super.execute(project, fv, config, monitor);

		IDataModel model = (IDataModel)config;

		IDataModel masterModel = (IDataModel)model.getProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM);

		if ((masterModel != null) && masterModel.getBooleanProperty(CREATE_PROJECT_OPERATION)) {

			// IDE-1122 SDK creating project has been moved to Class NewPluginProjectWizard

			String extName = this.masterModel.getStringProperty(EXT_NAME);

			IPath projectTempPath = (IPath)masterModel.getProperty(PROJECT_TEMP_PATH);

			processNewFiles(projectTempPath.append(extName + ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX));

			FileUtil.deleteDir(projectTempPath.toFile(), true);

			// End IDE-1122

			try {
				project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
			catch (Exception e) {
				ProjectCore.logError(e);
			}

			IFolder webappRoot = this.project.getFolder(ISDKConstants.DEFAULT_DOCROOT_FOLDER);

			deleteFolder(webappRoot.getFolder("WEB-INF/src"));
			deleteFolder(webappRoot.getFolder("WEB-INF/classes"));
		}

		if (shouldSetupExtClasspath()) {
			IJavaProject javaProject = JavaCore.create(project);

			List<IClasspathEntry> existingRawClasspath = Arrays.asList(javaProject.getRawClasspath());

			List<IClasspathEntry> newRawClasspath = new ArrayList<>();

			// first lets add all new source folders

			for (int i = 0; i < IPluginFacetConstants.EXT_PLUGIN_SDK_SOURCE_FOLDERS.length; i++) {
				IFolder source = this.project.getFolder(IPluginFacetConstants.EXT_PLUGIN_SDK_SOURCE_FOLDERS[i]);

				IFolder output = this.project.getFolder(IPluginFacetConstants.EXT_PLUGIN_SDK_OUTPUT_FOLDERS[i]);

				IClasspathAttribute[] attributes =
					{JavaCore.newClasspathAttribute("owner.project.facets", "liferay.ext")};

				IClasspathEntry sourceEntry = JavaCore.newSourceEntry(
					source.getFullPath(), new IPath[0], new IPath[0], output.getFullPath(), attributes);

				newRawClasspath.add(sourceEntry);
			}

			// next add all previous classpath entries except for source folders

			for (IClasspathEntry entry : existingRawClasspath) {
				if (entry.getEntryKind() != IClasspathEntry.CPE_SOURCE) {
					newRawClasspath.add(entry);
				}
			}

			IFolder outputFolder = this.project.getFolder(IPluginFacetConstants.EXT_PLUGIN_DEFAULT_OUTPUT_FOLDER);

			javaProject.setRawClasspath(
				newRawClasspath.toArray(new IClasspathEntry[0]), outputFolder.getFullPath(), null);

			ProjectUtil.fixExtProjectSrcFolderLinks(this.project);
		}

		// IDE-1239 need to make sure and delete docroot/WEB-INF/ext-web/docroot/WEB-INF/lib

		_removeUnneededFolders(this.project);
	}

	protected void deleteFolder(IFolder folder) throws CoreException {
		if (FileUtil.exists(folder)) {
			folder.delete(true, null);
		}
	}

	protected void fixTilesDefExtFile() {
		IWebProject webproject = LiferayCore.create(IWebProject.class, project);

		IFolder webappRoot = webproject.getDefaultDocrootFolder();

		IFile tilesDefExtFile = webappRoot.getFile("WEB-INF/ext-web/docroot/WEB-INF/tiles-defs-ext.xml");

		if (FileUtil.notExists(tilesDefExtFile)) {
			return;
		}

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			IDOMModel domModel = (IDOMModel)modelManager.getModelForEdit(tilesDefExtFile);

			domModel.aboutToChangeModel();

			IDOMDocument document = domModel.getDocument();

			Element root = document.getDocumentElement();

			Element def = document.createElement("definition");

			def.setAttribute("name", StringPool.EMPTY);

			root.appendChild(def);

			root.appendChild(document.createTextNode("\n"));

			new FormatProcessorXML().formatNode(def);

			domModel.changedModel();
			domModel.save();
			domModel.releaseFromEdit();

			tilesDefExtFile.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		catch (Exception e) {
			ProjectCore.logError(e);
		}
	}

	@Override
	protected String getDefaultOutputLocation() {
		return IPluginFacetConstants.EXT_PLUGIN_DEFAULT_OUTPUT_FOLDER;
	}

	private void _removeUnneededFolders(IProject project) throws CoreException {
		IWebProject webproject = LiferayCore.create(IWebProject.class, project);

		if ((webproject != null) && (webproject.getDefaultDocrootFolder() != null)) {
			IFolder webappRoot = webproject.getDefaultDocrootFolder();

			deleteFolder(webappRoot.getFolder("WEB-INF/lib"));
		}
	}

}