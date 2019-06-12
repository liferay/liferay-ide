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

package com.liferay.ide.ui.swtbot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Ying Xu
 * @author Vicky Wang
 * @author Li Lu
 * @author Rui Wang
 * @author Lily Li
 */
public interface UI {

	public final String ABOUT = "About";

	public final String ABSTRACT = "abstract";

	public final String ACTIVATOR = "activator";

	public final String ADD = "Add";

	public final String ADD_ALL = "Add All >>";

	public final String ADD_AND_REMOVE = "Add and Remove...";

	public final String ADD_EVENT_ACTION = "Add Event Action";

	public final String ADD_FILE_PATH = "Add Override File Path";

	public final String ADD_FILES_FROM_OSGI_TO_OVERRIDE = "Add Files From OSGi Bundle to Override...";

	public final String ADD_FROM_LIFERAY = "Add from Liferay...";

	public final String ADD_PROJECT_TO_WORKING_SET = "Add project to working set";

	public final String ADD_PROPERTY_KEY = "Add Property Key";

	public final String ADD_PROPERTY_OVERRIDE = "Add Property Override";

	public final String ADD_RESOURCE_ACTION = "Add Resource Action";

	public final String ADD_ROLE = "Add Role";

	public final String ADD_SERVICE = "Add Service";

	public final String ADD_SERVICE_WRAPPER = "Add Service Wrapper";

	public final String ADD_TO_CONTROL_PANEL = "Add to Control Panel";

	public final String ADD_WITH_BRACKET = "Add >";

	public final String ADD_WITH_DOT = "Add...";

	public final String ALL = "all";

	public final String ALLOW_MULTIPLE_INSTANCES = "Allow multiple instances";

	public final String ANT_LIFERAY_PLUGINS_SDK = "Ant (liferay-plugins-sdk)";

	public final String API = "api";

	public final String APPLICATION_CLASS = "Application class:";

	public final String APPLY_AND_CLOSE = "Apply and Close";

	public final String ARCHETYPE = "Archetype:";

	public final String AUTH_FAILURES = "Auth Failures";

	public final String AUTH_MAX_FAILURE = "Auth Max Failure";

	public final String AUTHENTICATOR = "Authenticator";

	public final String AUTHOR = "Author";

	public final String AUTOMATIC_PROJECT_SYNCHRONIZATION = "Automatic Project Synchronization";

	public final String AVAILABLE_SOFTWARE_SITES = "Available Software Sites";

	public final String BACK_WITH_LEFT_BRACKET = "< Back";

	public final String BROWSE = "Browse";

	public final String BROWSE_WITH_DOT = "Browse...";

	public final String BUILD_FRAMEWORK = "Build Framework:";

	public final String BUILD_LANG = "build-lang";

	public final String BUILD_SERVICE = "build-service";

	public final String BUILD_SERVICES = "Build Services";

	public final String BUILD_TYPE = "Build type:";

	public final String BUILD_WSDD = "Build WSDD";

	public final String BUNDLE_URL = "Bundle url:";

	public final String CANCEL = "Cancel";

	public final String CANCEL_OPERATION = "Cancel Operation";

	public final String CHOOSE_A_PACKAGE = "Choose a package:";

	public final String CHOOSE_A_SUPERCLASS = "Choose a superclass:";

	public final String CHOOSE_A_VALID_PROJECT_FILE = "Choose a valid project file";

	public final String CLASS = "Class:";

	public final String CLASSIC = "classic";

	public final String CLASSNAME = "Classname:";

	public final String CLEAR_LOG_VIEWER = "Clear Log Viewer";

	public final String CLICK_TO_PERFORM = "Click to perform";

	public final String CLOSE = "Close";

	public final String CLOSE_PROJECT = "Close Project";

	public final String COLLAPSE_ALL = "Collapse All";

	public final String COMPONENT_CLASS_NAME = "Component Class Name:";

