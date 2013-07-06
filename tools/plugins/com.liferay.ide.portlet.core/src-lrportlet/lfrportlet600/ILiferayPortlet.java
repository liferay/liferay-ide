/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/


package com.liferay.ide.portlet.core.model.lfrportlet600;


import com.liferay.ide.portlet.core.model.lfrportlet.common.ControlPanelEntryCategory;
import com.liferay.ide.portlet.core.model.lfrportlet.common.FacebookIntegration;
import com.liferay.ide.portlet.core.model.lfrportlet.common.IAssetRendererFactory;
import com.liferay.ide.portlet.core.model.lfrportlet.common.ICustomAttributeDisplay;
import com.liferay.ide.portlet.core.model.lfrportlet.common.ISchedulerEntry;
import com.liferay.ide.portlet.core.model.lfrportlet.common.IWebResource;
import com.liferay.ide.portlet.core.model.lfrportlet.common.IWorkflowHandler;
import com.liferay.ide.portlet.core.model.lfrportlet.common.UserPrincipalStrategy;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
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

	ElementType TYPE = new ElementType( ILiferayPortlet.class );

	// *** Name ***

	@Label( standard = "Name" )
	@NoDuplicates
	@XmlBinding( path = "portlet-name" )
	// TODO: Use possible value service to pick it up from portlet.xml
	ValueProperty PROP_PORTLET_NAME = new ValueProperty( TYPE, "Name" );

	Value<String> getPortletName();

	void setPortletName( String value );

	// *** Icon ***

	@Type( base = Path.class )
	@Label( standard = "Icon" )
	@ValidFileSystemResourceType( FileSystemResourceType.FILE )
	@FileExtensions( expr = "png,jpeg,jpg,gif,bmp" )
	@MustExist
	@CountConstraint( min = 0, max = 1 )
	@WorkspaceRelativePath
	@XmlBinding( path = "icon" )
	ValueProperty PROP_ICON = new ValueProperty( TYPE, "Icon" );

	Value<Path> getIcon();

	void setIcon( String value );

	void setIcon( Path value );

	// *** VirtualPath ***

	@Label( standard = "Virtual Path" )
	@XmlBinding( path = "virtual-path" )
	ValueProperty PROP_VIRTUAL_PATH = new ValueProperty( TYPE, "VirtualPath" );

	Value<String> getVirtualPath();

	void setVirtualPath( String value );

	// *** StrutsPath ***

	@Label( standard = "Struts path" )
	@XmlBinding( path = "struts-path" )
	ValueProperty PROP_STRUTS_PATH = new ValueProperty( TYPE, "StrutsPath" );

	Value<String> getStrutsPath();

	void setStrutsPath( String value );

	// *** ParentStrutsPath ***

	@Label( standard = "Parent Struts Path" )
	@XmlBinding( path = "parent-struts-path" )
	ValueProperty PROP_PARENT_STRUTS_PATH = new ValueProperty( TYPE, "ParentStrutsPath" );

	Value<String> getParentStrutsPath();

	void setParentStrutsPath( String value );

	// TODO:configuration path -- this is deprecated not sure to show it nor not

	// *** Configuration Action ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portal.kernel.portlet.ConfigurationAction" } )
	@MustExist
	@Label( standard = "Configuration Action" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "configuration-action-class" )
	ValueProperty PROP_CONFIGURATION_ACTION = new ValueProperty( TYPE, "ConfigurationAction" );

	ReferenceValue<JavaTypeName, JavaType> getConfigurationAction();

	void setConfigurationAction( String configurationAction );

	void setConfigurationAction( JavaTypeName configurationAction );

	// *** Indexer Class ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portal.kernel.search.Indexer" } )
	@MustExist
	@Label( standard = "Indexer" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "indexer-class" )
	ValueProperty PROP_INDEXER = new ValueProperty( TYPE, "Indexer" );

	ReferenceValue<JavaTypeName, JavaType> getIndexer();

	void setIndexer( String indexer );

	void setIndexer( JavaTypeName indexer );

	// *** Open Search Class ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portal.kernel.search.OpenSearch" } )
	@MustExist
	@Label( standard = "Open Search" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "open-search-class" )
	ValueProperty PROP_OPEN_SEARCH = new ValueProperty( TYPE, "OpenSearch" );

	ReferenceValue<JavaTypeName, JavaType> getOpenSearch();

	void setOpenSearch( String openSearch );

	void setOpenSearch( JavaTypeName openSearch );

	// *** SchedulerEntries ***

	@Type( base = ISchedulerEntry.class )
	@Label( standard = "Scheduler Entries" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "scheduler-entry", type = ISchedulerEntry.class ) } )
	ListProperty PROP_SCHEDULER_ENTRIES = new ListProperty( TYPE, "SchedulerEntries" );

	ElementList<ISchedulerEntry> getSchedulerEntries();

	// *** Portlet URL Class ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portlet.PortletURLImplWrapper" } )
	@MustExist
	@Label( standard = "Portlet URL" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "portlet-url-class" )
	ValueProperty PROP_PORTLET_URL = new ValueProperty( TYPE, "PortletURL" );

	ReferenceValue<JavaTypeName, JavaType> getPortletURL();

	void setPortletURL( String portletURL );

	void setPortletURL( JavaTypeName portletURL );

	// *** Friendly URL Mapper Class ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portal.kernel.portlet.FriendlyURLMapper" } )
	@MustExist
	@Label( standard = "Friendly URL Mapper" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "friendly-url-mapper-class" )
	ValueProperty PROP_FRIENDLY_URL_MAPPER = new ValueProperty( TYPE, "FriendlyURLMapper" );

	ReferenceValue<JavaTypeName, JavaType> getFriendlyURLMapper();

	void setFriendlyURLMapper( String friendlyURLMapper );

	void setFriendlyURLMapper( JavaTypeName friendlyURLMapper );

	// *** FriendlyUrlMapping ***

	@Label( standard = "Friendly URL Mapping" )
	@XmlBinding( path = "friendly-url-mapping" )
	ValueProperty PROP_FRIENDLY_URL_MAPPING = new ValueProperty( TYPE, "FriendlyUrlMapping" );

	Value<String> getFriendlyUrlMapping();

	void setFriendlyUrlMapping( String value );

	// *** FriendlyUrlRoutes ***

	@Label( standard = "Friendly Url Routes" )
	@XmlBinding( path = "friendly-url-routes" )
	ValueProperty PROP_FRIENDLY_URL_ROUTES = new ValueProperty( TYPE, "FriendlyUrlRoutes" );

	Value<String> getFriendlyUrlRoutes();

	void setFriendlyUrlRoutes( String value );

	// *** URL Encoder Class ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portal.kernel.servlet.URLEncoder" } )
	@MustExist
	@Label( standard = "URL Encoder" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "url-encoder-class" )
	ValueProperty PROP_URL_ENCODER = new ValueProperty( TYPE, "URLEncoder" );

	ReferenceValue<JavaTypeName, JavaType> getURLEncoder();

	void setURLEncoder( String urlEncoderClass );

	void setURLEncoder( JavaTypeName urlEncoderClass );

	// *** Portelet Data Handler Class ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portal.kernel.lar.PortletDataHandler" } )
	@MustExist
	@Label( standard = "Portelet Data Handler" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "portlet-data-handler-class" )
	ValueProperty PROP_PORTLET_DATA_HANDLER = new ValueProperty( TYPE, "PortletDataHandler" );

	ReferenceValue<JavaTypeName, JavaType> getPortletDataHandler();

	void setPortletDataHandler( String portletDataHandler );

	void setPortletDataHandler( JavaTypeName portletDataHandler );

	// *** Portlet Layout Listener ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portal.kernel.portlet.PortletLayoutListener" } )
	@MustExist
	@Label( standard = "Portlet Layout Listener" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "portlet-layout-listener-class" )
	ValueProperty PROP_PORTLET_LAYOUT_LISTENER = new ValueProperty( TYPE, "PortletLayoutListener" );

	ReferenceValue<JavaTypeName, JavaType> getPortletLayoutListener();

	void setPortletLayoutListener( String portletLayoutListener );

	void setPortletLayoutListener( JavaTypeName portletLayoutListener );

	// *** Poller Processor ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portal.kernel.poller.PollerProcessor" } )
	@MustExist
	@Label( standard = "Poller Processor" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "poller-processor-class" )
	ValueProperty PROP_POLLER_PROCESSOR = new ValueProperty( TYPE, "PollerProcessor" );

	ReferenceValue<JavaTypeName, JavaType> getPollerProcessor();

	void setPollerProcessor( String pollerProcessor );

	void setPollerProcessor( JavaTypeName pollerProcessor );

	// *** Pop Message Listener ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portal.kernel.pop.MessageListener" } )
	@MustExist
	@Label( standard = "Popup Message Listener" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "pop-message-listener-class" )
	ValueProperty PROP_POP_MESSAGE_LISTENER = new ValueProperty( TYPE, "PopMessageListener" );

	ReferenceValue<JavaTypeName, JavaType> getPopMessageListener();

	void setPopMessageListener( String popMessageListener );

	void setPopMessageListener( JavaTypeName popMessageListener );

	// *** SocialActivityInterpreter ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portlet.social.model.SocialActivityInterpreter" } )
	@MustExist
	@Label( standard = "Social Activity Interpreter" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "social-activity-interpreter-class" )
	ValueProperty PROP_SOCIAL_ACTIVITY_INTERPRETER = new ValueProperty( TYPE, "SocialActivityInterpreter" );

	ReferenceValue<JavaTypeName, JavaType> getSocialActivityInterpreter();

	void setSocialActivityInterpreter( String socialActivityInterpreter );

	void setSocialActivityInterpreter( JavaTypeName socialActivityInterpreter );

	// *** SocialRequestInterpreter ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portlet.social.model.SocialRequestInterpreter" } )
	@MustExist
	@Label( standard = "Social Request Interpreter" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "social-request-interpreter-class" )
	ValueProperty PROP_SOCIAL_REQUEST_INTERPRETER = new ValueProperty( TYPE, "SocialRequestInterpreter" );

	ReferenceValue<JavaTypeName, JavaType> getSocialRequestInterpreter();

	void setSocialRequestInterpreter( String socialRequestInterpreter );

	void setSocialRequestInterpreter( JavaTypeName socialRequestInterpreter );

	// *** WebDAVStorageToken ***

	@Label( standard = "WebDAV Token" )
	@XmlBinding( path = "webdav-storage-token" )
	ValueProperty PROP_WEBDAV_STORAGE_TOKEN = new ValueProperty( TYPE, "WebDAVStorageToken" );

	Value<String> getWebDAVStorageToken();

	void setWebDAVStorageToken( String value );

	// *** WebDAVStorage***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portal.kernel.webdav.WebDAVStorage" } )
	@MustExist
	@Label( standard = "WebDAV Storage" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "webdav-storage-class" )
	ValueProperty PROP_WEBDAV_STORAGE = new ValueProperty( TYPE, "WebDAVStorage" );

	ReferenceValue<JavaTypeName, JavaType> getWebDAVStorage();

	void setWebDAVStorage( String webDAVStorage );

	void setWebDAVStorage( JavaTypeName webDAVStorage );

	// *** XmlRpcMethod ***
	@Type( base = JavaTypeName.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portal.kernel.xmlrpc.Method" } )
	@Reference( target = JavaType.class )
	@Label( standard = "Xml Rpc Method" )
	@MustExist
	@Required
	@NoDuplicates
	@XmlBinding( path = "xml-rpc-method-class" )
	ValueProperty PROP_XML_RPC_METHOD = new ValueProperty( TYPE, "XmlRpcMethod" );

	ReferenceValue<JavaTypeName, JavaType> getXmlRpcMethod();

	void setXmlRpcMethod( String value );

	void setXmlRpcMethod( JavaTypeName value );

	// *** ControlPanelEntryCategory ***

	@Type( base = ControlPanelEntryCategory.class )
	@Label( standard = "Control Panel Entry Category" )
	@XmlBinding( path = "control-panel-entry-category" )
	ValueProperty PROP_CONTROL_PANEL_ENTRY_CATEGORY = new ValueProperty( TYPE, "ControlPanelEntryCategory" );

	Value<ControlPanelEntryCategory> getControlPanelEntryCategory();

	void setControlPanelEntryCategory( String value );

	void setControlPanelEntryCategory( ControlPanelEntryCategory value );

	// *** ControlPanelEntryWeight ***

	@Type( base = Double.class )
	@Label( standard = "Control Panel Entry Weight" )
	@XmlBinding( path = "control-panel-entry-weight" )
	ValueProperty PROP_CONTROL_PANEL_ENTRY_WEIGHT = new ValueProperty( TYPE, "ControlPanelEntryWeight" );

	Value<Double> getControlPanelEntryWeight();

	void setControlPanelEntryWeight( String value );

	void setControlPanelEntryWeight( Double value );

	// *** ControlPanelEntryClass ***
	@Type( base = JavaTypeName.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = { "com.liferay.portlet.ControlPanelEntry" } )
	@Reference( target = JavaType.class )
	@Label( standard = "Control Panel Entry Class" )
	@MustExist
	@Required
	@NoDuplicates
	@XmlBinding( path = "control-panel-entry-class" )
	ValueProperty PROP_CONTROL_PANEL_ENTRY_CLASS = new ValueProperty( TYPE, "ControlPanelEntryClass" );

	ReferenceValue<JavaTypeName, JavaType> getControlPanelEntryClass();

	void setControlPanelEntryClass( String value );

	void setControlPanelEntryClass( JavaTypeName value );

	// *** AssetRendererFactories ***

	@Type( base = IAssetRendererFactory.class )
	@Label( standard = "Asset Renderer Factory" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "asset-renderer-factory", type = IAssetRendererFactory.class ) } )
	ListProperty PROP_ASSET_RENDERER_FACTORIES = new ListProperty( TYPE, "AssetRendererFactories" );

	ElementList<IAssetRendererFactory> getAssetRendererFactories();

	// *** CustomAttributeDisplays ***

	@Type( base = ICustomAttributeDisplay.class )
	@Label( standard = "Custom Attribute Display" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "custom-attribute-display", type = ICustomAttributeDisplay.class ) } )
	ListProperty PROP_CUSTOM_ATTRIBUTE_DISPLAYS = new ListProperty( TYPE, "CustomAttributeDisplays" );

	ElementList<ICustomAttributeDisplay> getCustomAttributeDisplays();

	// *** WorkflowHandlers ***

	@Type( base = IWorkflowHandler.class )
	@Label( standard = "Workflow Handlers" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "workflow-handler", type = IWorkflowHandler.class ) } )
	ListProperty PROP_WORKFLOW_HANDLERS = new ListProperty( TYPE, "WorkflowHandlers" );

	ElementList<IWorkflowHandler> getWorkflowHandlers();

	// *** PreferencesCompanyWide ***

	@Type( base = Boolean.class )
	@Label( standard = "Preferences Company Wide" )
	@DefaultValue( text = "false" )
	@XmlBinding( path = "preferences-company-wide" )
	ValueProperty PROP_PREFERENCES_COMPANY_WIDE = new ValueProperty( TYPE, "PreferencesCompanyWide" );

	Value<Boolean> getPreferencesCompanyWide();

	void setPreferencesCompanyWide( String value );

	void setPreferencesCompanyWide( Boolean value );

	// *** PreferencesUniquePerLayout ***

	@Type( base = Boolean.class )
	@Label( standard = "Preferences Unique per Layput" )
	@DefaultValue( text = "true" )
	@XmlBinding( path = "preferences-unique-per-layout" )
	ValueProperty PROP_PREFERENCES_UNIQUE_PER_LAYOUT = new ValueProperty( TYPE, "PreferencesUniquePerLayout" );

	Value<Boolean> getPreferencesUniquePerLayout();

	void setPreferencesUniquePerLayout( String value );

	void setPreferencesUniquePerLayout( Boolean value );

	// *** PreferencesOwnedByGroup ***

	@Type( base = Boolean.class )
	@Label( standard = "Preferences Owned by Group" )
	@DefaultValue( text = "true" )
	@XmlBinding( path = "preferences-owned-by-group" )
	ValueProperty PROP_PREFERENCES_OWNED_BY_GROUP = new ValueProperty( TYPE, "PreferencesOwnedByGroup" );

	Value<Boolean> getPreferencesOwnedByGroup();

	void setPreferencesOwnedByGroup( String value );

	void setPreferencesOwnedByGroup( Boolean value );

	// *** UseDefaultTemplate ***

	@Type( base = Boolean.class )
	@Label( standard = "Use Default Template" )
	@DefaultValue( text = "true" )
	@XmlBinding( path = "use-default-template" )
	ValueProperty PROP_USE_DEFAULT_TEMPLATE = new ValueProperty( TYPE, "UseDefaultTemplate" );

	Value<Boolean> getUseDefaultTemplate();

	void setUseDefaultTemplate( String value );

	void setUseDefaultTemplate( Boolean value );

	// *** ShowPortletAccessDenied ***

	@Type( base = Boolean.class )
	@Label( standard = "Show Portlet Access Denied" )
	@DefaultValue( text = "true" )
	@XmlBinding( path = "show-portlet-access-denied" )
	ValueProperty PROP_SHOW_PORTLET_ACCESS_DENIED = new ValueProperty( TYPE, "ShowPortletAccessDenied" );

	Value<Boolean> getShowPortletAccessDenied();

	void setShowPortletAccessDenied( String value );

	void setShowPortletAccessDenied( Boolean value );

	// *** ShowPortletInActive ***

	@Type( base = Boolean.class )
	@Label( standard = "Show Portlet InActive" )
	@DefaultValue( text = "true" )
	@XmlBinding( path = "show-portlet-inactive" )
	ValueProperty PROP_SHOW_PORTLET_INACTIVE = new ValueProperty( TYPE, "ShowPortletInActive" );

	Value<Boolean> getShowPortletInActive();

	void setShowPortletInActive( String value );

	void setShowPortletInActive( Boolean value );

	// *** ActionUrlRedirect ***

	@Type( base = Boolean.class )
	@Label( standard = "Show Url Redirect" )
	@DefaultValue( text = "false" )
	@XmlBinding( path = "action-url-redirect" )
	ValueProperty PROP_ACTION_URL_REDIRECT = new ValueProperty( TYPE, "ActionUrlRedirect" );

	Value<Boolean> getActionUrlRedirect();

	void setActionUrlRedirect( String value );

	void setActionUrlRedirect( Boolean value );

	// *** RestoreCurrentView ***

	@Type( base = Boolean.class )
	@Label( standard = "Restore Current View" )
	@DefaultValue( text = "true" )
	@XmlBinding( path = "restore-current-view" )
	ValueProperty PROP_RESTORE_CURRENT_VIEW = new ValueProperty( TYPE, "RestoreCurrentView" );

	Value<Boolean> getRestoreCurrentView();

	void setRestoreCurrentView( String value );

	void setRestoreCurrentView( Boolean value );

	// *** MaximizeEdit ***

	@Type( base = Boolean.class )
	@Label( standard = "Maximize Edit" )
	@DefaultValue( text = "false" )
	@XmlBinding( path = "maximize-edit" )
	ValueProperty PROP_MAXIMIZE_EDIT = new ValueProperty( TYPE, "MaximizeEdit" );

	Value<Boolean> getMaximizeEdit();

	void setMaximizeEdit( String value );

	void setMaximizeEdit( Boolean value );

	// *** MaximizeHelp ***

	@Type( base = Boolean.class )
	@Label( standard = "Maximize Edit" )
	@DefaultValue( text = "false" )
	@XmlBinding( path = "maximize-help" )
	ValueProperty PROP_MAXIMIZE_HELP = new ValueProperty( TYPE, "MaximizeHelp" );

	Value<Boolean> getMaximizeHelp();

	void setMaximizeHelp( String value );

	void setMaximizeHelp( Boolean value );

	// *** PopupPrint ***

	@Type( base = Boolean.class )
	@Label( standard = "Popup Print" )
	@DefaultValue( text = "true" )
	@XmlBinding( path = "pop-up-print" )
	ValueProperty PROP_POP_UP_PRINT = new ValueProperty( TYPE, "PopupPrint" );

	Value<Boolean> getPopupPrint();

	void setPopupPrint( String value );

	void setPopupPrint( Boolean value );

	// *** LayoutCacheable ***

	@Type( base = Boolean.class )
	@Label( standard = "Layout Cacheable" )
	@DefaultValue( text = "true" )
	@XmlBinding( path = "layout-cacheable" )
	ValueProperty PROP_LAYOUT_CACHEABLE = new ValueProperty( TYPE, "LayoutCacheable" );

	Value<Boolean> getLayoutCacheable();

	void setLayoutCacheable( String value );

	void setLayoutCacheable( Boolean value );

	// *** Instanceable ***

	@Type( base = Boolean.class )
	@Label( standard = "Instanceable" )
	@DefaultValue( text = "false" )
	@XmlBinding( path = "instanceable" )
	ValueProperty PROP_INSTANCEABLE = new ValueProperty( TYPE, "Instanceable" );

	Value<Boolean> getInstanceable();

	void setInstanceable( String value );

	void setInstanceable( Boolean value );

	// *** Remoteable ***

	@Type( base = Boolean.class )
	@Label( standard = "Remoteable" )
	@DefaultValue( text = "false" )
	@XmlBinding( path = "remoteable" )
	ValueProperty PROP_REMOTEABLE = new ValueProperty( TYPE, "Remoteable" );

	Value<Boolean> getRemoteable();

	void setRemoteable( String value );

	void setRemoteable( Boolean value );

	// *** Scopeable ***

	@Type( base = Boolean.class )
	@Label( standard = "Scopeable" )
	@DefaultValue( text = "false" )
	@XmlBinding( path = "scopeable" )
	ValueProperty PROP_SCOPEABLE = new ValueProperty( TYPE, "Scopeable" );

	Value<Boolean> getScopeable();

	void setScopeable( String value );

	void setScopeable( Boolean value );

	// *** UserPrincipalStrategy ***

	@Type( base = UserPrincipalStrategy.class )
	@Label( standard = "label" )
	@XmlBinding( path = "user-principal-strategy" )
	ValueProperty PROP_USER_PRINCIPAL_STRATEGY = new ValueProperty( TYPE, "UserPrincipalStrategy" );

	Value<UserPrincipalStrategy> getUserPrincipalStrategy();

	void setUserPrincipalStrategy( String value );

	void setUserPrincipalStrategy( UserPrincipalStrategy value );

	// *** PrivateRequestAttributes ***

	@Type( base = Boolean.class )
	@Label( standard = "Private Request Attributes" )
	@DefaultValue( text = "true" )
	@XmlBinding( path = " private-request-attributes" )
	ValueProperty PROP_PRIVATE_REQUEST_ATTRIBUTES = new ValueProperty( TYPE, "PrivateRequestAttributes" );

	Value<Boolean> getPrivateRequestAttributes();

	void setPrivateRequestAttributes( String value );

	void setPrivateRequestAttributes( Boolean value );

	// *** PrivateSessionAttributes ***

	@Type( base = Boolean.class )
	@Label( standard = "Private Session Attributes" )
	@DefaultValue( text = "true" )
	@XmlBinding( path = " private-session-attributes" )
	ValueProperty PROP_PRIVATE_SESSION_ATTRIBUTES = new ValueProperty( TYPE, "PrivateSessionAttributes" );

	Value<Boolean> getPrivateSessionAttributes();

	void setPrivateSessionAttributes( String value );

	void setPrivateSessionAttributes( Boolean value );

	// *** AutoPropogatedParameters ***

	@Label( standard = "label" )
	@XmlBinding( path = "autopropagated-parameters" )
	ValueProperty PROP_AUTO_PROPOGATED_PARAMETERS = new ValueProperty( TYPE, "AutoPropogatedParameters" );

	Value<String> getAutoPropogatedParameters();

	void setAutoPropogatedParameters( String value );

	// *** ActionTimeout ***

	@Type( base = Integer.class )
	@Label( standard = "Action Timeout" )
	@DefaultValue( text = "0" )
	@XmlBinding( path = "action-time-out" )
	ValueProperty PROP_ACTION_TIMEOUT = new ValueProperty( TYPE, "ActionTimeout" );

	Value<Integer> getActionTimeout();

	void setActionTimeout( String value );

	void setActionTimeout( Integer value );

	// *** RenderTimeout ***

	@Type( base = Integer.class )
	@Label( standard = "Render Timeout" )
	@DefaultValue( text = "0" )
	@XmlBinding( path = "render-timeout" )
	ValueProperty PROP_RENDER_TIMEOUT = new ValueProperty( TYPE, "RenderTimeout" );

	Value<Integer> getRenderTimeout();

	void setRenderTimeout( String value );

	void setRenderTimeout( Integer value );

	// *** RenderWeight ***

	@Type( base = Integer.class )
	@Label( standard = "Render Weight" )
	@DefaultValue( text = "0" )
	@XmlBinding( path = "render-weight" )
	ValueProperty PROP_RENDER_WEIGHT = new ValueProperty( TYPE, "RenderWeight" );

	Value<Integer> getRenderWeight();

	void setRenderWeight( String value );

	void setRenderWeight( Integer value );

	// *** Ajaxable ***

	@Type( base = Boolean.class )
	@Label( standard = "Ajaxable" )
	@XmlBinding( path = "ajaxable" )
	ValueProperty PROP_AJAXABLE = new ValueProperty( TYPE, "Ajaxable" );

	Value<Boolean> getAjaxable();

	void setAjaxable( String value );

	void setAjaxable( Boolean value );

	// *** HeaderPortalCss ***

	@Type( base = IWebResource.class )
	@Label( standard = "Header Portal Css" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "header-portal-css", type = IWebResource.class ) } )
	ListProperty PROP_HEADER_PORTAL_CSS = new ListProperty( TYPE, "HeaderPortalCss" );

	ElementList<IWebResource> getHeaderPortalCss();

	// *** HeaderPortletCss ***

	@Type( base = IWebResource.class )
	@Label( standard = "Header Portlet Css" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "header-portlet-css", type = IWebResource.class ) } )
	ListProperty PROP_HEADER_PORTLET_CSS = new ListProperty( TYPE, "HeaderPortletCss" );

	ElementList<IWebResource> getHeaderPortletCss();

	// *** HeaderPortalJavascript ***

	@Type( base = IWebResource.class )
	@Label( standard = "Header Portal Javascript" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "header-portal-javascript", type = IWebResource.class ) } )
	ListProperty PROP_HEADER_PORTAL_JAVASCRIPT = new ListProperty( TYPE, "HeaderPortalJavascript" );

	ElementList<IWebResource> getHeaderPortalJavascript();

	// *** HeaderPortletJavascript ***

	@Type( base = IWebResource.class )
	@Label( standard = "Header Portlet Javascript" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "header-portlet-javascript", type = IWebResource.class ) } )
	ListProperty PROP_HEADER_PORTLET_JAVASCRIPT = new ListProperty( TYPE, "HeaderPortletJavascript" );

	ElementList<IWebResource> getHeaderPortletJavascript();

	// *** FooterPortalCss ***

	@Type( base = IWebResource.class )
	@Label( standard = "Footer Portal Css" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "footer-portal-css", type = IWebResource.class ) } )
	ListProperty PROP_FOOTER_PORTAL_CSS = new ListProperty( TYPE, "FooterPortalCss" );

	ElementList<IWebResource> getFooterPortalCss();

	// *** FooterPortletCss ***

	@Type( base = IWebResource.class )
	@Label( standard = "Footer Portlet Css" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "footer-portlet-css", type = IWebResource.class ) } )
	ListProperty PROP_FOOTER_PORTLET_CSS = new ListProperty( TYPE, "FooterPortletCss" );

	ElementList<IWebResource> getFooterPortletCss();

	// *** FooterPortalJavascript ***

	@Type( base = IWebResource.class )
	@Label( standard = "Footer Portal Javascript" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "footer-portal-javascript", type = IWebResource.class ) } )
	ListProperty PROP_FOOTER_PORTAL_JAVASCRIPT = new ListProperty( TYPE, "FooterPortalJavascript" );

	ElementList<IWebResource> getFooterPortalJavascript();

	// *** FooterPortletJavascript ***

	@Type( base = IWebResource.class )
	@Label( standard = "Footer Portlet Javascript" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "footer-portlet-javascript", type = IWebResource.class ) } )
	ListProperty PROP_FOOTER_PORTLET_JAVASCRIPT = new ListProperty( TYPE, "FooterPortletJavascript" );

	ElementList<IWebResource> getFooterPortletJavascript();

	// *** CssWrapperClass ***

	@Label( standard = "Css Wrapper Class" )
	@XmlBinding( path = "css-wrapper-class" )
	ValueProperty PROP_CSS_WRAPPER_CLASS = new ValueProperty( TYPE, "CssWrapperClass" );

	Value<String> getCssWrapperClass();

	void setCssWrapperClass( String value );

	// *** FacebookIntegration ***

	@Type( base = FacebookIntegration.class )
	@Label( standard = "Facebook Integration" )
	@XmlBinding( path = "facebook-integration" )
	@DefaultValue( text = "iframe" )
	ValueProperty PROP_FACEBOOK_INTEGRATION = new ValueProperty( TYPE, "FacebookIntegration" );

	Value<FacebookIntegration> getFacebookIntegration();

	void setFacebookIntegration( String value );

	void setFacebookIntegration( FacebookIntegration value );

	// *** AddDefaultResource ***

	@Type( base = Boolean.class )
	@Label( standard = "Add Default Resource" )
	@XmlBinding( path = "add-default-resource" )
	@DefaultValue( text = "false" )
	ValueProperty PROP_ADD_DEFAULT_RESOURCE = new ValueProperty( TYPE, "AddDefaultResource" );

	Value<Boolean> getAddDefaultResource();

	void setAddDefaultResource( String value );

	void setAddDefaultResource( Boolean value );

	// *** System ***

	@Type( base = Boolean.class )
	@Label( standard = "System" )
	@XmlBinding( path = "system" )
	@DefaultValue( text = "false" )
	ValueProperty PROP_SYSTEM = new ValueProperty( TYPE, "System" );

	Value<Boolean> getSystem();

	void setSystem( String value );

	void setSystem( Boolean value );

	// *** Active ***

	@Type( base = Boolean.class )
	@Label( standard = "Active" )
	@XmlBinding( path = "active" )
	@DefaultValue( text = "true" )
	ValueProperty PROP_ACTIVE = new ValueProperty( TYPE, "Active" );

	Value<Boolean> getActive();

	void setActive( String value );

	void setActive( Boolean value );

	// *** Include ***

	@Type( base = Boolean.class )
	@Label( standard = "Include" )
	@XmlBinding( path = "include" )
	@DefaultValue( text = "false" )
	ValueProperty PROP_INCLUDE = new ValueProperty( TYPE, "Include" );

	Value<Boolean> getInclude();

	void setInclude( String value );

	void setInclude( Boolean value );

}
