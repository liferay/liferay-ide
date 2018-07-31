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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.model.PortalPropertiesFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Gregory Amerson
 */
public class HookMethods {

	public static IFile getPortalPropertiesFile(Hook hook) {
		return getPortalPropertiesFile(hook, true);
	}

	public static IFile getPortalPropertiesFile(Hook hook, boolean onlyIfExists) {
		IFile retval = null;

		if (hook != null) {
			ElementHandle<PortalPropertiesFile> handle = hook.getPortalPropertiesFile();

			PortalPropertiesFile portalPropertiesFileElement = handle.content();

			if (portalPropertiesFileElement != null) {
				Value<Path> pathValue = portalPropertiesFileElement.getValue();

				Path filePath = pathValue.content();

				if (filePath != null) {
					for (IFolder folder : CoreUtil.getSourceFolders(JavaCore.create(hook.adapt(IProject.class)))) {
						IFile file = folder.getFile(filePath.toPortableString());

						if (onlyIfExists) {
							if (FileUtil.exists(file)) {
								retval = file;
							}
						}
						else {
							retval = file;
						}
					}
				}
			}
		}

		return retval;
	}

}