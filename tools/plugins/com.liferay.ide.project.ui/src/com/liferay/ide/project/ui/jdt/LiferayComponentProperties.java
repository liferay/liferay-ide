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

package com.liferay.ide.project.ui.jdt;

/**
 * @author Simon Jiang
 */
public class LiferayComponentProperties {

	public static final String[][] CODE_ASSISTANT_RESOURCE = {
		{
			"com.liferay.portlet.friendly-url-routes", "String",
			"The friendly-url-routes points to the XML file that defines the friendly URL<br>routes.This file is " +
				"read by the class loader"
		},
		{"javax.portlet.portlet.info.title", "", ""}, {"javax.portlet.portlet.info.short-title", "", ""},
		{"ddm.form.field.type.js.module", "", ""},
		{
			"com.liferay.portlet.action-timeout", "Int",
			"The default value of action-timeout is 0. If set to a value greater than 0,<br>and monitoring-spring." +
				"xml is enabled via the property 'spring.configs' in<br>portal.properties, and the property " +
					"'monitoring.portlet.action.request' in<br>portal.properties is set to true, then the portlet's " +
						"action phase processing<br>will be timed. If the execution time is longer than action-" +
							"timeout,it will be<br>recorded as a timeout request processing. The time unit is " +
								"millisecond."
		},
		{"javax.portlet.init-param.single-page-application-cacheable", "", ""},
		{
			"com.liferay.portlet.scopeable", "String",
			"If the scopeable is set to true, an administrator will be able to configure the<br>scope of the data of " +
				"the portlet to either the current community (default), <br>the current layout, or the scope of any " +
					"other layout of the community that<br>already exists. Portlets that want to support this must " +
						"be programmed to obtain<br>the proper scope group id according to the configuration and " +
							"scope their data<br>accordingly. The default is false."
		},
		{"osgi.http.whiteboard.filter.name", "", ""}, {"path", "", ""},
		{
			"com.liferay.portlet.control-panel-entry-category", "String",
			"Set the control-panel-entry-category value to 'my' to make this portlet<br>available within the My " +
				"Account administration of the user. Set the value to<br>'apps', 'configuration', 'sites', or " +
					"'users' to make it available in the Control<br>Panel under that category. Set the value to " +
						"'site_administration.configuration', <br>'site_administration.content', 'site_administr" +
							"ation.pages' or<br>'site_administration.users' to make it available in the Site Adm" +
								"inistration<br>under that category. Legacy values from previous versions of Liferay " +
									"will be<br>automatically mapped to the new values: 'content' to<br>'" +
										"site_administration.content', 'portal' to 'users', and 'server' to 'apps'."
		},
		{"/common/referer_jsp.jsp", "", ""}, {"xml.external.general.entities.allowed", "", ""},
		{"com.liferay.portlet.icon", "String", "The icon element specifies an image that represents the portlet"},
		{"portlet.login.login", "", ""},
		{
			"com.liferay.portlet.facebook-integration", "String",
			"Set the facebook-integration value to either 'fbml' or 'iframe'. The default<br>value is 'iframe' " +
				"because IFrame integration will work without requiring any<br>changes to your code. See the Message " +
					"Boards portlet for minor changes that were<br>made to make it FBML compliant. Note that the " +
						"Liferay tag libraries already<br>output FBML automatically if a request is made by Facebook."
		},
		{"form.navigator.entry.order:Integer", "", ""}, {"search.engine.impl", "", ""}, {"tagClassName", "", ""},
		{"open.id.ax.type.email[yahoo]", "", ""}, {"operation.mode", "", ""},
		{"open.id.ax.type.fullname[yahoo]", "", ""}, {"javax.portlet.init-param.view-template", "", ""},
		{
			"com.liferay.portlet.maximize-edit", "Boolean",
			"Set the maximize-edit value to true if the portlet goes into the maximized state<br>when the user goes " +
				"into the edit mode. This only affects the default portal<br>icons and not what may be " +
					"programmatically set by the portlet developer.<br>The default value is false."
		},
		{
			"com.liferay.portlet.footer-portlet-javascript", "String",
			"Set the path of JavaScript that will be referenced in the page's footer relative<br>to the portlet's " +
				"context path."
		},
		{"com.liferay.frontend.image.editor.capability.controls", "", ""},
		{"osgi.http.whiteboard.filter.pattern", "", ""}, {"portal.settings.authentication.tabs.name", "", ""},
		{"layout.type", "", ""}, {"repository.target.class.name", "", ""},
		{"javax.portlet.init-param.parserClassName", "", ""},
		{
			"com.liferay.portlet.css-class-wrapper", "String",
			"Set name of the CSS class that will be injected in the DIV that wraps this<br>portlet."
		},
		{"javax.portlet.init-param.clear-request-parameters", "", ""},
		{"javax.portlet.init-param.helpStylesheet", "", ""},
		{
			"com.liferay.portlet.parent-struts-path", "String",
			"The parent-struts-path must be the struts-path of another portlet in the same<br>web application. The " +
				"current portlet will be able to use all the struts<br>mappings of the parent without dupli" +
					"cating them in struts-config.xml"
		},
		{"key", "", ""},
		{
			"javax.portlet.expiration-cache", "Int",
			"Expiration-time defines the time in seconds after which the portlet output expires.<br>-1 indicates " +
				"that the output never expires.<br> Used in: portlet"
		},
		{"com.liferay.frontend.image.editor.capability.type", "", ""}, {"buffered.execution.mode", "", ""},
		{"javax.portlet.init-param.mvc-command-names-default-views", "", ""}, {"index.on.startup", "", ""},
		{"store.type", "", ""}, {"filter.init.filter-class", "", ""},
		{
			"com.liferay.portlet.footer-portlet-css", "String",
			"Set the path of CSS that will be referenced in the page's footer relative to the<br>portlet's context " +
				"path."
		},
		{"assignee.class.name", "", ""}, {"templatePath", "", ""}, {"editor.name", "", ""},
		{"osgi.http.whiteboard.servlet.pattern", "", ""}, {"recipient.type", "", ""},
		{"auth.token.ignore.mvc.action", "", ""}, {"com.liferay.portlet.single-page-application", "Boolean", ""},
		{"server.manager.executor.path", "", ""},
		{"filter.init.auth.verifier.PortalSessionAuthVerifier.urls.includes", "", ""},
		{"javax.portlet.portlet-info.keywords", "", ""}, {"verify.process.name", "", ""},
		{"jmx.objectname.cache.key", "", ""}, {"javax.portlet.init-param.servletName", "", ""},
		{"mvc.command.name", "", ""}, {"search.engine.id", "", ""}, {"javax.portlet.name", "", ""},
		{"com.liferay.frontend.image.editor.capability.category", "", ""}, {"fromName", "", ""},
		{
			"com.liferay.portlet.use-default-template", "Boolean",
			"Set the use-default-template value to true if the portlet uses the default<br>template to decorate and " +
				"wrap content. Setting this to false allows the<br>developer to own and maintain the portlet's " +
					"entire outputted content. The<br>default value is true.<br>The most common use of this is if " +
						"you want the portlet to look different from<br>the other portlets or if you want the " +
							"portlet to not have borders around the<br>outputted content."
		},
		{"form.navigator.category.order:Integer", "", ""},
		{
			"com.liferay.portlet.friendly-url-mapping", "String",
			"The friendly-url-mapping specifies the mapping used to map a friendly URL prefix<br>to a specific " +
				"portlet. For example, the Message Boards portlet has the mapping<br> message_boards and will map " +
					"all friendly URL's that contains<br>/-/message_boards to itself."
		},
		{"item.selector.view.order:Integer", "", ""}, {"add.template.action.id", "", ""},
		{
			"javax.portlet.portlet-mode", "",
			"Portlet modes. The specification pre-defines the following values <br>as valid portlet mode constants " +
				"<br>'edit', 'help', 'view'<br>Portlet mode names are not case sensitive.<br>Used in: custom-portl" +
					"et-mode, supports"
		},
		{
			"com.liferay.portlet.restore-current-view", "Boolean",
			"Set the restore-current-view value to true if the portlet restores to the<br>current view when toggling " +
				"between maximized and normal states. If set to false,<br>the portlet will reset the current view " +
					"when toggling between maximized and<br>normal states. The default value is true."
		},
		{"open.id.url[yahoo]", "", ""}, {"indexer.class.name", "", ""},
		{"javax.portlet.init-param.errorStylesheet", "", ""}, {"language.type", "", ""},
		{
			"javax.portlet.supported-publishing-event", "",
			"The event-definition-referenceType is used to reference events <br>declared with the event-definition " +
				"element at application level.<br> Used in: portlet"
		},
		{
			"com.liferay.portlet.pop-up-print", "Boolean",
			"Set the pop-up-print value to true if the portlet goes into the pop up state<br>when the user goes into " +
				"the print mode. This only affects the default portal<br>icons and not what may be programmatically " +
					"set by the portlet developer.<br>The default value is true."
		},
		{"portlet.add.default.resource.check.whitelist.mvc.action", "", ""},
		{
			"com.liferay.portlet.preferences-unique-per-layout", "Boolean",
			"Set the preferences-unique-per-layout value to true if the preferences for the<br>portlet are unique " +
				"across all pages. If set to false, the preferences for the<br>portlet are shared across all pages. " +
					"The default value is true.<br>The preferences-unique-per-layout element is used in combination " +
						"with the<br>preferences-owned-by-group element. See the comments for the<br>preferences-ow" +
							"ned-by-group element for more information."
		},
		{"javax.portlet.description", "String", ""}, {"url-pattern", "", ""},
		{
			"com.liferay.portlet.header-portal-css", "String",
			"Set the path of CSS that will be referenced in the page's header relative to the<br>portal's context path."
		},
		{"node.type", "", ""}, {"name", "", ""},
		{
			"com.liferay.portlet.requires-namespaced-parameters", "Boolean",
			"Set the requires-namespaced-parameters value to true if the portlet will only<br>process namespaced " +
				"parameters. The default value is true."
		},
		{"mvc.render.command.name", "", ""},
		{
			"com.liferay.portlet.header-portlet-javascript", "String",
			"Set the path of JavaScript that will be referenced in the page's header relative<br>to the portlet's " +
				"context path."
		},
		{"page", "", ""},
		{
			"javax.portlet.display-name", "String",
			"The display-name type contains a short name that is intended <br>to be displayed by tools. It is used " +
				"by display-name <br>elements.  The display name need not be unique."
		},
		{"ddm.form.field.type.display.order:Integer", "", ""}, {"class.name", "", ""},
		{"default.model.resource.name", "", ""}, {"model.class.name", "", ""},
		{
			"com.liferay.portlet.system", "String",
			"Set the system value to true if the portlet is a system portlet that a user<br>cannot manually add to " +
				"their page. The default value is false."
		},
		{
			"com.liferay.portlet.footer-portal-css", "String",
			"Set the path of CSS that will be referenced in the page's footer relative to the<br>portal's context path."
		},
		{"javax.portlet.init-param.template-path", "", ""}, {"javax.portlet.init-param.refererWebDAVToken", "", ""},
		{"panel.category.order:Integer", "", ""}, {"javax.portlet.init-param.always-send-redirect", "", ""},
		{"open.id.ax.type.lastname[default]", "", ""},
		{"javax.portlet.preferences", "String", "Portlet persistent preference store.Used in: portlet"},
		{"service.ranking:Integer", "", ""}, {"panel.app.order:Integer", "", ""}, {"webdav.storage.token", "", ""},
		{"timeout", "", ""}, {"osgi.command.function", "", ""},
		{
			"javax.portlet.init-param", "",
			"The init-param element contains the name the attribute.<br>This name must be unique within the portlet."
		},
		{"com.liferay.portal.workflow.kaleo.runtime.action.executor.language", "", ""}, {"osgi.command.scope", "", ""},
		{"javax.portlet.portlet-info.short-title", "", ""}, {"requestTimeout", "", ""},
		{"javax.portlet.init-param.edit-template", "", ""}, {"ddm.form.field.type.icon", "", ""},
		{"javax.portlet.init-param.mvc-action-command-package-prefix", "", ""},
		{"javax.portlet.portlet-info.title", "", ""},
		{"javax.portlet.init-param.always-display-default-configuration-icons", "", ""},
		{"portal.cache.manager.name", "", ""}, {"servlet-filter-name", "", ""}, {"sort.order", "", ""},
		{"javax.portlet.supports.mime-type", "", ""}, {"after-filter", "", ""}, {"full.text.proximity.slop", "", ""},
		{"javax.portlet.init-param.portlet-title-based-navigation", "", ""},
		{"javax.portlet.init-param.cssRegex", "", ""},
		{
			"com.liferay.portlet.instanceable", "String",
			"Set the instanceable value to true if the portlet can appear multiple times on a<br>page. If set to " +
				"false, the portlet can only appear once on a page. The default<br>value is false."
		},
		{"ddm.form.field.type.system", "", ""}, {"open.id.ax.schema[default]", "", ""},
		{"javax.portlet.init-param.view-action", "", ""},
		{
			"com.liferay.portlet.maximize-help", "Boolean",
			"Set the maximize-help value to true if the portlet goes into the maximized state<br>when the user goes " +
				"into the help mode. This only affects the default portal<br>icons and not what may be programmatic" +
					"ally set by the portlet developer.<br>The default value is false."
		},
		{"typeMappings.KeywordQueryDocumentType", "", ""}, {"template.language", "", ""},
		{"servlet.init.httpMethods", "", ""},
		{
			"com.liferay.portlet.show-portlet-access-denied", "Boolean",
			"Set the show-portlet-access-denied value to true if users are shown the portlet<br>with an access " +
				"denied message if they do not have access to the portlet. If set<br>to false, users are never shown " +
					"the portlet if they do not have access to the<br>portlet. The default value is set in " +
						"portal.properties."
		},
		{"javax.portlet.init-param.config-jsp", "", ""}, {"type", "", ""}, {"ddm.form.field.type.name", "", ""},
		{"mvc.path", "", ""},
		{
			"com.liferay.portlet.add-default-resource", "String",
			"If the add-default-resource value is set to false and the portlet does not<br>belong to the page but " +
				"has been dynamically added, then the user will not have<br>permissions to view the portlet. If the " +
					"add-default-resource value is set to<br>true, the default portlet resources and permissions are " +
						"added to the page, and<br>the user can then view the portlet. This is useful (and " +
							"necessary) for portlets<br>that need to be dynamically added to a page. However, to " +
								"prevent security  loop<br>holes, the default value is false.<br> The properties '" +
									"portlet.add.default.resource.check.enabled' and<br>  'portlet.add.default." +
										"resource.check.whitelist' in portal.properties  allow<br>security checks to " +
											"be configured around this behavior."
		},
		{"javax.portlet.init-param.add-process-action-success-action", "", ""},
		{
			"com.liferay.portlet.header-portlet-css", "String",
			"Set the path of CSS that will be referenced in the page's header relative to the<br>portlet's cont" +
				"ext path."
		},
		{"dispatcher", "", ""}, {"javax.portlet.keywords", "", ""}, {"cacheExpirationTime", "", ""},
		{
			"javax.portlet.window-state", "",
			"Portlet window state. Window state names are not case sensitive. <br>Used in: custom-window-state"
		},
		{
			"com.liferay.portlet.show-portlet-inactive", "Boolean",
			"Set the show-portlet-inactive value to true if users are shown the portlet <br>with an inactive message " +
				"if the portlet is inactive. If set to false, users are<br>never shown the portlet if the portlet is " +
					"inactive. The default value is set in<br>portal.properties."
		},
		{"javax.portlet.init-param.config-template", "", ""}, {"url-regex-ignore-pattern", "", ""},
		{
			"javax.portlet.PortletURLGenerationListener", "",
			"The listenerType is used to declare listeners for this portlet application. <br>Used in: portlet-app"
		},
		{"editor.config.key", "", ""},
		{
			"com.liferay.portlet.private-request-attributes", "Boolean",
			"Set the private-request-attributes value to true if the portlet does not share<br>request attributes " +
				"with the portal or any other portlet. The default value is<br>true. The property 'request.shared." +
					"attributes' in portal.properties specifies<br>which request attributes are shared even when the " +
						"private-request-attributes<br>value is true."
		},
		{"background.task.executor.class.name", "", ""},
		{
			"com.liferay.portlet.layout-cacheable", "Boolean",
			"Set the layout-cacheable flag to true if the data contained in this portlet can<br>will never change " +
				"unless the layout or Journal portlet entry is changed."
		},
		{"javax.portlet.security-role-ref.role-name", "", ""},
		{"com.liferay.frontend.image.editor.capability.icon", "", ""}, {"bundle.symbolic.name", "", ""},
		{
			"com.liferay.portlet.render-timeout", "Int",
			"The default value of render-timeout is 0. If set to a value greater than 0,<br>and monitoring-" +
				"spring.xml is enabled via the property 'spring.configs' in<br>portal.properties, and the property '" +
					"monitoring.portlet.render.request' in<br>portal.properties is set to true, then the portlet's " +
						"render phase processing<br>will be timed. If the execution time is longer than render-" +
							"timeout, it will be<br>recorded as a timeout request processing. The time unit is " +
								"millisecond."
		},
		{"distance.threshold", "", ""},
		{
			"com.liferay.portlet.ajaxable", "Boolean",
			"The default value of ajaxable is true. If set to false, then this portlet can<br>never be displayed via " +
				"Ajax."
		},
		{"xml.doctype.declaration.allowed", "", ""},
		{
			"com.liferay.portlet.virtual-pat", "String",
			"The virtual-path value sets the virtual path used to override the default <br>servlet context path"
		},
		{"portlet.preferences.translator.portlet.id", "", ""}, {"destination.name", "", ""},
		{"json.web.service.context.name", "", ""}, {"scripting.language", "", ""},
		{"javax.portlet.init-param.jsRegex", "", ""}, {"auth.public.path", "", ""},
		{"open.id.ax.type.firstname[default]", "", ""}, {"javax.portlet.init-param.edit-guest-template", "", ""},
		{"filter.init.auth.verifier.OAuthVerifier.urls.includes", "", ""},
		{"product.navigation.control.menu.category.order:Integer", "", ""}, {"ddm.form.field.type.label", "", ""},
		{"proxy.bean", "", ""},
		{
			"com.liferay.portlet.control-panel-entry-weight", "Double",
			"Set the control-panel-entry-weight value to a double number to control the<br> position of the entry " +
				"within its Control Panel category. Higher values mean <br>that the entry will appear lower in the " +
					"Control Panel menu"
		},
		{
			"com.liferay.portlet.preferences-company-wide", "Boolean",
			"Set the preferences-company-wide value to true if the preferences for the<br> portlet are across the " +
				"entire company. Setting this value to true means<br> the value for preferences-unique-per-layout " +
					"and preferences-owned-by-group are<br>not used. The default value is false."
		},
		{
			"javax.portlet.resource-bundle", "String",
			"Name of the resource bundle containing the language specific <br>portlet informations in different " +
				"languages (Filename without<br>the language specific part (e.g. _en) and the ending (.properties). " +
					"<br>Used in: portlet-info"
		},
		{
			"com.liferay.portlet.action-url-redirect", "Boolean",
			"Set the action-url-redirect value to true if an action URL for this portlet<br>should cause an auto " +
				"redirect. This helps prevent double submits. The default<br>value is false."
		},
		{"osgi.http.whiteboard.context.path", "", ""}, {"product.navigation.control.menu.entry.order:Integer", "", ""},
		{"javax.portlet.supported-public-render-parameter", "", ""}, {"typeMappings.SpellCheckDocumentType", "", ""},
		{
			"javax.portlet.security-role-ref", "",
			"The security-role-ref element contains the declaration of a <br> security role reference in the code of " +
				"the web application. The <br>declaration consists of an optional description, the security <br>role " +
					"name used in the code, and an optional link to a security <br>role. If the security role is not " +
						"specified, the Deployer must <br>choose an appropriate security role.<br>The value of the " +
							"role name element must be the String used <br>as the parameter to the <br> " +
								"EJBContext.isCallerInRole(String roleName) method<br>or the " +
									"HttpServletRequest.isUserInRole(String role) method.<br> Used in: portlet"
		},
		{"open.id.ax.type.email[default]", "", ""},
		{
			"javax.portlet.info.keywords", "String",
			"Locale specific keywords associated with this portlet.<br>The kewords are separated by commas.<br>Used " +
				"in: portlet-info"
		},
		{
			"com.liferay.portlet.active", "String",
			"Set the active value to true if the portlet is active and available to users.<br>If set to false, the " +
				"portlet will not be active or available to users. The<br>default value is true.<br>This value can " +
					"be changed at runtime via the Admin portlet."
		},
		{
			"com.liferay.portlet.user-principal-strategy", "String",
			"Set the user-principal-strategy value to either 'userId' or 'screenName'. <br>Calling " +
				"request.getRemoteUser() will normally return the user id. However, some<br>portlets may need the " +
					"user principal returned  to be screen name instead."
		},
		{
			"com.liferay.portlet.header-portal-javascript", "String",
			"Set the path of JavaScript that will be referenced in the page's header relative<br>to the portal's " +
				"context path."
		},
		{"xml.external.parameter.entities.allowed", "", ""}, {"render.command.name", "", ""},
		{"ddm.form.field.type.js.class.name", "", ""},
		{
			"javax.portlet.portlet-name", "String",
			"The portlet element contains the name of a portlet.<br>This name must be unique within the portlet " +
				"application."
		},
		{"xsl.secure.processing.enabled", "", ""}, {"com.liferay.portlet.application-type", "", ""}, {"mode", "", ""},
		{"javax.portlet.init-param.idParamKey", "", ""}, {"lang.type", "", ""},
		{
			"com.liferay.portlet.configuration-path", "String",
			"The configuration-path value is no longer available. Use<br>configuration-action-class instead."
		},
		{"factoryPid", "", ""},
		{
			"com.liferay.portlet.render-weight", "Int",
			"The default value of render-weight is 1. If set to a value less than 1, the<br> portlet is rendered in " +
				"parallel. If set to a value of 1 or greater, then the<br> portlet is rendered serially. Portlets " +
					"with a greater render weight have greater<br>priority and will be rendered before portlets with " +
						"a lower render weight."
		},
		{"auth.verifier.SyncAuthVerifier.urls.includes", "", ""},
		{
			"javax.portlet.info.short-title", "String",
			"Locale specific short version of the static title.<br>Used in: portlet-info"
		},
		{"com.liferay.portlet.display-category", "", ""},
		{
			"com.liferay.portlet.preferences-owned-by-group", "Boolean",
			"Set the preferences-owned-by-group value to true if the preferences for the<br> portlet are owned by " +
				"the group when the portlet is shown in a group page. If<br> set to false, the preferences are owned " +
					"by the user at all times. The default<br>value is true."
		},
		{"filter.init.portal_property_prefix", "", ""},
		{"javax.portlet.info.title", "String", "Indicated the locales the portlet supports.<br> Used in: portlet"},
		{"javax.portlet.init-param.editStylesheet", "", ""},
		{
			"javax.portlet.supported-processing-event", "String",
			"The event-definition-referenceType is used to reference events <br>declared with the event-definition " +
				"element at application level.<br>Used in: portlet"
		},
		{"userNotificationTimeout", "", ""}, {"filter.init.basic_auth", "", ""},
		{"message.status.destination.name", "", ""}, {"importer", "", ""}, {"notification.type", "", ""},
		{"open.id.ax.schema[yahoo]", "", ""}, {"valid.url.prefixes", "", ""}, {"full.text.exact.match.boost", "", ""},
		{"resource.name", "", ""}, {"com.liferay.portlet.webdav-storage-token", "", ""},
		{"portlet.login.update_account", "", ""},
		{
			"com.liferay.portlet.footer-portal-javascript", "String",
			"Set the path of JavaScript that will be referenced in the page's footer relative<br>to the portal's " +
				"context path."
		},
		{"javax.portlet.portlet.info.keywords", "", ""}, {"com.liferay.frontend.image.editor.capability.name", "", ""},
		{"json.web.service.context.path", "", ""}, {"filter.init.auth.verifier.SyncAuthVerifier.urls.includes", "", ""},
		{"platform", "", ""}, {"servlet-context-name", "", ""}, {"javax.portlet.init-param.stylesheetUrl", "", ""},
		{"product.navigation.control.menu.category.key", "", ""}, {"fromAddress", "", ""}, {"jmx.objectname", "", ""},
		{"javax.portlet.init-param.copy-request-parameters", "", ""},
		{
			"com.liferay.portlet.autopropagated-parameters", "String",
			"Set the autopropagated-parameters value to a comma delimited list of parameter<br>names that will be " +
				"automatically propagated through the portlet."
		},
		{
			"com.liferay.portlet.private-session-attributes", "Boolean",
			"Set the private-session-attributes value to true if the portlet does not share<br>session attributes " +
				"with the portal. The default value is true. The property<br>'session.shared.attributes' in " +
					"portal.properties specifies which session<br>attributes are shared even when the " +
						"private-session-attributes value is true."
		},
		{"javax.portlet.init-param.authenticatorClassName", "", ""}, {"osgi.http.whiteboard.servlet.name", "", ""},
		{
			"com.liferay.portlet.remoteable", "String",
			"Set the remoteable value to true if the portlet can be used remotely like in<br>WSRP. If set to false, " +
				"the portlet will not be available remotely. The default<br>value is false."
		},
		{"panel.category.key", "", ""},
		{
			"com.liferay.portlet.struts-path", "String",
			"Suppose the struts-path value is 'mail'. This tells the portal that all<br>requests with the path " +
				"mail/* are considered part of this portlet's scope. Users<br>who request paths that match mail/* " +
					"will only be granted access if they also<br>have access to this portlet. This is true for both " +
						"portlet requests and regular<br>servlet requests."
		},
		{"namespace", "", ""}, {"javax.portlet.init-param.mementoSessionKey", "", ""}
	};

}