	public final String COMPONENT_CLASS_TEMPLATE = "Component Class Template:";

	public final String COMPONENT_SUITE = "Component Suite:";

	public final String CONFIG = "Config";

	public final String CONFIGURE_LIFERAY_WORKSPACE_SETTINGS = "Configure Liferay Workspace Settings";

	public final String CONFIGURE_UPGRADE_PLANNER_OUTLINES = "Configure Upgrade Planner outlines";

	public final String CONSOLE = "Console";

	public final String CONSTRUCTORS_FROM_SUPERCLASS = "Constructors from superclass";

	public final String CONTACT_ALL_UPDATE_SITES_DURING_SITES_DURING_INSTALL_TO_FIND_REQUIRED_SOFTWARE =
		"Contact all update sites during install to find required software";

	public final String CONTENT_FOLDER = "Content folder:";

	public final String CONTENT_TARGETING_REPORT = "content-targeting-report";

	public final String CONTENT_TARGETING_RULE = "content-targeting-rule";

	public final String CONTENT_TARGETING_TRACKING_ACTION = "content-targeting-tracking-action";

	public final String CONTINUE = "Continue";

	public final String CONTROL_MENU_ENTRY = "control-menu-entry";

	public final String CREATE = "Create";

	public final String CREATE_A_JAVA_PROJECT = "Create a Java project";

	public final String CREATE_A_JAVA_PROJECT_IN_WORKSPACE =
		"Create a Java project in the workspace or in an external location.";

	public final String CREATE_A_NEW_LIFERAY_MODULE_PROJECT = "Create a new Liferay Module Project";

	public final String CREATE_A_NEW_PROJECT_RESOURCE = "Create a new project resource";

	public final String CREATE_ENTRY_CLASS = "Create Entry Class";

	public final String CREATE_JSP_FILES = "Create JSP files";

	public final String CREATE_NEW_LIFERAY_WORKSPACE = "Create New Liferay Workspace [Required]";

	public final String CREATE_NEW_PORTLET = "Create new portlet";

	public final String CREATE_RESOURCE_BUNDLE_FILE = "Create resource bundle file";

	public final String CREATE_VIRW_FILES = "Create view files";

	public final String CSS = "CSS:";

	public final String CSS_CLASS_WRAPPER = "CSS class wrapper:";

	public final String CURRENT_LIFERAY_VERSION = "Current Liferay Version:";

	public final String CUSTOM_JSP_FOLDER = "Custom JSP folder:";

	public final String CUSTOM_JSPS = "Custom JSPs";

	public final String DEBUG = "Debug";

	public final String DEFINE_ACTIONS_ON_PORTAL_EVENTS = "Define actions to be executed on portal events:";

	public final String DEFINE_PORTAL_SERVICES_TO_EXTEND = "Define portal services to extend:";

	public final String DELETE = "Delete";

	public final String DELETE_ALL_EVENTS = "Delete All Events";

	public final String DELETE_FROM_DISK = "Delete project contents on disk (cannot be undone)";

	public final String DELETE_LOG = "Delete Log";

	public final String DELETE_RESOURCES = "Delete Resources";

	public final String DELETE_SERVER = "Delete Server";

	public final String DESELECT_ALL = "Deselect All";

	public final String DESIGN = "Design";

	public final String DESTROY = "destroy";

	public final String DESTROYING_PROTOCALHANDLER = "Destroying ProtocolHandler";

	public final String DETECTED_PORTAL_BUNDLE_TYPE = "Detected portal bundle type";

	public final String DIAGRAM = "Diagram";

	public final String DISABLE_JSP_SYNTAX = "Disable JSP syntax validation for custom JSP folder (recommended).";

	public final String DISPLAY_CATEGORY = "Display Category:";

	public final String DISPLAY_NAME = "Display name:";

	public final String DOABOUT = "doAbout";

	public final String DOCONFIG = "doConfig";

