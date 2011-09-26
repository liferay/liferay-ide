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

import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

/**
 * The root container model class that will have &lt;portlet-app&gt; TODO: Update the comments
 * 
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a> <br/>
 */
@GenerateImpl
@Image( path = "images/obj16/portlet_model_obj.gif" )
@XmlRootBinding( namespace = "http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd", schemaLocation = "http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd", elementName = "portlet-app" )
public interface IPortletApp extends IResourceBundle, IIdentifiable {

	ModelElementType TYPE = new ModelElementType( IPortletApp.class );

	/*
	 * Version
	 */

	@Type( base = PortletAppVersion.class )
	@Label( standard = "Version" )
	@DefaultValue( text = "2.0" )
	@Required
	@XmlBinding( path = "@version" )
	ValueProperty PROP_VERSION = new ValueProperty( TYPE, "Version" );

	Value<PortletAppVersion> getVersion();

	void setVersion( PortletAppVersion version );

	void setVersion( String version );

	/*
	 * Portlets
	 */

	@Type( base = IPortlet.class )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "portlet", type = IPortlet.class ) )
	ListProperty PROP_PORTLETS = new ListProperty( TYPE, "Portlets" );

	ModelElementList<IPortlet> getPortlets();

	// *** CustomPortletMode ***

	@Type( base = ICustomPortletMode.class )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "custom-portlet-mode", type = ICustomPortletMode.class ) )
	ListProperty PROP_CUSTOM_PORTLET_MODES = new ListProperty( TYPE, "CustomPortletModes" );

	ModelElementList<ICustomPortletMode> getCustomPortletModes();

	// *** CustomWindowStates ***

	@Type( base = ICustomWindowState.class )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "custom-window-state", type = ICustomWindowState.class ) )
	ListProperty PROP_CUSTOM_WINDOW_STATES = new ListProperty( TYPE, "CustomWindowStates" );

	ModelElementList<ICustomWindowState> getCustomWindowStates();

	// *** UserAttributes ***

	@Type( base = IUserAttribute.class )
	@Label( standard = "User Attribute" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( type = IUserAttribute.class, element = "user-attribute" ) } )
	ListProperty PROP_USER_ATTRIBUTES = new ListProperty( TYPE, "UserAttributes" );

	ModelElementList<IUserAttribute> getUserAttributes();

	// *** SecurityConstraints ***

	@Type( base = ISecurityConstraint.class )
	@Label( standard = "Security Constraints" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( type = ISecurityConstraint.class, element = "security-constraint" ) } )
	ListProperty PROP_SECURITY_CONSTRAINTS = new ListProperty( TYPE, "SecurityConstraints" );

	ModelElementList<ISecurityConstraint> getSecurityConstraints();

	// *** Filters ***

	@Type( base = IFilter.class )
	@Label( standard = "filters" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "filter", type = IFilter.class ) )
	ListProperty PROP_FILTERS = new ListProperty( TYPE, "Filters" );

	ModelElementList<IFilter> getFilters();

	// *** FilterMappings ***

	@Type( base = IFilterMapping.class )
	@Label( standard = "filter mappings" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "filter-mapping", type = IFilterMapping.class ) )
	ListProperty PROP_FILTER_MAPPINGS = new ListProperty( TYPE, "FilterMappings" );

	ModelElementList<IFilterMapping> getFilterMappings();

	// *** DefaultNameSpace ***

	@Label( standard = "Default Namespace" )
	@NoDuplicates
	@Whitespace( trim = true )
	@XmlBinding( path = "default-namespace" )
	ValueProperty PROP_DEFAULT_NAMESPACE = new ValueProperty( TYPE, "DefaultNameSpace" );

	Value<String> getDefaultNameSpace();

	void setDefaultNameSpace( String value );

	// *** EventDefinitions ***

	@Type( base = IEventDefinition.class )
	@Label( standard = "Event Definitions" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "event-definition", type = IEventDefinition.class ) )
	ListProperty PROP_EVENT_DEFINITIONS = new ListProperty( TYPE, "EventDefinitions" );

	ModelElementList<IEventDefinition> getEventDefinitions();

	// *** PublicRenderParameters ***

	@Type( base = IPublicRenderParameter.class )
	@Label( standard = "Public Render Parameters" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "public-render-parameter", type = IPublicRenderParameter.class ) )
	ListProperty PROP_PUBLIC_RENDER_PARAMETERS = new ListProperty( TYPE, "PublicRenderParameters" );

	ModelElementList<IPublicRenderParameter> getPublicRenderParameters();

	// *** Listeners ***

	@Type( base = IListener.class )
	@Label( standard = "listeners" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "listener", type = IListener.class ) )
	ListProperty PROP_LISTENERS = new ListProperty( TYPE, "Listeners" );

	ModelElementList<IListener> getListeners();

	// *** ContainerRuntimeOptions ***

	@Type( base = IContainerRuntimeOption.class )
	@Label( standard = "Container Runtime Options" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "container-runtime-option", type = IContainerRuntimeOption.class ) )
	ListProperty PROP_CONTAINER_RUNTIME_OPTIONS = new ListProperty( TYPE, "ContainerRuntimeOptions" );

	ModelElementList<IContainerRuntimeOption> getContainerRuntimeOptions();

}
