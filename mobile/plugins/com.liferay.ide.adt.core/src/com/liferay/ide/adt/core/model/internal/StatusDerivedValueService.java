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
package com.liferay.ide.adt.core.model.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.DerivedValueService;


/**
 * @author Gregory Amerson
 */
public class StatusDerivedValueService extends DerivedValueService
{
    private String status = "OK";

    @Override
    protected void initDerivedValueService()
    {
        super.initDerivedValueService();

        new Job( "refresh status" )
        {
            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                if( ! status.equals( "OK" ) )
                {
                    try
                    {
                        Thread.sleep( 3000 );
                    }
                    catch( InterruptedException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    status = "OK";

                    new Job("refresh")
                    {
                        @Override
                        protected IStatus run( IProgressMonitor monitor )
                        {
//                            refresh();
                            return Status.OK_STATUS;
                        }

                    }.schedule( 100 );
                }

                return Status.OK_STATUS;
            }
        }.schedule(100);
    }

    @Override
    protected String compute()
    {
        return this.status;
    }

}