	public final String DOEDIT = "doEdit";

	public final String DOEDITDEFAULTS = "doEditDefaults";

	public final String DOEDITGUEST = "doEditGuest";

	public final String DOHELP = "doHelp";

	public final String DOPREVIEW = "doPreview";

	public final String DOPRINT = "doPrint";

	public final String DOVIEW = "doView";

	public final String DOWNLOAD_LIFERAY_BUNDLE = "Download Liferay bundle";

	public final String EDIT = "Edit";

	public final String EDIT_DEFAULTS = "Edit Defaults";

	public final String EDIT_GUEST = "Edit Guest";

	public final String EDIT_WITH_DOT = "Edit...";

	public final String EMAIL_ADDRESS = "Email address:";

	public final String ENTRY_CATEGORY = "Entry Category:";

	public final String ENTRY_CLASS = "Entry Class:";

	public final String ENTRY_WEIGHT = "Entry Weight:";

	public final String ERROR_LOG = "Error Log";

	public final String EVENT = "Event:";

	public final String EXPAND_ALL = "Expand All";

	public final String EXT = "Ext";

	public final String FARMEWORK_TYPE = "Theme framework:";

	public final String FILE = "File";

	public final String FINAL = "final";

	public final String FIND_LIFERAY_7_BREAKING_API_CHANGES = "Find Liferay 7 breaking API changes...";

	public final String FINDING_MIGRATION_PROBLEMS = "Finding migration problems...";

	public final String FINISH = "Finish";

	public final String FOLDER_NAME = "Folder name:";

	public final String FORM_FIELD = "form-field";

	public final String FREEMARKER = "Freemarker";

	public final String FREEMARKER_PORTLET = "freemarker-portlet";

	public final String FRIENDLY_URL_MAPPER = "Friendly URL Mapper";

	public final String GOGO_COMMAND = "GOGO Command";

	public final String GRADLE = "Gradle";

	public final String GRADLE_LIFERAY_WORKSPACE = "gradle-liferay-workspace";

	public final String HELP = "Help";

	public final String HOOK = "Hook";

	public final String HOOK_PLUGIN_PROJECT = "Hook plugin project:";

	public final String HOST_OSGI_BUNDLE = "Host OSGi Bundle:";

	public final String I_ACCEPT_THE_TERMS_OF_THE_LICENSE_AGREEMENTS = "I accept the terms of the license agreements";

	public final String ICEFACES = "ICEFaces";

	public final String ICON = "Icon:";

	public final String ID = "Id:";

	public final String IF_SERVER_IS_STARTED_PLUBLISH_CHANGES_IMMEDIATELY =
		"If server is started, publish changes immediately";

	public final String IGNORE_OPTIONAL_COMPILE_PROBLEMS = "Ignore optional compile problems";

	public final String IMPL_CLASS = "Impl Class:";

	public final String IMPORT = "Import...";

	public final String IMPORT_EXISTING_LIFERAY_WORKSPACE = "Import Existing Liferay Workspace [Required]";

	public final String IMPORT_LIFERAY_MODULE_PROJECT = "Import Liferay Module Project or Multi-Module Projects";

	public final String IMPORT_LIFERAY_WORKSPACE = "Import Liferay Workspace";

	public final String INCLUDE_SAMPLE_CODE = "Include sample code";

	public final String INCLUDE_SAMPLE_ENTITY_IN_NEW_FILE = "Include sample entity in new file.";

	public final String INDEXER_POST_PROCESSOR = "Indexer Post Processor";

	public final String INHERITED_ABSTRACT_METHODS = "Inherited abstract methods";

	public final String INIT = "init";

	public final String INIT_BUNDLE = "init-bundle";

	public final String INITIALIZE_SERVER_BUNDLE = "Initialize Server Bundle";

	public final String INSTALL_NEW_SOFTWARE = "Install New Software...";

	public final String INSTALL_UPDATE = "Install/Update";

