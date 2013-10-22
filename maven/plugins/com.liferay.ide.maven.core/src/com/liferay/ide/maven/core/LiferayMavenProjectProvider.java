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

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.maven.archetype.catalog.Archetype;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.archetype.ArchetypeCatalogFactory;
import org.eclipse.m2e.core.internal.archetype.ArchetypeManager;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.sapphire.platform.PathBridge;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayMavenProjectProvider extends AbstractLiferayProjectProvider
{

    public LiferayMavenProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    public IStatus createNewProject( Object operation, IProgressMonitor monitor ) throws CoreException
    {
        if( ! (operation instanceof NewLiferayPluginProjectOp ) )
        {
            throw new IllegalArgumentException( "Operation must be of type NewLiferayPluginProjectOp" ); //$NON-NLS-1$
        }

        IStatus retval = null;

        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.class.cast( operation );

        final String groupId = op.getGroupId().content();
        final String artifactId = op.getProjectName().content();
        final String version = op.getVersion().content();
        final String javaPackage = op.getGroupId().content();

        IPath location = PathBridge.create( op.getLocation().content() );
        // for location we should use the parent location

        if( location.lastSegment().equals( artifactId ) )
        {
            // use parent dir since maven archetype will generate new dir under this location
            location = location.removeLastSegments( 1 );
        }

        final PluginType pluginType = op.getPluginType().content( true );
        final IPortletFramework portletFramework = op.getPortletFramework().content( true );
        final String archetypeGroupId = "com.liferay.maven.archetypes"; //$NON-NLS-1$

        String archetypeType = null;

        if( pluginType.equals( PluginType.portlet ) && portletFramework.isRequiresAdvanced() )
        {
            String frameworkName = op.getPortletFrameworkAdvanced().content( true ).getShortName();

            archetypeType = "portlet-" + frameworkName.replace( "_", "-" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        else
        {
            archetypeType = pluginType.name();
        }

        final String archetypeArtifactId = "liferay-" + archetypeType + "-archetype"; //$NON-NLS-1$ //$NON-NLS-2$
        final String archetypeVersion = "6.2.0-RC4"; //$NON-NLS-1$

        Archetype archetype = null;

        monitor.beginTask( "Searching for liferay maven archetypes from all catalogs.", IProgressMonitor.UNKNOWN ); //$NON-NLS-1$

        final List<Archetype> allArchetypes = getAllArchetypes();

        monitor.done();

        for( Archetype arc : allArchetypes )
        {
            if( archetypeGroupId.equals( arc.getGroupId() ) && archetypeArtifactId.equals( arc.getArtifactId() ) &&
                archetypeVersion.equals( arc.getVersion() ) )
            {
                archetype = arc;
                break;
            }
        }

        if( archetype == null )
        {
            retval =
                LiferayMavenCore.createErrorStatus( "Unable to find archetype " + archetypeGroupId + ":" +
                    archetypeArtifactId );
        }
        else
        {
            final Properties properties = new Properties();

            final ProjectImportConfiguration configuration = new ProjectImportConfiguration();

            final List<IProject> newProjects =
                MavenPlugin.getProjectConfigurationManager().createArchetypeProjects(
                    location, archetype, groupId, artifactId, version, javaPackage, properties, configuration, monitor );

            if( CoreUtil.isNullOrEmpty( newProjects ) )
            {
                retval = LiferayMavenCore.createErrorStatus( "New project was not created due to unknown error" );
            }
            else
            {
                if( op.getPluginType().content().equals( PluginType.portlet ) )
                {
                    retval = op.getPortletFramework().content().postProjectCreated( newProjects.get( 0 ), monitor );
                }
            }
        }

        if( retval == null )
        {
            retval = Status.OK_STATUS;
        }

        return retval;
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    private List<Archetype> getAllArchetypes()
    {
        ArchetypeManager manager = MavenPluginActivator.getDefault().getArchetypeManager();
        Collection<ArchetypeCatalogFactory> archetypeCatalogs = manager.getArchetypeCatalogs();
        ArrayList<Archetype> list = new ArrayList<Archetype>();

        for( ArchetypeCatalogFactory catalog : archetypeCatalogs )
        {
            try
            {
                // temporary hack to get around 'Test Remote Catalog' blowing up on download
                // described in https://issues.sonatype.org/browse/MNGECLIPSE-1792
                if( catalog.getDescription().startsWith( "Test" ) ) //$NON-NLS-1$
                {
                    continue;
                }

                List arcs = catalog.getArchetypeCatalog().getArchetypes();
                if( arcs != null )
                {
                    list.addAll( arcs );
                }
            }
            catch( Exception ce )
            {
                LiferayMavenCore.logError( "Unable to read archetype catalog: " + catalog.getId(), ce ); //$NON-NLS-1$
            }
        }

        return list;
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

}
