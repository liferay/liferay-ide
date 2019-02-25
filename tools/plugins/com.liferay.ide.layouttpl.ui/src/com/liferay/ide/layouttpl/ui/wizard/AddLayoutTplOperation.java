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

package com.liferay.ide.layouttpl.ui.wizard;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.templates.ITemplateContext;
import com.liferay.ide.core.templates.ITemplateOperation;
import com.liferay.ide.core.templates.TemplatesCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.operation.INewLayoutTplDataModelProperties;
import com.liferay.ide.layouttpl.core.operation.LayoutTplDescriptorHelper;
import com.liferay.ide.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.layouttpl.ui.util.LayoutTemplatesFactory;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.wizard.LiferayDataModelOperation;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.net.URL;

import org.apache.commons.collections.ArrayStack;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class AddLayoutTplOperation
	extends LiferayDataModelOperation implements INewLayoutTplDataModelProperties, SapphireContentAccessor {

	public AddLayoutTplOperation(IDataModel model, TemplateStore templateStore, TemplateContextType contextType) {
		super(model, templateStore, contextType);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IStatus retval = null;

		IDataModel dm = getDataModel();

		String diagramClassName = dm.getStringProperty(LAYOUT_TEMPLATE_ID);

		LayoutTplElement diagramModel = createLayoutTplDigram(dm, _isBootstrapStyle(), _is62(), diagramClassName);

		try {
			IFile templateFile = null;

			String templateFileName = getDataModel().getStringProperty(LAYOUT_TEMPLATE_FILE);

			if (!CoreUtil.isNullOrEmpty(templateFileName)) {
				templateFile = createTemplateFile(templateFileName, diagramModel);
			}

			getDataModel().setProperty(LAYOUT_TPL_FILE_CREATED, templateFile);

			String wapTemplateFileName = getDataModel().getStringProperty(LAYOUT_WAP_TEMPLATE_FILE);

			diagramModel.setClassName(diagramClassName + ".wap");

			if (!CoreUtil.isNullOrEmpty(wapTemplateFileName) && _is62()) {
				createTemplateFile(wapTemplateFileName, diagramModel);
			}

			String thumbnailFileName = getDataModel().getStringProperty(LAYOUT_THUMBNAIL_FILE);

			if (!CoreUtil.isNullOrEmpty(thumbnailFileName)) {
				createThumbnailFile(thumbnailFileName);
			}
		}
		catch (CoreException ce) {
			LayoutTplUI.logError(ce);

			return LayoutTplUI.createErrorStatus(ce);
		}
		catch (IOException ioe) {
			LayoutTplUI.logError(ioe);

			return LayoutTplUI.createErrorStatus(ioe);
		}

		LayoutTplDescriptorHelper layoutTplDescHelper = new LayoutTplDescriptorHelper(getTargetProject());

		retval = layoutTplDescHelper.addNewLayoutTemplate(dm);

		return retval;
	}

	public IProject getTargetProject() {
		String projectName = model.getStringProperty(PROJECT_NAME);

		return ProjectUtil.getProject(projectName);
	}

	protected LayoutTplElement createLayoutTplDigram(
		IDataModel dm, boolean bootstrapStyle, boolean is62, String className) {

		LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

		layoutTpl.setBootstrapStyle(bootstrapStyle);
		layoutTpl.setClassName(className);
		layoutTpl.setIs62(is62);

		LayoutTemplatesFactory layoutTemplatesFactory = new LayoutTemplatesFactory();

		if (dm.getBooleanProperty(LAYOUT_IMAGE_1_COLUMN)) {
			layoutTemplatesFactory.add_Layout_1(layoutTpl);
		}
		else if (dm.getBooleanProperty(LAYOUT_IMAGE_1_2_I_COLUMN)) {
			layoutTemplatesFactory.add_Layout_1_2_I(layoutTpl);
		}
		else if (dm.getBooleanProperty(LAYOUT_IMAGE_1_2_II_COLUMN)) {
			layoutTemplatesFactory.add_Layout_1_2_II(layoutTpl);
		}
		else if (dm.getBooleanProperty(LAYOUT_IMAGE_1_2_1_COLUMN)) {
			layoutTemplatesFactory.add_Layout_1_2_1(layoutTpl);
		}
		else if (dm.getBooleanProperty(LAYOUT_IMAGE_2_I_COLUMN)) {
			layoutTemplatesFactory.add_Layout_2_I(layoutTpl);
		}
		else if (dm.getBooleanProperty(LAYOUT_IMAGE_2_II_COLUMN)) {
			layoutTemplatesFactory.add_Layout_2_II(layoutTpl);
		}
		else if (dm.getBooleanProperty(LAYOUT_IMAGE_2_III_COLUMN)) {
			layoutTemplatesFactory.add_Layout_2_III(layoutTpl);
		}
		else if (dm.getBooleanProperty(LAYOUT_IMAGE_2_2_COLUMN)) {
			layoutTemplatesFactory.add_Layout_2_2(layoutTpl);
		}
		else if (dm.getBooleanProperty(LAYOUT_IMAGE_3_COLUMN)) {
			layoutTemplatesFactory.add_Layout_3(layoutTpl);
		}

		return layoutTpl;
	}

	protected IFile createTemplateFile(String templateFileName, LayoutTplElement element) throws CoreException {
		IFolder defaultDocroot = CoreUtil.getDefaultDocrootFolder(getTargetProject());

		IFile templateFile = defaultDocroot.getFile(templateFileName);

		if (element != null) {
			_saveToFile(element, templateFile, null);
		}
		else {
			try (ByteArrayInputStream input = new ByteArrayInputStream(StringPool.EMPTY.getBytes())) {
				if (FileUtil.exists(templateFile)) {
					templateFile.setContents(input, IResource.FORCE, null);
				}
				else {
					templateFile.create(input, true, null);
				}
			}
			catch (IOException ioe) {
				throw new CoreException(LayoutTplUI.createErrorStatus(ioe));
			}
		}

		return templateFile;
	}

	protected void createThumbnailFile(String thumbnailFileName) throws CoreException, IOException {
		IFolder defaultDocroot = CoreUtil.getDefaultDocrootFolder(getTargetProject());

		IFile thumbnailFile = defaultDocroot.getFile(thumbnailFileName);

		LayoutTplUI defaultUI = LayoutTplUI.getDefault();

		Bundle bundle = defaultUI.getBundle();

		URL iconFileURL = bundle.getEntry("/icons/blank_columns.png");

		CoreUtil.prepareFolder((IFolder)thumbnailFile.getParent());

		if (FileUtil.exists(thumbnailFile)) {
			thumbnailFile.setContents(iconFileURL.openStream(), IResource.FORCE, null);
		}
		else {
			thumbnailFile.create(iconFileURL.openStream(), true, null);
		}
	}

	private static void _createLayoutTplContext(ITemplateOperation op, LayoutTplElement layouttpl) {
		ITemplateContext ctx = op.getContext();

		ctx.put("root", layouttpl);
		ctx.put("stack", new ArrayStack());
	}

	private boolean _is62() {
		IProject project = getTargetProject();

		Version version = Version.parseVersion(LiferayDescriptorHelper.getDescriptorVersion(project));

		if (CoreUtil.compareVersions(version, ILiferayConstants.V620) == 0) {
			return true;
		}

		return false;
	}

	private boolean _isBootstrapStyle() {
		ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, getTargetProject());

		ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

		if (portal != null) {
			Version version = Version.parseVersion(portal.getVersion());

			if (CoreUtil.compareVersions(version, ILiferayConstants.V620) >= 0) {
				return true;
			}

			return false;
		}

		return true;
	}

	private void _saveToFile(LayoutTplElement diagramElement, IFile file, IProgressMonitor monitor) {
		try {
			ITemplateOperation op = null;

			if (get(diagramElement.getBootstrapStyle())) {
				op = TemplatesCore.getTemplateOperation("com.liferay.ide.layouttpl.core.layoutTemplate.bootstrap");
			}
			else {
				op = TemplatesCore.getTemplateOperation("com.liferay.ide.layouttpl.core.layoutTemplate.legacy");
			}

			_createLayoutTplContext(op, diagramElement);

			op.setOutputFile(file);
			op.execute(monitor);
		}
		catch (Exception e) {
			LayoutTplCore.logError(e);
		}
	}

}