/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model.internal;

import static com.liferay.ide.core.model.internal.GenericResourceBundlePathService.RB_FILE_EXTENSION;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.SupportedLocales;
import com.liferay.ide.portlet.core.util.PortletUtil;

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
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Kamesh Sampath
 */
public class LocaleBundleValidationService extends ValidationService
{

    final Locale[] AVAILABLE_LOCALES = Locale.getAvailableLocales();
    final Locale DEFAULT_LOCALE = Locale.getDefault();

    /**
	 *
	 */
    @Override
    public Status validate()
    {
        Element modelElement = context( Element.class );

        if( modelElement instanceof SupportedLocales )
        {
            final IProject project = modelElement.adapt( IProject.class );
            final Portlet portlet = modelElement.nearest( Portlet.class );
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            final IWorkspaceRoot wroot = workspace.getRoot();
            IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries( project );
            if( cpEntries != null )
            {
                String locale = modelElement.property( context( ValueProperty.class ) ).text( false );
                Value<Path> resourceBundle = portlet.getResourceBundle();
                if( locale != null && resourceBundle != null )
                {
                    String bundleName = resourceBundle.text();
                    String localeString = PortletUtil.localeString( locale );
                    String ioFileName =
                        PortletUtil.convertJavaToIoFileName( bundleName, RB_FILE_EXTENSION, localeString );
                    for( IClasspathEntry iClasspathEntry : cpEntries )
                    {
                        if( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() )
                        {
                            IPath entryPath = wroot.getFolder( iClasspathEntry.getPath() ).getLocation();
                            entryPath = entryPath.append( ioFileName );
                            IFile resourceBundleFile = wroot.getFileForLocation( entryPath );
                            if( resourceBundleFile != null && !resourceBundleFile.exists() )
                            {
                                return Status.createWarningStatus( Resources.bind(
                                    StringEscapeUtils.unescapeJava( Resources.message ), new Object[] { locale,
                                        bundleName, localeString } ) );
                            }
                        }
                    }
                }
            }

        }
        return Status.createOkStatus();
    }

    private static final class Resources extends NLS
    {
        public static String message;
        static
        {
            initializeMessages( LocaleBundleValidationService.class.getName(), Resources.class );
        }
    }
}
