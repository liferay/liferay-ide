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
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.portlet.core.model;

import com.liferay.ide.portlet.core.model.internal.EventDefinitionReferenceService;
import com.liferay.ide.portlet.core.model.internal.QNameTextNodeValueBinding;
import com.liferay.ide.portlet.core.model.internal.QNamesPossibleValuesService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DependsOn;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Service.Param;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
public interface EventDefinitionRef extends Element, Identifiable, Describeable
{

    ElementType TYPE = new ElementType( EventDefinitionRef.class );

    // *** Qname ***

    @Label( standard = "Qname" )
    @XmlBinding( path = "qname" )
    @NoDuplicates
    @Service( impl = QNamesPossibleValuesService.class, params = { @Service.Param( name = "0", value = "Q_NAME" ) } )
    @DependsOn( { "/EventDefinitions/NamespaceURI", "/EventDefinitions/LocalPart" } )
    @CustomXmlValueBinding( impl = QNameTextNodeValueBinding.class, params = { "qname" } )
    ValueProperty PROP_Q_NAME = new ValueProperty( TYPE, "Qname" ); //$NON-NLS-1$

    Value<String> getQname();

    void setQname( String value );

    // *** Name ***

    @Label( standard = "Name" )
    @XmlBinding( path = "name" )
    @NoDuplicates
    @Enablement( expr = "${Qname == 'Q_NAME'}" )
    @PossibleValues( property = "/EventDefinitions/Name" )
    @Service( impl = EventDefinitionReferenceService.class, params = { @Param( name = "0", value = "name" ) } )
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" ); //$NON-NLS-1$

    Value<String> getName();

    void setName( String value );

}
