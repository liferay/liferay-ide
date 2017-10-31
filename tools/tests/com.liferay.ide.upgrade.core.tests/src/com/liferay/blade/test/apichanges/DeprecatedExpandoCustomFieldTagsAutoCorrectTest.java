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

package com.liferay.blade.test.apichanges;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeprecatedExpandoCustomFieldTagsAutoCorrectTest extends AutoCorrectJSPTagTestBase {

	@Override
	public File getOriginalTestFile() {
		return new File("jsptests/liferay-ui-custom/LiferayUICustom.jsp");
	}

	@Override
	public String getImplClassName() {
		return "DeprecatedExpandoCustomFieldTags";
	}

	@Override
	public List<String> getCheckPoints() {
		List<String> checkPoints = new ArrayList<>();

		checkPoints.add("58,<liferay-expando:custom-attributes-available className=\"<%= Foo.class.getName() %>\">");
		checkPoints.add(
			"59,<liferay-expando:custom-attribute-list className=\"<%= Foo.class.getName() %>\" classPK=\"<%= (foo != null) ? foo.getFooId() : 0 %>\" editable=\"<%= true %>\" label=\"<%= true %>\"></liferay-expando:custom-attribute-list>"
		);
		checkPoints.add("60,</liferay-expando:custom-attributes-available>");

		return Collections.singletonList("58,<liferay-expando:custom-attributes-available className=\"<%= Foo.class.getName() %>\">");
	}

	public int getExpectedFixedNumber() {
		return 2;
	}

	public int getExpectedNumber() {
		return 2;
	}

}
