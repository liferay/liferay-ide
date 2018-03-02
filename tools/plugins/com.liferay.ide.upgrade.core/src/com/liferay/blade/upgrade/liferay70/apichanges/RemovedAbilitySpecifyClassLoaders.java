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
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;
import com.liferay.ide.core.util.ListUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java", "problem.title=Removed the Ability to Specify Class Loaders in Scripting",
	"problem.summary=Removed the Ability to Specify Class Loaders in Scripting.", "problem.tickets=LPS-63180",
	"problem.section=#removed-the-ability-to-specify-class-loaders-in-scripting",
	"implName=RemovedAbilitySpecifyClassLoaders"
},
	service = FileMigrator.class)
public class RemovedAbilitySpecifyClassLoaders extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> result = new ArrayList<>();

		List<SearchResult> findImplementsInterfaces = javaFileChecker.findImplementsInterface("ScriptingExecutor");

		if (ListUtil.isNotEmpty(findImplementsInterfaces)) {
			result.addAll(findImplementsInterfaces);
		}

		SearchResult findImportResult = javaFileChecker.findImport("com.liferay.portal.kernel.scripting.Scripting");

		if (findImportResult != null) {
			result.add(findImportResult);
		}

		return result;
	}

}