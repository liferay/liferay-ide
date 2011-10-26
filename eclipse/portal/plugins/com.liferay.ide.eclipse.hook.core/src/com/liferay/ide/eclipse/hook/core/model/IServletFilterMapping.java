/*******************************************************************************
 *  Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *  
 *   This library is free software; you can redistribute it and/or modify it under
 *   the terms of the GNU Lesser General Public License as published by the Free
 *   Software Foundation; either version 2.1 of the License, or (at your option)
 *   any later version.
 *  
 *   This library is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *   FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *   details.
 *  
 *   Contributors:
 *          Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.eclipse.hook.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Image( path = "images/elcl16/filter_mapping_16x16.gif" )
public interface IServletFilterMapping extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IServletFilterMapping.class );

	// *** Servlet Filter Name ***

	@Label( standard = "Filter Name" )
	@XmlBinding( path = "servlet-filter-name" )
	ValueProperty PROP_SERVLET_FILTER_NAME = new ValueProperty( TYPE, "ServletFilterName" );

	Value<String> getServletFilterName();

	void setServletFilterName( String value );

	// TODO choice between after-filter and before-filter

	// *** AfterFilter ***

	@Label( standard = "After Filter" )
	@Whitespace( trim = true )
	@DefaultValue( text = "AFTER_FILTER" )
	@XmlBinding( path = "after-filter" )
	ValueProperty PROP_AFTER_FILTER = new ValueProperty( TYPE, "AfterFilter" );

	Value<String> getAfterFilter();

	void setAfterFilter( String value );

	// *** BeforeFilter ***

	@Label( standard = "Before Filter" )
	@Whitespace( trim = true )
	@DefaultValue( text = "BEFORE_FILTER" )
	@XmlBinding( path = "before-filter" )
	ValueProperty PROP_BEFORE_FILTER = new ValueProperty( TYPE, "BeforeFilter" );

	Value<String> getBeforeFilter();

	void setBeforeFilter( String value );

	// *** URLPattern ***

	@Label( standard = "URL Pattern" )
	@XmlBinding( path = "url-pattern" )
	ValueProperty PROP_URL_PATTERN = new ValueProperty( TYPE, "URLPattern" );

	Value<String> getURLPattern();

	void setURLPattern( String value );

	// *** Dispatchers ***

	@Type( base = IDispatcher.class )
	@Label( standard = "Dispatchers" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "dispatcher", type = IDispatcher.class ) } )
	ListProperty PROP_DISPATCHERS = new ListProperty( TYPE, "Dispatchers" );

	ModelElementList<IDispatcher> getDispatchers();

}
