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

package com.liferay.ide.core.tests;

import com.liferay.ide.core.util.VersionUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Seiphon Wang
 */
public class VersionUtilTest {

	@Test
	public void testSimplifyTargetPlatformVersion() throws Exception {
		Assert.assertEquals("7.3.2", VersionUtil.simplifyTargetPlatformVersion("7.3.2"));

		Assert.assertEquals("7.1.10.1", VersionUtil.simplifyTargetPlatformVersion("7.1.10.1"));

		Assert.assertEquals("7.3.2.1", VersionUtil.simplifyTargetPlatformVersion("7.3.2-1"));

		Assert.assertEquals("7.2.10.1", VersionUtil.simplifyTargetPlatformVersion("7.2.10.ga1"));

		Assert.assertEquals("7.3.10.5", VersionUtil.simplifyTargetPlatformVersion("7.3.10.ep5"));

		Assert.assertEquals("7.2.10.1", VersionUtil.simplifyTargetPlatformVersion("7.2.10.fp1-1"));

		Assert.assertEquals("7.4.13.1", VersionUtil.simplifyTargetPlatformVersion("7.4.13.u1"));
	}
}
