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

package com.liferay.ide.hook.core.operation;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.StringBufferOutputStream;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.hook.core.dd.HookDescriptorHelper;
import com.liferay.ide.hook.core.util.HookUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 * @author Simon Jiang
 * @author Terry Jia
 * @author Andy Wu
 */
@SuppressWarnings( { "restriction", "unchecked" } )
public class AddHookOperation extends AbstractDataModelOperation implements INewHookDataModelProperties
{

    public AddHookOperation( IDataModel model )
    {
        super( model );
    }

    @Override
    public IStatus execute( IProgressMonitor monitor, IAdaptable info ) throws ExecutionException
    {
        final IDataModel dm = getDataModel();

        IStatus retval = null;

        final IStatus status = checkDescriptorFile( getTargetProject() );

        if( !status.isOK() )
        {
            return status;
        }

        if( dm.getBooleanProperty( CREATE_CUSTOM_JSPS ) )
        {
            retval = createCustomJSPs( dm );
        }

        if( dm.getBooleanProperty( CREATE_PORTAL_PROPERTIES ) )
        {
            retval = createPortalProperties( dm );
        }

        if( dm.getBooleanProperty( CREATE_SERVICES ) )
        {
            retval = createServices( dm );
        }

        if( dm.getBooleanProperty( CREATE_LANGUAGE_PROPERTIES ) )
        {
            retval = createLanguageProperties( dm );
        }

        return retval;
    }

    public IProject getTargetProject()
    {
        String projectName = model.getStringProperty( PROJECT_NAME );

        return ProjectUtil.getProject( projectName );
    }

    protected IStatus checkDescriptorFile( IProject project )
    {
        final IWebProject webproject = LiferayCore.create( IWebProject.class, project );

        if( webproject == null || webproject.getDefaultDocrootFolder() == null )
        {
            return HookCore.createErrorStatus( "Could not find webapp root folder." ); //$NON-NLS-1$
        }

        final IFolder webappRoot = webproject.getDefaultDocrootFolder();

        // IDE-648 IDE-110
        final Path path = new Path( "WEB-INF/" + ILiferayConstants.LIFERAY_HOOK_XML_FILE ); //$NON-NLS-1$
        final IFile hookDescriptorFile = webappRoot.getFile( path );

        if( ! hookDescriptorFile.exists() )
        {
            try
            {
                createDefaultHookDescriptorFile( hookDescriptorFile );
            }
            catch( Exception ex )
            {
                return HookCore.createErrorStatus( ex );
            }
        }

        return Status.OK_STATUS;
    }

    protected IFile copyPortalJSPToProject( IPath portalDir, String portalJsp, IFolder customFolder )
        throws Exception
    {
        final IPath portalJspPath = new Path( portalJsp );

        final IPath originalPortalJspPath = portalDir.append( portalJsp );

        final IFile newJspFile = customFolder.getFile( portalJspPath );

        CoreUtil.prepareFolder( (IFolder) newJspFile.getParent() );

        if ( !newJspFile.getLocation().toFile().exists() )
        {
            if( originalPortalJspPath.toFile().exists() )
            {
                final InputStream fis = Files.newInputStream( originalPortalJspPath.toFile().toPath() );

                if( newJspFile.exists() )
                {
                    newJspFile.setContents( fis, IResource.FORCE, null );
                }
                else
                {
                    newJspFile.create( fis, true, null );
                }
            }
            else
            {
                CoreUtil.createEmptyFile( newJspFile );
            }
        }

        return newJspFile;
    }

