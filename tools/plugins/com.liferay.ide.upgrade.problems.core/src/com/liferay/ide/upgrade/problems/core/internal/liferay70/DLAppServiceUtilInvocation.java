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
	"file.extensions=java,jsp,jspf", "problem.title=Moved Recycle Bin Logic Into a New DLTrashService Interface",
	"problem.section=#moved-recycle-bin-logic-into-a-new-dltrashservice-interface",
	"problem.summary=Moved Recycle Bin Logic Into a New DLTrashService Interface", "problem.tickets=LPS-60810",
	"implName=DLAppServiceUtilInvocation", "version=7.0"
},
	service = FileMigrator.class)
public class DLAppServiceUtilInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> result = new ArrayList<>();

		result.addAll(javaFileChecker.findMethodInvocations(null, "DLAppServiceUtil", "moveFileEntryFromTrash", null));

		result.addAll(javaFileChecker.findMethodInvocations(null, "DLAppServiceUtil", "moveFileEntryToTrash", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(null, "DLAppServiceUtil", "moveFileShortcutFromTrash", null));

		result.addAll(javaFileChecker.findMethodInvocations(null, "DLAppServiceUtil", "moveFileShortcutToTrash", null));

		result.addAll(javaFileChecker.findMethodInvocations(null, "DLAppServiceUtil", "moveFolderFromTrash", null));

		result.addAll(javaFileChecker.findMethodInvocations(null, "DLAppServiceUtil", "moveFolderToTrash", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(null, "DLAppServiceUtil", "restoreFileEntryFromTrash", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(null, "DLAppServiceUtil", "restoreFileShortcutFromTrash", null));

		result.addAll(javaFileChecker.findMethodInvocations(null, "DLAppServiceUtil", "restoreFolderFromTrash", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(null, "DLAppLocalServiceUtil", "moveFileEntryToTrash", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(null, "DLAppLocalServiceUtil", "restoreFileEntryFromTrash", null));

		return result;
	}

}