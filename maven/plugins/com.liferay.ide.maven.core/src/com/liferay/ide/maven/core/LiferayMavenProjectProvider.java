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

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.maven.core.aether.AetherUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.descriptor.UpdateDescriptorVersionOperation;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.core.model.ProfileLocation;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.server.util.ComponentUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class LiferayMavenProjectProvider extends AbstractLiferayProjectProvider
{

    private static final String[] fileNames =
    {
        "liferay-portlet.xml",
        "liferay-display.xml",
        "service.xml",
        "liferay-hook.xml",
        "liferay-layout-templates.xml",
        "liferay-look-and-feel.xml",
        "liferay-portlet-ext.xml"
    };

    private static final Pattern publicid_pattern = Pattern.compile(
        "-\\//(?:[a-z][a-z]+)\\//(?:[a-z][a-z]+)[\\s+(?:[a-z][a-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[a-z][a-z]+)",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL );

    private static final Pattern systemid_pattern = Pattern.compile(
        "^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd", Pattern.CASE_INSENSITIVE |
            Pattern.DOTALL );

    public LiferayMavenProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
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

            final File parentPom = new File( locationDir, IMavenConstants.POM_FILE_NAME );

            if( parentPom.exists() )
            {
                try
                {
                    final IMaven maven = MavenPlugin.getMaven();

                    final Model model = maven.readModel( parentPom );

                    version.add( type.cast( model.getVersion() ) );

                    retval = version;
                }
                catch( CoreException e )
                {
                    LiferayMavenCore.logError( "unable to get parent version", e );
                }
            }
        }
        else if( "parentGroupId".equals( key ) )
        {
            final List<T> groupId = new ArrayList<T>();

            final File locationDir = (File) params[0];

            final File parentPom = new File( locationDir, IMavenConstants.POM_FILE_NAME );

            if( parentPom.exists() )
            {
                try
                {
                    final IMaven maven = MavenPlugin.getMaven();

                    final Model model = maven.readModel( parentPom );

                    groupId.add( type.cast( model.getGroupId() ) );

                    retval = groupId;
                }
                catch( CoreException e )
                {
                    LiferayMavenCore.logError( "unable to get parent groupId", e );
                }
            }
        }
        else if( "archetypeGAV".equals( key ) )
        {
            final String frameworkType = (String) params[0];

            final String value = LiferayMavenCore.getPreferenceString( "archetype-gav-" + frameworkType, "" );

            retval = Collections.singletonList( type.cast( value ) );
        }

        return retval;
    }

    private IFile[] getLiferayMetaFiles( IProject project )
    {
        List<IFile> files = new ArrayList<IFile>();

        for( String name : fileNames )
        {
            files.addAll( new SearchFilesVisitor().searchFiles( project, name ) );
        }

        return files.toArray( new IFile[files.size()] );
    }

    private String getNewDoctTypeSetting( String doctypeSetting, String newValue, Pattern p )
    {
        String newDoctTypeSetting = null;

        final Matcher m = p.matcher( doctypeSetting );

        if( m.find() )
        {
            String oldVersionString = m.group( m.groupCount() );
            newDoctTypeSetting = doctypeSetting.replace( oldVersionString, newValue );
        }

        return newDoctTypeSetting;
    }

    protected String getNewLiferayProfilesPluginVersion( String[] activeProfiles, List<NewLiferayProfile> newLiferayProfiles, String archetypeVersion )
    {
        org.osgi.framework.Version minVersion = new org.osgi.framework.Version( archetypeVersion.substring( 0, 3 ) );

        try
        {
            final List<Profile> profiles = MavenPlugin.getMaven().getSettings().getProfiles();

            org.osgi.framework.Version minNewVersion =
                new org.osgi.framework.Version( archetypeVersion.substring( 0, 3 ) );

            org.osgi.framework.Version minExistedVersion =
                new org.osgi.framework.Version( archetypeVersion.substring( 0, 3 ) );

            for( final String activeProfile : activeProfiles )
            {
                for( final NewLiferayProfile newProfile : newLiferayProfiles )
                {
                    if( activeProfile.equals( newProfile.getId().content() ) )
                    {
                        final String liferayVersion = newProfile.getLiferayVersion().content();

                        final org.osgi.framework.Version shortLiferayVersion =
                            new org.osgi.framework.Version( liferayVersion.substring( 0, 3 ) );

                        final org.osgi.framework.Version shortPluginVersion =
                            new org.osgi.framework.Version( archetypeVersion.substring( 0, 3 ) );

                        minNewVersion = shortLiferayVersion.compareTo( shortPluginVersion ) < 0
                                ? shortLiferayVersion : shortPluginVersion;
                    }
                }

                minVersion = minVersion.compareTo( minNewVersion ) < 0 ? minVersion : minNewVersion;

                for( final Profile existProfile : profiles )
                {
                    if( activeProfile.equals( existProfile.getId() ) )
                    {
                        final Properties properties = existProfile.getProperties();
                        final String liferayVersion = properties.getProperty( "liferay.version" );
                        final String pluginVersion = properties.getProperty( "liferay.maven.plugin.version" );

                        if( pluginVersion != null && liferayVersion != null )
                        {
                            final org.osgi.framework.Version shortLiferayVersion =
                                new org.osgi.framework.Version( liferayVersion.substring( 0, 3 ) );

                            final org.osgi.framework.Version shortPluginVersion =
                                new org.osgi.framework.Version( pluginVersion.substring( 0, 3 ) );

                            minExistedVersion = shortLiferayVersion.compareTo( shortPluginVersion ) < 0
                                    ? shortLiferayVersion : shortPluginVersion;
                        }
                    }
                }

                minVersion = minVersion.compareTo( minExistedVersion ) < 0 ? minVersion : minExistedVersion;
            }
        }
        catch(Exception e)
        {
        }

        return minVersion.toString();
    }

    protected List<NewLiferayProfile> getNewProfilesToSave(
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

    @Override
    public ILiferayProject provide( Object adaptable )
    {
        if( adaptable instanceof IProject )
        {
            final IProject project = (IProject) adaptable;

            try
            {
                if( MavenUtil.isMavenProject( project ) )
                {
                    if( LiferayNature.hasNature( project ) )
                    {
                        return new MavenBundlePluginProject( project );
                    }
                    else if( ComponentUtil.hasLiferayFacet( project ) )
                    {
                        return new FacetedMavenProject( project );
                    }
                    else
                    {
                        // return dummy maven project that can't lookup docroot resources
                        return new LiferayMavenProject( project )
                        {
                            @Override
                            public IFile getDescriptorFile( String name )
                            {
                                return null;
                            }
                        };
                    }
                }
            }
            catch( CoreException e )
            {
                LiferayMavenCore.logError(
                    "Unable to create ILiferayProject from maven project " + project.getName(), e );
            }
        }

        return null;
    }

    protected void updateDtdVersion( IProject project, String dtdVersion, String archetypeVesion )
    {
        final String tmpPublicId = dtdVersion;
        final String tmpSystemId = dtdVersion.replaceAll( "\\.", "_" );

        IStructuredModel editModel = null;

        final IFile[] metaFiles = getLiferayMetaFiles( project );

        for( IFile file : metaFiles )
        {
            try
            {
                editModel = StructuredModelManager.getModelManager().getModelForEdit( file );

                if( editModel != null && editModel instanceof IDOMModel )
                {
                    final IDOMDocument xmlDocument = ( (IDOMModel) editModel ).getDocument();
                    final DocumentTypeImpl docType = (DocumentTypeImpl) xmlDocument.getDoctype();

                    final String publicId = docType.getPublicId();
                    final String newPublicId = getNewDoctTypeSetting( publicId, tmpPublicId, publicid_pattern );

                    if( newPublicId != null )
                    {
                        docType.setPublicId( newPublicId );
                    }

                    final String systemId = docType.getSystemId();
                    final String newSystemId = getNewDoctTypeSetting( systemId, tmpSystemId, systemid_pattern );

                    if( newSystemId != null )
                    {
                        docType.setSystemId( newSystemId );
                    }

                    editModel.save();
                }
            }
            catch( Exception e )
            {
                final IStatus error =
                    ProjectCore.createErrorStatus(
                        "Unable to upgrade deployment meta file for " + file.getName(), e );
                ProjectCore.logError( error );
            }
            finally
            {
                if ( editModel != null )
                {
                    editModel.releaseFromEdit();
                }
            }
        }

        ProjectCore.operate( project, UpdateDescriptorVersionOperation.class, archetypeVesion, dtdVersion );
    }

}
