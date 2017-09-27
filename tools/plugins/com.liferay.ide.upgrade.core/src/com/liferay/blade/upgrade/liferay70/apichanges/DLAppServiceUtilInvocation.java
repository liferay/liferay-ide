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
			"problem.title=Moved Recycle Bin Logic Into a New DLTrashService Interface",
			"problem.section=#moved-recycle-bin-logic-into-a-new-dltrashservice-interface",
			"problem.summary=Moved Recycle Bin Logic Into a New DLTrashService Interface",
			"problem.tickets=LPS-60810",
			"implName=DLAppServiceUtilInvocation"
		},
		service = FileMigrator.class
	)

public class DLAppServiceUtilInvocation extends JavaFileMigrator{

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> result = new ArrayList<SearchResult>();

        result.addAll(
            javaFileChecker.findMethodInvocations(
                null, "DLAppServiceUtil", "moveFileEntryFromTrash", null));

        result.addAll(
            javaFileChecker.findMethodInvocations(
                null, "DLAppServiceUtil", "moveFileEntryToTrash", null ) );

        result.addAll(
            javaFileChecker.findMethodInvocations(
                null, "DLAppServiceUtil", "moveFileShortcutFromTrash", null));

        result.addAll(
            javaFileChecker.findMethodInvocations(
                null, "DLAppServiceUtil", "moveFileShortcutToTrash", null) );

        result.addAll(
            javaFileChecker.findMethodInvocations(
                null, "DLAppServiceUtil", "moveFolderFromTrash", null) );

        result.addAll(
                javaFileChecker.findMethodInvocations(
                    null, "DLAppServiceUtil", "moveFolderToTrash", null) );

        result.addAll(
                javaFileChecker.findMethodInvocations(
                    null, "DLAppServiceUtil", "restoreFileEntryFromTrash", null) );

        result.addAll(
                javaFileChecker.findMethodInvocations(
                    null, "DLAppServiceUtil", "restoreFileShortcutFromTrash", null) );

        result.addAll(
                javaFileChecker.findMethodInvocations(
                    null, "DLAppServiceUtil", "restoreFolderFromTrash", null) );

        result.addAll(
                javaFileChecker.findMethodInvocations(
                    null, "DLAppLocalServiceUtil", "moveFileEntryToTrash", null) );

        result.addAll(
                javaFileChecker.findMethodInvocations(
                    null, "DLAppLocalServiceUtil", "restoreFileEntryFromTrash", null) );

        return result;
	}

}
