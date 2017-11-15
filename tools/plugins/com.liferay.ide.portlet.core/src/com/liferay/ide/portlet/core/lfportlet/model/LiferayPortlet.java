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

package com.liferay.ide.portlet.core.lfportlet.model;

import com.liferay.ide.portlet.core.lfportlet.model.internal.IconRelativePathService;
import com.liferay.ide.portlet.core.lfportlet.model.internal.LiferayPortletNameValidationService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.FileExtensions;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.NumericRange;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Simon Jiang
 */
@Image(path = "images/elcl16/portlet_16x16.png")
public interface LiferayPortlet extends Element {

	public ElementType TYPE = new ElementType(LiferayPortlet.class);

	// *** portlet-name ***

	@Label(standard = "Portlet Name")
	@Required
	@Service(impl = LiferayPortletNameValidationService.class)
	@Unique
	@XmlBinding(path = "portlet-name")
	public	ValueProperty PROP_PORTLET_NAME = new ValueProperty(TYPE, "PortletName");

	public	Value<String> getPortletName();

	public void setPortletName(String portletName);

	// *** icon? ***

	@Label(standard = "Icon")
	@MustExist
	@Service(impl = IconRelativePathService.class)
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FILE)
	@XmlBinding(path = "icon")
	public	ValueProperty PROP_ICON = new ValueProperty(TYPE, "Icon");

	public	Value<Path> getIcon();

	public void setIcon(Path icon);

	public void setIcon(String icon);

	// css-class-wrapper?

	@Label(standard = "Css Class Wrapper")
	@XmlBinding(path = "css-class-wrapper")
	public	ValueProperty PROP_CSS_CLASS_WRAPPER = new ValueProperty(TYPE, "CssClassWrapper");

	public	Value<String> getCssClassWrapper();

	public void setCssClassWrappper(String value);

	// header-portlet-css*

	@Label(standard = "Header Portlet Css")
	@Type(base = PortletStyleElement.class)
	@FileExtensions(expr = ".css")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "header-portlet-css", type = PortletStyleElement.class))
	public ListProperty PROP_HEADER_PORTLET_CSSES = new ListProperty(TYPE, "HeaderPortletCsses");

	public ElementList<PortletStyleElement> getHeaderPortletCsses();

	// private-request-attributes?

	@DefaultValue(text = "true")
	@Label(standard = "Private Request Attributes")
	@Type(base = Boolean.class)
	@XmlBinding(path = "private-request-attributes")
	public	ValueProperty PROP_REQUEST_SESSION_ATTRIBUTES = new ValueProperty(TYPE, "PrivateRequestAttributes");

	public	Value<Boolean> getPrivateRequestAttributes();

	public void setPrivateRequestAttributes(Boolean value);

	public void setPrivateRequestAttributes(String value);

	// private-session-attributes?

	@DefaultValue(text = "true")
	@Label(standard = "Private Session Attributes")
	@Type(base = Boolean.class)
	@XmlBinding(path = "private-session-attributes")
	public	ValueProperty PROP_PRIVATE_SESSION_ATTRIBUTES = new ValueProperty(TYPE, "PrivateSessionAttributes");

	public	Value<Boolean> getPrivateSessionAttributes();

	public void setPrivateSessionAttributes(Boolean value);

	public void setPrivateSessionAttributes(String value);

	// use-default-template?

	@DefaultValue(text = "true")
	@Label(standard = "Use Default Template")
	@Type(base = Boolean.class)
	@XmlBinding(path = "use-default-template")
	public	ValueProperty PROP_USE_DEFAULT_TEMPLATE = new ValueProperty(TYPE, "UseDefaultTemplate");

	public	Value<Boolean> getUseDefaultTemplate();

	public void setUseDefaultTemplate(Boolean value);

	public void setUseDefaultTemplate(String value);

	// render-weight?

	@DefaultValue(text = "1")
	@Label(standard = " Render Weight")
	@Type(base = Double.class)
	@XmlBinding(path = "render-weight")
	public	ValueProperty PROP_RENDER_WEIGHT = new ValueProperty(TYPE, "RenderWeight");

	public	Value<Double> getRenderWeight();

	public void setRenderWeight(Double value);

	public void setRenderWeight(String value);

	// *** struts-path? ***

	@Label(standard = "Struts Path")
	@XmlBinding(path = "struts-path")
	public	ValueProperty PROP_STRUTS_PATH = new ValueProperty(TYPE, "StrutsPath");

	public	Value<String> getStrutsPath();

	public void setStrutsPath(String value);

	// preferences-owned-by-group?

	@DefaultValue(text = "true")
	@Label(standard = "Preferences Owned By Group")
	@Type(base = Boolean.class)
	@XmlBinding(path = "preferences-owned-by-group")
	public	ValueProperty PROP_PREFERENCES_OWNED_BY_GROUP = new ValueProperty(TYPE, "PreferencesOwnedByGroup");

	public	Value<Boolean> getPreferencesOwnedByGroup();

	public void setPreferencesOwnedByGroup(Boolean value);

	public void setPreferencesOwnedByGroup(String value);

	// *** configuration-action-class? ***

	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.portlet.ConfigurationAction")
	@MustExist
	@Reference(target = JavaType.class)
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "configuration-action-class")
	public	ValueProperty PROP_CONFIGURATION_ACTION_CLASS = new ValueProperty(TYPE, "ConfigurationActionClass");

	public ReferenceValue<JavaTypeName, JavaType> getConfigurationActionClass();

	public void setConfiguration(JavaTypeName value);

	public void setConfiguration(String value);

	// add-default-resource?

	@Label(standard = "Add Default Resource")
	@Type(base = Boolean.class)
	@DefaultValue(text = "false")
	@XmlBinding(path = "add-default-resource")
	public	ValueProperty PROP_ADD_DEFAULT_RESOURDE = new ValueProperty(TYPE, "AddDefaultResource");

	public	Value<Boolean> getAddDefaultResource();

	public void setAddDefaultSystem(Boolean value);

	public void setAddDefaultSystem(String value);

	// footer-portlet-javascript*

	@Label(standard = "Footer Portlet Javascript")
	@Type(base = PortletStyleElement.class)
	@FileExtensions(expr = ".js")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "footer-portlet-javascript", type = PortletStyleElement.class))

	public ListProperty PROP_FOOTER_PORTLET_JAVASCRIPTS = new ListProperty(TYPE, "FooterPortletJavascripts");

	public ElementList<PortletStyleElement> getFooterPortletJavascripts();

	// staged-model-data-handler-class*

	@Label(standard = "Staged Model Data Handler Classes")
	@Type(base = StagedModelDataHandlerClass.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "staged-model-data-handler-class", type = StagedModelDataHandlerClass.class))
	public ListProperty PROP_STAGED_MODEL_DATA_HANDLER_CLASSES = new ListProperty(TYPE, "StagedModelDataHandlerClasses");

	public ElementList<StagedModelDataHandlerClass> getStagedModelDataHandlerClasses();

	// control-panel-entry-category?

	@Label(standard = "Control Panel Entry Category")
	@PossibleValues(invalidValueSeverity = Severity.OK, values = {
		"apps", "configurations", "sites", "users", "site_administration.configuration", "site_administration.content",
		"site_administration.pages", "site_administration.users"

	})
	@XmlBinding(path = "control-panel-entry-category")
	public	ValueProperty PROP_CONTROL_PANEL_ENTRY_CATEGORY = new ValueProperty(TYPE, "ControlPanelEntryCategory");

	public	Value<String> getControlPanelEntryCategory();

	public void setControlPanelEntryCategory(String value);

	// control-panel-weight?

	@Label(standard = "Control Panel Entry Weight")
	@NumericRange(min = "0")
	@Type(base = Double.class)
	@XmlBinding(path = "control-panel-entry-weight")
	public	ValueProperty PROP_CONTROL_PANEL_ENTRY_WEIGHT = new ValueProperty(TYPE, "ControlPanelEntryWeight");

	public	Value<Double> getControlPanelEntryWeight();

	public void setControlPanelEntryWeight(Double value);

	public void setControlPanelEntryWeight(String value);

	// friendly-url-mapper-class?

	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.portlet.FriendlyURLMapper")
	@Label(standard = "Friendly Url Mapper Class")
	@MustExist
	@Reference(target = JavaType.class)
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "friendly-url-mapper-class")
	public	ValueProperty PROP_FRIENDLY_URL_MAPPER_CLASS = new ValueProperty(TYPE, "FriendlyURLMapperClass");

	public ReferenceValue<JavaTypeName, JavaType> getFriendlyURLMapperClass();

	public void setFriendlyURLMapperClass(JavaTypeName value);

	public void setFriendlyURLMapperClass(String value);

	// friendly-url-mapping?

	@Label(standard = "Friendly URL Mapping ")
	@XmlBinding(path = "friendly-url-mapping")
	public	ValueProperty PROP_FRIENDLY_URL_MAPPING = new ValueProperty(TYPE, "FriendlyURLMapping");

	public	Value<String> getFriendlyURLMapping();

	public void setFriendlyURLMapping(String value);

	// friendly-url-routes?

	@Label(standard = "Friendly URL Routes ")
	@XmlBinding(path = "friendly-url-routes")
	public	ValueProperty PROP_FRIENDLY_URL_Routes = new ValueProperty(TYPE, "FriendlyURLRoutes");

	public	Value<String> getFriendlyURLRoutes();

	public void setFriendlyURLRoutes(String value);

	// portlet-data-handler-class?

	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.lar.PortletDataHandler")
	@Label(standard = "Portlet Data Handler Class")
	@MustExist
	@Reference(target = JavaType.class)
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "portlet-data-handler-class")
	public ValueProperty PROP_PORTLET_DATA_HANDLER_CLASS = new ValueProperty(TYPE, "PortletDataHandlerClass");

	public ReferenceValue<JavaTypeName, JavaType> getPortletDataHandlerClass();

	public void setPortletDataHandlerClass(JavaTypeName value);

	public void setPortletDataHandlerClass(String value);

	// system?

	@DefaultValue(text = "false")
	@Label(standard = "Make portlet as system portlet")
	@Type(base = Boolean.class)
	@XmlBinding(path = "system")
	public ValueProperty PROP_SYSTEM = new ValueProperty(TYPE, "System");

	public Value<Boolean> getSystem();

	public void setSystem(Boolean value);

	public void setSystem(String value);

	// instanceable?

	@DefaultValue(text = "false")
	@Label(standard = "Make multiple portlet instance in Web Page")
	@Type(base = Boolean.class)
	@XmlBinding(path = "instanceable")
	public ValueProperty PROP_INSTANCEABLE = new ValueProperty(TYPE, "Instanceable");

	public Value<Boolean> getInstanceable();

	public void setInstanceable(Boolean value);

	public void setInstanceable(String value);

	// scopeable?

	@DefaultValue(text = "false")
	@Label(standard = "Permit user to customize portlet ")
	@Type(base = Boolean.class)
	@XmlBinding(path = "scopeable")
	public ValueProperty PROP_SCOPEABLE = new ValueProperty(TYPE, "Scopeable");

	public Value<Boolean> getScopeable();

	public void setScopeable(Boolean value);

	public void setScopeable(String value);

	// *** indexer-class* ***

	@Label(standard = "Indexer Class")
	@Type(base = IndexerClass.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "indexer-class", type = IndexerClass.class))
	public ListProperty PROP_INDEXER_CLASSES = new ListProperty(TYPE, "IndexerClasses");

	public ElementList<IndexerClass> getIndexerClasses();

	// social-activity-interpreter-class*

	@Type(base = SocialActivityInterpreterClass.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "social-activity-interpreter-class", type = SocialActivityInterpreterClass.class))
	public ListProperty PROP_SOCIAL_ACTIVITY_INTERPRETER_CLASSES = new ListProperty(TYPE, "SocialActivityInterpreterClasses");

	public ElementList<SocialActivityInterpreterClass> getSocialActivityInterpreterClasses();

	// header-portlet-javascript*

	@Label(standard = "Header Portlet Javascript")
	@Type(base = PortletStyleElement.class)
	@FileExtensions(expr = ".js")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "header-portlet-javascript", type = PortletStyleElement.class))
	public ListProperty PROP_HEADER_PORTLET_JAVASCRIPTS = new ListProperty(TYPE, "HeaderPortletJavascripts");

	public ElementList<PortletStyleElement> getHeaderPortletJavascripts();

	// *** schedule entry* ***

	@Type(base = SchedulerEntry.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "scheduler-entry", type = SchedulerEntry.class))
	public ListProperty PROP_SCHEDULER_ENTRIES = new ListProperty(TYPE, "SchedulerEntries");

	public ElementList<SchedulerEntry> getSchedulerEntries();

	// control-panel-entry-class?

	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = "com.liferay.portlet.ControlPanelEntry")
	@Label(standard = "Control Panel Entry Class")
	@MustExist
	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@XmlBinding(path = "control-panel-entry-class")
	public ValueProperty PROP_CONTROL_PANEL_ENTRY_CLASS = new ValueProperty(TYPE, "ControlPanelEntryClass");

	public ReferenceValue<JavaTypeName, JavaType> getControlPanelEntryClass();

	public void setControlPanelEntryClass(JavaTypeName value);

	public void setControlPanleEntryClass(String value);

	// asset-renderer-factory*

	@Label(standard = "Asset Renderer Factories")
	@Type(base = AssetRendererFactory.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "asset-renderer-factory", type = AssetRendererFactory.class))
	public ListProperty PROP_ASSET_RENDERER_FACTORIES = new ListProperty(TYPE, "AssetRendererFactories");

	public ElementList<AssetRendererFactory> getAssetRendererFactories();

	// trash-handler*

	@Label(standard = "Trash Handlers")
	@Type(base = TrashHandler.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "trash-handler", type = TrashHandler.class))
	public ListProperty PROP_TRASH_HANDLERS = new ListProperty(TYPE, "TrashHandlers");

	public ElementList<TrashHandler> getTrashHandlers();

	// workflow-handler

	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.workflow.WorkflowHandler")
	@Label(standard = "Workflow Handler")
	@MustExist
	@Reference(target = JavaType.class)
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "workflow-handler")
	public	ValueProperty PROP_WORKFLOW_HANDLER = new ValueProperty(TYPE, "WorkflowHandler");

	public ReferenceValue<JavaTypeName, JavaType> getWorkflowHandler();

	public void setWorkflowHandler(JavaTypeName value);

	public void setWorkflowHandler(String value);

}