	public final String INTERFACES = "Interfaces:";

	public final String JAVA_PACKAGE = "Java package:";

	public final String JAVA_PROJECT = "Java Project";

	public final String JAVASCRIPT = "JavaScript:";

	public final String JAVAX_PORTLET_FACES_GENERICFACESPORTLET = "javax.portlet.faces.GenericFacesPortlet";

	public final String JSF_2_X = "JSF 2.x";

	public final String JSF_STANDARD = "JSF Standard";

	public final String JSP = "JSP";

	public final String JSP_FILE_PATH = "JSP File Path";

	public final String JSP_FILES_TO_OVERRIDE = "JSP files to override";

	public final String JSP_FOLDER = "JSP folder:";

	public final String KALEO_DESIGNER = "Kaleo Designer";

	public final String KALEO_WORKFLOWS = "Kaleo Workflows";

	public final String LANGUAGE_PROPERTIES = "Language properties";

	public final String LANGUAGE_PROPERTY_FILES = "Language property files:";

	public final String LAUNCH_NEW_PORTLET_WIZARD_AFTER_PROJECT = "Launch New Portlet Wizard after project is created";

	public final String LAYOUT_PLUGIN_PROJECT = "Layout plugin project:";

	public final String LAYOUT_TEMPLATE = "layout-template";

	public final String LAYOUT_TEMPLATE_UPCASE = "Layout Template";

	public final String LIFERAY = "Liferay";

	public final String LIFERAY_7_MIGRATION_PROBLEMS = "Liferay 7 Migration Problems";

	public final String LIFERAY_7_X = "Liferay 7.x";

	public final String LIFERAY_COMPONENT_CLASS = "Liferay Component Class";

	public final String LIFERAY_FACES_ALLOY = "Liferay Faces Alloy";

	public final String LIFERAY_HOOK_CONFIGURATION = "Liferay Hook Configuration";

	public final String LIFERAY_IDE_STABLE_RELEASES = "Liferay IDE Stable releases";

	public final String LIFERAY_INC = "Liferay, Inc.";

	public final String LIFERAY_JSF_PORTLET = "Liferay JSF Portlet";

	public final String LIFERAY_LAYOUT_TEMPLATE = "Liferay Layout Template";

	public final String LIFERAY_MODULE_FRAGMENT_FILES = "Liferay Module Fragment Files";

	public final String LIFERAY_MODULE_PROJECT = "Liferay Module Project";

	public final String LIFERAY_MODULE_PROJECTS = "Liferay Module Project(s)";

	public final String LIFERAY_MVC = "Liferay MVC";

	public final String LIFERAY_PLUGIN_PROJECT = "Liferay Plugin Project";

	public final String LIFERAY_PLUGINS = "Liferay Plugins (Legacy)";

	public final String LIFERAY_PORTAL_BUNDLE = "Liferay Community Edition Portal 7.0.4 GA5";

	public final String LIFERAY_PORTAL_BUNDLE_DIRECTORY = "Liferay Portal Bundle Directory";

	public final String LIFERAY_PORTLET = "Liferay Portlet";

	public final String LIFERAY_PROJECT_FROM_EXISTING_SOURCE = "Liferay Project from Existing Source";

	public final String LIFERAY_RUNTIME_NAME = "Liferay runtime name:";

	public final String LIFERAY_SERVICE_BUILDER = "Liferay Service Builder";

	public final String LIFERAY_TOMCAT_DIRECTORY = "Liferay Tomcat directory";

	public final String LIFERAY_UPGRADE_PLAN = "Liferay Upgrade Plan";

	public final String LIFERAY_UPGRADE_PLAN_INFO = "Liferay Upgrade Plan Info";

	public final String LIFERAY_UPGRADE_PLANNER = "Liferay Upgrade Planner";

	public final String LIFERAY_V_62_SERVER_TOMCAT_7 = "Liferay v6.2 Server (Tomcat 7)";

