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

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.DefaultXmlBinding;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Image( path = "images/obj16/portlet_class_obj.gif" )
public interface IPortlet extends IResourceBundle, IIdentifiable, IDescribeable {

	ModelElementType TYPE = new ModelElementType( IPortlet.class );

	// *** Portlet Name ***
	@Label( standard = "Portlet name" )
	@Required
	@NoDuplicates
	@MustExist
	@XmlBinding( path = "portlet-name" )
	@Whitespace( trim = true )
	ValueProperty PROP_PORTLET_NAME = new ValueProperty( TYPE, "PortletName" );

	Value<String> getPortletName();

	void setPortletName( String portletName );

	// *** Portlet Display Name ***

	@Label( standard = "Display name" )
	@NoDuplicates
	@XmlBinding( path = "display-name" )
	@Whitespace( trim = true )
	ValueProperty PROP_DISPLAY_NAME = new ValueProperty( TYPE, "DisplayName" );

	Value<String> getDisplayName();

	void setDisplayName( String displayName );

	// *** Portlet Class ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "javax.portlet.GenericPortlet" } )
	@Label( standard = "Portlet class" )
	@Required
	@XmlBinding( path = "portlet-class" )
	ValueProperty PROP_PORTLET_CLASS = new ValueProperty( TYPE, "PortletClass" );

	ReferenceValue<JavaTypeName, JavaType> getPortletClass();

	void setPortletClass( String portletClass );

	void setPortletClass( JavaTypeName portletClass );

	// *** ExpirationCache ***

	@Type( base = Integer.class )
	@Label( standard = "Expiration Cache" )
	@CountConstraint( min = 0, max = 1 )
	@XmlBinding( path = "expiration-cache" )
	ValueProperty PROP_EXPIRATION_CACHE = new ValueProperty( TYPE, "ExpirationCache" );

	Value<Integer> getExpirationCache();

	void setExpirationCache( String value );

	void setExpirationCache( Integer value );

	// *** CacheScope ***

	@Label( standard = "Cache scope" )
	@XmlBinding( path = "cache-scope" )
	@CountConstraint( min = 0, max = 1 )
	@Whitespace( trim = true )
	ValueProperty PROP_CACHE_SCOPE = new ValueProperty( TYPE, "CacheScope" );

	Value<String> getCacheScope();

	void setCacheScope( String value );

	// *** Supports ***

	@Type( base = ISupports.class )
	@Label( standard = "Supports" )
	@Required
	@CustomXmlElementBinding( impl = DefaultXmlBinding.class, params = { "supports" } )
	ImpliedElementProperty PROP_SUPPORTS = new ImpliedElementProperty( TYPE, "Supports" );

	ISupports getSupports();

	// *** SupportedLocales ***

	@Type( base = ISupportedLocales.class )
	@Label( standard = "Supported Locales" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "supported-locale", type = ISupportedLocales.class ) )
	ListProperty PROP_SUPPORTED_LOCALES = new ListProperty( TYPE, "SupportedLocales" );

	ModelElementList<ISupportedLocales> getSupportedLocales();

	// *** PortletInfo ***

	@Type( base = IPortletInfo.class )
	@Label( standard = "Portlet Information" )
	@XmlBinding( path = "portlet-info" )
	ImpliedElementProperty PROP_PORTLET_INFO = new ImpliedElementProperty( TYPE, "PortletInfo" );

	IPortletInfo getPortletInfo();

	// *** PortletPreference ***

	@Type( base = IPortletPreference.class )
	@CountConstraint( min = 0, max = 1 )
	@Label( standard = "label" )
	@XmlBinding( path = "portlet-preferences" )
	ImpliedElementProperty PROP_PORTLET_PREFERENCE = new ImpliedElementProperty( TYPE, "PortletPreference" );

	IPortletPreference getPortletPreference();

	// *** Init Params ***

	@Type( base = IParam.class )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "init-param", type = IParam.class ) )
	ListProperty PROP_INIT_PARAMS = new ListProperty( TYPE, "InitParams" );

	ModelElementList<IParam> getInitParams();

	// *** SecurityRoleRefs ***

	@Type( base = ISecurityRoleRef.class )
	@Label( standard = "Security Role Ref" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "security-role-ref", type = ISecurityRoleRef.class ) )
	ListProperty PROP_SECURITY_ROLE_REFS = new ListProperty( TYPE, "SecurityRoleRefs" );

	ModelElementList<ISecurityRoleRef> getSecurityRoleRefs();

	// *** SupportedProcessingEvents ***

	@Type( base = IEventDefinitionRef.class )
	@Label( standard = "Supported Processing Events" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "supported-processing-event", type = IEventDefinitionRef.class ) )
	ListProperty PROP_SUPPORTED_PROCESSING_EVENTS = new ListProperty( TYPE, "SupportedProcessingEvents" );

	ModelElementList<IEventDefinitionRef> getSupportedProcessingEvents();

	// *** Supported Publishing Events ***

	@Type( base = IEventDefinitionRef.class )
	@Label( standard = "Supported Publishing Events" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "supported-publishing-event", type = IEventDefinitionRef.class ) )
	ListProperty PROP_SUPPORTED_PUBLISHING_EVENTS = new ListProperty( TYPE, "SupportedPublishingEvents" );

	ModelElementList<IEventDefinitionRef> getSupportedPublishingEvents();

	// *** SupportedPublicRenderParameters ***

	@Type( base = ISupportedPublicRenderParameter.class )
	@Label( standard = "Public Render Parameters" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "supported-public-render-parameter", type = ISupportedPublicRenderParameter.class ) )
	ListProperty PROP_SUPPORTED_PUBLIC_RENDER_PARAMETERS = new ListProperty( TYPE, "SupportedPublicRenderParameters" );

	ModelElementList<ISupportedPublicRenderParameter> getSupportedPublicRenderParameters();

	// *** ContainerRuntimeOptions ***

	@Type( base = IContainerRuntimeOption.class )
	@Label( standard = "Container Runtime Options" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "container-runtime-option", type = IContainerRuntimeOption.class ) )
	ListProperty PROP_CONTAINER_RUNTIME_OPTIONS = new ListProperty( TYPE, "ContainerRuntimeOptions" );

	ModelElementList<IContainerRuntimeOption> getContainerRuntimeOptions();

}
