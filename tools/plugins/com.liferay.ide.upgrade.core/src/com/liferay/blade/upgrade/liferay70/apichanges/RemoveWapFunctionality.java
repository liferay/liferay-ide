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
		"file.extensions=java,jsp,jspf",
		"problem.summary=Removed the WAP Functionality",
		"problem.tickets=LPS-62920",
		"problem.title=Removed the WAP Functionality",
		"problem.section=#removed-the-wap-functionality",
		"implName=RemoveWapFunctionality"
	},
	service = FileMigrator.class
)
public class RemoveWapFunctionality extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> result = new ArrayList<SearchResult>();
		
		result.addAll(javaFileChecker.findMethodInvocations(null, "LayoutLocalServiceUtil", "updateLookAndFeel", null));
		
		result.addAll(javaFileChecker.findMethodInvocations(null, "LayoutRevisionLocalServiceUtil", "addLayoutRevision",
				null));
		
		result.addAll(javaFileChecker.findMethodInvocations(null, "LayoutRevisionLocalServiceUtil",
				"updateLayoutRevision", null));
		
		result.addAll(
				javaFileChecker.findMethodInvocations(null, "LayoutRevisionServiceUtil", "addLayoutRevision", null));
		
		result.addAll(javaFileChecker.findMethodInvocations(null, "LayoutServiceUtil", "updateLookAndFeel", null));
		
		result.addAll(
				javaFileChecker.findMethodInvocations(null, "LayoutSetLocalServiceUtil", "updateLookAndFeel", null));
		
		result.addAll(javaFileChecker.findMethodInvocations(null, "LayoutSetServiceUtil", "updateLookAndFeel", null));
		
		result.addAll(javaFileChecker.findMethodInvocations(null, "ThemeLocalServiceUtil", "getColorScheme", null));
		
		result.addAll(
				javaFileChecker.findMethodInvocations(null, "ThemeLocalServiceUtil", "getControlPanelThemes", null));
		
		result.addAll(javaFileChecker.findMethodInvocations(null, "ThemeLocalServiceUtil", "getPageThemes", null));
		
		result.addAll(javaFileChecker.findMethodInvocations(null, "ThemeLocalServiceUtil", "getTheme", null));
		
		return result;
	}
}