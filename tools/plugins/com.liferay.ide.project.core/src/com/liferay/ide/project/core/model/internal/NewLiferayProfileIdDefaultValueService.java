/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.project.core.model.NewLiferayProfile;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.services.DefaultValueServiceData;


/**
 * @author Gregory Amerson
 */
public class NewLiferayProfileIdDefaultValueService extends DefaultValueService
{

    private static final Pattern DUP = Pattern.compile( "(.*)\\(([0-9]+)\\)$" );

    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        final Listener listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        newLiferayProfile().getRuntimeName().attach( listener );
    }

    @Override
    protected DefaultValueServiceData compute()
    {
        final NewLiferayProfile newLiferayProfile = newLiferayProfile();

        final String defaultRuntimeName = newLiferayProfile.getRuntimeName().content();

        // first try to use this as a runtimeName, but need to check it against existing possible values.
        // if no existing profiles with this name exist, use it, if not, append a (1)

        String data = defaultRuntimeName;

        if( data.equals( "<None>" ) )
        {
            return new DefaultValueServiceData( StringPool.EMPTY );
        }

        final Set<String> possibleValues =
            NewLiferayPluginProjectOpMethods.getPossibleProfileIds(
                newLiferayProfile.nearest( NewLiferayPluginProjectOp.class ), false );

        while( possibleValues.contains( data ) )
        {
            try
            {
                data = nextSuffix( data );
            }
            catch( Exception e )
            {
            }
        }

        data = data.replaceAll( StringPool.SPACE, StringPool.DASH );

        return new DefaultValueServiceData( data );
    }

    private String nextSuffix( String val )
    {
        // look for an existing ([0-9])
        final Matcher matcher = DUP.matcher( val );

        if( matcher.matches() )
        {
            final int num = Integer.parseInt( matcher.group( 2 ) );
            return matcher.group( 1 ) + "(" + ( num + 1 ) + ")";
        }

        return val + " (1)";
    }

    private NewLiferayProfile newLiferayProfile()
    {
        return context( NewLiferayProfile.class );
    }
}
