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
package com.liferay.ide.project.core.upgrade.service;

import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Andy Wu
 */
public class Liferay62ServerLocationDerivedValueService extends DerivedValueService
{

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initDerivedValueService()
    {
        super.initDerivedValueService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( CodeUpgradeOp.PROP_LOCATION ).attach( this.listener );
    }

    @Override
    protected String compute()
    {
        final Path path = op().getLocation().content();

        SDK sdk = SDKUtil.createSDKFromLocation( PathBridge.create( path ) );

        String liferay62ServerLocation = null;

        try
        {
            liferay62ServerLocation = (String)(sdk.getBuildProperties( true ).get( ISDKConstants.PROPERTY_APP_SERVER_PARENT_DIR ));
        }
        catch( CoreException e )
        {
        }

        return liferay62ServerLocation;
    }

    private CodeUpgradeOp op()
    {
        return context( CodeUpgradeOp.class );
    }

}
