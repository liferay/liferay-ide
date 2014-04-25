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

package com.liferay.ide.project.core.model;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.alloy.core.LautRunner;
import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class UpgradeLiferayProjectsOpMethods
{

    private final static String publicid_regrex =
                    "-\\//(?:[a-z][a-z]+)\\//(?:[a-z][a-z]+)[\\s+(?:[a-z][a-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[a-z][a-z]+)";

    private final static String systemid_regrex =
        "^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd";

    private final static String[] fileNames = { "liferay-portlet.xml", "liferay-display.xml", "service.xml",
        "liferay-hook.xml", "liferay-layout-templates.xml", "liferay-look-and-feel.xml", "liferay-portlet-ext.xml",
        "liferay-plugin-package.properties" };


    public static final Status execute( final UpgradeLiferayProjectsOp op, final ProgressMonitor pm )
    {
        Status retval = Status.createOkStatus();

        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay plugin project (this process may take several minutes)", 30 );

        final ElementList<NamedItem> projectItems = op.getSelectedProjects();
        final ElementList<NamedItem> upgradeActions = op.getSelectedActions();
        final String runtimeName = op.getRuntimeName().content();
        final List<String> projectItemNames = new ArrayList<String>();
        final List<String> projectActionItems = new ArrayList<String>();

        for( NamedItem projectItem : projectItems )
        {
            projectItemNames.add( projectItem.getName().content() );
        }

        for( NamedItem upgradeAction : upgradeActions )
        {
            projectActionItems.add( upgradeAction.getName().content() );
        }

        Status[] upgradeStatuses = performUpgrade( projectItemNames, projectActionItems, runtimeName, monitor );


        for( Status s : upgradeStatuses )
        {
            if( !s.ok() )
            {
                retval = Status.createErrorStatus( "Some upgrade actions failed, please see Eclipse error log for more details" );
            }
        }

        return retval;
    }

    private static Status executeServiceBuild( final IProject project, final IFile servcieXml, IProgressMonitor monitor )
    {
        Status retval = null;

        if( project == null )
        {
            return Status.createErrorStatus( "This action can only be executed from a Liferay project" );

        }

        if( !ProjectUtil.isLiferayFacetedProject( project ) )
        {
            return Status.createErrorStatus( "This action can only be executed from a Liferay plugin project" );
        }

        final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 20 );
        submon.subTask( "Run Service Build Process" ); //$NON-NLS-1$ //$NON-NLS-2$

        final IWorkspaceRunnable workspaceRunner = new IWorkspaceRunnable()
        {

            public void run( IProgressMonitor monitor ) throws CoreException
            {

                final IProjectBuilder builder = getProjectBuilder( project );

                monitor.worked( 50 );

                IStatus retval = builder.buildService( servcieXml, monitor );

                if( retval == null )
                {
                    retval = LiferayProjectCore.createErrorStatus( "Could not build service for " + project .getName() );
                }

                if( retval == null || !retval.isOK() )
                {
                    throw new CoreException( retval );
                }

                monitor.worked( 90 );
            }
        };

        try
        {
            ResourcesPlugin.getWorkspace().run( workspaceRunner, submon );
        }
        catch( CoreException e1 )
        {
            retval = Status.createErrorStatus( e1 );
        }

        return retval == null || retval.ok() ? Status.createOkStatus() : retval;
    }

    private static IProjectBuilder getProjectBuilder( IProject project ) throws CoreException
    {
        final ILiferayProject liferayProject = LiferayCore.create( project );

        if( liferayProject == null )
        {
            throw new CoreException( LiferayProjectCore.createErrorStatus( "Could not create ILiferayProject for " +
                            project.getName() ) );
        }

        final IProjectBuilder builder = liferayProject.adapt( IProjectBuilder.class );

        if( builder == null )
        {
            throw new CoreException( LiferayProjectCore.createErrorStatus( "Could not create IProjectBuilder for " +
                            project.getName() ) );
        }

        return builder;
    }

    private static String getNewDoctTypeSetting( String doctypeSetting, String newValue, String regrex )
    {
        String newDoctTypeSetting = null;
        Pattern p = Pattern.compile( regrex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
        Matcher m = p.matcher( doctypeSetting );
        if( m.find() )
        {
            String oldVersionString = m.group( m.groupCount() );
            newDoctTypeSetting = doctypeSetting.replace( oldVersionString, newValue );
        }

        return newDoctTypeSetting;
    }

    private static IFile[] getUpgradeDTDFiles( IProject project )
    {
        List<IFile> files = new ArrayList<IFile>();

        for( String name : fileNames )
        {
            files.addAll( new SearchFilesVisitor().searchFiles( project, name ) );
        }

        return files.toArray( new IFile[files.size()] );
    }

    private static Status rebuildService( IProject project, IProgressMonitor monitor, int perUnit )
    {
        Status retval = Status.createOkStatus();
        try
        {
            int worked = 0;

            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Execute service rebuild" );

            List<IFile> files = new ArrayList<IFile>();
            files.addAll( new SearchFilesVisitor().searchFiles( project, ILiferayConstants.LIFERAY_SERVICE_BUILDER_XML_FILE ) );

            worked = worked + perUnit;
            submon.worked( worked );

            for( IFile servicesFile : files )
            {
                final Status serviceBuildStatus = executeServiceBuild( project, servicesFile, monitor);

                if ( !serviceBuildStatus.ok() )
                {
                    final IStatus error =
                        LiferayProjectCore.createErrorStatus( "Service build task execute faild for " +
                            project.getName() );
                    LiferayProjectCore.logError( error );

                    retval = StatusBridge.create( error );
                    break;
                }
            }

            worked = worked + perUnit;
            submon.worked( worked );
        }
        catch( Exception e )
        {
            final IStatus error =
                LiferayProjectCore.createErrorStatus( "Unable to run service build task for " + project.getName(), e );
            LiferayProjectCore.logError( error );

            retval = StatusBridge.create( error );
        }

        return retval;
    }

    private static Status runLaut( final IProject project, IProgressMonitor monitor, int perUnit )
    {
        Status retval = Status.createOkStatus();

        try
        {
            int worked = 0;

            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Execute alloyUI upgrade tool" );

            final LautRunner lautRunner = AlloyCore.getLautRunner();

            if( lautRunner == null )
            {
                LiferayProjectCore.logError( "Alloy Core Not set LautRunner", null ); //$NON-NLS-1$
            }
            else
            {
                worked = worked + perUnit;
                submon.worked( worked );

                lautRunner.exec( project, monitor );

                worked = worked + perUnit;
                submon.worked( worked );
            }
        }
        catch( Exception e )
        {
            final IStatus error =
                LiferayProjectCore.createErrorStatus( "Unable to run LautTunner for " + project.getName(), e );
            LiferayProjectCore.logError( error );

            retval = StatusBridge.create( error );
        }

        return retval;
    }

    public static final Status[] performUpgrade(
        final List<String> projectItems, final List<String> projectActions, final String runtimeName,
        final IProgressMonitor monitor )
    {
        final List<Status> retval = new ArrayList<Status>();

        int worked = 0;
        int workUnit = projectItems.size();
        int actionUnit = projectActions.size();
        int totalWork = 100;
        int perUnit = totalWork / ( workUnit * actionUnit );
        monitor.beginTask( "Upgrading Project ", totalWork );

        for( String projectItem : projectItems )
        {
            if( projectItem != null )
            {
                IProject project = ProjectUtil.getProject( projectItem );
                monitor.setTaskName( "Upgrading Project " + project.getName() );

                for( String action : projectActions )
                {
                    if( action.equals( "RuntimeUpgrade" ) )
                    {
                        final Status status = upgradeRuntime( project, runtimeName, monitor, perUnit );
                        retval.add( status );
                        worked = worked + totalWork / ( workUnit * actionUnit );
                        monitor.worked( worked );
                    }

                    if( action.equals( "MetadataUpgrade" ) )
                    {
                        final Status status = upgradeDTDHeader( project, monitor, perUnit );
                        retval.add( status );
                        worked = worked + totalWork / ( workUnit * actionUnit );
                        monitor.worked( worked );
                    }

                    if( action.equals( "ServicebuilderUpgrade" ) )
                    {
                        final Status status = rebuildService( project, monitor, perUnit );
                        retval.add( status );
                        worked = worked + totalWork / ( workUnit * actionUnit );
                        monitor.worked( worked );
                    }

                    if( action.equals( "AlloyUIExecute" ) )
                    {
                        final Status status = runLaut( project, monitor, perUnit );
                        retval.add( status );
                        worked = worked + totalWork / ( workUnit * actionUnit );
                        monitor.worked( worked );
                    }
                }
            }
        }

        return retval.toArray( new Status[0] );
    }

    private static void updateProperties( IFile file, String propertyName, String propertiesValue ) throws Exception
    {

        File osfile = new File( file.getLocation().toOSString() );
        PropertiesConfiguration pluginPackageProperties = new PropertiesConfiguration();
        pluginPackageProperties.load( osfile );
        pluginPackageProperties.setProperty( propertyName, propertiesValue );
        FileWriter output = new FileWriter( osfile );
        try
        {
            pluginPackageProperties.save( output );
        }
        finally
        {
            output.close();
        }
        file.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );

    }

    private static Status upgradeDTDHeader( IProject project, IProgressMonitor monitor, int perUnit )
    {
        Status retval = Status.createOkStatus();

        try
        {
            int worked = 0;
            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Prograde Upgrade Update DTD Header" );

            IFile[] metaFiles = getUpgradeDTDFiles( project );

            for( IFile file : metaFiles )
            {
                IStructuredModel editModel = StructuredModelManager.getModelManager().getModelForEdit( file );

                if( editModel != null && editModel instanceof IDOMModel )
                {
                    worked = worked + perUnit;
                    submon.worked( worked );

                    IDOMDocument xmlDocument = ( (IDOMModel) editModel ).getDocument();
                    DocumentTypeImpl docType = (DocumentTypeImpl) xmlDocument.getDoctype();

                    String publicId = docType.getPublicId();
                    String newPublicId = getNewDoctTypeSetting( publicId, "6.2.0", publicid_regrex );
                    if( newPublicId != null )
                    {
                        docType.setPublicId( newPublicId );
                    }

                    worked = worked + perUnit;
                    submon.worked( worked );

                    String systemId = docType.getSystemId();
                    String newSystemId = getNewDoctTypeSetting( systemId, "6_2_0", systemid_regrex );
                    if( newSystemId != null )
                    {
                        docType.setSystemId( newSystemId );
                    }

                    editModel.save();
                    editModel.releaseFromEdit();

                    worked = worked + perUnit;
                    submon.worked( worked );
                }
                else
                {
                    updateProperties( file, "liferay-versions", "6.2.0+" );
                }
            }
        }
        catch( Exception e )
        {
            final IStatus error =
                LiferayProjectCore.createErrorStatus(
                    "Unable to upgrade deployment meta file for " + project.getName(), e );
            LiferayProjectCore.logError( error );

            retval = StatusBridge.create( error );
        }

        return retval;
    }

    private static Status upgradeRuntime( IProject project, String runtimeName, IProgressMonitor monitor, int perUnit )
    {
        Status retval = Status.createOkStatus();

        try
        {
            int worked = 0;
            final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 25 );
            submon.subTask( "Update project runtime" );

            final org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime =
                RuntimeManager.getRuntime( runtimeName );

            if( runtime != null )
            {
                worked = worked + perUnit;
                submon.worked( worked );

                if( runtime != null )
                {
                    final IFacetedProject fProject = ProjectUtil.getFacetedProject( project );

                    final org.eclipse.wst.common.project.facet.core.runtime.IRuntime primaryRuntime =
                        fProject.getPrimaryRuntime();

                    if( !runtime.equals( primaryRuntime ) )
                    {

                        worked = worked + perUnit;
                        submon.worked( worked );

                        fProject.setTargetedRuntimes( Collections.singleton( runtime ), monitor );

                        worked = worked + perUnit;
                        submon.worked( worked );

                        fProject.setPrimaryRuntime( runtime, monitor );
                        worked = worked + perUnit;
                        submon.worked( worked );
                    }
                }
            }
        }
        catch( Exception e )
        {
            final IStatus error =
                LiferayProjectCore.createErrorStatus( "Unable to upgrade target runtime for " + project.getName(), e );
            LiferayProjectCore.logError( error );

            retval = StatusBridge.create( error );
        }

        return retval;
    }
}
