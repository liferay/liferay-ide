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

import com.liferay.ide.sdk.core.ISDKListener;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;

import java.util.Set;

import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.services.PossibleValuesService;


/**
 * @author Gregory Amerson
 */
public class PluginsSDKNamePossibleValuesService extends PossibleValuesService implements ISDKListener
{

    @Override
    protected void fillPossibleValues( Set<String> values )
    {
        SDK[] validSDKs = SDKManager.getInstance().getSDKs();

        if( validSDKs.length > 0 )
        {
            for( SDK validSDK : validSDKs )
            {
                values.add( validSDK.getName() );
            }
        }
    }

    @Override
    public void dispose()
    {
        SDKManager.getInstance().removeSDKListener( this );

        super.dispose();
    }

    @Override
    public Severity getInvalidValueSeverity( String invalidValue )
    {
        if( PluginsSDKNameDefaultValueService.NONE.equals( invalidValue ) ) //$NON-NLS-1$
        {
            return Severity.OK;
        }

        return super.getInvalidValueSeverity( invalidValue );
    }

    @Override
    protected void init()
    {
        super.init();

        SDKManager.getInstance().addSDKListener( this );
    }

    @Override
    public boolean ordered()
    {
        return true;
    }

    public void sdksAdded( SDK[] sdk )
    {
        broadcast();
    }

    public void sdksChanged( SDK[] sdk )
    {
        broadcast();
    }

    public void sdksRemoved( SDK[] sdk )
    {
        broadcast();
    }

}