	public final String LIFERAY_V_62_TOMCAT_7 = "Liferay v6.2 (Tomcat 7)";

	public final String LIFERAY_VAADIN_PORTLET = "Liferay Vaadin Portlet";

	public final String LIFERAY_VERSION = "Liferay version:";

	public final String LIFERAY_WORKSPACE = "Liferay Workspace";

	public final String LIFERAY_WORKSPACE_PROJECT = "Liferay Workspace Project";

	public final String LOCATION = "Location";

	public final String LOCATION_WITH_COLON = "Location:";

	public final String LOGIN_PRE_ACTION = "Login Pre Action";

	public final String MAVEN = "Maven";

	public final String MAVEN_LIFERAY_WORKSPACE = "maven-liferay-workspace";

	public final String MODEL_CLASS = "Model Class:";

	public final String MODEL_LISTENER = "Model Listener";

	public final String MOVE_DOWN = "Move Down";

	public final String MOVE_UP = "Move Up";

	public final String MVC_PORTLET = "mvc-portlet";

	public final String MVC_PORTLET_UPCASE = "MVC Portlet";

	public final String NAME = "Name";

	public final String NAME_WITH_COLON = "Name:";

	public final String NAMESPACE = "Namespace:";

	public final String NEW = "New";

	public final String NEW_LAYOUT_TEMPLATE = "New Layout Template";

	public final String NEW_LIFERAY_COMPONENT = "New Liferay Component";

	public final String NEW_LIFERAY_COMPONENT_CLASS = "New Liferay Component Class";

	public final String NEW_LIFERAY_HOOK = "New Liferay Hook";

	public final String NEW_LIFERAY_HOOK_CONFIGURATION = "New Liferay Hook Configuration (Legacy)";

	public final String NEW_LIFERAY_JSF_PORTLET = "New Liferay JSF Portlet (Legacy)";

	public final String NEW_LIFERAY_JSF_PROJECT = "New Liferay JSF Project";

	public final String NEW_LIFERAY_KALEO_WORKFLOW = "New Liferay Kaleo Workflow";

	public final String NEW_LIFERAY_LAYOUT_TMEPLATE = "New Liferay Layout Template (Legacy)";

	public final String NEW_LIFERAY_MODULE_EXT_PROJECT = "New Liferay Module Ext Project";

	public final String NEW_LIFERAY_MODULE_PROJECT = "New Liferay Module Project";

	public final String NEW_LIFERAY_MODULE_PROJECT_FRAGMENT = "New Liferay Module Project Fragment";

	public final String NEW_LIFERAY_PLUGIN_PROJECT = "New Liferay Plugin Project (Legacy)";

	public final String NEW_LIFERAY_PLUGIN_PROJECTS_FROM_EXISING_SOURCE =
		"New Liferay Plugin Projects from Existing Source (Legacy)";

	public final String NEW_LIFERAY_PORTLET = "New Liferay Portlet (Legacy)";

	public final String NEW_LIFERAY_RUNTIME = "New Liferay Runtime...";

	public final String NEW_LIFERAY_SERVER = "New Liferay Server";

	public final String NEW_LIFERAY_SERVICE_BUILDER = "New Liferay Service Builder (Legacy)";

	public final String NEW_LIFERAY_VAADIN_PORTLET = "New Liferay Vaadin Portlet (Legacy)";

	public final String NEW_LIFERAY_WORPSPACE_PROJECT = "New Liferay Workspace Project";

	public final String NEW_SERVER = "New Server";

	public final String NEW_SERVER_RUNTIME_ENVIRONMENT = "New Server Runtime Environmet";

	public final String NEW_SERVICE_BUILDER = "New Service Builder";

	public final String NEW_UPGRADE_PLAN = "New Upgrade Plan";

	public final String NEW_WITH_DOT = "New...";

	public final String NEXT_WITH_BRACKET = "Next >";

