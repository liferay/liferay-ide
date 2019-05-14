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

package com.liferay.ide.upgrade.problems.test.apichanges;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class DeprecatedExpandoCustomFieldTagsAutoCorrectTest extends AutoCorrectJSPTagTestBase {

	@Override
	public List<String> getCheckPoints() {
		List<String> checkPoints = new ArrayList<>();

		checkPoints.add("58,<liferay-expando:custom-attributes-available className=\"<%= Foo.class.getName() %>\">");

		checkPoints.add(
			"59,<liferay-expando:custom-attribute-list className=\"<%= Foo.class.getName() %>\" classPK=" +
				"\"<%= (foo != null) ? foo.getFooId() : 0 %>\" editable=\"<%= true %>\" label=\"<%= true %>\">" +
					"</liferay-expando:custom-attribute-list>");

		checkPoints.add(
			"59,<liferay-expando:custom-attribute-list className=\"<%= Foo.class.getName() %>\"" +
				"classPK=\"<%= (foo != null) ? foo.getFooId() : 0 %>" +
					"\" editable=\"<%= true %>\" label=\"<%= true %>\">" + "</liferay-expando:custom-attribute-list>");

		checkPoints.add("60,</liferay-expando:custom-attributes-available>");

		return Collections.singletonList(
			"58,<liferay-expando:custom-attributes-available className=\"<%= Foo.class.getName() %>\">");
	}

	public int getExpectedFixedNumber() {
		return 2;
	}

	public int getExpectedNumber() {
		return 2;
	}

	@Override
	public String getImplClassName() {
		return "DeprecatedExpandoCustomFieldTags";
	}

	@Override
	public File getOriginalTestFile() {
		return new File("jsptests/liferay-ui-custom/LiferayUICustom.jsp");
	}

}