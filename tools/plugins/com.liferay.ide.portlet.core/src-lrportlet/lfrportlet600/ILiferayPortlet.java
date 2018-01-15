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

package com.liferay.ide.portlet.core.model.lfrportlet600;

import com.liferay.ide.portlet.core.model.lfrportlet.common.ControlPanelEntryCategory;
import com.liferay.ide.portlet.core.model.lfrportlet.common.FacebookIntegration;
import com.liferay.ide.portlet.core.model.lfrportlet.common.IAssetRendererFactory;
import com.liferay.ide.portlet.core.model.lfrportlet.common.ICustomAttributeDisplay;
import com.liferay.ide.portlet.core.model.lfrportlet.common.ISchedulerEntry;
import com.liferay.ide.portlet.core.model.lfrportlet.common.IWebResource;
import com.liferay.ide.portlet.core.model.lfrportlet.common.IWorkflowHandler;
import com.liferay.ide.portlet.core.model.lfrportlet.common.UserPrincipalStrategy;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.FileExtensions;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.workspace.WorkspaceRelativePath;

/**
 * @author kamesh
 */
@GenerateImpl
public interface ILiferayPortlet extends Element {

	public ElementType TYPE = new ElementType(ILiferayPortlet.class);

	// *** Name ***

	@Label(standard = "Name")
	@NoDuplicates
	@XmlBinding(path = "portlet-name")

	// TODO: Use possible value service to pick it up from portlet.xml

	public ValueProperty PROP_PORTLET_NAME = new ValueProperty(TYPE, "Name");

	public Value<String> getPortletName();

	public void setPortletName(String value);

	// *** Icon ***

	@Type(base = Path.class)
	@Label(standard = "Icon")
	@ValidFileSystemResourceType(FileSystemResourceType.FILE)
	@FileExtensions(expr = "png,jpeg,jpg,gif,bmp")
	@MustExist
	@Length(min = 0, max = 1)
	@WorkspaceRelativePath
	@XmlBinding(path = "icon")
	public ValueProperty PROP_ICON = new ValueProperty(TYPE, "Icon");

	public Value<Path> getIcon();

	public void setIcon(String value);

	public void setIcon(Path value);

	// *** VirtualPath ***

	@Label(standard = "Virtual Path")
	@XmlBinding(path = "virtual-path")
	public ValueProperty PROP_VIRTUAL_PATH = new ValueProperty(TYPE, "VirtualPath");

	public Value<String> getVirtualPath();

	public void setVirtualPath(String value);

	// *** StrutsPath ***

	@Label(standard = "Struts path")
	@XmlBinding(path = "struts-path")
	public ValueProperty PROP_STRUTS_PATH = new ValueProperty(TYPE, "StrutsPath");

	public Value<String> getStrutsPath();

	public void setStrutsPath(String value);

	// *** ParentStrutsPath ***

	@Label(standard = "Parent Struts Path")
	@XmlBinding(path = "parent-struts-path")
	public ValueProperty PROP_PARENT_STRUTS_PATH = new ValueProperty(TYPE, "ParentStrutsPath");

	public Value<String> getParentStrutsPath();

	public void setParentStrutsPath(String value);

	// TODO:configuration path -- this is deprecated not sure to show it nor not

	// *** Configuration Action ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portal.kernel.portlet.ConfigurationAction"})
	@MustExist
	@Label(standard = "Configuration Action")
	@Required
	@NoDuplicates
	@XmlBinding(path = "configuration-action-class")
	public ValueProperty PROP_CONFIGURATION_ACTION = new ValueProperty(TYPE, "ConfigurationAction");

	public ReferenceValue<JavaTypeName, JavaType> getConfigurationAction();

	public void setConfigurationAction(String configurationAction);

	public void setConfigurationAction(JavaTypeName configurationAction);

	// *** Indexer Class ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portal.kernel.search.Indexer"})
	@MustExist
	@Label(standard = "Indexer")
	@Required
	@NoDuplicates
	@XmlBinding(path = "indexer-class")
	public ValueProperty PROP_INDEXER = new ValueProperty(TYPE, "Indexer");

	public ReferenceValue<JavaTypeName, JavaType> getIndexer();

