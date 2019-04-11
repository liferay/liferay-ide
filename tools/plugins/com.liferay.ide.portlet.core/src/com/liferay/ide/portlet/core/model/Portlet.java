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
public interface Portlet extends Describeable, Displayable, Identifiable, ResourceBundle {

	public ElementType TYPE = new ElementType(Portlet.class);

	public Value<String> getCacheScope();

	public ElementList<ContainerRuntimeOption> getContainerRuntimeOptions();

	public Value<Integer> getExpirationCache();

	public ElementList<Param> getInitParams();

	public ReferenceValue<JavaTypeName, JavaType> getPortletClass();

	public PortletInfo getPortletInfo();

	public Value<String> getPortletName();

	public PortletPreference getPortletPreference();

	public ElementList<SecurityRoleRef> getSecurityRoleRefs();

	public ElementList<SupportedLocales> getSupportedLocales();

	public ElementList<EventDefinitionRef> getSupportedProcessingEvents();

	public ElementList<SupportedPublicRenderParameter> getSupportedPublicRenderParameters();

	public ElementList<EventDefinitionRef> getSupportedPublishingEvents();

	public Supports getSupports();

	public void setCacheScope(String value);

	public void setExpirationCache(Integer value);

	public void setExpirationCache(String value);

	public void setPortletClass(JavaTypeName portletClass);

	public void setPortletClass(String portletClass);

	public void setPortletName(String portletName);

	@Label(standard = "Cache scope")
	@Length(max = 1, min = 0)
	@XmlBinding(path = "cache-scope")
	public ValueProperty PROP_CACHE_SCOPE = new ValueProperty(TYPE, "CacheScope");

	@Label(standard = "Container Runtime Options")
	@Type(base = ContainerRuntimeOption.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "container-runtime-option", type = ContainerRuntimeOption.class)
	)
	public ListProperty PROP_CONTAINER_RUNTIME_OPTIONS = new ListProperty(TYPE, "ContainerRuntimeOptions");

	@Label(standard = "Expiration Cache")
	@Length(max = 1, min = 0)
	@Type(base = Integer.class)
	@XmlBinding(path = "expiration-cache")
	public ValueProperty PROP_EXPIRATION_CACHE = new ValueProperty(TYPE, "ExpirationCache");

	@Type(base = Param.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "init-param", type = Param.class))
	public ListProperty PROP_INIT_PARAMS = new ListProperty(TYPE, "InitParams");

	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = "javax.portlet.GenericPortlet")
	@Label(standard = "Portlet class")
	@MustExist
	@Reference(target = JavaType.class)
	@Required
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "portlet-class")
	public ValueProperty PROP_PORTLET_CLASS = new ValueProperty(TYPE, "PortletClass");

	@Label(standard = "Portlet Information")
	@Type(base = PortletInfo.class)
	@XmlBinding(path = "portlet-info")
	public ImpliedElementProperty PROP_PORTLET_INFO = new ImpliedElementProperty(TYPE, "PortletInfo");

	@Label(standard = "Portlet name")
	@MustExist
	@Required
	@Unique
	@XmlBinding(path = "portlet-name")
	public ValueProperty PROP_PORTLET_NAME = new ValueProperty(TYPE, "PortletName");

	@Label(standard = "label")
	@Length(max = 1, min = 0)
	@Type(base = PortletPreference.class)
	@XmlBinding(path = "portlet-preferences")
	public ImpliedElementProperty PROP_PORTLET_PREFERENCE = new ImpliedElementProperty(TYPE, "PortletPreference");

	@Label(standard = "Security Role Ref")
	@Type(base = SecurityRoleRef.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "security-role-ref", type = SecurityRoleRef.class))
	public ListProperty PROP_SECURITY_ROLE_REFS = new ListProperty(TYPE, "SecurityRoleRefs");

	@Label(standard = "Supported Locales")
	@Type(base = SupportedLocales.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "supported-locale", type = SupportedLocales.class))
	public ListProperty PROP_SUPPORTED_LOCALES = new ListProperty(TYPE, "SupportedLocales");

	@Label(standard = "Supported Processing Events")
	@Type(base = EventDefinitionRef.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "supported-processing-event", type = EventDefinitionRef.class)
	)
	public ListProperty PROP_SUPPORTED_PROCESSING_EVENTS = new ListProperty(TYPE, "SupportedProcessingEvents");

	@Label(standard = "Public Render Parameters")
	@Type(base = SupportedPublicRenderParameter.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "supported-public-render-parameter", type = SupportedPublicRenderParameter.class)
	)
	public ListProperty PROP_SUPPORTED_PUBLIC_RENDER_PARAMETERS = new ListProperty(
		TYPE, "SupportedPublicRenderParameters");

	@Label(standard = "Supported Publishing Events")
	@Type(base = EventDefinitionRef.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "supported-publishing-event", type = EventDefinitionRef.class)
	)
	public ListProperty PROP_SUPPORTED_PUBLISHING_EVENTS = new ListProperty(TYPE, "SupportedPublishingEvents");

	@Label(standard = "Supports")
	@Required
	@Type(base = Supports.class)
	@XmlBinding(path = "supports")
	public ImpliedElementProperty PROP_SUPPORTS = new ImpliedElementProperty(TYPE, "Supports");

}