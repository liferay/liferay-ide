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

package com.liferay.ide.kaleo.core.util;

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.scripting.core.GroovyScriptProxy;
import com.liferay.ide.server.core.ILiferayRuntime;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IRuntime;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class WorkflowValidationProxy extends GroovyScriptProxy
{

    private IRuntime runtime;

    public WorkflowValidationProxy( IRuntime runtime )
    {
        this.runtime = runtime;
    }

    @SuppressWarnings( "deprecation" )
    protected URL[] getProxyClasspath() throws CoreException
    {
        List<URL> scriptUrlList = new ArrayList<URL>();

        IRuntime serverRuntime = this.runtime;

        if( serverRuntime == null )
        {
            throw new CoreException( KaleoCore.createErrorStatus( "Could not get server runtime." ) );
        }

        ILiferayRuntime liferayRuntime = (ILiferayRuntime) serverRuntime.loadAdapter( ILiferayRuntime.class, null );

        File libFolder = liferayRuntime.getAppServerPortalDir().append( "WEB-INF/lib" ).toFile();

        // dom4j.jar portal-impl.jar xercesImpl.jar

        String[] libs = libFolder.list( new FilenameFilter()
        {
            public boolean accept( File dir, String name )
            {
                return name.endsWith( ".jar" ) &&
                    ( name.contains( "dom4j" ) || name.contains( "xercesImpl" ) || name.contains( "portal-impl" ) ||
                        name.contains( "util-java" ) || name.contains( "commons-lang" ) );
            }
        } );

        for( String lib : libs )
        {
            File libJar = new File( libFolder, lib );

            if( libJar.exists() )
            {
                try
                {
                    scriptUrlList.add( libJar.toURL() );
                }
                catch( MalformedURLException e )
                {
                }
            }
        }

        File[] jars = new File[]
        {
            runtime.getLocation().append( "webapps/kaleo-designer-portlet/WEB-INF/lib/kaleo-web-service.jar" ).toFile(),
            runtime.getLocation().append( "lib/ext/portal-service.jar" ).toFile()
        };

        for( File jar : jars )
        {
            if( jar.exists() )
            {
                try
                {
                    scriptUrlList.add( jar.toURL() );
                }
                catch( MalformedURLException e )
                {
                }
            }
        }

        File parserDir = runtime.getLocation().append( "webapps/kaleo-web/WEB-INF/classes/" ).toFile();

        if( parserDir.exists() )
        {
            try
            {
                scriptUrlList.add( parserDir.toURL() );
            }
            catch( Exception e )
            {
            }
        }

        return scriptUrlList.toArray( new URL[0] );
    }

    protected File getGroovyFile() throws Exception
    {
        final ILiferayRuntime liferayRuntime = (ILiferayRuntime) runtime.loadAdapter( ILiferayRuntime.class, null );

        if( liferayRuntime != null )
        {
            final Version version = new Version( liferayRuntime.getPortalVersion() );

            return new File( FileLocator.toFileURL(
                KaleoCore.getDefault().getBundle().getEntry( getGroovyWorkflowValidationScript( version ) ) ).getFile() );
        }

        IStatus error = KaleoCore.createErrorStatus( "Unable to locate groovy script" );
        KaleoCore.getDefault().getLog().log( error );
        throw new CoreException( error );
    }

    private String getGroovyWorkflowValidationScript( final Version runtimeVersion )
    {
        final int result = runtimeVersion.compareTo( new Version( 6, 1, 30 ) );

        if( result > 0 )
        {
            return "/scripts/portal/WorkflowValidation620.groovy";
        }
        else if( result == 0 )
        {
            return "/scripts/portal/WorkflowValidation6130.groovy";
        }
        else
        {
            return "/scripts/portal/WorkflowValidation6120.groovy";
        }
    }
}
