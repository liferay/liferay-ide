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

package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image( path = "images/elcl16/constraint_16x16.png" )
public interface SecurityConstraint extends Element, Identifiable, Displayable
{

    ElementType TYPE = new ElementType( SecurityConstraint.class );

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
    ListProperty PROP_PORTLET_NAMES = new ListProperty( TYPE, "PortletNames" ); //$NON-NLS-1$

    ElementList<PortletName> getPortletNames();

    // *** UserDataConstraint ***

    @Type( base = UserDataConstraint.class )
    @Label( standard = "User Data Constraint" )
    @Required
    @XmlBinding( path = "user-data-constraint" )
    ImpliedElementProperty PROP_USER_DATA_CONSTRAINT = new ImpliedElementProperty( TYPE, "UserDataConstraint" ); //$NON-NLS-1$

    UserDataConstraint getUserDataConstraint();

}