	public void setIndexer(String indexer);

	public void setIndexer(JavaTypeName indexer);

	// *** Open Search Class ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portal.kernel.search.OpenSearch"})
	@MustExist
	@Label(standard = "Open Search")
	@Required
	@NoDuplicates
	@XmlBinding(path = "open-search-class")
	public ValueProperty PROP_OPEN_SEARCH = new ValueProperty(TYPE, "OpenSearch");

	public ReferenceValue<JavaTypeName, JavaType> getOpenSearch();

	public void setOpenSearch(String openSearch);

	public void setOpenSearch(JavaTypeName openSearch);

	// *** SchedulerEntries ***

	@Type(base = ISchedulerEntry.class)
	@Label(standard = "Scheduler Entries")
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "scheduler-entry", type = ISchedulerEntry.class)})
	public ListProperty PROP_SCHEDULER_ENTRIES = new ListProperty(TYPE, "SchedulerEntries");

	public ElementList<ISchedulerEntry> getSchedulerEntries();

	// *** Portlet URL Class ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portlet.PortletURLImplWrapper"})
	@MustExist
	@Label(standard = "Portlet URL")
	@Required
	@NoDuplicates
	@XmlBinding(path = "portlet-url-class")
	public ValueProperty PROP_PORTLET_URL = new ValueProperty(TYPE, "PortletURL");

	public ReferenceValue<JavaTypeName, JavaType> getPortletURL();

	public void setPortletURL(String portletURL);

	public void setPortletURL(JavaTypeName portletURL);

	// *** Friendly URL Mapper Class ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portal.kernel.portlet.FriendlyURLMapper"})
	@MustExist
	@Label(standard = "Friendly URL Mapper")
	@Required
	@NoDuplicates
	@XmlBinding(path = "friendly-url-mapper-class")
	public ValueProperty PROP_FRIENDLY_URL_MAPPER = new ValueProperty(TYPE, "FriendlyURLMapper");

	public ReferenceValue<JavaTypeName, JavaType> getFriendlyURLMapper();

	public void setFriendlyURLMapper(String friendlyURLMapper);

	public void setFriendlyURLMapper(JavaTypeName friendlyURLMapper);

	// *** FriendlyUrlMapping ***

	@Label(standard = "Friendly URL Mapping")
	@XmlBinding(path = "friendly-url-mapping")
	public ValueProperty PROP_FRIENDLY_URL_MAPPING = new ValueProperty(TYPE, "FriendlyUrlMapping");

	public Value<String> getFriendlyUrlMapping();

	public void setFriendlyUrlMapping(String value);

	// *** FriendlyUrlRoutes ***

	@Label(standard = "Friendly Url Routes")
	@XmlBinding(path = "friendly-url-routes")
	public ValueProperty PROP_FRIENDLY_URL_ROUTES = new ValueProperty(TYPE, "FriendlyUrlRoutes");

	public Value<String> getFriendlyUrlRoutes();

	public void setFriendlyUrlRoutes(String value);

	// *** URL Encoder Class ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portal.kernel.servlet.URLEncoder"})
	@MustExist
	@Label(standard = "URL Encoder")
	@Required
	@NoDuplicates
	@XmlBinding(path = "url-encoder-class")
	public ValueProperty PROP_URL_ENCODER = new ValueProperty(TYPE, "URLEncoder");

	public ReferenceValue<JavaTypeName, JavaType> getURLEncoder();

	public void setURLEncoder(String urlEncoderClass);

	public void setURLEncoder(JavaTypeName urlEncoderClass);

	// *** Portelet Data Handler Class ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portal.kernel.lar.PortletDataHandler"})
	@MustExist
	@Label(standard = "Portelet Data Handler")
	@Required
	@NoDuplicates
	@XmlBinding(path = "portlet-data-handler-class")
	public ValueProperty PROP_PORTLET_DATA_HANDLER = new ValueProperty(TYPE, "PortletDataHandler");

	public ReferenceValue<JavaTypeName, JavaType> getPortletDataHandler();

	public void setPortletDataHandler(String portletDataHandler);

	public void setPortletDataHandler(JavaTypeName portletDataHandler);

	// *** Portlet Layout Listener ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portal.kernel.portlet.PortletLayoutListener"})
	@MustExist
	@Label(standard = "Portlet Layout Listener")
	@Required
	@NoDuplicates
	@XmlBinding(path = "portlet-layout-listener-class")
	public ValueProperty PROP_PORTLET_LAYOUT_LISTENER = new ValueProperty(TYPE, "PortletLayoutListener");

	public ReferenceValue<JavaTypeName, JavaType> getPortletLayoutListener();

	public void setPortletLayoutListener(String portletLayoutListener);

	public void setPortletLayoutListener(JavaTypeName portletLayoutListener);

	// *** Poller Processor ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portal.kernel.poller.PollerProcessor"})
	@MustExist
	@Label(standard = "Poller Processor")
	@Required
	@NoDuplicates
	@XmlBinding(path = "poller-processor-class")
	public ValueProperty PROP_POLLER_PROCESSOR = new ValueProperty(TYPE, "PollerProcessor");

	public ReferenceValue<JavaTypeName, JavaType> getPollerProcessor();

	public void setPollerProcessor(String pollerProcessor);

	public void setPollerProcessor(JavaTypeName pollerProcessor);

	// *** Pop Message Listener ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portal.kernel.pop.MessageListener"})
	@MustExist
	@Label(standard = "Popup Message Listener")
	@Required
	@NoDuplicates
	@XmlBinding(path = "pop-message-listener-class")
	public ValueProperty PROP_POP_MESSAGE_LISTENER = new ValueProperty(TYPE, "PopMessageListener");

	public ReferenceValue<JavaTypeName, JavaType> getPopMessageListener();

	public void setPopMessageListener(String popMessageListener);

	public void setPopMessageListener(JavaTypeName popMessageListener);

	// *** SocialActivityInterpreter ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {
		"com.liferay.portlet.social.model.SocialActivityInterpreter"
	})
	@MustExist
	@Label(standard = "Social Activity Interpreter")
	@Required
	@NoDuplicates
	@XmlBinding(path = "social-activity-interpreter-class")
	public ValueProperty PROP_SOCIAL_ACTIVITY_INTERPRETER = new ValueProperty(TYPE, "SocialActivityInterpreter");

	public ReferenceValue<JavaTypeName, JavaType> getSocialActivityInterpreter();

	public void setSocialActivityInterpreter(String socialActivityInterpreter);

	public void setSocialActivityInterpreter(JavaTypeName socialActivityInterpreter);

	// *** SocialRequestInterpreter ***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portlet.social.model.SocialRequestInterpreter"})
	@MustExist
	@Label(standard = "Social Request Interpreter")
	@Required
	@NoDuplicates
	@XmlBinding(path = "social-request-interpreter-class")
	public ValueProperty PROP_SOCIAL_REQUEST_INTERPRETER = new ValueProperty(TYPE, "SocialRequestInterpreter");

	public ReferenceValue<JavaTypeName, JavaType> getSocialRequestInterpreter();

	public void setSocialRequestInterpreter(String socialRequestInterpreter);

	public void setSocialRequestInterpreter(JavaTypeName socialRequestInterpreter);

	// *** WebDAVStorageToken ***

	@Label(standard = "WebDAV Token")
	@XmlBinding(path = "webdav-storage-token")
	public ValueProperty PROP_WEBDAV_STORAGE_TOKEN = new ValueProperty(TYPE, "WebDAVStorageToken");

	public Value<String> getWebDAVStorageToken();

	public void setWebDAVStorageToken(String value);

	// *** WebDAVStorage***

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portal.kernel.webdav.WebDAVStorage"})
	@MustExist
	@Label(standard = "WebDAV Storage")
	@Required
	@NoDuplicates
	@XmlBinding(path = "webdav-storage-class")
	public ValueProperty PROP_WEBDAV_STORAGE = new ValueProperty(TYPE, "WebDAVStorage");

	public ReferenceValue<JavaTypeName, JavaType> getWebDAVStorage();

	public void setWebDAVStorage(String webDAVStorage);

	public void setWebDAVStorage(JavaTypeName webDAVStorage);

	// *** XmlRpcMethod ***

	@Type(base = JavaTypeName.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portal.kernel.xmlrpc.Method"})
	@Reference(target = JavaType.class)
	@Label(standard = "Xml Rpc Method")
	@MustExist
	@Required
	@NoDuplicates
	@XmlBinding(path = "xml-rpc-method-class")
	public ValueProperty PROP_XML_RPC_METHOD = new ValueProperty(TYPE, "XmlRpcMethod");

	public ReferenceValue<JavaTypeName, JavaType> getXmlRpcMethod();

	public void setXmlRpcMethod(String value);

	public void setXmlRpcMethod(JavaTypeName value);

	// *** ControlPanelEntryCategory ***

	@Type(base = ControlPanelEntryCategory.class)
	@Label(standard = "Control Panel Entry Category")
	@XmlBinding(path = "control-panel-entry-category")
	public ValueProperty PROP_CONTROL_PANEL_ENTRY_CATEGORY = new ValueProperty(TYPE, "ControlPanelEntryCategory");

	public Value<ControlPanelEntryCategory> getControlPanelEntryCategory();

	public void setControlPanelEntryCategory(String value);

	public void setControlPanelEntryCategory(ControlPanelEntryCategory value);

	// *** ControlPanelEntryWeight ***

	@Type(base = Double.class)
	@Label(standard = "Control Panel Entry Weight")
	@XmlBinding(path = "control-panel-entry-weight")
	public ValueProperty PROP_CONTROL_PANEL_ENTRY_WEIGHT = new ValueProperty(TYPE, "ControlPanelEntryWeight");

	public Value<Double> getControlPanelEntryWeight();

	public void setControlPanelEntryWeight(String value);

	public void setControlPanelEntryWeight(Double value);

	// *** ControlPanelEntryClass ***

	@Type(base = JavaTypeName.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portlet.ControlPanelEntry"})
	@Reference(target = JavaType.class)
	@Label(standard = "Control Panel Entry Class")
	@MustExist
	@Required
	@NoDuplicates
	@XmlBinding(path = "control-panel-entry-class")
	public ValueProperty PROP_CONTROL_PANEL_ENTRY_CLASS = new ValueProperty(TYPE, "ControlPanelEntryClass");

	public ReferenceValue<JavaTypeName, JavaType> getControlPanelEntryClass();

	public void setControlPanelEntryClass(String value);

	public void setControlPanelEntryClass(JavaTypeName value);

	// *** AssetRendererFactories ***

	@Type(base = IAssetRendererFactory.class)
	@Label(standard = "Asset Renderer Factory")
	@XmlListBinding(mappings = {
		@XmlListBinding.Mapping(element = "asset-renderer-factory", type = IAssetRendererFactory.class)
	})
	public ListProperty PROP_ASSET_RENDERER_FACTORIES = new ListProperty(TYPE, "AssetRendererFactories");

	public ElementList<IAssetRendererFactory> getAssetRendererFactories();

	// *** CustomAttributeDisplays ***

	@Type(base = ICustomAttributeDisplay.class)
	@Label(standard = "Custom Attribute Display")
	@XmlListBinding(mappings = {
		@XmlListBinding.Mapping(element = "custom-attribute-display", type = ICustomAttributeDisplay.class)
	})
	public ListProperty PROP_CUSTOM_ATTRIBUTE_DISPLAYS = new ListProperty(TYPE, "CustomAttributeDisplays");

	public ElementList<ICustomAttributeDisplay> getCustomAttributeDisplays();

	// *** WorkflowHandlers ***

	@Type(base = IWorkflowHandler.class)
	@Label(standard = "Workflow Handlers")
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "workflow-handler", type = IWorkflowHandler.class)})
	public ListProperty PROP_WORKFLOW_HANDLERS = new ListProperty(TYPE, "WorkflowHandlers");

	public ElementList<IWorkflowHandler> getWorkflowHandlers();

	// *** PreferencesCompanyWide ***

	@Type(base = Boolean.class)
	@Label(standard = "Preferences Company Wide")
	@DefaultValue(text = "false")
	@XmlBinding(path = "preferences-company-wide")
	public ValueProperty PROP_PREFERENCES_COMPANY_WIDE = new ValueProperty(TYPE, "PreferencesCompanyWide");

	public Value<Boolean> getPreferencesCompanyWide();

	public void setPreferencesCompanyWide(String value);

	public void setPreferencesCompanyWide(Boolean value);

	// *** PreferencesUniquePerLayout ***

	@Type(base = Boolean.class)
	@Label(standard = "Preferences Unique per Layput")
	@DefaultValue(text = "true")
	@XmlBinding(path = "preferences-unique-per-layout")
	public ValueProperty PROP_PREFERENCES_UNIQUE_PER_LAYOUT = new ValueProperty(TYPE, "PreferencesUniquePerLayout");

	public Value<Boolean> getPreferencesUniquePerLayout();

	public void setPreferencesUniquePerLayout(String value);

	public void setPreferencesUniquePerLayout(Boolean value);

	// *** PreferencesOwnedByGroup ***

	@Type(base = Boolean.class)
	@Label(standard = "Preferences Owned by Group")
	@DefaultValue(text = "true")
	@XmlBinding(path = "preferences-owned-by-group")
	public ValueProperty PROP_PREFERENCES_OWNED_BY_GROUP = new ValueProperty(TYPE, "PreferencesOwnedByGroup");

	public Value<Boolean> getPreferencesOwnedByGroup();

	public void setPreferencesOwnedByGroup(String value);

	public void setPreferencesOwnedByGroup(Boolean value);

	// *** UseDefaultTemplate ***

	@Type(base = Boolean.class)
	@Label(standard = "Use Default Template")
	@DefaultValue(text = "true")
	@XmlBinding(path = "use-default-template")
	public ValueProperty PROP_USE_DEFAULT_TEMPLATE = new ValueProperty(TYPE, "UseDefaultTemplate");

	public Value<Boolean> getUseDefaultTemplate();

	public void setUseDefaultTemplate(String value);

	public void setUseDefaultTemplate(Boolean value);

	// *** ShowPortletAccessDenied ***

	@Type(base = Boolean.class)
	@Label(standard = "Show Portlet Access Denied")
	@DefaultValue(text = "true")
	@XmlBinding(path = "show-portlet-access-denied")
	public ValueProperty PROP_SHOW_PORTLET_ACCESS_DENIED = new ValueProperty(TYPE, "ShowPortletAccessDenied");

	public Value<Boolean> getShowPortletAccessDenied();

	public void setShowPortletAccessDenied(String value);

	public void setShowPortletAccessDenied(Boolean value);

	// *** ShowPortletInActive ***

	@Type(base = Boolean.class)
	@Label(standard = "Show Portlet InActive")
	@DefaultValue(text = "true")
	@XmlBinding(path = "show-portlet-inactive")
	public ValueProperty PROP_SHOW_PORTLET_INACTIVE = new ValueProperty(TYPE, "ShowPortletInActive");

	public Value<Boolean> getShowPortletInActive();

	public void setShowPortletInActive(String value);

	public void setShowPortletInActive(Boolean value);

	// *** ActionUrlRedirect ***

	@Type(base = Boolean.class)
	@Label(standard = "Show Url Redirect")
	@DefaultValue(text = "false")
	@XmlBinding(path = "action-url-redirect")
	public ValueProperty PROP_ACTION_URL_REDIRECT = new ValueProperty(TYPE, "ActionUrlRedirect");

	public Value<Boolean> getActionUrlRedirect();

	public void setActionUrlRedirect(String value);

	public void setActionUrlRedirect(Boolean value);

	// *** RestoreCurrentView ***

	@Type(base = Boolean.class)
	@Label(standard = "Restore Current View")
	@DefaultValue(text = "true")
	@XmlBinding(path = "restore-current-view")
	public ValueProperty PROP_RESTORE_CURRENT_VIEW = new ValueProperty(TYPE, "RestoreCurrentView");

	public Value<Boolean> getRestoreCurrentView();

	public void setRestoreCurrentView(String value);

	public void setRestoreCurrentView(Boolean value);

	// *** MaximizeEdit ***

	@Type(base = Boolean.class)
	@Label(standard = "Maximize Edit")
	@DefaultValue(text = "false")
	@XmlBinding(path = "maximize-edit")
	public ValueProperty PROP_MAXIMIZE_EDIT = new ValueProperty(TYPE, "MaximizeEdit");

	public Value<Boolean> getMaximizeEdit();

	public void setMaximizeEdit(String value);

	public void setMaximizeEdit(Boolean value);

	// *** MaximizeHelp ***

	@Type(base = Boolean.class)
	@Label(standard = "Maximize Edit")
	@DefaultValue(text = "false")
	@XmlBinding(path = "maximize-help")
	public ValueProperty PROP_MAXIMIZE_HELP = new ValueProperty(TYPE, "MaximizeHelp");

	public Value<Boolean> getMaximizeHelp();

	public void setMaximizeHelp(String value);

	public void setMaximizeHelp(Boolean value);

	// *** PopupPrint ***

	@Type(base = Boolean.class)
	@Label(standard = "Popup Print")
	@DefaultValue(text = "true")
	@XmlBinding(path = "pop-up-print")
	public ValueProperty PROP_POP_UP_PRINT = new ValueProperty(TYPE, "PopupPrint");

	public Value<Boolean> getPopupPrint();

	public void setPopupPrint(String value);

	public void setPopupPrint(Boolean value);

	// *** LayoutCacheable ***

	@Type(base = Boolean.class)
	@Label(standard = "Layout Cacheable")
	@DefaultValue(text = "true")
	@XmlBinding(path = "layout-cacheable")
	public ValueProperty PROP_LAYOUT_CACHEABLE = new ValueProperty(TYPE, "LayoutCacheable");

	public Value<Boolean> getLayoutCacheable();

	public void setLayoutCacheable(String value);

	public void setLayoutCacheable(Boolean value);

	// *** Instanceable ***

	@Type(base = Boolean.class)
	@Label(standard = "Instanceable")
	@DefaultValue(text = "false")
	@XmlBinding(path = "instanceable")
	public ValueProperty PROP_INSTANCEABLE = new ValueProperty(TYPE, "Instanceable");

	public Value<Boolean> getInstanceable();

	public void setInstanceable(String value);

	public void setInstanceable(Boolean value);

	// *** Remoteable ***

	@Type(base = Boolean.class)
	@Label(standard = "Remoteable")
	@DefaultValue(text = "false")
	@XmlBinding(path = "remoteable")
	public ValueProperty PROP_REMOTEABLE = new ValueProperty(TYPE, "Remoteable");

	public Value<Boolean> getRemoteable();

	public void setRemoteable(String value);

	public void setRemoteable(Boolean value);

	// *** Scopeable ***

	@Type(base = Boolean.class)
	@Label(standard = "Scopeable")
	@DefaultValue(text = "false")
	@XmlBinding(path = "scopeable")
	public ValueProperty PROP_SCOPEABLE = new ValueProperty(TYPE, "Scopeable");

	public Value<Boolean> getScopeable();

	public void setScopeable(String value);

	public void setScopeable(Boolean value);

	// *** UserPrincipalStrategy ***

	@Type(base = UserPrincipalStrategy.class)
	@Label(standard = "label")
	@XmlBinding(path = "user-principal-strategy")
	public ValueProperty PROP_USER_PRINCIPAL_STRATEGY = new ValueProperty(TYPE, "UserPrincipalStrategy");

	public Value<UserPrincipalStrategy> getUserPrincipalStrategy();

	public void setUserPrincipalStrategy(String value);

	public void setUserPrincipalStrategy(UserPrincipalStrategy value);

	// *** PrivateRequestAttributes ***

	@Type(base = Boolean.class)
	@Label(standard = "Private Request Attributes")
	@DefaultValue(text = "true")
	@XmlBinding(path = " private-request-attributes")
	public ValueProperty PROP_PRIVATE_REQUEST_ATTRIBUTES = new ValueProperty(TYPE, "PrivateRequestAttributes");

	public Value<Boolean> getPrivateRequestAttributes();

	public void setPrivateRequestAttributes(String value);

	public void setPrivateRequestAttributes(Boolean value);

	// *** PrivateSessionAttributes ***

	@Type(base = Boolean.class)
	@Label(standard = "Private Session Attributes")
	@DefaultValue(text = "true")
	@XmlBinding(path = " private-session-attributes")
	public ValueProperty PROP_PRIVATE_SESSION_ATTRIBUTES = new ValueProperty(TYPE, "PrivateSessionAttributes");

	public Value<Boolean> getPrivateSessionAttributes();

	public void setPrivateSessionAttributes(String value);

	public void setPrivateSessionAttributes(Boolean value);

	// *** AutoPropogatedParameters ***

	@Label(standard = "label")
	@XmlBinding(path = "autopropagated-parameters")
	public ValueProperty PROP_AUTO_PROPOGATED_PARAMETERS = new ValueProperty(TYPE, "AutoPropogatedParameters");

	public Value<String> getAutoPropogatedParameters();

	public void setAutoPropogatedParameters(String value);

	// *** ActionTimeout ***

	@Type(base = Integer.class)
	@Label(standard = "Action Timeout")
	@DefaultValue(text = "0")
	@XmlBinding(path = "action-time-out")
	public ValueProperty PROP_ACTION_TIMEOUT = new ValueProperty(TYPE, "ActionTimeout");

	public Value<Integer> getActionTimeout();

	public void setActionTimeout(String value);

	public void setActionTimeout(Integer value);

	// *** RenderTimeout ***

	@Type(base = Integer.class)
	@Label(standard = "Render Timeout")
	@DefaultValue(text = "0")
	@XmlBinding(path = "render-timeout")
	public ValueProperty PROP_RENDER_TIMEOUT = new ValueProperty(TYPE, "RenderTimeout");

	public Value<Integer> getRenderTimeout();

	public void setRenderTimeout(String value);

	public void setRenderTimeout(Integer value);

	// *** RenderWeight ***

	@Type(base = Integer.class)
	@Label(standard = "Render Weight")
	@DefaultValue(text = "0")
	@XmlBinding(path = "render-weight")
	public ValueProperty PROP_RENDER_WEIGHT = new ValueProperty(TYPE, "RenderWeight");

	public Value<Integer> getRenderWeight();

	public void setRenderWeight(String value);

	public void setRenderWeight(Integer value);

	// *** Ajaxable ***

	@Type(base = Boolean.class)
	@Label(standard = "Ajaxable")
	@XmlBinding(path = "ajaxable")
	public ValueProperty PROP_AJAXABLE = new ValueProperty(TYPE, "Ajaxable");

	public Value<Boolean> getAjaxable();

	public void setAjaxable(String value);

	public void setAjaxable(Boolean value);

	// *** HeaderPortalCss ***

	@Type(base = IWebResource.class)
	@Label(standard = "Header Portal Css")
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "header-portal-css", type = IWebResource.class)})
	public ListProperty PROP_HEADER_PORTAL_CSS = new ListProperty(TYPE, "HeaderPortalCss");

	public ElementList<IWebResource> getHeaderPortalCss();

	// *** HeaderPortletCss ***

	@Type(base = IWebResource.class)
	@Label(standard = "Header Portlet Css")
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "header-portlet-css", type = IWebResource.class)})
	public ListProperty PROP_HEADER_PORTLET_CSS = new ListProperty(TYPE, "HeaderPortletCss");

	public ElementList<IWebResource> getHeaderPortletCss();

	// *** HeaderPortalJavascript ***

	@Type(base = IWebResource.class)
	@Label(standard = "Header Portal Javascript")
	@XmlListBinding(mappings = {
		@XmlListBinding.Mapping(element = "header-portal-javascript", type = IWebResource.class)
	})
	public ListProperty PROP_HEADER_PORTAL_JAVASCRIPT = new ListProperty(TYPE, "HeaderPortalJavascript");

	public ElementList<IWebResource> getHeaderPortalJavascript();

	// *** HeaderPortletJavascript ***

	@Type(base = IWebResource.class)
	@Label(standard = "Header Portlet Javascript")
	@XmlListBinding(mappings = {
		@XmlListBinding.Mapping(element = "header-portlet-javascript", type = IWebResource.class)
	})
	public ListProperty PROP_HEADER_PORTLET_JAVASCRIPT = new ListProperty(TYPE, "HeaderPortletJavascript");

	public ElementList<IWebResource> getHeaderPortletJavascript();

	// *** FooterPortalCss ***

	@Type(base = IWebResource.class)
	@Label(standard = "Footer Portal Css")
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "footer-portal-css", type = IWebResource.class)})
	public ListProperty PROP_FOOTER_PORTAL_CSS = new ListProperty(TYPE, "FooterPortalCss");

	public ElementList<IWebResource> getFooterPortalCss();

	// *** FooterPortletCss ***

	@Type(base = IWebResource.class)
	@Label(standard = "Footer Portlet Css")
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "footer-portlet-css", type = IWebResource.class)})
	public ListProperty PROP_FOOTER_PORTLET_CSS = new ListProperty(TYPE, "FooterPortletCss");

	public ElementList<IWebResource> getFooterPortletCss();

	// *** FooterPortalJavascript ***

	@Type(base = IWebResource.class)
	@Label(standard = "Footer Portal Javascript")
	@XmlListBinding(mappings = {
		@XmlListBinding.Mapping(element = "footer-portal-javascript", type = IWebResource.class)
	})
	public ListProperty PROP_FOOTER_PORTAL_JAVASCRIPT = new ListProperty(TYPE, "FooterPortalJavascript");

	public ElementList<IWebResource> getFooterPortalJavascript();

	// *** FooterPortletJavascript ***

	@Type(base = IWebResource.class)
	@Label(standard = "Footer Portlet Javascript")
	@XmlListBinding(mappings = {
		@XmlListBinding.Mapping(element = "footer-portlet-javascript", type = IWebResource.class)
	})
	public ListProperty PROP_FOOTER_PORTLET_JAVASCRIPT = new ListProperty(TYPE, "FooterPortletJavascript");

	public ElementList<IWebResource> getFooterPortletJavascript();

	// *** CssWrapperClass ***

	@Label(standard = "Css Wrapper Class")
	@XmlBinding(path = "css-wrapper-class")
	public ValueProperty PROP_CSS_WRAPPER_CLASS = new ValueProperty(TYPE, "CssWrapperClass");

	public Value<String> getCssWrapperClass();

	public void setCssWrapperClass(String value);

	// *** FacebookIntegration ***

	@Type(base = FacebookIntegration.class)
	@Label(standard = "Facebook Integration")
	@XmlBinding(path = "facebook-integration")
	@DefaultValue(text = "iframe")
	public ValueProperty PROP_FACEBOOK_INTEGRATION = new ValueProperty(TYPE, "FacebookIntegration");

	public Value<FacebookIntegration> getFacebookIntegration();

	public void setFacebookIntegration(String value);

	public void setFacebookIntegration(FacebookIntegration value);

	// *** AddDefaultResource ***

	@Type(base = Boolean.class)
	@Label(standard = "Add Default Resource")
	@XmlBinding(path = "add-default-resource")
	@DefaultValue(text = "false")
	public ValueProperty PROP_ADD_DEFAULT_RESOURCE = new ValueProperty(TYPE, "AddDefaultResource");

	public Value<Boolean> getAddDefaultResource();

	public void setAddDefaultResource(String value);

	public void setAddDefaultResource(Boolean value);

	// *** System ***

	@Type(base = Boolean.class)
	@Label(standard = "System")
	@XmlBinding(path = "system")
	@DefaultValue(text = "false")
	public ValueProperty PROP_SYSTEM = new ValueProperty(TYPE, "System");

	public Value<Boolean> getSystem();

	public void setSystem(String value);

	public void setSystem(Boolean value);

	// *** Active ***

	@Type(base = Boolean.class)
	@Label(standard = "Active")
	@XmlBinding(path = "active")
	@DefaultValue(text = "true")
	public ValueProperty PROP_ACTIVE = new ValueProperty(TYPE, "Active");

	public Value<Boolean> getActive();

	public void setActive(String value);

	public void setActive(Boolean value);

	// *** Include ***

	@Type(base = Boolean.class)
	@Label(standard = "Include")
	@XmlBinding(path = "include")
	@DefaultValue(text = "false")
	public ValueProperty PROP_INCLUDE = new ValueProperty(TYPE, "Include");

	public Value<Boolean> getInclude();

	public void setInclude(String value);

	public void setInclude(Boolean value);

}