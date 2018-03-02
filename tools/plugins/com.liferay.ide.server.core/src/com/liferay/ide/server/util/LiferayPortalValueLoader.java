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

package com.liferay.ide.server.util;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class LiferayPortalValueLoader
{

    private IPath[] userLibs;
    private IPath portalDir;

    public LiferayPortalValueLoader( IPath[] extraLibs )
    {
        this.userLibs = extraLibs;
    }

    public LiferayPortalValueLoader( IPath appServerPortalDir, IPath[] extraLibs )
    {
        this.portalDir = appServerPortalDir;
        this.userLibs = extraLibs;
    }

    private void addLibs( File libDir, List<URL> libUrlList ) throws MalformedURLException
    {
        if( FileUtil.exists(libDir) )
        {
            File[] libs = libDir.listFiles
            (
                new FilenameFilter()
                {
                    @Override
                    public boolean accept( File dir, String fileName )
                    {
                        return fileName.toLowerCase().endsWith( ".jar" );
                    }
                }
            );

            if( ListUtil.isNotEmpty(libs) )
            {
                for( File portaLib : libs )
                {
                    libUrlList.add( portaLib.toURI().toURL() );
                }
            }
        }
    }

    private Object[] getFieldValuesFromClass( String loadClassName, String fieldName)
    {
        Object[] retval = null;

        try
        {
            final Class<?> classRef = loadClass(loadClassName);
            final Field propertiesField = classRef.getDeclaredField( fieldName );
            retval = ( Object[] ) ( propertiesField.get( propertiesField ) );
        }
        catch( Exception e )
        {
            retval = new Object[0];
            LiferayServerCore.logError( "Error unable to find " + loadClassName, e ); //$NON-NLS-1$
        }

        return retval;
    }

    private Object getMethodValueFromClass( String loadClassName, String methodName)
    {
        Object retval = null;

        try
        {
            final Class<?> classRef = loadClass( loadClassName );
            final Method method = classRef.getMethod( methodName );
            retval = method.invoke( null );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( "Error unable to find " + loadClassName, e ); //$NON-NLS-1$
        }

        return retval;
    }

    @SuppressWarnings( "resource" )
    private Class<?> loadClass( String className ) throws Exception
    {
        final List<URL> libUrlList = new ArrayList<URL>();

        if ( portalDir != null )
        {
            final File libDir = portalDir.append( "WEB-INF/lib" ).toFile();

            addLibs( libDir, libUrlList );
        }

        if( ListUtil.isNotEmpty(userLibs) )
        {
            for( IPath url : userLibs )
            {
                libUrlList.add( new File( url.toOSString() ).toURI().toURL() );
            }
        }

        final URL[] urls = libUrlList.toArray( new URL[libUrlList.size()] );

        return new URLClassLoader( urls ).loadClass( className );
    }

    public String[] loadHookPropertiesFromClass()
    {
        final String loadClassName = "com.liferay.portal.deploy.hot.HookHotDeployListener"; //$NON-NLS-1$
        final String fieldName = "SUPPORTED_PROPERTIES"; //$NON-NLS-1$

        return ( String[] ) getFieldValuesFromClass( loadClassName, fieldName );
    }

    public String loadServerInfoFromClass()
    {
        final String loadClassName = "com.liferay.portal.kernel.util.ReleaseInfo"; //$NON-NLS-1$
        final String methodName = "getServerInfo"; //$NON-NLS-1$

        return ( String )getMethodValueFromClass( loadClassName, methodName);
    }

    public Version loadVersionFromClass()
    {
        final String loadClassName = "com.liferay.portal.kernel.util.ReleaseInfo"; //$NON-NLS-1$
        final String methodName = "getVersion"; //$NON-NLS-1$

        Version retval = null;

        try
        {
            final String versionString = ( String )getMethodValueFromClass( loadClassName, methodName);
            retval = Version.parseVersion( versionString );
        }
        catch( Exception e )
        {
            retval = Version.emptyVersion;
            LiferayServerCore.logError( "Error unable to find " + loadClassName, e ); //$NON-NLS-1$
        }

        return retval;
    }

}
