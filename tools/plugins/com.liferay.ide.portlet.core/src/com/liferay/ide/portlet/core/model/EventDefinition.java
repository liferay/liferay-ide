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

import com.liferay.ide.portlet.core.model.internal.NameAndQNameChoiceValueBinding;
import com.liferay.ide.portlet.core.model.internal.NameOrQnameValidationService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image( path = "images/elcl16/event_16x16.gif" )
@Label( full = "Event Definition", standard = "Event Definition" )
public interface EventDefinition extends QName, Identifiable, Describeable
{

    ElementType TYPE = new ElementType( EventDefinition.class );

    // *** Name ***

    @Label( standard = "Name" )
    @XmlBinding( path = "name" )
    @Service( impl = NameOrQnameValidationService.class )
    @Enablement( expr = "${(NamespaceURI == 'NAMESPACE_URI' && LocalPart == 'LOCAL_PART') || (empty NamespaceURI && empty LocalPart) }" )
    @CustomXmlValueBinding( impl = NameAndQNameChoiceValueBinding.class, params = { "name" } )
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" ); //$NON-NLS-1$

    Value<String> getName();

    void setName( String value );

    // *** Aliases ***

    @Type( base = AliasQName.class )
    @Label( standard = "Aliases" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "alias", type = AliasQName.class ) } )
    ListProperty PROP_ALIASES = new ListProperty( TYPE, "Aliases" ); //$NON-NLS-1$

    ElementList<AliasQName> getAliases();

    // *** Event Value Type ***

    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @JavaTypeConstraint( kind = { JavaTypeKind.CLASS, JavaTypeKind.INTERFACE }, type = { "java.io.Serializable" } )
    @Label( standard = "Value Type" )
    @XmlBinding( path = "value-type" )
    ValueProperty PROP_EVENT_VALUE_TYPE = new ValueProperty( TYPE, "EventValueType" ); //$NON-NLS-1$

    ReferenceValue<JavaTypeName, JavaType> getEventValueType();

    void setEventValueType( String eventValueType );

    void setEventValueType( JavaTypeName eventValueType );

}
