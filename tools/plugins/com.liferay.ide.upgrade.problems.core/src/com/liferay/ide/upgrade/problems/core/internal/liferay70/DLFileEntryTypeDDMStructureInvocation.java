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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.plan.tasks.core.SearchResult;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java,jsp,jspf", "problem.title=DLFileEntryTypeLocalServiceUtil Api Changes",
	"problem.section=#removed-the-dlfileentrytypesddmstructures-mapping-table",
	"problem.summary=Removed the DLFileEntryTypes_DDMStructures Mapping Table", "problem.tickets=LPS-56660",
	"implName=DLFileEntryTypeDDMStructureInvocation", "version=7.0"
},
	service = FileMigrator.class)
public class DLFileEntryTypeDDMStructureInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> result = new ArrayList<>();

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