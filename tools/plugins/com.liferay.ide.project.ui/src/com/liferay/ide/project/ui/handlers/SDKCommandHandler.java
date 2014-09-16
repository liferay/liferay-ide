package com.liferay.ide.project.ui.handlers;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Simon Jiang
 */
public abstract class SDKCommandHandler extends AbstractHandler
{

    protected abstract String getSDKCommand();

    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        IStatus retval = null;
        final ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            final Object selected = structuredSelection.getFirstElement();

            IProject project = null;

            if( selected instanceof IResource )
            {
                project = ( (IResource) selected ).getProject();
            }
            else if( selected instanceof IJavaProject )
            {
                project = ( (IJavaProject) selected ).getProject();
            }

            final boolean isLiferay = CoreUtil.isLiferayProject( project );

            if( isLiferay )
            {
                if( SDKUtil.isSDKProject( project ) )
                {
                    retval = executeSdkCommand( project );
                }
            }
        }

        return retval;
    }

    protected IStatus executeSdkCommand( final IProject project )
    {
        IStatus retval = null;

        try
        {
            final IFile buildXmlFile = project.getFile( "build.xml" ); //$NON-NLS-1$

            if( buildXmlFile.exists() )
            {
                final IProject p = project;
                final IFile buildFile = buildXmlFile;

                new Job( p.getName() + " : " + getSDKCommand() ) //$NON-NLS-1$
                {

                    @Override
                    protected IStatus run( IProgressMonitor monitor )
                    {
                        try
                        {
                            SDK sdk = SDKUtil.getSDK( p );

                            Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( p );

                            sdk.runCommand( p, buildFile, getSDKCommand(), null, appServerProperties, monitor );

                            p.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                        }
                        catch( Exception e )
                        {
                            return ProjectUI.createErrorStatus( "Error running SDK command " + getSDKCommand(), e ); //$NON-NLS-1$
                        }

                        return Status.OK_STATUS;
                    }
                }.schedule();

            }

        }
        catch( Exception e )
        {
            retval = ProjectCore.createErrorStatus( "Unable to execute sdk command", e );
        }

        return retval;
    }

}
