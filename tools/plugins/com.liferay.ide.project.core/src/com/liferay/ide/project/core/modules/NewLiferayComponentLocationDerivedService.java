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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentLocationDerivedService extends DerivedValueService
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

        op().property( NewLiferayComponentOp.PROP_PROJECT_NAME ).attach( this.listener );
    }

    @Override
    protected String compute()
    {
        String retVal = null;

        final Value<String> projectName = op().getProjectName();

        if( projectName != null )
        {
            final String iProjectName = projectName.content( true );

            if( iProjectName != null )
            {
                final IProject project = CoreUtil.getProject( iProjectName );

                if( project != null )
                {
                    return project.getLocation().toOSString();
                }
            }
        }

        return retVal;
    }

    private NewLiferayComponentOp op()
    {
        return context( NewLiferayComponentOp.class );
    }

    @Override
    public void dispose()
    {
        if( op() != null )
        {
            op().property( NewLiferayComponentOp.PROP_PROJECT_NAME ).detach( this.listener );
        }
        super.dispose();
    }
}
