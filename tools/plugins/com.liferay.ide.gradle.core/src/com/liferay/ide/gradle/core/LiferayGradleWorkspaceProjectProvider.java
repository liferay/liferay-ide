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
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Andy Wu
 * @author Terry Jia
 */
public class LiferayGradleWorkspaceProjectProvider extends AbstractLiferayProjectProvider
    implements NewLiferayProjectProvider<NewLiferayWorkspaceOp>
{

    public static final String defaultBundleUrl =
        "https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.2-ga3/liferay-ce-portal-tomcat-7.0-ga3-20160804222206210.zip";

    public LiferayGradleWorkspaceProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    @Override
    public IStatus createNewProject( NewLiferayWorkspaceOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = Status.OK_STATUS;

        IPath location = PathBridge.create( op.getLocation().content() );
        String wsName = op.getWorkspaceName().toString();

        StringBuilder sb = new StringBuilder();

        sb.append( "-b " );
        sb.append( "\"" + location.append( wsName ).toFile().getAbsolutePath() + "\"" );
        sb.append( " " );
        sb.append( "init" );

        try
        {
            BladeCLI.execute( sb.toString() );
        }
        catch( BladeCLIException e )
        {
            retval = ProjectCore.createErrorStatus( e );
        }

        String workspaceLocation = location.append( wsName ).toPortableString();
        boolean isInitBundle = op.getProvisionLiferayBundle().content();
        final String bundleUrl = op.getBundleUrl().content( false );

        IStatus importStatus = null;

        if( isInitBundle )
        {
            importStatus = importProject( workspaceLocation, monitor, "initBundle", bundleUrl );
        }

        importStatus = importProject( workspaceLocation, monitor, null, null );

        retval = importStatus;

        if( !retval.isOK() )
        {
            return retval;
        }

        return retval;
    }

    public IStatus importProject( String location, IProgressMonitor monitor, String extraOperation, String bundleUrl )
    {
        try
        {
            final IStatus importJob = GradleUtil.importGradleProject( new File( location ), monitor );

            if( !importJob.isOK() || importJob.getException() != null )
            {
                return importJob;
            }

            if( !CoreUtil.empty( extraOperation ) )
            {
                IPath path = new Path( location );

                path.lastSegment();

                IProject project = CoreUtil.getProject( path.lastSegment() );

                if( bundleUrl != null )
                {
                    final IFile gradlePropertiesFile = project.getFile( "gradle.properties" );

                    String content = FileUtil.readContents( gradlePropertiesFile.getContents() );

                    String bundleUrlProp = "liferay.workspace.bundle.url=" + bundleUrl;

                    String separator = System.getProperty( "line.separator", "\n" );

                    String newContent = content + separator + bundleUrlProp;

                    gradlePropertiesFile.setContents(
                        new ByteArrayInputStream( newContent.getBytes() ), IResource.FORCE, monitor );
                }

                GradleUtil.runGradleTask( project, extraOperation, monitor );

                project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
            }

        }
        catch( Exception e )
        {
            return GradleCore.createErrorStatus( "import Liferay workspace project error", e );
        }

        return Status.OK_STATUS;
    }

    @Override
    public synchronized ILiferayProject provide( Object adaptable )
    {
        ILiferayProject retval = null;

        if( adaptable instanceof IProject )
        {
            final IProject project = (IProject) adaptable;

            if( LiferayWorkspaceUtil.isValidWorkspace( project ) )
            {
                return new LiferayWorkspaceProject( project );
            }
        }

        return retval;
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;

        // TODO validation gradle project location

        return retval;
    }
}
