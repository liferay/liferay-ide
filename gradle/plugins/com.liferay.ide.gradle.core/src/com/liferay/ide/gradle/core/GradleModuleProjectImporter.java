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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.AbstractLiferayProjectImporter;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Andy Wu
 */
public class GradleModuleProjectImporter extends AbstractLiferayProjectImporter
{

    @Override
    public boolean canImport( String location )
    {
        boolean retval = false;

        File file = new File( location );

        if( file.exists() )
        {
            File[] childFiles = file.listFiles();

            for( File child : childFiles )
            {
                if( !child.isDirectory() )
                {
                    child.getName().endsWith( ".gradle" );
                    retval = true;
                }
            }
        }


        //TODO also check for all parent folders to see if anywhere in the parent hierachy has a build.gradle

        return retval;

        //another way to detect the gradle project

        /*try
        {
            ProjectImportConfiguration configuration = new ProjectImportConfiguration();
            GradleDistributionWrapper from = GradleDistributionWrapper.from( GradleDistribution.fromBuild() );

            configuration.setGradleDistribution( from );
            configuration.setProjectDir( new File( location ) );
            configuration.setApplyWorkingSets( false );
            configuration.setWorkingSets( new ArrayList<String>() );

            ProcessStreams stream = CorePlugin.processStreamsProvider().getBackgroundJobProcessStreams();

            CancellationTokenSource tokenSource = GradleConnector.newCancellationTokenSource();

            ProgressListener listener = new ProgressListener()
            {
                public void statusChanged( ProgressEvent arg0 )
                {
                }
            };

            List<ProgressListener> listeners = new ArrayList<ProgressListener>();
            listeners.add( listener );

            TransientRequestAttributes transientAttributes = new TransientRequestAttributes(
                false, stream.getOutput(), stream.getError(), null, listeners,
                ImmutableList.<org.gradle.tooling.events.ProgressListener> of(), tokenSource.token() );

            ModelRepository repository =
                CorePlugin.modelRepositoryProvider().getModelRepository( configuration.toFixedAttributes() );

            OmniGradleBuildStructure buildStructure =
                repository.fetchGradleBuildStructure( transientAttributes, FetchStrategy.FORCE_RELOAD );

            if( buildStructure.getRootProject() != null )
            {
                return true;
            }

            return false;
        }
        catch( Exception e )
        {
            return false;
        }*/

    }

    @Override
    public void importProject( String location, IProgressMonitor monitor ) throws CoreException
    {
        GradleUtil.importGradleProject( new File( location ), monitor );
    }

}