    protected IStatus createCustomJSPs( IDataModel dm )
    {
        IProject project = getTargetProject();

        IFolder defaultWebappRootFolder = LiferayCore.create( IWebProject.class, project ).getDefaultDocrootFolder();

        String customJSPsFolder = dm.getStringProperty( CUSTOM_JSPS_FOLDER );

        String customFolderValue = defaultWebappRootFolder.getFullPath().append( customJSPsFolder ).toPortableString();

        IFolder customFolder = (IFolder) project.getWorkspace().getRoot().getFolder( new Path( customFolderValue ) );

        try
        {
            CoreUtil.prepareFolder( customFolder );
        }
        catch( CoreException e )
        {
            return HookCore.createErrorStatus( e );
        }

        final List<String[]> customJsps = (List<String[]>) dm.getProperty( CUSTOM_JSPS_ITEMS );
        final ILiferayProject liferayProject = LiferayCore.create( getTargetProject() );

        final ILiferayPortal portal = liferayProject.adapt( ILiferayPortal.class );

        IStatus status = null;

        if( portal != null )
        {
            final IPath portalDir = portal.getAppServerPortalDir();

            if( customJsps != null && portalDir != null )
            {
                for( String[] customJsp : customJsps )
                {
                    try
                    {
                        IFile copiedFile = copyPortalJSPToProject( portalDir, customJsp[0], customFolder );

                        if( copiedFile != null )
                        {
                            Set<IFile> jspFilesCreated = (Set<IFile>) dm.getProperty( CUSTOM_JSPS_FILES_CREATED );

                            jspFilesCreated.add( copiedFile );

                            dm.setProperty( CUSTOM_JSPS_FILES_CREATED, jspFilesCreated );
                        }
                    }
                    catch( Exception e )
                    {
                        HookCore.logError( e );
                    }
                }
            }

            HookDescriptorHelper hookDescHelper = new HookDescriptorHelper( getTargetProject() );

            status = hookDescHelper.setCustomJSPDir( this.model );

            if( this.model.getBooleanProperty( DISABLE_CUSTOM_JSP_FOLDER_VALIDATION ) )
            {
                HookUtil.configureJSPSyntaxValidationExclude( getTargetProject(), customFolder, true );
            }
        }
        else
        {
            status = HookCore.createErrorStatus( "Could not get portal info from project " + project.getName() );
        }

        return status;
    }

    protected void createDefaultHookDescriptorFile( IFile hookDescriptorFile ) throws UnsupportedEncodingException,
        CoreException, BadLocationException, TemplateException
    {
        HookDescriptorHelper helper = new HookDescriptorHelper( getTargetProject() );
        helper.createDefaultDescriptor();
    }

    protected IStatus createLanguageProperties( IDataModel dm )
    {
        IProject project = getTargetProject();

        String contentFolderValue = dm.getStringProperty( CONTENT_FOLDER );

        IFolder contentFolder = (IFolder) project.getWorkspace().getRoot().getFolder( new Path( contentFolderValue ) );

        try
        {
            CoreUtil.prepareFolder( contentFolder );
        }
        catch( CoreException e )
        {
            return HookCore.createErrorStatus( e );
        }

        List<String[]> languagePropertiesFiles = (List<String[]>) dm.getProperty( LANGUAGE_PROPERTIES_ITEMS );

        if( languagePropertiesFiles != null )
        {
            for( String[] languagePropertyFile : languagePropertiesFiles )
            {
                try
                {
                    if( !languagePropertyFile[0].contains( "*" ) )
                    {
                        IFile createdFile =
                            ProjectUtil.createEmptyProjectFile( languagePropertyFile[0], contentFolder );

                        if( createdFile != null )
                        {
                            Set<IFile> languageFilesCreated =
                                (Set<IFile>) dm.getProperty( LANGUAGE_PROPERTIES_FILES_CREATED );

                            languageFilesCreated.add( createdFile );

                            dm.setProperty( LANGUAGE_PROPERTIES_FILES_CREATED, languageFilesCreated );
                        }
                    }
                }
                catch( Exception e )
                {
                    HookCore.logError( e );
                }
            }
        }

        HookDescriptorHelper hookDescHelper = new HookDescriptorHelper( getTargetProject() );

        Set<IFile> languageFilesCreated = (Set<IFile>) dm.getProperty( LANGUAGE_PROPERTIES_FILES_CREATED );

        // need to get file paths relative to package root
        List<String> languageProperties = new ArrayList<String>();

        IPackageFragmentRoot packRoot =
            (IPackageFragmentRoot) model.getProperty( INewJavaClassDataModelProperties.JAVA_PACKAGE_FRAGMENT_ROOT );

        if( packRoot != null )
        {
            for( IFile languageFile : languageFilesCreated )
            {
                if( packRoot.getPath().isPrefixOf( languageFile.getFullPath() ) )
                {
                    String languageProperty =
                        languageFile.getFullPath().makeRelativeTo( packRoot.getPath() ).toPortableString();

                    languageProperties.add( languageProperty );
                }
            }
        }

        IStatus status = hookDescHelper.addLanguageProperties( languageProperties );

        return status;

    }

