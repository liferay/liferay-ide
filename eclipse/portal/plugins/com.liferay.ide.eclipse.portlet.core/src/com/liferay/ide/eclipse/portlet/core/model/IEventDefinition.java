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

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.NameAndQNameChoiceValueBinding;
import com.liferay.ide.eclipse.portlet.core.model.internal.NameOrQnameValidationService;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Image( path = "images/elcl16/event_16x16.gif" )
@Label( full = "Event Definition", standard = "Event Definition" )
public interface IEventDefinition extends IQName, IIdentifiable, IDescribeable {

	ModelElementType TYPE = new ModelElementType( IEventDefinition.class );

	// *** Name ***

	@Label( standard = "Name" )
	@XmlBinding( path = "name" )
	@Whitespace( trim = true )
	@DefaultValue( text = "EVENT_NAME" )
	@Service( impl = NameOrQnameValidationService.class )
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

	// *** Event Value Type ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = { JavaTypeKind.CLASS, JavaTypeKind.INTERFACE }, type = { "java.io.Serializable" } )
	@Label( standard = "Value Type" )
	@XmlBinding( path = "value-type" )
	ValueProperty PROP_EVENT_VALUE_TYPE = new ValueProperty( TYPE, "EventValueType" );

	ReferenceValue<JavaTypeName, JavaType> getEventValueType();

	void setEventValueType( String eventValueType );

	void setEventValueType( JavaTypeName eventValueType );

}
