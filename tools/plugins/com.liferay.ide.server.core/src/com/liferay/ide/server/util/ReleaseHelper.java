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

package com.liferay.ide.server.util;

import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.IPath;

/**
 * @author Gregory Amerson
 */
public class ReleaseHelper
{
    protected File serviceJarFile;
    protected JarClassLoader loader;

    public ReleaseHelper( IPath serviceJarPath )
    {
        if( !serviceJarPath.toFile().exists() )
        {
            throw new IllegalArgumentException( "Service jar file must exist." ); //$NON-NLS-1$
        }

        serviceJarFile = serviceJarPath.toFile();

        loader = new JarClassLoader( serviceJarFile.getAbsolutePath() );
    }

    public String getVersion() throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {

        String retval = null;

        try
        {
            Class infoClass = loader.loadClass( "com.liferay.portal.kernel.util.ReleaseInfo" ); //$NON-NLS-1$

            Object o = infoClass.newInstance();

            Method m = infoClass.getMethod( "getVersion" ); //$NON-NLS-1$

            Object result = m.invoke( o );

            if( result != null )
            {
                retval = result.toString();
            }
        }
        catch( ClassNotFoundException e )
        {
            throw e;
        }
        catch( InstantiationException e )
        {
            throw e;
        }
        catch( IllegalAccessException e )
        {
            throw e;
        }
        catch( Throwable e )
        {
            LiferayServerCore.logError( e );
        }

        return retval;
    }

}
