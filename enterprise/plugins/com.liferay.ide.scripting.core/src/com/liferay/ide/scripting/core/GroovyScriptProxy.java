/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay IDE ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */
package com.liferay.ide.scripting.core;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;

/**
 * @author Gregory Amerson
 */
public abstract class GroovyScriptProxy implements InvocationHandler
{

    protected ClassLoader previousClassLoader;
    protected URLClassLoader proxyClassLoader;
    protected Object serviceObject;

    protected void configureClassloader() throws CoreException
    {
        if( proxyClassLoader == null )
        {
            proxyClassLoader = createClassLoader();
        }

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        if( currentClassLoader.equals( proxyClassLoader ) )
        {
            return;
        }
        else
        {
            previousClassLoader = currentClassLoader;
            Thread.currentThread().setContextClassLoader( proxyClassLoader );
        }
    }

    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
    {
        Object retval = null;
        Throwable error = null;

        configureClassloader();

        try
        {
            Object serviceObject = getServiceObject();

            Method serviceMethod = serviceObject.getClass().getMethod( method.getName(), method.getParameterTypes() );

            retval = serviceMethod.invoke( serviceObject, args );
        }
        catch( Throwable t )
        {
            ScriptingCore.logError( "Error in script method " + method.getName(), t );
            error = t;
        }
        finally
        {
            unconfigureClassloader();
        }

        if( error != null )
        {
            throw new RuntimeException( "Error in workflow validation proxy.", error.getCause() );
        }

        return retval;
    }

    protected URLClassLoader createClassLoader() throws CoreException
    {
        List<URL> urls = new ArrayList<URL>();

        urls.add( ScriptingCore.getGroovyScriptingSupport().getGroovyLibURL() );

        for( URL url : getProxyClasspath() )
        {
            urls.add( url );
        }

        return new URLClassLoader( urls.toArray( new URL[0] ), Platform.class.getClassLoader() );
    }

    protected void unconfigureClassloader()
    {
        if( previousClassLoader != null )
        {
            Thread.currentThread().setContextClassLoader( previousClassLoader );
        }
        else
        {
            Thread.currentThread().setContextClassLoader( this.getClass().getClassLoader() );
        }
    }

    protected Object getServiceObject() throws Exception
    {

        if( serviceObject == null )
        {
            serviceObject = ScriptingCore.getGroovyScriptingSupport().newInstanceFromFile( getGroovyFile() );
        }

        return serviceObject;
    }

    protected abstract File getGroovyFile() throws Exception;

    protected abstract URL[] getProxyClasspath() throws CoreException;
}
