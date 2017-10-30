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

package com.liferay.ide.project.core.facet;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.project.facet.IJ2EEModuleFacetInstallDataModelProperties;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public abstract class PluginFacetInstall implements IDelegate, IPluginProjectDataModelProperties
{

    protected static final String DEFAULT_DEPLOY_PATH = "/WEB-INF/classes"; //$NON-NLS-1$

    /**
     * copied from ProjectFacetPreferencesGroup
     */
    private static final String PATH_IN_PROJECT = ".settings/org.eclipse.wst.common.project.facet.core.prefs.xml"; //$NON-NLS-1$

    protected IDataModel masterModel = null;

    protected IDataModel model = null;

    protected IProgressMonitor monitor;

    protected IProject project;

    protected void configureDeploymentAssembly( final String srcPath, final String deployPath )
    {
        IVirtualComponent vProject = ComponentCore.createComponent( this.project );

        IVirtualFolder vProjectFolder = vProject.getRootFolder();

        IVirtualFolder deployFolder = vProjectFolder.getFolder( new Path( deployPath ) );

        try
        {
            deployFolder.createLink( new Path( srcPath ), IResource.FORCE, null );
        }
        catch( CoreException e )
        {
            ProjectCore.logError( "Unable to create link", e ); //$NON-NLS-1$
        }

        try
        {
            IPath outputLocation = JavaCore.create( this.project ).getOutputLocation();
            vProject.setMetaProperty( IModuleConstants.PROJ_REL_JAVA_OUTPUT_PATH, outputLocation.toPortableString() );
        }
        catch( JavaModelException e )
        {
            ProjectCore.logError( "Unable to set java-ouput-path", e ); //$NON-NLS-1$
        }
    }

    protected void copyToProject( IPath parent, File newFile ) throws CoreException, IOException
    {
        if( newFile == null || !shouldCopyToProject( newFile ) )
        {
            return;
        }

        IResource projectEntry = null;
        IPath newFilePath = new Path( newFile.getPath() );
        IPath newFileRelativePath = newFilePath.makeRelativeTo( parent );

        if( newFile.isDirectory() )
        {
            projectEntry = this.project.getFolder( newFileRelativePath );
        }
        else
        {
            projectEntry = this.project.getFile( newFileRelativePath );
        }

        if( projectEntry.exists() )
        {
            if( projectEntry instanceof IFolder )
            {
                // folder already exists, we can return
                return;
            }
            else if( projectEntry instanceof IFile )
            {
                ( (IFile) projectEntry ).setContents( Files.newInputStream( newFile.toPath() ), IResource.FORCE, null );
            }
        }
        else if( projectEntry instanceof IFolder )
        {
            IFolder newProjectFolder = (IFolder) projectEntry;

            newProjectFolder.create( true, true, null );
        }
        else if( projectEntry instanceof IFile )
        {
            ( (IFile) projectEntry ).create( Files.newInputStream( newFile.toPath() ), IResource.FORCE, null );
        }
    }

    protected boolean deletePath( IPath path )
    {
        if( path != null && path.toFile().exists() )
        {
            return path.toFile().delete();
        }

        return false;
    }

    @Override
    public void execute( IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor )
        throws CoreException
    {
        if( !( config instanceof IDataModel ) )
        {
            return;
        }
        else
        {
            this.model = (IDataModel) config;
            this.masterModel = (IDataModel) this.model.getProperty( FacetInstallDataModelProvider.MASTER_PROJECT_DM );
            this.project = project;
            this.monitor = monitor;
        }

        // IDE-195
        // If the user has the plugins sdk in the workspace, trying to write to the P/foo-portlet/.settings/ will find
        // the file first in the the plugins-sdk that is in the workspace and will fail to find the file.

        try
        {
            final IFile f = this.project.getProject().getFile( PATH_IN_PROJECT );
            final File file = f.getLocation().toFile();
            final IWorkspace ws = ResourcesPlugin.getWorkspace();
            final IWorkspaceRoot wsroot = ws.getRoot();
            final IPath path = new Path( file.getAbsolutePath() );
            final IFile[] wsFiles = wsroot.findFilesForLocationURI( path.toFile().toURI() );
            if( !CoreUtil.isNullOrEmpty( wsFiles ) )
            {
                for( IFile wsFile : wsFiles )
                {
                    wsFile.getParent().getParent().refreshLocal( IResource.DEPTH_INFINITE, null );
                }
            }
        }
        catch( Exception ex )
        {
            // best effort to make sure directories are current
        }

        if( shouldInstallPluginLibraryDelegate() )
        {
            installPluginLibraryDelegate();
        }

        if( shouldSetupDefaultOutputLocation() )
        {
            setupDefaultOutputLocation();

            IJavaProject javaProject = JavaCore.create( project );

            IPath outputLocation = project.getFolder( getDefaultOutputLocation() ).getFullPath();

            javaProject.setOutputLocation( outputLocation, monitor );
        }
    }

    protected IPath getAppServerDir()
    {
        IRuntime serverRuntime;

        if( masterModel != null )
        {
            serverRuntime = (IRuntime) masterModel.getProperty( PluginFacetInstallDataModelProvider.FACET_RUNTIME );
        }
        else
        {
            serverRuntime = getFacetedProject().getPrimaryRuntime();
        }

        return ServerUtil.getAppServerDir( serverRuntime );
    }

    protected abstract String getDefaultOutputLocation();

    protected IDataModel getFacetDataModel( String facetId )
    {
        IFacetedProjectWorkingCopy fp = getFacetedProject();

        for( IProjectFacetVersion pfv : fp.getProjectFacets() )
        {
            if( pfv.getProjectFacet().getId().equals( facetId ) )
            {
                Action action = fp.getProjectFacetAction( pfv.getProjectFacet() );

                if( action != null )
                {
                    Object config = action.getConfig();

                    return (IDataModel) Platform.getAdapterManager().getAdapter( config, IDataModel.class );
                }
            }
        }

        return null;
    }

    protected IFacetedProjectWorkingCopy getFacetedProject()
    {
        return (IFacetedProjectWorkingCopy) this.model.getProperty( IFacetDataModelProperties.FACETED_PROJECT_WORKING_COPY );
    }

    protected String getRuntimeLocation()
    {
        try
        {
            return ServerUtil.getRuntime( this.project ).getLocation().toOSString();
        }
        catch( CoreException e )
        {
            e.printStackTrace();
        }

        return null;
    }

    protected SDK getSDK()
    {
        String sdkName = null;

        try
        {
            sdkName = masterModel.getStringProperty( IPluginProjectDataModelProperties.LIFERAY_SDK_NAME );
        }
        catch( Exception ex )
        {
        }

        if( sdkName == null )
        {
            try
            {
                sdkName = model.getStringProperty( IPluginProjectDataModelProperties.LIFERAY_SDK_NAME );
            }
            catch( Exception ex )
            {
            }
        }

        return SDKManager.getInstance().getSDK( sdkName );
    }

    protected IFolder getWebRootFolder()
    {
        IDataModel webFacetDataModel = null;

        if( masterModel != null )
        {
            FacetDataModelMap map =
                (FacetDataModelMap) masterModel.getProperty( IFacetProjectCreationDataModelProperties.FACET_DM_MAP );

            webFacetDataModel = map.getFacetDataModel( IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId() );
        }
        else
        {
            webFacetDataModel = getFacetDataModel( IModuleConstants.JST_WEB_MODULE );
        }

        IPath webrootFullPath = null;

        if( webFacetDataModel != null )
        {
            webrootFullPath =
                this.project.getFullPath().append(
                    webFacetDataModel.getStringProperty( IJ2EEModuleFacetInstallDataModelProperties.CONFIG_FOLDER ) );
        }
        else
        {
            IVirtualComponent component = ComponentCore.createComponent( this.project );
            if( component != null )
            {
                webrootFullPath = component.getRootFolder().getUnderlyingFolder().getFullPath();
            }
        }

        return ResourcesPlugin.getWorkspace().getRoot().getFolder( webrootFullPath );
    }

    protected void installPluginLibraryDelegate() throws CoreException
    {
        LibraryInstallDelegate libraryDelegate =
            (LibraryInstallDelegate) this.model.getProperty( IPluginProjectDataModelProperties.LIFERAY_PLUGIN_LIBRARY_DELEGATE );

        libraryDelegate.execute( monitor );
    }

    protected boolean isProjectInSDK()
    {
        return masterModel.getBooleanProperty( LIFERAY_USE_SDK_LOCATION );
    }

    protected void processNewFiles( IPath path ) throws CoreException
    {
        try
        {
            List<File> newFiles = FileListing.getFileListing( path.toFile() );

            for( File file : newFiles )
            {
                try
                {
                    copyToProject( path, file );
                }
                catch( Exception e )
                {
                    ProjectCore.logError( e );
                }
            }
        }
        catch( FileNotFoundException e1 )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e1 ) );
        }
    }

