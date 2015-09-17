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

import com.liferay.ide.project.core.model.SDKProjectsImportOp;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;


/**
 * @author Simon Jiang
 */
public class SDKImportVersionDerivedValueService extends DerivedValueService
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

        op().property( SDKProjectsImportOp.PROP_SDK_LOCATION ).attach( this.listener );
    }

    @Override
    protected String compute()
    {
        String retVal = null;

        if ( op().getSdkLocation() != null )
        {
            if ( op().getSdkLocation().content() != null && !op().getSdkLocation().content().isEmpty() )
            {
                final Path sdkPath = op().getSdkLocation().content();
                final IStatus status = ProjectImportUtil.validateSDKPath(
                    op().getSdkLocation().content().toPortableString() );

                if ( status.isOK() )
                {
                    SDK sdk = SDKUtil.createSDKFromLocation( PathBridge.create( sdkPath ) );

                    retVal = sdk.getVersion();
                }
            }
        }

        return retVal;
    }

    private SDKProjectsImportOp op()
    {
        return context( SDKProjectsImportOp.class );
    }

    @Override
    public void dispose()
    {
        if ( op() != null)
        {
            op().property( SDKProjectsImportOp.PROP_SDK_LOCATION ).detach( this.listener );
        }

        super.dispose();
    }
}
