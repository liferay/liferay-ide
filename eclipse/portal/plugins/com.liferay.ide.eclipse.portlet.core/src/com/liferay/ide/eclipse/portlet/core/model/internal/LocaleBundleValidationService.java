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

import java.util.List;
import java.util.Locale;
import java.util.Vector;

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
		if ( modelElement instanceof IPortlet ) {
			final IProject project = modelElement.adapt( IProject.class );
			final IPortlet portlet = modelElement.nearest( IPortlet.class );
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IWorkspaceRoot wroot = workspace.getRoot();
			IClasspathEntry[] cpEntries = PortletUtil.getClasspathEntries( project );
			String bundleName = target().getText( false );
			if ( bundleName != null ) {
				Vector<String> missingLocales = new Vector<String>();
				List<ISupportedLocales> supportedLocales = portlet.getSupportedLocales();
				if ( supportedLocales != null && !supportedLocales.isEmpty() ) {
					for ( IClasspathEntry iClasspathEntry : cpEntries ) {
						if ( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() ) {
							for ( ISupportedLocales iSupportedLocales : supportedLocales ) {
								IPath entryPath = wroot.getFolder( iClasspathEntry.getPath() ).getLocation();
								String supportedLocale = iSupportedLocales.getSupportedLocale().getText( false );
								if ( supportedLocale != null ) {
									String localeString = PortletUtil.localeString( supportedLocale );
									String ioFileName =
										PortletUtil.convertJavaToIoFileName(
											bundleName, RB_FILE_EXTENSION, localeString );
									entryPath = entryPath.append( ioFileName );
									IFile resourceBundleFile = wroot.getFileForLocation( entryPath );
									// System.out.println( "ResourceBundleValidationService.validate():" +
									// resourceBundleFile );
									if ( resourceBundleFile != null && !resourceBundleFile.exists() ) {
										missingLocales.add( supportedLocale );
									}
								}

							}

						}
					}

					if ( !missingLocales.isEmpty() ) {
						StringBuilder msgMissingLocales = new StringBuilder();
						String sampleLocale = "";
						for ( String missingLocale : missingLocales ) {
							sampleLocale = PortletUtil.localeString( missingLocale );
							msgMissingLocales.append( PortletUtil.localeDisplayString( missingLocale ) );
							msgMissingLocales.append( "," );
						}

						return Status.createWarningStatus( Resources.bind( Resources.message, new Object[] {
							msgMissingLocales.toString(), sampleLocale } ) );
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
