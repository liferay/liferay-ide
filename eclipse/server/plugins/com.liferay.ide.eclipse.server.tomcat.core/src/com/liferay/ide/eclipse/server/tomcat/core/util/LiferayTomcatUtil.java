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

package com.liferay.ide.eclipse.server.tomcat.core.util;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.core.util.FileListing;
import com.liferay.ide.eclipse.core.util.FileUtil;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.core.IPluginPublisher;
import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;
import com.liferay.ide.eclipse.server.tomcat.core.ILiferayTomcatConstants;
import com.liferay.ide.eclipse.server.tomcat.core.ILiferayTomcatRuntime;
import com.liferay.ide.eclipse.server.tomcat.core.ILiferayTomcatServer;
import com.liferay.ide.eclipse.server.tomcat.core.LiferayTomcatPlugin;
import com.liferay.ide.eclipse.server.tomcat.core.LiferayTomcatRuntime70;
import com.liferay.ide.eclipse.server.tomcat.core.LiferayTomcatServerBehavior;
import com.liferay.ide.eclipse.server.util.PortalSupportHelper;
import com.liferay.ide.eclipse.server.util.ServerUtil;
import com.liferay.ide.eclipse.ui.util.UIUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jst.server.tomcat.core.internal.TomcatVersionHelper;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerEvent;
import org.osgi.framework.Version;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class LiferayTomcatUtil {

    private static String CONFIG_DIR = "conf";
    private static String SERVICE_NAME = "Catalina";
    private static String HOST_NAME = "localhost";
    private static String DEFAULT_PORTAL_DIR = "/webapps/ROOT";
    private static String DEFAULT_PORTAL_CONTEXT_FILE = "ROOT.xml";

	public static void displayToggleMessage(String msg, String key) {
		UIUtil.postInfoWithToggle(
			"Liferay Tomcat Server", msg, "Do not show this message again", false,
			LiferayTomcatPlugin.getPreferenceStore(), key);
	}

	public static IPath[] getAllUserClasspathLibraries(IPath runtimeLocation, IPath portalDir) {
		List<IPath> libs = new ArrayList<IPath>();
		IPath libFolder = runtimeLocation.append("lib");
		IPath webinfLibFolder = portalDir.append("WEB-INF/lib");

		try {
			List<File> libFiles = FileListing.getFileListing(new File(libFolder.toOSString()));

			for (File lib : libFiles) {
				if (lib.exists() && lib.getName().endsWith(".jar")) {
					libs.add(new Path(lib.getPath()));
				}
			}

			libFiles = FileListing.getFileListing(new File(webinfLibFolder.toOSString()));

			for (File lib : libFiles) {
				if (lib.exists() && lib.getName().endsWith(".jar")) {
					libs.add(new Path(lib.getPath()));
				}
			}
		}
		catch (FileNotFoundException e) {
			LiferayTomcatPlugin.logError(e);
		}

		return libs.toArray(new IPath[0]);
	}

	public static Properties getCategories(IPath runtimeLocation, IPath portalDir) {
        Properties retval = null;

        File implJar = portalDir.append("WEB-INF/lib/portal-impl.jar").toFile();

        if (implJar.exists()) {
            try {
                JarFile jar = new JarFile(implJar);
                Properties categories = new Properties();
                Properties props = new Properties();
                props.load(jar.getInputStream(jar.getEntry("content/Language.properties")));
                Enumeration<?> names = props.propertyNames();

                while (names.hasMoreElements()) {
                    String name = names.nextElement().toString();
                    if (name.startsWith("category.")) {
                        categories.put(name, props.getProperty(name));
                    }
                }
                retval = categories;

            }
            catch (IOException e) {
                LiferayTomcatPlugin.logError(e);
            }
        }

        return retval;
    }
	
	public static Properties getEntryCategories(IPath runtimeLocation, IPath portalDir) {
        Properties categories = getCategories( runtimeLocation, portalDir );
        
        Properties retval = new Properties();
        retval.put( "category.my", categories.getProperty( "category.my" ) + " Account Section" );
        retval.put( "category.portal", categories.getProperty( "category.portal" ) + " Section");
        retval.put( "category.server", categories.getProperty( "category.server" ) + " Section");
        retval.put( "category.content", categories.getProperty( "category.content" ) + " Section");

        return retval;
    }

	public static ILiferayTomcatRuntime getLiferayTomcatRuntime(IRuntime runtime) {
		if (runtime != null) {
			return (ILiferayTomcatRuntime) runtime.createWorkingCopy().loadAdapter(ILiferayTomcatRuntime.class, null);
		}

		return null;
	}

	public static IPath getPortalDir(IPath appServerDir) {
        return checkAndReturnCustomPortalDir( appServerDir );
	}

    /*
     * Added for IDE-646
     */
    protected static IPath checkAndReturnCustomPortalDir( IPath appServerDir )
    {
        IPath retval = null;
        
        if( appServerDir != null )
        {
            File contextFile = appServerDir.append( CONFIG_DIR ).append( SERVICE_NAME ).append( HOST_NAME ).append(
                    DEFAULT_PORTAL_CONTEXT_FILE ).toFile();

            if( contextFile.exists() )
            {
                Context tcPortalContext = loadContextFile( contextFile );
                
                if( tcPortalContext != null )
                {
                    String docBase = tcPortalContext.getDocBase();
                    
                    if( docBase != null )
                    {
                        return new Path( docBase );
                    }
                }
            }
            
            if( retval == null )
            {
                retval = appServerDir.append( DEFAULT_PORTAL_DIR );
            }
        }
        
        return retval;
    }

	public static String[] getSupportedHookProperties(IPath runtimeLocation, IPath portalDir)
		throws IOException {

		IPath hookPropertiesPath =
			LiferayTomcatPlugin.getDefault().getStateLocation().append("hook_properties").append(
				runtimeLocation.toPortableString().replaceAll("\\/", "_") + "_hook_properties.txt");

		IPath errorPath = LiferayTomcatPlugin.getDefault().getStateLocation().append("hookError.log");

		File hookPropertiesFile = hookPropertiesPath.toFile();

		File errorFile = errorPath.toFile();

		if (!hookPropertiesFile.exists()) {
			loadHookPropertiesFile(runtimeLocation, portalDir, hookPropertiesFile, errorFile);
		}

		String[] hookProperties = FileUtil.readLinesFromFile(hookPropertiesFile);

		if (hookProperties.length == 0) {
			loadHookPropertiesFile(runtimeLocation, portalDir, hookPropertiesFile, errorFile);

			hookProperties = FileUtil.readLinesFromFile(hookPropertiesFile);
		}

		return hookProperties;
	}

	public static String getVersion(IPath location, IPath portalDir)
        throws IOException {

	    String versionFromManifest = getConfigInfoFromManifest("version", portalDir);

        if (versionFromManifest!=null) {
	        return versionFromManifest;
	    }

	    return getVersionFromClass(location, portalDir);
	}

	public static String getVersionFromClass(IPath location, IPath portalDir)
		throws IOException {

		IPath versionsInfoPath = LiferayTomcatPlugin.getDefault().getStateLocation().append("version.properties");

		String locationKey = location.toPortableString().replaceAll("\\/", "_");

		File versionInfoFile = versionsInfoPath.toFile();

		Properties properties = new Properties();

		if (versionInfoFile.exists()) {
			try {
				properties.load(new FileInputStream(versionInfoFile));
				String version = (String) properties.get(locationKey);

				if (!CoreUtil.isNullOrEmpty(version)) {
					return version;
				}
			}
			catch (Exception e) {
			}
		}

		File versionFile = LiferayTomcatPlugin.getDefault().getStateLocation().append("version.txt").toFile();

		if (versionFile.exists()) {
			FileUtil.clearContents(versionFile);
		}

		IPath errorPath = LiferayTomcatPlugin.getDefault().getStateLocation().append("versionError.log");

		File errorFile = errorPath.toFile();

		loadVersionInfoFile(location, portalDir, versionFile, errorFile);

		Version version = CoreUtil.readVersionFile(versionFile);

		if (version.equals(Version.emptyVersion)) {
			loadVersionInfoFile(location, portalDir, versionInfoFile, errorFile);

			version = CoreUtil.readVersionFile(versionInfoFile);
		}

		if (!version.equals(Version.emptyVersion)) {
			properties.put(locationKey, version.toString());
			try {
				properties.store(new FileOutputStream(versionInfoFile), "");
			}
			catch (Exception e) {
			}
		}

		return version.toString();
	}

    public static String getConfigInfoFromManifest(String configType, IPath portalDir)
        throws IOException {

        File implJar = portalDir.append( "WEB-INF/lib/portal-impl.jar").toFile();
        String version = null;
        String serverInfo = null;

        if (implJar.exists()) {
            try {
                JarFile jar = new JarFile(implJar);

                Manifest manifest = jar.getManifest();
                Attributes attributes = manifest.getMainAttributes();

                version = attributes.getValue( "Liferay-Portal-Version" );
                serverInfo = attributes.getValue( "Liferay-Portal-Server-Info" );

                if(CoreUtil.compareVersions( Version.parseVersion( version ), new Version( 6, 2, 0 ) ) < 0) {
                    version = null;
                    serverInfo = null;
                }
            }
            catch (IOException e) {
                LiferayTomcatPlugin.logError(e);
            }
        }

        if(configType.equals( "version" )) {
            return version;
        }

        if(configType.equals( "server" )) {
            return serverInfo;
        }

        return null;
    }

	public static boolean isExtProjectContext(Context context) {

		return false;
	}

	public static boolean isLiferayModule(IModule module) {
		boolean retval = false;

		if (module != null) {
			IProject project = module.getProject();

			retval = ProjectUtil.isLiferayProject(project);
		}

		return retval;
	}

	public static Context loadContextFile(File contextFile) {
		FileInputStream fis = null;
		Context context = null;
		if (contextFile != null && contextFile.exists()) {
			try {
				Factory factory = new Factory();
				factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
				fis = new FileInputStream(contextFile);
				context = (Context)factory.loadDocument(fis);
				if (context != null) {
					String path = context.getPath();
					// If path attribute is not set, derive from file name
					if (path == null) {
						String fileName = contextFile.getName();
						path = fileName.substring(0, fileName.length() - ".xml".length());
						if ("ROOT".equals(path))
							path = "";
						context.setPath("/" + path);
					}
				}
			} catch (Exception e) {
				// may be a spurious xml file in the host dir?

			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return context;
	}

	public static void loadHookPropertiesFile(
		IPath runtimeLocation, IPath portalDir, File hookPropertiesFile, File errorFile)
		throws IOException {

		String portalSupportClass = "com.liferay.ide.eclipse.server.core.support.GetSupportedHookProperties";

		IPath[] libRoots = new IPath[] { runtimeLocation.append("lib"), runtimeLocation.append("lib/ext") };

		URL[] urls = new URL[] {
				FileLocator.toFileURL(LiferayServerCorePlugin.getDefault().getBundle().getEntry(
					"portal-support/portal-support.jar"))
		};

		PortalSupportHelper helper =
			new PortalSupportHelper(
				libRoots, portalDir, portalSupportClass, hookPropertiesFile, errorFile, urls, new String[] {});

		try {
			helper.launch(null);
		}
		catch (CoreException e) {
			LiferayTomcatPlugin.logError(e);
		}
	}

	public static void loadVersionInfoFile(IPath runtimeLocation, IPath portalDir, File versionInfoFile, File errorFile)
		throws IOException {

		String portalSupportClass = "com.liferay.ide.eclipse.server.core.support.ReleaseInfoGetVersion";

		IPath[] libRoots = new IPath[] { runtimeLocation.append("lib"), runtimeLocation.append("lib/ext") };

		URL[] urls = new URL[] {
				FileLocator.toFileURL(LiferayServerCorePlugin.getDefault().getBundle().getEntry(
					"portal-support/portal-support.jar"))
		};

		PortalSupportHelper helper =
			new PortalSupportHelper(
				libRoots, portalDir, portalSupportClass, versionInfoFile, errorFile, urls, new String[] {});

		try {
			helper.launch(null);
		}
		catch (CoreException e) {
			LiferayTomcatPlugin.logError(e);
		}
	}

	public static IPath modifyLocationForBundle(IPath currentLocation) {
		IPath modifiedLocation = null;

		if (currentLocation == null || CoreUtil.isNullOrEmpty(currentLocation.toOSString())) {
			return null;
		}

		File location = currentLocation.toFile();

		if (location.exists() && location.isDirectory()) {
			// check to see if this location contains tomcat dir *tomcat*
			File[] files = location.listFiles();

			boolean matches = false;

			String pattern = ".*tomcat.*";

			File tomcatDir = null;

			for ( File file : files )
			{
				if ( file.isDirectory() && file.getName().matches( pattern ) )
				{
					matches = true;

					tomcatDir = file;

					break;
				}
			}

			if (matches && tomcatDir != null) {
				modifiedLocation = new Path(tomcatDir.getPath());
			}
		}

		return modifiedLocation;
	}

	public static void syncStopServer(final IServer server) {
		if (server.getServerState() != IServer.STATE_STARTED) {
			return;
		}

		final LiferayTomcatServerBehavior serverBehavior =
			(LiferayTomcatServerBehavior) server.loadAdapter(LiferayTomcatServerBehavior.class, null);

		Thread shutdownThread = new Thread() {

			@Override
			public void run() {
				serverBehavior.stop(true);

				synchronized (server) {
					try {
						server.wait(5000);
					}
					catch (InterruptedException e) {
					}
				}
			}

		};

		IServerListener shutdownListener = new IServerListener() {

			public void serverChanged(ServerEvent event) {
				if (event.getState() == IServer.STATE_STOPPED) {
					synchronized (server) {
						server.notifyAll();
					}
				}
			}
		};

		server.addServerListener(shutdownListener);

		try {
			shutdownThread.start();
			shutdownThread.join();
		}
		catch (InterruptedException e) {
		}

		server.removeServerListener(shutdownListener);
	}

	public static IStatus validateRuntimeStubLocation( String runtimeTypeId, IPath runtimeStubLocation )
	{
		try {
			IRuntimeWorkingCopy runtimeStub = ServerCore.findRuntimeType( runtimeTypeId ).createRuntime( null, null );
			runtimeStub.setLocation(runtimeStubLocation);
			runtimeStub.setStub(true);
			return runtimeStub.validate(null);
		}
		catch (Exception e) {
			return LiferayTomcatPlugin.createErrorStatus(e);
		}

	}

	public static IStatus canAddModule( IModule module, IServer currentServer ) {
		IProject project = module.getProject();

		if ( project != null ) {
			IFacetedProject facetedProject = ProjectUtil.getFacetedProject( project );

			if ( facetedProject != null ) {
				IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet( facetedProject );

				if ( liferayFacet != null ) {
					String facetId = liferayFacet.getId();

					IRuntime runtime = null;

					try {
						runtime = ServerUtil.getRuntime( project );
					}
					catch ( CoreException e ) {
					}

					if ( runtime != null ) {
						IPluginPublisher pluginPublisher =
							LiferayServerCorePlugin.getPluginPublisher( facetId, runtime.getRuntimeType().getId() );

						if ( pluginPublisher != null ) {
							IStatus status = pluginPublisher.canPublishModule( currentServer, module );

							if ( !status.isOK() ) {
								return status;
							}
						}
					}
				}
			}
		}

		return Status.OK_STATUS;
	}

	public static void addRuntimeVMArgments(
		List<String> runtimeVMArgs, IPath installPath, IPath configPath, IPath deployPath, boolean isTestEnv,
		IServer currentServer, ILiferayTomcatServer liferayTomcatServer ) {
		addUserVMArgs( runtimeVMArgs, currentServer, liferayTomcatServer );

		runtimeVMArgs.add( "-Dfile.encoding=UTF8" );
		runtimeVMArgs.add( "-Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false" );
		runtimeVMArgs.add( "-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager" );

		ILiferayRuntime liferayRuntime =
			(ILiferayRuntime) currentServer.getRuntime().loadAdapter( ILiferayRuntime.class, null );

		Version portalVersion = new Version( liferayRuntime.getPortalVersion() );

		if( CoreUtil.compareVersions( portalVersion, LiferayTomcatRuntime70.leastSupportedVersion ) < 0 )
		{
			runtimeVMArgs.add( "-Djava.security.auth.login.config=\"" + configPath.toOSString() + "/conf/jaas.config\"" );
		}
		else
		{
			runtimeVMArgs.add( "-Djava.net.preferIPv4Stack=true" );
		}

		runtimeVMArgs.add( "-Djava.util.logging.config.file=\"" + installPath.toOSString() +
			"/conf/logging.properties\"" );
		runtimeVMArgs.add( "-Djava.io.tmpdir=\"" + installPath.toOSString() + "/temp\"" );

		File externalPropertiesFile =
			getExternalPropertiesFile( installPath, configPath, currentServer, liferayTomcatServer );

		runtimeVMArgs.add( "-Dexternal-properties=\"" + externalPropertiesFile.getAbsolutePath() + "\"" );
	}

	private static void addUserVMArgs(
		List<String> runtimeVMArgs, IServer currentServer, ILiferayTomcatServer portalTomcatServer ) {
		String[] memoryArgs = ILiferayTomcatConstants.DEFAULT_MEMORY_ARGS.split( " " );
		String userTimezone = ILiferayTomcatConstants.DEFAULT_USER_TIMEZONE;

		if ( currentServer != null && portalTomcatServer != null ) {
			memoryArgs = DebugPlugin.parseArguments( portalTomcatServer.getMemoryArgs() );

			userTimezone = portalTomcatServer.getUserTimezone();
		}

		if ( memoryArgs != null ) {
			for ( String arg : memoryArgs ) {
				runtimeVMArgs.add( arg );
			}
		}

		runtimeVMArgs.add( "-Duser.timezone=" + userTimezone );
	}

	private static File getExternalPropertiesFile(
		IPath installPath, IPath configPath, IServer currentServer, ILiferayTomcatServer portalServer ) {
		File retval = null;

		if ( portalServer != null ) {
			File portalIdePropFile =
				ensurePortalIDEPropertiesExists( installPath, configPath, currentServer, portalServer );

			retval = portalIdePropFile;

			String externalProperties = portalServer.getExternalProperties();

			if ( !CoreUtil.isNullOrEmpty( externalProperties ) ) {
				File externalPropertiesFile = setupExternalPropertiesFile( portalIdePropFile, externalProperties );

				if ( externalPropertiesFile != null ) {
					retval = externalPropertiesFile;
				}
			}
		}

		return retval;
	}

	private static File setupExternalPropertiesFile( File portalIdePropFile, String externalPropertiesPath ) {
		File retval = null;
		// first check to see if there is an external properties file
		File externalPropertiesFile = new File( externalPropertiesPath );

		if ( externalPropertiesFile.exists() ) {
			ExternalPropertiesConfiguration props = new ExternalPropertiesConfiguration();
			try {
				props.load( new FileInputStream( externalPropertiesFile ) );

				props.setProperty( "include-and-override", portalIdePropFile.getAbsolutePath() );

				props.setHeader( "# Last modified by Liferay IDE " + new Date() );

				props.save( new FileOutputStream( externalPropertiesFile ) );

				retval = externalPropertiesFile;
			}
			catch ( Exception e ) {
				retval = null;
			}
		}
		else {
			retval = null; // don't setup an external properties file
		}

		return retval;
	}

	private static File ensurePortalIDEPropertiesExists(
		IPath installPath, IPath configPath, IServer currentServer, ILiferayTomcatServer portalServer ) {

		IPath idePropertiesPath = installPath.append("../portal-ide.properties");

		String hostName = "localhost";

		try {
			ServerInstance server =
				TomcatVersionHelper.getCatalinaServerInstance( configPath.append( "conf/server.xml" ), null, null );

			hostName = server.getHost().getName();
		}
		catch (Exception e) {
			LiferayTomcatPlugin.logError(e);
		}

		// read portal-developer.properties
		// Properties devProps = new Properties();
		// IPath devPropertiesPath =
		// installPath.append("webapps/ROOT/WEB-INF/classes/portal-developer.properties");
		// if (devPropertiesPath.toFile().exists()) {
		// devProps.load(new FileReader(devPropertiesPath.toFile()));
		// }

		// if (idePropertiesPath.toFile().exists()) {
		// String value =
		// CoreUtil.readPropertyFileValue(idePropertiesPath.toFile(),
		// "auto.deploy.tomcat.conf.dir");
		// if (configPath.append("conf/Catalina/"+hostName).toFile().equals(new
		// File(value))) {
		// return;
		// }
		// }

		Properties props = new Properties();

		props.put( "include-and-override", "portal-developer.properties" );
        
		props.put( "com.liferay.portal.servlet.filters.etag.ETagFilter", "false" );
		props.put( "com.liferay.portal.servlet.filters.header.HeaderFilter", "false" );
		props.put( "json.service.auth.token.enabled", "false" );


		props.put("auto.deploy.tomcat.conf.dir", configPath.append("conf/Catalina/" + hostName).toOSString());

		if ( currentServer != null && portalServer != null ) {
			IPath runtimLocation = currentServer.getRuntime().getLocation();

			String autoDeployDir = portalServer.getAutoDeployDirectory();

			if ( !ILiferayTomcatConstants.DEFAULT_AUTO_DEPLOYDIR.equals( autoDeployDir ) ) {
				IPath autoDeployDirPath = new Path( autoDeployDir );

				if ( autoDeployDirPath.isAbsolute() && autoDeployDirPath.toFile().exists() ) {
					props.put( "auto.deploy.deploy.dir", portalServer.getAutoDeployDirectory() );
				}
				else {
					File autoDeployDirFile = new File( runtimLocation.toFile(), autoDeployDir );

					if ( autoDeployDirFile.exists() ) {
						props.put( "auto.deploy.deploy.dir", autoDeployDirFile.getPath() );
					}
				}
			}

			props.put( "auto.deploy.interval", portalServer.getAutoDeployInterval() );
		}

//		props.put( "json.service.public.methods", "*" );
		props.put( "jsonws.web.service.public.methods", "*" );

		File file = idePropertiesPath.toFile();

		try {
			props.store(new FileOutputStream(file), null);
		}
		catch (Exception e) {
			LiferayTomcatPlugin.logError(e);
		}

		return file;
	}

	public static String[] getServletFilterNames( IPath portalDir ) throws Exception
	{
		List<String> retval = new ArrayList<String>();

		File filtersWebXmlFile = portalDir.append( "WEB-INF/liferay-web.xml" ).toFile();

		if ( !filtersWebXmlFile.exists() )
		{
			filtersWebXmlFile = portalDir.append( "WEB-INF/web.xml" ).toFile();
		}

		if ( filtersWebXmlFile.exists() )
		{
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( filtersWebXmlFile );

			NodeList filterNameElements = document.getElementsByTagName( "filter-name" );

			for ( int i = 0; i < filterNameElements.getLength(); i++ )
			{
				Node filterNameElement = filterNameElements.item( i );

				String content = filterNameElement.getTextContent();

				if ( !CoreUtil.isNullOrEmpty( content ) )
				{
					retval.add( content.trim() );
				}
			}
		}

		return retval.toArray( new String[0] );
	}
}
