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

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java", "problem.title=MBMessageService API Changes",
	"problem.section=#removed-permissionclassname-permissionclasspk-and-permissionowner-parameter",
	"problem.summary=Removed permissionClassName, permissionClassPK, and permissionOwner Parameters from MBMessage API",
	"problem.tickets=LPS-55877", "version=7.0"
},
	service = FileMigrator.class)
public class MBMessageServiceUtilInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> result = new ArrayList<>();

		result.addAll(
			javaFileChecker.findMethodInvocations(null, "MBMessageServiceUtil", "addDiscussionMessage", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(null, "MBMessageServiceUtil", "deleteDiscussionMessage", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(null, "MBMessageServiceUtil", "updateDiscussionMessage", null));

		return result;
	}

}