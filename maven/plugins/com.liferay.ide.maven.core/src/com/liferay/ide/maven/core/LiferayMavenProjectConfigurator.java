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
package com.liferay.ide.maven.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.dd.HookDescriptorHelper;
import com.liferay.ide.hook.core.util.HookUtil;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.markers.IMavenMarkerManager;
import org.eclipse.m2e.core.internal.markers.MavenProblemInfo;
import org.eclipse.m2e.core.internal.markers.SourceLocation;
import org.eclipse.m2e.core.internal.markers.SourceLocationHelper;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;
import org.eclipse.m2e.wtp.WTPProjectsUtil;
import org.eclipse.m2e.wtp.WarPluginConfiguration;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.util.ComponentUtilities;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.osgi.framework.Version;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 * @author Kamesh Sampath
 */
@SuppressWarnings( "restriction" )
public class LiferayMavenProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator
{

    private static final IPath ROOT_PATH = new Path("/");  //$NON-NLS-1$
    private static final String MAVEN_WAR_PLUGIN_KEY = "org.apache.maven.plugins:maven-war-plugin";
    private static final String WAR_SOURCE_FOLDER = "/src/main/webapp"; //$NON-NLS-1$

    private IMavenMarkerManager mavenMarkerManager;

    public LiferayMavenProjectConfigurator()
    {
        super();

        this.mavenMarkerManager = MavenPluginActivator.getDefault().getMavenMarkerManager();
    }

    private MavenProblemInfo checkValidConfigDir( Plugin liferayMavenPlugin, Xpp3Dom config, String configParam )
    {
        MavenProblemInfo retval = null;
        String message = null;
        String value = null;

        if( configParam != null )
        {
            if( config == null )
            {
                message = NLS.bind( Msgs.missingConfigValue, configParam );
            }
            else
            {
                final Xpp3Dom dirNode = config.getChild( configParam );

                if( dirNode == null )
                {
                    message = NLS.bind( Msgs.missingConfigValue, configParam );
                }
                else
                {
                    value = dirNode.getValue();

                    if( CoreUtil.isNullOrEmpty( value ) )
                    {
                        message = NLS.bind( Msgs.emptyConfigValue, configParam );
                    }
                    else
                    {
                        final File configDir = new File( value );

                        if( ( ! configDir.exists() ) || ( ! configDir.isDirectory() ) )
                        {
                            message = NLS.bind( Msgs.unusableConfigValue, configParam, value );
                        }
                    }
                }
            }
        }

        if( message != null )
        {
            final SourceLocation location =
                SourceLocationHelper.findLocation( liferayMavenPlugin, SourceLocationHelper.CONFIGURATION );

            retval = new MavenProblemInfo( message, IMarker.SEVERITY_WARNING, location );
        }

        return retval;
    }

    private MavenProblemInfo checkValidVersion( Plugin plugin, Xpp3Dom config, String versionNodeName )
    {
        MavenProblemInfo retval = null;
        Version liferayVersion = null;
        String version = null;

        if( config != null )
        {
            // check for version config node
            final Xpp3Dom versionNode = config.getChild( versionNodeName );

            if( versionNode != null )
            {
                version = versionNode.getValue();

                try
                {
                    liferayVersion = new Version(  MavenUtil.getVersion( version ) );
                }
                catch( IllegalArgumentException e )
                {
                    // bad version
                }
            }
        }

        if( liferayVersion == null || liferayVersion.equals( ILiferayConstants.EMPTY_VERSION ) )
        {
            // could not get valid liferayVersion
            final SourceLocation location =
                SourceLocationHelper.findLocation( plugin, SourceLocationHelper.CONFIGURATION );
            final String problemMsg = NLS.bind( Msgs.unusableConfigValue, versionNodeName, version );
            retval = new MavenProblemInfo( problemMsg, IMarker.SEVERITY_WARNING, location );
        }

        return retval;
    }

