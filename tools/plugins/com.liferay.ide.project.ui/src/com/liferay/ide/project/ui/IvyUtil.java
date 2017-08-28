/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.project.ui;

import static org.eclipse.jst.j2ee.classpathdep.ClasspathDependencyUtil.getDefaultRuntimePath;
import static org.eclipse.jst.j2ee.classpathdep.ClasspathDependencyUtil.modifyDependencyPath;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.util.ComponentUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.ivyde.eclipse.cp.ClasspathSetup;
import org.apache.ivyde.eclipse.cp.IvyClasspathContainer;
import org.apache.ivyde.eclipse.cp.IvyClasspathContainerConfiguration;
import org.apache.ivyde.eclipse.cp.IvyClasspathContainerHelper;
import org.apache.ivyde.eclipse.cp.SettingsSetup;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.osgi.framework.Version;


/**
 * @author Gregory Amerson
 */
public class IvyUtil
{

    public static IvyClasspathContainer addIvyLibrary( IProject project, IProgressMonitor monitor )
    {
        final String projectName = project.getName();
        final IJavaProject javaProject = JavaCore.create( project );

        final IvyClasspathContainerConfiguration conf =
            new IvyClasspathContainerConfiguration( javaProject, ISDKConstants.IVY_XML_FILE, true );
        final ClasspathSetup classpathSetup = new ClasspathSetup();

        conf.setAdvancedProjectSpecific( false );
        conf.setClasspathSetup( classpathSetup );
        conf.setClassthProjectSpecific( false );
        conf.setConfs( Collections.singletonList( "*" ) ); //$NON-NLS-1$
        conf.setMappingProjectSpecific( false );
        conf.setSettingsProjectSpecific( true );

        SDK sdk = SDKUtil.getSDK( project );
        final SettingsSetup settingsSetup = new SettingsSetup();

        if( sdk.getLocation().append( ISDKConstants.IVY_SETTINGS_XML_FILE ).toFile().exists() )
        {
            StringBuilder builder = new StringBuilder();
            builder.append( "${" ); //$NON-NLS-1$
            builder.append( ISDKConstants.VAR_NAME_LIFERAY_SDK_DIR );
            builder.append( ":" ); //$NON-NLS-1$
            builder.append( projectName );
            builder.append( "}/" ); //$NON-NLS-1$
            builder.append( ISDKConstants.IVY_SETTINGS_XML_FILE );
            settingsSetup.setIvySettingsPath( builder.toString() );
        }

        StringBuilder builder = new StringBuilder();
        builder.append( "${" ); //$NON-NLS-1$
        builder.append( ISDKConstants.VAR_NAME_LIFERAY_SDK_DIR );
        builder.append( ":" ); //$NON-NLS-1$
        builder.append( projectName );
        builder.append( "}/.ivy" ); //$NON-NLS-1$

        settingsSetup.setIvyUserDir( builder.toString() );
        conf.setIvySettingsSetup( settingsSetup );

        final IPath path = conf.getPath();
        final IClasspathAttribute[] atts = conf.getAttributes();

        final IClasspathEntry ivyEntry = JavaCore.newContainerEntry(path, null, atts, false);

        final IVirtualComponent virtualComponent = ComponentCore.createComponent( project );

        try
        {
            IClasspathEntry[] entries = javaProject.getRawClasspath();
            List<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>( Arrays.asList( entries ) );

            IPath runtimePath = getDefaultRuntimePath( virtualComponent, ivyEntry);

            // add the deployment assembly config to deploy ivy container to /WEB-INF/lib
            final IClasspathEntry cpeTagged = modifyDependencyPath( ivyEntry, runtimePath );

            newEntries.add( cpeTagged );
            entries = (IClasspathEntry[]) newEntries.toArray( new IClasspathEntry[newEntries.size()] );
            javaProject.setRawClasspath( entries, javaProject.getOutputLocation(), monitor );

            IvyClasspathContainer ivycp = IvyClasspathContainerHelper.getContainer( path, javaProject );

            return ivycp;
        }
        catch( JavaModelException e )
        {
            ProjectUI.logError( "Unable to add Ivy library container", e ); //$NON-NLS-1$
        }

        return null;
    }

    public static void addIvyNature( IProject project, IProgressMonitor monitor ) throws CoreException
    {
        CoreUtil.addNaturesToProject( project, new String[] { "org.apache.ivyde.eclipse.ivynature" }, monitor ); //$NON-NLS-1$
    }

    public static IStatus configureIvyProject( final IProject project, IProgressMonitor monitor ) throws CoreException
    {
        SDK sdk = SDKUtil.getSDK( project );

        // check for 6.1.2 and greater but not 6.1.10 which is older EE release
        // and match 6.2.0 and greater
        final Version version = new Version( sdk.getVersion() );

        if( ( CoreUtil.compareVersions( version, ILiferayConstants.V611 ) >= 0 &&
                        CoreUtil.compareVersions( version, ILiferayConstants.V6110 ) < 0 ) ||
                        CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 )
        {
            IFile ivyXmlFile = project.getFile( ISDKConstants.IVY_XML_FILE );

            if( ivyXmlFile.exists() )
            {
                // IDE-1044
                addIvyNature( project, monitor );

                IvyClasspathContainer ivycp = addIvyLibrary( project, monitor );

                if( ivycp != null )
                {
                    IStatus status = ivycp.launchResolve( false, monitor );

                    if( status.isOK() )
                    {
                        final IWebProject webproject = LiferayCore.create( IWebProject.class, project );

                        if ( webproject != null )
                        {
                            final IFolder webinfFolder = webproject.getDefaultDocrootFolder().getFolder( "WEB-INF" );

                            if ( webinfFolder != null )
                            {
                                ComponentUtil.validateFolder( webinfFolder, monitor );
                            }
                        }
                    }
                    else
                    {
                        return status;
                    }
                }
            }
        }

        return Status.OK_STATUS;
    }

}
