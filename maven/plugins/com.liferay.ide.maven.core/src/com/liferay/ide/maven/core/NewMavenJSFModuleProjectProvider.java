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

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.maven.core.aether.AetherUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.maven.archetype.ArchetypeGenerationRequest;
import org.apache.maven.archetype.ArchetypeGenerationResult;
import org.apache.maven.archetype.catalog.Archetype;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class NewMavenJSFModuleProjectProvider extends LiferayMavenProjectProvider
    implements NewLiferayProjectProvider<NewLiferayJSFModuleProjectOp>
{

    public NewMavenJSFModuleProjectProvider()
    {
        super();
    }

    protected IPath createArchetypeProject( NewLiferayJSFModuleProjectOp op, final IProgressMonitor monitor )
        throws CoreException
    {
        IPath projectLocation = null;
        final String javaPackage = "com.example";

        final String projectName = op.getProjectName().content();

        IPath location = PathBridge.create( op.getLocation().content() );

        // for location we should use the parent location
        if( location.lastSegment().equals( projectName ) )
        {
            // use parent dir since maven archetype will generate new dir under this location
            location = location.removeLastSegments( 1 );
        }

        final String groupId = op.getProjectName().content();
        final String artifactId = op.getProjectName().content();
        final String version = "1.0.0";

        final String archetypeArtifactId = op.getArchetype().content();

        final Archetype archetype = new Archetype();

        final String[] gav = archetypeArtifactId.split( ":" );

        final String archetypeVersion = gav[gav.length - 1];

        archetype.setGroupId( gav[0] );
        archetype.setArtifactId( gav[1] );
        archetype.setVersion( archetypeVersion );

        Artifact artifact = AetherUtil.getLatestAvailableArtifact( archetypeArtifactId );

        final Properties properties = new Properties();

        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

        if( location == null )
        {
            location = workspaceRoot.getLocation();
        }

        try
        {
            ArchetypeGenerationRequest request = new ArchetypeGenerationRequest()
                .setTransferListener( MavenPluginActivator.getDefault().getMaven().createTransferListener( monitor ) )
                .setArchetypeGroupId( artifact.getGroupId() )
                .setArchetypeArtifactId( artifact.getArtifactId() )
                .setArchetypeVersion( artifact.getVersion() )
                .setArchetypeRepository( AetherUtil.newCentralRepository().getUrl() )
                .setGroupId( groupId )
                .setArtifactId( artifactId )
                .setVersion( version )
                .setPackage( javaPackage )
                // the model does not have a package field
                .setLocalRepository( MavenPluginActivator.getDefault().getMaven().getLocalRepository() )
                .setRemoteArtifactRepositories(
                    MavenPluginActivator.getDefault().getMaven().getArtifactRepositories( true ) )
                .setProperties( properties )
                .setOutputDirectory( location.toPortableString() );

            ArchetypeGenerationResult result = getArchetyper().generateProjectFromArchetype( request );

            Exception cause = result.getCause();

            if( cause != null )
            {
                throw new CoreException( LiferayCore.createErrorStatus( "Unable to create project from archetype." ) );
            }

            projectLocation = location.append( artifactId );

            if( !projectLocation.toFile().exists() )
            {
                throw new CoreException( LiferayCore.createErrorStatus( "Can't create gradle JSF project. " ) );
            }
        }
        catch( Exception e )
        {
            throw new CoreException( LiferayCore.createErrorStatus( "Failed to create JSF project. ", e ) );
        }

        return projectLocation;
    }

    @Override
    public IStatus createNewProject( NewLiferayJSFModuleProjectOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;

        IPath projectLocation = createArchetypeProject( op, monitor );

        IPath buildGradle = projectLocation.append( "build.gradle" );

        if( buildGradle.toFile().exists() )
        {
            buildGradle.toFile().delete();
        }

        ILiferayProjectImporter importer = LiferayCore.getImporter( "maven" );
        IStatus canImport = importer.canImport( projectLocation.toOSString() );

        if( canImport.getCode() != Status.ERROR )
        {
            importer.importProjects( projectLocation.toOSString(), monitor );
        }

        retval = Status.OK_STATUS;

        return retval;
    }

    private Version extractLatestVersion( final String artifactId )
    {
        Version maxVersion = new Version( "5.0.1" );

        try
        {
            String responseString = getHttpResponse(
                "http://search.maven.org/solrsearch/select?q=g:com.liferay.faces.archetype+AND+a:" + artifactId +
                    "&rows=20&wt=json" );
            Object result = getJSONResponse( responseString );

            if( result instanceof JSONObject )
            {
                JSONObject resultJson = (JSONObject) result;
                Object response = resultJson.get( "response" );

                if( response != null )
                {
                    JSONObject responseJson = (JSONObject) response;
                    Object docs = responseJson.get( "docs" );

                    if( docs != null && docs instanceof JSONArray )
                    {
                        JSONArray docsJson = (JSONArray) docs;

                        for( int i = 0; i < docsJson.length(); i++ )
                        {
                            JSONObject docJson = (JSONObject) docsJson.get( i );
                            String versionString = (String) docJson.get( "latestVersion" );
                            Version tmpVersion = Version.parseVersion( versionString );

                            if( tmpVersion.compareTo( maxVersion ) > 0 || maxVersion == null )
                            {
                                maxVersion = tmpVersion;
                            }
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            LiferayMavenCore.logError( "Failed to get latest JSF archtype version", e );
        }

        return maxVersion;
    }

    private org.apache.maven.archetype.ArchetypeManager getArchetyper()
    {
        return MavenPluginActivator.getDefault().getArchetypeManager().getArchetyper();
    }

    @Override
    public <T> List<T> getData( String key, Class<T> type, Object... params )
    {
        if( "archetypeGAV".equals( key ) && type.equals( String.class ) && params.length == 1 )
        {
            List<T> retval = new ArrayList<>();

            String templateName = params[0].toString();

            Version latestVersion =
                extractLatestVersion( "com.liferay.faces.archetype." + templateName + ".portlet:" );

            final String gav = "com.liferay.faces.archetype:com.liferay.faces.archetype." + templateName +
                ".portlet:" + latestVersion.toString();

            retval.add( type.cast( gav ) );

            return retval;
        }

        return super.getData( key, type, params );
    }

    public String getHttpResponse( final String request )
    {
        StringBuilder retVal = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();

        try
        {
            // create httpget
            HttpGet httpget = new HttpGet( request );
            System.out.println( "executing request " + httpget.getURI() );

            // execute httpRequest
            HttpResponse response = httpclient.execute( httpget );
            int statusCode = response.getStatusLine().getStatusCode();

            if( statusCode == HttpStatus.SC_OK )
            {
                HttpEntity entity = response.getEntity();

                String body = CoreUtil.readStreamToString( entity.getContent(), false );

                EntityUtils.consume( entity );

                retVal.append( body );
            }
            else
            {
                return response.getStatusLine().getReasonPhrase();
            }
        }
        catch( Exception e )
        {
            LiferayMavenCore.logError( "Failed to http response from maven central", e );
        }

        return retVal.toString();
    }

    private Object getJSONResponse( String response )
    {
        Object retval = null;

        try
        {
            retval = new JSONObject( response );
        }
        catch( JSONException e )
        {
            try
            {
                retval = new JSONArray( response );
            }
            catch( JSONException e1 )
            {
            }
        }

        return retval;
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;

        final Path currentProjectLocation = PathBridge.create( path );

        boolean isLiferayWorkspace = false;

        if( currentProjectLocation != null )
        {
            isLiferayWorkspace = LiferayWorkspaceUtil.isValidWorkspaceLocation( currentProjectLocation.toOSString() );
        }

        if( LiferayWorkspaceUtil.isValidWorkspaceLocation( currentProjectLocation.toOSString() ) )
        {
            retval =
                LiferayMavenCore.createErrorStatus( "Can't set WorkspaceProject root folder as project directory." );
        }

        if( isLiferayWorkspace )
        {
            File workspaceDir = LiferayWorkspaceUtil.getWorkspaceDir( currentProjectLocation.toFile() );

            if( workspaceDir == null || !workspaceDir.exists() )
            {
                return LiferayCore.createErrorStatus( "The project location of Liferay Workspace shoule be existed." );
            }

            String[] folders =
                LiferayWorkspaceUtil.getLiferayWorkspaceProjectWarsDirs( workspaceDir.getAbsolutePath() );

            if( folders != null )
            {
                boolean appendWarFolder = false;
                IPath projectLocation = PathBridge.create( currentProjectLocation );

                for( String folder : folders )
                {
                    if( projectLocation.lastSegment().endsWith( folder ) )
                    {
                        appendWarFolder = true;
                        break;
                    }
                }

                if( appendWarFolder == false )
                {
                    return LiferayMavenCore.createErrorStatus(
                        "The project location should be set point to wars folder of Liferay workspace." );
                }
            }
            else
            {
                return LiferayMavenCore.createErrorStatus( "The Liferay Workspace wasn't defined wars folder path." );
            }
        }

        return retval;
    }
}
