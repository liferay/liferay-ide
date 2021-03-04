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

package com.liferay.ide.upgrade.problems.core.internal.liferay70;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf", "problem.title=DLFileEntryTypeLocalServiceUtil Api Changes",
		"problem.section=#removed-the-dlfileentrytypesddmstructures-mapping-table",
		"problem.summary=Removed the DLFileEntryTypes_DDMStructures Mapping Table", "problem.tickets=LPS-56660",
		"version=7.0"
	},
	service = FileMigrator.class
)
public class DLFileEntryTypeDDMStructureInvocation extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<FileSearchResult> result = new ArrayList<>();

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil", "addDDMStructureDLFileEntryType", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil", "addDDMStructureDLFileEntryTypes", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil", "clearDDMStructureDLFileEntryTypes", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil", "deleteDDMStructureDLFileEntryType", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil", "deleteDDMStructureDLFileEntryTypes", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil", "getDDMStructureDLFileEntryTypes", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil", "getDDMStructureDLFileEntryTypesCount", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil", "hasDDMStructureDLFileEntryType", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil", "hasDDMStructureDLFileEntryTypes", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil", "setDDMStructureDLFileEntryTypes", null));

		return result;
	}

}