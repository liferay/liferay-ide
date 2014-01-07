/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/

package com.liferay.ide.server.tomcat.core;

import static com.liferay.ide.core.util.CoreUtil.empty;
import static com.liferay.ide.server.tomcat.core.LiferayTomcatPlugin.warning;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.util.LiferayPortalValueLoader;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.tomcat.core.util.LiferayTomcatUtil;
import com.liferay.ide.server.util.JavaUtil;
import com.liferay.ide.server.util.ReleaseHelper;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMStandin;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatVersionHandler;
import org.eclipse.jst.server.tomcat.core.internal.TomcatRuntime;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class LiferayTomcatRuntime extends TomcatRuntime implements ILiferayTomcatRuntime
{

    public static final String PROP_BUNDLE_ZIP_LOCATION = "bundle-zip-location"; //$NON-NLS-1$

    public static final String PROP_JAVADOC_URL = "javadoc-url"; //$NON-NLS-1$

    public static final String PROP_SOURCE_LOCATION = "source-location"; //$NON-NLS-1$

    protected HashMap<IPath, ReleaseHelper> releaseHelpers;

    private IStatus runtimeDelegateStatus;

    public LiferayTomcatRuntime()
    {
        releaseHelpers = new HashMap<IPath, ReleaseHelper>();
    }

    private IPath findBundledJREPath( IPath location )
    {
        if( Platform.getOS().equals( Platform.OS_WIN32 ) && location != null && location.toFile().exists() )
        {
            // look for jre dir
            File tomcat = location.toFile();
            String[] jre = tomcat.list( new FilenameFilter()
            {

                public boolean accept( File dir, String name )
                {
                    return name.startsWith( "jre" ); //$NON-NLS-1$
                }
            } );
            for( String dir : jre )
            {
                File javaw = new File( location.toFile(), dir + "/win/bin/javaw.exe" ); //$NON-NLS-1$
                if( javaw.exists() )
                {
                    return new Path( javaw.getPath() ).removeLastSegments( 2 );
                }
            }
        }
        return null;
    }

    public IVMInstall findPortalBundledJRE( boolean addVM )
    {
        IPath jrePath = findBundledJREPath( getRuntime().getLocation() );
        if( jrePath == null )
            return null;

        // make sure we don't have an existing JRE that has the same path
        for( IVMInstallType vmInstallType : JavaRuntime.getVMInstallTypes() )
        {
            for( IVMInstall vmInstall : vmInstallType.getVMInstalls() )
            {
                if( vmInstall.getInstallLocation().equals( jrePath.toFile() ) )
                {
                    return vmInstall;
                }
            }
        }

        if( addVM )
        {
            IVMInstallType installType = JavaRuntime.getVMInstallType( StandardVMType.ID_STANDARD_VM_TYPE );
            VMStandin newVM = new VMStandin( installType, JavaUtil.createUniqueId( installType ) );
            newVM.setInstallLocation( jrePath.toFile() );
            if( !CoreUtil.isNullOrEmpty( getRuntime().getName() ) )
            {
                newVM.setName( getRuntime().getName() + " JRE" ); //$NON-NLS-1$
            }
            else
            {
                newVM.setName( "Liferay JRE" ); //$NON-NLS-1$
            }

            // make sure the new VM name isn't the same as existing name
            boolean existingVMWithSameName = ServerUtil.isExistingVMName( newVM.getName() );

            int num = 1;
            while( existingVMWithSameName )
            {
                newVM.setName( getRuntime().getName() + " JRE (" + ( num++ ) + ")" ); //$NON-NLS-1$ //$NON-NLS-2$
                existingVMWithSameName = ServerUtil.isExistingVMName( newVM.getName() );
            }

            return newVM.convertToRealVM();
        }

        return null;
    }

    public IPath getAppServerDeployDir()
    {
        return getAppServerDir().append( "webapps" ); //$NON-NLS-1$
    }

    public IPath getAppServerDir()
    {
        return getRuntime().getLocation();
    }

    public IPath getAppServerLibGlobalDir()
    {
        return getAppServerDir().append( "lib/ext" ); //$NON-NLS-1$
    }

    public IPath getAppServerPortalDir()
    {
        return LiferayTomcatUtil.getPortalDir( getAppServerDir() );
    }

    public String getAppServerType()
    {
        return "tomcat"; //$NON-NLS-1$
    }

    public IPath getBundleZipLocation()
    {
        String zipLocation = getAttribute( PROP_BUNDLE_ZIP_LOCATION, (String) null );

        return zipLocation != null ? new Path( zipLocation ) : null;
    }

    public IPath getDeployDir()
    {
        return getAppServerDir().append( "/webapps" ); //$NON-NLS-1$
    }

    protected String getExpectedServerInfo()
    {
        return Msgs.liferayPortal;
    }

    public String[] getHookSupportedProperties()
    {
        LiferayPortalValueLoader loader = new LiferayPortalValueLoader( getAppServerPortalDir(),getRuntimeLocation() );
        return loader.loadHookPropertiesFromClass();
    }

    public String getJavadocURL()
    {
        return getAttribute( PROP_JAVADOC_URL, (String) null );
    }

    protected Version getLeastSupportedVersion()
    {
        return ILiferayConstants.LEAST_SUPPORTED_VERSION;
    }

    public IPath getLibGlobalDir()
    {
        return getAppServerDir().append( "/lib/ext" ); //$NON-NLS-1$
    }

    public String getPortalVersion()
    {
        // check for existing release info
        return LiferayTomcatUtil.getVersion( getRuntimeLocation(), getAppServerPortalDir() );
    }

    public Properties getPortletCategories()
    {
        return ServerUtil.getPortletCategories( getAppServerPortalDir() );
    }

    public Properties getPortletEntryCategories()
    {
        return ServerUtil.getEntryCategories( getAppServerPortalDir(), getPortalVersion() );
    }

    protected ReleaseHelper getReleaseHelper( IPath serviceJar )
    {
        if( releaseHelpers == null )
        {
            releaseHelpers = new HashMap<IPath, ReleaseHelper>();
        }

        ReleaseHelper cachedHelper = releaseHelpers.get( serviceJar );

        if( cachedHelper != null )
        {
            return cachedHelper;
        }

        ReleaseHelper newHelper = new ReleaseHelper( serviceJar );

        releaseHelpers.put( serviceJar, newHelper );

        return newHelper;
    }

    public IPath getRuntimeLocation()
    {
        return getRuntime().getLocation();
    }

    public String getServerInfo()
    {
        String serverInfo = null;

        try
        {
            serverInfo =
                LiferayTomcatUtil.getConfigInfoFromCache( LiferayTomcatUtil.CONFIG_TYPE_SERVER, getAppServerPortalDir() );

            if( serverInfo == null )
            {
                serverInfo =
                    LiferayTomcatUtil.getConfigInfoFromManifest(
                        LiferayTomcatUtil.CONFIG_TYPE_SERVER, getAppServerPortalDir() );

                if( serverInfo == null )
                {
                    LiferayPortalValueLoader loader = new LiferayPortalValueLoader( getAppServerPortalDir(),getRuntimeLocation() );
                    serverInfo = loader.loadServerInfoFromClass();
                }

                if( serverInfo != null )
                {
                    LiferayTomcatUtil.saveConfigInfoIntoCache(
                        LiferayTomcatUtil.CONFIG_TYPE_SERVER, serverInfo, getAppServerPortalDir() );
                }
            }
        }
        catch( Exception e )
        {
            LiferayTomcatPlugin.logError( e );
        }

        return serverInfo;
    }

    public String[] getServletFilterNames()
    {
        try
        {
            return ServerUtil.getServletFilterNames( getAppServerPortalDir() );
        }
        catch( Exception e )
        {
            return new String[0];
        }
    }

    public IPath getSourceLocation()
    {
        String location = getAttribute( PROP_SOURCE_LOCATION, (String) null );

        return location != null ? new Path( location ) : null;
    }

    public IPath[] getUserLibs()
    {
        return LiferayTomcatUtil.getAllUserClasspathLibraries( getRuntimeLocation(), getAppServerPortalDir() );

    }

    @Override
    public ITomcatVersionHandler getVersionHandler()
    {
        String id = getRuntime().getRuntimeType().getId();
        if( id.indexOf( "runtime.60" ) > 0 ) //$NON-NLS-1$
        {
            return new LiferayTomcat60Handler();
        }
        else if( id.indexOf( "runtime.70" ) > 0 ) //$NON-NLS-1$
        {
            return new LiferayTomcat70Handler();
        }

        return null;
    }

    public IVMInstall getVMInstall()
    {
        if( getVMInstallTypeId() == null )
        {
            IVMInstall vmInstall = findPortalBundledJRE( false );
            if( vmInstall != null )
            {
                setVMInstall( vmInstall );
                return vmInstall;
            }
            else
            {
                return JavaRuntime.getDefaultVMInstall();
            }
        }
        try
        {
            IVMInstallType vmInstallType = JavaRuntime.getVMInstallType( getVMInstallTypeId() );
            IVMInstall[] vmInstalls = vmInstallType.getVMInstalls();
            int size = vmInstalls.length;
            String id = getVMInstallId();
            for( int i = 0; i < size; i++ )
            {
                if( id.equals( vmInstalls[i].getId() ) )
                    return vmInstalls[i];
            }
        }
        catch( Exception e )
        {
            // ignore
        }
        return null;
    }

    public void setBundleZipLocation( IPath path )
    {
        if( path != null )
        {
            setAttribute( PROP_BUNDLE_ZIP_LOCATION, path.toPortableString() );
        }
    }

    public void setJavadocURL( String url )
    {
        if( url != null )
        {
            setAttribute( PROP_JAVADOC_URL, url );
        }
    }

    public void setSourceLocation( IPath location )
    {
        if( location != null )
        {
            setAttribute( PROP_SOURCE_LOCATION, location.toPortableString() );
        }
    }

    @Override
    public IStatus validate()
    {
        // first validate that this runtime is
        if( runtimeDelegateStatus == null )
        {
            runtimeDelegateStatus = LiferayServerCore.validateRuntimeDelegate( this );
        }

        if( !runtimeDelegateStatus.isOK() )
        {
            return runtimeDelegateStatus;
        }

        IStatus status = super.validate();

        if( !status.isOK() )
        {
            return status;
        }

        String version = getPortalVersion();

        Version portalVersion = Version.parseVersion( version );

        if( portalVersion != null && ( CoreUtil.compareVersions( portalVersion, getLeastSupportedVersion() ) < 0 ) )
        {
            status =
                LiferayTomcatPlugin.createErrorStatus( NLS.bind(
                    Msgs.portalVersionNotSupported, getLeastSupportedVersion() ) );
        }

        if( !getRuntime().isStub() )
        {
            String serverInfo = getServerInfo();

            if( CoreUtil.isNullOrEmpty( serverInfo ) || serverInfo.indexOf( getExpectedServerInfo() ) < 0 )
            {
                status =
                    LiferayTomcatPlugin.createErrorStatus( NLS.bind(
                        Msgs.portalServerNotSupported, getExpectedServerInfo() ) );
            }
        }

        // need to check if runtime is specifying a zip or location for javadoc, is so validate it
        String javadocUrlValue = getJavadocURL();

        if( !empty( javadocUrlValue ) )
        {
            IStatus javadocUrlStatus = validateJavadocUrlValue( javadocUrlValue );

            if( !javadocUrlStatus.isOK() )
            {
                return javadocUrlStatus;
            }
        }

        // need to check if this runtime is specifying a zip file for a bundle package, if so validate it
        IPath bundleZip = getBundleZipLocation();

        if( bundleZip != null && ( !CoreUtil.isNullOrEmpty( bundleZip.toString() ) ) )
        {
            String rootEntryName = null;

            try
            {
                ZipInputStream zis = new ZipInputStream( new FileInputStream( bundleZip.toFile() ) );

                ZipEntry rootEntry = zis.getNextEntry();
                rootEntryName = new Path( rootEntry.getName() ).segment( 0 );

                if( rootEntryName.endsWith( StringPool.FORWARD_SLASH ) )
                {
                    rootEntryName = rootEntryName.substring( 0, rootEntryName.length() - 1 );
                }

                boolean foundTomcat = false;

                ZipEntry entry = zis.getNextEntry();

                while( entry != null && !foundTomcat )
                {
                    String entryName = entry.getName();

                    if( entryName.startsWith( rootEntryName + "/tomcat-" ) ) //$NON-NLS-1$
                    {
                        foundTomcat = true;
                    }

                    entry = zis.getNextEntry();
                };
            }
            catch( Exception e )
            {
                return LiferayTomcatPlugin.createWarningStatus( Msgs.bundleZipLocationNotValid );
            }

            // if we get here then the user has specified a good zip installation so now we need to see if the
            // installation of the runtime will work for EXT plugins.
            IPath location = getRuntime().getLocation();
            String bundleDir = location.removeLastSegments( 1 ).lastSegment();

            if( !bundleDir.equals( rootEntryName ) )
            {
                return LiferayTomcatPlugin.createWarningStatus( NLS.bind(
                    Msgs.runtimeLocationDirectoryNotMatch, bundleDir, rootEntryName ) );
            }
        }

        return status;
    }

    private IStatus validateJavadocUrlValue( String javadocUrlValue )
    {
        if( javadocUrlValue.startsWith( "http" ) || javadocUrlValue.startsWith( "jar:file:" ) || //$NON-NLS-1$ //$NON-NLS-2$
            javadocUrlValue.startsWith( "file:" ) ) //$NON-NLS-1$
        {
            return Status.OK_STATUS;
        }

        return warning( Msgs.javadocURLStart );
    }

    private static class Msgs extends NLS
    {
        public static String bundleZipLocationNotValid;
        public static String javadocURLStart;
        public static String liferayPortal;
        public static String portalServerNotSupported;
        public static String portalVersionNotSupported;
        public static String runtimeLocationDirectoryNotMatch;

        static
        {
            initializeMessages( LiferayTomcatRuntime.class.getName(), Msgs.class );
        }
    }
}
