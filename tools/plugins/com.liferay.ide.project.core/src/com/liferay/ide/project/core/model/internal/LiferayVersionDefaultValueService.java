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
package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.PropertyDef;


/**
 * @author Gregory Amerson
 */
public class LiferayVersionDefaultValueService extends DefaultValueService
{
    private Set<String> possibleValues;
    private String runtimeVersion;

    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        final PropertyDef def = context().find( PropertyDef.class );
        final Property property = context( Element.class ).property( def );
        final PossibleValuesService possibleValuesService = property.service( PossibleValuesService.class );

        possibleValuesService.attach
        (
            new Listener()
            {
                @Override
                public void handle( Event event )
                {
                    possibleValues = possibleValuesService.values();
                    refresh();
                }
            }
        );

        context( NewLiferayProfile.class ).property( NewLiferayProfile.PROP_RUNTIME_NAME ).attach
        (
            new FilteredListener<PropertyContentEvent>()
            {
                @Override
                protected void handleTypedEvent( PropertyContentEvent event )
                {
                    possibleValues = possibleValuesService.values();
                    runtimeVersion = null;
                    refresh();
                }
            }
        );

        this.possibleValues = possibleValuesService.values();
    }

    @Override
    protected String compute()
    {
        String data = null;

        if( this.possibleValues.size() > 0 )
        {
            if( this.runtimeVersion == null )
            {
                new Job("get runtime version")
                {
                    @Override
                    protected IStatus run( IProgressMonitor monitor )
                    {
                        final NewLiferayProfile newLiferayProfile = context( NewLiferayProfile.class );

                        if( !newLiferayProfile.disposed() )
                        {
                            final String runtimeName = newLiferayProfile.getRuntimeName().content();
                            final ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( runtimeName );

                            if( liferayRuntime != null )
                            {
                                runtimeVersion = liferayRuntime.getPortalVersion();
                                refresh();
                            }
                        }
                        return Status.OK_STATUS;
                    }
                }.schedule();
            }
            else
            {
                try
                {
                    final List<String> filteredVals = new ArrayList<String>();

                    for( final String val : possibleValues )
                    {
                        if( val.equals( runtimeVersion ) )
                        {
                            data = val;
                            break;
                        }

                        if( val.contains( runtimeVersion ) )
                        {
                            filteredVals.add( val );
                        }
                    }

                    if( data == null )
                    {
                        if( filteredVals.size() > 0 )
                        {
                            data = checkForSnapshots( filteredVals.toArray( new String[0] ) );
                        }
                        else
                        {
                            data = checkForSnapshots( possibleValues.toArray( new String[0] ) );
                        }
                    }
                }
                catch( Exception e )
                {
                }
            }
        }

        return data;
    }

    private String checkForSnapshots( final String[] values )
    {
        String retval = null;

        retval = values[ values.length - 1 ];

        if( retval.endsWith( "SNAPSHOT" ) && values.length > 1 )
        {
            retval = values[ values.length - 2 ];
        }

        return retval;
    }

}