//    protected boolean promptForOverwrite( final IResource projectEntryPath )
//    {
//        final boolean[] retval = new boolean[1];
//
//        PlatformUI.getWorkbench().getDisplay().syncExec( new Runnable()
//        {
//            public void run()
//            {
//                retval[0] =
//                    MessageDialog.openQuestion(
//                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Overwrite project file?",
//                        "Overwrite project file: " + projectEntryPath.getLocation() );
//            }
//
//        } );
//
//        return retval[0];
//    }

    protected void setupDefaultOutputLocation() throws CoreException
    {
        IJavaProject jProject = JavaCore.create( this.project );
        IFolder folder = this.project.getFolder( getDefaultOutputLocation() );

        if( folder.getParent().exists() )
        {
            CoreUtil.prepareFolder( folder );

            IPath oldOutputLocation = jProject.getOutputLocation();
            IFolder oldOutputFolder = CoreUtil.getWorkspaceRoot().getFolder( oldOutputLocation );
            jProject.setOutputLocation( folder.getFullPath(), null );

            try
            {
                if( !folder.equals( oldOutputFolder ) && oldOutputFolder.exists() )
                {
                    IContainer outputParent = oldOutputFolder.getParent();
                    oldOutputFolder.delete( true, null );

                    if( outputParent.members().length == 0 && outputParent.getName().equals( "build" ) ) //$NON-NLS-1$
                    {
                        outputParent.delete( true, null );
                    }
                }
            }
            catch( Exception e )
            {
                // best effort
            }
        }
    }

    protected boolean shouldConfigureDeploymentAssembly()
    {
        return this.model.getBooleanProperty( CONFIGURE_DEPLOYMENT_ASSEMBLY );
    }

    protected boolean shouldCopyToProject( File file )
    {
        if( isProjectInSDK() )
        {
            return true;
        }

        for( String name : ISDKConstants.PORTLET_PLUGIN_ZIP_IGNORE_FILES )
        {
            if( file.getName().equals( name ) )
            {
                return false;
            }
        }

        return true;
    }

    protected boolean shouldInstallPluginLibraryDelegate()
    {
        return this.model.getBooleanProperty( INSTALL_LIFERAY_PLUGIN_LIBRARY_DELEGATE );
    }

    protected boolean shouldSetupDefaultOutputLocation()
    {
        return this.model.getBooleanProperty( SETUP_DEFAULT_OUTPUT_LOCATION );
    }

    protected boolean shouldSetupExtClasspath()
    {
        return this.model.getBooleanProperty( SETUP_EXT_CLASSPATH );
    }
}
