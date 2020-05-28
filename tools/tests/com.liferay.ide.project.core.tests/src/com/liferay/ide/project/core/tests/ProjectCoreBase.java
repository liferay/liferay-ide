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
package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.core.util.JobUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProfileLocation;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOpMethods;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKCorePlugin;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.tests.ServerCoreBase;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMStandin;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.junit.Before;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 * @author Li Lu
 */
@SuppressWarnings( "restriction" )
public class ProjectCoreBase extends ServerCoreBase
{

    private static final String BUNDLE_ID = "com.liferay.ide.project.core.tests";

    public static void deleteAllWorkspaceProjects() throws Exception
    {
        for( IProject project : CoreUtil.getAllProjects() )
        {
            if ( project != null && project.isAccessible() && project.exists())
            {
                final NullProgressMonitor monitor = new NullProgressMonitor();

                try
                {
                    project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                }
                catch( Exception e)
                {
                    //ignore
                }

                project.close( monitor );
                project.delete( true, monitor );

                assertFalse( project.exists() );
            }
        }
    }

    public static void createLiferayWorkspaceProject() throws Exception {
        NewLiferayWorkspaceOp workspaceOp = NewLiferayWorkspaceOp.TYPE.instantiate();

        workspaceOp.setWorkspaceName( "test-liferay-workspace" );
        workspaceOp.setUseDefaultLocation( true );

        if( workspaceOp.validation().ok() )
        {
            NewLiferayWorkspaceOpMethods.execute( workspaceOp, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        }

        waitForBuildAndValidation();

        IProject workspaceProject = CoreUtil.getProject( "test-liferay-workspace" );

        assertTrue(workspaceProject != null);
        assertTrue(workspaceProject.exists());
    }

    protected static void waitForBuildAndValidation() throws Exception
    {
        IWorkspaceRoot root = null;

        try
        {
            ResourcesPlugin.getWorkspace().checkpoint(true);
            Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
            Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor());
            Job.getJobManager().join(ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor());
            Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
            Job.getJobManager().join("org.eclipse.buildship.core.jobs", new NullProgressMonitor());
            JobUtil.waitForLiferayProjectJob();
            Thread.sleep(200);
            Job.getJobManager().beginRule(root = ResourcesPlugin.getWorkspace().getRoot(), null);
        }
        catch (InterruptedException e)
        {
            failTest( e );
        }
        catch (IllegalArgumentException e)
        {
            failTest( e );
        }
        catch (OperationCanceledException e)
        {
            failTest( e );
        }
        finally
        {
            if (root != null) {
                Job.getJobManager().endRule(root);
            }
        }
    }

    protected void waitForBuildAndValidation(IProject project) throws Exception
    {
        project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
        waitForBuildAndValidation();
    }

    protected IProject createAntProject( NewLiferayPluginProjectOp op ) throws Exception
    {
        op.setProjectProvider( "ant" );

        final IProject project = createProject( op );

        /*
        assertEquals(
            "SDK project layout is not standard, /src folder exists.", false, project.getFolder( "src" ).exists() );

        switch( op.getPluginType().content() )
        {
            case ext:
                break;
            case hook:
            case portlet:
            case web:

                assertEquals(
                    "java source folder docroot/WEB-INF/src doesn't exist.", true,
                    project.getFolder( "docroot/WEB-INF/src" ).exists() );

                break;
            case layouttpl:
                break;
            case theme:
                break;
            default:
                break;
        }
        */

        project.refreshLocal( IResource.DEPTH_INFINITE, null );

        return project;
    }

    public NewLiferayPluginProjectOp setMavenProfile( NewLiferayPluginProjectOp op ) throws Exception
    {
        NewLiferayProfile profile = op.getNewLiferayProfiles().insert();
        profile.setId( "Liferay-v6.2-CE-(Tomcat-7)" );
        profile.setLiferayVersion( "6.2.2" );
        profile.setRuntimeName( getRuntimeVersion() );
        profile.setProfileLocation( ProfileLocation.projectPom );

        op.setActiveProfilesValue( "Liferay-v6.2-CE-(Tomcat-7)" );

        return op;
    }

