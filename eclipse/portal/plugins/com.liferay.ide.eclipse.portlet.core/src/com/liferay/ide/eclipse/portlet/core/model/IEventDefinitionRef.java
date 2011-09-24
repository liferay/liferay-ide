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
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DependsOn;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.EventDefinitionReferenceService;
import com.liferay.ide.eclipse.portlet.core.model.internal.QNameTextNodeValueBinding;
import com.liferay.ide.eclipse.portlet.core.model.internal.QNamesPossibleValuesService;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
public interface IEventDefinitionRef extends IModelElement, IIdentifiable, IDescribeable {

	ModelElementType TYPE = new ModelElementType( IEventDefinitionRef.class );

	// *** Qname ***

	@Label( standard = "Qname" )
	@XmlBinding( path = "qname" )
	@Whitespace( trim = true )
	@DefaultValue( text = "Q_NAME" )
	@NoDuplicates
	@Service( impl = QNamesPossibleValuesService.class, params = { "Q_NAME" } )
	@DependsOn( { "/EventDefinitions/NamespaceURI", "/EventDefinitions/LocalPart" } )
	@CustomXmlValueBinding( impl = QNameTextNodeValueBinding.class, params = { "qname" } )
	ValueProperty PROP_Q_NAME = new ValueProperty( TYPE, "Qname" );

	Value<String> getQname();

	void setQname( String value );

	// *** Name ***

	@Label( standard = "Name" )
	@XmlBinding( path = "name" )
	@Whitespace( trim = true )
	@NoDuplicates
	@DefaultValue( text = "EVENT_NAME" )
	@Enablement( expr = "${Qname == 'Q_NAME'}" )
	@PossibleValues( property = "/EventDefinitions/Name" )
	@Service( impl = EventDefinitionReferenceService.class, params = { "name" } )
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

	Value<String> getName();

	void setName( String value );

}
