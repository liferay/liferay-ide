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

package com.liferay.ide.project.ui.upgrade;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.Problem;
import com.liferay.ide.project.core.upgrade.Liferay7UpgradeAssistantSettings;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Terry Jia
 */
public class RunVerifyPropertiesHandler extends AbstractOSGiCommandHandler
{

    public RunVerifyPropertiesHandler()
    {
        super( "verifyProperties" );
    }

    @Override
    protected Object execute( ExecutionEvent event, Command command ) throws ExecutionException
    {
        Status retval = null;
        final Map<String, File> parameters = new HashMap<>();

        try
        {
            final Liferay7UpgradeAssistantSettings settings =
                UpgradeAssistantSettingsUtil.getObjectFromStore( Liferay7UpgradeAssistantSettings.class );

            parameters.put( "portalDir", new File( settings.getPortalSettings().getNewLiferayPortalLocation() ) );

            final Object o = command.execute( parameters );

            if( o != null && o instanceof List<?> )
            {
                @SuppressWarnings( "unchecked" )
                final List<Problem> problems = (List<Problem>) o;

                settings.getPortalSettings().setProblems( problems.toArray( new Problem[0] ) );

                UpgradeAssistantSettingsUtil.setObjectToStore( Liferay7UpgradeAssistantSettings.class, settings );
            }

            retval = Status.createOkStatus();
        }
        catch( Exception e )
        {
            retval = Status.createErrorStatus( e );
        }

        return retval;
    }

}