	public final String NO = "No";

	public final String NO_OPERTAIONS = "No operations to display at this time.";

	public final String NPM_ANGULAR_PORTLET = "npm-angular-portlet";

	public final String NPM_REACT_PORTLET = "npm-react-portlet";

	public final String NPM_VUEJS_PORTLET = "npm-vuejs-portlet";

	public final String OK = "OK";

	public final String OOMPH = "Oomph";

	public final String OPEN = "Open";

	public final String OPEN_LIFERAY_HOME_FOLDER = "Open Liferay Home Folder";

	public final String OPEN_LIFERAY_PORTAL_HOME = "Open Liferay Portal Home";

	public final String OTHER = "Other...";

	public final String OVERRIDDEN_FILES = "Overridden files:";

	public final String OVERVIEW = "Overview";

	public final String PACKAGE = "Package";

	public final String PACKAGE_EXPLORER = "Package Explorer";

	public final String PACKAGE_NAME = "Package name:";

	public final String PACKAGE_PATH = "Package path:";

	public final String PANEL_APP = "panel-app";

	public final String PLEASE_SELECT_A_PROPERTY = "Please select a property:";

	public final String PLUGIN_PROJECT = "Plugin project:";

	public final String PLUGIN_TYPE = "Plugin type:";

	public final String POLLER_PROCESSOR = "Poller Processor";

	public final String POM_XML = "pom.xml";

	public final String PORTAL_PROPERTIES = "Portal properties";

	public final String PORTAL_PROPERTIES_FILE = "Portal properties file:";

	public final String PORTLET = "portlet";

	public final String PORTLET_ACTION_COMMAND = "Portlet Action Command";

	public final String PORTLET_CLASS = "Portlet class:";

	public final String PORTLET_CONFIGURATION_ICON = "portlet-configuration-icon";

	public final String PORTLET_FILTER = "Portlet Filter";

	public final String PORTLET_NAME = "Portlet name:";

	public final String PORTLET_PLUGIN_PROJECT = "Portlet plugin project:";

	public final String PORTLET_PROVIDER = "portlet-provider";

	public final String PORTLET_TOOLBAR_CONTRIBUTOR = "portlet-toolbar-contributor";

	public final String PORTLET_UPCASE = "Portlet";

	public final String PREFERENCE_RECORDER = "Preference Recorder";

	public final String PREFERENCES = "Preferences";

	public final String PREVIEW = "Preview";

	public final String PRIMEFACES = "PrimeFaces";

	public final String PRINT = "Print";

	public final String PROCESSACTION = "processAction";

	public final String PROGRESS = "Progress";

	public final String PROJECT = "Project";

	public final String PROJECT_EXPLORER = "Project Explorer";

	public final String PROJECT_NAME = "Project name:";

	public final String PROJECT_TEMPLATE_NAME = "Project Template Name:";

	public final String PROJECT_WITH_DOT = "Project...";

	public final String PROPERTIES = "Properties:";

	public final String PROPERTY = "Property:";

	public final String PUBLIC = "public";

	public final String RECORD_INTO = "Record into:";

	public final String REFRESH = "Refresh";

	public final String REMOVE = "Remove";

	public final String REMOVE_ALL = "<< Remove All";

	public final String REMOVE_PROJECT = "< Remove";

	public final String REMOVE_WITH_DOT = "Remove...";

	public final String RESOURCE_BUNDLE_FILE_PATH = "Resource bundle file path:";

	public final String REST = "rest";

	public final String REST_UPCASE = "Rest";

	public final String RESTART_UPGRADE_PLAN = "Restart Upgrade Plan";

	public final String RICHFACES = "RichFaces";

	public final String ROLE_ID = "Role-id:";

	public final String RUNTIME_ENVIRONMENTS = "Runtime Environments";

	public final String SAVE = "Save (Ctrl+S)";

	public final String SCREEN_NAME = "Screen name:";

