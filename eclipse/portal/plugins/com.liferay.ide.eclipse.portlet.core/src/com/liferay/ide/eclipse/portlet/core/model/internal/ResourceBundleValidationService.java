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

import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelPropertyValidationService;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;

import com.liferay.ide.eclipse.portlet.core.model.IPortlet;
import com.liferay.ide.eclipse.portlet.core.model.IResourceBundle;
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
			String ioFileName = PortletUtil.convertToFileName( text );
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
			if ( isResourceBundleForDefaultLocale( element(), text ) ) {
				return Status.createOkStatus();
			}
			else {
				return Status.createWarningStatus( Resources.bind( Resources.noDefaultRBMessage, new Object[] {
					Locale.getDefault().getDisplayName(), Locale.getDefault().toString() } ) );
			}

		}
		else {
			return Status.createErrorStatus( Resources.bind( Resources.message, text ) );
		}

	}

	/**
	 * @param iModelElement
	 * @param newBundleName
	 * @return
	 */
	private boolean isResourceBundleForDefaultLocale( IModelElement iModelElement, String newBundleName ) {
		IModelParticle parent = iModelElement.parent() != null ? iModelElement.parent().parent() : null;
		if ( parent != null && parent instanceof IPortlet ) {
			IPortlet portlet = (IPortlet) parent;
			List<IResourceBundle> resourceBundles = portlet.getResourceBundles();
			// Check for default locale
			String bundleName = null;
			for ( IResourceBundle iResourceBundle : resourceBundles ) {
				bundleName = iResourceBundle.getResourceBundle().getText();
				if ( bundleName != null && bundleName.lastIndexOf( '.' ) != -1 ) {
					bundleName = bundleName.substring( bundleName.lastIndexOf( '.' ), bundleName.length() );
					if ( bundleName.indexOf( "_" ) == -1 ) {
						return true;
					}
				}

			}

			// Also check the newly added bundle
			bundleName = newBundleName;
			if ( bundleName != null && bundleName.lastIndexOf( '.' ) != -1 ) {
				bundleName = bundleName.substring( bundleName.lastIndexOf( '.' ), bundleName.length() );
				if ( bundleName.indexOf( "_" ) == -1 ) {
					return true;
				}
			}

		}
		else {
			// As this is not applicable for other elements we return true always
			return true;
		}
		return false;
	}

	private static final class Resources extends NLS {

		public static String message;
		public static String noDefaultRBMessage;

		static {
			initializeMessages( ResourceBundleValidationService.class.getName(), Resources.class );
		}
	}
}
