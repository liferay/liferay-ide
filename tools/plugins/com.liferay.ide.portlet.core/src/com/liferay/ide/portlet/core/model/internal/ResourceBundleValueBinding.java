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
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */

public final class ResourceBundleValueBinding extends XmlValueBindingImpl 
{

    private String[] params;

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.BindingImpl#init(org.eclipse.sapphire.modeling.IModelElement,
     * org.eclipse.sapphire.modeling.ModelProperty, java.lang.String[])
     */
    @Override
    public void init( final IModelElement element, final ModelProperty property, final String[] params )
    {
        super.init( element, property, params );
        this.params = params;

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.ValueBindingImpl#read()
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
     * @see org.eclipse.sapphire.modeling.ValueBindingImpl#write(java.lang.String)
     */
    @Override
    public void write( final String value )
    {
        final XmlElement element = xml( false ).getChildElement( this.params[0], true );
        if( value != null && ( value.endsWith( ".properties" ) || value.indexOf( "/" ) != -1 ) )  //$NON-NLS-1$//$NON-NLS-2$
        {
            IProject project = element().adapt( IProject.class );
            element.setText( PortletUtil.convertIOToJavaFileName( project, value.trim() ) );
        }
        else
        {
            element.setText( value );
        }
    }
}
