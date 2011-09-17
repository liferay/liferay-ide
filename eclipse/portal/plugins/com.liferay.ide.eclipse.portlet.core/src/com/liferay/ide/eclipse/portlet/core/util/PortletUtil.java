/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. and Accenture Services Pvt Ltd., All rights reserved.
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
 *  Greg Amerson - Initial API and Implementation
 *  Kamesh Sampath - [IDE-301, IDE-405] Portlet xml editor implementation and reorgnization
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.util;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;

import com.liferay.ide.eclipse.core.util.CoreUtil;

/**
 * @author Greg Amerson
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@SuppressWarnings( "restriction" )
public class PortletUtil {

	public static IFolder getFirstSrcFolder( IProject project ) {
		@SuppressWarnings( "deprecation" )
		IPackageFragmentRoot[] sourceFolders = J2EEProjectUtilities.getSourceContainers( project );

		if ( sourceFolders != null && sourceFolders.length > 0 ) {
			IResource resource = sourceFolders[0].getResource();

			return resource instanceof IFolder ? (IFolder) resource : null;
		}

		return null;
	}

	public static IFolder getFirstSrcFolder( String projectName ) {
		IProject project = CoreUtil.getProject( projectName );

		return getFirstSrcFolder( project );
	}

	/**
	 * @param value
	 * @return
	 */
	public static String stripPrefix( String value ) {
		String strippedValue = value;
		int colonIndex = value.indexOf( PortletAppModelConstants.COLON );
		if ( colonIndex != -1 ) {
			strippedValue = strippedValue.substring( colonIndex + 1, strippedValue.length() );
		}
		return strippedValue;

	}

	/**
	 * @param project
	 * @return
	 */
	public static IClasspathEntry[] getClasspathEntries( IProject project ) {
		if ( project != null ) {
			IJavaProject javaProject = JavaCore.create( project );
			try {
				IClasspathEntry[] classPathEntries = javaProject.getRawClasspath();
				return classPathEntries;
			}
			catch ( JavaModelException e ) {
				// TODO log the exception
			}
		}
		return null;
	}

}
