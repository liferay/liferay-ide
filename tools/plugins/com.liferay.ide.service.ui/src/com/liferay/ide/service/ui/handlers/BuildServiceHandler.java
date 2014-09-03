package com.liferay.ide.service.ui.handlers;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.job.BuildServiceJob;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;

/**
 * @author Simon Jiang
 */
public class BuildServiceHandler extends AbstractHandler
{

    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        IStatus retval = null;
        final ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            Object selected = structuredSelection.getFirstElement();

            if( selected instanceof IResource )
            {
                IResource resource = (IResource) selected;
                boolean isLiferayProject = CoreUtil.isLiferayProject( resource.getProject() );

                if( isLiferayProject )
                {
                    IProject project = resource.getProject();
                    retval = executeServiceBuild( project );
                }
            }
            else if( selected instanceof IJavaProject )
            {
                final IProject project = ( (IJavaProject) selected ).getProject();
                retval = executeServiceBuild( project );
            }
        }

        return retval;
    }

    protected IStatus executeServiceBuild( final IProject project )
    {
        IStatus retval = null;

        try
        {
            final IFile servicesFile = getServiceFile( project );

            if( servicesFile != null )
            {
                final BuildServiceJob job = ServiceCore.createBuildServiceJob( servicesFile );
                job.schedule();
                retval = job.getResult();
            }
        }
        catch( Exception e )
        {
            ServiceCore.logError( e );
        }

        return retval;
    }
    
    protected IFile getServiceFile( IProject project )
    {
        IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

        if( webappRoot != null )
        {
            for( IContainer container : webappRoot.getUnderlyingFolders() )
            {
                if( container != null && container.exists() )
                {
                    Path path = new Path( "WEB-INF/" + ILiferayConstants.LIFERAY_SERVICE_BUILDER_XML_FILE ); //$NON-NLS-1$
                    IFile serviceFile = container.getFile( path );

                    if( serviceFile.exists() )
                    {
                        return serviceFile;
                    }
                }
            }
        }

        return null;
    }
}
