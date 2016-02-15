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

import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Andy Wu
 */
public class ImportModuleProjectBuildTypeDerivedValueService extends DerivedValueService
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

        op().property( ImportLiferayModuleProjectOp.PROP_LOCATION ).attach( this.listener );
    }

    @Override
    protected String compute()
    {
        String retVal = null;

        if( op().getLocation() != null )
        {
            Path path = op().getLocation().content();

            if( path != null && !path.isEmpty() )
            {
                String location = path.toOSString();

                retVal = ImportLiferayModuleProjectOpMethods.getBuildType( location );

                if( retVal == null )
                {
                    retVal = "";
                }
            }
        }

        return retVal;
    }

    private ImportLiferayModuleProjectOp op()
    {
        return context( ImportLiferayModuleProjectOp.class );
    }

    @Override
    public void dispose()
    {
        if( op() != null )
        {
            op().property( ImportLiferayModuleProjectOp.PROP_LOCATION ).detach( this.listener );
        }
        super.dispose();
    }
}
