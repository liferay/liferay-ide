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
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
@Image( path = "images/elcl16/option_16x16.gif" )
public interface IContainerRuntimeOption extends IModelElement, INameable {

	ModelElementType TYPE = new ModelElementType( IContainerRuntimeOption.class );

	// *** Values ***

	@Type( base = IOptionValue.class )
	@Label( standard = "Options" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( type = IOptionValue.class, element = "value" ) } )
	ListProperty PROP_OPTIONS = new ListProperty( TYPE, "Options" );

	ModelElementList<IOptionValue> getOptions();

}
