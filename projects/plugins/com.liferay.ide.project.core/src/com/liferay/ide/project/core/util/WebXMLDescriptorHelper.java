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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.DescriptorHelper;
import com.liferay.ide.core.util.NodeUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jst.j2ee.jsp.JSPConfig;
import org.eclipse.jst.j2ee.jsp.JspFactory;
import org.eclipse.jst.j2ee.jsp.TagLibRefType;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.jst.j2ee.webapplication.WebApp;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class WebXMLDescriptorHelper extends DescriptorHelper
{

    public WebXMLDescriptorHelper( IProject project )
    {
        super( project );

        setDescriptorPath( ILiferayConstants.WEB_XML_FILE );
    }

    public IStatus addTagLib( final TagLibRefType tagLibRefType )
    {
        IFile file = getDescriptorFile( getDescriptorPath() );

        IStatus status = null;

        if( file != null && file.exists() )
        {
            status = new DOMModelEditOperation( file )
            {

                protected IStatus doExecute( IDOMDocument document )
                {
                    return doAddTagLib( document, tagLibRefType );
                }
            }.execute();
        }
        else
        {
            WebArtifactEdit webArtifactEdit = WebArtifactEdit.getWebArtifactEditForWrite( this.project );
            int j2eeVersion = webArtifactEdit.getJ2EEVersion();

            WebApp webApp = webArtifactEdit.getWebApp();

            if( tagLibReferenceExists( webApp, tagLibRefType ) )
            {
                return Status.OK_STATUS;
            }
            // webApp.setFileList(null);
            JSPConfig jspConfig = webApp.getJspConfig();

            if( jspConfig == null && webApp.getVersionID() != 23 )
            {
                jspConfig = JspFactory.eINSTANCE.createJSPConfig();
            }

            if( jspConfig != null )
            {
                jspConfig.getTagLibs().add( tagLibRefType );
            }
            else
            {
                EList tagLibs = webApp.getTagLibs();
                tagLibs.add( tagLibRefType );
            }

            if( jspConfig != null )
            {
                webApp.setJspConfig( jspConfig );
            }

            webArtifactEdit.save( null );
            webArtifactEdit.dispose();

            status = Status.OK_STATUS;
        }

        if( !status.isOK() )
        {
            return status;
        }

        return status;
    }

    protected IStatus doAddTagLib( IDOMDocument document, TagLibRefType tagLibRefType )
    {
        if( tagLibReferenceExists( document, tagLibRefType ) )
        {
            return Status.OK_STATUS;
        }

        String typeId = document.getDocumentTypeId();
        Element docRoot = document.getDocumentElement();

        if( typeId != null && typeId.contains( "2.3" ) )
        {
            Element taglibNextSibling = findChildElement( docRoot, "resource-env-ref" );

            if( taglibNextSibling == null )
            {
                taglibNextSibling = findChildElement( docRoot, "resource-ref" );
            }

            if( taglibNextSibling == null )
            {
                taglibNextSibling = findChildElement( docRoot, "security-constraint" );
            }

            if( taglibNextSibling == null )
            {
                taglibNextSibling = findChildElement( docRoot, "login-config" );
            }

            if( taglibNextSibling == null )
            {
                taglibNextSibling = findChildElement( docRoot, "security-role" );
            }

            if( taglibNextSibling == null )
            {
                taglibNextSibling = findChildElement( docRoot, "env-entry" );
            }

            if( taglibNextSibling == null )
            {
                taglibNextSibling = findChildElement( docRoot, "ejb-ref" );
            }

            if( taglibNextSibling == null )
            {
                taglibNextSibling = findChildElement( docRoot, "ejb-local-ref" );
            }

            Element taglib = insertChildElement( docRoot, taglibNextSibling, "taglib", "" );

            appendChildElement( taglib, "taglib-uri", tagLibRefType.getTaglibURI() );

            appendChildElement( taglib, "taglib-location", tagLibRefType.getTaglibLocation() );

            if( taglibNextSibling == null )
            {
                docRoot.appendChild( document.createTextNode( System.getProperty( "line.separator" ) ) );
            }
            // format the new node added to the model;
            FormatProcessorXML processor = new FormatProcessorXML();

            processor.formatNode( taglib );
        }
        else
        {
            Element jspConfig = findChildElement( docRoot, "jsp-config" );

            if( jspConfig == null )
            {
                jspConfig = appendChildElement( docRoot, "jsp-config" );
            }

            Element taglib = appendChildElement( jspConfig, "taglib" );

            appendChildElement( taglib, "taglib-uri", tagLibRefType.getTaglibURI() );

            appendChildElement( taglib, "taglib-location", tagLibRefType.getTaglibLocation() );

            docRoot.appendChild( document.createTextNode( System.getProperty( "line.separator" ) ) );

            // format the new node added to the model;
            FormatProcessorXML processor = new FormatProcessorXML();

            processor.formatNode( jspConfig );
        }

        return Status.OK_STATUS;
    }

    protected boolean tagLibReferenceExists( IDOMDocument document, TagLibRefType tagLibRefType )
    {
        NodeList taglibs = document.getElementsByTagName( "taglib" );

        for( int i = 0; i < taglibs.getLength(); i++ )
        {
            Node taglib = taglibs.item( i );

            boolean taglibUriEquals =
                NodeUtil.getChildElementContent( taglib, "taglib-uri" ).trim().equals(
                    tagLibRefType.getTaglibURI().trim() );

            boolean taglibLocationEquals =
                NodeUtil.getChildElementContent( taglib, "taglib-location" ).trim().equals(
                    tagLibRefType.getTaglibLocation().trim() );

            if( taglibUriEquals && taglibLocationEquals )
            {
                return true;
            }
        }

        return false;
    }

    protected boolean tagLibReferenceExists( WebApp webApp, TagLibRefType tagLibRefType )
    {
        EList taglibs = webApp.getTagLibs();

        if( taglibs != null )
        {
            for( Object taglib : taglibs )
            {
                if( taglib instanceof TagLibRefType )
                {
                    TagLibRefType existingTaglib = (TagLibRefType) taglib;

                    if( existingTaglib.equals( tagLibRefType ) )
                    {
                        return true;
                    }
                }
            }
        }

        JSPConfig config = webApp.getJspConfig();

        if( config != null )
        {
            taglibs = config.getTagLibs();

            if( taglibs != null )
            {
                for( Object taglib : taglibs )
                {
                    if( taglib instanceof TagLibRefType )
                    {
                        TagLibRefType existingTaglib = (TagLibRefType) taglib;

                        if( existingTaglib.equals( tagLibRefType ) )
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

}
