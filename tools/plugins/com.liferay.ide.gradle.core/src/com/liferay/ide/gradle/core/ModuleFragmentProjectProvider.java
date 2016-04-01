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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.gradle.core.modules.NewModuleFragmentOp;
import com.liferay.ide.gradle.core.modules.OverrideFilePath;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.buildship.core.workspace.SynchronizeGradleProjectsJob;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 * @author Lovett Li
 */
public class ModuleFragmentProjectProvider extends AbstractLiferayProjectProvider
    implements NewLiferayProjectProvider<NewModuleFragmentOp>
{

    public ModuleFragmentProjectProvider()
    {
        super( null );
    }

    @Override
    public synchronized ILiferayProject provide( Object adaptable )
    {
        return null; // this only provides new projects
    }

    @Override
    public IStatus createNewProject( NewModuleFragmentOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = Status.OK_STATUS;

        final String projectName = op.getProjectName().content();

        final IPath location = PathBridge.create( op.getLocation().content() );

        final String hostBundleName = op.getHostOsgiBundle().content();

        final IPath temp = GradleCore.getDefault().getStateLocation().append(
            hostBundleName.substring( 0, hostBundleName.lastIndexOf( ".jar" ) ) );

        final IRuntime runtime = ServerUtil.getRuntime( op.getLiferayRuntimeName().content() );

        final PortalBundle portalBundle = LiferayServerCore.newPortalBundle( runtime.getLocation() );

        File hostBundle = portalBundle.getOSGiBundlesDir().append( "modules" ).append( hostBundleName ).toFile();

        try
        {
            ZipUtil.unzip( hostBundle, temp.toFile() );
        }
        catch( IOException e )
        {
            throw new CoreException( GradleCore.createErrorStatus( e ) );
        }

        String bundleSymbolicName = "";
        String version = "";

        if( temp.toFile().exists() )
        {
            final File file = temp.append( "META-INF/MANIFEST.MF" ).toFile();
            final String[] contents = FileUtil.readLinesFromFile( file );

            for( String content : contents )
            {
                if( content.contains( "Bundle-SymbolicName:" ) )
                {
                    bundleSymbolicName =
                        content.substring( content.indexOf( "Bundle-SymbolicName:" ) + "Bundle-SymbolicName:".length() );
                }

                if( content.contains( "Bundle-Version:" ) )
                {
                    version = content.substring( content.indexOf( "Bundle-Version:" ) + "Bundle-Version:".length() );
                }
            }
        }

        final ElementList<OverrideFilePath> files = op.getOverrideFiles();

        final StringBuilder sb = new StringBuilder();
        sb.append( "create " );
        sb.append( "-d \"" + location.toFile().getAbsolutePath() + "\" " );
        sb.append( "-t " + "fragment" + " " );

        if( !bundleSymbolicName.equals( "" ) )
        {
            sb.append( "-h " + bundleSymbolicName + " " );
        }

        if( !version.equals( "" ) )
        {
            sb.append( "-H " + version + " " );
        }

        sb.append( "\"" + projectName + "\" " );

        try
        {
            final String[] ret = BladeCLI.execute( sb.toString() );

            final String errors = BladeCLI.checkForErrors( ret );

            if( errors.length() > 0 )
            {
                retval = GradleCore.createErrorStatus( "Project create error: " + errors );
                return retval;
            }

            IPath projecLocation = location;

            final String lastSegment = location.lastSegment();

            if( location != null && location.segmentCount() > 0 )
            {
                if( !lastSegment.equals( projectName ) )
                {
                    projecLocation = location.append( projectName );
                }
            }

            for( OverrideFilePath file : files )
            {
                File fragmentFile = temp.append( file.getValue().content() ).toFile();

                if( fragmentFile.exists() )
                {
                    File folder = null;

                    if( fragmentFile.getName().equals( "portlet.properties" ) )
                    {
                        folder = location.append( projectName ).append( "src/main/java" ).toFile();

                        FileUtil.copyFileToDir( fragmentFile, "portlet-ext.properties", folder );
                    }
                    else
                    {
                        String parent = fragmentFile.getParentFile().getPath();
                        parent = parent.replaceAll( "\\\\", "/" );
                        String metaInfResources = "META-INF/resources";

                        parent = parent.substring( parent.indexOf( metaInfResources ) + metaInfResources.length() );

                        IPath resources =
                            location.append( projectName ).append( "src/main/resources/META-INF/resources" );

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

            final boolean hasLiferayWorkspace = LiferayWorkspaceUtil.hasLiferayWorkspace();
            final boolean useDefaultLocation = op.getUseDefaultLocation().content( true );
            boolean inWorkspacePath = false;

            final IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

            if( liferayWorkspaceProject != null && !useDefaultLocation )
            {
                IPath workspaceLocation = liferayWorkspaceProject.getLocation();

                if( workspaceLocation != null )
                {
                    String liferayWorkspaceProjectModulesDir =
                        LiferayWorkspaceUtil.getLiferayWorkspaceProjectModulesDir( liferayWorkspaceProject );

                    if( liferayWorkspaceProjectModulesDir != null )
                    {
                        IPath modulesPath = workspaceLocation.append( liferayWorkspaceProjectModulesDir );

                        if( modulesPath.isPrefixOf( projecLocation ) )
                        {
                            inWorkspacePath = true;
                        }
                    }
                }
            }

            if( ( hasLiferayWorkspace && useDefaultLocation ) || inWorkspacePath )
            {
                IProject[] projects = new IProject[] { liferayWorkspaceProject };
                SynchronizeGradleProjectsJob synchronizeJob =
                    new SynchronizeGradleProjectsJob( Arrays.asList( projects ) );
                synchronizeJob.schedule();
            }
            else
            {
                GradleUtil.importGradleProject( projecLocation.toFile(), monitor );
            }
        }
        catch( Exception e )
        {
            retval = GradleCore.createErrorStatus( "Could not create module fragment project.", e );
        }

        return retval;
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;

        File projectFodler = path.append( projectName ).toFile();

        if( projectFodler.exists() )
        {
            retval = GradleCore.createErrorStatus( " Project folder is not empty. " );
        }

        return retval;
    }

}
