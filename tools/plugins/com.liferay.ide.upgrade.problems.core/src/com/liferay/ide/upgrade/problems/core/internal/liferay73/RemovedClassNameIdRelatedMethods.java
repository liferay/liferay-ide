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
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.title=Removed classNameId related methods from DDM Persistence classes",
		"problem.summary=Removed classNameId related methods from DDM Persistence classes",
		"problem.section=#removed-class-name-related-methods", "problem.tickets=LPS-108525", "problem.version=7.3",
		"version=7.3"
	},
	service = FileMigrator.class
)
public class RemovedClassNameIdRelatedMethods extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile fileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructureLinkPersistence", null, "findByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructureLinkPersistence", null, "findByClassNameId_First", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructureLinkPersistence", null, "fetchByClassNameId_First", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructureLinkPersistence", null, "findByClassNameId_Last", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructureLinkPersistence", null, "fetchByClassNameId_Last", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"DDMStructureLinkPersistence", null, "findByClassNameId_PrevAndNext", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructureLinkPersistence", null, "removeByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructureLinkPersistence", null, "countByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureLinkUtil", "findByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureLinkUtil", "findByClassNameId_First", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureLinkUtil", "fetchByClassNameId_First", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureLinkUtil", "findByClassNameId_Last", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureLinkUtil", "fetchByClassNameId_Last", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureLinkUtil", "findByClassNameId_PrevAndNext", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureLinkUtil", "removeByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureLinkUtil", "countByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructurePersistence", null, "findByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructurePersistence", null, "findByClassNameId_First", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructurePersistence", null, "fetchByClassNameId_First", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructurePersistence", null, "findByClassNameId_Last", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructurePersistence", null, "fetchByClassNameId_Last", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructurePersistence", null, "findByClassNameId_PrevAndNext", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructurePersistence", null, "removeByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMStructurePersistence", null, "countByClassNameId", null));

		searchResults.addAll(fileChecker.findMethodInvocations(null, "DDMStructureUtil", "findByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureUtil", "findByClassNameId_First", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureUtil", "fetchByClassNameId_First", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureUtil", "findByClassNameId_Last", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureUtil", "fetchByClassNameId_Last", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMStructureUtil", "findByClassNameId_PrevAndNext", null));

		searchResults.addAll(fileChecker.findMethodInvocations(null, "DDMStructureUtil", "removeByClassNameId", null));

		searchResults.addAll(fileChecker.findMethodInvocations(null, "DDMStructureUtil", "countByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMTemplateLinkPersistence", null, "findByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMTemplateLinkPersistence", null, "findByClassNameId_First", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMTemplateLinkPersistence", null, "fetchByClassNameId_First", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMTemplateLinkPersistence", null, "findByClassNameId_Last", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMTemplateLinkPersistence", null, "fetchByClassNameId_Last", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				"DDMTemplateLinkPersistence", null, "findByClassNameId_PrevAndNext", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMTemplateLinkPersistence", null, "removeByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("DDMTemplateLinkPersistence", null, "countByClassNameId", null));

		searchResults.addAll(fileChecker.findMethodInvocations(null, "DDMTemplateLinkUtil", "findByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMTemplateLinkUtil", "findByClassNameId_First", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMTemplateLinkUtil", "fetchByClassNameId_First", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMTemplateLinkUtil", "findByClassNameId_Last", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMTemplateLinkUtil", "fetchByClassNameId_Last", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMTemplateLinkUtil", "findByClassNameId_PrevAndNext", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMTemplateLinkUtil", "removeByClassNameId", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "DDMTemplateLinkUtil", "countByClassNameId", null));

		return searchResults;
	}

}