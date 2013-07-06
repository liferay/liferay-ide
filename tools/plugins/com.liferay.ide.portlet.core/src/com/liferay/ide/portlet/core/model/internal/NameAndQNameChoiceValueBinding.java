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

public final class NameAndQNameChoiceValueBinding extends XmlValueBindingImpl
{
    private static final String Q_NAME = "qname"; //$NON-NLS-1$
    private static final String NAME = "name"; //$NON-NLS-1$

    private String[] params;
    private XmlPath path;

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.PropertyBinding#init(org.eclipse.sapphire.modeling.Element,
     * org.eclipse.sapphire.modeling.Property, java.lang.String[])
     */
    @Override
    public void init( Property property )
    {
        super.init( property );
        this.params = property.definition().getAnnotation( CustomXmlValueBinding.class ).params();;

        final XmlNamespaceResolver xmlNamespaceResolver = resource().getXmlNamespaceResolver();
        this.path = new XmlPath( params[0], xmlNamespaceResolver );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.ValuePropertyBinding#read()
     */
    @Override
    public String read()
    {
        final XmlElement parent = xml( false );
        // System.out.println( "NameAndQNameChoiceValueBinding.read() - \n" + parent );
        String value = null;
        if( parent != null )
        {
            // System.out.println( "NameAndQNameChoiceValueBinding.read()" + params[0] );
            final XmlElement eventNameElement = parent.getChildElement( NAME, false );
            final XmlElement eventQNameElement = parent.getChildElement( Q_NAME, false );

            if( eventNameElement != null && NAME.equals( params[0] ) )
            {
                // System.out.println( "NameAndQNameChoiceValueBinding.read() - \n" + eventNameElement );
                value = eventNameElement.getText();
            }
            else if( eventQNameElement != null && Q_NAME.equals( params[0] ) )
            {

                // System.out.println( "NameAndQNameChoiceValueBinding.read() - \n" + eventQNameElement );
                value = eventQNameElement.getText();
            }
        }

        return value;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.ValuePropertyBinding#write(java.lang.String)
     */
    @Override
    public void write( final String value )
    {
        final XmlElement parent = xml( true );

        // System.out.println( "EventDefinitionValueBinding.write()" + parent );

        final XmlElement eventNameElement = parent.getChildElement( NAME, false );
        final XmlElement eventQNameElement = parent.getChildElement( Q_NAME, false );

        if( NAME.equals( params[0] ) && eventQNameElement != null )
        {
            parent.removeChildNode( Q_NAME );
        }
        else if( Q_NAME.equals( params[0] ) && eventNameElement != null )
        {
            parent.removeChildNode( NAME );
        }

        parent.setChildNodeText( this.path, value, true );

    }

    @Override
    public XmlNode getXmlNode()
    {
        final XmlElement parent = xml();

        XmlElement element = parent.getChildElement( Q_NAME, false );

        if( element != null )
        {
            return element;
        }

        element = parent.getChildElement( NAME, false );

        if( element != null )
        {
            return element;
        }

        return null;
    }

}
