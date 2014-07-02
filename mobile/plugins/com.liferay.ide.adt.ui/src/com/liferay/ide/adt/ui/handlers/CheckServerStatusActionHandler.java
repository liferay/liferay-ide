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
package com.liferay.ide.adt.ui.handlers;
import com.liferay.ide.adt.core.model.GenerateCustomServicesOp;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;


/**
 * @author Kuo Zhang
 */
public class CheckServerStatusActionHandler extends SapphireActionHandler
{

    public CheckServerStatusActionHandler()
    {
        super();
    }

    @Override
    protected Object run( final Presentation context )
    {
        new Thread( "server update" )
        {
            public void run()
            {
                op( context ).updateServerStatus();
            }
        }.start();

        return null;
    }

    private GenerateCustomServicesOp op(  Presentation context )
    {
        return context.part().getModelElement().nearest( GenerateCustomServicesOp.class );
    }

}
