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

package com.liferay.ide.server.core;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Greg Amerson
 */
public class LiferayServerPropertyTester extends PropertyTester
{

    public LiferayServerPropertyTester()
    {
    }

    public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
    {
        if( receiver instanceof IServer )
        {
            IServer server = (IServer) receiver;

            try
            {
                return server.getRuntime().getRuntimeType().getId().startsWith( "com.liferay." ); //$NON-NLS-1$
            }
            catch( Throwable t )
            {
                // ignore
            }
        }

        return false;
    }

}
