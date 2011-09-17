/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Services Pvt Ltd., All rights reserved.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.ModelPropertyValidationService;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;

import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class ResourceBundleValidationService extends ModelPropertyValidationService<Value<Path>> {

	@Override
	public Status validate() {
		IProject project = adapt( IProject.class );
		final String text = target().getText( false );
		// System.out.println( "ResourceBundleValidationService.validate() - text:" + text );
		boolean resourceBundleExists = true;
		if ( text != null ) {

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot wroot = workspace.getRoot();
			IClasspathEntry[] cpEntries = PortletUtil.getClasspathEntries( project );
			String ioFileName = convertToFileName( text );
			for ( IClasspathEntry iClasspathEntry : cpEntries ) {
				if ( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() ) {
					IPath entryPath = wroot.getFolder( iClasspathEntry.getPath() ).getLocation();
					entryPath = entryPath.append( ioFileName );
					IFile resourceBundleFile = wroot.getFileForLocation( entryPath );
					// System.out.println( "ResourceBundleValidationService.validate():" + resourceBundleFile );
					if ( resourceBundleFile != null && resourceBundleFile.exists() ) {
						resourceBundleExists = true;
						break;
					}
					else {
						resourceBundleExists = false;
					}
				}
			}

		}

		if ( resourceBundleExists ) {
			return Status.createOkStatus();
		}
		else {
			return Status.createErrorStatus( Resources.bind( Resources.message, text ) );
		}

	}

	/**
	 * This method is used to convert the java package name file to a io file name e.g. com.liferay.Test will be
	 * returned as com/liferay/Test.properties
	 * 
	 * @param value
	 *            - the resource bundle name without .properties
	 * @return - actual io file name like value.properties
	 */
	private String convertToFileName( String value ) {
		// Replace all "." by "/"
		String strFileName = value.replace( '.', '/' );
		// Attach extension
		strFileName = strFileName + ".properties";
		return strFileName;
	}

	private static final class Resources extends NLS {

		public static String message;

		static {
			initializeMessages( ResourceBundleValidationService.class.getName(), Resources.class );
		}
	}
}
