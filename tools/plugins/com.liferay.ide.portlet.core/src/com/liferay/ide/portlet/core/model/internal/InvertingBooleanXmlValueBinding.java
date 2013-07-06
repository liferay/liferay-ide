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
 * Contributors:
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model.internal;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

/**
 * @author Kamesh Sampath
 */
public final class InvertingBooleanXmlValueBinding extends XmlValueBindingImpl
{
    private XmlPath path;
    private String[] params;


    @Override
    public void init( final Property property )
    {
        super.init( property );

        this.params = property.definition().getAnnotation( CustomXmlValueBinding.class ).params();

        final XmlNamespaceResolver xmlNamespaceResolver = resource().getXmlNamespaceResolver();
        this.path = new XmlPath( params[0], xmlNamespaceResolver );
    }

    @Override
    public String read()
    {
        String value = null;

        final XmlElement element = xml( false );

        if( element != null )
        {
            value = element.getChildNodeText( this.path );

            if( value != null )
            {
                if( value.equalsIgnoreCase( "true" ) ) //$NON-NLS-1$
                {
                    value = "false"; //$NON-NLS-1$
                }
                else if( value.equalsIgnoreCase( "false" ) ) //$NON-NLS-1$
                {
                    value = "true"; //$NON-NLS-1$
                }
            }
        }

        return value;
    }

    @Override
    public void write( final String value )
    {
        String val = value;

        if( val != null )
        {
            if( val.equalsIgnoreCase( "true" ) ) //$NON-NLS-1$
            {
                val = "false"; //$NON-NLS-1$
            }
            else if( val.equalsIgnoreCase( "false" ) ) //$NON-NLS-1$
            {
                val = "true"; //$NON-NLS-1$
            }
        }

        xml( true ).setChildNodeText( this.path, val, true );
    }

    @Override
    public XmlNode getXmlNode()
    {
        final XmlElement element = xml( false );

        if( element != null )
        {
            return element.getChildNode( this.path, false );
        }

        return null;
    }

}
