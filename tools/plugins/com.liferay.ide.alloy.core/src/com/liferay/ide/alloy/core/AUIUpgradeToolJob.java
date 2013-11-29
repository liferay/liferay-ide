package com.liferay.ide.alloy.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;


public class AUIUpgradeToolJob extends Job
{

    public AUIUpgradeToolJob( IProject project )
    {
        super( "AUI upgrade tool: " + project.getName() );
    }

    @Override
    protected IStatus run( IProgressMonitor monitor )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
