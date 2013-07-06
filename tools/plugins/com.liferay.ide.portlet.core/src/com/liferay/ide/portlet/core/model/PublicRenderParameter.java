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
import com.liferay.ide.portlet.core.model.internal.QNameLocalPartValueBinding;
import com.liferay.ide.portlet.core.model.internal.QNamespaceValueBinding;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Service.Param;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image( path = "/images/elcl16/parameter_16x16.gif" )
@Label( full = "Public Render Parameter", standard = "Public Render Parameter" )
public interface PublicRenderParameter extends QName, Identifiable
{

    ElementType TYPE = new ElementType( PublicRenderParameter.class );

    // *** Identifier ***

    @Label( standard = "Identifier" )
    @Required
    @NoDuplicates
    @XmlBinding( path = "identifier" )
    ValueProperty PROP_IDENTIFIER = new ValueProperty( TYPE, "Identifier" ); //$NON-NLS-1$

    Value<String> getIdentifier();

    void setIdentifier( String value );

    // *** NamespaceURI ***

    @Label( standard = "Namespace URI" )
    // @DefaultValue( text = "NAMESPACE_URI" )
    @XmlBinding( path = "qname" )
    @Service( impl = NameOrQnameValidationService.class, params = { @Param( name = "qname", value = "" ) } )
    @CustomXmlValueBinding( impl = QNamespaceValueBinding.class, params = { "qname" } )
    ValueProperty PROP_NAMESPACE_URI = new ValueProperty( TYPE, "NamespaceURI" ); //$NON-NLS-1$

    Value<String> getNamespaceURI();

    void setNamespaceURI( String value );

    // *** LocalPart ***

    @Label( standard = "Local Part" )
    @XmlBinding( path = "qname" )
    @Service( impl = NameOrQnameValidationService.class, params = { @Param( name = "qname", value = "" ) } )
    @CustomXmlValueBinding( impl = QNameLocalPartValueBinding.class, params = { "qname" } )
    ValueProperty PROP_LOCAL_PART = new ValueProperty( TYPE, "LocalPart" ); //$NON-NLS-1$

    Value<String> getLocalPart();

    void setLocalPart( String value );

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

}
