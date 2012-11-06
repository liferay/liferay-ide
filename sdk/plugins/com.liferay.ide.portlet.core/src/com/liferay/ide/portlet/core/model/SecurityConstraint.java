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

package com.liferay.ide.portlet.core.model;

import com.liferay.ide.portlet.core.model.internal.DefaultXmlBinding;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Image( path = "images/elcl16/constraint_16x16.png" )
public interface SecurityConstraint extends IModelElement, Identifiable, Displayable
{

    ModelElementType TYPE = new ModelElementType( SecurityConstraint.class );

    // *** Portlet Name ***
    @Type( base = PortletName.class )
    @Label( standard = "Portlet name" )
    @Required
    @CountConstraint( min = 1 )
    @NoDuplicates
    @XmlListBinding
    ( 
        path = "portlet-collection", 
        mappings = @XmlListBinding.Mapping
        (
            element = "portlet-name",
            type = PortletName.class 
        ) 
    )
    ListProperty PROP_PORTLET_NAMES = new ListProperty( TYPE, "PortletNames" );

    ModelElementList<PortletName> getPortletNames();

    // *** UserDataConstraint ***

    @Type( base = UserDataConstraint.class )
    @Label( standard = "User Data Constraint" )
    @Required
    @CustomXmlElementBinding( impl = DefaultXmlBinding.class, params = { "user-data-constraint" } )
    ImpliedElementProperty PROP_USER_DATA_CONSTRAINT = new ImpliedElementProperty( TYPE, "UserDataConstraint" );

    UserDataConstraint getUserDataConstraint();

}
