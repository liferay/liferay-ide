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
package com.liferay.ide.maven.core.tests;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.core.model.ProfileLocation;
import com.liferay.ide.project.core.tests.ProjectCoreBase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.osgi.framework.Version;



/**
 * @author Gregory Amerson
 * @author Simon Jiang *
 */
@SuppressWarnings( "restriction" )
public abstract class LiferayMavenProjectTestCase extends AbstractMavenProjectTestCase
{
    private final static String skipBundleTests = System.getProperty( "skipBundleTests" );

    protected final ProjectCoreBase base = new ProjectCoreBase();

    protected void createTestBundleProfile( NewLiferayPluginProjectOp op )
    {
        NewLiferayProfile profile = op.getNewLiferayProfiles().insert();

        Set<String> vals = profile.getLiferayVersion().service( PossibleValuesService.class ).values();

        Version greatest = new Version( "6.2.2" );

        for( final String val : vals )
        {
            try
            {
                final Version v = new Version( val );

                if( greatest == null )
                {
                    greatest = v;
                }
                else
                {
                    if( CoreUtil.compareVersions( greatest, v ) < 0 )
                    {
                        greatest = v;
                        break;
                    }
                }
            }
            catch( Exception e )
            {
            }
        }

        profile.setLiferayVersion( greatest.getMajor() + "." + greatest.getMicro() + "." + greatest.getMinor() );
        profile.setId( "test-bundle" );
        profile.setRuntimeName( base.getRuntimeVersion() );
        profile.setProfileLocation( ProfileLocation.projectPom );

        op.setActiveProfilesValue( "test-bundle" );
    }

    public void failTest( Exception e )
    {
        StringWriter s = new StringWriter();
        e.printStackTrace( new PrintWriter( s ) );
        fail( s.toString() );
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        base.setupRuntime();
    }

    protected boolean shouldSkipBundleTests() { return "true".equals( skipBundleTests ); }

    public void waitForBuildAndValidation() throws Exception
    {
        IWorkspaceRoot root = null;

        try
        {
            ResourcesPlugin.getWorkspace().checkpoint( true );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor() );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor() );
            Job.getJobManager().join( ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor() );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor() );
            Thread.sleep( 200 );
            Job.getJobManager().beginRule( root = ResourcesPlugin.getWorkspace().getRoot(), null );
        }
        catch( InterruptedException e )
        {
            failTest( e );
        }
        catch( IllegalArgumentException e )
        {
            failTest( e );
        }
        catch( OperationCanceledException e )
        {
            failTest( e );
        }
        finally
        {
            if( root != null )
            {
                Job.getJobManager().endRule( root );
            }
        }
    }

    public void waitForBuildAndValidation( IProject project ) throws Exception
    {
        project.build( IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor() );
        waitForBuildAndValidation();
        project.build( IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor() );
        waitForBuildAndValidation();
    }

}