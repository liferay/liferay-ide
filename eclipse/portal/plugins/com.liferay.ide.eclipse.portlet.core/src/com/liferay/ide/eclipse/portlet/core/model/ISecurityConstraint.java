/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt Ltd., All rights reserved.
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
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.DefaultXmlBinding;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Image( path = "images/elcl16/security_constraint_16x16.gif" )
public interface ISecurityConstraint extends IModelElement, IIdentifiable {

	ModelElementType TYPE = new ModelElementType( ISecurityConstraint.class );

	// *** PortletDisplayNames ***

	@Type( base = IPortletDisplayName.class )
	@Label( standard = "Portlet Display Names" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "display-name", type = IPortletDisplayName.class ) } )
	ListProperty PROP_PORTLET_DISPLAY_NAMES = new ListProperty( TYPE, "PortletDisplayNames" );

	ModelElementList<IPortletDisplayName> getPortletDisplayNames();

	// *** PortletCollection ***

	@Type( base = IPortletCollection.class )
	@Label( standard = "Portlet Collection" )
	@XmlBinding( path = "portlet-collection" )
	@Required
	ImpliedElementProperty PROP_PORTLET_COLLECTION = new ImpliedElementProperty( TYPE, "PortletCollection" );

	IPortletCollection getPortletCollection();

	// *** UserDataConstraint ***

	@Type( base = IUserDataConstraint.class )
	@Label( standard = "User Data Constraint" )
	@DefaultValue( text = "" )
	@Required
	@CustomXmlElementBinding( impl = DefaultXmlBinding.class, params = { "user-data-constraint" } )
	ImpliedElementProperty PROP_USER_DATA_CONSTRAINT = new ImpliedElementProperty( TYPE, "UserDataConstraint" );

	IUserDataConstraint getUserDataConstraint();

}