    protected IProject createMavenProject( NewLiferayPluginProjectOp op ) throws Exception
    {
        op.setProjectProvider( "maven" );

        op = setMavenProfile( op );

        IProject project = createProject( op );

        switch( op.getPluginType().content() )
        {
            case ext:
            case hook:
            case portlet:
            case web:
                assertEquals( "java source folder src/main/webapp doesn't exist.", true, project.getFolder( "src/main/webapp" ).exists() );
            case layouttpl:
            case theme:
            case servicebuilder:
            default:
                break;
            }

            Thread.sleep( 3000 );

            return project;
    }

    protected IRuntime createNewRuntime( final String name ) throws Exception
    {
        final IPath newRuntimeLocation = new Path( getLiferayRuntimeDir().toString() + "-new" );

        if( ! newRuntimeLocation.toFile().exists() )
        {
            FileUtils.copyDirectory( getLiferayRuntimeDir().toFile(), newRuntimeLocation.toFile() );
        }

        assertEquals( true, newRuntimeLocation.toFile().exists() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        IRuntime runtime = ServerCore.findRuntime( name );

        if( runtime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( name, npm );

            runtimeWC.setName( name );
            runtimeWC.setLocation( newRuntimeLocation );

            runtime = runtimeWC.save( true, npm );
        }

        ServerCore.getRuntimes();
        assertNotNull( runtime );

        return runtime;
    }

    protected SDK createNewSDK() throws Exception
    {
        final IPath newSDKLocation = new Path( getLiferayPluginsSdkDir().toString() + "-new" );

        if( ! newSDKLocation.toFile().exists() )
        {
            FileUtils.copyDirectory( getLiferayPluginsSdkDir().toFile(), newSDKLocation.toFile() );
        }

        assertEquals( true, newSDKLocation.toFile().exists() );

        SDK newSDK = SDKUtil.createSDKFromLocation( newSDKLocation );

        if( newSDK == null )
        {
            FileUtils.copyDirectory( getLiferayPluginsSdkDir().toFile(), newSDKLocation.toFile() );
            newSDK = SDKUtil.createSDKFromLocation( newSDKLocation );
        }

        SDKManager.getInstance().addSDK( newSDK );

        return newSDK;
    }

    public IProject createProject( NewLiferayPluginProjectOp op )
    {
        return createProject( op, null );
    }

    public IProject createProject( NewLiferayPluginProjectOp op, String projectName )
    {
        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );

        assertEquals(
            status.toString(), Status.createOkStatus().message().toLowerCase(), status.message().toLowerCase() );

        if( projectName == null || op.getProjectProvider().content().getShortName().equalsIgnoreCase( "ant" ) )
        {
            projectName = op.getFinalProjectName().content();
        }

//        if( op.getProjectProvider().content().getShortName().equalsIgnoreCase( "maven" ) )
//        {
//            if( op.getPluginType().content().equals( PluginType.ext ) )
//            {
//                projectName = projectName + "-ext";
//            }
//            else if( op.getPluginType().content().equals( PluginType.servicebuilder ) )
//            {
//                projectName = projectName + "-portlet";
//            }
//        }

        final IProject newLiferayPluginProject = project( projectName );

        assertNotNull( newLiferayPluginProject );

        assertEquals( true, newLiferayPluginProject.exists() );

        final IFacetedProject facetedProject = ProjectUtil.getFacetedProject( newLiferayPluginProject );

        assertNotNull( facetedProject );

        final IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet( facetedProject );

        assertNotNull( liferayFacet );

        final PluginType pluginTypeValue = op.getPluginType().content( true );

        if( pluginTypeValue.equals( PluginType.servicebuilder ) )
        {
            assertEquals( "liferay.portlet", liferayFacet.getId() );
        }
        else
        {
            assertEquals( "liferay." + pluginTypeValue, liferayFacet.getId() );
        }

        return newLiferayPluginProject;
    }

    protected String getBundleId()
    {
        return BUNDLE_ID;
    }

    protected IPath getCustomLocationBase()
    {
        final IPath customLocationBase =
            org.eclipse.core.internal.utils.FileUtil.canonicalPath( new Path( System.getProperty( "java.io.tmpdir" ) ) ).append(
                "custom-project-location-tests" );

        return customLocationBase;
    }

    protected IPath getIvyCacheZip()
    {
        return getLiferayBundlesPath().append( "ivy-cache-7.0.zip" );
    }

    protected IPath getLiferayPluginsSdkDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.2" );
    }

    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.2-ce-ga6-20171101150212422.zip" );
    }

    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.2/";
    }

    protected IProject getProject( String path, String projectName ) throws Exception
    {
        IProject project = CoreUtil.getWorkspaceRoot().getProject( projectName );

        if( project != null && project.exists() )
        {
            return project;
        }

        return importProject( path, getBundleId(), projectName );
    }

    protected IProject importProject( String path, String bundleId, String projectName ) throws Exception
    {
        final IPath sdkLocation = SDKManager.getInstance().getDefaultSDK().getLocation();
        final IPath projectFolder = sdkLocation.append( path );

        final File projectZipFile = getProjectZip( bundleId, projectName );

        ZipUtil.unzip( projectZipFile, projectFolder.toFile() );

        final IPath projectPath = projectFolder.append( projectName );
        assertEquals( true, projectPath.toFile().exists() );

        final ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir( projectPath.toOSString() );
        assertNotNull( projectRecord );

        final IRuntime runtime = ServerCore.findRuntime( getRuntimeVersion() );
        assertNotNull( runtime );

        final IProject project = ProjectImportUtil.importProject(
            projectRecord, ServerUtil.getFacetRuntime( runtime ), sdkLocation.toOSString(),new NullProgressMonitor() );

        assertNotNull( project );

        assertEquals( "Expected new project to exist.", true, project.exists() );

        return project;
    }

    protected File getProjectZip( String bundleId, String projectName ) throws IOException
    {
        final URL projectZipUrl =
            Platform.getBundle( bundleId ).getEntry( "projects/" + projectName + ".zip" );

        final File projectZipFile = new File( FileLocator.toFileURL( projectZipUrl ).getFile() );
        return projectZipFile;
    }

    protected NewLiferayPluginProjectOp newProjectOp( final String projectName ) throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName + "-" + getRuntimeVersion() );

        IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();

        if( vmInstall == null )
        {
            throw new CoreException( SDKCorePlugin.createErrorStatus( "Could not get default VM install" ) );
        }

        return op;
    }

    private void ensureDefaultVMInstallExists() throws CoreException
    {
        IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();

        if( vmInstall != null )
        {
            return;
        }

        final IVMInstallType vmInstallType = JavaRuntime.getVMInstallType( StandardVMType.ID_STANDARD_VM_TYPE );
        String id = null;

        do
        {
            id = String.valueOf( System .currentTimeMillis() );
        }
        while( vmInstallType.findVMInstall( id ) != null );

        VMStandin newVm = new VMStandin( vmInstallType, id );
        newVm.setName( "Default-VM" );
        String jrePath = System.getProperty( "java.home" );
        File installLocation = new File( jrePath );
        newVm.setInstallLocation( installLocation );
        IVMInstall realVm = newVm.convertToRealVM();
        JavaRuntime.setDefaultVMInstall( realVm, new NullProgressMonitor() );
    }

    private void persistAppServerProperties() throws FileNotFoundException, IOException, ConfigurationException
    {
        Properties initProps = new Properties();
        initProps.put( "app.server.type", "tomcat" );
        initProps.put( "app.server.tomcat.dir", getLiferayRuntimeDir().toPortableString() );
        initProps.put( "app.server.tomcat.deploy.dir", getLiferayRuntimeDir().append( "webapps" ).toPortableString() );
        initProps.put( "app.server.tomcat.lib.global.dir", getLiferayRuntimeDir().append( "lib/ext" ).toPortableString() );
        initProps.put( "app.server.parent.dir", getLiferayRuntimeDir().removeLastSegments( 1 ).toPortableString() );
        initProps.put( "app.server.tomcat.portal.dir", getLiferayRuntimeDir().append( "webapps/ROOT" ).toPortableString() );

        IPath loc = getLiferayPluginsSdkDir();
        String userName = System.getProperty( "user.name" ); //$NON-NLS-1$
        File userBuildFile = loc.append( "build." + userName + ".properties" ).toFile(); //$NON-NLS-1$ //$NON-NLS-2$

        try(OutputStream fileOutput = Files.newOutputStream( userBuildFile.toPath() ))
        {
            if( userBuildFile.exists() )
            {
                PropertiesConfiguration propsConfig = new PropertiesConfiguration( userBuildFile );
                for( Object key : initProps.keySet() )
                {
                    propsConfig.setProperty( (String) key, initProps.get( key ) );
                }
                propsConfig.setHeader( "" );
                propsConfig.save( fileOutput );

            }
            else
            {
                Properties props = new Properties();
                props.putAll( initProps );
                props.store( fileOutput, StringPool.EMPTY );
            }
        }
    }

    protected void removeAllRuntimes() throws Exception
    {
        for( IRuntime r : ServerCore.getRuntimes() )
        {
            r.delete();
        }
    }

    /**
     * @throws Exception
     */
    @Before
    public void setupPluginsSDK() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final SDK existingSdk = SDKManager.getInstance().getSDK( getLiferayPluginsSdkDir() );

        if( existingSdk == null )
        {
            FileUtil.deleteDir( getLiferayPluginsSdkDir().toFile(), true );
        }

        final File liferayPluginsSdkDirFile = getLiferayPluginsSdkDir().toFile();

        if( ! liferayPluginsSdkDirFile.exists() )
        {
            final File liferayPluginsSdkZipFile = getLiferayPluginsSDKZip().toFile();

            assertEquals(
                "Expected file to exist: " + liferayPluginsSdkZipFile.getAbsolutePath(), true,
                liferayPluginsSdkZipFile.exists() );

            liferayPluginsSdkDirFile.mkdirs();

            final String liferayPluginsSdkZipFolder = getLiferayPluginsSdkZipFolder();

            if( CoreUtil.isNullOrEmpty( liferayPluginsSdkZipFolder ) )
            {
                ZipUtil.unzip( liferayPluginsSdkZipFile, liferayPluginsSdkDirFile );
            }
            else
            {
                ZipUtil.unzip(
                    liferayPluginsSdkZipFile, liferayPluginsSdkZipFolder, liferayPluginsSdkDirFile,
                    new NullProgressMonitor() );
            }
        }

        assertEquals( true, liferayPluginsSdkDirFile.exists() );

        final File ivyCacheDir = new File( liferayPluginsSdkDirFile, ".ivy" );

        if( ! ivyCacheDir.exists() )
        {
         // setup ivy cache

            final File ivyCacheZipFile = getIvyCacheZip().toFile();

            assertEquals( "Expected ivy-cache.zip to be here: " + ivyCacheZipFile.getAbsolutePath(), true, ivyCacheZipFile.exists() );

            ZipUtil.unzip( ivyCacheZipFile, liferayPluginsSdkDirFile );
        }

        assertEquals( "Expected .ivy folder to be here: " + ivyCacheDir.getAbsolutePath(), true, ivyCacheDir.exists() );

        SDK sdk = null;

        if( existingSdk == null )
        {
            sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );
        }
        else
        {
            sdk = existingSdk;
        }

        assertNotNull( sdk );

        sdk.setDefault( true );

        SDKManager.getInstance().setSDKs( new SDK[] { sdk } );

        final IPath customLocationBase = getCustomLocationBase();

        final File customBaseDir = customLocationBase.toFile();

        if( customBaseDir.exists() )
        {
            FileUtil.deleteDir( customBaseDir, true );

            if( customBaseDir.exists() )
            {
                for( File f : customBaseDir.listFiles() )
                {
                    System.out.println(f.getAbsolutePath());
                }
            }

            assertEquals( "Unable to delete pre-existing customBaseDir", false, customBaseDir.exists() );
        }

        SDK workspaceSdk = SDKUtil.getWorkspaceSDK();

        if ( workspaceSdk == null)
        {
            persistAppServerProperties();

            IStatus validationStatus = sdk.validate( true );

            StringBuilder sb = new StringBuilder();

            Stream.of(validationStatus.getChildren()).map(IStatus::getMessage).forEach(sb::append);

            PrintDirectoryTree.print(sdk.getLocation().toFile(), sb);

            assertTrue(sb.toString(), validationStatus.isOK());

            SDKUtil.openAsProject( sdk );
        }

        ensureDefaultVMInstallExists();
    }

    @Override
    public void setupRuntime() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.setupRuntime();
    }

    public void setupPluginsSDKAndRuntime() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        setupPluginsSDK();
        setupRuntime();
    }

}