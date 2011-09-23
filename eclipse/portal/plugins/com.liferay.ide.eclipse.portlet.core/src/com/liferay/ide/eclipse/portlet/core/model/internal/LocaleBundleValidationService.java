/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt. Ltd., All rights reserved.
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

import static com.liferay.ide.eclipse.portlet.core.model.internal.ResourceBundleRelativePathService.RB_FILE_EXTENSION;

import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelPropertyValidationService;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;

import com.liferay.ide.eclipse.portlet.core.model.IPortlet;
import com.liferay.ide.eclipse.portlet.core.model.ISupportedLocales;
import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class LocaleBundleValidationService extends ModelPropertyValidationService<Value<Path>> {

	final Locale[] AVAILABLE_LOCALES = Locale.getAvailableLocales();
	final Locale DEFAULT_LOCALE = Locale.getDefault();

	/**
	 * 
	 */
	@Override
	public Status validate() {
		IModelElement modelElement = element();
		if ( modelElement instanceof ISupportedLocales ) {
			final IProject project = modelElement.adapt( IProject.class );
			final IPortlet portlet = modelElement.nearest( IPortlet.class );
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IWorkspaceRoot wroot = workspace.getRoot();
			IClasspathEntry[] cpEntries = PortletUtil.getClasspathEntries( project );
			String locale = target().getText( false );
			Value<Path> resourceBundle = portlet.getResourceBundle();
			if ( locale != null && resourceBundle != null ) {
				String bundleName = resourceBundle.getText();
				String localeString = PortletUtil.localeString( locale );
				String ioFileName = PortletUtil.convertJavaToIoFileName( bundleName, RB_FILE_EXTENSION, localeString );
				for ( IClasspathEntry iClasspathEntry : cpEntries ) {
					if ( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() ) {
						IPath entryPath = wroot.getFolder( iClasspathEntry.getPath() ).getLocation();
						entryPath = entryPath.append( ioFileName );
						IFile resourceBundleFile = wroot.getFileForLocation( entryPath );
						if ( resourceBundleFile != null && !resourceBundleFile.exists() ) {
							return Status.createWarningStatus( Resources.bind(
								StringEscapeUtils.unescapeJava( Resources.message ), new Object[] { locale, bundleName,
									localeString } ) );
						}
					}
				}
			}

		}
		return Status.createOkStatus();
	}

	private static final class Resources extends NLS {

		public static String message;
		static {
			initializeMessages( LocaleBundleValidationService.class.getName(), Resources.class );
		}
	}
}
