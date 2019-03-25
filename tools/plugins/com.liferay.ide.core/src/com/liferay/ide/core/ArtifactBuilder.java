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

package com.liferay.ide.core;

import com.liferay.ide.core.util.FileUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public interface ArtifactBuilder {

	public default Artifact classpathEntryToArtifact(IClasspathEntry classpathEntry) {
		if (classpathEntry == null) {
			return null;
		}

		try {
			IPath path = classpathEntry.getPath();

			String[] segments = path.segments();

			//parse from file path "**/**/group name/artifact name/version/sha1 value/jar name"

			int length = segments.length;

			if (Version.valueOf(segments[length - 3]) != null) {
				return new Artifact(
					segments[length - 5], segments[length - 4], segments[length - 3], "classpath",
					FileUtil.getFile(classpathEntry.getSourceAttachmentPath()));
			}

			return null;
		}
		catch (Exception e) {
			return null;
		}
	}

}