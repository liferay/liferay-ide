/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NamedItem;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.UpgradeLiferayProjectsOp;
import com.liferay.ide.project.core.model.UpgradeLiferayProjectsOpMethods;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.ProjectUtil.SearchFilesVisitor;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.service.core.operation.ServiceBuilderDescriptorHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Simon Jiang
 */

@SuppressWarnings( { "restriction", "deprecation" } )
public class LiferayProjectUpgradeOpTests extends ProjectCoreBase
{

    private final static String[] fileNames = { "liferay-portlet.xml", "liferay-display.xml", "service.xml",
        "liferay-hook.xml", "liferay-layout-templates.xml", "liferay-look-and-feel.xml", "liferay-portlet-ext.xml",
        "liferay-plugin-package.properties" };


    private final static String publicid_regrex =
                    "-\\//(?:[a-z][a-z]+)\\//(?:[a-z][a-z]+)[\\s+(?:[a-z][a-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[a-z][a-z]+)";

    private final static String systemid_regrex =
        "^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd";


    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.1.1" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.1.1-ce-ga2-20121004092655026.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.1.1/";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.1.1-ce-ga2/tomcat-7.0.27" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.1.1-ce-ga2-20120731132656558.zip" );
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.eclipse.server.tomcat.runtime.70";
    }

    @Override
    protected String getRuntimeVersion()
    {
        return "6.1.1";
    }


    protected IPath getLiferayPluginsSdkDi620()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.2.0" );
    }

    protected IPath getLiferayPluginsSDKZip620()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    protected String getLiferayPluginsSdkZipFolder620()
    {
        return "liferay-plugins-sdk-6.2.0/";
    }

    protected IPath getLiferayRuntimeDir620()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2.0-ce-ga1/tomcat-7.0.42" );
    }

    protected IPath getLiferayRuntimeZip620()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    protected String getRuntimeId620()
    {
        return "com.liferay.ide.server.62.tomcat.runtime.70";
    }

    protected String getRuntimeVersion620()
    {
        return "6.2.0";
    }

    protected IRuntime createNewRuntime620( final String name ) throws Exception
    {
        final IPath newRuntimeLocation = new Path( getLiferayRuntimeDir620().toString() + "-new" );

        if( ! newRuntimeLocation.toFile().exists() )
        {
            FileUtils.copyDirectory( getLiferayRuntimeDir620().toFile(), newRuntimeLocation.toFile() );
        }

        assertEquals( true, newRuntimeLocation.toFile().exists() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        IRuntime runtime = ServerCore.findRuntime( name );

        if( runtime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId620() ).createRuntime( name, npm );

            runtimeWC.setName( name );
            runtimeWC.setLocation( newRuntimeLocation );

            runtime = runtimeWC.save( true, npm );
        }

        ServerCore.getRuntimes();
        assertNotNull( runtime );

        return runtime;
    }


    public void setupRuntime620()  throws Exception
    {
        final File liferayRuntimeDirFile = getLiferayRuntimeDir620().toFile();

        if( ! liferayRuntimeDirFile.exists() )
        {
            final File liferayRuntimeZipFile = getLiferayRuntimeZip620().toFile();

            assertEquals(
                "Expected file to exist: " + liferayRuntimeZipFile.getAbsolutePath(), true,
                liferayRuntimeZipFile.exists() );

            ZipUtil.unzip( liferayRuntimeZipFile, LiferayProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayRuntimeDirFile.exists() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        final String runtimeName = getRuntimeVersion620();

        IRuntime runtime = ServerCore.findRuntime( runtimeName );

        if( runtime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( runtimeName, npm );

            runtimeWC.setName( runtimeName );
            runtimeWC.setLocation( getLiferayRuntimeDir() );

            runtime = runtimeWC.save( true, npm );
        }

        assertNotNull( runtime );

        final ILiferayTomcatRuntime liferayRuntime =
            (ILiferayTomcatRuntime) ServerCore.findRuntime( runtimeName ).loadAdapter( ILiferayTomcatRuntime.class, npm );

        assertNotNull( liferayRuntime );
    }


    public IProject createNewPluginAntProject(PluginType projectType) throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( projectType.name() );
        op.setPluginType( projectType );

        return createAntProject( op );
    }


    public IProject[] createAllPluginTypeAntProject(String prefixProjectName) throws Exception
    {
        List<IProject> projects = new ArrayList<IProject>();

        for (PluginType plugin : PluginType.values())
        {
            if ( !plugin.name().equals( PluginType.web.name() ))
            {
                final NewLiferayPluginProjectOp op = newProjectOp( plugin.name() + "-" + prefixProjectName );
                op.setPluginType( plugin );
                IProject project = createAntProject( op );
                projects.add( project );
            }

        }

        return projects.toArray( new IProject[projects.size()] );
    }

    public IProject createServicePluginTypeAntProject(String prefixProjectName) throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( PluginType.servicebuilder.name() + "-" + prefixProjectName );
        op.setPluginType( PluginType.servicebuilder.name() );
        IProject project = createAntProject( op );

        return project;
    }




    private IFile[] getUpgradeDTDFiles(IProject project)
    {
        List<IFile> files = new ArrayList<IFile>();

        for( String name : fileNames )
        {
            files.addAll( new SearchFilesVisitor().searchFiles( project, name ) );
        }

        return files.toArray( new IFile[files.size()] );
    }

    private String getNewDoctTypeSetting(String doctypeSetting, String regrex)
    {
        Pattern p = Pattern.compile( regrex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
        Matcher m = p.matcher( doctypeSetting );
        if ( m.find() )
        {
            return m.group( m.groupCount() );
        }

        return null;
    }

    private void checkDTDHeader(IProject project, String checkPublicId, String checkSystemId, String checkVersion )
    {
        try
        {
            IFile[] metaFiles = getUpgradeDTDFiles(project);
            for(IFile file : metaFiles)
            {
                IStructuredModel readtModel = StructuredModelManager.getModelManager().getModelForRead( file ) ;
                if( readtModel != null && readtModel instanceof IDOMModel )
                {
                    IDOMDocument xmlDocument = ( (IDOMModel) readtModel ).getDocument();
                    DocumentTypeImpl docType = (DocumentTypeImpl)xmlDocument.getDoctype();

                    String publicId = getNewDoctTypeSetting(docType.getPublicId(), publicid_regrex);
                    assertEquals( checkPublicId , publicId );

                    String systemId = getNewDoctTypeSetting(docType.getSystemId(), systemid_regrex);;
                    assertEquals( checkSystemId , systemId );

                    readtModel.releaseFromRead();

                }
                else
                {
                    checkProperties(file,"liferay-versions", checkVersion);
                }

            }
        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( "Unable to upgrade deployment meta file.", e ); //$NON-NLS-1$
        }
    }

    private void checkProperties( IFile file, String propertyName, String propertiesValue )
    {
        try
        {
            File osfile = new File( file.getLocation().toOSString() );
            PropertiesConfiguration  pluginPackageProperties= new PropertiesConfiguration();
            pluginPackageProperties.load( osfile );
            String value = (String) pluginPackageProperties.getProperty( propertyName );
            assertEquals( propertiesValue , value );
            file.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( e );
        }
    }

     @Test
    public void testPossibleTargetRuntime() throws Exception
    {
        UpgradeLiferayProjectsOp op = UpgradeLiferayProjectsOp.TYPE.instantiate();

        final String originalRuntimeName = getRuntimeVersion();
        final IRuntime oldOriginalRuntime = ServerCore.findRuntime( originalRuntimeName );
        assertNotNull( oldOriginalRuntime );

        setupRuntime620();

        final String newRuntimeName = getRuntimeVersion620();
        final IRuntime newOriginalRuntime = ServerCore.findRuntime( getRuntimeVersion620() );
        assertNotNull( newOriginalRuntime );


        Set<String> exceptedRuntimeNames = new HashSet<String>();
        exceptedRuntimeNames.add( originalRuntimeName );
        exceptedRuntimeNames.add( newRuntimeName );

        final Set<String> acturalRuntimeNames = op.getRuntimeName().service( PossibleValuesService.class ).values();
        assertNotNull( acturalRuntimeNames );

        assertEquals( true, exceptedRuntimeNames.containsAll( acturalRuntimeNames ) );
        assertEquals( true, acturalRuntimeNames.containsAll( exceptedRuntimeNames ) );
    }


    @Test
    public void testMetadaUpgrade() throws Exception
    {
        UpgradeLiferayProjectsOp op = UpgradeLiferayProjectsOp.TYPE.instantiate();

        IProject[] projects = createAllPluginTypeAntProject("dtd");

        for( IProject project : projects)
        {
            checkDTDHeader( project, "6.1.0", "6_1_0", "6.1.1");
        }

        List<String> actionString = new ArrayList<String>();
        List<String> projectString = new ArrayList<String>();

        NamedItem upgradeAction = op.getSelectedActions().insert();
        upgradeAction.setName( "MetadataUpgrade" );
        actionString.add( upgradeAction.getName().content() );

        for( IProject project : projects)
        {
            NamedItem upgradeProjectItem = op.getSelectedProjects().insert();
            upgradeProjectItem.setName( project.getName() );
            projectString.add( upgradeProjectItem.getName().content() );
        }

        UpgradeLiferayProjectsOpMethods.runUpgradeJob( projectString, actionString, op.getRuntimeName().content(), new NullProgressMonitor() );

        for( IProject project : projects)
        {
            checkDTDHeader( project, "6.2.0", "6_2_0", "6.2.0+");
        }
    }

    @Test
    public void testExecUpgradeRuntime() throws Exception
    {
        IProject[] projects = createAllPluginTypeAntProject("runtime");

        IRuntime runtime611 = ServerCore.findRuntime( getRuntimeVersion() );
        for( IProject project : projects)
        {
            assertEquals(runtime611.getName() , ServerUtil.getRuntime( project ).getName());
        }

        setupRuntime620();

        UpgradeLiferayProjectsOp op = UpgradeLiferayProjectsOp.TYPE.instantiate();
        IRuntime runtime620 = ServerCore.findRuntime( getRuntimeVersion620() );
        op.setRuntimeName( runtime620.getName() );

        List<String> actionString = new ArrayList<String>();
        List<String> projectString = new ArrayList<String>();

        NamedItem upgradeRuntimAction = op.getSelectedActions().insert();
        upgradeRuntimAction.setName( "RuntimeUpgrade" );
        actionString.add( upgradeRuntimAction.getName().content() );

        for( IProject project : projects)
        {
            NamedItem upgradeProjectItem = op.getSelectedProjects().insert();
            upgradeProjectItem.setName( project.getName() );
            projectString.add( upgradeProjectItem.getName().content() );
        }

        UpgradeLiferayProjectsOpMethods.runUpgradeJob( projectString, actionString, op.getRuntimeName().content(), new NullProgressMonitor() );

        for( String projectName : projectString)
        {
            IProject project = ProjectUtil.getProject( projectName );
            assertEquals(runtime620.getName() , ServerUtil.getRuntime( project ).getName());
        }

    }


    @Test
    public void testExecServiceBuilder() throws Exception
    {
        UpgradeLiferayProjectsOp op = UpgradeLiferayProjectsOp.TYPE.instantiate();

        IProject project = createServicePluginTypeAntProject( "service-builder");

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

        assertNotNull( webappRoot );

        final IVirtualFile serviceXml = webappRoot.getFile( "WEB-INF/service.xml" );

        assertEquals( true, serviceXml.exists() );

        new ServiceBuilderDescriptorHelper( serviceXml.getUnderlyingFile().getProject() ).addDefaultEntity();

        setupRuntime620();

        IRuntime runtime620 = ServerCore.findRuntime( getRuntimeVersion() );
        op.setRuntimeName( runtime620.getName() );


        List<String> actionString = new ArrayList<String>();
        List<String> projectString = new ArrayList<String>();

        NamedItem upgradeRuntimAction = op.getSelectedActions().insert();
        upgradeRuntimAction.setName( "RuntimeUpgrade" );
        actionString.add( upgradeRuntimAction.getName().content() );
        NamedItem serviceBuilderAction = op.getSelectedActions().insert();
        serviceBuilderAction.setName( "ServicebuilderUpgrade" );
        actionString.add( serviceBuilderAction.getName().content() );


        NamedItem upgradeProjectItem = op.getSelectedProjects().insert();
        upgradeProjectItem.setName( project.getName() );
        projectString.add( upgradeProjectItem.getName().content() );

        UpgradeLiferayProjectsOpMethods.runUpgradeJob( projectString, actionString, op.getRuntimeName().content(), new NullProgressMonitor() );

        IProject upgradeProject = ProjectUtil.getProject( project.getName() );
        assertEquals(runtime620.getName() , ServerUtil.getRuntime( upgradeProject ).getName());

        final IVirtualFile serviceJarXml = webappRoot.getFile( "WEB-INF/lib/" + project.getName() + "-service.jar" );

        assertEquals( true, serviceJarXml.exists() );
    }


    @Test
    public void testExecAlloyUpgradeTool() throws Exception
    {
        UpgradeLiferayProjectsOp op = UpgradeLiferayProjectsOp.TYPE.instantiate();

        IProject project = createServicePluginTypeAntProject( "portlet");

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

        assertNotNull( webappRoot );

        final IVirtualFile mainCss = webappRoot.getFile( "css/main.css" );

        assertEquals( true, mainCss.exists() );

        CoreUtil.writeStreamFromString( ".aui-field-select{}", new FileOutputStream( mainCss.getUnderlyingFile().getLocation().toFile() ) );

        List<String> actionString = new ArrayList<String>();
        List<String> projectString = new ArrayList<String>();

        NamedItem upgradeRuntimAction = op.getSelectedActions().insert();
        upgradeRuntimAction.setName( "AlloyUIExecute" );
        actionString.add( upgradeRuntimAction.getName().content() );

        NamedItem upgradeProjectItem = op.getSelectedProjects().insert();
        upgradeProjectItem.setName( project.getName() );
        projectString.add( upgradeProjectItem.getName().content() );

        UpgradeLiferayProjectsOpMethods.runUpgradeJob( projectString, actionString, op.getRuntimeName().content(), new NullProgressMonitor() );

        mainCss.getUnderlyingFile().refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );

        final IVirtualFile serviceJarXml = webappRoot.getFile( "css/main.css" );

        assertEquals( true, serviceJarXml.exists() );

        String cssContent = CoreUtil.readStreamToString( mainCss.getUnderlyingFile().getContents() );

        assertEquals( false, cssContent.contains( "aui" ) );
    }

    @Before
    public void removeAllVersionRuntime()  throws Exception
    {
        removeAllRuntimes();
        setupPluginsSDKAndRuntime();
    }

}
