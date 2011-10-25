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

import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlDocumentType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@GenerateImpl
@XmlDocumentType( publicId = "-//Liferay//DTD Hook 5.2.0//EN", systemId = "http://www.liferay.com/dtd/liferay-hook_5_2_0.dtd" )
@XmlRootBinding( elementName = "hook" )
public interface IHook extends IHookCommonElement {

	ModelElementType TYPE = new ModelElementType( IHook.class );

	// *** PortalProperties ***

	@Label( standard = "Portal Properties" )
	@XmlBinding( path = "portal-properties" )
	@CountConstraint( min = 0, max = 1 )
	ValueProperty PROP_PORTAL_PROPERTIES = new ValueProperty( TYPE, "PortalProperties" );

	Value<String> getPortalProperties();

	void setPortalProperties( String value );

	// *** LanguageProperties ***

	@Type( base = ILanguageProperty.class )
	@Label( standard = "Language Properties" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "language-properties", type = ILanguageProperty.class ) } )
	ListProperty PROP_LANGUAGE_PROPERTIES = new ListProperty( TYPE, "LanguageProperties" );

	ModelElementList<ILanguageProperty> getLanguageProperties();

	// *** CustomJspDir ***

	@Label( standard = "Custom JSP Dir" )
	@XmlBinding( path = "custom-jsp-dir" )
	@CountConstraint( min = 0, max = 1 )
	ValueProperty PROP_CUSTOM_JSP_DIR = new ValueProperty( TYPE, "CustomJspDir" );

	Value<String> getCustomJspDir();

	void setCustomJspDir( String value );

}
