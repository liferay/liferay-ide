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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Andy Wu
 */
public class BundleUrlDefaultValueService extends DefaultValueService
{

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( ImportLiferayWorkspaceOp.PROP_WORKSPACE_LOCATION ).attach( this.listener );
    }

    @Override
    protected String compute()
    {
        String bundleURL = null;

        Path path = op().getWorkspaceLocation().content();

        if( path == null )
        {
            return null;
        }

        String workspaceLocation = op().getWorkspaceLocation().content().toPortableString();

        String buildType = LiferayWorkspaceUtil.getWorkspaceType( workspaceLocation );

        if( buildType != null )
        {
            if( buildType.startsWith( "gradle" ) )
            {
                bundleURL = LiferayWorkspaceUtil.getGradleProperty(
                    workspaceLocation, LiferayWorkspaceUtil.LIFERAY_WORKSPACE_BUNDLE_URL,
                    BaseLiferayWorkspaceOp.defaultBundleUrl );
            }
            // for maven type liferay workspace
            else
            {
                bundleURL = BaseLiferayWorkspaceOp.defaultBundleUrl;
            }
        }

        return bundleURL;
    }

    private ImportLiferayWorkspaceOp op()
    {
        return context( ImportLiferayWorkspaceOp.class );
    }

    @Override
    public void dispose()
    {
        op().property( ImportLiferayWorkspaceOp.PROP_WORKSPACE_LOCATION ).detach( this.listener );

        super.dispose();
    }
}
