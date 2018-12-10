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

package com.liferay.ide.upgrade.task.problem.upgrade.liferay70.apichanges;

import com.liferay.ide.upgrade.task.problem.api.FileMigrator;
import com.liferay.ide.upgrade.task.problem.api.JavaFile;
import com.liferay.ide.upgrade.task.problem.api.SearchResult;
import com.liferay.ide.upgrade.task.problem.upgrade.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java", "problem.title=StorageAdapter API Changes",
	"problem.summary=Removed Operations That Used the Fields Class from the StorageAdapter Interface",
	"problem.tickets=LPS-53021", "problem.type=java",
	"problem.section=#removed-operations-that-used-the-fields-class-from-the-storageadapter-inter",
	"implName=StorageAdapterCreateUpdateMethods", "version=7.0"
},
	service = FileMigrator.class)
public class StorageAdapterCreateUpdateMethods extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> searchResults = new ArrayList<>();

		List<SearchResult> invocations = javaFileChecker.findMethodInvocations(
			null, "StorageEngineUtil", "create", new String[] {"long", "long", "Fields", "ServiceContext"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "StorageEngineUtil", "update", new String[] {"long", "Fields", "boolean", "ServiceContext"});

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(
			null, "StorageEngineUtil", "update", new String[] {"long", "Fields", "ServiceContext"});

		searchResults.addAll(invocations);

		return searchResults;
	}

}