    @Override
    public void configure( ProjectConfigurationRequest request, IProgressMonitor monitor ) throws CoreException
    {
        if( monitor == null )
        {
            monitor = new NullProgressMonitor();
        }

        monitor.beginTask( NLS.bind( Msgs.configuringLiferayProject, request.getProject() ), 100 );

        final Plugin liferayMavenPlugin =
            MavenUtil.getPlugin(
                request.getMavenProjectFacade(), ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY, monitor );

        if( ! shouldConfigure( liferayMavenPlugin, request ) )
        {
            monitor.done();
            return;
        }

        final IProject project = request.getProject();
        final IFile pomFile = project.getFile( IMavenConstants.POM_FILE_NAME );
        final IFacetedProject facetedProject = ProjectFacetsManager.create( project, false, monitor );

        LiferayNature.addLiferayNature( project, monitor );

        removeLiferayMavenMarkers( project );

        monitor.worked( 25 );

        final MavenProject mavenProject = request.getMavenProject();
        final List<MavenProblemInfo> errors = findLiferayMavenPluginProblems( project, request, monitor );

        if( errors.size() > 0 )
        {
            try
            {
                this.markerManager.addErrorMarkers( pomFile,
                                                    ILiferayMavenConstants.LIFERAY_MAVEN_MARKER_CONFIGURATION_WARNING_ID,
                                                    errors );
            }
            catch( CoreException e )
            {
                // no need to log this error its just best effort
            }
        }

        monitor.worked( 25 );

        MavenProblemInfo installProblem = null;

        if( shouldInstallNewLiferayFacet( facetedProject ) )
        {
            installProblem = installNewLiferayFacet( facetedProject, request, monitor );
        }

        monitor.worked( 25 );

        if( installProblem != null )
        {
            this.markerManager.addMarker( pomFile,
                                          ILiferayMavenConstants.LIFERAY_MAVEN_MARKER_CONFIGURATION_WARNING_ID,
                                          installProblem.getMessage(),
                                          installProblem.getLocation().getLineNumber(),
                                          IMarker.SEVERITY_WARNING );
        }
        else
        {
            final String pluginType = MavenUtil.getLiferayMavenPluginType( mavenProject );

            // IDE-817 we need to mak sure that on deployment it will have the correct suffix for project name
            final IVirtualComponent projectComponent = ComponentCore.createComponent( project );

            try
            {
                if( projectComponent != null )
                {
                    final String deployedName = projectComponent.getDeployedName();

                    final String pluginTypeSuffix = "-" + pluginType;

                    final String deployedFileName = project.getName() + pluginTypeSuffix; //$NON-NLS-1$

                    if( deployedName == null || ( deployedName != null && ! deployedName.endsWith( pluginTypeSuffix ) ) )
                    {
                        configureDeployedName( project, deployedFileName );
                    }

                    final String oldContextRoot = ComponentUtilities.getServerContextRoot( project );

                    if( oldContextRoot == null || ( oldContextRoot != null && ! oldContextRoot.endsWith( pluginTypeSuffix ) ) )
                    {

                        final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( LiferayMavenCore.PLUGIN_ID );

                        boolean setMavenPluginSuffix =
                            prefs.getBoolean( LiferayMavenCore.PREF_ADD_MAVEN_PLUGIN_SUFFIX, false );

                        if( setMavenPluginSuffix )
                        {
                            ComponentUtilities.setServerContextRoot( project, deployedFileName );
                        }

                    }
                }
            }
            catch( Exception e )
            {
                LiferayMavenCore.logError(
                    "Unable to configure deployed name for project " + project.getName(), e );
            }

            if( ILiferayMavenConstants.THEME_PLUGIN_TYPE.equals( pluginType ) )
            {
                final IVirtualComponent component = ComponentCore.createComponent( project, true );

                if( component != null )
                {
                    // make sure to update the main deployment folder
                    WarPluginConfiguration config = new WarPluginConfiguration( mavenProject, project );
                    String warSourceDirectory = config.getWarSourceDirectory();
                    IFolder contentFolder = project.getFolder( warSourceDirectory );
                    IPath warPath = ROOT_PATH.append( contentFolder.getProjectRelativePath() );
                    IPath themeFolder = ROOT_PATH.append( getThemeTargetFolder( mavenProject, project ) );

                    // add a link to our m2e-liferay/theme-resources folder into deployment assembly
                    WTPProjectsUtil.insertLinkBefore(project, themeFolder, warPath, ROOT_PATH, monitor);
                }
            }
        }

        if ( project != null && ProjectUtil.isHookProject( project ) )
        {
            final HookDescriptorHelper hookDescriptor = new HookDescriptorHelper( project );
            final String customJSPFolder = hookDescriptor.getCustomJSPFolder( null );

            if ( customJSPFolder != null )
            {
                final IWebProject webproject = LiferayCore.create( IWebProject.class, project );

                if ( webproject != null && webproject.getDefaultDocrootFolder() != null)
                {
                    final IFolder docFolder = webproject.getDefaultDocrootFolder();
                    final IPath newPath = Path.fromOSString( customJSPFolder );
                    final IPath pathValue = docFolder.getFullPath().append( newPath );

                    final boolean disableCustomJspValidation =
                        LiferayMavenCore.getPreferenceBoolean( LiferayMavenCore.PREF_DISABLE_CUSTOM_JSP_VALIDATION );

                    if( disableCustomJspValidation )
                    {
                        HookUtil.configureJSPSyntaxValidationExclude(
                            project, project.getFolder( pathValue.makeRelativeTo( project.getFullPath() ) ), true );
                    }
                }
            }
        }

        monitor.worked( 25 );
        monitor.done();
    }

