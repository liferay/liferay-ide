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
package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.LiferayPortalValueLoader;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */
public abstract class AbstractPortalBundle implements PortalBundle
{
    private static final String CONFIG_TYPE_SERVER = "server";
    private static final String CONFIG_TYPE_VERSION = "version";
    private static final Version MANIFEST_VERSION_REQUIRED = ILiferayConstants.V700;

    protected IPath autoDeployPath;
    protected IPath liferayHome;
    protected IPath modulesPath;
    protected IPath bundlePath;

    public AbstractPortalBundle( IPath path )
    {
        if( path == null )
        {
            throw new IllegalArgumentException( "path cannot be null" );
        }

        this.bundlePath = path;

        this.liferayHome = bundlePath.append( ".." );

        this.autoDeployPath = this.liferayHome.append( "deploy" );

        this.modulesPath = this.liferayHome.append( "osgi" );
    }

    public AbstractPortalBundle( Map<String, String> appServerProperties)
    {
        if( appServerProperties == null )
        {
            throw new IllegalArgumentException( "bundle parameters cannot be null" );
        }

        final String appServerPath = (appServerProperties.get( "app.server.dir"));
        final String appServerDeployPath = (appServerProperties.get( "app.server.deploy.dir"));
        final String appServerParentPath = (appServerProperties.get( "app.server.parent.dir"));

        this.bundlePath = new Path(appServerPath);

        this.liferayHome = new Path(appServerParentPath);

        this.autoDeployPath = new Path(appServerDeployPath);

        this.modulesPath = null;
    }

    @Override
    public IPath getAppServerDir()
    {
        return this.bundlePath;
    }

    @Override
    public IPath[] getBundleDependencyJars()
    {
        List<IPath> libs = new ArrayList<IPath>();
        IPath bundleLibPath =  getAppServerLibDir();
        List<File> libFiles;
        try
        {
            libFiles = FileListing.getFileListing( new File( bundleLibPath.toOSString() ) );
            for( File lib : libFiles )
            {
                if( lib.exists() && lib.getName().endsWith( ".jar" ) ) //$NON-NLS-1$
                {
                    libs.add( new Path( lib.getPath() ) );
                }
            }
        }
        catch( FileNotFoundException e )
        {
        }

        return libs.toArray( new IPath[libs.size()] );
    }

    protected abstract IPath getAppServerLibDir();

    protected abstract int getDefaultJMXRemotePort();

    @Override
    public String[] getHookSupportedProperties()
    {
        IPath portalDir = getAppServerPortalDir();
        IPath[] extraLibs = getBundleDependencyJars();

        return new LiferayPortalValueLoader( portalDir, extraLibs ).loadHookPropertiesFromClass();
    }

    @Override
    public int getJmxRemotePort()
    {
        return getDefaultJMXRemotePort();
    }

    @Override
    public IPath getAutoDeployPath()
    {
        return this.autoDeployPath;
    }

    @Override
    public IPath getModulesPath()
    {
        return this.modulesPath;
    }

    @Override
    public IPath getLiferayHome()
    {
        return this.liferayHome;
    }

    @Override
    public String getVersion()
    {
        return getPortalVersion( getAppServerPortalDir(), getBundleDependencyJars()  );
    }

    @Override
    public Properties getPortletCategories()
    {
        return ServerUtil.getPortletCategories( getAppServerPortalDir() );
    }

    @Override
    public Properties getPortletEntryCategories()
    {
        return ServerUtil.getEntryCategories( getAppServerPortalDir(), getVersion() );
    }

