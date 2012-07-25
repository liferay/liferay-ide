/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.core.model;

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
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlNamespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlSchema;

/**
 * The root container model class that will have &lt;portlet-app&gt;
 * 
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a> <br/>
 * @author Gregory Amerson
 */
@GenerateImpl
@Image( path = "images/obj16/portlet_model_obj.gif" )
@XmlNamespace( prefix = "", uri = "http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd" )
@XmlSchema
(
    namespace = "http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd",
    location = "http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd"
)
@XmlBinding( path = "portlet-app" )
public interface PortletApp extends ResourceBundle, Identifiable
{

    ModelElementType TYPE = new ModelElementType( PortletApp.class );

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

    @Type( base = Portlet.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "portlet", type = Portlet.class ) )
    ListProperty PROP_PORTLETS = new ListProperty( TYPE, "Portlets" );

    ModelElementList<Portlet> getPortlets();

    // *** CustomPortletMode ***

    @Type( base = CustomPortletMode.class )
    @XmlListBinding
    ( 
        mappings = @XmlListBinding.Mapping
        (
            element = "custom-portlet-mode",
            type = CustomPortletMode.class
        ) 
    )
    ListProperty PROP_CUSTOM_PORTLET_MODES = new ListProperty( TYPE, "CustomPortletModes" );

    ModelElementList<CustomPortletMode> getCustomPortletModes();

    // *** CustomWindowStates ***

    @Type( base = CustomWindowState.class )
    @XmlListBinding
    ( 
        mappings = @XmlListBinding.Mapping
        (
            element = "custom-window-state",
            type = CustomWindowState.class
        )
    )
    ListProperty PROP_CUSTOM_WINDOW_STATES = new ListProperty( TYPE, "CustomWindowStates" );

    ModelElementList<CustomWindowState> getCustomWindowStates();

    // *** UserAttributes ***

    @Type( base = UserAttribute.class )
    @Label( standard = "User Attribute" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( type = UserAttribute.class, element = "user-attribute" ) } )
    ListProperty PROP_USER_ATTRIBUTES = new ListProperty( TYPE, "UserAttributes" );

    ModelElementList<UserAttribute> getUserAttributes();

    // *** SecurityConstraints ***

    @Type( base = SecurityConstraint.class )
    @Label( standard = "Security Constraints" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping(
                    type = SecurityConstraint.class,
                    element = "security-constraint" ) } )
    ListProperty PROP_SECURITY_CONSTRAINTS = new ListProperty( TYPE, "SecurityConstraints" );

    ModelElementList<SecurityConstraint> getSecurityConstraints();

    // *** Filters ***

    @Type( base = Filter.class )
    @Label( standard = "filters" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "filter", type = Filter.class ) )
    ListProperty PROP_FILTERS = new ListProperty( TYPE, "Filters" );

    ModelElementList<Filter> getFilters();

    // *** FilterMappings ***

    @Type( base = FilterMapping.class )
    @Label( standard = "filter mappings" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "filter-mapping", type = FilterMapping.class ) )
    ListProperty PROP_FILTER_MAPPINGS = new ListProperty( TYPE, "FilterMappings" );

    ModelElementList<FilterMapping> getFilterMappings();

    // *** DefaultNameSpace ***

    @Label( standard = "Default Namespace" )
    @NoDuplicates
    @XmlBinding( path = "default-namespace" )
    ValueProperty PROP_DEFAULT_NAMESPACE = new ValueProperty( TYPE, "DefaultNameSpace" );

    Value<String> getDefaultNameSpace();

    void setDefaultNameSpace( String value );

    // *** EventDefinitions ***

    @Type( base = EventDefinition.class )
    @Label( standard = "Event Definitions" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "event-definition", type = EventDefinition.class ) )
    ListProperty PROP_EVENT_DEFINITIONS = new ListProperty( TYPE, "EventDefinitions" );

    ModelElementList<EventDefinition> getEventDefinitions();

    // *** PublicRenderParameters ***

    @Type( base = PublicRenderParameter.class )
    @Label( standard = "Public Render Parameters" )
    @XmlListBinding
    ( 
        mappings = @XmlListBinding.Mapping
        (
            element = "public-render-parameter",
            type = PublicRenderParameter.class
        ) 
    )
    ListProperty PROP_PUBLIC_RENDER_PARAMETERS = new ListProperty( TYPE, "PublicRenderParameters" );

    ModelElementList<PublicRenderParameter> getPublicRenderParameters();

    // *** Listeners ***

    @Type( base = Listener.class )
    @Label( standard = "listeners" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "listener", type = Listener.class ) )
    ListProperty PROP_LISTENERS = new ListProperty( TYPE, "Listeners" );

    ModelElementList<Listener> getListeners();

    // *** ContainerRuntimeOptions ***

    @Type( base = ContainerRuntimeOption.class )
    @Label( standard = "Container Runtime Options" )
    @XmlListBinding
    ( 
        mappings = @XmlListBinding.Mapping
        (
            element = "container-runtime-option",
            type = ContainerRuntimeOption.class
        )
    )
    ListProperty PROP_CONTAINER_RUNTIME_OPTIONS = new ListProperty( TYPE, "ContainerRuntimeOptions" );

    ModelElementList<ContainerRuntimeOption> getContainerRuntimeOptions();

}
