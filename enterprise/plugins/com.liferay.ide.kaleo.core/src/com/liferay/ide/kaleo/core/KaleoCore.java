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
package com.liferay.ide.kaleo.core;

import com.liferay.ide.kaleo.core.util.IWorkflowValidation;
import com.liferay.ide.kaleo.core.util.WorkflowValidationProxy;
import com.liferay.ide.server.core.ILiferayServer;

import java.lang.reflect.Proxy;
import java.util.HashMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class KaleoCore extends Plugin
{
    public static final String DEFAULT_KALEO_VERSION = "7.0.0";

    public static final String PLUGIN_ID = "com.liferay.ide.kaleo.core"; //$NON-NLS-1$

    public static final QualifiedName DEFAULT_SCRIPT_LANGUAGE_KEY = new QualifiedName( PLUGIN_ID, "defaultScriptLanguage" );

    public static final QualifiedName DEFAULT_TEMPLATE_LANGUAGE_KEY = new QualifiedName( PLUGIN_ID, "defaultTemplateLanguage" );

    public static final QualifiedName GRID_VISIBLE_KEY = new QualifiedName( PLUGIN_ID, "gridVisible" );

    private static HashMap<String, IKaleoConnection> kaleoConnections;

    // The shared instance
    private static KaleoCore plugin;

    private static HashMap<String, IWorkflowValidation> workflowValidators;
    // The plug-in ID

    public static IStatus createErrorStatus( String msg )
    {
        return createErrorStatus( msg, null );
    }

    public static IStatus createErrorStatus( String msg, Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, e );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static KaleoCore getDefault()
    {
        return plugin;
    }

    public static IKaleoConnection getKaleoConnection( final ILiferayServer liferayServer )
    {
        if( kaleoConnections == null )
        {
            kaleoConnections = new HashMap<String, IKaleoConnection>();

            ServerCore.addServerLifecycleListener( new IServerLifecycleListener()
            {

                public void serverAdded( IServer server )
                {
                }

                public void serverChanged( IServer server )
                {
                }

                public void serverRemoved( IServer s )
                {
                    if( liferayServer.equals( s ) )
                    {
                        IKaleoConnection service = kaleoConnections.get( liferayServer.getId() );

                        if( service != null )
                        {
                            service = null;
                            kaleoConnections.put( liferayServer.getId(), null );
                        }
                    }
                }
            } );
        }

        IKaleoConnection service = kaleoConnections.get( liferayServer.getId() );

        if( service == null )
        {
            service = new KaleoConnection();

            updateKaleoConnectionSettings( liferayServer, service );

            kaleoConnections.put( liferayServer.getId(), service );
        }

        return service;
    }

    public static IKaleoConnection getKaleoConnection( IServer parent )
    {
        return getKaleoConnection( (ILiferayServer) parent.loadAdapter( ILiferayServer.class, null ) );
    }

    public static IWorkflowValidation getWorkflowValidation(final IRuntime runtime)
    {
        if (workflowValidators == null)
        {
            workflowValidators = new HashMap<String, IWorkflowValidation>();
        }

        IWorkflowValidation validator = workflowValidators.get(runtime.getId());

        if (validator == null)
        {
            Class<?>[] interfaces = new Class<?>[] { IWorkflowValidation.class };

            validator =
                (IWorkflowValidation) Proxy.newProxyInstance(
                    IWorkflowValidation.class.getClassLoader(), interfaces, new WorkflowValidationProxy(runtime));

            workflowValidators.put(runtime.getId(), validator);
        }

        return validator;
    }

    public static void logError( Exception e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e ) );
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, msg, e ) );
    }

    public static void updateKaleoConnectionSettings( ILiferayServer server )
    {
        updateKaleoConnectionSettings( server, getKaleoConnection( server ) );
    }

    public static void updateKaleoConnectionSettings( ILiferayServer server, IKaleoConnection connection )
    {
        connection.setHost( server.getHost() );
        connection.setHttpPort( server.getHttpPort() );
        connection.setPortalHtmlUrl( server.getPortalHomeUrl() );
        connection.setPortalContextPath( "/" );
        connection.setUsername( server.getUsername() );
        connection.setPassword( server.getPassword() );
    }

    private WorkflowSupportManager workflowSupportManager;

    public synchronized WorkflowSupportManager getWorkflowSupportManager()
    {
        if( this.workflowSupportManager == null )
        {
            this.workflowSupportManager = new WorkflowSupportManager();
        }

        return this.workflowSupportManager;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );
    }
}
