package com.liferay.ide.service.ui.handlers;

import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.job.BuildServiceJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;


/**
 * @author Simon Jiang
 */
public class BuildWSDDHandler extends BuildServiceHandler
{

    @Override
    protected IStatus executeServiceBuild( final IProject project )
    {
        IStatus retval = null;

        try
        {
            final IFile servicesFile = getServiceFile( project );

            if( servicesFile != null )
            {
                final BuildServiceJob job = ServiceCore.createBuildWSDDJob( servicesFile );
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
}
