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

package com.liferay.ide.gradle.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 */
public class NewModuleFragmentFilesOpMethods
{

    public static final Status execute( final NewModuleFragmentFilesOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Copy files (this process may take several minutes)", 100 );

        final String projectName = op.getProjectName().content();

        final IProject project = CoreUtil.getProject( projectName );

        Status retval = null;

        try
        {
            final String hostBundleName = op.getHostOsgiBundle().content();

            final IPath temp = GradleCore.getDefault().getStateLocation().append( hostBundleName );

            if( !temp.toFile().exists() )
            {
                final IRuntime runtime = ServerUtil.getRuntime( op.getLiferayRuntimeName().content() );

                final PortalBundle portalBundle = LiferayServerCore.newPortalBundle( runtime.getLocation() );

                File hostBundle =
                    portalBundle.getOSGiBundlesDir().append( "modules" ).append( hostBundleName + ".jar" ).toFile();

                if( !hostBundle.exists() )
                {
                    hostBundle = GradleCore.getDefault().getStateLocation().append( hostBundleName + ".jar" ).toFile();
                }

                try
                {
                    ZipUtil.unzip( hostBundle, temp.toFile() );
                }
                catch( IOException e )
                {
                    throw new CoreException( GradleCore.createErrorStatus( e ) );
                }
            }

            final ElementList<OverrideFilePath> files = op.getOverrideFiles();

            for( OverrideFilePath file : files )
            {
                File fragmentFile = temp.append( file.getValue().content() ).toFile();

                if( fragmentFile.exists() )
                {
                    File folder = null;

                    if( fragmentFile.getName().equals( "portlet.properties" ) )
                    {
                        folder = project.getLocation().append( "src/main/java" ).toFile();

                        FileUtil.copyFileToDir( fragmentFile, "portlet-ext.properties", folder );
                    }
                    else
                    {
                        String parent = fragmentFile.getParentFile().getPath();
                        parent = parent.replaceAll( "\\\\", "/" );
                        String metaInfResources = "META-INF/resources";

                        parent = parent.substring( parent.indexOf( metaInfResources ) + metaInfResources.length() );

                        IPath resources = project.getLocation().append(
                            "src/main/resources/META-INF/resources" );

                        folder = resources.toFile();

                        if( !parent.equals( "resources" ) && !parent.equals( "" ) )
                        {
                            folder = resources.append( parent ).toFile();
                            folder.mkdirs();
                        }

                        FileUtil.copyFileToDir( fragmentFile, folder );
                    }
                }
            }

            project.refreshLocal( IResource.DEPTH_INFINITE, null );

            retval = Status.createOkStatus();
        }
        catch( Exception e )
        {
            final String msg = "Error copy files.";
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }

}
