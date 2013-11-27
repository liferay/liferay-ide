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

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.SupportedLocales;
import com.liferay.ide.portlet.core.util.PortletUtil;

/**
 * @author Kamesh Sampath
 * @author Kuo Zhang
 */
public class LocaleBundleValidationService extends ValidationService
{

    private FilteredListener<PropertyContentEvent> listener;
    
    /*
     * IDE-1132 ,Add a listener to Property ResourceBundle, so LocaleBundle can be validated once ResourceBundle
     * gets changed.
     */
    protected void initValidationService()
    {
        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                if( !context( SupportedLocales.class ).disposed() )
                {
                    refresh();
                }
            }
        };

        context( SupportedLocales.class ).nearest( Portlet.class ).getResourceBundle().attach( this.listener );
    }


    public void forceRefresh()
    {
        if( !context( SupportedLocales.class ).disposed() )
        {
            refresh();
        }
    }

    public Status compute()
    {
        final Element modelElement = context( Element.class );

        if( ! modelElement.disposed() && modelElement instanceof SupportedLocales )
        {
            final IProject project = modelElement.adapt( IProject.class );
            final Portlet portlet = modelElement.nearest( Portlet.class );
            final IWorkspaceRoot wroot = ResourcesPlugin.getWorkspace().getRoot();
            final IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries( project );

            if( cpEntries != null )
            {
                final String locale = modelElement.property( context( ValueProperty.class ) ).text( false );
                final Value<Path> resourceBundle = portlet.getResourceBundle();

                if( locale == null )
                {
                    return Status.createErrorStatus( Resources.localeMustNotEmpty );
                }
                else
                {
                    final String bundleName = resourceBundle.text();

                    if( resourceBundle != null && bundleName != null )
                    {
                        final String localeString = PortletUtil.localeString( locale );
                        final String ioFileName =
                            PortletUtil.convertJavaToIoFileName( bundleName, RB_FILE_EXTENSION, localeString );

                        for( IClasspathEntry iClasspathEntry : cpEntries )
                        {
                            if( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() )
                            {
                                IPath entryPath = wroot.getFolder( iClasspathEntry.getPath() ).getLocation();
                                entryPath = entryPath.append( ioFileName );
                                IFile resourceBundleFile = wroot.getFileForLocation( entryPath );
                                if( resourceBundleFile != null && resourceBundleFile.exists() )
                                {
                                    return Status.createOkStatus();
                                }
                                else
                                {
                                    return Status.createWarningStatus( Resources.bind(
                                        StringEscapeUtils.unescapeJava( Resources.noResourceBundle ), new Object[] {
                                            locale, bundleName, localeString } ) );
                                }

                            }
                        }
                    }
                }
            }
        }

        return Status.createOkStatus();
    }

    @Override
    public void dispose()
    {
        context( SupportedLocales.class ).nearest( Portlet.class ).getResourceBundle().detach( this.listener );
    }
    
    private static final class Resources extends NLS
    {
        public static String localeMustNotEmpty;
        public static String noResourceBundle;

        static
        {
            initializeMessages( LocaleBundleValidationService.class.getName(), Resources.class );
        }
    }
}
