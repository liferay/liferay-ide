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

package com.liferay.ide.eclipse.hook.core.model600;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlDocumentType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@XmlDocumentType( publicId = "-//Liferay//DTD Hook 6.0.0//EN", systemId = "http://www.liferay.com/dtd/liferay-hook_6_0_0.dtd" )
@XmlRootBinding( elementName = "hook" )
@GenerateImpl
public interface IHook extends com.liferay.ide.eclipse.hook.core.model.IHook, IHook6xCommonElement {

	ModelElementType TYPE = new ModelElementType( IHook.class );

	// *** CustomJspGlobal ***

	@Type( base = Boolean.class )
	@Label( standard = "Custom JSP Global" )
	@DefaultValue( text = "false" )
	@XmlBinding( path = "custom-jsp-global" )
	ValueProperty PROP_CUSTOM_JSP_GLOBAL = new ValueProperty( TYPE, "CustomJspGlobal" );

	Value<Boolean> getCustomJspGlobal();

	void setCustomJspGlobal( String value );

	void setCustomJspGlobal( Boolean value );

}
