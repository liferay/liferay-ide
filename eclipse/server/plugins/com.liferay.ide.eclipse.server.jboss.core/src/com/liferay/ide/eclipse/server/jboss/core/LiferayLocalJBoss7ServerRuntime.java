/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.jboss.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.jboss.ide.eclipse.as.core.server.internal.v7.IJBoss7Deployment;
import org.jboss.ide.eclipse.as.core.server.internal.v7.LocalJBoss7ServerRuntime;
import org.jboss.ide.eclipse.as.core.util.IJBossToolingConstants;
import org.osgi.framework.Version;

import com.liferay.ide.eclipse.core.ILiferayConstants;
import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.sdk.util.SDKUtil;
import com.liferay.ide.eclipse.server.core.ILiferayLocalRuntime;
import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;
import com.liferay.ide.eclipse.server.jboss.core.util.LiferayJBossUtil;
import com.liferay.ide.eclipse.server.util.ReleaseHelper;
import com.liferay.ide.eclipse.server.util.ServerUtil;

/**
 * @author kamesh
 */
public class LiferayLocalJBoss7ServerRuntime extends LocalJBoss7ServerRuntime implements ILiferayLocalRuntime
{

	private static final String PROP_JBOSS_MODULES_DIR = "modules";

	private static final String PROP_JBOSS_STANDALONE_DEPLOYMENTS_DIR = "standalone/deployments";

	private static final String PROP_JBOSS_PORTAL_DIR_PATH = PROP_JBOSS_STANDALONE_DEPLOYMENTS_DIR+"/ROOT.war";

	private static final String PROP_JBOSS_APP_SERVER_TYPE = "jboss";

	public static final String PROP_BUNDLE_SOURCE_LOCATION = "bundle-source-location";

	public static final String PROP_BUNDLE_ZIP_LOCATION = "bundle-zip-location";

	private IStatus runtimeDelegateStatus;

	protected final HashMap<IPath, ReleaseHelper> releaseHelpers = new HashMap<IPath, ReleaseHelper>();

	/**
	 * 
	 */
	public LiferayLocalJBoss7ServerRuntime()
	{
		// TODO Auto-generated constructor stub
	}

	public IPath[] getAllUserClasspathLibraries()
	{
		return LiferayJBossUtil.getAllUserClasspathLibraries( getRuntimeLocation(), getPortalDir() );
	}

	public Properties getPortletCategories()
	{
		return ServerUtil.getCategories( getRuntimeLocation(), getPortalDir() );
	}

	public String getPortalVersion()
	{
		String portalVersion = "";
		try
		{
			portalVersion = ServerUtil.getPortalVersion( getPortalDir() );
		}
		catch ( IOException e )
		{

		}

		return portalVersion;
	}

	public IPath getRuntimeLocation()
	{
		return getRuntime().getLocation();
	}

	public String[] getSupportedHookProperties()
	{
		String[] supportedHooks = new String[0];
		try
		{
			supportedHooks = ServerUtil.getSupportedHookProperties( getRuntimeLocation(), getPortalDir() );
		}
		catch ( Exception e )
		{

		}
		return supportedHooks;
	}

	public IPath getAppServerDir()
	{
		return getRuntimeLocation();
	}

	public String getAppServerType()
	{
		return PROP_JBOSS_APP_SERVER_TYPE;
	}

	public IPath getDeployDir()
	{
		return getAppServerDir().append( PROP_JBOSS_STANDALONE_DEPLOYMENTS_DIR );
	}

	public IPath getLibGlobalDir()
	{
		return getAppServerDir().append( PROP_JBOSS_MODULES_DIR );
	}

	public IPath getPortalDir()
	{
		return getAppServerDir().append( PROP_JBOSS_PORTAL_DIR_PATH );
	}

	public String[] getServletFilterNames()
	{
		String[] servletFilters = new String[0];
		try
		{
			servletFilters = ServerUtil.getServletFilterNames( getPortalDir() );
		}
		catch ( Exception e )
		{

		}
		return servletFilters;
	}

	public IVMInstall getVMInstall()
	{
		return getVM();
	}

	public IPath getBundleZipLocation()
	{
		String zipLocation = getAttribute( PROP_BUNDLE_ZIP_LOCATION, (String) null );

		return zipLocation != null ? new Path( zipLocation ) : null;
	}

