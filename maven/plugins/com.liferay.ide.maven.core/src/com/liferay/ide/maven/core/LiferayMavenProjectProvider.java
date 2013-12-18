/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.maven.core.aether.AetherUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProfileLocation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.Model;
import org.apache.maven.settings.Profile;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenUpdateRequest;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayMavenProjectProvider extends NewLiferayProjectProvider
{

    private static final String LIFERAY_ARCHETYPES_GROUP_ID = "com.liferay.maven.archetypes";

    public LiferayMavenProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    public IStatus doCreateNewProject( final NewLiferayPluginProjectOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;

        final IMavenConfiguration mavenConfiguration = MavenPlugin.getMavenConfiguration();
        final IMavenProjectRegistry mavenProjectRegistry = MavenPlugin.getMavenProjectRegistry();
        final IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

        final String groupId = op.getGroupId().content();
        final String artifactId = op.getProjectName().content();
        final String version = op.getArtifactVersion().content();
        final String javaPackage = op.getGroupId().content();
        final String activeProfilesValue = op.getActiveProfilesValue().content();
        final PluginType pluginType = op.getPluginType().content( true );
        final IPortletFramework portletFramework = op.getPortletFramework().content( true );
        final String frameworkName = getFrameworkName( op );

        IPath location = PathBridge.create( op.getLocation().content() );

        // for location we should use the parent location
        if( location.lastSegment().equals( artifactId ) )
        {
            // use parent dir since maven archetype will generate new dir under this location
            location = location.removeLastSegments( 1 );
        }

        String archetypeType = null;

        if( pluginType.equals( PluginType.portlet ) && portletFramework.isRequiresAdvanced() )
        {
            archetypeType = "portlet-" + frameworkName.replace( "_", "-" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        else
        {
            archetypeType = pluginType.name();
        }

        final String archetypeArtifactId = "liferay-" + archetypeType + "-archetype"; //$NON-NLS-1$ //$NON-NLS-2$

        // get latest liferay archetype
        monitor.beginTask( "Determining latest Liferay maven plugin archetype version.", IProgressMonitor.UNKNOWN );
        final String archetypeVersion =
            AetherUtil.getLatestAvailableLiferayArtifact( LIFERAY_ARCHETYPES_GROUP_ID, archetypeArtifactId ).getVersion();

        final Archetype archetype = new Archetype();
        archetype.setArtifactId( archetypeArtifactId );
        archetype.setGroupId( LIFERAY_ARCHETYPES_GROUP_ID );
        archetype.setModelEncoding( "UTF-8" );
        archetype.setVersion( archetypeVersion );

        final Properties properties = new Properties();

        final ResolverConfiguration resolverConfig = new ResolverConfiguration();

        if( ! CoreUtil.isNullOrEmpty( activeProfilesValue ) )
        {
            resolverConfig.setSelectedProfiles( activeProfilesValue );
        }

        final ProjectImportConfiguration configuration = new ProjectImportConfiguration( resolverConfig );

        final List<IProject> newProjects =
            projectConfigurationManager.createArchetypeProjects(
                location, archetype, groupId, artifactId, version, javaPackage, properties, configuration, monitor );

        if( CoreUtil.isNullOrEmpty( newProjects ) )
        {
            retval = LiferayMavenCore.createErrorStatus( "New project was not created due to unknown error" );
        }
        else
        {
            // add new profiles if it was specified to add to project or parent poms
            if( ! CoreUtil.isNullOrEmpty( activeProfilesValue ) )
            {
                final String[] activeProfiles = activeProfilesValue.split( "," );

                // find all profiles that should go in user settings file
                final List<NewLiferayProfile> newUserSettingsProfiles =
                    getNewProfilesToSave( activeProfiles, op.getNewLiferayProfiles(), ProfileLocation.userSettings );

                if( newUserSettingsProfiles.size() > 0 )
                {
                    final String userSettingsFile = mavenConfiguration.getUserSettingsFile();

                    String userSettingsPath = null;

                    if( CoreUtil.isNullOrEmpty( userSettingsFile ) )
                    {
                        userSettingsPath = MavenCli.DEFAULT_USER_SETTINGS_FILE.getAbsolutePath();
                    }
                    else
                    {
                        userSettingsPath = userSettingsFile;
                    }

                    try
                    {
                        // backup user's settings.xml file
                        final File settingsXmlFile = new File( userSettingsPath );
                        final File backupFile = getBackupFile( settingsXmlFile );

                        FileUtils.copyFile( settingsXmlFile, backupFile );

                        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                        final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                        final Document pomDocument = docBuilder.parse( settingsXmlFile.getCanonicalPath() );

                        for( NewLiferayProfile newProfile : newUserSettingsProfiles )
                        {
                            MavenUtil.createNewLiferayProfileNode( pomDocument, newProfile, archetypeVersion );
                        }

                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource( pomDocument );
                        StreamResult result = new StreamResult( settingsXmlFile );
                        transformer.transform(source, result);
                    }
                    catch( Exception e )
                    {
                        LiferayMavenCore.logError( "Unable to save new Liferay profile to user settings.xml.", e );
                    }
                }

                // find all profiles that should go in the project pom
                final List<NewLiferayProfile> newProjectPomProfiles =
                    getNewProfilesToSave( activeProfiles, op.getNewLiferayProfiles(), ProfileLocation.projectPom );

                // only need to set the first project as nested projects should pickup the parent setting
                final IProject newProject = newProjects.get( 0 );

                final IMavenProjectFacade newMavenProject = mavenProjectRegistry.getProject( newProject );

                final IFile pomFile = newMavenProject.getPom();

                try
                {
                    final IDOMModel domModel =
                        (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( pomFile );

                    for( final NewLiferayProfile newProfile : newProjectPomProfiles )
                    {
                        final Node newNode =
                            MavenUtil.createNewLiferayProfileNode( domModel.getDocument(), newProfile, archetypeVersion );
                        FormatProcessorXML formatter = new FormatProcessorXML();
                        formatter.formatNode( newNode );
                    }

                    domModel.save();

                    domModel.releaseFromEdit();
                }
                catch( IOException e )
                {
                    LiferayMavenCore.logError( "Unable to save new Liferay profiles to project pom.", e );
                }

                for( final IProject project : newProjects )
                {
                    try
                    {
                        projectConfigurationManager.updateProjectConfiguration(
                            new MavenUpdateRequest( project, mavenConfiguration.isOffline(), true ), monitor );
                    }
                    catch( Exception e )
                    {
                        LiferayMavenCore.logError( "Unable to update configuration for " + project.getName(), e );
                    }
                }
            }

            if( op.getPluginType().content().equals( PluginType.portlet ) )
            {
                retval = op.getPortletFramework().content().postProjectCreated( newProjects.get( 0 ), frameworkName, monitor );
            }
        }

        if( retval == null )
        {
            retval = Status.OK_STATUS;
        }

        return retval;
    }

    private File getBackupFile( final File file )
    {
        final String suffix = new SimpleDateFormat( "yyyyMMddhhmmss" ).format( Calendar.getInstance().getTime() );
        return new File( file.getParentFile(), file.getName() + "." + suffix );
    }

    @Override
    public <T> List<T> getData( String key, Class<T> type, Object... params )
    {
        List<T> retval = null;

        if( "profileIds".equals( key ) )
        {
            final List<T> profileIds = new ArrayList<T>();

            try
            {
                final List<Profile> profiles = MavenPlugin.getMaven().getSettings().getProfiles();

                for( final Profile profile : profiles )
                {
                    if( profile.getActivation() != null )
                    {
                        if( profile.getActivation().isActiveByDefault() )
                        {
                            continue;
                        }
                    }

                    profileIds.add( type.cast( profile.getId() ) );
                }

                if( params[0] != null && params[0] instanceof File )
                {
                    final File locationDir = (File) params[0];

                    File pomFile = new File( locationDir, IMavenConstants.POM_FILE_NAME );

                    if( ! pomFile.exists() && locationDir.getParentFile().exists() )
                    {
                        // try one level up for when user is adding new module
                        pomFile = new File( locationDir.getParentFile(), IMavenConstants.POM_FILE_NAME );
                    }

                    if( pomFile.exists() )
                    {
                        final IMaven maven = MavenPlugin.getMaven();

                        Model model = maven.readModel( pomFile );

                        File parentDir = pomFile.getParentFile();

                        while( model != null )
                        {
                            for( org.apache.maven.model.Profile p : model.getProfiles() )
                            {
                                profileIds.add( type.cast( p.getId() ) );
                            }

                            parentDir = parentDir.getParentFile();

                            if( parentDir != null && parentDir.exists() )
                            {
                                try
                                {
                                    model = maven.readModel( new File( parentDir, IMavenConstants.POM_FILE_NAME ) );
                                }
                                catch( Exception e)
                                {
                                    model = null;
                                }
                            }
                        }
                    }
                }
            }
            catch( CoreException e )
            {
                LiferayMavenCore.logError( e );
            }

            retval = profileIds;
        }
        else if( "liferayVersions".equals( key ) )
        {
            final List<T> possibleVersions = new ArrayList<T>();

            final RepositorySystem system = AetherUtil.newRepositorySystem();

            final RepositorySystemSession session = AetherUtil.newRepositorySystemSession( system );

            final String groupId = params[0].toString();
            final String artifactId = params[1].toString();

            final String coords = groupId + ":" + artifactId + ":[6,)";

            final Artifact artifact = new DefaultArtifact( coords );

            final VersionRangeRequest rangeRequest = new VersionRangeRequest();
            rangeRequest.setArtifact( artifact );
            rangeRequest.addRepository( AetherUtil.newCentralRepository() );
            rangeRequest.addRepository( AetherUtil.newLiferayRepository() );

            try
            {
                final VersionRangeResult rangeResult = system.resolveVersionRange( session, rangeRequest );

                final List<Version> versions = rangeResult.getVersions();

                for( Version version : versions )
                {
                    final String val = version.toString();

                    if( ! "6.2.0".equals( val ) )
                    {
                        possibleVersions.add( type.cast( val ) );
                    }
                }

                retval = possibleVersions;
            }
            catch( VersionRangeResolutionException e )
            {
            }
        }
        else if( "parentVersion".equals( key ) )
        {
            final List<T> version = new ArrayList<T>();

            final File locationDir = (File) params[0];

            File parentPom = new File( locationDir, IMavenConstants.POM_FILE_NAME );

            if( parentPom.exists() )
            {
                try
                {
                    final IMaven maven = MavenPlugin.getMaven();

                    Model model = maven.readModel( parentPom );

                    version.add( type.cast( model.getVersion() ) );

                    retval = version;
                }
                catch( CoreException e )
                {
                    e.printStackTrace();
                }
            }
        }

        return retval;
    }

    private List<NewLiferayProfile> getNewProfilesToSave(
        String[] activeProfiles, List<NewLiferayProfile> newLiferayProfiles, ProfileLocation location )
    {
        List<NewLiferayProfile> profilesToSave = new ArrayList<NewLiferayProfile>();

        for( String activeProfile : activeProfiles )
        {
            for( NewLiferayProfile newProfile : newLiferayProfiles )
            {
                if( activeProfile.equals( newProfile.getId().content() ) &&
                    newProfile.getProfileLocation().content().equals( location ) )
                {
                    profilesToSave.add( newProfile );
                }
            }
        }

        return profilesToSave;
    }

    public ILiferayProject provide( Object type )
    {
        if( type instanceof IProject )
        {
            final IProject project = (IProject) type;

            try
            {
                if( MavenUtil.isMavenProject( project ) )
                {
                    return new LiferayMavenProject( project );
                }

            }
            catch( CoreException e )
            {
                LiferayMavenCore.logError(
                    "Unable to create ILiferayProject from maven project " + project.getName(), e ); //$NON-NLS-1$
            }
        }

        return null;
    }

    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;
        // if the path is a folder and it has a pom.xml that is a package type of 'pom' then this is a valid location

        final File dir = path.toFile();

        if( dir.exists() )
        {
            final File pomFile = path.append( IMavenConstants.POM_FILE_NAME ).toFile();

            if( pomFile.exists() )
            {
                final IMaven maven = MavenPlugin.getMaven();

                try
                {
                    final Model result = maven.readModel( pomFile );

                    if( ! "pom".equals( result.getPackaging() ) )
                    {
                        retval =
                            LiferayMavenCore.createErrorStatus( "\"" + pomFile.getParent() +
                                "\" contains a non-parent maven project." );
                    }
                    else
                    {
                        final IPath newProjectPath = path.append( projectName );

                        retval = validateProjectLocation( projectName, newProjectPath );
                    }
                }
                catch( CoreException e )
                {
                    retval = LiferayMavenCore.createErrorStatus( "Invalid project location.", e );
                    LiferayMavenCore.log( retval );
                }
            }
            else
            {
                final File[] files = dir.listFiles();

                if( files.length > 0 )
                {
                    retval = LiferayMavenCore.createErrorStatus( "Project location is not empty or a parent pom." );
                }
            }
        }

        return retval;
    }

}
