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

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;

/**
 * @author Gregory Amerson
 */
public class GroovyScriptingSupport
{

    public Object newInstanceFromFile( File scriptFile )
    {
        @SuppressWarnings( "resource" ) final GroovyClassLoader gcl = new GroovyClassLoader();

        try
        {
            return gcl.parseClass( scriptFile ).newInstance();
        }
        catch( Exception e )
        {
            ScriptingCore.logError( "Could not create script file.", e );
        }
        finally
        {
            // only available in jdk 1.7
//            try
//            {
//                gcl.close();
//            }
//            catch( IOException e )
//            {
//                ScriptingCore.logError( "Could not close classloader.", e );
//            }
        }

        return null;
    }

    protected GroovyShell createGroovyShell( Map<String, Object> variableMap )
    {
        GroovyShell groovyShell = new GroovyShell( new GroovyClassLoader() );

        if( variableMap != null && variableMap.keySet() != null && variableMap.keySet().size() > 0 )
        {
            for( String key : variableMap.keySet() )
            {
                groovyShell.setVariable( key, variableMap.get( key ) );
            }
        }

        return groovyShell;
    }

    public URL getGroovyLibURL()
    {
        try
        {
            return FileLocator.toFileURL( ScriptingCore.getPluginEntry( "/lib/groovy-all-1.7.5.jar" ) );
        }
        catch( IOException e )
        {
        }

        return null;
    }

}