	public String getServerInfo()
	{
		String serverInfo = "";
		try
		{
			serverInfo = ServerUtil.getServerInfo( getPortalDir() );
		}
		catch ( IOException e )
		{

		}

		return serverInfo;

	}

	public void setBundleZipLocation( IPath path )
	{
		// TODO Auto-generated method stub

	}

	public IPath getPortalSourceLocation()
	{
		String sourceLocation = getAttribute( PROP_BUNDLE_SOURCE_LOCATION, (String) null );

		return sourceLocation != null ? new Path( sourceLocation ) : null;
	}

	protected String getExpectedServerInfo()
	{
		return "Liferay Portal";
	}

	protected Version getLeastSupportedVersion()
	{
		return ILiferayConstants.LEAST_SUPPORTED_VERSION;
	}

	@Override
	public IStatus validate()
	{
		// first validate that this runtime is
		if ( runtimeDelegateStatus == null )
		{
			runtimeDelegateStatus = LiferayServerCorePlugin.validateRuntimeDelegate( this );
		}

		if ( !runtimeDelegateStatus.isOK() )
		{
			return runtimeDelegateStatus;
		}

		IStatus status = super.validate();

		if ( !status.isOK() )
		{
			return status;
		}

		String version = getPortalVersion();

		Version portalVersion = Version.parseVersion( version );

		if ( portalVersion != null && ( SDKUtil.compareVersions( portalVersion, getLeastSupportedVersion() ) < 0 ) )
		{
			status =
				LiferayJBossServerCorePlugin.createErrorStatus( "Portal version not supported.  Need at least " +
					getLeastSupportedVersion() );
		}

		if ( !getRuntime().isStub() )
		{
			String serverInfo = getServerInfo();

			if ( CoreUtil.isNullOrEmpty( serverInfo ) || serverInfo.indexOf( getExpectedServerInfo() ) < 0 )
			{
				status =
					LiferayJBossServerCorePlugin.createErrorStatus( "Portal server not supported.  Expecting " +
						getExpectedServerInfo() );
			}
		}

		// need to check if this runtime is specifying a zip file for a bundle package, if so validate it
		IPath bundleZip = getBundleZipLocation();

		if ( bundleZip != null && ( !CoreUtil.isNullOrEmpty( bundleZip.toString() ) ) )
		{
			String rootEntryName = null;

			try
			{
				ZipInputStream zis = new ZipInputStream( new FileInputStream( bundleZip.toFile() ) );

				ZipEntry rootEntry = zis.getNextEntry();
				rootEntryName = new Path( rootEntry.getName() ).segment( 0 );

				if ( rootEntryName.endsWith( "/" ) )
				{
					rootEntryName = rootEntryName.substring( 0, rootEntryName.length() - 1 );
				}

				boolean foundJBoss = false;

				ZipEntry entry = zis.getNextEntry();

				while ( entry != null && !foundJBoss )
				{
					String entryName = entry.getName();

					if ( entryName.startsWith( rootEntryName + "/jboss-" ) )
					{
						foundJBoss = true;
					}

					entry = zis.getNextEntry();
				};
			}
			catch ( Exception e )
			{
				return LiferayJBossServerCorePlugin.createWarningStatus( "Bundle zip location does not specify a valid Liferay JBoss bundle." );
			}

			// if we get here then the user has specified a good zip installation so now we need to see if the
			// installation of the runtime will work for EXT plugins.
			IPath location = getRuntime().getLocation();
			String bundleDir = location.removeLastSegments( 1 ).lastSegment();

			if ( !bundleDir.equals( rootEntryName ) )
			{
				return LiferayJBossServerCorePlugin.createWarningStatus( "Runtime location directory layout does not match zip file." );
			}
		}

		return status;
	}

	/**
	 * @param serviceJar
	 * @return
	 */
	protected ReleaseHelper getReleaseHelper( IPath serviceJar )
	{

		ReleaseHelper cachedHelper = releaseHelpers.get( serviceJar );

		if ( cachedHelper != null )
		{
			return cachedHelper;
		}

		ReleaseHelper newHelper = new ReleaseHelper( serviceJar );

		releaseHelpers.put( serviceJar, newHelper );

		return newHelper;
	}

}