	public final String SCRIPT_LANGUAGE = "Script language:";

	public final String SDK = "SDK";

	public final String SDK_DIRECTORY = "SDK Directory:";

	public final String SDK_LOCATION = "SDK Location:";

	public final String SDK_VERSION = "SDK Version:";

	public final String SELECT = "Select...";

	public final String SELECT_A_JSP_TO_CUSTOMIZE = "Select a JSP to customize:";

	public final String SELECT_ALL = "Select All";

	public final String SELECT_AN_EVENT_ACTION = "Select an event action:";

	public final String SELECT_HOST_OSGI_BUNDLE = "Select Host OSGi Bundle:";

	public final String SELECT_MAVEN_PROFILES = "Select Maven Profiles...";

	public final String SELECT_MODEL_CLASS = "Select Model Class:";

	public final String SELECT_SERVICE_NAME = "Select Service Name:";

	public final String SELECTED_PROJECT = "Selected project:";

	public final String SERVER = "Server";

	public final String SERVER_NAME = "Server name:";

	public final String SERVER_RUNTIEME_ENVIRONMENTS = "Server runtime environments:";

	public final String SERVER_STARTUP_IN = "Server startup in";

	public final String SERVERESOURCE = "serveResource";

	public final String SERVERS = "Servers";

	public final String SERVERS_HOST_NAME = "Server's host name:";

	public final String SERVICE = "service";

	public final String SERVICE_BUILDER = "service-builder";

	public final String SERVICE_BUILDER_DTD_VERSION = "7.0.0";

	public final String SERVICE_BUILDER_PORTLET = "Service Builder Portlet";

	public final String SERVICE_FILE = "Service file:";

	public final String SERVICE_NAME = "Service Name:";

	public final String SERVICE_TYPE = "Service Type:";

	public final String SERVICE_WRAPPER = "service-wrapper";

	public final String SERVICE_WRAPPER_UPCASE = "Service Wrapper";

	public final String SERVICE_XML = "service.xml";

	public final String SERVICES = "Services";

	public final String SET_UP_LIFERAY_WORKSPACE = "Set Up Liferay Workspace";

	public final String SETUP_TASKS = "Setup Tasks";

	public final String SHOW_PROGRESS_VIEW = "Show Progress View";

	public final String SHOW_UPGRADE_PLAN_INFO_VIEW = "Show Upgrade Plan Info View";

	public final String SHOW_VIEW = "Show View";

	public final String SIMULATION_PANEL_ENTRY = "simulation-panel-entry";

	public final String SKIP = "Skip";

	public final String SOCIAL_BOOKMARK = "social-bookmark";

	public final String SOURCE = "Source";

	public final String SOURCE_FOLDER = "Source folder:";

	public final String SOY_PORTLET = "soy-portlet";

	public final String SPECIFY_PROPERTIES_TO_OVERRIDE = "Specify properties to override:";

	public final String SPRING_MVC = "Spring MVC";

	public final String SPRING_MVC_PORTLET = "spring-mvc-portlet";

	public final String STANDARD_JSF = "Standard JSF";

	public final String START = "Start";

	public final String START_THE_SERVER_IN_DEBUG_MODE_WITH_KEY = "Start the server in debug mode (Ctrl+Alt+D)";

	public final String START_THE_SERVER_IN_DEBUG_MODE_WITH_KEY_MAC = "Start the server in debug mode (⌥⌘D)";

	public final String START_THE_SERVER_WITH_KEYS = "Start the server (Ctrl+Alt+R)";

	public final String START_THE_SERVER_WITH_KEYS_MAC = "Start the server (⌥⌘R)";

	public final String STOP = "Stop";

	public final String STOP_THE_SERVER_WITH_KEYS = "Stop the server (Ctrl+Alt+S)";

	public final String STOP_THE_SERVER_WITH_KEYS_MAC = "Stop the server (⌥⌘S)";

