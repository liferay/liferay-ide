package com.liferay.ide.service.ui.handlers;

import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.job.BuildServiceJob;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


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
            final BuildServiceJob job = ServiceCore.createBuildWSDDJob( project );
            job.schedule();
            retval = Status.OK_STATUS;
        }
        catch( Exception e )
        {
            retval = ServiceCore.createErrorStatus( "Unable to execute build-wsdd command", e );
        }

        return retval;
    }
}
