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

package com.liferay.ide.hook.ui.editor;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.model.CustomJsp;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.model.Hook6xx;
import com.liferay.ide.hook.core.util.HookUtil;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.ui.util.UIUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class HookXmlEditor extends SapphireEditorForXml
{
    protected boolean customModelDirty = false;

    private boolean ignoreCustomModelChanges;

    public HookXmlEditor()
    {
        super( Hook6xx.ELEMENT_TYPE, null );
    }

    @Override
    protected void adaptModel( final Element model )
    {
        super.adaptModel( model );

        Listener listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            public void handleTypedEvent( final PropertyContentEvent event )
            {
                handleCustomJspsPropertyChangedEvent( event );
            }
        };

        this.ignoreCustomModelChanges = true;
        model.attach( listener, Hook.PROP_CUSTOM_JSPS.name() + "/*" ); //$NON-NLS-1$
        this.ignoreCustomModelChanges = false;
    }

    @Override
    protected void createFormPages() throws PartInitException
    {
        addDeferredPage( 1, "Overview", "HookConfigurationPage" );
    }

    private void configureCustomJspValidation( final IProject project, final String customerJspPath )
    {
        final IFolder docFolder = CoreUtil.getDefaultDocrootFolder( project );

        if( docFolder != null )
        {
            final IPath newPath = org.eclipse.core.runtime.Path.fromOSString( customerJspPath );

            final IPath pathValue = docFolder.getFullPath().append( newPath );

            final IFolder customJspFolder = project.getFolder( pathValue.makeRelativeTo( project.getFullPath() ) );

            boolean needAddCustomJspValidation =
                HookUtil.configureJSPSyntaxValidationExclude( project, customJspFolder, false );

            if( !needAddCustomJspValidation )
            {
                UIUtil.async( new Runnable()
                {

                    public void run()
                    {
                        final boolean addDisableCustomJspValidation =
                            MessageDialog.openQuestion(
                                UIUtil.getActiveShell(), Msgs.disableCustomValidationTitle,
                                Msgs.disableCustomValidationMsg );

                        if( addDisableCustomJspValidation )
                        {
                            new WorkspaceJob( " disable custom jsp validation for " + project.getName() )
                            {

                                @Override
                                public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                                {
                                    HookUtil.configureJSPSyntaxValidationExclude(
                                        project, customJspFolder, true );
                                    project.build( IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor() );

                                    return Status.OK_STATUS;
                                }
                            }.schedule();
                        }
                    }
                } );
            }
        }
    }

    private void copyCustomJspsToProject( IPath portalDir, ElementList<CustomJsp> customJsps )
    {
        try
        {
            CustomJspDir customJspDirElement = this.getModelElement().nearest( Hook.class ).getCustomJspDir().content();

            if( customJspDirElement != null && customJspDirElement.validation().ok() )
            {
                Path customJspDir = customJspDirElement.getValue().content();
                final IWebProject webproject = LiferayCore.create( IWebProject.class, getProject() );

                if( webproject != null )
                {
                    IFolder defaultDocroot = webproject.getDefaultDocrootFolder();
                    IFolder customJspFolder = defaultDocroot.getFolder( customJspDir.toPortableString() );

                    for( CustomJsp customJsp : customJsps )
                    {
                        String content = customJsp.getValue().content();

                        if( !empty( content ) )
                        {
                            IFile customJspFile = customJspFolder.getFile( content );

                            if( !customJspFile.exists() )
                            {
                                IPath portalJsp = portalDir.append( content );

                                try
                                {
                                    CoreUtil.makeFolders( (IFolder) customJspFile.getParent() );

                                    if( portalJsp.toFile().exists() )
                                    {
                                        customJspFile.create(
                                            Files.newInputStream( portalJsp.toFile().toPath() ), true, null );
                                    }
                                    else
                                    {
                                        CoreUtil.createEmptyFile( customJspFile );
                                    }
                                }
                                catch( Exception e )
                                {
                                    HookUI.logError( e );
                                }
                            }
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            HookUI.logError( e );
        }
    }

    @Override
    public void doSave( IProgressMonitor monitor )
    {
        if( this.customModelDirty )
        {
            final Hook hook = getModelElement().nearest( Hook.class );
            final ElementList<CustomJsp> customJsps = hook.getCustomJsps();

            final ILiferayProject liferayProject = LiferayCore.create( getProject() );
            final ILiferayPortal portal = liferayProject.adapt( ILiferayPortal.class );

            if( portal != null )
            {
                final IPath portalDir = portal.getAppServerPortalDir();

                if( portalDir != null )
                {
                    copyCustomJspsToProject( portalDir, customJsps );
                }
            }

            this.customModelDirty = false;

            super.doSave( monitor );

            this.firePropertyChange( IEditorPart.PROP_DIRTY );

            ElementHandle<CustomJspDir> customJspDir = hook.getCustomJspDir();

            if( customJspDir != null && !customJspDir.empty() )
            {
                Value<Path> customJspPath = customJspDir.content().getValue();
                final String customeJspValue = customJspPath.content().makeRelative().toPortableString();
                configureCustomJspValidation( getProject(), customeJspValue );
            }
        }
        else
        {
            super.doSave( monitor );
        }
    }

    public InputStream getFileContents() throws CoreException, MalformedURLException, IOException
    {
        final IEditorInput editorInput = getEditorInput();

        if( editorInput instanceof FileEditorInput )
        {
            return ( (FileEditorInput) editorInput ).getFile().getContents();
        }
        else if( editorInput instanceof IStorageEditorInput )
        {
            return ( (IStorageEditorInput) editorInput ).getStorage().getContents();
        }
        else if( editorInput instanceof FileStoreEditorInput )
        {
            return ( (FileStoreEditorInput) editorInput ).getURI().toURL().openStream();
        }
        else
        {
            return null;
        }
    }

    protected void handleCustomJspsPropertyChangedEvent( final PropertyContentEvent event )
    {
        if( this.ignoreCustomModelChanges )
        {
            return;
        }

        this.customModelDirty = true;
        this.firePropertyChange( IEditorPart.PROP_DIRTY );
    }

    @Override
    public boolean isDirty()
    {
        if( this.customModelDirty )
        {
            return true;
        }

        return super.isDirty();
    }

    @Override
    protected void pageChange( int pageIndex )
    {
        this.ignoreCustomModelChanges = true;
        super.pageChange( pageIndex );
        this.ignoreCustomModelChanges = false;
    }

    private static class Msgs extends NLS
    {
        public static String disableCustomValidationMsg;
        public static String disableCustomValidationTitle;

        static
        {
            initializeMessages( HookXmlEditor.class.getName(), Msgs.class );
        }
    }

}