	public final String STRUTS_IN_ACTION = "Struts In Action";

	public final String STRUTS_PORTLET_ACTION = "Struts Portlet Action";

	public final String STYLED = "_styled";

	public final String SUPERCLASS = "Superclass:";

	public final String SWITCH_UPGRADE_PLAN = "Switch Upgrade Plan";

	public final String TARGET_LIFERAY_VERSION = "Target Liferay Version:";

	public final String TARGET_PLATFORM = "Target platform:";

	public final String TEMPLATE_CONTEXT_CONCONTRIBUTOR = "template-context-contributor";

	public final String TEMPLATE_FILE = "Template file:";

	public final String TEMPLATE_FILE_SELECTION = "Template file selection";

	public final String TEST = "test";

	public final String TEST_JASON_WEB_SERVICE = "Test JSON Web Services";

	public final String TEST_LIFERAY_WEB_SERVICE = "Test Liferay Web Services";

	public final String THEME = "theme";

	public final String THEME_CONTRIBUTOR = "theme-contributor";

	public final String THEME_PARENT = "Theme parent:";

	public final String THEME_UPCASE = "Theme";

	public final String THUMBNAIL_FILE = "Thumbnail file:";

	public final String THUMBNAIL_FILE_SELECTION = "Thumbnail file selection";

	public final String TITLE = "Title:";

	public final String TYPE = "Type";

	public final String UNSTYLED = "_unstyled";

	public final String UPDATE_EXCLUSION_FILTERS = "Update exclusion filters in other source folders to solve nesting";

	public final String UPDATE_PROJECT = "Update Project...";

	public final String UPGRADE_CODE_OUTLINE =
		"http://192.168.133.135:8080/web/guest/docs/7-2/reference/-/knowledge_base/reference" +
			"/upgrading-code-to-product-ver";

	public final String UPGRADE_PLAN_DETAILS = "Upgrade Plan Details";

	public final String UPGRADE_PLAN_OUTLINE = "Upgrade plan outline:";

	public final String UPGRADE_PLANNER = "Upgrade Planner";

	public final String UPGRADE_TO_LIFERAY_PLUGINS_SDK_7 = "Upgrade to Liferay Plugins SDK 7";

	public final String UPGRADE_TO_LIFERAY_WORKSPACE = "Upgrade to Liferay Workspace";

	public final String UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT = "Upgrade Your Development Environment";

	public final String UPLOAD_NEW_WORKFLOW = "Upload new workflow...";

	public final String USE_DEFAULT_LOCATION = "Use default location";

	public final String USE_DEFAULT_PORTLET_MVCPORTLET = "Use default portlet (MVCPortlet)";

	public final String USER_ID = "User-id:";

	public final String VAADIN = "Vaadin";

	public final String VALUE = "Value";

	public final String VALUE_WITH_COLON = "Value:";

	public final String VELOCITY = "Velocity";

	public final String VIEW = "View";

	public final String VIEW_FOLDER = "View folder:";

	public final String WAP_TEMPLATE_FILE = "WAP template file:";

	public final String WAP_TEMPLATE_FILE_SELECTION = "WAP template file selection";

	public final String WAR = "war";

	public final String WAR_CORE_EXT = "war-core-ext";

	public final String WAR_HOOK = "war-hook";

	public final String WAR_MVC_PORTLET = "war-mvc-portlet";

	public final String WATCH = "watch";

	public final String WEB = "Web";

	public final String WEB_ROOT_FOLDER = "Web root folder:";

	public final String WELCOME = "Welcome";

	public final String WINDOW = "Window";

	public final String WORKING_SET = "Working set:";

	public final String WORKSPACE = "Workspace:";

	public final String WORKSPACE_FILE = "workspace file";

	public final String WORKSPACE_LOCATION = "Workspace location:";

	public final String WORKSPACE_NAME = "Workspace name:";

	public final String YES = "Yes";

}