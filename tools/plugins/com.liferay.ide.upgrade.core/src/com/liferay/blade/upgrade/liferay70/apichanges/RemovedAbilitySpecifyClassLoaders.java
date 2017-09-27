/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.upgrade.liferay70.apichanges;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

@Component(
	property = {
		"file.extensions=java",
		"problem.title=Removed the Ability to Specify Class Loaders in Scripting",
		"problem.summary=Removed the Ability to Specify Class Loaders in Scripting.",
		"problem.tickets=LPS-63180",
		"problem.section=#removed-the-ability-to-specify-class-loaders-in-scripting",
		"implName=RemovedAbilitySpecifyClassLoaders"
	},
	service = FileMigrator.class
)
public class RemovedAbilitySpecifyClassLoaders extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> result = new ArrayList<SearchResult>();
		
		List<SearchResult> findImplementsInterfaces = javaFileChecker.findImplementsInterface("ScriptingExecutor");
		if (findImplementsInterfaces.size()>0){
			result.addAll(findImplementsInterfaces);	
		}

		SearchResult findImportResult = javaFileChecker.findImport("com.liferay.portal.kernel.scripting.Scripting");
		if (findImportResult!=null){
			result.add(findImportResult);	
		}

		return result;
	}
}
