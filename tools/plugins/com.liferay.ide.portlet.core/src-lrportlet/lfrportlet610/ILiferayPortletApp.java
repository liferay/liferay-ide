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


package com.liferay.ide.portlet.core.model.lfrportlet610;

import com.liferay.ide.portlet.core.model.internal.Doctype;
import com.liferay.ide.portlet.core.model.internal.DtdRootElementController;
import com.liferay.ide.portlet.core.model.lfrportlet.common.ILiferayPortletAppBase;

import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author kamesh
 */
@GenerateImpl
@Doctype( rootElementName = "liferay-portlet-app", publicId = "-//Liferay//DTD Portlet Application 6.1.0//EN", systemId = "http://www.liferay.com/dtd/liferay-portlet-app_6_1_0.dtd" )
@CustomXmlRootBinding( value = DtdRootElementController.class )
public interface ILiferayPortletApp extends ILiferayPortletAppBase {

	ElementType TYPE = new ElementType( ILiferayPortletApp.class );

	// *** Portlets ***

	@Type( base = ILiferayPortlet.class )
	@CountConstraint( min = 0 )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "portlet", type = ILiferayPortlet.class ) } )
	ListProperty PROP_PORTLETS = new ListProperty( TYPE, "Portlets" );

	ElementList<ILiferayPortlet> getPortlets();

}
