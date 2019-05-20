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

package com.liferay.ide.upgrade.problems.test.apichanges72;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

/**
 * @author Seiphon Wang
 */
public class RemoveLinkApplicationURLsTest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 4;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay72.RemoveLinkApplicationURLs";
	}

	@Override
	public File getTestFile() {
		return new File("jsptests/link-application-urls/general.jsp");
	}

}
