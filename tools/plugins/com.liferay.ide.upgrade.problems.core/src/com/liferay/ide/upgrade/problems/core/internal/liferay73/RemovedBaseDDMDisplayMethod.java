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

package com.liferay.ide.upgrade.problems.core.internal.liferay73;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ethan Sun
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.summary=Removed com.liferay.dynamic.data.mapping.util.BaseDDMDisplay Method",
		"problem.tickets=LPS-103549",
		"problem.title=Removed com.liferay.dynamic.data.mapping.util.BaseDDMDisplay Method",
		"problem.section=#removed-BaseDDMDisplay-method", "problem.version=7.3", "version=7.3"
	},
	service = FileMigrator.class
)
public class RemovedBaseDDMDisplayMethod extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"AlloyServiceInvoker", null, "dynamicQuery",
				new String[] {"com.liferay.portal.kernel.dao.orm.DynamicQuery"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"AlloyServiceInvoker", null, "dynamicQueryCount",
				new String[] {"com.liferay.portal.kernel.dao.orm.DynamicQuery"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"Recurrence", null, "addExceptionDate", new String[] {"com.liferay.calendar.model.Calendar"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDL", null, "getRecordJSONObject", new String[] {"com.liferay.dynamic.data.lists.model.DDLRecord"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDL", null, "getRecordsJSONArray",
				new String[] {"com.liferay.dynamic.data.lists.model.DDLRecordSet"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDL", null, "getRecordsJSONArray",
				new String[] {"java.util.List<com.liferay.dynamic.data.lists.model.DDLRecord>"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDL", null, "isEditable",
				new String[] {"javax.servlet.http.HttpServletRequest", "java.lang.String", "long"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDL", null, "isEditable",
				new String[] {"javax.portlet.PortletPreferences", "java.lang.String", "long"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDL", null, "updateRecord",
				new String[] {"long", "long", "boolean", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMDataProviderContext", null, "addParameters",
				new String[] {"java.util.Map<java.lang.String, java.lang.String>"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations("DDMDataProviderContext", null, "getParameters", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMExpression", null, "setDDMExpressionFunction",
				new String[] {
					"java.lang.String", "com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMExpression", null, "setExpressionStringVariableValue",
				new String[] {"java.lang.String", "java.lang.String"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMExpression", null, "setFloatVariableValue", new String[] {"java.lang.String", "java.lang.Float"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMExpression", null, "setLongVariableValue", new String[] {"java.lang.String", "java.lang.Long"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMExpression", null, "setNumberVariableValue",
				new String[] {"java.lang.String", "java.lang.Number"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMExpression", null, "setObjectVariableValue",
				new String[] {"java.lang.String", "java.lang.Object"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMExpressionFactory", null, "createDoubleDDMExpression", new String[] {"java.lang.String"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMExpressionFactory", null, "createFloatDDMExpression", new String[] {"java.lang.String"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMExpressionFactory", null, "createIntegerDDMExpression", new String[] {"java.lang.String"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMExpressionFactory", null, "createLongDDMExpression", new String[] {"java.lang.String"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMExpressionFactory", null, "createNumberDDMExpression", new String[] {"java.lang.String"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMExpressionFactory", null, "createStringDDMExpression", new String[] {"java.lang.String"}));

		searchResults.addAll(javaFileChecker.findMethodInvocations("DDMExpressionFunction", null, "evaluate", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMFormEvaluator", null, "evaluate", new String[] {"DDMFormEvaluatorContext"}));

		searchResults.add(
			javaFileChecker.findImport("com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorContext"));

		searchResults.addAll(
			javaFileChecker.findSuperClass("com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorContext"));

		searchResults.addAll(javaFileChecker.findMethodInvocations("DDMFormEvaluatorContext", null, "", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMFormFieldEvaluationResult", null, "getNestedDDMFormFieldEvaluationResults", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMFormFieldEvaluationResult", null, "setNestedDDMFormFieldEvaluationResults",
				new String[] {
					"java.util.List<com.liferay.dynamic.data.mapping.form.evaluator.DDMFormFieldEvaluationResult>"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DefaultDDMFormFieldTypeSettings", null, "visibilityExpression", null));

		searchResults.add(
			javaFileChecker.findImport("com.liferay.dynamic.data.mapping.io.DDMFormFieldTypesJSONSerializer"));

		searchResults.addAll(
			javaFileChecker.findImplementsInterface(
				"com.liferay.dynamic.data.mapping.io.DDMFormFieldTypesJSONSerializer"));

		searchResults.add(javaFileChecker.findImport("com.liferay.dynamic.data.mapping.io.DDMFormJSONSerializer"));

		searchResults.addAll(
			javaFileChecker.findImplementsInterface("com.liferay.dynamic.data.mapping.io.DDMFormJSONSerializer"));

		searchResults.add(
			javaFileChecker.findImport("com.liferay.dynamic.data.mapping.io.DDMFormLayoutJSONSerializer"));

		searchResults.addAll(
			javaFileChecker.findImplementsInterface("com.liferay.dynamic.data.mapping.io.DDMFormLayoutJSONSerializer"));

		searchResults.add(
			javaFileChecker.findImport("com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializer"));

		searchResults.addAll(
			javaFileChecker.findImplementsInterface(
				"com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializer"));

		searchResults.add(
			javaFileChecker.findImport("com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONSerializer"));

		searchResults.addAll(
			javaFileChecker.findImplementsInterface("com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONSerializer"));

		searchResults.add(javaFileChecker.findImport("com.liferay.dynamic.data.mapping.io.DDMFormXSDDeserializer"));

		searchResults.addAll(
			javaFileChecker.findImplementsInterface("com.liferay.dynamic.data.mapping.io.DDMFormXSDDeserializer"));

		searchResults.add(javaFileChecker.findImport("com.liferay.dynamic.data.mapping.io.DDMExporterFactory"));

		searchResults.addAll(
			javaFileChecker.findImplementsInterface("com.liferay.dynamic.data.mapping.io.DDMExporterFactory"));

		searchResults.add(javaFileChecker.findImport("com.liferay.dynamic.data.mapping.io.DDMFormExporter"));

		searchResults.addAll(
			javaFileChecker.findImplementsInterface("com.liferay.dynamic.data.mapping.io.DDMFormExporter"));

		searchResults.addAll(javaFileChecker.findMethodInvocations("DDMFormField", null, "getDDMFormFieldRules", null));

		searchResults.addAll(javaFileChecker.findMethodInvocations("DDMFormField", null, "getDDMFormFieldRules", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMFormField", null, "setDDMFormFieldRules",
				new String[] {"java.util.List<com.liferay.dynamic.data.mapping.model.DDMFormFieldRule>"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMFormFieldRenderingContext", null, "setChildElementsHTML", new String[] {"String"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"BaseDDMDisplay", null, "getViewTemplatesTitle",
				new String[] {"com.liferay.dynamic.data.mapping.model.DDMStructure", "boolean", "java.util.Locale"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations("BaseDDMDisplay", null, "isShowAddStructureButton", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"BaseDDMDisplay", null, "getBaseDDMDisplayResourceBundle", new String[] {"java.lang.String"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"BaseDDMDisplay", null, "getDDMDisplayResourceBundle", new String[] {"java.lang.String"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"BaseDDMDisplay", null, "getPortalResourceBundle", new String[] {"java.lang.String"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMDisplay", null, "getViewTemplatesTitle",
				new String[] {"com.liferay.dynamic.data.mapping.model.DDMStructure", "boolean", "java.util.Locale"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations("DDMDisplay", null, "isShowAddStructureButton", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations("DDMFormFieldFactoryHelper", null, "collectResourceBundles", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMFormFieldFactoryHelper", null, "getResourceBundle", new String[] {"java.util.Locale"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMFormFieldFactoryHelper", null, "getResourceBundleBaseName", null));

		searchResults.add(
			javaFileChecker.findImport("com.liferay.dynamic.data.mapping.util.DDMFormInstanceFactoryHelper"));

		searchResults.addAll(
			javaFileChecker.findSuperClass("com.liferay.dynamic.data.mapping.util.DDMFormInstanceFactoryHelper"));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"BlogsEntry", null, "getEntryImageURL", new String[] {"com.liferay.portal.kernel.theme.ThemeDisplay"}));

		searchResults.addAll(javaFileChecker.findMethodInvocations("BlogsEntry", null, "getSmallImageType", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"BlogsEntryLocalService", null, "addEntry",
				new String[] {
					"long", "java.lang.String", "java.lang.String", "java.lang.String", "int", "int", "int", "int",
					"int", "boolean", "boolean", "java.lang.String[]", "boolean", "java.lang.String",
					"java.lang.String", "java.io.InputStream", "com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations("BlogsEntryLocalService", null, "getNoAssetEntries", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"BlogsEntryLocalService", null, "updateEntry",
				new String[] {
					"long", "long", "java.lang.String", "java.lang.String", "java.lang.String", "int", "int", "int",
					"int", "int", "boolean", "boolean", "String[]", "boolean", "java.lang.String", "java.lang.String",
					"java.io.InputStream", "com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"BlogsEntryLocalService", null, "updateStatus",
				new String[] {"long", "long", "int", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "BlogsEntryLocalServiceUtil", "addEntry",
				new String[] {
					"long", "java.lang.String", "java.lang.String", "java.lang.String", "int", "int", "int", "int",
					"int", "boolean", "boolean", "java.lang.String[]", "boolean", "java.lang.String",
					"java.lang.String", "java.io.InputStream", "com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(null, "BlogsEntryLocalServiceUtil", "getNoAssetEntries", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "BlogsEntryLocalServiceUtil", "updateEntry",
				new String[] {
					"long", "long", "java.lang.String", "java.lang.String", "java.lang.String", "int", "int", "int",
					"int", "int", "boolean", "boolean", "String[]", "boolean", "java.lang.String", "java.lang.String",
					"java.io.InputStream", "com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "BlogsEntryLocalServiceUtil", "updateStatus",
				new String[] {"long", "long", "int", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(javaFileChecker.findMethodInvocations("BlogsEntryFinder", null, "findByNoAssets", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations("BookmarksEntryLocalService", null, "getNoAssetEntries", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(null, "BookmarksEntryLocalServiceUtil", "getNoAssetEntries", null));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"BookmarksFolderLocalService", null, "updateFolder",
				new String[] {
					"long", "long", "long", "java.lang.String", "java.lang.String", "boolean",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "BookmarksFolderLocalServiceUtil", "updateFolder",
				new String[] {
					"long", "long", "long", "java.lang.String", "java.lang.String", "boolean",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"BookmarksFolderService", null, "getSubfolderIds",
				new String[] {"java.util.List<java.lang.Long>", "long", "long"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"BookmarksFolderService", null, "updateFolder",
				new String[] {
					"long", "long", "String", "String", "boolean", "com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "BookmarksFolderServiceUtil", "getSubfolderIds",
				new String[] {"java.util.List<java.lang.Long>", "long", "long"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "BookmarksFolderServiceUtil", "updateFolder",
				new String[] {
					"long", "long", "String", "String", "boolean", "com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.add(javaFileChecker.findImport("com.liferay.bookmarks.service.persistence.BookmarksEntryFinder"));

		searchResults.addAll(
			javaFileChecker.findImplementsInterface("com.liferay.bookmarks.service.persistence.BookmarksEntryFinder"));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"CalendarBookingLocalService", null, "addCalendarBooking",
				new String[] {
					"long", "long", "long[]", "long", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String", "long", "long", "boolean",
					"java.lang.String", "long", "java.lang.String", "long", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"CalendarBookingLocalService", null, "deleteCalendarBookingInstance",
				new String[] {"com.liferay.calendar.model.CalendarBooking", "int", "boolean"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"CalendarBookingLocalService", null, "deleteCalendarBookingInstance",
				new String[] {"com.liferay.calendar.model.CalendarBooking", "int", "boolean", "boolean"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"CalendarBookingLocalService", null, "deleteCalendarBookingInstance",
				new String[] {"com.liferay.calendar.model.CalendarBooking", "long", "boolean"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"CalendarBookingLocalService", null, "deleteCalendarBookingInstance",
				new String[] {
					"com.liferay.calendar.model.CalendarBooking calendarBooking", "long", "boolean", "boolean"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"CalendarBookingLocalService", null, "deleteCalendarBookingInstance",
				new String[] {"long", "long", "boolean"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "CalendarBookingLocalServiceUtil", "addCalendarBooking",
				new String[] {
					"long", "long", "long[]", "long", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String", "long", "long", "boolean",
					"java.lang.String", "long", "java.lang.String", "long", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "CalendarBookingLocalServiceUtil", "deleteCalendarBookingInstance",
				new String[] {"com.liferay.calendar.model.CalendarBooking", "int", "boolean"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "CalendarBookingLocalServiceUtil", "deleteCalendarBookingInstance",
				new String[] {"com.liferay.calendar.model.CalendarBooking", "int", "boolean", "boolean"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "CalendarBookingLocalServiceUtil", "deleteCalendarBookingInstance",
				new String[] {"com.liferay.calendar.model.CalendarBooking", "long", "boolean"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "CalendarBookingLocalServiceUtil", "deleteCalendarBookingInstance",
				new String[] {
					"com.liferay.calendar.model.CalendarBooking calendarBooking", "long", "boolean", "boolean"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "CalendarBookingLocalServiceUtil", "deleteCalendarBookingInstance",
				new String[] {"long", "long", "boolean"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"CalendarBookingService", null, "addCalendarBooking",
				new String[] {
					"long", "long[]", "long", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String", "int", "int", "int", "int",
					"int", "int", "int", "int", "int", "int", "java.lang.String", "boolean", "java.lang.String", "long",
					"java.lang.String", "long", "java.lang.String", "com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"CalendarBookingService", null, "addCalendarBooking",
				new String[] {
					"long", "long[]", "long", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String", "long", "long", "boolean",
					"java.lang.String", "long", "java.lang.String", "long", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"CalendarBookingService", null, "invokeTransition",
				new String[] {"long", "int", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"CalendarBookingService", null, "updateRecurringCalendarBooking",
				new String[] {
					"long", "long", "long[]", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String", "long", "long", "boolean",
					"java.lang.String", "long", "java.lang.String", "long", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "CalendarBookingServiceUtil", "addCalendarBooking",
				new String[] {
					"long", "long[]", "long", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String", "int", "int", "int", "int",
					"int", "int", "int", "int", "int", "int", "java.lang.String", "boolean", "java.lang.String", "long",
					"java.lang.String", "long", "java.lang.String", "com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "CalendarBookingServiceUtil", "addCalendarBooking",
				new String[] {
					"long", "long[]", "long", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String", "long", "long", "boolean",
					"java.lang.String", "long", "java.lang.String", "long", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "CalendarBookingServiceUtil", "invokeTransition",
				new String[] {"long", "int", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "CalendarBookingServiceUtil", "updateRecurringCalendarBooking",
				new String[] {
					"long", "long", "long[]", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String", "long", "long", "boolean",
					"java.lang.String", "long", "java.lang.String", "long", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordLocalService", null, "addRecord",
				new String[] {
					"long", "long", "long", "int", "java.util.Map<String, java.io.Serializable>",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordLocalService", null, "deleteRecordLocale",
				new String[] {"long", "java.util.Locale", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordLocalService", null, "getLatestRecordVersion", new String[] {"long"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordLocalService", null, "getRecordVersion", new String[] {"long"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordLocalService", null, "getRecordVersion", new String[] {"long", "java.lang.String"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordLocalService", null, "getRecordVersions",
				new String[] {
					"long", "int", "int",
					"com.liferay.portal.kernel.util.OrderByComparator" +
						"<com.liferay.dynamic.data.lists.model.DDLRecordVersion>"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordLocalService", null, "getRecordVersionsCount", new String[] {"long"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordLocalService", null, "revertRecordVersion",
				new String[] {"long", "long", "java.lang.String", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordLocalServiceUtil", "addRecord",
				new String[] {
					"long", "long", "long", "int", "java.util.Map<String, java.io.Serializable>",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordLocalServiceUtil", "deleteRecordLocale",
				new String[] {"long", "java.util.Locale", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordLocalServiceUtil", "getLatestRecordVersion", new String[] {"long"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordLocalServiceUtil", "getRecordVersion", new String[] {"long"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordLocalServiceUtil", "getRecordVersion", new String[] {"long", "java.lang.String"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordLocalServiceUtil", "getRecordVersions",
				new String[] {
					"long", "int", "int",
					"com.liferay.portal.kernel.util.OrderByComparator" +
						"<com.liferay.dynamic.data.lists.model.DDLRecordVersion>"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordLocalServiceUtil", "getRecordVersionsCount", new String[] {"long"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordLocalServiceUtil", "revertRecordVersion",
				new String[] {"long", "long", "java.lang.String", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordService", null, "addRecord",
				new String[] {
					"long", "long", "int", "java.util.Map<String, java.io.Serializable>",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordService", null, "deleteRecordLocale",
				new String[] {"long", "java.util.Locale", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordService", null, "revertRecordVersion",
				new String[] {"long", "java.lang.String", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordService", null, "updateRecord",
				new String[] {
					"long", "int", "java.util.Map<java.lang.String, java.io.Serializable>", "boolean",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordServiceUtil", "addRecord",
				new String[] {
					"long", "long", "int", "java.util.Map<String, java.io.Serializable>",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordServiceUtil", "deleteRecordLocale",
				new String[] {"long", "java.util.Locale", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordServiceUtil", "revertRecordVersion",
				new String[] {"long", "java.lang.String", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordServiceUtil", "updateRecord",
				new String[] {
					"long", "int", "java.util.Map<java.lang.String, java.io.Serializable>", "boolean",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDLRecordSetLocalService", null, "addRecordSetResources",
				new String[] {
					"com.liferay.dynamic.data.lists.model.DDLRecordSet", "java.lang.String[]", "java.lang.String[]"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDLRecordSetLocalServiceUtil", "addRecordSetResources",
				new String[] {
					"com.liferay.dynamic.data.lists.model.DDLRecordSet", "java.lang.String[]", "java.lang.String[]"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMFormInstanceLocalService", null, "addFormInstanceResources",
				new String[] {
					"com.liferay.dynamic.data.mapping.model.DDMFormInstance", "java.lang.String[]", "java.lang.String[]"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDMFormInstanceLocalServiceUtil", "addFormInstanceResources",
				new String[] {
					"com.liferay.dynamic.data.mapping.model.DDMFormInstance", "java.lang.String[]", "java.lang.String[]"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMStructureLayoutLocalService", null, "addStructureLayout",
				new String[] {
					"long", "long", "long", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDMStructureLayoutLocalServiceUtil", "addStructureLayout",
				new String[] {
					"long", "long", "long", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMStructureLocalService", null, "addStructure",
				new String[] {
					"long", "long", "long", "long", "java.lang.String",
					"java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String", "java.lang.String", "int",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMStructureLocalService", null, "addStructure",
				new String[] {
					"long", "long", "long", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMStructureLocalService", null, "addStructure",
				new String[] {
					"long", "long", "java.lang.String", "long", "java.lang.String",
					"java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String", "java.lang.String", "int",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMStructureLocalService", null, "updateStructure",
				new String[] {
					"long", "long", "long", "java.lang.String", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMStructureLocalService", null, "updateStructure",
				new String[] {
					"long", "long", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				"DDMStructureLocalService", null, "updateXSD",
				new String[] {"long", "String", "com.liferay.portal.kernel.service.ServiceContext"}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDMStructureLocalServiceUtil", "addStructure",
				new String[] {
					"long", "long", "long", "long", "java.lang.String",
					"java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String", "java.lang.String", "int",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDMStructureLocalServiceUtil", "addStructure",
				new String[] {
					"long", "long", "long", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDMStructureLocalServiceUtil", "addStructure",
				new String[] {
					"long", "long", "java.lang.String", "long", "java.lang.String",
					"java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String", "java.lang.String", "int",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDMStructureLocalServiceUtil", "updateStructure",
				new String[] {
					"long", "long", "long", "java.lang.String", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDMStructureLocalServiceUtil", "updateStructure",
				new String[] {
					"long", "long", "java.util.Map<java.util.Locale, java.lang.String>",
					"java.util.Map<java.util.Locale, java.lang.String>", "java.lang.String",
					"com.liferay.portal.kernel.service.ServiceContext"
				}));

		searchResults.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DDMStructureLocalServiceUtil", "updateXSD",
				new String[] {"long", "String", "com.liferay.portal.kernel.service.ServiceContext"}));

		return searchResults;
	}

}