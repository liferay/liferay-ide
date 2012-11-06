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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.service.ui;

import com.liferay.ide.service.core.model.ServiceBuilder600;
import com.liferay.ide.service.core.model.ServiceBuilder610;
import com.liferay.ide.service.core.model.ServiceBuilderVersionType;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 * @author Gregory Amerson
 */
public class ServiceBuilderEditor extends SapphireEditorForXml
{
    private SapphireDiagramEditor pageDiagram;

    public ServiceBuilderEditor()
    {
        super( ServiceUI.PLUGIN_ID );

        setEditorDefinitionPath( ServiceUI.PLUGIN_ID +
            "/com/liferay/ide/service/ui/ServiceBuilderEditor.sdef/serviceBuilderPage" );
    }

    @Override
    protected void createDiagramPages() throws PartInitException
    {
        IPath path =
            new Path( ServiceUI.PLUGIN_ID + "/com/liferay/ide/service/ui/ServiceBuilderEditor.sdef/diagramPage" );
        this.pageDiagram = new SapphireDiagramEditor( this, this.getModelElement(), path );
        addEditorPage( 0, this.pageDiagram );
    }

    @Override
    protected IModelElement createModel()
    {
        RootXmlResource resource = null;

        try
        {
            InputStream editorContents = getFileContents();
            ServiceBuilderVersionType dtdVersion = null;

            resource = new RootXmlResource( new XmlResourceStore( editorContents ) );
            Document document = resource.getDomDocument();
            dtdVersion = getDTDVersion( document );

            if( document != null )
            {
                switch( dtdVersion )
                {
                    case v6_0_0:
                        setRootModelElementType( ServiceBuilder600.TYPE );
                        break;

                    case v6_1_0:
                    default:
                        setRootModelElementType( ServiceBuilder610.TYPE );
                        break;
                }
            }
        }
        catch( Exception e )
        {
            ServiceUI.logError( e );
            setRootModelElementType( ServiceBuilder600.TYPE );
        }
        finally
        {
            if( resource != null )
            {
                resource.dispose();
            }
        }

        return super.createModel();
    }

    @Override
    public void doSave( final IProgressMonitor monitor )
    {
        super.doSave( monitor );

        this.pageDiagram.doSave( monitor );
    }

    protected ServiceBuilderVersionType getDTDVersion( Document document )
    {
        ServiceBuilderVersionType dtdVersion = null;
        DocumentType docType = document.getDoctype();

        if( docType != null )
        {
            String publicId = docType.getPublicId();
            String systemId = docType.getSystemId();
            if( publicId != null && systemId != null )
            {
                if( publicId.contains( "6.0.0" ) || systemId.contains( "6.0.0" ) )
                {
                    dtdVersion = ServiceBuilderVersionType.v6_0_0;
                }
                else if( publicId.contains( "6.1.0" ) || systemId.contains( "6.1.0" ) )
                {
                    dtdVersion = ServiceBuilderVersionType.v6_1_0;
                }
            }

        }

        return dtdVersion;
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
}
