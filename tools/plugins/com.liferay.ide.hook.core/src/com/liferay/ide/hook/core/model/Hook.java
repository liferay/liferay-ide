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

package com.liferay.ide.hook.core.model;

import com.liferay.ide.hook.core.model.internal.CustomJspDirListener;
import com.liferay.ide.hook.core.model.internal.CustomJspsBindingImpl;
import com.liferay.ide.hook.core.model.internal.CustomJspsEnablementService;
import com.liferay.ide.hook.core.model.internal.PortalPropertiesFileListener;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.FixedOrderList;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public interface Hook extends Element {

	public ElementType TYPE = new ElementType(Hook.class);

	public ElementHandle<CustomJspDir> getCustomJspDir();

	public Value<Boolean> getCustomJspGlobal();

	public ElementList<CustomJsp> getCustomJsps();

	public ElementList<IndexerPostProcessor> getIndexerPostProcessors();

	public ElementList<LanguageProperty> getLanguageProperties();

	public ElementHandle<PortalPropertiesFile> getPortalPropertiesFile();

	public ElementList<ServiceWrapper> getServices();

	public ElementList<ServletFilterMapping> getServletFilterMappings();

	public ElementList<ServletFilter> getServletFilters();

	public ElementList<StrutsAction> getStrutsActions();

	public void setCustomJspGlobal(Boolean value);

	public void setCustomJspGlobal(String value);

	@Label(standard = "Custom JSP Dir")
	@Listeners(CustomJspDirListener.class)
	@Type(base = CustomJspDir.class)
	@XmlBinding(path = "custom-jsp-dir")
	public ElementProperty PROP_CUSTOM_JSP_DIR = new ElementProperty(TYPE, "CustomJspDir");

	@DefaultValue(text = "true")
	@Label(standard = "Custom JSP Global")
	@Type(base = Boolean.class)
	@XmlBinding(path = "custom-jsp-global")
	public ValueProperty PROP_CUSTOM_JSP_GLOBAL = new ValueProperty(TYPE, "CustomJspGlobal");

	@CustomXmlListBinding(impl = CustomJspsBindingImpl.class)
	@FixedOrderList
	@Label(standard = "custom jsps")
	@Service(impl = CustomJspsEnablementService.class)
	@Type(base = CustomJsp.class)
	public ListProperty PROP_CUSTOM_JSPS = new ListProperty(TYPE, "CustomJsps");

	@Label(standard = "Index Post Processors")
	@Type(base = IndexerPostProcessor.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "indexer-post-processor", type = IndexerPostProcessor.class)
	)
	public ListProperty PROP_INDEXER_POST_PROCESSORS = new ListProperty(TYPE, "IndexerPostProcessors");

	@Label(standard = "Language Properties")
	@Type(base = LanguageProperty.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "language-properties", type = LanguageProperty.class))
	public ListProperty PROP_LANGUAGE_PROPERTIES = new ListProperty(TYPE, "LanguageProperties");

	@Listeners(PortalPropertiesFileListener.class)
	@Type(base = PortalPropertiesFile.class)
	@XmlBinding(path = "portal-properties")
	public ElementProperty PROP_PORTAL_PROPERTIES_FILE = new ElementProperty(TYPE, "PortalPropertiesFile");

	@Label(standard = "Service Wrappers")
	@Type(base = ServiceWrapper.class)
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "service", type = ServiceWrapper.class)})
	public ListProperty PROP_SERVICES = new ListProperty(TYPE, "Services");

	@Label(standard = "Servlet Filter Mappings")
	@Type(base = ServletFilterMapping.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "servlet-filter-mapping", type = ServletFilterMapping.class)
	)
	public ListProperty PROP_SERVLET_FILTER_MAPPINGS = new ListProperty(TYPE, "ServletFilterMappings");

	@Label(standard = "Servlet filters")
	@Type(base = ServletFilter.class)
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "servlet-filter", type = ServletFilter.class)})
	public ListProperty PROP_SERVLET_FILTERS = new ListProperty(TYPE, "ServletFilters");

	@Label(standard = "Struts Actions")
	@Type(base = StrutsAction.class)
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "struts-action", type = StrutsAction.class)})
	public ListProperty PROP_STRUTS_ACTIONS = new ListProperty(TYPE, "StrutsActions");

}