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

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Gregory Amerson
 */
public class NewLiferayProfileIdValidationService extends ValidationService
{

    private Set<String> existingValues;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        this.existingValues =
            NewLiferayPluginProjectOpMethods.getPossibleProfileIds( context( NewLiferayPluginProjectOp.class ), true );

        this.existingValues.remove( profile().getId().content() );
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final NewLiferayProfile newLiferayProfile = profile();
        String profileId = newLiferayProfile.getId().content( true );

        if( profileId == null || profileId.isEmpty() )
        {
            retval = Status.createErrorStatus( "Profile id can not be empty." );
        }
        else if( profileId.contains( StringPool.SPACE ) )
        {
            retval = Status.createErrorStatus( "No spaces are allowed in profile id." );
        }

        if( !existingValues.isEmpty() )
        {
            for( String val : this.existingValues )
            {
                if( val != null && val.equals( profileId ) )
                {
                    retval = Status.createErrorStatus( "Profile already exists." );
                    break;
                }
            }
        }

        return retval;
    }

    private NewLiferayProfile profile()
    {
        return context( NewLiferayProfile.class );
    }
}
