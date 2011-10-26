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
import org.eclipse.sapphire.modeling.Transient;
import org.eclipse.sapphire.modeling.TransientProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

import com.liferay.ide.eclipse.hook.core.model.internal.HookVersion;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@GenerateImpl
public interface IHookCommonElement extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IHookCommonElement.class );

	// *** Version ***

	@Type( base = HookVersion.class )
	@Label( standard = "Version" )
	TransientProperty PROP_VERSION = new TransientProperty( TYPE, "Version" );

	Transient<HookVersion> getVersion();

	void setVersion( HookVersion value );

	// *** Services ***

	@Type( base = IService.class )
	@Label( standard = "Services" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "service", type = IService.class ) } )
	ListProperty PROP_SERVICES = new ListProperty( TYPE, "Services" );

	ModelElementList<IService> getServices();

	// *** ServletFilters ***

	@Type( base = IServletFilter.class )
	@Label( standard = "Servlet filters" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "servlet-filter", type = IServletFilter.class ) } )
	ListProperty PROP_SERVLET_FILTERS = new ListProperty( TYPE, "ServletFilters" );

	ModelElementList<IServletFilter> getServletFilters();

	// *** ServletFilterMappings ***

	@Type( base = IServletFilterMapping.class )
	@Label( standard = "Servlet Filter Mappings" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "servlet-filter-mapping", type = IServletFilterMapping.class ) } )
	ListProperty PROP_SERVLET_FILTER_MAPPINGS = new ListProperty( TYPE, "ServletFilterMappings" );

	ModelElementList<IServletFilterMapping> getServletFilterMappings();

	// *** StrutsActions ***

	@Type( base = IStrutsAction.class )
	@Label( standard = "Struts Actions" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "struts-action", type = IStrutsAction.class ) } )
	ListProperty PROP_STRUTS_ACTIONS = new ListProperty( TYPE, "StrutsActions" );

	ModelElementList<IStrutsAction> getStrutsActions();

}
