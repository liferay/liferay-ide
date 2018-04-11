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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.test.core.base.BaseTests;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class CoreUtilTests extends BaseTests {

	@Test
	public void compareVersions() throws Exception {
		Assert.assertEquals(0, CoreUtil.compareVersions(ILiferayConstants.V601, ILiferayConstants.V601));
		Assert.assertEquals(1, CoreUtil.compareVersions(ILiferayConstants.V620, ILiferayConstants.V610));
		Assert.assertEquals(-1, CoreUtil.compareVersions(ILiferayConstants.V610, ILiferayConstants.V620));
	}

}