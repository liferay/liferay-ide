package com.liferay.ide.service.ui.handlers;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.job.BuildServiceJob;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Simon Jiang
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class BuildServiceHandler extends AbstractHandler
{

    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        IStatus retval = null;
        IProject project = null;

        final ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            final Object selected = structuredSelection.getFirstElement();

            if( selected instanceof IResource )
            {
                project = ( (IResource) selected ).getProject();
            }
            else if( selected instanceof IJavaElement )
            {
                project = ( (IJavaElement) selected ).getJavaProject().getProject();
            }
            else if( selected instanceof PackageFragmentRootContainer )
            {
                project = ( (PackageFragmentRootContainer) selected ).getJavaProject().getProject();
            }
        }

        if( project == null )
        {
            final IEditorInput editorInput = HandlerUtil.getActiveEditorInput( event );

            if( editorInput != null && editorInput.getAdapter( IResource.class ) != null )
            {
                project = ( (IResource) editorInput.getAdapter( IResource.class ) ).getProject();
            }
        }

        if( project != null )
        {
            retval = executeServiceBuild( project );
        }

        return retval;
    }

    protected IStatus executeServiceBuild( final IProject project )
    {
        IStatus retval = null;

        try
        {
            final BuildServiceJob job = ServiceCore.createBuildServiceJob( project );
            job.schedule();
            retval = Status.OK_STATUS;
        }
        catch( Exception e )
        {
            retval = ServiceCore.createErrorStatus( "Unable to execute build-service command", e );
        }

        return retval;
    }

    protected IFile getServiceFile( IProject project )
    {
        final IFolder docroot = CoreUtil.getDefaultDocrootFolder( project );

        if( docroot != null && docroot.exists() )
        {
            final IPath path = new Path( "WEB-INF/" + ILiferayConstants.LIFERAY_SERVICE_BUILDER_XML_FILE );
            final IFile serviceFile = docroot.getFile( path );

            if( serviceFile.exists() )
            {
                return serviceFile;
            }
        }

        return null;
    }
}
