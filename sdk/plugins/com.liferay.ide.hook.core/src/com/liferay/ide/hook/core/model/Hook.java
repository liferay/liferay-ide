/*******************************************************************************
 *  Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 *          Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.hook.core.model;

import com.liferay.ide.hook.core.model.internal.CustomJspsBindingImpl;
import com.liferay.ide.hook.core.model.internal.PortalPropertiesBindingImpl;
import com.liferay.ide.hook.core.model.internal.PortalPropertiesOverridesEnablementService;

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
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@GenerateImpl
public interface Hook extends IModelElement
{

    ModelElementType TYPE = new ModelElementType( Hook.class );

    // *** Version ***

    @Type( base = HookVersionType.class )
    @Label( standard = "Version" )
    TransientProperty PROP_VERSION = new TransientProperty( TYPE, "Version" ); //$NON-NLS-1$

    Transient<HookVersionType> getVersion();

    void setVersion( HookVersionType value );

    // *** PortalPropertiesFile ***

    @Type( base = PortalPropertiesFile.class )
    @XmlBinding( path = "portal-properties" )
    ElementProperty PROP_PORTAL_PROPERTIES_FILE = new ElementProperty( TYPE, "PortalPropertiesFile" ); //$NON-NLS-1$

    ModelElementHandle<PortalPropertiesFile> getPortalPropertiesFile();

    // *** PortalProperties ***

    @Type( base = PortalProperty.class )
    @Label( standard = "Portal Properties Overrides" )
    @FixedOrderList
    @Service( impl = PortalPropertiesOverridesEnablementService.class )
    @CustomXmlListBinding( impl = PortalPropertiesBindingImpl.class )
    ListProperty PROP_PORTAL_PROPERTIES_OVERRIDES = new ListProperty( TYPE, "PortalPropertiesOverrides" ); //$NON-NLS-1$

    ModelElementList<PortalProperty> getPortalPropertiesOverrides();

    // *** LanguageProperties ***

    @Type( base = LanguageProperty.class )
    @Label( standard = "Language Properties" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping(
                    element = "language-properties",
                    type = LanguageProperty.class ) } )
    ListProperty PROP_LANGUAGE_PROPERTIES = new ListProperty( TYPE, "LanguageProperties" ); //$NON-NLS-1$

    ModelElementList<LanguageProperty> getLanguageProperties();

    @Type( base = CustomJsp.class )
    @Label( standard = "custom jsps" )
    @FixedOrderList
    @CustomXmlListBinding( impl = CustomJspsBindingImpl.class )
    @DependsOn( value = { "CustomJspDir/Value" } )
    ListProperty PROP_CUSTOM_JSPS = new ListProperty( TYPE, "CustomJsps" ); //$NON-NLS-1$

    ModelElementList<CustomJsp> getCustomJsps();

    // *** CustomJspDir ***

    @Type( base = CustomJspDir.class )
    @Label( standard = "Custom JSP Dir" )
    @XmlBinding( path = "custom-jsp-dir" )
    ElementProperty PROP_CUSTOM_JSP_DIR = new ElementProperty( TYPE, "CustomJspDir" ); //$NON-NLS-1$

    ModelElementHandle<CustomJspDir> getCustomJspDir();

    // *** CustomJspGlobal ***

    @Type( base = Boolean.class )
    @Label( standard = "Custom JSP Global" )
    @XmlBinding( path = "custom-jsp-global" )
    @DefaultValue( text = "true" )
    ValueProperty PROP_CUSTOM_JSP_GLOBAL = new ValueProperty( TYPE, "CustomJspGlobal" ); //$NON-NLS-1$

    Value<Boolean> getCustomJspGlobal();

    void setCustomJspGlobal( String value );

    void setCustomJspGlobal( Boolean value );

    // *** IndexerPostProcessors ***

    @Type( base = IndexerPostProcessor.class )
    @Label( standard = "Index Post Processors" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping(
                    element = "indexer-post-processor",
                    type = IndexerPostProcessor.class ) } )
    ListProperty PROP_INDEXER_POST_PROCESSORS = new ListProperty( TYPE, "IndexerPostProcessors" ); //$NON-NLS-1$

    ModelElementList<IndexerPostProcessor> getIndexerPostProcessors();

    // *** Services ***

    @Type( base = ServiceWrapper.class )
    @Label( standard = "Service Wrappers" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "service", type = ServiceWrapper.class ) } )
    ListProperty PROP_SERVICES = new ListProperty( TYPE, "Services" ); //$NON-NLS-1$

    ModelElementList<ServiceWrapper> getServices();

    // *** ServletFilters ***

    @Type( base = ServletFilter.class )
    @Label( standard = "Servlet filters" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "servlet-filter", type = ServletFilter.class ) } )
    ListProperty PROP_SERVLET_FILTERS = new ListProperty( TYPE, "ServletFilters" ); //$NON-NLS-1$

    ModelElementList<ServletFilter> getServletFilters();

    // *** ServletFilterMappings ***

    @Type( base = ServletFilterMapping.class )
    @Label( standard = "Servlet Filter Mappings" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping(
                    element = "servlet-filter-mapping",
                    type = ServletFilterMapping.class ) } )
    ListProperty PROP_SERVLET_FILTER_MAPPINGS = new ListProperty( TYPE, "ServletFilterMappings" ); //$NON-NLS-1$

    ModelElementList<ServletFilterMapping> getServletFilterMappings();

    // *** StrutsActions ***

    @Type( base = StrutsAction.class )
    @Label( standard = "Struts Actions" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "struts-action", type = StrutsAction.class ) } )
    ListProperty PROP_STRUTS_ACTIONS = new ListProperty( TYPE, "StrutsActions" ); //$NON-NLS-1$

    ModelElementList<StrutsAction> getStrutsActions();

}