    public static IPath getThemeTargetFolder( MavenProject mavenProject, IProject project )
    {
        return MavenUtil.getM2eLiferayFolder( mavenProject, project ).append(
            ILiferayMavenConstants.THEME_RESOURCES_FOLDER );
    }

    public void configureClasspath( IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor )
        throws CoreException
    {
    }

    // Copied from org.eclipse.m2e.wtp.AbstractProjectConfiguratorDelegate#configureDeployedName()
    protected void configureDeployedName(IProject project, String deployedFileName) {
        //We need to remove the file extension from deployedFileName
        int extSeparatorPos  = deployedFileName.lastIndexOf('.');
        String deployedName = extSeparatorPos > -1? deployedFileName.substring(0, extSeparatorPos): deployedFileName;
        //From jerr's patch in MNGECLIPSE-965
        IVirtualComponent projectComponent = ComponentCore.createComponent(project);
        if(projectComponent != null && !deployedName.equals(projectComponent.getDeployedName())){//MNGECLIPSE-2331 : Seems projectComponent.getDeployedName() can be null
          StructureEdit moduleCore = null;
          try {
            moduleCore = StructureEdit.getStructureEditForWrite(project);
            if (moduleCore != null){
              WorkbenchComponent component = moduleCore.getComponent();
              if (component != null) {
                component.setName(deployedName);
                moduleCore.saveIfNecessary(null);
              }
            }
          } finally {
            if (moduleCore != null) {
              moduleCore.dispose();
            }
          }
        }
      }

    public void configureRawClasspath(
        ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor )
        throws CoreException
    {
    }

