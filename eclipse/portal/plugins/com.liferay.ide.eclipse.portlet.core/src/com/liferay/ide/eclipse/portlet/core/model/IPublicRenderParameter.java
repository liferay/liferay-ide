/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Services Pvt Ltd., All rights reserved.
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
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.NameAndQNameChoiceValueBinding;
import com.liferay.ide.eclipse.portlet.core.model.internal.QNameLocalPartValueBinding;
import com.liferay.ide.eclipse.portlet.core.model.internal.QNamespaceValueBinding;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Image( path = "/images/obj16/pub_obj.gif" )
public interface IPublicRenderParameter extends IModelElement, IIdentifiable {

	ModelElementType TYPE = new ModelElementType( IPublicRenderParameter.class );

	// *** Identifier ***

	@Label( standard = "Identifier" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "identifier" )
	ValueProperty PROP_IDENTIFIER = new ValueProperty( TYPE, "Identifier" );

	Value<String> getIdentifier();

	void setIdentifier( String value );

	// *** NamespaceURI ***

	@Label( standard = "Namespace URI" )
	@DefaultValue( text = "NAMESPACE_URI" )
	@XmlBinding( path = "qname" )
	@CustomXmlValueBinding( impl = QNamespaceValueBinding.class, params = { "qname" } )
	ValueProperty PROP_NAMESPACE_URI = new ValueProperty( TYPE, "NamespaceURI" );

	Value<String> getNamespaceURI();

	void setNamespaceURI( String value );

	// *** LocalPart ***

	@Label( standard = "Local Part" )
	@DefaultValue( text = "LOCAL_PART" )
	@XmlBinding( path = "qname" )
	@CustomXmlValueBinding( impl = QNameLocalPartValueBinding.class, params = { "qname" } )
	ValueProperty PROP_LOCAL_PART = new ValueProperty( TYPE, "LocalPart" );

	Value<String> getLocalPart();

	void setLocalPart( String value );

	// *** Name ***

	@Label( standard = "Name" )
	@XmlBinding( path = "name" )
	@Whitespace( trim = true )
	@DefaultValue( text = "PARAM_NAME" )
	@Enablement( expr = "${(NamespaceURI == 'NAMESPACE_URI' && LocalPart == 'LOCAL_PART') || (empty NamespaceURI && empty LocalPart) }" )
	@CustomXmlValueBinding( impl = NameAndQNameChoiceValueBinding.class, params = { "name" } )
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

	Value<String> getName();

	void setName( String value );

	// *** Aliases ***

	@Type( base = IAliasQName.class )
	@Label( standard = "Aliases" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "alias", type = IAliasQName.class ) } )
	ListProperty PROP_ALIASES = new ListProperty( TYPE, "Aliases" );

	ModelElementList<IAliasQName> getAliases();

}
