/*******************************************************************************
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
 *******************************************************************************/

package com.liferay.ide.portlet.core.lfportlet.model;

import com.liferay.ide.portlet.core.lfportlet.model.internal.IconRelativePathService;
import com.liferay.ide.portlet.core.lfportlet.model.internal.LiferayPortletNameValidationService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.NumericRange;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;


/**
 * @author Simon Jiang
 */
@Image( path = "images/elcl16/portlet_16x16.png" )
public interface LiferayPortlet extends Element
{
    ElementType TYPE = new ElementType( LiferayPortlet.class );


    // *** portlet-name ***

    @Label( standard = "Portlet Name" )
    @Required
    @Service( impl = LiferayPortletNameValidationService.class )
    @Unique
    @XmlBinding( path = "portlet-name" )
    ValueProperty PROP_PORTLET_NAME = new ValueProperty( TYPE, "PortletName" );

    Value<String> getPortletName();
    void setPortletName( String portletName );


    // *** icon? ***

    @Label( standard = "Icon" )
    @MustExist
    @Service( impl = IconRelativePathService.class )
    @Type( base = Path.class )
    @ValidFileSystemResourceType( FileSystemResourceType.FILE )
    @XmlBinding( path = "icon" )
    ValueProperty PROP_ICON = new ValueProperty( TYPE, "Icon" );

    Value<Path> getIcon();
    void setIcon( Path icon );
    void setIcon( String icon );


    // *** virtual-path? ***

    @Label( standard = "Virtual Path" )
    @XmlBinding( path = "virtual-path" )
    ValueProperty PROP_VIRTUAL_PATH = new ValueProperty( TYPE, "VirtualPath" );

    Value<String> getVirtualPath();
    void setVirtualPath( String value );


    // *** struts-path? ***

    @Label( standard = "Struts Path" )
    @XmlBinding( path = "struts-path")
    ValueProperty PROP_STRUTS_PATH = new ValueProperty( TYPE, "StrutsPath" );

    Value<String> getStrutsPath();
    void setStrutsPath( String value );


    // *** parent-struts-path? ***

    @Label( standard = "Parent Struts Path" )
    @XmlBinding( path = "parent-struts-path" )
    ValueProperty PROP_PARENT_STRUTS_PATH = new ValueProperty( TYPE, "ParentStrutsPath" );

    Value<String> getParentStrutsPath();
    void setParentStrutsPath( String value );


    // *** configuration-action-class? ***

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.portlet.ConfigurationAction" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "configuration-action-class" )
    ValueProperty PROP_CONFIGURATION_ACTION_CLASS= new ValueProperty( TYPE, "ConfigurationActionClass" );

    ReferenceValue<JavaTypeName, JavaType > getConfigurationActionClass();
    void setConfiguration( JavaTypeName value );
    void setConfiguration( String value );


    // *** indexer-class* ***

