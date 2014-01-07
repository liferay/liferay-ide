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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.LiferayProjectCore;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */
public class LiferayPortalValueLoader
{

    private IPath portalDir;
    private IPath globalDir;
    private IPath[] extraLib;

    public LiferayPortalValueLoader( IPath appServerPortalDir, IPath appServerGlobalDir )
    {
        this.portalDir = appServerPortalDir;
        this.globalDir = appServerGlobalDir;
    }
    
    public LiferayPortalValueLoader( IPath appServerPortalDir, IPath[] extraLib )
    {
        this.portalDir = appServerPortalDir;
        this.extraLib = extraLib;
    }    
    
    
    private Class<?> loadClass( String className ) throws Exception
    {
        final ArrayList<URL> libUrlList = new ArrayList<URL>();

        if ( portalDir != null )
        {
            final File[] portalLibs = portalDir.append( "WEB-INF/lib" ).toFile().listFiles
            ( 
                new FilenameFilter() 
                {
                    public boolean accept( File dir, String fileName )
                    {
                        return fileName.toLowerCase().endsWith( ".jar" ); 
                    }
                } 
            );

            for( File portaLib : portalLibs )
            {
                libUrlList.add( portaLib.toURI().toURL() );
            }
        }

        if ( globalDir != null )
        {
            final File[] libs = globalDir.append( "lib" ).toFile().listFiles
            ( 
                new FilenameFilter() 
                {

                    public boolean accept( File dir, String fileName )
                    {
                        return fileName.toLowerCase().endsWith( ".jar" ); 
                    }
                } 
             );

            for( File lib : libs )
            {
                libUrlList.add( lib.toURI().toURL() );
            }  

            final File[] extLibs = globalDir.append( "lib/ext" ).toFile().listFiles
            ( 
                new FilenameFilter() 
                {

                    public boolean accept( File dir, String fileName )
                    {
                        return fileName.toLowerCase().endsWith( ".jar" ); 
                    }
                } 
             );

            for( File extLib : extLibs )
            {
                libUrlList.add( extLib.toURI().toURL() );
            }             
        }

        if( ! CoreUtil.isNullOrEmpty( extraLib ) )
        {
            for( IPath url : extraLib )
            {
                libUrlList.add( new File( url.toOSString() ).toURI().toURL() );
            }
        }

        final URL[] urls = libUrlList.toArray( new URL[libUrlList.size()] );

        @SuppressWarnings( "resource" )
        URLClassLoader classLoader = new URLClassLoader( urls );
        return classLoader.loadClass( className );
    }

    private Object[] getFieldValuesFromClass( String loadClassName, String fieldName)
    {
        Object[] retval = null;
        try
        {
            Class<?> classRef = loadClass(loadClassName);
            final Field propertiesField = classRef.getDeclaredField( fieldName );
            retval = ( Object[] ) ( propertiesField.get( propertiesField ) );

        }
        catch( Exception e )
        {
            retval = new Object[0];
            LiferayProjectCore.logError( "Error unable to find " + loadClassName, e ); //$NON-NLS-1$
        }

        return retval;
    }
    
    private Object getMethodValueFromClass( String loadClassName, String methodName)
    {
        Object retval = null;
        try
        {
            final Class<?> classRef = loadClass( loadClassName );
            final Method method = classRef.getMethod( methodName, null );
            retval = method.invoke( null, null );

        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( "Error unable to find " + loadClassName, e ); //$NON-NLS-1$
        }

        return retval;
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
            LiferayProjectCore.logError( "Error unable to find " + loadClassName, e ); //$NON-NLS-1$
        }

        return retval;
    }

}
