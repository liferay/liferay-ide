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

package com.liferay.ide.hook.ui.editor;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.model.CustomJsp;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.model.Hook6xx;
import com.liferay.ide.hook.ui.HookUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.PropertyContentEvent;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class HookXmlEditor extends SapphireEditorForXml
{

    public static final String ID = "com.liferay.ide.eclipse.hook.ui.editor.HookXmlEditor";//$NON-NLS-1$

    protected boolean customModelDirty = false;

    private boolean ignoreCustomModelChanges;

    /**
	 *
	 */
    public HookXmlEditor()
    {
        super
        (
            Hook6xx.TYPE,
            DefinitionLoader
                .sdef( HookXmlEditor.class )
                .page( "HookConfigurationPage" )
        );
    }

    @Override
    protected void adaptModel( final IModelElement model )
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
        model.attach( listener, "CustomJsps/*" ); //$NON-NLS-1$
        this.ignoreCustomModelChanges = false;
    }

    private void copyCustomJspsToProject( ModelElementList<CustomJsp> customJsps )
    {
        try
        {
            CustomJspDir customJspDirElement = this.getModelElement().nearest( Hook.class ).getCustomJspDir().element();

            if( customJspDirElement != null && customJspDirElement.validation().ok() )
            {
                Path customJspDir = customJspDirElement.getValue().getContent();
                IFolder defaultDocroot = CoreUtil.getDefaultDocrootFolder( getProject() );
                IFolder customJspFolder = defaultDocroot.getFolder( customJspDir.toPortableString() );

                final ILiferayProject liferayProject = LiferayCore.create( getProject() );
                final IPath portalDir = liferayProject.getAppServerPortalDir();

                for( CustomJsp customJsp : customJsps )
                {
                    String content = customJsp.getValue().getContent();

                    if( !empty( content ) )
                    {
                        IFile customJspFile = customJspFolder.getFile( content );

                        if( !customJspFile.exists() )
                        {
                            IPath portalJsp = portalDir.append( content );

                            try
                            {
                                CoreUtil.makeFolders( (IFolder) customJspFile.getParent() );
                                customJspFile.create( new FileInputStream( portalJsp.toFile() ), true, null );
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
            ModelElementList<CustomJsp> customJsps = getModelElement().nearest( Hook.class ).getCustomJsps();

            copyCustomJspsToProject( customJsps );

            this.customModelDirty = false;

            super.doSave( monitor );

            this.firePropertyChange( IEditorPart.PROP_DIRTY );
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

}