    private List<MavenProblemInfo> findLiferayMavenPluginProblems( IProject project,
                                                                   ProjectConfigurationRequest request,
                                                                   IProgressMonitor monitor ) throws CoreException
    {
        final List<MavenProblemInfo> warnings = new ArrayList<MavenProblemInfo>();

        // first check to make sure that the AppServer* properties are available and pointed to valid location
        final Plugin liferayMavenPlugin =
            MavenUtil.getPlugin(
                request.getMavenProjectFacade(), ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY, monitor );

        if( liferayMavenPlugin != null )
        {
            final Xpp3Dom config = (Xpp3Dom) liferayMavenPlugin.getConfiguration();

            final MavenProblemInfo validLiferayProblemInfo =
                checkValidVersion( liferayMavenPlugin, config, ILiferayMavenConstants.PLUGIN_CONFIG_LIFERAY_VERSION );

            if( validLiferayProblemInfo != null )
            {
                warnings.add( validLiferayProblemInfo );
            }

            final Version mavenPluginVersion = new Version( MavenUtil.getVersion( liferayMavenPlugin.getVersion() ) );

            if( mavenPluginVersion == null || mavenPluginVersion.equals( ILiferayConstants.EMPTY_VERSION ) )
            {
                // could not get valid version for liferaymavenPlugin
                final SourceLocation location = SourceLocationHelper.findLocation( liferayMavenPlugin, "version" );
                final String problemMsg =
                    NLS.bind( Msgs.invalidVersion, "liferay-maven-plugin", liferayMavenPlugin.getVersion() );
                final MavenProblemInfo versionProblem =
                    new MavenProblemInfo( problemMsg, IMarker.SEVERITY_WARNING, location );
                warnings.add( versionProblem );
            }

            final String[] configDirParams = new String[]
            {
                ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR,
            };

            for( final String configParam : configDirParams )
            {
                final MavenProblemInfo configProblemInfo =
                    checkValidConfigDir( liferayMavenPlugin, config, configParam );

                if( configProblemInfo != null )
                {
                    warnings.add( configProblemInfo );
                }
            }
        }

        return warnings;
    }

    private IProjectFacetVersion getLiferayProjectFacet( IFacetedProject facetedProject )
    {
        IProjectFacetVersion retval = null;

        if( facetedProject != null )
        {
            for( IProjectFacetVersion fv : facetedProject.getProjectFacets() )
            {
                if( fv.getProjectFacet().getId().contains( "liferay." ) ) //$NON-NLS-1$
                {
                    retval = fv;
                    break;
                }
            }
        }

        return retval;
    }

    private Action getNewLiferayFacetInstallAction( String pluginType )
    {
        Action retval = null;
        IProjectFacetVersion newFacet = null;
        IDataModelProvider dataModel = null;

        if( ILiferayMavenConstants.PORTLET_PLUGIN_TYPE.equals( pluginType ) )
        {
            newFacet = IPluginFacetConstants.LIFERAY_PORTLET_PROJECT_FACET.getDefaultVersion();
            dataModel = new MavenPortletPluginFacetInstallProvider();
        }
        else if( ILiferayMavenConstants.HOOK_PLUGIN_TYPE.equals( pluginType ) )
        {
            newFacet = IPluginFacetConstants.LIFERAY_HOOK_PROJECT_FACET.getDefaultVersion();
            dataModel = new MavenHookPluginFacetInstallProvider();
        }
        else if( ILiferayMavenConstants.EXT_PLUGIN_TYPE.equals( pluginType ) )
        {
            newFacet = IPluginFacetConstants.LIFERAY_EXT_PROJECT_FACET.getDefaultVersion();
            dataModel = new MavenExtPluginFacetInstallProvider();
        }
        else if( ILiferayMavenConstants.LAYOUTTPL_PLUGIN_TYPE.equals( pluginType ) )
        {
            newFacet = IPluginFacetConstants.LIFERAY_LAYOUTTPL_PROJECT_FACET.getDefaultVersion();
            dataModel = new MavenLayoutTplPluginFacetInstallProvider();
        }
        else if( ILiferayMavenConstants.THEME_PLUGIN_TYPE.equals( pluginType ) )
        {
            newFacet = IPluginFacetConstants.LIFERAY_THEME_PROJECT_FACET.getDefaultVersion();
            dataModel = new MavenThemePluginFacetInstallProvider();
        }
        else if( ILiferayMavenConstants.WEB_PLUGIN_TYPE.equals( pluginType ) )
        {
            newFacet = IPluginFacetConstants.LIFERAY_WEB_PROJECT_FACET.getDefaultVersion();
            dataModel = new MavenWebPluginFacetInstallProvider();
        }

        if( newFacet != null )
        {
            final IDataModel config = DataModelFactory.createDataModel( dataModel );
            retval = new Action( Action.Type.INSTALL, newFacet, config );
        }

        return retval;
    }