    protected IStatus createPortalProperties( IDataModel dm )
    {
        IProject project = getTargetProject();

        String portalPropertiesFile = dm.getStringProperty( PORTAL_PROPERTIES_FILE );

        // check to see if we have an existing file to read in
        IPath portalPropertiesPath = new Path( portalPropertiesFile );

        IPath propertiesFilesPath = portalPropertiesPath.makeRelativeTo( project.getFullPath() );

        IFile propertiesFile = project.getFile( propertiesFilesPath );

        Properties properties = new Properties();

        if( propertiesFile.exists() )
        {
            try
            {
                properties.load( propertiesFile.getContents() );
            }
            catch( Exception e )
            {
                return HookCore.createErrorStatus( e );
            }
        }

        List<String[]> actionItems = (List<String[]>) dm.getProperty( PORTAL_PROPERTIES_ACTION_ITEMS );

        if( actionItems != null )
        {
            for( String[] actionItem : actionItems )
            {
                properties.put( actionItem[0], actionItem[1] );
            }
        }

        List<String[]> overrideItems = (List<String[]>) dm.getProperty( PORTAL_PROPERTIES_OVERRIDE_ITEMS );

        if( overrideItems != null )
        {
            for( String[] overrideItem : overrideItems )
            {
                properties.put( overrideItem[0], overrideItem[1] );
            }
        }

        StringBufferOutputStream buffer = new StringBufferOutputStream();

        try
        {
            properties.store( buffer, StringPool.EMPTY );
        }
        catch( IOException e )
        {
            return HookCore.createErrorStatus( e );
        }

        try
        {
            ByteArrayInputStream bis = new ByteArrayInputStream( buffer.toString().getBytes( "UTF-8" ) ); //$NON-NLS-1$

            if( propertiesFile.exists() )
            {
                propertiesFile.setContents( bis, IResource.FORCE, null );
            }
            else
            {
                CoreUtil.prepareFolder( (IFolder) propertiesFile.getParent() );

                propertiesFile.create( bis, true, null );
            }
        }
        catch( Exception e )
        {
            return HookCore.createErrorStatus( e );
        }

        HookDescriptorHelper hookDescHelper = new HookDescriptorHelper( getTargetProject() );

        String propertiesClasspath = null;

        IPackageFragmentRoot packRoot =
            (IPackageFragmentRoot) model.getProperty( INewJavaClassDataModelProperties.JAVA_PACKAGE_FRAGMENT_ROOT );

        if( packRoot != null )
        {
            if( packRoot.getPath().isPrefixOf( propertiesFile.getFullPath() ) )
            {
                propertiesClasspath =
                    propertiesFile.getFullPath().makeRelativeTo( packRoot.getPath() ).toPortableString();
            }
        }

        IStatus status = hookDescHelper.setPortalProperties( this.model, propertiesClasspath );

        return status;
    }

    protected IStatus createServices( IDataModel dm )
    {
        List<String[]> actionItems = (List<String[]>) dm.getProperty( SERVICES_ITEMS );

        HookDescriptorHelper hookDescHelper = new HookDescriptorHelper( getTargetProject() );

        IStatus status = hookDescHelper.addActionItems( actionItems );

        return status;
    }

}
