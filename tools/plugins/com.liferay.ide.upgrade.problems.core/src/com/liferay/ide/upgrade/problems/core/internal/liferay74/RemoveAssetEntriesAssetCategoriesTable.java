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

package com.liferay.ide.upgrade.problems.core.internal.liferay74;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf", "problem.tickets=LPS-89065",
		"problem.title=Remove AssetEntries_AssetCategories table and related code",
		"problem.summary=The AssetEntries_AssetCategories table and its corresponding code have been removed from the portal",
		"problem.section=#removed-assetentries-assetcatagories-table", "version=7.4"
	},
	service = FileMigrator.class
)
public class RemoveAssetEntriesAssetCategoriesTable extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile fileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		searchResults.add(fileChecker.findImport("com.liferay.asset.kernel.model.AssetEntries_AssetCategoriesTable"));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "addAssetEntryAssetCategory",
				new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "addAssetEntryAssetCategory",
				new String[] {"long", "AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "addAssetEntryAssetCategories",
				new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "addAssetEntryAssetCategories",
				new String[] {"long", "List<AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "clearAssetEntryAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "deleteAssetEntryAssetCategory",
				new String[] {"long", "AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "deleteAssetEntryAssetCategory",
				new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "deleteAssetEntryAssetCategories",
				new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "deleteAssetEntryAssetCategories",
				new String[] {"long", "List<AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "getAssetEntryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "getAssetEntryAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "getAssetEntryAssetCategories",
				new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "getAssetEntryAssetCategories",
				new String[] {"long", "int", "int", "OrderByComparator<AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "getAssetEntryAssetCategoriesCount", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "hasAssetEntryAssetCategory",
				new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "hasAssetEntryAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "setAssetEntryAssetCategories",
				new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "getAssetEntryLocalService", null));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "setAssetEntryLocalService",
				new String[] {"com.liferay.asset.kernel.service.AssetEntryLocalService"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "getAssetEntryPersistence", null));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "setAssetEntryPersistence",
				new String[] {"AssetEntryPersistence"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations("AssetCategoryLocalServiceBaseImpl", null, "getAssetEntryFinder", null));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceBaseImpl", null, "setAssetEntryFinder", new String[] {"AssetEntryFinder"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations("AssetCategoryServiceBaseImpl", null, "getAssetEntryLocalService", null));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryServiceBaseImpl", null, "setAssetEntryLocalService",
				new String[] {"com.liferay.asset.kernel.service.AssetEntryLocalService"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations("AssetCategoryServiceBaseImpl", null, "getAssetEntryService", null));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryServiceBaseImpl", null, "setAssetEntryService",
				new String[] {"com.liferay.asset.kernel.service.AssetEntryService"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations("AssetCategoryServiceBaseImpl", null, "getAssetEntryPersistence", null));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryServiceBaseImpl", null, "setAssetEntryPersistence",
				new String[] {"AssetEntryPersistence"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations("AssetCategoryServiceBaseImpl", null, "getAssetEntryFinder", null));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryServiceBaseImpl", null, "setAssetEntryFinder", new String[] {"AssetEntryFinder"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "addAssetCategoryAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "addAssetCategoryAssetEntry",
				new String[] {"long", "AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "addAssetCategoryAssetEntries",
				new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "addAssetCategoryAssetEntries",
				new String[] {"long", "List<AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "clearAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "deleteAssetCategoryAssetEntry",
				new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "deleteAssetCategoryAssetEntry",
				new String[] {"long", "AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "deleteAssetCategoryAssetEntries",
				new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "deleteAssetCategoryAssetEntries",
				new String[] {"long", "List<AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "getAssetCategoryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "getAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "getAssetCategoryAssetEntries",
				new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "getAssetCategoryAssetEntries",
				new String[] {"long", "int", "int", "OrderByComparator<AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "getAssetCategoryAssetEntriesCount", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "hasAssetCategoryAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "hasAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceBaseImpl", null, "setAssetCategoryAssetEntries",
				new String[] {"long", "long[]"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "getAssetEntryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "getAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "getAssetEntries", new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "getAssetEntries",
				new String[] {"long", "int", "int", "OrderByComparator<com.liferay.asset.kernel.model.AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "getAssetEntriesSize", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "containsAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "containsAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "addAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "addAssetEntry",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "addAssetEntries", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "addAssetEntries",
				new String[] {"long", "List<com.liferay.asset.kernel.model.AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "clearAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "removeAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "removeAssetEntry",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "removeAssetEntries", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "removeAssetEntries",
				new String[] {"long", "List<com.liferay.asset.kernel.model.AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "setAssetEntries", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistenceImpl", null, "setAssetEntries",
				new String[] {"long", "List<com.liferay.asset.kernel.model.AssetEntry>"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "getAssetCategoryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "getAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "getAssetCategories", new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "getAssetCategories",
				new String[] {
					"long", "int", "int", "OrderByComparator<com.liferay.asset.kernel.model.AssetCategory>"
				}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "getAssetCategoriesSize", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "containsAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "containsAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "addAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "addAssetCategory",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "addAssetCategories", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "addAssetCategories",
				new String[] {"long", "List<com.liferay.asset.kernel.model.AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "clearAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "removeAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "removeAssetCategory",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "removeAssetCategories", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "removeAssetCategories",
				new String[] {"long", "List<com.liferay.asset.kernel.model.AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "setAssetCategories", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistenceImpl", null, "setAssetCategories",
				new String[] {"long", "List<com.liferay.asset.kernel.model.AssetCategory>"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "addAssetEntryAssetCategories",
				new String[] {"long", "List<AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "addAssetEntryAssetCategories", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "addAssetEntryAssetCategory",
				new String[] {"long", "AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "addAssetEntryAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "clearAssetEntryAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "deleteAssetEntryAssetCategories",
				new String[] {"long", "List<AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "deleteAssetEntryAssetCategories", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "deleteAssetEntryAssetCategory",
				new String[] {"long", "AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "deleteAssetEntryAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "getAssetEntryAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "getAssetEntryAssetCategories",
				new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "getAssetEntryAssetCategories",
				new String[] {"long", "int", "int", "OrderByComparator<AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "getAssetEntryAssetCategoriesCount", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "getAssetEntryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "getAssetEntryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "hasAssetEntryAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "hasAssetEntryAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalService", null, "setAssetEntryAssetCategories", new String[] {"long", "long[]"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "addAssetEntryAssetCategories",
				new String[] {"long", "java.util.List<com.liferay.asset.kernel.model.AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "addAssetEntryAssetCategories",
				new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "addAssetEntryAssetCategory",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "addAssetEntryAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "clearAssetEntryAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "deleteAssetEntryAssetCategories",
				new String[] {"long", "java.util.List<com.liferay.asset.kernel.model.AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "deleteAssetEntryAssetCategories",
				new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "deleteAssetEntryAssetCategory",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "deleteAssetEntryAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "getAssetEntryAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "getAssetEntryAssetCategories",
				new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "getAssetEntryAssetCategories",
				new String[] {
					"long", "int", "int",
					"com.liferay.portal.kernel.util.OrderByComparator<com.liferay.asset.kernel.model.AssetCategory>"
				}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "getAssetEntryAssetCategoriesCount", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "getAssetEntryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "hasAssetEntryAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "hasAssetEntryAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "setAssetEntryAssetCategories",
				new String[] {"long", "long[]"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "addAssetEntryAssetCategories",
				new String[] {"long", "java.util.List<AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "addAssetEntryAssetCategories",
				new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "addAssetEntryAssetCategory",
				new String[] {"long", "AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "addAssetEntryAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "clearAssetEntryAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "deleteAssetEntryAssetCategories",
				new String[] {"long", "java.util.List<AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "deleteAssetEntryAssetCategories",
				new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "deleteAssetEntryAssetCategory",
				new String[] {"long", "AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "deleteAssetEntryAssetCategory",
				new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "getAssetEntryAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "getAssetEntryAssetCategories",
				new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "getAssetEntryAssetCategories",
				new String[] {
					"long", "int", "int", "com.liferay.portal.kernel.util.OrderByComparator<AssetCategory>"
				}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "getAssetEntryAssetCategoriesCount", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "getAssetEntryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "hasAssetEntryAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "hasAssetEntryAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryLocalServiceWrapper", null, "setAssetEntryAssetCategories",
				new String[] {"long", "long[]"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "addAssetCategoryAssetEntries",
				new String[] {"long", "List<AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "addAssetCategoryAssetEntries", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "addAssetCategoryAssetEntry", new String[] {"long", "AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "addAssetCategoryAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "clearAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "deleteAssetCategoryAssetEntries",
				new String[] {"long", "List<AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "deleteAssetCategoryAssetEntries", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "deleteAssetCategoryAssetEntry", new String[] {"long", "AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "deleteAssetCategoryAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "getAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "getAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "getAssetCategoryAssetEntries", new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "getAssetCategoryAssetEntries",
				new String[] {"long", "int", "int", "OrderByComparator<AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "getAssetCategoryPrimaryKeys",
				new String[] {"long", "int", "int", "OrderByComparator<AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "hasAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "hasAssetCategoryAssetEntry", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalService", null, "setAssetCategoryAssetEntries", new String[] {"long", "long[]"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "addAssetCategoryAssetEntries",
				new String[] {"long", "java.util.List<com.liferay.asset.kernel.model.AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "addAssetCategoryAssetEntries", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "addAssetCategoryAssetEntry",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "addAssetCategoryAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "clearAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "deleteAssetCategoryAssetEntries",
				new String[] {"long", "java.util.List<com.liferay.asset.kernel.model.AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "deleteAssetCategoryAssetEntries",
				new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "deleteAssetCategoryAssetEntry",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "deleteAssetCategoryAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "getAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "getAssetCategoryAssetEntries",
				new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "getAssetCategoryAssetEntries",
				new String[] {"long", "int", "int", "com.liferay.portal.kernel.util.OrderByComparator"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "getAssetCategoryAssetEntriesCount", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "getAssetCategoryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "hasAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "hasAssetCategoryAssetEntry", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryLocalServiceUtil", "setAssetCategoryAssetEntries", new String[] {"long", "long[]"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "addAssetCategoryAssetEntries",
				new String[] {"long", "java.util.List<AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "addAssetCategoryAssetEntries",
				new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "addAssetCategoryAssetEntry",
				new String[] {"long", "AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "addAssetCategoryAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "clearAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "deleteAssetCategoryAssetEntries",
				new String[] {"long", "java.util.List<AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "deleteAssetCategoryAssetEntries",
				new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "deleteAssetCategoryAssetEntry",
				new String[] {"long", "AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "deleteAssetCategoryAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "getAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "getAssetCategoryAssetEntries",
				new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "getAssetCategoryAssetEntries",
				new String[] {"long", "int", "int", "com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "getAssetCategoryAssetEntriesCount", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "getAssetCategoryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "hasAssetCategoryAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "hasAssetCategoryAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryLocalServiceWrapper", null, "setAssetCategoryAssetEntries", new String[] {"long", "long"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "getAssetEntryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "getAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "getAssetEntries", new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "getAssetEntries",
				new String[] {"long", "int", "int", "com.liferay.portal.kernel.util.OrderByComparator"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "getAssetEntriesSize", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "containsAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "containsAssetEntries", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "addAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "addAssetEntry",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "addAssetEntries", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "addAssetEntries",
				new String[] {"long", "java.util.List<com.liferay.asset.kernel.model.AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "clearAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "removeAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "removeAssetEntry",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "removeAssetEntries", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "removeAssetEntries",
				new String[] {"long", "java.util.List<com.liferay.asset.kernel.model.AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "setAssetEntries", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetCategoryPersistence", null, "setAssetEntries",
				new String[] {"long", "java.util.List<com.liferay.asset.kernel.model.AssetEntry>"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "getAssetEntryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "AssetCategoryUtil", "getAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "getAssetEntries", new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "getAssetEntries",
				new String[] {"long", "int", "int", "OrderByComparator<com.liferay.asset.kernel.model.AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "AssetCategoryUtil", "getAssetEntriesSize", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "containsAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "containsAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "addAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "addAssetEntry",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "addAssetEntries", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "addAssetEntries",
				new String[] {"long", "List<com.liferay.asset.kernel.model.AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "AssetCategoryUtil", "clearAssetEntries", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "removeAssetEntry", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "removeAssetEntry",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetEntry"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "removeAssetEntries", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "removeAssetEntries",
				new String[] {"long", "List<com.liferay.asset.kernel.model.AssetEntry>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "setAssetEntries", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetCategoryUtil", "setAssetEntries",
				new String[] {"long", "List<com.liferay.asset.kernel.model.AssetEntry>"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "getAssetCategoryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "getAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "getAssetCategories", new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "getAssetCategories",
				new String[] {"long", "int", "int", "com.liferay.portal.kernel.util.OrderByComparator"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "getAssetCategoriesSize", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "containsAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "containsAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "addAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "addAssetCategory",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "addAssetCategories", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "addAssetCategories",
				new String[] {"long", "java.util.List<com.liferay.asset.kernel.model.AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "clearAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "removeAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "removeAssetCategory",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "removeAssetCategories", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "removeAssetCategories",
				new String[] {"long", "java.util.List<com.liferay.asset.kernel.model.AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "setAssetCategories", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"AssetEntryPersistence", null, "setAssetCategories",
				new String[] {"long", "java.util.List<com.liferay.asset.kernel.model.AssetCategory>"}));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "getAssetCategoryPrimaryKeys", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "AssetEntryUtil", "getAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "getAssetCategories", new String[] {"long", "int", "int"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "getAssetCategories",
				new String[] {
					"long", "int", "int", "OrderByComparator<com.liferay.asset.kernel.model.AssetCategory>"
				}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "AssetEntryUtil", "getAssetCategoriesSize", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "containsAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "containsAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "addAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "addAssetCategory",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "addAssetCategories", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "addAssetCategory",
				new String[] {"long", "List<com.liferay.asset.kernel.model.AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "AssetEntryUtil", "clearAssetCategories", new String[] {"long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "removeAssetCategory", new String[] {"long", "long"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "removeAssetCategory",
				new String[] {"long", "com.liferay.asset.kernel.model.AssetCategory"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "removeAssetCategories", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "removeAssetCategories",
				new String[] {"long", "List<com.liferay.asset.kernel.model.AssetCategory>"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "setAssetCategories", new String[] {"long", "long[]"}));
		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, "AssetEntryUtil", "setAssetCategories",
				new String[] {"long", "List<com.liferay.asset.kernel.model.AssetCategory>"}));

		return searchResults;
	}

}