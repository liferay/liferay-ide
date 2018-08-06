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

package com.liferay.ide.portlet.ui.wizard;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.portlet.core.operation.NewEntryClassOperation;
import com.liferay.ide.portlet.core.operation.NewPortletClassOperation;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.project.core.IPluginWizardFragmentProperties;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.descriptor.AddNewPortletOperation;
import com.liferay.ide.project.core.descriptor.RemoveAllPortletsOperation;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.jst.j2ee.internal.common.operations.AddJavaEEArtifactOperation;
import org.eclipse.jst.j2ee.internal.common.operations.NewJavaEEArtifactClassOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class AddPortletOperation
	extends AddJavaEEArtifactOperation implements INewPortletClassDataModelProperties, IPluginWizardFragmentProperties {

	public AddPortletOperation(IDataModel dataModel, TemplateStore store, TemplateContextType type) {
		super(dataModel);

		IWebProject webproject = LiferayCore.create(IWebProject.class, getTargetProject());

		if (webproject != null) {
			webappRoot = webproject.getDefaultDocrootFolder();
		}

		templateStore = store;
		portletContextType = type;
	}

	@Override
	public IStatus doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IStatus status = Status.OK_STATUS;

		if (getDataModel().getBooleanProperty(CREATE_NEW_PORTLET_CLASS)) {
			status = super.doExecute(monitor, info);
		}

		if (!status.isOK()) {
			return status;
		}

		if (getDataModel().getBooleanProperty(CREATE_RESOURCE_BUNDLE_FILE)) {
			try {
				createEmptyFileInDefaultSourceFolder(
					getDataModel().getStringProperty(CREATE_RESOURCE_BUNDLE_FILE_PATH));
			}
			catch (CoreException ce) {
				status = PortletCore.createErrorStatus(ce);
			}
		}

		if (getDataModel().getBooleanProperty(CREATE_ENTRY_CLASS) &&
			getDataModel().getBooleanProperty(ADD_TO_CONTROL_PANEL)) {

			try {
				NewEntryClassOperation entryClassOperation = new NewEntryClassOperation(getDataModel());

				entryClassOperation.execute(monitor, info);
			}
			catch (ExecutionException ee) {
				status = PortletCore.createErrorStatus(ee);
			}
		}

		if (getDataModel().getBooleanProperty(CREATE_JSPS)) {
			status = createModeJSPFiles();
		}

		try {
			String cssFilePath = getDataModel().getStringProperty(CSS_FILE);

			if (!CoreUtil.isNullOrEmpty(cssFilePath)) {
				createEmptyFileInDocroot(cssFilePath);
			}

			String javascriptFilePath = getDataModel().getStringProperty(JAVASCRIPT_FILE);

			if (!CoreUtil.isNullOrEmpty(javascriptFilePath)) {
				createEmptyFileInDocroot(javascriptFilePath);
			}
		}
		catch (Exception ex) {
			status = PortletCore.createErrorStatus(ex);
		}

		return status;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IStatus status = doExecute(monitor, info);

		if (!status.isOK()) {
			return status;
		}

		generateMetaData(getDataModel());

		return Status.OK_STATUS;
	}

	protected IStatus addNewPortlet(IProject project, IDataModel model) {
		return ProjectCore.operate(project, AddNewPortletOperation.class, model);
	}

	protected void createEmptyFileInDefaultSourceFolder(String filePath) throws CoreException {
		List<IFolder> sourceFolders = CoreUtil.getSourceFolders(JavaCore.create(getTargetProject()));

		if (ListUtil.isNotEmpty(sourceFolders)) {
			IFile projectFile = sourceFolders.get(0).getFile(filePath);

			if (!projectFile.exists()) {
				IFolder parent = (IFolder)projectFile.getParent();

				CoreUtil.prepareFolder(parent);

				try(InputStream input = new ByteArrayInputStream(new byte[0])){
					projectFile.create(input, IResource.FORCE, null);
				}
				catch (IOException e) {
					throw new CoreException(PortletUIPlugin.createErrorStatus(e));
				}
			}
		}
	}

	protected void createEmptyFileInDocroot(String filePath) throws CoreException {
		IFile projectFile = getProjectFile(filePath);

		if (!projectFile.exists()) {
			CoreUtil.createEmptyFile(projectFile);
		}
	}

	@SuppressWarnings("unchecked")
	protected IStatus createModeJSPFiles() {
		IDataModel dm = getDataModel();

		TemplateContext context = new DocumentTemplateContext(portletContextType, new Document(), 0, 0);

		context.setVariable("portlet_display_name", getDataModel().getStringProperty(DISPLAY_NAME));

		List<ParamValue> initParams = (List<ParamValue>)getDataModel().getProperty(INIT_PARAMS);

		String initParamSuffix = null;

		ParamValue paramValue = initParams.get(0);

		if (INIT_NAME_61[0].equals(paramValue.getName())) {
			initParamSuffix = "template";
		}
		else {
			initParamSuffix = "jsp";
		}

		if (dm.getBooleanProperty(ABOUT_MODE)) {
			createResourceForMode("about-" + initParamSuffix, ABOUT_MODE_TEMPLATE, context);
		}

		if (dm.getBooleanProperty(CONFIG_MODE)) {
			createResourceForMode("config-" + initParamSuffix, CONFIG_MODE_TEMPLATE, context);
		}

		if (dm.getBooleanProperty(EDIT_MODE)) {
			createResourceForMode("edit-" + initParamSuffix, EDIT_MODE_TEMPLATE, context);
		}

		if (dm.getBooleanProperty(EDITDEFAULTS_MODE)) {
			createResourceForMode("edit-defaults-" + initParamSuffix, EDITDEFAULTS_MODE_TEMPLATE, context);
		}

		if (dm.getBooleanProperty(EDITGUEST_MODE)) {
			createResourceForMode("edit-guest-" + initParamSuffix, EDITGUEST_MODE_TEMPLATE, context);
		}

		if (dm.getBooleanProperty(HELP_MODE)) {
			createResourceForMode("help-" + initParamSuffix, HELP_MODE_TEMPLATE, context);
		}

		if (dm.getBooleanProperty(PREVIEW_MODE)) {
			createResourceForMode("preview-" + initParamSuffix, PREVIEW_MODE_TEMPLATE, context);
		}

		if (dm.getBooleanProperty(PRINT_MODE)) {
			createResourceForMode("print-" + initParamSuffix, PRINT_MODE_TEMPLATE, context);
		}

		if (dm.getBooleanProperty(VIEW_MODE)) {
			createResourceForMode("view-" + initParamSuffix, VIEW_MODE_TEMPLATE, context);
		}

		return Status.OK_STATUS;
	}

	@SuppressWarnings("unchecked")
	protected void createResourceForMode(String initParamName, String templateId, TemplateContext context) {
		Template template = templateStore.findTemplateById(templateId);

		String templateString = null;

		try {
			TemplateBuffer buffer = context.evaluate(template);

			templateString = buffer.getString();
		}
		catch (Exception ex) {
			PortletCore.logError(ex);

			return;
		}

		// need to get the path in the web app for the new jsp mode file

		IFile viewJspFile = null;

		List<ParamValue> initParams = (List<ParamValue>)getDataModel().getProperty(INIT_PARAMS);

		for (ParamValue paramValue : initParams) {
			if (paramValue.getName().equals(initParamName)) {
				viewJspFile = getProjectFile(paramValue.getValue());

				break;
			}
		}

		if (viewJspFile != null) {
			try (InputStream input = new ByteArrayInputStream(templateString.getBytes("UTF-8"))){
				if (viewJspFile.exists()) {
					viewJspFile.setContents(input, IResource.FORCE, null);
				}
				else {

					// make sure that full path to jspfile is available

					CoreUtil.prepareFolder((IFolder)viewJspFile.getParent());
					viewJspFile.create(input, IResource.FORCE, null);
				}
			}
			catch (Exception ex) {
				PortletCore.logError(ex);
			}
		}
	}

	protected IStatus generateMetaData(IDataModel aModel) {
		if (shouldGenerateMetaData(aModel)) {
			IProject project = getTargetProject();

			if (aModel.getBooleanProperty(REMOVE_EXISTING_ARTIFACTS)) {
				removeAllPortlets(project);
			}

			IStatus status = addNewPortlet(project, model);

			if (!status.isOK()) {
				PortletCore plugin = PortletCore.getDefault();

				plugin.getLog().log(status);

				return status;
			}
		}

		return Status.OK_STATUS;
	}

	@Override
	protected NewJavaEEArtifactClassOperation getNewClassOperation() {
		return new NewPortletClassOperation(getDataModel());
	}

	protected IFile getProjectFile(String filePath) {
		IFile retval = null;

		if (webappRoot != null) {
			retval = this.webappRoot.getFile(new Path(filePath));
		}

		return retval;
	}

	protected IStatus removeAllPortlets(IProject project) {
		return ProjectCore.operate(project, RemoveAllPortletsOperation.class);
	}

	protected boolean shouldGenerateMetaData(IDataModel aModel) {
		return ProjectUtil.isPortletProject(getTargetProject());
	}

	protected TemplateContextType portletContextType;
	protected TemplateStore templateStore;
	protected IFolder webappRoot;

}