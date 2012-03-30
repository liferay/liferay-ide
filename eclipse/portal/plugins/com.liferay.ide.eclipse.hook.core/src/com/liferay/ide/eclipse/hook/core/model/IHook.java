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
 *          Gregory Amerson - IDE-355 
 *******************************************************************************/

package com.liferay.ide.eclipse.hook.core.model;

import com.liferay.ide.eclipse.hook.core.model.internal.CustomJspsBindingImpl;
import com.liferay.ide.eclipse.hook.core.model.internal.PortalPropertiesBindingImpl;

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Transient;
import org.eclipse.sapphire.modeling.TransientProperty;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DependsOn;
import org.eclipse.sapphire.modeling.annotations.FixedOrderList;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@GenerateImpl
public interface IHook extends IModelElement
{

	ModelElementType TYPE = new ModelElementType( IHook.class );

	// *** Version ***

	@Type( base = HookVersionType.class )
	@Label( standard = "Version" )
	TransientProperty PROP_VERSION = new TransientProperty( TYPE, "Version" );

	Transient<HookVersionType> getVersion();

	void setVersion( HookVersionType value );

	// *** PortalPropertiesFile ***

	@Type( base = IPortalPropertiesFile.class )
	@XmlBinding( path = "portal-properties" )
	ElementProperty PROP_PORTAL_PROPERTIES_FILE = new ElementProperty( TYPE, "PortalPropertiesFile" );

	ModelElementHandle<IPortalPropertiesFile> getPortalPropertiesFile();

	// *** PortalProperties ***

	@Type( base = IPortalProperty.class )
	@Label( standard = "Portal Properties Overrides" )
	@CustomXmlListBinding( impl = PortalPropertiesBindingImpl.class )
	ListProperty PROP_PORTAL_PROPERTIES_OVERRIDES = new ListProperty( TYPE, "PortalPropertiesOverrides" );

	ModelElementList<IPortalProperty> getPortalPropertiesOverrides();

	// *** LanguageProperties ***

	@Type( base = ILanguageProperty.class )
	@Label( standard = "Language Properties" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "language-properties", type = ILanguageProperty.class ) } )
	ListProperty PROP_LANGUAGE_PROPERTIES = new ListProperty( TYPE, "LanguageProperties" );

	ModelElementList<ILanguageProperty> getLanguageProperties();

	@Type( base = ICustomJsp.class )
	@Label( standard = "custom jsps" )
	@FixedOrderList
	@CustomXmlListBinding( impl = CustomJspsBindingImpl.class )
	@DependsOn( value = { "CustomJspDir/Value" } )
	ListProperty PROP_CUSTOM_JSPS = new ListProperty( TYPE, "CustomJsps" );

	ModelElementList<ICustomJsp> getCustomJsps();

	// *** CustomJspDir ***

	@Type( base = ICustomJspDir.class )
	@Label( standard = "Custom JSP Dir" )
	@XmlBinding( path = "custom-jsp-dir" )
	ElementProperty PROP_CUSTOM_JSP_DIR = new ElementProperty( TYPE, "CustomJspDir" );

	ModelElementHandle<ICustomJspDir> getCustomJspDir();

	// *** CustomJspGlobal ***

	@Type( base = Boolean.class )
	@Label( standard = "Custom JSP Global" )
	@XmlBinding( path = "custom-jsp-global" )
	@DefaultValue( text = "true" )
	ValueProperty PROP_CUSTOM_JSP_GLOBAL = new ValueProperty( TYPE, "CustomJspGlobal" );

	Value<Boolean> getCustomJspGlobal();

	void setCustomJspGlobal( String value );

	void setCustomJspGlobal( Boolean value );

	// *** IndexerPostProcessors ***

	@Type( base = IIndexerPostProcessor.class )
	@Label( standard = "Index Post Processors" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "indexer-post-processor", type = IIndexerPostProcessor.class ) } )
	ListProperty PROP_INDEXER_POST_PROCESSORS = new ListProperty( TYPE, "IndexerPostProcessors" );

	ModelElementList<IIndexerPostProcessor> getIndexerPostProcessors();

	// *** Services ***

	@Type( base = IService.class )
	@Label( standard = "Service Wrappers" )
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
