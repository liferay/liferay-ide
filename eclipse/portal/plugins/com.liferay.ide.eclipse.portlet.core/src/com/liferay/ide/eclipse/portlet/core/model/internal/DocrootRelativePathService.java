/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Inc., All rights reserved.
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
 *
 * Contributors:
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.RelativePathService;

import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class DocrootRelativePathService extends RelativePathService {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.RelativePathService#roots()
	 */
	@SuppressWarnings( "unchecked" )
	@Override
	public List<Path> roots() {
		IProject project = adapt( IProject.class );
		IClasspathEntry[] classpathEntries = PortletUtil.getClasspathEntries( project );
		if ( classpathEntries != null ) {
			List<Path> roots = new ArrayList<Path>();
			for ( int i = 0; i < classpathEntries.length; i++ ) {
				IClasspathEntry classpathEntry = classpathEntries[i];
				if ( IClasspathEntry.CPE_SOURCE == classpathEntry.getEntryKind() ) {
					IPath entryPath = classpathEntry.getPath();
					roots.add( Path.fromOSString( entryPath.toOSString() ) );

				}
			}
			return roots;
		}
		else {
			return Collections.EMPTY_LIST;
		}

	}
}
