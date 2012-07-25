/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.hook.core.model.Hook;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.PropertyEvent;
import org.eclipse.sapphire.services.EnablementService;
import org.eclipse.sapphire.services.EnablementServiceData;

/**
 * @author Gregory Amerson
 */
public class PortalPropertiesOverridesEnablementService extends EnablementService
{

    @Override
    protected void initEnablementService()
    {
        Listener listener = new FilteredListener<PropertyEvent>()
        {

            protected void handleTypedEvent( PropertyEvent event )
            {
                refresh();
            };
        };

        context( Hook.class ).attach( listener, Hook.PROP_PORTAL_PROPERTIES_FILE.getName() );
    }

    @Override
    protected EnablementServiceData compute()
    {
        boolean retval = false;

        IFile file = HookMethods.getPortalPropertiesFile( context( Hook.class ) );

        if( file != null && file.exists() )
        {
            retval = true;
        }

        return new EnablementServiceData( retval );
    }

}
