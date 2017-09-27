
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

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=java",
		"problem.title=MBMessageService API Changes",
		"problem.section=#removed-permissionclassname-permissionclasspk-and-permissionowner-parameter",
		"problem.summary=Removed permissionClassName, permissionClassPK, and permissionOwner Parameters from MBMessage API",
		"problem.tickets=LPS-55877",
		"implName=MBMessageServiceUtilInvocation"
	},
	service = FileMigrator.class
)
public class MBMessageServiceUtilInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
	    final List<SearchResult> result = new ArrayList<SearchResult>();

        result.addAll( javaFileChecker.findMethodInvocations(null,
            "MBMessageServiceUtil", "addDiscussionMessage", null) ) ;

        result.addAll( javaFileChecker.findMethodInvocations(null,
            "MBMessageServiceUtil", "deleteDiscussionMessage", null) ) ;

        result.addAll( javaFileChecker.findMethodInvocations(null,
            "MBMessageServiceUtil", "updateDiscussionMessage", null) ) ;

		return result;
	}

}
