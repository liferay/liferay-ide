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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProfileLocation;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.theme.core.util.ThemeUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import org.apache.maven.archetype.exception.UnknownArchetype;
import org.apache.maven.archetype.metadata.RequiredProperty;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.cli.configuration.SettingsXmlConfigurationProcessor;
import org.apache.maven.model.Model;
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
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.archetype.ArchetypeManager;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenUpdateRequest;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class NewMavenPluginProjectProvider extends LiferayMavenProjectProvider implements NewLiferayProjectProvider<NewLiferayPluginProjectOp>
{

    @Override
    public IStatus createNewProject( NewLiferayPluginProjectOp op, IProgressMonitor monitor ) throws CoreException
    {
        ElementList<ProjectName> projectNames = op.getProjectNames();

        IStatus retval = null;

        final IMavenConfiguration mavenConfiguration = MavenPlugin.getMavenConfiguration();
        final IMavenProjectRegistry mavenProjectRegistry = MavenPlugin.getMavenProjectRegistry();
        final IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

        final String groupId = op.getGroupId().content();
        final String artifactId = op.getProjectName().content();
        final String version = op.getArtifactVersion().content();
        final String javaPackage = op.getGroupId().content();
        final String activeProfilesValue = op.getActiveProfilesValue().content();
        final IPortletFramework portletFramework = op.getPortletFramework().content( true );
        final String frameworkName = NewLiferayPluginProjectOpMethods.getFrameworkName( op );

        IPath location = PathBridge.create( op.getLocation().content() );

        // for location we should use the parent location
        if( location.lastSegment().equals( artifactId ) )
        {
            // use parent dir since maven archetype will generate new dir under this location
            location = location.removeLastSegments( 1 );
        }

        final String archetypeArtifactId = op.getArchetype().content( true );

        final Archetype archetype = new Archetype();

        final String[] gav = archetypeArtifactId.split( ":" );

        final String archetypeVersion = gav[gav.length - 1];

        archetype.setGroupId( gav[0] );
        archetype.setArtifactId( gav[1] );
        archetype.setVersion( archetypeVersion );

        final ArchetypeManager archetypeManager = MavenPluginActivator.getDefault().getArchetypeManager();
        final ArtifactRepository remoteArchetypeRepository = archetypeManager.getArchetypeRepository( archetype );
        final Properties properties = new Properties();

        try
        {
            final List<?> archProps =
                archetypeManager.getRequiredProperties( archetype, remoteArchetypeRepository, monitor );

            if( !CoreUtil.isNullOrEmpty( archProps ) )
            {
                for( Object prop : archProps )
                {
                    if( prop instanceof RequiredProperty )
                    {
                        final RequiredProperty rProp = (RequiredProperty) prop;

                        if( op.getPluginType().content().equals( PluginType.theme ) )
                        {
                            final String key = rProp.getKey();

                            if( key.equals( "themeParent" ) )
                            {
                                properties.put( key, op.getThemeParent().content( true ) );
                            }
                            else if( key.equals( "themeType" ) )
                            {
                                properties.put(
                                    key, ThemeUtil.getTemplateExtension( op.getThemeFramework().content( true ) ) );
                            }
                        }
                        else
                        {
                            properties.put( rProp.getKey(), rProp.getDefaultValue() );
                        }
                    }
                }
            }
        }
        catch( UnknownArchetype e1 )
        {
            LiferayMavenCore.logError( "Unable to find archetype required properties", e1 );
        }

        final ResolverConfiguration resolverConfig = new ResolverConfiguration();

        if( ! CoreUtil.isNullOrEmpty( activeProfilesValue ) )
        {
            resolverConfig.setSelectedProfiles( activeProfilesValue );
        }

        final ProjectImportConfiguration configuration = new ProjectImportConfiguration( resolverConfig );

        final List<IProject> newProjects =
            projectConfigurationManager.createArchetypeProjects(
                location, archetype, groupId, artifactId, version, javaPackage, properties, configuration, monitor );

        if( !CoreUtil.isNullOrEmpty( newProjects ) )
        {
            for( IProject project : newProjects )
            {
                projectNames.insert().setName( project.getName() );
            }
        }

        if( CoreUtil.isNullOrEmpty( newProjects ) )
        {
            retval = LiferayMavenCore.createErrorStatus( "New project was not created due to unknown error" );
        }
        else
        {
            final IProject firstProject = newProjects.get( 0 );

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
                        userSettingsPath =
                            SettingsXmlConfigurationProcessor.DEFAULT_USER_SETTINGS_FILE.getAbsolutePath();
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
                            MavenUtil.createNewLiferayProfileNode( pomDocument, newProfile );
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
                final IMavenProjectFacade newMavenProject = mavenProjectRegistry.getProject( firstProject );

                final IFile pomFile = newMavenProject.getPom();

                IDOMModel domModel = null;

                try
                {
                    domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( pomFile );

                    for( final NewLiferayProfile newProfile : newProjectPomProfiles )
                    {
                        MavenUtil.createNewLiferayProfileNode( domModel.getDocument(), newProfile );
                    }

                    domModel.save();

                }
                catch( IOException e )
                {
                    LiferayMavenCore.logError( "Unable to save new Liferay profiles to project pom.", e );
                }
                finally
                {
                    if( domModel != null )
                    {
                        domModel.releaseFromEdit();
                    }
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

                final String pluginVersion =
                    getNewLiferayProfilesPluginVersion( activeProfiles, op.getNewLiferayProfiles(), archetypeVersion );

                final String archVersion = MavenUtil.getMajorMinorVersionOnly( archetypeVersion );
                updateDtdVersion( firstProject, pluginVersion, archVersion );
            }

            if( op.getPluginType().content().equals( PluginType.portlet ) )
            {
                final String portletName = op.getPortletName().content( false );
                retval = portletFramework.postProjectCreated( firstProject, frameworkName, portletName, monitor );
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
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;
        // if the path is a folder and it has a pom.xml that is a package type of 'pom' then this is a valid location
        //if projectName is null or empty , don't need to check , just return
        if( CoreUtil.isNullOrEmpty(projectName) )
            return  retval;

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
                        final String name = result.getName();

                        if( projectName.equals( name ) )
                        {
                            retval =
                                LiferayMavenCore.createErrorStatus( "The project name \"" + projectName +
                                    "\" can't be the same as the parent." );
                        }
                        else
                        {
                            final IPath newProjectPath = path.append( projectName );

                            retval = validateProjectLocation( projectName, newProjectPath );
                        }
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
