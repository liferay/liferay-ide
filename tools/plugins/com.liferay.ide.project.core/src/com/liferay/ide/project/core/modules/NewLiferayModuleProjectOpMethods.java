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
 *
 *******************************************************************************/
package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Simon Jiang
 */
public class NewLiferayModuleProjectOpMethods extends BaseOpMethods
{

    protected static String[] services = new String[] {
        "com.liferay.amazon.rankings.web.upgrade.AmazonRankingsWebUpgrade",
        "com.liferay.application.list.PanelApp",
        "com.liferay.application.list.PanelAppRegistry",
        "com.liferay.application.list.PanelCategory",
        "com.liferay.application.list.PanelCategoryRegistry",
        "com.liferay.asset.browser.web.upgrade.AssetBrowserWebUpgrade",
        "com.liferay.asset.categories.admin.web.upgrade.AssetCategoriesAdminWebUpgrade",
        "com.liferay.asset.categories.navigation.web.upgrade.AssetCategoriesNavigationWebUpgrade",
        "com.liferay.asset.publisher.web.exportimport.portlet.preferences.processor.AssetPublisherPortletDisplayTemplateExportCapability",
        "com.liferay.asset.publisher.web.exportimport.portlet.preferences.processor.AssetPublisherPortletDisplayTemplateImportCapability",
        "com.liferay.asset.publisher.web.upgrade.AssetPublisherWebUpgrade",
        "com.liferay.asset.tags.admin.web.upgrade.AssetTagsAdminWebUpgrade",
        "com.liferay.asset.tags.compiler.web.upgrade.AssetTagsCompilerWebUpgrade",
        "com.liferay.asset.tags.navigation.web.upgrade.AssetTagsNavigationWebUpgrade",
        "com.liferay.blogs.recent.bloggers.web.upgrade.RecentBloggersWebUpgrade",
        "com.liferay.blogs.web.exportimport.portlet.preferences.processor.BlogsPortletDisplayTemplateExportCapability",
        "com.liferay.blogs.web.exportimport.portlet.preferences.processor.BlogsPortletDisplayTemplateImportCapability",
        "com.liferay.blogs.web.upgrade.BlogsWebUpgrade",
        "com.liferay.bookmarks.configuration.BookmarksGroupServiceConfiguration",
        "com.liferay.bookmarks.service.BookmarksEntryLocalService",
        "com.liferay.bookmarks.service.BookmarksFolderLocalService",
        "com.liferay.calendar.service.CalendarBookingLocalService",
        "com.liferay.calendar.service.CalendarImporterLocalService",
        "com.liferay.calendar.service.CalendarResourceLocalService",
        "com.liferay.comment.page.comments.web.upgrade.PageCommentsWebUpgrade",
        "com.liferay.currency.converter.web.upgrade.CurrencyConverterWebUpgrade",
        "com.liferay.dictionary.web.upgrade.DictionaryWebUpgrade",
        "com.liferay.dynamic.data.lists.exporter.DDLExporter",
        "com.liferay.dynamic.data.lists.exporter.DDLExporterFactory",
        "com.liferay.dynamic.data.lists.service.DDLRecordLocalService",
        "com.liferay.dynamic.data.lists.service.DDLRecordService",
        "com.liferay.dynamic.data.lists.service.DDLRecordSetService",
        "com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer",
        "com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory",
        "com.liferay.dynamic.data.mapping.io.DDMFormJSONDeserializer",
        "com.liferay.dynamic.data.mapping.io.DDMFormLayoutJSONDeserializer",
        "com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializer",
        "com.liferay.dynamic.data.mapping.io.DDMFormXSDDeserializer",
        "com.liferay.dynamic.data.mapping.registry.DDMFormFieldType",
        "com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderer",
        "com.liferay.dynamic.data.mapping.render.DDMFormFieldRendererRegistry",
        "com.liferay.dynamic.data.mapping.render.DDMFormFieldValueRenderer",
        "com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService",
        "com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService",
        "com.liferay.dynamic.data.mapping.service.DDMStructureLocalService",
        "com.liferay.dynamic.data.mapping.service.DDMStructureService",
        "com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService",
        "com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService",
        "com.liferay.dynamic.data.mapping.service.DDMTemplateService",
        "com.liferay.dynamic.data.mapping.storage.StorageAdapter",
        "com.liferay.dynamic.data.mapping.storage.StorageEngine",
        "com.liferay.dynamic.data.mapping.type.checkbox.CheckboxDDMFormFieldRenderer",
        "com.liferay.dynamic.data.mapping.type.options.OptionsDDMFormFieldRenderer",
        "com.liferay.dynamic.data.mapping.type.radio.RadioDDMFormFieldRenderer",
        "com.liferay.dynamic.data.mapping.type.select.SelectDDMFormFieldRenderer",
        "com.liferay.dynamic.data.mapping.type.text.TextDDMFormFieldRenderer",
        "com.liferay.dynamic.data.mapping.util.DDM",
        "com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter",
        "com.liferay.dynamic.data.mapping.util.DDMXML",
        "com.liferay.dynamic.data.mapping.util.DefaultDDMStructureHelper",
        "com.liferay.expando.web.upgrade.ExpandoWebUpgrade",
        "com.liferay.exportimport.controller.PortletExportController",
        "com.liferay.exportimport.controller.PortletImportController",
        "com.liferay.flags.page.flags.web.upgrade.PageFlagsWebUpgrade",
        "com.liferay.hello.velocity.web.upgrade.HelloVelocityWebUpgrade",
        "com.liferay.iframe.web.upgrade.IFrameWebUpgrade",
        "com.liferay.invitation.web.upgrade.InvitationWebUpgrade",
        "com.liferay.item.selector.ItemSelector",
        "com.liferay.item.selector.ItemSelectorCriterionHandler",
        "com.liferay.item.selector.ItemSelectorView",
        "com.liferay.item.selector.web.util.ItemSelectorCriterionSerializer",
        "com.liferay.journal.configuration.JournalGroupServiceConfiguration",
        "com.liferay.journal.content.asset.addon.entry.comments.CommentRatingsContentMetadataAssetAddonEntry",
        "com.liferay.journal.content.asset.addon.entry.common.ContentMetadataAssetAddonEntry",
        "com.liferay.journal.content.asset.addon.entry.common.UserToolAssetAddonEntry",
        "com.liferay.journal.content.search.web.upgrade.JournalContentSearchWebUpgrade",
        "com.liferay.journal.content.web.upgrade.JournalContentWebUpgrade",
        "com.liferay.journal.service.JournalArticleImageLocalService",
        "com.liferay.journal.service.JournalArticleLocalService",
        "com.liferay.journal.service.JournalArticleResourceLocalService",
        "com.liferay.journal.service.JournalArticleService",
        "com.liferay.journal.service.JournalContentSearchLocalService",
        "com.liferay.journal.service.JournalFeedLocalService",
        "com.liferay.journal.service.JournalFeedService",
        "com.liferay.journal.service.JournalFolderLocalService",
        "com.liferay.journal.service.JournalFolderService",
        "com.liferay.journal.upgrade.JournalServiceUpgrade",
        "com.liferay.journal.util.JournalConverter",
        "com.liferay.journal.web.upgrade.JournalWebUpgrade",
        "com.liferay.layout.admin.web.upgrade.LayoutAdminWebUpgrade",
        "com.liferay.layout.prototype.web.upgrade.LayoutPrototypeWebUpgrade",
        "com.liferay.layout.set.prototype.web.upgrade.LayoutSetPrototypeWebUpgrade",
        "com.liferay.loan.calculator.web.upgrade.LoanCalculatorWebUpgrade",
        "com.liferay.marketplace.service.AppLocalService",
        "com.liferay.marketplace.service.ModuleLocalService",
        "com.liferay.marketplace.store.web.oauth.util.OAuthManager",
        "com.liferay.mentions.util.MentionsNotifier",
        "com.liferay.mentions.util.MentionsUserFinder",
        "com.liferay.my.account.web.upgrade.MyAccountWebUpgrade",
        "com.liferay.nested.portlets.web.upgrade.NestedPortletWebUpgrade",
        "com.liferay.network.utilities.web.upgrade.NetworkUtilitiesWebUpgrade",
        "com.liferay.password.generator.web.upgrade.PasswordGeneratorWebUpgrade",
        "com.liferay.password.policies.admin.web.upgrade.PasswordPoliciesAdminWebUpgrade",
        "com.liferay.portal.cluster.ClusterChannelFactory",
        "com.liferay.portal.executor.internal.PortalExecutorFactory",
        "com.liferay.portal.expression.ExpressionFactory",
        "com.liferay.portal.instances.web.upgrade.PortalInstancesWebUpgrade",
        "com.liferay.portal.jmx.MBeanRegistry",
        "com.liferay.portal.js.loader.modules.extender.JSLoaderModulesServlet",
        "com.liferay.portal.kernel.atom.AtomCollectionAdapter",
        "com.liferay.portal.kernel.audit.AuditRouter",
        "com.liferay.portal.kernel.cache.MultiVMPool",
        "com.liferay.portal.kernel.cache.PortalCacheManager",
        "com.liferay.portal.kernel.cache.SingleVMPool",
        "com.liferay.portal.kernel.cache.configurator.PortalCacheConfiguratorSettings",
        "com.liferay.portal.kernel.cluster.ClusterEventListener",
        "com.liferay.portal.kernel.cluster.ClusterExecutor",
        "com.liferay.portal.kernel.cluster.ClusterLink",
        "com.liferay.portal.kernel.cluster.ClusterMasterExecutor",
        "com.liferay.portal.kernel.cluster.ClusterMasterTokenTransitionListener",
        "com.liferay.portal.kernel.comment.CommentManager",
        "com.liferay.portal.kernel.editor.configuration.EditorConfigContributor",
        "com.liferay.portal.kernel.editor.configuration.EditorConfigTransformer",
        "com.liferay.portal.kernel.editor.configuration.EditorOptionsContributor",
        "com.liferay.portal.kernel.events.LifecycleAction",
        "com.liferay.portal.kernel.executor.PortalExecutorConfig",
        "com.liferay.portal.kernel.executor.PortalExecutorManager",
        "com.liferay.portal.kernel.facebook.FacebookConnect",
        "com.liferay.portal.kernel.json.JSONFactory",
        "com.liferay.portal.kernel.jsonwebservice.JSONWebServiceActionsManager",
        "com.liferay.portal.kernel.jsonwebservice.JSONWebServiceRegistratorFactory",
        "com.liferay.portal.kernel.lock.LockManager",
        "com.liferay.portal.kernel.messaging.Destination",
        "com.liferay.portal.kernel.messaging.DestinationConfiguration",
        "com.liferay.portal.kernel.messaging.DestinationEventListener",
        "com.liferay.portal.kernel.messaging.DestinationFactory",
        "com.liferay.portal.kernel.messaging.MessageBus",
        "com.liferay.portal.kernel.messaging.MessageBusEventListener",
        "com.liferay.portal.kernel.messaging.MessageListener",
        "com.liferay.portal.kernel.messaging.sender.SingleDestinationMessageSenderFactory",
        "com.liferay.portal.kernel.messaging.sender.SynchronousMessageSender",
        "com.liferay.portal.kernel.mobile.device.DeviceRecognitionProvider",
        "com.liferay.portal.kernel.mobile.device.rulegroup.action.ActionHandler",
        "com.liferay.portal.kernel.mobile.device.rulegroup.rule.RuleHandler",
        "com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle",
        "com.liferay.portal.kernel.monitoring.DataSampleFactory",
        "com.liferay.portal.kernel.monitoring.DataSampleProcessor",
        "com.liferay.portal.kernel.monitoring.MonitoringControl",
        "com.liferay.portal.kernel.monitoring.PortalMonitoringControl",
        "com.liferay.portal.kernel.monitoring.PortletMonitoringControl",
        "com.liferay.portal.kernel.monitoring.ServiceMonitoringControl",
        "com.liferay.portal.kernel.notifications.UserNotificationDefinition",
        "com.liferay.portal.kernel.notifications.UserNotificationHandler",
        "com.liferay.portal.kernel.openid.OpenId",
        "com.liferay.portal.kernel.poller.PollerProcessor",
        "com.liferay.portal.kernel.pop.MessageListener",
        "com.liferay.portal.kernel.portlet.AddPortletProvider",
        "com.liferay.portal.kernel.portlet.BrowsePortletProvider",
        "com.liferay.portal.kernel.portlet.ConfigurationAction",
        "com.liferay.portal.kernel.portlet.DisplayInformationProvider",
        "com.liferay.portal.kernel.portlet.EditPortletProvider",
        "com.liferay.portal.kernel.portlet.FriendlyURLMapper",
        "com.liferay.portal.kernel.portlet.FriendlyURLResolver",
        "com.liferay.portal.kernel.portlet.ManagePortletProvider",
        "com.liferay.portal.kernel.portlet.PortletLayoutListener",
        "com.liferay.portal.kernel.portlet.ViewPortletProvider",
        "com.liferay.portal.kernel.portlet.configuration.PortletConfigurationIconFactory",
        "com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor",
        "com.liferay.portal.kernel.portlet.toolbar.contributor.locator.PortletToolbarContributorLocator",
        "com.liferay.portal.kernel.repository.RepositoryFactory",
        "com.liferay.portal.kernel.repository.registry.RepositoryDefiner",
        "com.liferay.portal.kernel.repository.search.RepositorySearchQueryTermBuilder",
        "com.liferay.portal.kernel.resiliency.spi.cache.SPIPortalCacheManagerConfigurator",
        "com.liferay.portal.kernel.sanitizer.Sanitizer",
        "com.liferay.portal.kernel.scheduler.SchedulerEngine",
        "com.liferay.portal.kernel.scheduler.SchedulerEngineHelper",
        "com.liferay.portal.kernel.scheduler.SchedulerEntry",
        "com.liferay.portal.kernel.scripting.ScriptingExecutor",
        "com.liferay.portal.kernel.search.IndexSearcher",
        "com.liferay.portal.kernel.search.IndexWriter",
        "com.liferay.portal.kernel.search.Indexer",
        "com.liferay.portal.kernel.search.IndexerRegistry",
        "com.liferay.portal.kernel.search.OpenSearch",
        "com.liferay.portal.kernel.search.SearchEngine",
        "com.liferay.portal.kernel.search.SearchEngineConfigurator",
        "com.liferay.portal.kernel.search.SearchPermissionChecker",
        "com.liferay.portal.kernel.search.SearchResultManager",
        "com.liferay.portal.kernel.search.SummaryFactory",
        "com.liferay.portal.kernel.search.filter.FilterTranslator",
        "com.liferay.portal.kernel.search.hits.HitsProcessor",
        "com.liferay.portal.kernel.search.query.QueryPreProcessConfiguration",
        "com.liferay.portal.kernel.search.query.QueryTranslator",
        "com.liferay.portal.kernel.search.result.SearchResultContributor",
        "com.liferay.portal.kernel.search.suggest.QuerySuggester",
        "com.liferay.portal.kernel.search.suggest.SpellCheckIndexWriter",
        "com.liferay.portal.kernel.search.suggest.SuggesterTranslator",
        "com.liferay.portal.kernel.security.access.control.AccessControl",
        "com.liferay.portal.kernel.security.access.control.AccessControlPolicy",
        "com.liferay.portal.kernel.security.auth.AlwaysAllowDoAsUser",
        "com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManager",
        "com.liferay.portal.kernel.security.auto.login.AutoLogin",
        "com.liferay.portal.kernel.security.sso.OpenSSO",
        "com.liferay.portal.kernel.security.sso.SSO",
        "com.liferay.portal.kernel.servlet.PortalWebResources",
        "com.liferay.portal.kernel.servlet.URLEncoder",
        "com.liferay.portal.kernel.servlet.taglib.DynamicInclude",
        "com.liferay.portal.kernel.servlet.taglib.TagDynamicIdFactory",
        "com.liferay.portal.kernel.settings.SettingsFactory",
        "com.liferay.portal.kernel.settings.SettingsLocatorHelper",
        "com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration",
        "com.liferay.portal.kernel.settings.definition.SettingsIdMapping",
        "com.liferay.portal.kernel.social.SocialActivityManager",
        "com.liferay.portal.kernel.struts.StrutsAction",
        "com.liferay.portal.kernel.struts.StrutsPortletAction",
        "com.liferay.portal.kernel.template.TemplateHandler",
        "com.liferay.portal.kernel.template.TemplateResourceLoader",
        "com.liferay.portal.kernel.transaction.TransactionLifecycleListener",
        "com.liferay.portal.kernel.trash.TrashHandler",
        "com.liferay.portal.kernel.trash.TrashRendererFactory",
        "com.liferay.portal.kernel.util.InfrastructureUtil",
        "com.liferay.portal.kernel.util.Props",
        "com.liferay.portal.kernel.webdav.WebDAVStorage",
        "com.liferay.portal.kernel.workflow.WorkflowHandler",
        "com.liferay.portal.kernel.xml.SAXReaderUtil",
        "com.liferay.portal.kernel.xmlrpc.Method",
        "com.liferay.portal.lock.service.LockLocalService",
        "com.liferay.portal.messaging.DestinationPrototype",
        "com.liferay.portal.model.LayoutTypeAccessPolicy",
        "com.liferay.portal.model.ModelListener",
        "com.liferay.portal.model.Portlet",
        "com.liferay.portal.model.Release",
        "com.liferay.portal.model.adapter.builder.ModelAdapterBuilder",
        "com.liferay.portal.monitoring.internal.statistics.portal.ServerStatistics",
        "com.liferay.portal.monitoring.internal.statistics.portal.ServerSummaryStatistics",
        "com.liferay.portal.monitoring.internal.statistics.portlet.ActionRequestSummaryStatistics",
        "com.liferay.portal.monitoring.internal.statistics.portlet.EventRequestSummaryStatistics",
        "com.liferay.portal.monitoring.internal.statistics.portlet.RenderRequestSummaryStatistics",
        "com.liferay.portal.monitoring.internal.statistics.portlet.ResourceRequestSummaryStatistics",
        "com.liferay.portal.monitoring.internal.statistics.portlet.ServerStatistics",
        "com.liferay.portal.monitoring.internal.statistics.service.ServerStatistics",
        "com.liferay.portal.scheduler.quartz.internal.QuartzSchedulerEngine",
        "com.liferay.portal.search.IndexerRequestBufferOverflowHandler",
        "com.liferay.portal.search.analysis.KeywordTokenizer",
        "com.liferay.portal.search.elasticsearch.connection.ElasticsearchConnection",
        "com.liferay.portal.search.elasticsearch.connection.ElasticsearchConnectionManager",
        "com.liferay.portal.search.elasticsearch.document.ElasticsearchDocumentFactory",
        "com.liferay.portal.search.elasticsearch.document.ElasticsearchUpdateDocumentCommand",
        "com.liferay.portal.search.elasticsearch.facet.FacetProcessor",
        "com.liferay.portal.search.elasticsearch.filter.BooleanFilterTranslator",
        "com.liferay.portal.search.elasticsearch.filter.DateRangeTermFilterTranslator",
        "com.liferay.portal.search.elasticsearch.filter.ExistsFilterTranslator",
        "com.liferay.portal.search.elasticsearch.filter.GeoBoundingBoxFilterTranslator",
        "com.liferay.portal.search.elasticsearch.filter.GeoDistanceFilterTranslator",
        "com.liferay.portal.search.elasticsearch.filter.GeoDistanceRangeFilterTranslator",
        "com.liferay.portal.search.elasticsearch.filter.GeoPolygonFilterTranslator",
        "com.liferay.portal.search.elasticsearch.filter.MissingFilterTranslator",
        "com.liferay.portal.search.elasticsearch.filter.PrefixFilterTranslator",
        "com.liferay.portal.search.elasticsearch.filter.QueryFilterTranslator",
        "com.liferay.portal.search.elasticsearch.filter.RangeTermFilterTranslator",
        "com.liferay.portal.search.elasticsearch.filter.TermFilterTranslator",
        "com.liferay.portal.search.elasticsearch.filter.TermsFilterTranslator",
        "com.liferay.portal.search.elasticsearch.index.IndexFactory",
        "com.liferay.portal.search.elasticsearch.internal.cluster.ClusterSettingsContext",
        "com.liferay.portal.search.elasticsearch.internal.facet.CompositeFacetProcessor",
        "com.liferay.portal.search.elasticsearch.query.BooleanQueryTranslator",
        "com.liferay.portal.search.elasticsearch.query.DisMaxQueryTranslator",
        "com.liferay.portal.search.elasticsearch.query.FuzzyQueryTranslator",
        "com.liferay.portal.search.elasticsearch.query.MatchAllQueryTranslator",
        "com.liferay.portal.search.elasticsearch.query.MatchQueryTranslator",
        "com.liferay.portal.search.elasticsearch.query.MoreLikeThisQueryTranslator",
        "com.liferay.portal.search.elasticsearch.query.MultiMatchQueryTranslator",
        "com.liferay.portal.search.elasticsearch.query.NestedQueryTranslator",
        "com.liferay.portal.search.elasticsearch.query.StringQueryTranslator",
        "com.liferay.portal.search.elasticsearch.query.TermQueryTranslator",
        "com.liferay.portal.search.elasticsearch.query.TermRangeQueryTranslator",
        "com.liferay.portal.search.elasticsearch.query.WildcardQueryTranslator",
        "com.liferay.portal.search.elasticsearch.settings.SettingsContributor",
        "com.liferay.portal.search.elasticsearch.suggest.AggregateSuggesterTranslator",
        "com.liferay.portal.search.elasticsearch.suggest.CompletionSuggesterTranslator",
        "com.liferay.portal.search.elasticsearch.suggest.PhraseSuggesterTranslator",
        "com.liferay.portal.search.elasticsearch.suggest.TermSuggesterTranslator",
        "com.liferay.portal.security.auth.AuthFailure",
        "com.liferay.portal.security.auth.AuthToken",
        "com.liferay.portal.security.auth.Authenticator",
        "com.liferay.portal.security.auth.EmailAddressGenerator",
        "com.liferay.portal.security.auth.EmailAddressValidator",
        "com.liferay.portal.security.auth.FullNameGenerator",
        "com.liferay.portal.security.auth.FullNameValidator",
        "com.liferay.portal.security.auth.ScreenNameGenerator",
        "com.liferay.portal.security.auth.ScreenNameValidator",
        "com.liferay.portal.security.exportimport.UserExporter",
        "com.liferay.portal.security.ldap.AttributesTransformer",
        "com.liferay.portal.security.ldap.LDAPSettings",
        "com.liferay.portal.security.ldap.LDAPToPortalConverter",
        "com.liferay.portal.security.ldap.PortalToLDAPConverter",
        "com.liferay.portal.security.membershippolicy.OrganizationMembershipPolicy",
        "com.liferay.portal.security.membershippolicy.RoleMembershipPolicy",
        "com.liferay.portal.security.membershippolicy.SiteMembershipPolicy",
        "com.liferay.portal.security.membershippolicy.UserGroupMembershipPolicy",
        "com.liferay.portal.security.permission.PermissionCheckerFactory",
        "com.liferay.portal.security.permission.PermissionPropagator",
        "com.liferay.portal.security.permission.ResourceActions",
        "com.liferay.portal.security.permission.ResourcePermissionChecker",
        "com.liferay.portal.security.sso.ntlm.NetlogonConnectionManager",
        "com.liferay.portal.security.sso.openid.OpenIdProvider",
        "com.liferay.portal.security.sso.openid.OpenIdProviderRegistry",
        "com.liferay.portal.security.sso.token.events.LogoutProcessor",
        "com.liferay.portal.security.sso.token.security.auth.TokenRetriever",
        "com.liferay.portal.service.ClassNameLocalService",
        "com.liferay.portal.service.CompanyLocalService",
        "com.liferay.portal.service.GroupLocalService",
        "com.liferay.portal.service.LayoutBranchService",
        "com.liferay.portal.service.LayoutLocalService",
        "com.liferay.portal.service.LayoutPrototypeLocalService",
        "com.liferay.portal.service.LayoutRevisionLocalService",
        "com.liferay.portal.service.LayoutSetBranchLocalService",
        "com.liferay.portal.service.LayoutSetBranchService",
        "com.liferay.portal.service.LayoutSetLocalService",
        "com.liferay.portal.service.LayoutSetPrototypeLocalService",
        "com.liferay.portal.service.MembershipRequestLocalService",
        "com.liferay.portal.service.PortletLocalService",
        "com.liferay.portal.service.ReleaseLocalService",
        "com.liferay.portal.service.RepositoryEntryLocalService",
        "com.liferay.portal.service.RepositoryLocalService",
        "com.liferay.portal.service.ResourceActionLocalService",
        "com.liferay.portal.service.ResourceBlockLocalService",
        "com.liferay.portal.service.ResourceLocalService",
        "com.liferay.portal.service.ResourcePermissionLocalService",
        "com.liferay.portal.service.RoleLocalService",
        "com.liferay.portal.service.ServiceWrapper",
        "com.liferay.portal.service.SubscriptionLocalService",
        "com.liferay.portal.service.UserGroupRoleLocalService",
        "com.liferay.portal.service.UserLocalService",
        "com.liferay.portal.service.WorkflowDefinitionLinkLocalService",
        "com.liferay.portal.service.configuration.configurator.ServiceConfigurator",
        "com.liferay.portal.service.impl.LayoutLocalServiceHelper",
        "com.liferay.portal.store.db.DBStore",
        "com.liferay.portal.template.freemarker.FreeMarkerTemplateContextHelper",
        "com.liferay.portal.template.freemarker.FreeMarkerTemplateResourceLoader",
        "com.liferay.portal.template.soy.SoyTemplateContextHelper",
        "com.liferay.portal.template.velocity.VelocityTemplateContextHelper",
        "com.liferay.portal.template.velocity.VelocityTemplateResourceLoader",
        "com.liferay.portal.util.Portal",
        "com.liferay.portal.util.PortalInetSocketAddressEventListener",
        "com.liferay.portal.verify.VerifyProcess",
        "com.liferay.portal.workflow.kaleo.manager.PortalKaleoManager",
        "com.liferay.portlet.ControlPanelEntry",
        "com.liferay.portlet.PortletInstanceFactory",
        "com.liferay.portlet.admin.util.Omniadmin",
        "com.liferay.portlet.asset.model.AssetRendererFactory",
        "com.liferay.portlet.asset.service.AssetEntryLocalService",
        "com.liferay.portlet.asset.util.AssetEntryQueryProcessor",
        "com.liferay.portlet.calendar.service.CalEventLocalService",
        "com.liferay.portlet.configuration.web.upgrade.PortletConfigurationWebUpgrade",
        "com.liferay.portlet.css.web.upgrade.PortletCSSWebUpgrade",
        "com.liferay.portlet.display.template.PortletDisplayTemplate",
        "com.liferay.portlet.display.template.exportimport.portlet.preferences.processor.PortletDisplayTemplateExportCapability",
        "com.liferay.portlet.display.template.exportimport.portlet.preferences.processor.PortletDisplayTemplateImportCapability",
        "com.liferay.portlet.documentlibrary.service.DLAppHelperLocalService",
        "com.liferay.portlet.documentlibrary.service.DLAppLocalService",
        "com.liferay.portlet.documentlibrary.service.DLAppService",
        "com.liferay.portlet.documentlibrary.service.DLFileEntryLocalService",
        "com.liferay.portlet.documentlibrary.service.DLFileEntryMetadataLocalService",
        "com.liferay.portlet.documentlibrary.service.DLFileEntryTypeLocalService",
        "com.liferay.portlet.documentlibrary.service.DLFolderLocalService",
        "com.liferay.portlet.documentlibrary.store.Store",
        "com.liferay.portlet.documentlibrary.store.StoreWrapper",
        "com.liferay.portlet.documentlibrary.util.DLProcessor",
        "com.liferay.portlet.dynamicdatamapping.DDMStructureLinkManager",
        "com.liferay.portlet.dynamicdatamapping.DDMStructureManager",
        "com.liferay.portlet.dynamicdatamapping.DDMTemplateManager",
        "com.liferay.portlet.expando.model.CustomAttributesDisplay",
        "com.liferay.portlet.expando.service.ExpandoColumnLocalService",
        "com.liferay.portlet.expando.service.ExpandoTableLocalService",
        "com.liferay.portlet.expando.service.ExpandoValueLocalService",
        "com.liferay.portlet.exportimport.lar.PortletDataHandler",
        "com.liferay.portlet.exportimport.lar.StagedModelDataHandler",
        "com.liferay.portlet.exportimport.staging.LayoutStaging",
        "com.liferay.portlet.exportimport.staging.Staging",
        "com.liferay.portlet.exportimport.staging.permission.StagingPermission",
        "com.liferay.portlet.exportimport.xstream.XStreamAliasRegistryUtil$XStreamAlias",
        "com.liferay.portlet.exportimport.xstream.XStreamConverter",
        "com.liferay.portlet.messageboards.service.MBBanLocalService",
        "com.liferay.portlet.messageboards.service.MBBanService",
        "com.liferay.portlet.messageboards.service.MBCategoryLocalService",
        "com.liferay.portlet.messageboards.service.MBCategoryService",
        "com.liferay.portlet.messageboards.service.MBDiscussionLocalService",
        "com.liferay.portlet.messageboards.service.MBMessageLocalService",
        "com.liferay.portlet.messageboards.service.MBMessageService",
        "com.liferay.portlet.messageboards.service.MBStatsUserLocalService",
        "com.liferay.portlet.messageboards.service.MBThreadFlagLocalService",
        "com.liferay.portlet.messageboards.service.MBThreadLocalService",
        "com.liferay.portlet.messageboards.service.MBThreadService",
        "com.liferay.portlet.mobiledevicerules.service.MDRRuleGroupInstanceLocalService",
        "com.liferay.portlet.mobiledevicerules.service.MDRRuleGroupLocalService",
        "com.liferay.portlet.ratings.service.RatingsEntryLocalService",
        "com.liferay.portlet.ratings.service.RatingsStatsLocalService",
        "com.liferay.portlet.social.model.SocialActivityInterpreter",
        "com.liferay.portlet.social.model.SocialRequestInterpreter",
        "com.liferay.portlet.social.service.SocialActivityLocalService",
        "com.liferay.portlet.social.service.SocialRequestLocalService",
        "com.liferay.portlet.social.service.SocialRequestService",
        "com.liferay.portlet.trash.service.TrashEntryService",
        "com.liferay.quick.note.web.uprade.QuickNoteWebUpgrade",
        "com.liferay.ratings.page.ratings.web.upgrade.PageRatingsWebUpgrade",
        "com.liferay.roles.admin.web.upgrade.RolesAdminWebUpgrade",
        "com.liferay.rss.web.upgrade.RSSWebUpgrade",
        "com.liferay.search.web.upgrade.SearchWebUpgrade",
        "com.liferay.search.web.util.SearchFacet",
        "com.liferay.service.access.policy.service.SAPEntryLocalService",
        "com.liferay.service.access.policy.service.SAPEntryService",
        "com.liferay.site.admin.web.upgrade.SiteAdminWebUpgrade",
        "com.liferay.site.browser.web.upgrade.SiteBrowserWebUpgrade",
        "com.liferay.site.memberships.web.upgrade.SiteMembershipsWebUpgrade",
        "com.liferay.site.my.sites.web.upgrade.MySitesWebUpgrade",
        "com.liferay.site.navigation.breadcrumb.web.upgrade.BreadcrumbWebUpgrade",
        "com.liferay.site.navigation.directory.web.upgrade.SitesDirectoryWebUpgrade",
        "com.liferay.site.navigation.language.web.upgrade.LanguageWebUpgrade",
        "com.liferay.site.navigation.menu.web.upgrade.NavigationMenuWebUpgrade",
        "com.liferay.site.navigation.site.map.web.upgrade.SiteMapWebUpgrade",
        "com.liferay.site.teams.web.upgrade.SiteTeamsWebUpgrade",
        "com.liferay.social.activities.web.upgrade.SocialActivitiesWebUpgrade",
        "com.liferay.social.activity.web.upgrade.SocialActivityWebUpgrade",
        "com.liferay.social.requests.web.upgrade.SocialRequestsWebUpgrade",
        "com.liferay.translator.web.upgrade.TranslatorWebUpgrade",
        "com.liferay.trash.web.upgrade.TrashWebUpgrade",
        "com.liferay.unit.converter.web.upgrade.UnitConverterWebUpgrade",
        "com.liferay.user.groups.admin.web.exportimport.portlet.preferences.processor.UserGroupsAdminPortletDisplayTemplateExportCapability",
        "com.liferay.user.groups.admin.web.exportimport.portlet.preferences.processor.UserGroupsAdminPortletDisplayTemplateImportCapability",
        "com.liferay.user.groups.admin.web.upgrade.UserGroupsAdminWebUpgrade",
        "com.liferay.users.admin.web.upgrade.UsersAdminWebUpgrade",
        "com.liferay.web.proxy.web.upgrade.WebProxyWebUpgrade",
        "com.liferay.wiki.configuration.WikiGroupServiceConfiguration",
        "com.liferay.wiki.display.context.WikiDisplayContextFactory",
        "com.liferay.wiki.engine.WikiEngine",
        "com.liferay.wiki.importer.WikiImporter",
        "com.liferay.wiki.service.WikiPageLocalService",
        "com.liferay.wiki.service.WikiPageResourceLocalService",
        "com.liferay.wiki.web.display.context.WikiDisplayContextProvider",
        "com.liferay.xsl.content.web.upgrade.XSLContentWebUpgrade",
        "java.net.ContentHandler", "java.util.ResourceBundle",
        "javax.management.DynamicMBean", "javax.management.MBeanServer",
        "javax.portlet.Portlet", "javax.portlet.PreferencesValidator",
        "javax.servlet.Filter", "javax.servlet.Servlet",
        "javax.servlet.ServletContext", "javax.servlet.http.HttpServlet",
        "javax.sql.DataSource", "javax.xml.parsers.SAXParserFactory",
        "org.apache.felix.bundlerepository.RepositoryAdmin",
        "org.apache.felix.cm.PersistenceManager",
        "org.apache.felix.fileinstall.ArtifactInstaller",
        "org.apache.felix.fileinstall.ArtifactTransformer",
        "org.apache.felix.fileinstall.ArtifactUrlTransformer",
        "org.apache.felix.gogo.api.CommandSessionListener",
        "org.apache.felix.service.command.CommandProcessor",
        "org.apache.felix.service.command.Converter",
        "org.apache.felix.webconsole.BrandingPlugin",
        "org.apache.felix.webconsole.WebConsoleSecurityProvider",
        "org.apache.felix.webconsole.bundleinfo.BundleInfoProvider",
        "org.eclipse.equinox.console.commands.CommandsTracker",
        "org.eclipse.equinox.http.servlet.context.ContextPathCustomizer",
        "org.eclipse.osgi.framework.console.CommandProvider",
        "org.eclipse.osgi.framework.console.ConsoleSession",
        "org.eclipse.osgi.service.debug.DebugOptionsListener",
        "org.eclipse.osgi.signedcontent.SignedContentFactory",
        "org.osgi.service.cm.ConfigurationAdmin",
        "org.osgi.service.cm.ConfigurationListener",
        "org.osgi.service.cm.ManagedService",
        "org.osgi.service.cm.ManagedServiceFactory",
        "org.osgi.service.cm.SynchronousConfigurationListener",
        "org.osgi.service.condpermadmin.ConditionalPermissionAdmin",
        "org.osgi.service.event.EventAdmin",
        "org.osgi.service.event.EventHandler",
        "org.osgi.service.http.HttpService",
        "org.osgi.service.http.context.ServletContextHelper",
        "org.osgi.service.http.runtime.HttpServiceRuntime",
        "org.osgi.service.log.LogReaderService",
        "org.osgi.service.log.LogService",
        "org.osgi.service.metatype.MetaTypeProvider",
        "org.osgi.service.metatype.MetaTypeService",
        "org.osgi.service.packageadmin.PackageAdmin",
        "org.osgi.service.permissionadmin.PermissionAdmin",
        "org.osgi.service.provisioning.ProvisioningService",
        "org.osgi.service.startlevel.StartLevel",
        "org.osgi.service.url.URLStreamHandlerService",
        "org.osgi.service.useradmin.UserAdmin",
        "org.springframework.context.ApplicationContext" };

