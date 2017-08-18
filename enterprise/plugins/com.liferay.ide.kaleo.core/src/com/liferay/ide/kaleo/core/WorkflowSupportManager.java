/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay IDE ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.core;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 *
 */
public class WorkflowSupportManager
{

    public static final String SUPPORT_PROJECT_NAME = "Kaleo Designer Support";
    private IServer currentServer;

    public WorkflowSupportManager()
    {
    }

    public IJavaProject getSupportProject()
    {
        try
        {
            checkForSupportProject();

            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IProject project = root.getProject( SUPPORT_PROJECT_NAME );

            if( project.exists() && project.isOpen() && project.hasNature( JavaCore.NATURE_ID ) )
            {
                return JavaCore.create( project );
            }

        }
        catch( CoreException e )
        {
        }

        return null;
    }

    public void setCurrentServer(IServer server)
    {
        this.currentServer = server;
    }

    private void checkForSupportProject()
    {
        try
        {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IProject project = root.getProject( SUPPORT_PROJECT_NAME );

            if( !project.exists() || !project.isOpen())
            {
                createSupportProject( new NullProgressMonitor() );
            }

            computeClasspath( JavaCore.create( project ), new NullProgressMonitor() );
        }
        catch( CoreException e )
        {
        }
    }

    public IProject createSupportProject( IProgressMonitor monitor ) throws CoreException
    {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject( SUPPORT_PROJECT_NAME );

        if( project.exists() )
        {
            if( !project.isOpen() )
            {
                project.open( monitor );
            }

            return project;
        }

        project.create( CoreUtil.newSubMonitor(monitor,1) );
        project.open( CoreUtil.newSubMonitor(monitor,1) );

        CoreUtil.makeFolders( project.getFolder( "src" ) );

        CoreUtil.addNaturesToProject( project, new String[] { JavaCore.NATURE_ID }, CoreUtil.newSubMonitor(monitor,1) );

        IJavaProject jProject = JavaCore.create( project );
        jProject.setOutputLocation( project.getFullPath().append( "bin" ), CoreUtil.newSubMonitor(monitor,1) );
        computeClasspath( jProject, CoreUtil.newSubMonitor(monitor,1) );

        return project;
    }

    private void computeClasspath( IJavaProject project, IProgressMonitor monitor )
    {
        int numEntries = 2;
        IPath runtimeContainerPath = null;

        try
        {
            String id = this.currentServer.getRuntime().getId();
            runtimeContainerPath = new Path("org.eclipse.jst.server.core.container/com.liferay.ide.eclipse.server.tomcat.runtimeClasspathProvider/" + id);
            numEntries++;
        }
        catch (Throwable t)
        {
           // do nothing
        }

        IClasspathEntry[] classpath = new IClasspathEntry[numEntries];
        classpath[0] = JavaCore.newContainerEntry( JavaRuntime.newDefaultJREContainerPath() );
        classpath[1] = JavaCore.newSourceEntry( project.getProject().getFolder( "src" ).getFullPath() );

        if (runtimeContainerPath != null)
        {
            classpath[2] = JavaCore.newContainerEntry( runtimeContainerPath );
        }

        try
        {
            project.setRawClasspath( classpath, monitor );
        }
        catch( JavaModelException e )
        {
        }
    }
}
