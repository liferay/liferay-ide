/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *    Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.tomcat.core.util.LiferayTomcatUtil;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatVersionHandler;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.TomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayTomcatServer extends TomcatServer
    implements ILiferayTomcatConstants, ILiferayTomcatServer, ILiferayTomcatServerWC
{

    public LiferayTomcatServer()
    {
        super();
    }

    public String getId()
    {
        return getServer().getId();
    }

    public String getHost()
    {
        return getServer().getHost();
    }

    public String getAutoDeployDirectory()
    {
        return getAttribute( PROPERTY_AUTO_DEPLOY_DIR, "../deploy" );
    }

    public String getAutoDeployInterval()
    {
        return getAttribute( PROPERTY_AUTO_DEPLOY_INTERVAL, ILiferayTomcatConstants.DEFAULT_AUTO_DEPLOY_INTERVAL );
    }

    public String getExternalProperties()
    {
        return getAttribute( PROPERTY_EXTERNAL_PROPERTIES, "" );
    }

    public String getMemoryArgs()
    {
        return getAttribute( PROPERTY_MEMORY_ARGS, ILiferayTomcatConstants.DEFAULT_MEMORY_ARGS );
    }

    public URL getPluginContextURL( String context )
    {
        try
        {
            return new URL( getPortalHomeUrl(), "/" + context );
        }
        catch( Exception ex )
        {
            return null;
        }
    }

    public String getHttpPort()
    {
        try
        {
            return String.valueOf( getTomcatConfiguration().getMainPort().getPort() );
        }
        catch( CoreException e )
        {
            return null;
        }
    }

    public URL getPortalHomeUrl()
    {
        try
        {
            TomcatConfiguration config = getTomcatConfiguration();
            if( config == null )
                return null;

            String url = "http://" + getServer().getHost();
            int port = config.getMainPort().getPort();
            port = ServerUtil.getMonitoredPort( getServer(), port, "web" );
            if( port != 80 )
                url += ":" + port;
            return new URL( url );
        }
        catch( Exception ex )
        {
            return null;
        }
    }

    public ILiferayTomcatConfiguration getLiferayTomcatConfiguration() throws CoreException
    {
        return (ILiferayTomcatConfiguration) getTomcatConfiguration();
    }

    @Override
    public TomcatConfiguration getTomcatConfiguration() throws CoreException
    {
        if( configuration == null )
        {
            IFolder folder = getServer().getServerConfiguration();
            if( folder == null || !folder.exists() )
            {
                String path = null;
                if( folder != null )
                {
                    path = folder.getFullPath().toOSString();
                    IProject project = folder.getProject();
                    if( project != null && project.exists() && !project.isOpen() )
                        throw new CoreException( new Status( IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(
                            Messages.errorConfigurationProjectClosed, path, project.getName() ), null ) );
                }
                throw new CoreException( new Status( IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(
                    Messages.errorNoConfiguration, path ), null ) );
            }

            String id = getServer().getServerType().getId();
            if( id.indexOf( "60" ) > 0 )
            {
                configuration = new LiferayTomcat60Configuration( folder );
            }
            else if( id.indexOf( "70" ) > 0 )
            {
                configuration = new LiferayTomcat70Configuration( folder );
            }
            try
            {
                ( (ILiferayTomcatConfiguration) configuration ).load( folder, null );
            }
            catch( CoreException ce )
            {
                // ignore
                configuration = null;
                throw ce;
            }
        }
        return configuration;
    }

    @Override
    public ITomcatVersionHandler getTomcatVersionHandler()
    {
        ITomcatVersionHandler handler = super.getTomcatVersionHandler();
        if( handler instanceof ILiferayTomcatHandler )
        {
            ( (ILiferayTomcatHandler) handler ).setCurrentServer( getServer() );
        }
        return handler;
    }

    public String getUserTimezone()
    {
        return getAttribute( PROPERTY_USER_TIMEZONE, ILiferayTomcatConstants.DEFAULT_USER_TIMEZONE );
    }

    public URL getWebServicesListURL()
    {
        try
        {
            return new URL( getPortalHomeUrl(), "/tunnel-web/axis" );
        }
        catch( MalformedURLException e )
        {
            LiferayTomcatPlugin.logError( "Unable to get web services list URL", e );
        }

        return null;
    }

    @Override
    public void importRuntimeConfiguration( IRuntime runtime, IProgressMonitor monitor ) throws CoreException
    {
        if( runtime == null )
        {
            configuration = null;
            return;
        }
        IPath path = runtime.getLocation().append( "conf" );

        String id = getServer().getServerType().getId();
        IFolder folder = getServer().getServerConfiguration();
        if( id.indexOf( "60" ) > 0 )
        {
            configuration = new LiferayTomcat60Configuration( folder );
        }
        else if( id.indexOf( "70" ) > 0 )
        {
            configuration = new LiferayTomcat70Configuration( folder );
        }

        if( path.toFile().exists() )
        {
            try
            {
                configuration.importFromPath( path, isTestEnvironment(), monitor );
            }
            catch( CoreException ce )
            {
                // ignore
                configuration = null;
                throw ce;
            }
        }
    }

    @Override
    public void modifyModules( IModule[] add, IModule[] remove, IProgressMonitor monitor ) throws CoreException
    {

        // check if we are adding ext plugin then we need to turn off auto publishing if we are removing ext plugin
        // then we can re-enable publishing if it was previously set

        boolean addingExt = false;
        boolean removingExt = false;

        if( !CoreUtil.isNullOrEmpty( add ) )
        {
            for( IModule m : add )
            {
                if( m.getProject() != null && ProjectUtil.isExtProject( m.getProject() ) )
                {
                    addingExt = true;
                    break;
                }
            }
        }
        else if( !CoreUtil.isNullOrEmpty( remove ) )
        {
            for( IModule m : remove )
            {
                if( m.getProject() != null && ProjectUtil.isExtProject( m.getProject() ) )
                {
                    removingExt = true;
                    break;
                }
            }
        }

        // if (addingExt && !removingExt) {
        // int existingSetting =
        // getServer().getAttribute(Server.PROP_AUTO_PUBLISH_SETTING, Server.AUTO_PUBLISH_RESOURCE);
        //
        // if (existingSetting != Server.AUTO_PUBLISH_DISABLE) {
        // LiferayTomcatUtil.displayToggleMessage(
        // "The Ext plugin Automatic publishing has been set to disabled since an Ext plugin has been added.  This setting will be restored once the Ext plugin is removed.",
        // LiferayTomcatPlugin.PREFERENCES_ADDED_EXT_PLUGIN_TOGGLE_KEY);
        // }
        //
        // IServerWorkingCopy wc = getServer().createWorkingCopy();
        // wc.setAttribute(Server.PROP_AUTO_PUBLISH_SETTING, Server.AUTO_PUBLISH_DISABLE);
        // wc.setAttribute("last-" + Server.PROP_AUTO_PUBLISH_SETTING, existingSetting);
        // wc.save(true, monitor);
        // }

        if( !addingExt && removingExt )
        {
            LiferayTomcatUtil.displayToggleMessage(
                "Removing the Ext plugin will only update the metadata; it will not actually restore any changes made by the Ext plugin.  To restore the server to its original state, use the \"Clean App Server\" action available in the project context menu.",
                LiferayTomcatPlugin.PREFERENCES_REMOVE_EXT_PLUGIN_TOGGLE_KEY );
        }
        // int lastSetting =
        // getServer().getAttribute("last-" + Server.PROP_AUTO_PUBLISH_SETTING, Server.AUTO_PUBLISH_RESOURCE);

        // IServerWorkingCopy wc = getServer().createWorkingCopy();
        // wc.setAttribute(Server.PROP_AUTO_PUBLISH_SETTING, lastSetting);
        // wc.save(true, monitor);
        // }

        super.modifyModules( add, remove, monitor );

    }

    @Override
    public void saveConfiguration( IProgressMonitor monitor ) throws CoreException
    {

        LiferayTomcatRuntime portalRuntime =
            (LiferayTomcatRuntime) getServer().getRuntime().loadAdapter( LiferayTomcatRuntime.class, monitor );

        String serverInfo = portalRuntime.getServerInfo();

        String expectedServerInfo = portalRuntime.getExpectedServerInfo();

        if( serverInfo != null && expectedServerInfo != null )
        {
            if( serverInfo.contains( "Enterprise Edition" ) && !( expectedServerInfo.contains( "Enterprise Edition" ) ) )
            {

                LiferayTomcatUtil.displayToggleMessage(
                    "The runtime type for this Server is Liferay Portal CE (Community Edition). However, the actual "
                        + "runtime configured is Liferay Portal Enterprise Edition. The current server will work correctly "
                        + "but please consider switching to the Liferay Portal EE runtime type for enhanced support.\n\n"
                        + "The Liferay Portal EE adapter is found in Liferay Developer Studio which is available for free "
                        + "to EE customers.  More infomation is on the customer portal home page on "
                        + "http://www.liferay.com/group/customer",
                    LiferayTomcatPlugin.PREFERENCES_EE_UPGRADE_MSG_TOGGLE_KEY );
            }
        }

        super.saveConfiguration( monitor );
    }

    public void setAutoDeployDirectory( String dir )
    {
        setAttribute( PROPERTY_AUTO_DEPLOY_DIR, dir );
    }

    public void setAutoDeployInterval( String interval )
    {
        setAttribute( PROPERTY_AUTO_DEPLOY_INTERVAL, interval );
    }

    @Override
    public void setDefaults( IProgressMonitor monitor )
    {
        super.setDefaults( monitor );
        setTestEnvironment( false );
        setDeployDirectory( ILiferayTomcatConstants.DEFAULT_DEPLOYDIR );
        setSaveSeparateContextFiles( false );
    }

    public void setExternalProperties( String externalProperties )
    {
        setAttribute( PROPERTY_EXTERNAL_PROPERTIES, externalProperties );
    }

    public void setMemoryArgs( String memoryArgs )
    {
        setAttribute( PROPERTY_MEMORY_ARGS, memoryArgs );
    }

    public void setUserTimezone( String userTimezone )
    {
        setAttribute( PROPERTY_USER_TIMEZONE, userTimezone );
    }

    protected IPersistentPreferenceStore getPrefStore()
    {
        return LiferayTomcatPlugin.getPreferenceStore();
    }

}
