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
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 * @author Simon Jiang
 */
@Image(path = "images/elcl16/portlet_16x16.png")
public interface Portlet extends ResourceBundle, Identifiable, Describeable, Displayable {

	public ElementType TYPE = new ElementType(Portlet.class);

	// *** Portlet Name ***

	@Label(standard = "Portlet name")
	@Required
	@Unique
	@MustExist
	@XmlBinding(path = "portlet-name")
	public 	ValueProperty PROP_PORTLET_NAME = new ValueProperty(TYPE, "PortletName");

	public 	Value<String> getPortletName();

	public void setPortletName(String portletName);

	// *** Portlet Class ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"javax.portlet.GenericPortlet"})
	@Label(standard = "Portlet class")
	@MustExist
	@Required
	@XmlBinding(path = "portlet-class")
	public ValueProperty PROP_PORTLET_CLASS = new ValueProperty(TYPE, "PortletClass");

	public ReferenceValue<JavaTypeName, JavaType> getPortletClass();

	public void setPortletClass(String portletClass);

	public void setPortletClass(JavaTypeName portletClass);

	// *** ExpirationCache ***

	@Type(base = Integer.class)
	@Label(standard = "Expiration Cache")
	@Length(min = 0, max = 1)
	@XmlBinding(path = "expiration-cache")
	public 	ValueProperty PROP_EXPIRATION_CACHE = new ValueProperty(TYPE, "ExpirationCache");

	public Value<Integer> getExpirationCache();

	public void setExpirationCache(String value);

	public void setExpirationCache(Integer value);

	// *** CacheScope ***

	@Label(standard = "Cache scope")
	@XmlBinding(path = "cache-scope")
	@Length(min = 0, max = 1)
	ValueProperty PROP_CACHE_SCOPE = new ValueProperty(TYPE, "CacheScope");

	Value<String> getCacheScope();

	void setCacheScope(String value);

	// *** Supports ***

	@Type(base = Supports.class)
	@Label(standard = "Supports")
	@Required
	@XmlBinding(path = "supports")
	ImpliedElementProperty PROP_SUPPORTS = new ImpliedElementProperty(TYPE, "Supports");

	Supports getSupports();

	// *** SupportedLocales ***

	@Type(base = SupportedLocales.class)
	@Label(standard = "Supported Locales")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "supported-locale", type = SupportedLocales.class))
	ListProperty PROP_SUPPORTED_LOCALES = new ListProperty(TYPE, "SupportedLocales");

	ElementList<SupportedLocales> getSupportedLocales();

	// *** PortletInfo ***

	@Type(base = PortletInfo.class)
	@Label(standard = "Portlet Information")
	@XmlBinding(path = "portlet-info")
	ImpliedElementProperty PROP_PORTLET_INFO = new ImpliedElementProperty(TYPE, "PortletInfo");

	PortletInfo getPortletInfo();

	// *** PortletPreference ***

	@Type(base = PortletPreference.class)
	@Length(min = 0, max = 1)
	@Label(standard = "label")
	@XmlBinding(path = "portlet-preferences")
	ImpliedElementProperty PROP_PORTLET_PREFERENCE = new ImpliedElementProperty(TYPE, "PortletPreference");

	PortletPreference getPortletPreference();

	// *** Init Params ***

	@Type(base = Param.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "init-param", type = Param.class))
	ListProperty PROP_INIT_PARAMS = new ListProperty(TYPE, "InitParams");

	ElementList<Param> getInitParams();

	// *** SecurityRoleRefs ***

	@Type(base = SecurityRoleRef.class)
	@Label(standard = "Security Role Ref")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "security-role-ref", type = SecurityRoleRef.class))
	ListProperty PROP_SECURITY_ROLE_REFS = new ListProperty(TYPE, "SecurityRoleRefs");

	ElementList<SecurityRoleRef> getSecurityRoleRefs();

	// *** SupportedProcessingEvents ***

	@Type(base = EventDefinitionRef.class)
	@Label(standard = "Supported Processing Events")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "supported-processing-event", type = EventDefinitionRef.class))
	ListProperty PROP_SUPPORTED_PROCESSING_EVENTS = new ListProperty(TYPE, "SupportedProcessingEvents");

	ElementList<EventDefinitionRef> getSupportedProcessingEvents();

	// *** Supported Publishing Events ***

	@Type(base = EventDefinitionRef.class)
	@Label(standard = "Supported Publishing Events")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "supported-publishing-event", type = EventDefinitionRef.class))
	ListProperty PROP_SUPPORTED_PUBLISHING_EVENTS = new ListProperty(TYPE, "SupportedPublishingEvents");

	ElementList<EventDefinitionRef> getSupportedPublishingEvents();

	// *** SupportedPublicRenderParameters ***

	@Type(base = SupportedPublicRenderParameter.class)
	@Label(standard = "Public Render Parameters")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "supported-public-render-parameter", type = SupportedPublicRenderParameter.class))
	ListProperty PROP_SUPPORTED_PUBLIC_RENDER_PARAMETERS = new ListProperty(TYPE, "SupportedPublicRenderParameters");

	ElementList<SupportedPublicRenderParameter> getSupportedPublicRenderParameters();

	// *** ContainerRuntimeOptions ***

	@Type(base = ContainerRuntimeOption.class)
	@Label(standard = "Container Runtime Options")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "container-runtime-option", type = ContainerRuntimeOption.class))
	ListProperty PROP_CONTAINER_RUNTIME_OPTIONS = new ListProperty(TYPE, "ContainerRuntimeOptions");

	ElementList<ContainerRuntimeOption> getContainerRuntimeOptions();

}