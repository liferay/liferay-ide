/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 *******************************************************************************/

package com.liferay.ide.eclipse.display.model;





import com.liferay.ide.eclipse.portlet.core.model.internal.Doctype;
import com.liferay.ide.eclipse.portlet.core.model.internal.DtdRootElementController;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Doctype( rootElementName = "display", publicId = "-//Liferay//DTD Display 6.0.0//EN", systemId = "http://www.liferay.com/dtd/liferay-display_6_0_0.dtd" )
@CustomXmlRootBinding( value = DtdRootElementController.class )
public interface IDisplay extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IDisplay.class );

	// *** Categories ***

	@Type( base = ICategory.class )
	@Label( standard = "Categories" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "category", type = ICategory.class ) } )
	ListProperty PROP_CATEGORIES = new ListProperty( TYPE, "Categories" );

	ModelElementList<ICategory> getCategories();

}
