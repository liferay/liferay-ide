/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * Contributors:
 * 		Kamesh Sampath - initial implementation
 * 		Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.hook.ui.editor;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.model.CustomJsp;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.model.Hook600;
import com.liferay.ide.hook.core.model.Hook610;
import com.liferay.ide.hook.core.model.HookVersionType;
import com.liferay.ide.hook.core.util.HookUtil;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

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
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public class HookXmlEditor extends SapphireEditorForXml
{

    private static final String EDITOR_DEFINITION_PATH =
        "com.liferay.ide.hook.ui/com/liferay/ide/hook/ui/editor/hook-editor.sdef/HookConfigurationPage"; //$NON-NLS-1$

    public static final String ID = "com.liferay.ide.eclipse.hook.ui.editor.HookXmlEditor"; //$NON-NLS-1$

    protected boolean customModelDirty = false;

    private boolean ignoreCustomModelChanges;

    /**
	 *
	 */
    public HookXmlEditor()
    {
        super( ID );

        setEditorDefinitionPath( EDITOR_DEFINITION_PATH );
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

                ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( getProject() );
                IPath portalDir = liferayRuntime.getPortalDir();

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
        catch( CoreException e )
        {
            HookUI.logError( e );
        }
    }

    @Override
    protected IModelElement createModel()
    {
        RootXmlResource resource = null;
        HookVersionType dtdVersion = null;

        try
        {
            InputStream editorContents = getFileContents();

            resource = new RootXmlResource( new XmlResourceStore( editorContents ) );
            Document document = resource.getDomDocument();
            dtdVersion = HookUtil.getDTDVersion( document );

            if( document != null )
            {
                switch( dtdVersion )
                {
                    case v6_0_0:
                        setRootModelElementType( Hook600.TYPE );
                        break;

                    case v6_1_0:
                    default:
                        setRootModelElementType( Hook610.TYPE );
                        break;

                }
            }
        }
        catch( Exception e )
        {
            HookUI.logError( e );
            setRootModelElementType( Hook610.TYPE );
        }
        finally
        {
            if( resource != null )
            {
                resource.dispose();
            }
        }

        IModelElement modelElement = super.createModel();

        if( dtdVersion != null )
        {
            Hook hookModel = (Hook) modelElement;

            hookModel.setVersion( dtdVersion );
        }

        Listener listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            public void handleTypedEvent( final PropertyContentEvent event )
            {
                handleCustomJspsPropertyChangedEvent( event );
            }
        };

        this.ignoreCustomModelChanges = true;
        modelElement.attach( listener, "CustomJsps/*" ); //$NON-NLS-1$
        this.ignoreCustomModelChanges = false;

        return modelElement;
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
