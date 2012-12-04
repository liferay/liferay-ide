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
 *******************************************************************************/

package com.liferay.ide.core.util;

import com.liferay.ide.core.CorePlugin;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for helping edit XML files in user projects.
 *
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class DescriptorHelper
{

    public abstract class DOMModelEditOperation extends DOMModelOperation
    {
        public DOMModelEditOperation( IFile descriptorFile )
        {
            super( descriptorFile );
        }

        public void createDefaultDescriptor( String templateString, String version )
        {
            String content = MessageFormat.format( templateString, version, version.replace( '.', '_' ) );

            try
            {
                this.file.create( new ByteArrayInputStream( content.getBytes( "UTF-8" ) ), IResource.FORCE, null ); //$NON-NLS-1$
            }
            catch( Exception e )
            {
                CorePlugin.logError( e );
            }
        }

        protected abstract void createDefaultFile();

        public IStatus execute()
        {
            IStatus retval = null;

            if( !this.file.exists() )
            {
                createDefaultFile();
            }

            IDOMModel domModel = null;

            try
            {
                domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( this.file );

                domModel.aboutToChangeModel();

                IDOMDocument document = domModel.getDocument();

                retval = doExecute( document );

                domModel.changedModel();

                domModel.save();
            }
            catch( Exception e )
            {
                retval = CorePlugin.createErrorStatus( e );
            }
            finally
            {
                if( domModel != null )
                {
                    domModel.releaseFromEdit();
                }
            }

            return retval;
        }
    }

    protected static abstract class DOMModelOperation
    {
        protected IFile file;

        public DOMModelOperation( IFile descriptorFile )
        {
            this.file = descriptorFile;
        }

        protected abstract IStatus doExecute( IDOMDocument document );

        public abstract IStatus execute();
    }

    protected abstract class DOMModelReadOperation extends DOMModelOperation
    {
        public DOMModelReadOperation( IFile descriptorFile )
        {
            super( descriptorFile );
        }

        public IStatus execute()
        {
            IStatus retval = null;

            if( !this.file.exists() )
            {
                return CorePlugin.createErrorStatus( this.file.getName() + " doesn't exist" ); //$NON-NLS-1$
            }

            IDOMModel domModel = null;

            try
            {
                domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead( this.file );

                IDOMDocument document = domModel.getDocument();

                retval = doExecute( document );
            }
            catch( Exception e )
            {
                retval = CorePlugin.createErrorStatus( e );
            }
            finally
            {
                if( domModel != null )
                {
                    domModel.releaseFromRead();
                }
            }

            return retval;
        }
    }

    public static Element appendChildElement( Element parentElement, String newElementName )
    {
        return appendChildElement( parentElement, newElementName, null );
    }

    public static Element appendChildElement( Element parentElement, String newElementName, String initialTextContent )
    {
        Element newChildElement = null;

        if( parentElement != null && newElementName != null )
        {
            Document ownerDocument = parentElement.getOwnerDocument();

            newChildElement = ownerDocument.createElement( newElementName );

            if( initialTextContent != null )
            {
                newChildElement.appendChild( ownerDocument.createTextNode( initialTextContent ) );
            }

            parentElement.appendChild( newChildElement );
        }

        return newChildElement;
    }

    public static Node appendTextNode( Element parentElement, String initialTextContent )
    {
        Node newChildElement = null;

        if( parentElement != null )
        {
            Document ownerDocument = parentElement.getOwnerDocument();

            newChildElement = ownerDocument.createTextNode( initialTextContent );

            parentElement.appendChild( newChildElement );
        }

        return newChildElement;
    }

    public static Element findChildElement( Element parentElement, String elementName )
    {
        Element retval = null;

        if( parentElement == null )
        {
            return retval;
        }

        NodeList children = parentElement.getChildNodes();

        for( int i = 0; i < children.getLength(); i++ )
        {
            Node child = children.item( i );

            if( child instanceof Element && child.getNodeName().equals( elementName ) )
            {
                retval = (Element) child;
                break;
            }
        }

        return retval;
    }

    public static IFile getDescriptorFile( IProject project, String fileName )
    {
        IVirtualFolder docroot = CoreUtil.getDocroot( project );

        if( docroot != null )
        {
            for( IContainer container : docroot.getUnderlyingFolders() )
            {
                if( container != null && container.exists() )
                {
                    IFolder webInf = container.getFolder( new Path( "WEB-INF" ) ); //$NON-NLS-1$

                    if( webInf.exists() )
                    {
                        return webInf.getFile( fileName );
                    }
                }
            }
        }

        return null;
    }

    public static Element insertChildElement(
        Element parentElement, Node refNode, String newElementName, String initialTextContent )
    {
        Element newChildElement = null;

        if( parentElement != null && newElementName != null )
        {
            Document ownerDocument = parentElement.getOwnerDocument();

            newChildElement = ownerDocument.createElement( newElementName );

            if( initialTextContent != null )
            {
                newChildElement.appendChild( ownerDocument.createTextNode( initialTextContent ) );
            }

            parentElement.insertBefore( newChildElement, refNode );
        }

        return newChildElement;
    }

    public static void removeChildren( Node node )
    {
        if( node == null || node.getChildNodes() == null || node.getChildNodes().getLength() <= 0 )
        {
            return;
        }

        NodeList children = node.getChildNodes();

        for( int i = 0; i < children.getLength(); i++ )
        {
            node.removeChild( children.item( i ) );
        }
    }

    protected String descriptorPath;

    protected IProject project;

    public DescriptorHelper( IProject project )
    {
        this.project = project;
    }

    public List<Element> getChildElements( Element parent )
    {
        List<Element> retval = new ArrayList<Element>();

        if( parent != null )
        {
            NodeList children = parent.getChildNodes();

            for( int i = 0; i < children.getLength(); i++ )
            {
                Node child = children.item( i );

                if( child instanceof Element )
                {
                    retval.add( (Element) child );
                }
            }
        }

        return retval;
    }

    protected IFile getDescriptorFile( String fileName )
    {
        return getDescriptorFile( project, fileName );
    }

    public String getDescriptorPath()
    {
        return this.descriptorPath;
    }

    protected IProject getProject()
    {
        return project;
    }

    public void setDescriptorPath( String path )
    {
        this.descriptorPath = path;
    }
}
