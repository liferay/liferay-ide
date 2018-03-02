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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
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
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatVersionHandler;
import org.eclipse.jst.server.tomcat.core.internal.TomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerUtil;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class LiferayTomcatServer extends TomcatServer
    implements ILiferayTomcatConstants, ILiferayTomcatServer, ILiferayTomcatServerWC
{

    public LiferayTomcatServer()
    {
        super();
    }

    @Override
    public String getId()
    {
        return getServer().getId();
    }

    @Override
    public String getHost()
    {
        return getServer().getHost();
    }

    @Override
    public String getAutoDeployDirectory()
    {
        return getAttribute( PROPERTY_AUTO_DEPLOY_DIR, "../deploy" ); //$NON-NLS-1$
    }

    @Override
    public String getAutoDeployInterval()
    {
        return getAttribute( PROPERTY_AUTO_DEPLOY_INTERVAL, ILiferayTomcatConstants.DEFAULT_AUTO_DEPLOY_INTERVAL );
    }

    public int getDefaultServerMode()
    {
        int defaultServerMode = ILiferayTomcatConstants.STANDARD_SERVER_MODE;

        try
        {
            String version = LiferayTomcatUtil.getVersion( ((ILiferayRuntime)getServer().getRuntime() ) );
            Version portalVersion = Version.parseVersion( version );

            if( CoreUtil.compareVersions( portalVersion, ILiferayConstants.V620 ) < 0 )
            {
                defaultServerMode = ILiferayTomcatConstants.DEVELOPMENT_SERVER_MODE;
            }
        }
        catch( Exception e )
        {
        }

        return defaultServerMode;
    }

    @Override
    public String getExternalProperties()
    {
        return getAttribute( PROPERTY_EXTERNAL_PROPERTIES, StringPool.EMPTY );
    }

    @Override
    public String getMemoryArgs()
    {
        return getAttribute( PROPERTY_MEMORY_ARGS, ILiferayTomcatConstants.DEFAULT_MEMORY_ARGS );
    }

    @Override
    public URL getPluginContextURL( String context )
    {
        try
        {
            return new URL( getPortalHomeUrl(), StringPool.FORWARD_SLASH + context );
        }
        catch( Exception ex )
        {
            return null;
        }
    }

    @Override
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

    @Override
    public URL getPortalHomeUrl()
    {
        try
        {
            TomcatConfiguration config = getTomcatConfiguration();
            if( config == null )
                return null;

            String url = "http://" + getServer().getHost(); //$NON-NLS-1$
            int port = config.getMainPort().getPort();
            port = ServerUtil.getMonitoredPort( getServer(), port, "web" ); //$NON-NLS-1$
            if( port != 80 )
                url += ":" + port; //$NON-NLS-1$
            return new URL( url );
        }
        catch( Exception ex )
        {
            return null;
        }
    }

    @Override
    public String getPassword()
    {
        return getAttribute( ATTR_PASSWORD, DEFAULT_PASSWORD );
    }

    public ILiferayTomcatConfiguration getLiferayTomcatConfiguration() throws CoreException
    {
        return (ILiferayTomcatConfiguration) getTomcatConfiguration();
    }

    @Override
    public boolean getUseDefaultPortalServerSettings()
    {
        return getAttribute(
            PROPERTY_USE_DEFAULT_PORTAL_SERVER_SETTINGS,
            ILiferayTomcatConstants.DEFAULT_USE_DEFAULT_PORTAL_SERVER_SETTING );
    }

    @Override
    public int getServerMode()
    {
        return getAttribute( PROPERTY_SERVER_MODE, getDefaultServerMode() );
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
                            Msgs.errorConfigurationProjectClosed, path, project.getName() ), null ) );
                }
                throw new CoreException( new Status( IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(
                    Msgs.errorNoConfiguration, path ), null ) );
            }

            String id = getServer().getServerType().getId();
            if( id.endsWith( "60" ) ) //$NON-NLS-1$
            {
                configuration = new LiferayTomcat60Configuration( folder );
            }
            else if( id.endsWith( "70" ) || id.endsWith( "7062" ) ) //$NON-NLS-1$ //$NON-NLS-2$
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

    @Override
    public String getUsername()
    {
        return getAttribute( ATTR_USERNAME, DEFAULT_USERNAME );
    }

    @Override
    public String getUserTimezone()
    {
        return getAttribute( PROPERTY_USER_TIMEZONE, ILiferayTomcatConstants.DEFAULT_USER_TIMEZONE );
    }

    @Override
    public URL getWebServicesListURL()
    {
        try
        {
            return new URL( getPortalHomeUrl(), "/tunnel-web/axis" ); //$NON-NLS-1$
        }
        catch( MalformedURLException e )
        {
            LiferayTomcatPlugin.logError( "Unable to get web services list URL", e ); //$NON-NLS-1$
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
        IPath path = runtime.getLocation().append( "conf" ); //$NON-NLS-1$

        String id = getServer().getServerType().getId();
        IFolder folder = getServer().getServerConfiguration();
        if( id.endsWith( "60" ) ) //$NON-NLS-1$
        {
            configuration = new LiferayTomcat60Configuration( folder );
        }
        else if( id.endsWith( "70" ) || id.endsWith( "7062" ) ) //$NON-NLS-1$ //$NON-NLS-2$
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

        if( ListUtil.isNotEmpty(add) )
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
        else if( ListUtil.isNotEmpty(remove) )
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
                Msgs.removingExtPlugin, LiferayTomcatPlugin.PREFERENCES_REMOVE_EXT_PLUGIN_TOGGLE_KEY );
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
            if( serverInfo.contains( Msgs.enterpriseEdition ) && !( expectedServerInfo.contains( Msgs.enterpriseEdition ) ) )
            {
                LiferayTomcatUtil.displayToggleMessage(
                    Msgs.switchRuntimeType, LiferayTomcatPlugin.PREFERENCES_EE_UPGRADE_MSG_TOGGLE_KEY );
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
        ServerUtil.setServerDefaultName(getServerWorkingCopy());
    }

    public void setExternalProperties( String externalProperties )
    {
        setAttribute( PROPERTY_EXTERNAL_PROPERTIES, externalProperties );
    }

    @Override
    public void setMemoryArgs( String memoryArgs )
    {
        setAttribute( PROPERTY_MEMORY_ARGS, memoryArgs );
    }

    @Override
    public void setPassword( String password )
    {
        setAttribute( ATTR_PASSWORD, password );
    }

    public void setUseDefaultPortalServerSettings( final boolean useDefaultPortalServerSettings )
    {
        setAttribute( PROPERTY_USE_DEFAULT_PORTAL_SERVER_SETTINGS, useDefaultPortalServerSettings );
    }

    public void setServerMode( int serverMode )
    {
        setAttribute( PROPERTY_SERVER_MODE, serverMode );
    }

    @Override
    public void setUsername( String username )
    {
        setAttribute( ATTR_USERNAME, username );
    }

    @Override
    public void setUserTimezone( String userTimezone )
    {
        setAttribute( PROPERTY_USER_TIMEZONE, userTimezone );
    }

    protected IEclipsePreferences getPrefStore()
    {
        return LiferayTomcatPlugin.getPreferenceStore();
    }

    private static class Msgs extends NLS
    {
        public static String enterpriseEdition;
        public static String errorConfigurationProjectClosed;
        public static String errorNoConfiguration;
        public static String removingExtPlugin;
        public static String switchRuntimeType;

        static
        {
            initializeMessages( LiferayTomcatServer.class.getName(), Msgs.class );
        }
    }
}