    private String getConfigInfoFromCache( String configType, IPath portalDir )
    {
        IPath configInfoPath = null;

        if( configType.equals( CONFIG_TYPE_VERSION ) )
        {
            configInfoPath = LiferayServerCore.getDefault().getStateLocation().append( "version.properties" );
        }

        else if( configType.equals( CONFIG_TYPE_SERVER ) )
        {
            configInfoPath = LiferayServerCore.getDefault().getStateLocation().append( "serverInfos.properties" );
        }

        else
        {
            return null;
        }

        File configInfoFile = configInfoPath.toFile();

        String portalDirKey = CoreUtil.createStringDigest( portalDir.toPortableString() );

        Properties properties = new Properties();

        if( configInfoFile.exists() )
        {
            try( FileInputStream fileInput = new FileInputStream( configInfoFile ) )
            {
                properties.load( fileInput );
                String configInfo = (String) properties.get( portalDirKey );

                if( !CoreUtil.isNullOrEmpty( configInfo ) )
                {
                    return configInfo;
                }
            }
            catch(IOException e)
            {
                LiferayServerCore.logError( e );
            }
        }

        return null;
    }

    private String getPortalVersion( IPath portalDir, IPath[] extraLib)
    {
        String version = getConfigInfoFromCache( CONFIG_TYPE_VERSION, portalDir );

        if( version == null )
        {
            version = getConfigInfoFromManifest( CONFIG_TYPE_VERSION, portalDir );

            if( version == null )
            {
                final LiferayPortalValueLoader loader = new LiferayPortalValueLoader( portalDir, extraLib );

                final Version loadedVersion = loader.loadVersionFromClass();

                if( loadedVersion != null )
                {
                    version = loadedVersion.toString();
                }
            }

            if( version != null )
            {
                saveConfigInfoIntoCache( CONFIG_TYPE_VERSION, version, portalDir );
            }
        }

        return version;
    }

    private String getConfigInfoFromManifest( String configType, IPath portalDir )
    {
        File implJar = portalDir.append( "/WEB-INF/lib/portal-impl.jar").toFile();

        String version = null;
        String serverInfo = null;

        if( implJar.exists() )
        {
            try ( JarFile jar = new JarFile( implJar ) )
            {
                Manifest manifest = jar.getManifest();

                Attributes attributes = manifest.getMainAttributes();

                version = attributes.getValue( "Liferay-Portal-Version" );
                serverInfo = attributes.getValue( "Liferay-Portal-Server-Info" );

                if( CoreUtil.compareVersions( Version.parseVersion( version ), MANIFEST_VERSION_REQUIRED ) < 0 )
                {
                    version = null;
                    serverInfo = null;
                }
            }
            catch( IOException e )
            {
                LiferayServerCore.logError( e );
            }
        }

        if( configType.equals( CONFIG_TYPE_VERSION ) )
        {
            return version;
        }

        if( configType.equals( CONFIG_TYPE_SERVER ) )
        {
            return serverInfo;
        }

        return null;
    }

    private void saveConfigInfoIntoCache( String configType, String configInfo, IPath portalDir )
    {
        IPath versionsInfoPath = null;

        if( configType.equals( CONFIG_TYPE_VERSION ) )
        {
            versionsInfoPath = LiferayServerCore.getDefault().getStateLocation().append( "version.properties" );
        }
        else if( configType.equals( CONFIG_TYPE_SERVER ) )
        {
            versionsInfoPath = LiferayServerCore.getDefault().getStateLocation().append( "serverInfos.properties" );
        }

        if( versionsInfoPath != null )
        {
            File versionInfoFile = versionsInfoPath.toFile();

            if( configInfo != null )
            {
                String portalDirKey = CoreUtil.createStringDigest( portalDir.toPortableString() );
                Properties properties = new Properties();

                try ( FileInputStream fileInput = new FileInputStream( versionInfoFile ) )
                {
                    properties.load( fileInput );
                }
                catch( Exception e )
                {
                }

                try ( FileOutputStream fileOutput = new FileOutputStream( versionInfoFile ) )
                {
                    properties.put( portalDirKey, configInfo );
                    properties.store( fileOutput, StringPool.EMPTY );
                }
                catch( Exception e )
                {
                    LiferayServerCore.logError( e );
                }
            }
        }
    }
}