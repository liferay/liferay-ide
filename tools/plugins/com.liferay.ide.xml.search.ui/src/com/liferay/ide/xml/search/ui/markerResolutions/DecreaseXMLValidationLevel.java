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

package com.liferay.ide.xml.search.ui.markerResolutions;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.util.ComponentUtil;
import com.liferay.ide.ui.LiferayUIPlugin;
import com.liferay.ide.xml.search.ui.LiferayXMLSearchUI;
import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public abstract class DecreaseXMLValidationLevel implements IMarkerResolution2
{

    public DecreaseXMLValidationLevel()
    {
    }

    @Override
    public String getDescription()
    {
        return null;
    }

    @Override
    public Image getImage()
    {
        final URL url = LiferayXMLSearchUI.getDefault().getBundle().getEntry( "/icons/arrow_down.png" );
        return ImageDescriptor.createFromURL( url ).createImage();
    }

    @Override
    public String getLabel()
    {
        return null;
    }

    protected void resetContent( IMarker marker )
    {
        IFile file = (IFile) marker.getResource();

        IDOMModel domModel = null;
        try
        {
            if( marker.getType().equals( XMLSearchConstants.LIFERAY_JSP_MARKER_ID ) )
            {
                String content = (String) marker.getAttribute( XMLSearchConstants.TEXT_CONTENT );
                int nodeType = (int) marker.getAttribute( XMLSearchConstants.NODE_TYPE );
                String elementName = (String) marker.getAttribute( XMLSearchConstants.ELEMENT_NAME );
                int startOffset = (int) marker.getAttribute( XMLSearchConstants.START_OFFSET );

                IDOMElement element = null;

                domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( file );
                IDOMDocument document = domModel.getDocument();
                NodeList elements = document.getElementsByTagName( elementName );

                for( int i = 0; i < elements.getLength(); i++ )
                {
                    IDOMElement e = (IDOMElement) elements.item( i );
                    if( e.getStartOffset() == startOffset )
                    {
                        element = e;
                        break;
                    }
                }

                if( element != null )
                {
                    // remember if the editor is dirty, if not, then need to save it after reseting the content
                    boolean isEditorDirty = LiferayUIPlugin.getActivePage().getActiveEditor().isDirty();

                    // TODO, in Liferay jsp file, we don't have any xml reference from element content
                    // current references are all from attribute values, so the element type node hasn't been tested
                    // Be careful when you use it
                    if( Node.ELEMENT_NODE == nodeType )
                    {
                        element.setNodeValue( StringPool.EMPTY );
                        element.setNodeValue( content );
                    }
                    else if( Node.ATTRIBUTE_NODE == nodeType )
                    {
                        String attrName = (String) marker.getAttribute( XMLSearchConstants.ATTR_NAME );

                        if( attrName != null )
                        {
                            Attr attr = element.getAttributeNode( attrName );
                            attr.setNodeValue( StringPool.EMPTY );
                            attr.setNodeValue( content );
                        }
                    }

                    ComponentUtil.validateFile( (IFile) marker.getResource(), new NullProgressMonitor() );

                    if( !isEditorDirty )
                    {
                        IWorkbenchPage activePage = LiferayUIPlugin.getActivePage();
                        IEditorPart activeEditor = activePage.getActiveEditor();
                        activePage.saveEditor( activeEditor, false );
                    }
                }
            }
        }
        catch( IOException | CoreException e )
        {
            LiferayXMLSearchUI.logError( e );
        }
        finally
        {
            domModel.releaseFromEdit();
        }
    }

    protected abstract void resolve( IMarker marker );

    @Override
    public void run( IMarker marker )
    {
        resolve( marker );

        ComponentUtil.validateFile( (IFile) marker.getResource(), new NullProgressMonitor() );

        // apply the resolution from quick assist need to reset the content to trigger to refresh the editor
        if( marker.getAttribute( XMLSearchConstants.FOR_JSP_QUICK_ASSIST, false ) )
        {
            resetContent( marker );
        }
    }

}
