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

package com.liferay.ide.hook.ui.wizard;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchScope;

/**
 * @author Gregory Amerson
 */
public class PortalServiceSearchScope implements IJavaSearchScope {

	public PortalServiceSearchScope() {
	}

	public boolean encloses(IJavaElement element) {
		if (element != null) {
			IPath elementPath = element.getPath();

			if (elementPath != null) {
				for (IPath enclosingJar : enclosingJars) {
					if (elementPath.lastSegment().equals(enclosingJar.lastSegment())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean encloses(String resourcePath) {
		IPath path = new Path(resourcePath);

		if (path != null) {
			for (String pattern : resourcePatterns) {
				if (path.toPortableString().matches(pattern)) {
					return true;
				}
			}
		}

		return false;
	}

	public IPath[] enclosingProjectsAndJars() {
		return enclosingJars;
	}

	public boolean includesBinaries() {
		return true;
	}

	public boolean includesClasspaths() {
		return true;
	}

	public void setEnclosingJarPaths(IPath[] jarPaths) {
		enclosingJars = jarPaths;
	}

	public void setIncludesBinaries(boolean includesBinaries) {
	}

	public void setIncludesClasspaths(boolean includesClasspaths) {
	}

	public void setResourcePattern(String[] patterns) {
		resourcePatterns = patterns;
	}

	protected IPath[] enclosingJars;
	protected String[] resourcePatterns;

}