    private MavenProblemInfo installNewLiferayFacet( IFacetedProject facetedProject,
                                                     ProjectConfigurationRequest request,
                                                     IProgressMonitor monitor ) throws CoreException
    {
        MavenProblemInfo retval = null;

        final String pluginType = MavenUtil.getLiferayMavenPluginType( request.getMavenProject() );
        final Plugin liferayMavenPlugin =
            MavenUtil.getPlugin(
                request.getMavenProjectFacade(), ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY, monitor );
        final Action action = getNewLiferayFacetInstallAction( pluginType );

        if( action != null )
        {
            try
            {
                facetedProject.modify( Collections.singleton( action ), monitor );
            }
            catch( Exception e )
            {
                final SourceLocation location =
                    SourceLocationHelper.findLocation( liferayMavenPlugin, SourceLocationHelper.CONFIGURATION );
                final String problemMsg = NLS.bind( Msgs.facetInstallError,
                                                    pluginType,
                                                    e.getCause() != null ? e.getCause().getMessage() : e.getMessage() );

                retval = new MavenProblemInfo( location, e );
                retval.setMessage( problemMsg );

                LiferayMavenCore.logError(
                    "Unable to install liferay facet " + action.getProjectFacetVersion(), e.getCause() ); //$NON-NLS-1$
            }
        }

        return retval;
    }

    private void removeLiferayMavenMarkers( IProject project ) throws CoreException
    {
        this.mavenMarkerManager.deleteMarkers( project,
                                               ILiferayMavenConstants.LIFERAY_MAVEN_MARKER_CONFIGURATION_WARNING_ID );
    }

    /*
     * IDE-1489 when no liferay maven plugin is found the project will be scanned for liferay specific files
     */
    private boolean shouldConfigure( Plugin liferayMavenPlugin, ProjectConfigurationRequest request )
    {
        final IProject project = request.getProject();
        final MavenProject mavenProject = request.getMavenProject();

        boolean configureAsLiferayPlugin = liferayMavenPlugin != null;

        final IFolder warSourceDir = warSourceDirectory( project, mavenProject );

        if( !configureAsLiferayPlugin && warSourceDir != null )
        {

            final IPath baseDir = warSourceDir.getRawLocation();
            final String[] includes = { "**/liferay*.xml", "**/liferay*.properties" };

            final DirectoryScanner dirScanner = new DirectoryScanner();
            dirScanner.setBasedir( baseDir.toFile() );
            dirScanner.setIncludes( includes );
            dirScanner.scan();

            final String[] liferayProjectFiles = dirScanner.getIncludedFiles();
            configureAsLiferayPlugin = liferayProjectFiles != null && liferayProjectFiles.length > 0;
        }

        return configureAsLiferayPlugin;
    }

    private boolean shouldInstallNewLiferayFacet( IFacetedProject facetedProject )
    {
        return getLiferayProjectFacet( facetedProject ) == null;
    }

    private IFolder warSourceDirectory( final IProject project, final MavenProject mavenProject )
    {
        IFolder retval = null;

        final Xpp3Dom warPluginConfiguration =
            (Xpp3Dom) mavenProject.getPlugin( MAVEN_WAR_PLUGIN_KEY ).getConfiguration();

        if( warPluginConfiguration != null )
        {
            final Xpp3Dom[] warSourceDirs = warPluginConfiguration.getChildren( "warSourceDirectory" );

            if( warSourceDirs != null && warSourceDirs.length > 0 )
            {
                final String resourceLocation = warSourceDirs[0].getValue();
                retval = project.getFolder( resourceLocation );
            }
        }

        if( retval == null )
        {
            /*
             * if no explicit warSourceDirectory set we assume the default warSource directory
             * ${basedir}/src/main/webapp refer to http://maven.apache.org/plugins/maven-war-plugin/war-mojo.html
             * for more information
             */
            retval = project.getFolder( WAR_SOURCE_FOLDER );
        }

        return retval;
    }

    private static class Msgs extends NLS
    {
        public static String configuringLiferayProject;
        public static String emptyConfigValue;
        public static String facetInstallError;
        public static String invalidVersion;
        public static String missingConfigValue;
        public static String unusableConfigValue;

        static
        {
            initializeMessages( LiferayMavenProjectConfigurator.class.getName(), Msgs.class );
        }
    }

}
