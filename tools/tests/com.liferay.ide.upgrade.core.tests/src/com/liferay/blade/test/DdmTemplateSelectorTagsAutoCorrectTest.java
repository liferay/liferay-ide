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

package com.liferay.blade.test;

import com.liferay.blade.test.apichanges.AutoCorrectJSPTagTestBase;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class DdmTemplateSelectorTagsAutoCorrectTest extends AutoCorrectJSPTagTestBase {

	@Override
	public File getOriginalTestFile() {
		return new File("jsptests/ddm-template-selector/DdmTemplateSelectorTagsTest.jsp");
	}

	@Override
	public String getImplClassName() {
		return "DdmTemplateSelectorTags";
	}

	@Override
	public List<String> getCheckPoints() {
		return Collections.singletonList("74,className=\"<%= PortalUtil.getClassNameId(templateHandler.getClassName()) %>\"/>");
	}

}
