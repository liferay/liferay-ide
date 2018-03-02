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

package com.liferay.ide.hook.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.hook.ui.HookUI;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.forms.PropertyEditorActionHandler;
import org.eclipse.sapphire.ui.forms.PropertyEditorCondition;
import org.eclipse.sapphire.ui.forms.PropertyEditorPart;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class CreateSrcFileActionHandler extends PropertyEditorActionHandler {

	@Override
	public Object run(Presentation context) {
		IFile file = _getSrcFile();

		try {
			if (!file.exists()) {
				InputStream defaultContentStream = new ByteArrayInputStream(StringPool.EMPTY.getBytes());

				file.create(defaultContentStream, true, null);

				try {
					file.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
				catch (Exception e) {
					HookUI.logError(e);
				}

				ValueProperty valueProperty = ValueProperty.class.cast(property().definition());

				// do this so that the downstream properties can update their
				// enablement/validation/etc.

				Value<Object> value = getModelElement().property(valueProperty);

				Object content = value.content();

				value.clear();
				value.write(content);

				refreshEnablementState();
			}
		}
		catch (Exception e) {
			HookUI.logError("Unable to create src file: " + file.getName(), e);
		}

		return null;
	}

	public static class Condition extends PropertyEditorCondition {

		@Override
		protected boolean evaluate(PropertyEditorPart part) {
			Property property = part.property();
			Element element = part.getModelElement();

			if ((property.definition() instanceof ValueProperty) && (element != null) &&
				property.definition().isOfType(Path.class)) {

				ValidFileSystemResourceType typeAnnotation =
					property.definition().getAnnotation(ValidFileSystemResourceType.class);

				if ((typeAnnotation != null) && (typeAnnotation.value() == FileSystemResourceType.FILE)) {
					return true;
				}
			}

			return false;
		}

	}

	@Override
	protected boolean computeEnablementState() {
		boolean enabled = super.computeEnablementState();

		if ((getModelElement() != null) && enabled) {

			// check for existence of the file

			IFile srcFile = _getSrcFile();

			if ((srcFile != null) && srcFile.exists()) {
				enabled = false;
			}
		}

		return enabled;
	}

	private IPath _getDefaultSrcFolderPath() {
		IProject project = getModelElement().adapt(IProject.class);

		List<IFolder> folders = CoreUtil.getSourceFolders(JavaCore.create(project));

		if (ListUtil.isNotEmpty(folders)) {
			return folders.get(0).getFullPath();
		}

		return null;
	}

	private IFile _getSrcFile() {
		IFile retval = null;

		ValueProperty valueProperty = ValueProperty.class.cast(property().definition());

		Value<Path> value = getModelElement().property(valueProperty);

		if ((value != null) && !CoreUtil.isNullOrEmpty(value.text())) {
			IPath defaultSrcFolderPath = _getDefaultSrcFolderPath();

			if (defaultSrcFolderPath != null) {
				IPath filePath = defaultSrcFolderPath.append(value.text());

				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

				retval = root.getFile(filePath);
			}
		}

		return retval;
	}

}