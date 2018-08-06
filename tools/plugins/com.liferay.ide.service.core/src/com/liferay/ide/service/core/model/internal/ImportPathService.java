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

package com.liferay.ide.service.core.model.internal;

import java.io.File;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.services.RelativePathService;

/**
 * @author Gregory Amerson
 */
public class ImportPathService extends RelativePathService {

	@Override
	public Path convertToAbsolute(Path path) {
		if (path != null) {
			IFile file = context(Element.class).adapt(IFile.class);

			if (file != null) {
				IContainer parent = file.getParent();

				IPath baseLocation = parent.getLocation();

				IPath absoluteLocation = baseLocation.append(PathBridge.create(path));

				Path absolute = PathBridge.create(absoluteLocation.makeAbsolute());

				return absolute;
			}
		}

		return super.convertToAbsolute(path);
	}

	@Override
	public Path convertToRelative(Path path) {
		if (path != null) {
			IFile file = context(Element.class).adapt(IFile.class);

			if (file != null) {
				IContainer parent = file.getParent();

				IPath baseLocation = parent.getLocation();

				if (baseLocation != null) {
					return path.makeRelativeTo(PathBridge.create(baseLocation));
				}
			}
		}

		return null;
	}

	@Override
	public boolean enclosed() {
		return false;
	}

	@Override
	public List<Path> roots() {
		File file = context(Element.class).adapt(File.class);

		if (file == null) {
			return Collections.emptyList();
		}
		else {
			return Collections.singletonList(new Path(file.getParent()));
		}
	}

}