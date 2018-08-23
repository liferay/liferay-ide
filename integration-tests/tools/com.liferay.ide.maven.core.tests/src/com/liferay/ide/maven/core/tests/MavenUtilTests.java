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

package com.liferay.ide.maven.core.tests;

import com.liferay.ide.maven.core.MavenUtil;
import com.liferay.ide.test.core.base.BaseTests;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class MavenUtilTests extends BaseTests {

	@Test
	public void testMavenStyleMilestones() throws Exception {
		Assert.assertEquals("7.0.0", MavenUtil.getMajorMinorVersionOnly("7.0.0-m1"));
	}

	@Test
	public void testPortalEEStyle() throws Exception {
		Assert.assertEquals("6.2.0", MavenUtil.getMajorMinorVersionOnly("6.2.10.6"));
	}

}