    public static String[] getServices()
    {
        //TODO IDE-2164
        return services;
    }

    public static final Status execute( final NewLiferayModuleProjectOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay plugin project (this process may take several minutes)", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final NewLiferayProjectProvider<NewLiferayModuleProjectOp> projectProvider = op.getProjectProvider().content( true );

            final IStatus status = projectProvider.createNewProject( op, monitor );

            retval = StatusBridge.create( status );
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay module project."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }

    public static String getMavenParentPomGroupId( NewLiferayModuleProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> groupId =
                op.getProjectProvider().content().getData( "parentGroupId", String.class, parentProjectDir );

            if( ! groupId.isEmpty() )
            {
                retval = groupId.get( 0 );
            }
        }

        return retval;
    }

    public static String getMavenParentPomVersion( NewLiferayModuleProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> version =
                op.getProjectProvider().content().getData( "parentVersion", String.class, parentProjectDir );

            if( !version.isEmpty() )
            {
                retval = version.get( 0 );
            }
        }

        return retval;
    }

    public static void addProperties( File dest, List<String> properties ) throws Exception
    {

        if( properties == null || properties.size() < 1 )
        {
            return;
        }

        FileInputStream fis = new FileInputStream( dest );

        String content = new String( FileUtil.readContents( fis ) );

        fis.close();

        String fontString = content.substring( 0, content.indexOf( "property" ) );

        String endString = content.substring( content.indexOf( "}," ) + 2 );

        String property = content.substring( content.indexOf( "property" ), content.indexOf( "}," ) );

        property = property.substring( property.indexOf( "{" ) + 1 );

        StringBuilder sb = new StringBuilder();

        sb.append( "property = {\n" );

        if( !CoreUtil.isNullOrEmpty( property ) )
        {
            property = property.substring( 1 );
            property = property.substring( 0, property.lastIndexOf( "\t" ) - 1 );
            property += ",\n";
            sb.append( property );
        }

        for( String str : properties )
        {
            sb.append( "\t\t\"" + str + "\",\n" );
        }

        sb.deleteCharAt( sb.toString().length() - 2 );

        sb.append( "\t}," );

        StringBuilder all = new StringBuilder();

        all.append( fontString );
        all.append( sb.toString() );
        all.append( endString );

        String newContent = all.toString();

        if( !content.equals( newContent ) )
        {
            FileUtil.writeFileFromStream( dest, new ByteArrayInputStream( newContent.getBytes() ) );
        }
    }

    public static void addDenpendencies(File file , String bundleId , String bundleVersion) throws Exception
    {
       String content = new String( FileUtil.readContents( file, true ) );

       String head = content.substring( 0 , content.lastIndexOf( "dependencies" ) );

       String end = content.substring( content.lastIndexOf( "}" )+1 , content.length() );

       String dependencies = content.substring( content.lastIndexOf( "{" )+2 , content.lastIndexOf( "}" ) );

       String appended = "\tcompile 'com.liferay:"+bundleId+":"+bundleVersion+"'\n";

       StringBuilder preNewContent = new StringBuilder();

       preNewContent.append(head);
       preNewContent.append("dependencies {\n");
       preNewContent.append(dependencies+appended);
       preNewContent.append("}");
       preNewContent.append(end);

       String newContent = preNewContent.toString();

       if (!content.equals(newContent))
       {
           FileUtil.writeFileFromStream( file, new ByteArrayInputStream( newContent.getBytes() ) );
       }
    }
}
