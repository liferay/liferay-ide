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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.hook.core.model.CustomJsp;
import com.liferay.ide.hook.core.util.HookUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Resource;

/**
 * @author Gregory Amerson
 */
public class CustomJspsBindingImpl extends HookListBindingImpl {

	@Override
	public void init(Property property) {
		super.init(property);

		ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project());

		if (liferayProject != null) {
			ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

			if (portal != null) {
				_portalDir = portal.getAppServerPortalDir();
			}
		}
	}

	@Override
	public void remove(Resource resource) {
		CustomJspResource customJspResource = resource.adapt(CustomJspResource.class);

		ObjectValue<String> customJsp = customJspResource.getCustomJsp();

		_customJsps.remove(customJsp);
	}

	@Override
	public ElementType type(Resource resource) {
		if (resource instanceof CustomJspResource) {
			return CustomJsp.TYPE;
		}

		return null;
	}

	@Override
	protected Object insertUnderlyingObject(ElementType type, int position) {
		ObjectValue<String> retval = null;

		if (type.equals(CustomJsp.TYPE)) {
			retval = new ObjectValue<>();

			_customJsps.add(retval);
		}

		return retval;
	}

	@Override
	protected List<?> readUnderlyingList() {
		IFolder customJspFolder = HookUtil.getCustomJspFolder(hook(), project());

		if ((customJspFolder == null) || (_portalDir == null)) {
			_lastCustomJspDirPath = null;

			return Collections.emptyList();
		}

		IPath customJspDirPath = customJspFolder.getProjectRelativePath();

		if ((customJspDirPath != null) && customJspDirPath.equals(_lastCustomJspDirPath)) {
			return _customJsps;
		}

		_customJsps = new ArrayList<>();

		_lastCustomJspDirPath = customJspDirPath;

		IFile[] customJspFiles = _getCustomJspFiles();

		for (IFile customJspFile : customJspFiles) {
			IPath customJspFilePath = customJspFile.getProjectRelativePath();

			int index = customJspFilePath.matchingFirstSegments(customJspDirPath);

			IPath customJspPath = customJspFilePath.removeFirstSegments(index);

			_customJsps.add(new ObjectValue<String>(customJspPath.toPortableString()));
		}

		return _customJsps;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Resource resource(Object obj) {
		Element element = property().element();

		Resource resource = element.resource();

		return new CustomJspResource(resource, (ObjectValue<String>)obj);
	}

	private void _findJspFiles(IFolder folder, List<IFile> jspFiles) throws CoreException {
		if (FileUtil.notExists(folder)) {
			return;
		}

		IResource[] members = folder.members(IResource.FOLDER | IResource.FILE);

		for (IResource member : members) {
			if ((member instanceof IFile) && "jsp".equals(member.getFileExtension())) {
				jspFiles.add((IFile)member);
			}
			else if (member instanceof IFolder) {
				_findJspFiles((IFolder)member, jspFiles);
			}
		}
	}

	private IFile[] _getCustomJspFiles() {
		List<IFile> customJspFiles = new ArrayList<>();

		IFolder customJspFolder = HookUtil.getCustomJspFolder(hook(), project());

		try {
			_findJspFiles(customJspFolder, customJspFiles);
		}
		catch (CoreException ce) {
			ce.printStackTrace();
		}

		return customJspFiles.toArray(new IFile[0]);
	}

	private List<ObjectValue<String>> _customJsps;
	private IPath _lastCustomJspDirPath;
	private IPath _portalDir;

}