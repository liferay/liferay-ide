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
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.util.PortletModelUtil;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public final class QNameValueBinding extends XmlValueBindingImpl
{

    private String[] params;
    private XmlPath path;

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.BindingImpl#init(org.eclipse.sapphire.modeling.IModelElement,
     * org.eclipse.sapphire.modeling.ModelProperty, java.lang.String[])
     */
    @Override
    public void init( IModelElement element, ModelProperty property, String[] params )
    {
        super.init( element, property, params );
        this.params = params;

        final XmlNamespaceResolver xmlNamespaceResolver = resource().getXmlNamespaceResolver();
        this.path = new XmlPath( params[0], xmlNamespaceResolver );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.ValueBindingImpl#read()
     */
    @Override
    public String read()
    {
        final XmlElement parent = xml( false );
        String value = null;
        if( parent != null )
        {
            final XmlElement qNameElement = parent.getChildElement( params[0], false );
            value = qNameElement.getText();
        }

        return value;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.ValueBindingImpl#write(java.lang.String)
     */
    @Override
    public void write( final String value )
    {
        final XmlElement parent = xml( true );
        // System.out.println( "EventDefinitionValueBinding.write()" + parent );
        final XmlElement qNameElement = parent.getChildElement( params[0], true );
        qNameElement.setChildNodeText( this.path, value, true );

        // Only for debugging purposes
        try
        {
            PortletModelUtil.printDocument( parent.getDomNode().getOwnerDocument(), System.out );
        }
        catch( IOException e )
        {
            PortletCore.logError( e );
        }
        catch( TransformerException e )
        {
            PortletCore.logError( e );
        }

    }

    @Override
    public XmlNode getXmlNode()
    {
        final XmlElement parent = xml();

        XmlElement element = parent.getChildElement( params[0], false );

        if( element != null )
        {
            return element;
        }

        return null;
    }

}
