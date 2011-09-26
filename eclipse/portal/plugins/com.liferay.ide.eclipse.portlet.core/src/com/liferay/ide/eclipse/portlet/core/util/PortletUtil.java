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

import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
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
	 * This method will return the first source folder of the Java project
	 * 
	 * @param javaProject
	 *            - the java project where the source folder needs to be indentified
	 * @return
	 * @throws JavaModelException
	 */
	public static IPackageFragmentRoot getSourceFolder( IJavaProject javaProject ) throws JavaModelException {
		for ( IPackageFragmentRoot root : javaProject.getPackageFragmentRoots() ) {
			if ( root.getKind() == IPackageFragmentRoot.K_SOURCE ) {
				return root;
			}
		}

		return null;
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

	/**
	 * @param text
	 * @return
	 */
	public static String localeString( String text ) {
		int dash = text.indexOf( "-" );
		String localeString = text.substring( dash + 1, text.length() );
		return localeString.trim();
	}

	/**
	 * @param text
	 * @return
	 */
	public static String localeDisplayString( String text ) {
		int dash = text.indexOf( "-" );
		String localeDisplatString = text.substring( 0, dash );
		return localeDisplatString.trim();
	}

	/**
	 * @param displayName
	 * @param locale
	 * @return
	 */
	public static String buildLocaleDisplayString( String displayName, Locale locale ) {
		StringBuilder builder = new StringBuilder( displayName );
		builder.append( " - " );
		builder.append( locale.toString() );
		return builder.toString();
	}

	/**
	 * this will convert the IO file name of the resource bundle to java package name
	 * 
	 * @param project
	 * @param value
	 *            - the io file name
	 * @return - the java package name of the resource bundle
	 */
	public static String convertIOToJavaFileName( IProject project, String value ) {
		String rbIOFile = value.substring( value.lastIndexOf( "/" ) + 1 );
		IFile resourceBundleFile = null;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot wroot = workspace.getRoot();
		IClasspathEntry[] cpEntries = PortletUtil.getClasspathEntries( project );
		for ( IClasspathEntry iClasspathEntry : cpEntries ) {
			if ( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() ) {
				IPath entryPath = wroot.getFolder( iClasspathEntry.getPath() ).getLocation();
				entryPath = entryPath.append( rbIOFile );
				resourceBundleFile = wroot.getFileForLocation( entryPath );
				// System.out.println( "ResourceBundleValidationService.validate():" + resourceBundleFile );
				if ( resourceBundleFile != null && resourceBundleFile.exists() ) {
					break;
				}
			}
		}

		String javaName = resourceBundleFile.getProjectRelativePath().toPortableString();
		if ( javaName.indexOf( '.' ) != -1 ) {
			// Strip the extension
			javaName = value.substring( 0, value.lastIndexOf( '.' ) );
			// Replace all "/" by "."
			javaName = javaName.replace( '/', '.' );
		}
		return javaName;
	}

	/**
	 * This method is used to convert the java package name file to a io file name e.g. com.liferay.Test will be
	 * returned as com/liferay/Test.properties
	 * 
	 * @param value
	 *            - the file name without extension in java packaging format
	 * @param extension
	 *            - the extension that needs to be attached to the file
	 * @param locales
	 *            - the locale String
	 * @return - actual io file name like value.<extension>
	 */
	public static String convertJavaToIoFileName( String value, String extension, String... locales ) {
		// Replace all "." by "/"
		String strFileName = value.replace( '.', '/' );
		// Attach extension
		if ( locales != null && locales.length > 0 ) {
			strFileName = strFileName + "_" + locales[0];
		}
		strFileName = strFileName + "." + extension;
		return strFileName;
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
	 * @param value
	 * @return
	 */
	public static String stripSuffix( String value ) {
		String strippedValue = value;
		int colonIndex = value.indexOf( PortletAppModelConstants.COLON );
		if ( colonIndex != -1 ) {
			strippedValue = strippedValue.substring( 0, colonIndex );
		}
		return strippedValue;

	}

}
