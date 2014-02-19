/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;

/**
 * @author Gregory Amerson
 */
public class LiferayVersionPossibleValuesService extends PossibleValuesService
{

    private List<String> versions = null;
    private Job versionsJob = null;
    private String groupId;
    private String artifactId;

    @Override
    protected void initPossibleValuesService()
    {
        super.initPossibleValuesService();

        this.groupId = this.param( "groupId" );
        this.artifactId = this.param( "artifactId" );
    }

    @Override
    protected void compute( Set<String> values )
    {
        if( this.versions != null )
        {
            values.addAll( this.versions );
        }
        else if( this.versionsJob == null )
        {
            this.versionsJob = new Job( "Determining possible Liferay versions." )
            {
                @Override
                protected IStatus run( IProgressMonitor monitor )
                {
                    final NewLiferayPluginProjectOp op = op();

                    if( ! op.disposed() )
                    {
                        final ILiferayProjectProvider projectProvider = op.getProjectProvider().content();

                        try
                        {
                            versions = projectProvider.getData( "liferayVersions", String.class, groupId, artifactId );
                        }
                        catch( Exception e )
                        {
                            LiferayProjectCore.logError( "Could not determine possible versions.", e );
                        }

                        refresh();
                    }

                    return Status.OK_STATUS;
                }
            };

            this.versionsJob.schedule();
        }
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }

    @Override
    public boolean ordered()
    {
        return true;
    }

    @Override
    public org.eclipse.sapphire.modeling.Status problem( Value<?> value )
    {
        return org.eclipse.sapphire.modeling.Status.createOkStatus();
    }

}