    @Label( standard = "Indexer Class" )
    @Type( base = IndexerClass.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "indexer-class", type = IndexerClass.class ) )
    ListProperty PROP_INDEXER_CLASSES = new ListProperty( TYPE, "IndexerClasses" );

    ElementList<IndexerClass> getIndexerClasses();


    //    open-search-class?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.search.OpenSearch")
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "open-search-class" )
    ValueProperty PROP_OPEN_SEARCH_CLASS = new ValueProperty( TYPE, "OpenSearchClass" );

    ReferenceValue<JavaTypeName, JavaType> getOpenSearchClass();
    void setOpenSearchClass( JavaTypeName value );
    void setOpenSearchClass( String value );


    // *** schedule entry* ***

    @Type( base = SchedulerEntry.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "scheduler-entry", type = SchedulerEntry.class ) )
    ListProperty PROP_SCHEDULER_ENTRIES = new ListProperty( TYPE, "SchedulerEntries" );

    ElementList<SchedulerEntry> getSchedulerEntries();


    //    portlet-url-class?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portlet.PortletURLImplWrapper" )
    @Label( standard = "Portlet Url Class" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "portlet-url-class" )
    ValueProperty PROP_PORTLET_URL_CLASS = new ValueProperty( TYPE, "PortletUrlClass" );

    ReferenceValue<JavaTypeName, JavaType> getPortletUrlClass();
    void setPortletUrlClass( JavaTypeName value );
    void setPortletUrlClass( String value );


    //    friendly-url-mapper-class?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.portlet.FriendlyURLMapper" )
    @Label( standard = "Friendly Url Mapper Class" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "friendly-url-mapper-class" )
    ValueProperty PROP_FRIENDLY_URL_MAPPER_CLASS = new ValueProperty( TYPE, "FriendlyURLMapperClass" );

    ReferenceValue<JavaTypeName, JavaType> getFriendlyURLMapperClass();
    void setFriendlyURLMapperClass( JavaTypeName value );
    void setFriendlyURLMapperClass( String value );


    //    friendly-url-mapping?

    @Label( standard = "Friendly URL Mapping ")
    @XmlBinding( path = "friendly-url-mapping" )
    ValueProperty PROP_FRIENDLY_URL_MAPPING = new ValueProperty( TYPE, "FriendlyURLMapping" );

    Value<String> getFriendlyURLMapping();
    void setFriendlyURLMapping( String value );


    //    friendly-url-routes?

    @Label( standard = "Friendly URL Routes ")
    @XmlBinding( path = "friendly-url-routes" )
    ValueProperty PROP_FRIENDLY_URL_Routes = new ValueProperty( TYPE, "FriendlyURLRoutes" );

    Value<String> getFriendlyURLRoutes();
    void setFriendlyURLRoutes( String value );


    //    url-encoder-class?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.servlet.URLEncoder" )
    @Label( standard = "URL Encoder Class" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "url-encoder-class" )
    ValueProperty PROP_URL_ENCODER_CLASS = new ValueProperty( TYPE, "URLEncoderClass" );

    ReferenceValue<JavaTypeName, JavaType> getURLEncoderClass();
    void setURLEncoderClass( JavaTypeName value );
    void setURLEncoderClass( String value );


    //    portlet-data-handler-class?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.lar.PortletDataHandler" )
    @Label( standard = "Portlet Data Handler Class" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "portlet-data-handler-class" )
    ValueProperty PROP_PORTLET_DATA_HANDLER_CLASS = new ValueProperty( TYPE, "PortletDataHandlerClass" );

    ReferenceValue<JavaTypeName, JavaType> getPortletDataHandlerClass();
    void setPortletDataHandlerClass( JavaTypeName value );
    void setPortletDataHandlerClass( String value );


    //    staged-model-data-handler-class*

    @Label( standard = "Staged Model Data Handler Classes" )
    @Type( base = StagedModelDataHandlerClass.class )
    @XmlListBinding
    (
        mappings = @XmlListBinding.Mapping
        (
            element = "staged-model-data-handler-class",
            type = StagedModelDataHandlerClass.class
        )
    )
    ListProperty PROP_STAGED_MODEL_DATA_HANDLER_CLASSES = new ListProperty( TYPE, "StagedModelDataHandlerClasses" );

    ElementList<StagedModelDataHandlerClass> getStagedModelDataHandlerClasses();


    //    portlet-layout-listener-class?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.portlet.PortletLayoutListener" )
    @Label( standard = "Portlet Layout Listener Class" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "portlet-layout-listener-class" )
    ValueProperty PROP_PORTLET_LAYOUT_LISTENER_CLASS = new ValueProperty( TYPE, "PortletLayoutListenerClass" );

    ReferenceValue<JavaTypeName, JavaType> getPortletLayoutListenerClass();
    void setPortletLayoutListenerClass( JavaTypeName value );
    void setPortletLayoutListenerClass( String value );


    //    poller-processor-class?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.Poller.PollerProcessor" )
    @Label( standard = "Poller Processor Class" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "poller-processor-class" )
    ValueProperty PROP_POLLER_PROCESSOR_CLASS = new ValueProperty( TYPE, "PollerProcessorClass" );

    ReferenceValue<JavaTypeName, JavaType> getPollerProcessorClass();
    void setPollerProcessorClass( JavaTypeName value );
    void setPollerProcessorClass( String value );


    //    pop-message-listener-class?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portlet.kernel.pop.MessageListener" )
    @Label( standard = "Pop Message Listener Class" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "pop-message-listener-class" )
    ValueProperty PROP_POP_MESSAGE_LISTENER_CLASS = new ValueProperty( TYPE, "PopMessageListenerClass" );

    ReferenceValue<JavaTypeName, JavaType> getPopMessageListenerClass();
    void setPopMessageListenerClass( JavaTypeName value );
    void setPopMessageListenerClass( String value );


    //    social-activity-interpreter-class*

    @Type( base = SocialActivityInterpreterClass.class )
    @XmlListBinding
    (
        mappings = @XmlListBinding.Mapping
        (
            element = "social-activity-interpreter-class",
            type = SocialActivityInterpreterClass.class
        )
    )
    ListProperty PROP_SOCIAL_ACTIVITY_INTERPRETER_CLASSES = new ListProperty( TYPE, "SocialActivityInterpreterClasses" );

    ElementList<SocialActivityInterpreterClass> getSocialActivityInterpreterClasses();



    // social-request-interpreter-class?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portlet.social.model.SocialRequestInterpreter" )
    @Label( standard = "Social Request Interpreter Class" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "social-request-interpreter-class" )
    ValueProperty PROP_SOCIAL_REQUEST_INTERPRETER_CLASS = new ValueProperty( TYPE, "SocialRequestInterpreterClass" );

    ReferenceValue<JavaTypeName, JavaType> getSocialRequestInterpreterClass();
    void setSocialRequestInterpreterClass( JavaTypeName value );
    void setSocialRequestInterperterClass( String value );


    // user-notification-definitions?

    @Label( standard = "User Notification Definitions" )
    @MustExist
    @Type( base = Path.class )
    @ValidFileSystemResourceType( FileSystemResourceType.FILE )
    @XmlBinding( path = "user-notification-definitions" )
    ValueProperty PROP_USER_NOTIFICATION_DEFINITIONS = new ValueProperty( TYPE, "UserNotificationDefinitions" );

    Value<Path> getUserNotificationDefinitions();
    void setUserNotificationDefinitions( String value );


    // user-notification-handler-class*

    @Type( base = UserNotificationHandlerClass.class )
    @XmlListBinding
    (
        mappings = @XmlListBinding.Mapping
        (
            element = "user-notification-handler-class",
            type = UserNotificationHandlerClass.class
        )
    )
    ListProperty PROP_USER_NOTIFICATION_HANDLER_CLASSES = new ListProperty( TYPE, "UserNotificationHandlerClasses" );

    ElementList<UserNotificationHandlerClass> getUserNotificationHandlerClasses();



    // webdav-storage-token?

    @MustExist
    @Label( standard = "Web DAV Storage Token" )
    @Type( base = Path.class )
    @XmlBinding( path = "webdav-storage-token" )
    ValueProperty PROP_WEB_DAV_STORAGE_TOKEN = new ValueProperty( TYPE, "WebDAVStorageToken" );

    Value<Path> getWebDAVStorageToken();
    void setWebDAVStorageToken( String value );
    void setWebDAVStorateToken( Path value );


    //    webdav-storage-class?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portlet.kernal.webdav.WebDAVStorage" )
    @Label( standard = "WebDAV Storage Class" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "webdav-storage-class" )
    ValueProperty PROP_WEBDAV_STORAGE_CLASS = new ValueProperty( TYPE, "WebDAVStorageClass" );

    ReferenceValue<JavaTypeName, JavaType> getWebDAVStorageClass();
    void setWebDAVStorageClass( JavaTypeName value );
    void setWebDAVStorageClass( String value );


    //    xml-rpc-method-class?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portlet.kernal.xmlrpc.Method" )
    @Label( standard = "Xml RPC Method Class" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "xml-rpc-method-class" )
    ValueProperty PROP_XML_RPC_METHOD_CLASS = new ValueProperty( TYPE, "XmlRPCMethodClass" );

    ReferenceValue<JavaTypeName, JavaType> getXmlRPCMethodClass();
    void setXmlRPCMethodClass( JavaTypeName value );
    void setXmlRPCMethodClass( String value );


    //    control-panel-entry-category?

    @Label( standard = "Control Panel Entry Category" )
    @PossibleValues
    (
        values =
        {
            "apps", "configurations", "sites", "users", "site_administration.configuration",
            "site_administration.content" ,"site_administration.pages","site_administration.users"
        }
    )
    @XmlBinding( path = "control-panel-entry-category" )
    ValueProperty PROP_CONTROL_PANEL_ENTRY_CATEGORY = new ValueProperty( TYPE, "ControlPanelEntryCategory" );

    Value<String> getControlPanelEntryCategory();
    void setControlPanelEntryCategory( String value );


    // control-panel-weight?

    @Label( standard = "Control Panel Entry Weight" )
    @NumericRange( min = "0" )
    @Type( base = Double.class )
    @XmlBinding( path = "control-panel-entry-weight" )
    ValueProperty PROP_CONTROL_PANEL_ENTRY_WEIGHT = new ValueProperty( TYPE, "ControlPanelEntryWeight" );

    Value<Double> getControlPanelEntryWeight();
    void setControlPanelEntryWeight( Double value );
    void setControlPanelEntryWeight( String value );


    //    control-panel-entry-class?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portlet.ControlPanelEntry" )
    @Label( standard = "Control Panel Entry Class" )
    @MustExist
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @XmlBinding( path = "control-panel-entry-class" )
    ValueProperty PROP_CONTROL_PANEL_ENTRY_CLASS = new ValueProperty( TYPE, "ControlPanelEntryClass" );

    ReferenceValue<JavaTypeName, JavaType> getControlPanelEntryClass();
    void setControlPanelEntryClass( JavaTypeName value );
    void setControlPanleEntryClass( String value );


    //    asset-renderer-factory*

    @Label( standard = "Asset Renderer Factories" )
    @Type( base = AssetRendererFactory.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "asset-renderer-factory", type = AssetRendererFactory.class) )
    ListProperty PROP_ASSET_RENDERER_FACTORIES = new ListProperty( TYPE, "AssetRendererFactories" );

    ElementList<AssetRendererFactory> getAssetRendererFactories();


    //    atom-collection-adapter*

    @Label( standard = "Atom Collection Adapters" )
    @Type( base = AtomCollectionAdapter.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "atom-collection-adapter", type = AtomCollectionAdapter.class) )
    ListProperty PROP_ATOM_COLLECTION_ADAPTERS = new ListProperty( TYPE, "AtomCollectionAdapters" );

    ElementList<AtomCollectionAdapter> getAtomCollectionAdapters();


    //    custom-attributes-display*

    @Label( standard = "Custom Attribute Displays" )
    @Type( base = CustomAttributesDisplay.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "custom-attributes-display", type = CustomAttributesDisplay.class) )
    ListProperty PROP_CUSTOM_ATTRIBUTES_DISPLAYS = new ListProperty( TYPE, "CustomAttributesDisplays" );

    ElementList<CustomAttributesDisplay> getCustomAttributesDisplays();


    //    ddm-display?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portlet.dynamicdatamapping.util.DDMDisplay" )
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "ddm-display" )
    ValueProperty PROP_DDM_DISPLAY = new ValueProperty( TYPE, "DDMDisplay" );

    ReferenceValue<JavaTypeName, JavaType> getDDMDisplay();
    void setDDMDisplay( JavaTypeName value );
    void setDDMDisplay( String value );


    //    permission-propagator?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portal.security.permission.PermissionPropagator" )
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "permission-propagator" )
    ValueProperty PROP_PERMISSION_PROPAGATOR = new ValueProperty( TYPE, "PermissionPropagator" );

    ReferenceValue<JavaTypeName, JavaType> getPermissionPropagator();
    void setPermissionPropagator( JavaTypeName value );
    void setPermissionPropagator( String value );


    //    trash-handler*

    @Label( standard = "Transh Handlers" )
    @Type( base = TrashHandler.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "trash-handler", type = TrashHandler.class) )
    ListProperty PROP_TRASH_HANDLERS = new ListProperty( TYPE, "TrashHandlers" );

    ElementList<TrashHandler> getTrashHandlers();


    // template-handler?

    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.template.TemplateHandler" )
    @Label( standard = "Template Handler" )
    @MustExist
    @Reference( target = JavaType.class )
    @Type( base = JavaTypeName.class )
    @XmlBinding( path = "template-handler" )
    ValueProperty PROP_TEMPLATE_HANDLER = new ValueProperty( TYPE, "TemplateHandler" );

    ReferenceValue<JavaTypeName, JavaType> getTemplateHandler();
    void setTemplateHandler( JavaTypeName value );
    void setTemplateHandler( String value );


    //    workflow-handler*

    // Have no idea about it, dtd has no description of it



    //    preferences-company-wide?

    @DefaultValue( text = "false" )
    @Label( standard = "Preferences Company Wide" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "preferences-company-wide" )
    ValueProperty PROP_PREFERENCES_COMPANY_WIDE = new ValueProperty( TYPE, "PreferencesCompanyWide" );

    Value<Boolean> getPreferencesCompanyWide();
    void setPreferencesCompanyWide( Boolean value );
    void setPreferencesCompanyWide( String value );


    //    preferences-unique-per-layout?

    @DefaultValue( text = "true" )
    @Label( standard = "Preferences Unique Per Layout" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "preferences-unique-per-layout" )
    ValueProperty PROP_PREFERENCES_UNIQUE_PER_LAYOUT = new ValueProperty( TYPE, "PreferencesUniquePerLayout" );

    Value<Boolean> getPreferencesUniquePerLayout();
    void setPreferencesUniquePerLayout( Boolean value );
    void setPreferencesUniquePerLayout( String value );


    //    preferences-owned-by-group?

    @DefaultValue( text = "true" )
    @Label( standard = "Preferences Owned By Group" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "preferences-owned-by-group" )
    ValueProperty PROP_PREFERENCES_OWNED_BY_GROUP = new ValueProperty( TYPE, "PreferencesOwnedByGroup" );

    Value<Boolean> getPreferencesOwnedByGroup();
    void setPreferencesOwnedByGroup( Boolean value );
    void setPreferencesOwnedByGroup( String value );


    //    use-default-template?

    @DefaultValue( text = "true" )
    @Label( standard = "Use Default Template" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "use-default-template" )
    ValueProperty PROP_USE_DEFAULT_TEMPLATE = new ValueProperty( TYPE, "UseDefaultTemplate" );

    Value<Boolean> getUseDefaultTemplate();
    void setUseDefaultTemplate( Boolean value );
    void setUseDefaultTemplate( String value );


    //    show-portlet-access-denied?

    @DefaultValue( text = "false" ) // To Default Value is set in portal.properties
    @Label( standard = "Show Portlet Access Denied" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "show-portlet-access-denied" )
    ValueProperty PROP_SHOW_PORTLET_ACCESS_DENIED = new ValueProperty( TYPE, "ShowPortletAccessDenied" );

    Value<Boolean> getShwoPortletAccessDenied();
    void setShowPortletAccessDenied( Boolean value );
    void setShowPortletAccessDenied( String value );


    //    show-portlet-inactive?

    @DefaultValue( text = "false" ) // To Default Value is set in portal.properties
    @Label( standard = "Show Url Inactive" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "show-portlet-inactive" )
    ValueProperty PROP_SHOW_PORTLET_INACTIVE = new ValueProperty( TYPE, "ShowPortletInactive" );

    Value<Boolean> getShwoPortletInactive();
    void setShowPortletInactive( Boolean value );
    void setShowPortletInactive( String value );


    //    action-url-redirect?

    @DefaultValue( text = "false" )
    @Label( standard = "Action Url Redirect" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "action-url-redirect" )
    ValueProperty PROP_ACTION_URL_REDIRECT = new ValueProperty( TYPE, "ActionUrlRedirect" );

    Value<Boolean> getActionUrlRedirect();
    void setActionUrlRedirect( Boolean value );
    void setActionUrlRedirect( String value );


    //    restore-current-view?

    @DefaultValue( text = "true" )
    @Label( standard = "Restore Current View" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "restore-current-view" )
    ValueProperty PROP_RESTORE_CURRENT_VIEW = new ValueProperty( TYPE, "RestoreCurrentView" );

    Value<Boolean> getRestoreCurrentView();
    void setRestoreCurrentView( Boolean value );
    void setRestoreCurrentView( String value );


    //    maximize-edit?

    @DefaultValue( text = "false" )
    @Label( standard = "Maximize Edit" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "maximize-edit" )
    ValueProperty PROP_MAXIMIZE_EDIT = new ValueProperty( TYPE, "MaxmizeEdit" );

    Value<Boolean> getMaxmizeEdit();
    void setMaximizeEdit( Boolean value );
    void setMaximizeEdit( String value );


    //    maximize-help?

    @DefaultValue( text = "false" )
    @Label( standard = "Maximize Help" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "maximize-help" )
    ValueProperty PROP_MAXIMIZE_HELP = new ValueProperty( TYPE, "MaxmizeHelp" );

    Value<Boolean> getMaxmizeHelp();
    void setMaximizeHelp( Boolean value );
    void setMaximizeHelp( String value );


    //    pop-up-print?

    @DefaultValue( text = "true" )
    @Label( standard = "Pop up Print" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "pop-up-pring" )
    ValueProperty PROP_POP_UP_PRINT = new ValueProperty( TYPE, "PopUpPrint" );

    Value<Boolean> getPopUpPrint();
    void setPopUpPrint( Boolean value );
    void setPopUpPrint( String value );


    //    layout-cacheable?

    @DefaultValue( text = "true" ) // To be reconsidered
    @Label( standard = "Layout Cacheable" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "layout-cacheable" )
    ValueProperty PROP_LAYOUT_CACHEABLE = new ValueProperty( TYPE, "LayoutCacheable" );

    Value<Boolean> getLayoutCacheable();
    void setLayoutCacheable( Boolean value );
    void setLayoutCacheable( String value );


    //    instanceable?

    @DefaultValue( text = "false" )
    @Label( standard = "Instanceable" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "instanceable" )
    ValueProperty PROP_INSTANCEABLE = new ValueProperty( TYPE, "Instanceable" );

    Value<Boolean> getInstanceable();
    void setInstanceable( Boolean value );
    void setInstanceable( String value );


    //    remoteable?

    @DefaultValue( text = "false" )
    @Label( standard = "Remoteable" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "remoteable" )
    ValueProperty PROP_REMOTEABLE = new ValueProperty( TYPE, "Remoteable" );

    Value<Boolean> getReomteable();
    void setRemoteable( Boolean value );
    void setRemoteable( String value );


    //    scopeable?

    @DefaultValue( text = "false" )
    @Label( standard = "Scopeable" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "scopeable" )
    ValueProperty PROP_SCOPEABLE = new ValueProperty( TYPE, "Scopeable" );

    Value<Boolean> getScopeable();
    void setScopeable( Boolean value );
    void setScopeable( String value );


    //    user-principal-strategy?

    @DefaultValue( text = "userId" )
    @Label( standard = "User Principal Strategy" )
    @PossibleValues( values = { "userId", "screenName" } )
    @XmlBinding( path = "user-principal-strategy" )
    ValueProperty PROP_USER_PRINCIPAL_STRATEGY = new ValueProperty( TYPE, "UserPrincipalStrategy" );

    Value<String> getUserPrincipalStrategy();
    void setUserPrincipalStrategy( String value );


    //    private-request-attributes?

    @DefaultValue( text = "true" )
    @Label( standard = "Private Request Attributes" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "private-request-attributes" )
    ValueProperty PROP_REQUEST_SESSION_ATTRIBUTES = new ValueProperty( TYPE, "PrivateRequestAttributes" );

    Value<Boolean> getPrivateRequestAttributes();
    void setPrivateRequestAttributes( Boolean value );
    void setPrivateRequestAttributes( String value );


    //    private-session-attributes?

    @DefaultValue( text = "true" )
    @Label( standard = "Private Session Attributes" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "private-session-attributes" )
    ValueProperty PROP_PRIVATE_SESSION_ATTRIBUTES = new ValueProperty( TYPE, "PrivateSessionAttributes" );

    Value<Boolean> getPrivateSessionAttributes();
    void setPrivateSessionAttributes( Boolean value );
    void setPrivateSessionAttributes( String value );


    //    requires-namespaced-parameters?

    @DefaultValue( text = "true")
    @Label( standard = "Requires Namespace Paremeters" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "requires-namespace-parameters")
    ValueProperty PROP_REQUIRES_NAMESPACE_PARAMETERS = new ValueProperty( TYPE, "RequiresNamespaceParameters" );

    Value<Boolean> getRequiresNamesapceParameters();
    void setRequiresNamespaceParameters( Boolean value );
    void setRequiresNamespaceParameters( String value );


    //    action-timeout?

    @DefaultValue( text = "0")
    @Label( standard = " Action Timeout" )
    @Type( base = Double.class )
    @XmlBinding( path = "action-timeout" )
    ValueProperty PROP_ACTION_TIMEOUT = new ValueProperty( TYPE, "ActionTimeout" );

    Value<Double> getActionTimeout();
    void setActionTimeout( Double value );
    void setActionTimeout( String value );


    //    render-timeout?

    @Type( base = Double.class )
    @Label( standard = " Render Timeout" )
    @XmlBinding( path = "render-timeout" )
    @DefaultValue( text = "0")
    ValueProperty PROP_RENDER_TIMEOUT = new ValueProperty( TYPE, "RenderTimeout" );

    Value<Double> getRenderTimeout();
    void setRenderTimeout( Double value );
    void setRenderTimeout( String value );


    //    render-weight?

    @DefaultValue( text = "1" )
    @Label( standard = " Render Weight" )
    @Type( base = Double.class )
    @XmlBinding( path = "render-weight" )
    ValueProperty PROP_RENDER_WEIGHT = new ValueProperty( TYPE, "RenderWeight" );

    Value<Double> getRenderWeight();
    void setRenderWeight( Double value );
    void setRenderWeight( String value );


    //    ajaxable?

    @DefaultValue( text = "true" )
    @Label( standard = "Ajaxable" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "ajaxable" )
    ValueProperty PROP_AJAXABLE = new ValueProperty( TYPE, "Ajaxable" );

    Value<Boolean> getAjaxable();
    void setAjaxable( Boolean value );
    void setAjaxable( String value );


    //    header-portal-css*

    @Label( standard = "Header Portal Csses" )
    @Type( base = HeaderPortalCss.class)
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "header-portal-css", type = HeaderPortalCss.class ) )
    ListProperty PROP_HEADER_PORTAL_CSSES = new ListProperty( TYPE, "HeaderPortalCsses" );

    ElementList<HeaderPortalCss> getHeaderPortalCsses();


    //    header-portlet-css*

    @Label( standard = "Header Portlet Csses" )
    @Type( base = HeaderPortletCss.class)
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "header-portlet-css", type = HeaderPortletCss.class ) )
    ListProperty PROP_HEADER_PORTLET_CSSES = new ListProperty( TYPE, "HeaderPortletCsses" );

    ElementList<HeaderPortletCss> getHeaderPortletCsses();


    //    header-portal-javascript*

    @Label( standard = "Header Portal Javascripts" )
    @Type( base = HeaderPortalJavascript.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "header-portal-javascript", type = HeaderPortalJavascript.class ) )
    ListProperty PROP_HEADER_PORTAL_JAVASCRIPTS = new ListProperty( TYPE, "HeaderPortalJavascripts" );

    ElementList<HeaderPortalJavascript> getHeaderPortalJavascripts();


    //    header-portlet-javascript*

    @Label( standard = "Header Portlet Javascripts" )
    @Type( base = HeaderPortletJavascript.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "header-portlet-javascript", type = HeaderPortletJavascript.class ) )
    ListProperty PROP_HEADER_PORTLET_JAVASCRIPTS = new ListProperty( TYPE, "HeaderPortletJavascripts" );

    ElementList<HeaderPortletJavascript> getHeaderPortletJavascripts();


    //    footer-portal-css*

    @Label( standard = "Footer Portal Csses" )
    @Type( base = FooterPortalCss.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "footer-portal-css", type = FooterPortalCss.class ) )
    ListProperty PROP_FOOTER_PORTAL_CSSES = new ListProperty( TYPE, "FooterPortalCsses" );

    ElementList<FooterPortalCss> getFooterPortalCsses();


    //    footer-portlet-css*

    @Label( standard = "Footer Portlet Csses" )
    @Type( base = FooterPortletCss.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "footer-portlet-css", type = FooterPortletCss.class ) )
    ListProperty PROP_FOOTER_PORTLET_CSSES = new ListProperty( TYPE, "FooterPortletCsses" );

    ElementList<FooterPortletCss> getFooterPortletCsses();


    //    footer-portal-javascript*

    @Label( standard = "Footer Portal Javascripts" )
    @Type( base = FooterPortalJavascript.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "footer-portal-javascript", type = FooterPortalJavascript.class ) )
    ListProperty PROP_FOOTER_PORTAL_JAVASCRIPTS = new ListProperty( TYPE, "FooterPortalJavascripts" );

    ElementList<FooterPortalJavascript> getFooterPortalJavascripts();


    //    footer-portlet-javascript*

    @Label( standard = "Footer Portlet Javascripts" )
    @Type( base = FooterPortletJavascript.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "footer-portlet-javascript", type = FooterPortletJavascript.class ) )

    ListProperty PROP_FOOTER_PORTLET_JAVASCRIPTS = new ListProperty( TYPE, "FooterPortletJavascripts" );

    ElementList<FooterPortletJavascript> getFooterPortletJavascripts();


    //    css-class-wrapper?

    @Label( standard = "Css Class Wrapper" )
    @XmlBinding( path = "css-class-wrapper" )
    ValueProperty PROP_CSS_CLASS_WRAPPER = new ValueProperty( TYPE, "CssClassWrapper" );

    Value<String> getCssClassWrapper();
    void setCssClassWrappper( String value );


    //    facebook-integration?

    @DefaultValue( text = "fbml" )
    @Label( standard = "Facebook Integration" )
    @PossibleValues( values = { "fbml", "iframe" } )
    @XmlBinding( path = "face-integration" )
    ValueProperty PROP_FACEBOOK_INTEGRATION = new ValueProperty( TYPE, "FacebookIntegration" );

    Value<String> getFacebookIntegration();
    void setFacebookIntegration( String value );


    //    add-default-resource?

    @Label( standard = "Add Default Resource" )
    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    @XmlBinding( path = "add-default-resource" )
    ValueProperty PROP_ADD_DEFAULT_RESOURDE = new ValueProperty( TYPE, "AddDefaultResource" );

    Value<Boolean> getAddDefaultResource();
    void setAddDefaultSystem( Boolean value );
    void setAddDefaultSystem( String value );


    //    system?

    @DefaultValue( text = "false" )
    @Label( standard = "System" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "system")
    ValueProperty PROP_SYSTEM = new ValueProperty( TYPE, "System" );

    Value<Boolean> getSystem();
    void setSystem( Boolean value );
    void setSystem( String value );


    //    active?

    @DefaultValue( text = "true" )
    @Label( standard = "Active" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "active" )
    ValueProperty PROP_ACTIVE = new ValueProperty( TYPE, "Active" );

    Value<Boolean> getActive();
    void setActive( Boolean value );
    void setActive( String value );


    //    include?

    @DefaultValue( text = "true" )
    @Label( standard = "Include" )
    @Type( base = Boolean.class )
    @XmlBinding( path = "include" )
    ValueProperty PROP_INDCLUDE = new ValueProperty( TYPE, "Include" );

    Value<Boolean> getInclude();
    void setInclude( Boolean value );
    void setInclude( String value );
}