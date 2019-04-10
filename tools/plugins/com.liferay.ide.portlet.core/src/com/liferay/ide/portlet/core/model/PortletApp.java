/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 */

package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlNamespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlSchema;

/**
 * The root container model class that will have &lt;portlet-app&gt;
 *
 * @author Kamesh Sampath <br/>
 * @author Gregory Amerson
 */
@Image(path = "images/obj16/portlet_model_obj.gif")
@XmlBinding(path = "portlet-app")
@XmlNamespace(prefix = "", uri = "http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd")
@XmlSchema(
	location = "http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd",
	namespace = "http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd"
)
public interface PortletApp extends Identifiable, ResourceBundle {

	public ElementType TYPE = new ElementType(PortletApp.class);

	public ElementList<ContainerRuntimeOption> getContainerRuntimeOptions();

	public ElementList<CustomPortletMode> getCustomPortletModes();

	public ElementList<CustomWindowState> getCustomWindowStates();

	public Value<String> getDefaultNameSpace();

	public ElementList<EventDefinition> getEventDefinitions();

	public ElementList<FilterMapping> getFilterMappings();

	public ElementList<Filter> getFilters();

	public ElementList<Listener> getListeners();

	public ElementList<Portlet> getPortlets();

	public ElementList<PublicRenderParameter> getPublicRenderParameters();

	public ElementList<SecurityConstraint> getSecurityConstraints();

	public ElementList<UserAttribute> getUserAttributes();

	public void setDefaultNameSpace(String value);

	@Label(standard = "Container Runtime Options")
	@Type(base = ContainerRuntimeOption.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "container-runtime-option", type = ContainerRuntimeOption.class)
	)
	public ListProperty PROP_CONTAINER_RUNTIME_OPTIONS = new ListProperty(TYPE, "ContainerRuntimeOptions");

	@Type(base = CustomPortletMode.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "custom-portlet-mode", type = CustomPortletMode.class))
	public ListProperty PROP_CUSTOM_PORTLET_MODES = new ListProperty(TYPE, "CustomPortletModes");

	@Type(base = CustomWindowState.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "custom-window-state", type = CustomWindowState.class))
	public ListProperty PROP_CUSTOM_WINDOW_STATES = new ListProperty(TYPE, "CustomWindowStates");

	@Label(standard = "Default Namespace")
	@Unique
	@XmlBinding(path = "default-namespace")
	public ValueProperty PROP_DEFAULT_NAMESPACE = new ValueProperty(TYPE, "DefaultNameSpace");

	@Label(standard = "Event Definitions")
	@Type(base = EventDefinition.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "event-definition", type = EventDefinition.class))
	public ListProperty PROP_EVENT_DEFINITIONS = new ListProperty(TYPE, "EventDefinitions");

	@Label(standard = "filter mappings")
	@Type(base = FilterMapping.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "filter-mapping", type = FilterMapping.class))
	public ListProperty PROP_FILTER_MAPPINGS = new ListProperty(TYPE, "FilterMappings");

	@Label(standard = "filters")
	@Type(base = Filter.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "filter", type = Filter.class))
	public ListProperty PROP_FILTERS = new ListProperty(TYPE, "Filters");

	@Label(standard = "listeners")
	@Type(base = Listener.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "listener", type = Listener.class))
	public ListProperty PROP_LISTENERS = new ListProperty(TYPE, "Listeners");

	@Type(base = Portlet.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "portlet", type = Portlet.class))
	public ListProperty PROP_PORTLETS = new ListProperty(TYPE, "Portlets");

	@Label(standard = "Public Render Parameters")
	@Type(base = PublicRenderParameter.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "public-render-parameter", type = PublicRenderParameter.class)
	)
	public ListProperty PROP_PUBLIC_RENDER_PARAMETERS = new ListProperty(TYPE, "PublicRenderParameters");

	@Label(standard = "Security Constraints")
	@Type(base = SecurityConstraint.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "security-constraint", type = SecurityConstraint.class)
	)
	public ListProperty PROP_SECURITY_CONSTRAINTS = new ListProperty(TYPE, "SecurityConstraints");

	@Label(standard = "User Attribute")
	@Type(base = UserAttribute.class)
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "user-attribute", type = UserAttribute.class)})
	public ListProperty PROP_USER_ATTRIBUTES = new ListProperty(TYPE, "UserAttributes");

}