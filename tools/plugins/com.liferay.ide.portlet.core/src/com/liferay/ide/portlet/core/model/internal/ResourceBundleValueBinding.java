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

import com.liferay.ide.portlet.core.util.PortletUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

/**
 * @author Kamesh Sampath
 */

public final class ResourceBundleValueBinding extends XmlValueBindingImpl
{

    private String[] params;

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.PropertyBinding#init(org.eclipse.sapphire.modeling.Element,
     * org.eclipse.sapphire.modeling.Property, java.lang.String[])
     */
    @Override
    public void init( final Property property )
    {
        super.init( property );
        this.params = property.definition().getAnnotation( CustomXmlValueBinding.class ).params();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.ValuePropertyBinding#read()
     */
    @Override
    public String read()
    {
        String value = null;
        final XmlElement parent = xml( false );
        if( this.params != null && parent != null )
        {
            final XmlElement element = parent.getChildElement( this.params[0], false );

            if( element != null )
            {

                value = element.getText();

                // System.out.println( "Reading VALUE ___________________ " + value );

                if( value != null )
                {
                    value = value.trim();
                }
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
        final XmlElement element = xml( false ).getChildElement( this.params[0], true );
        if( value != null && ( value.endsWith( ".properties" ) || value.indexOf( "/" ) != -1 ) )  //$NON-NLS-1$//$NON-NLS-2$
        {
            IProject project = property().element().adapt( IProject.class );
            element.setText( PortletUtil.convertIOToJavaFileName( project, value.trim() ) );
        }
        else
        {
            element.setText( value );
        }
    }
}
