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

import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class HasBundlesDirDerivedValueService extends DerivedValueService
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

        op().property( ImportLiferayWorkspaceOp.PROP_WORKSPACE_LOCATION ).attach( this.listener );
    }

    @Override
    protected String compute()
    {
        String retval = "false";

        final Path path = op().getWorkspaceLocation().content();

        if( path != null )
        {
            if( LiferayWorkspaceUtil.isValidWorkspaceLocation( path.toPortableString() ) )
            {
                retval = LiferayWorkspaceUtil.hasBundlesDir( path.toPortableString() ) ? "true" : "false";
            }
        }

        return retval;
    }

    private ImportLiferayWorkspaceOp op()
    {
        return context( ImportLiferayWorkspaceOp.class );
    }

}
