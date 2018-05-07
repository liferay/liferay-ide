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

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class NewLiferayPluginProjectProfileTests {

	@Test
	public void testProfiles() throws Exception {
		NewLiferayPluginProjectOp op = _newMavenProjectOp();

		String emptyActiveProfilesValue = op.getActiveProfilesValue().content();

		Assert.assertEquals(emptyActiveProfilesValue, null);

		op.setActiveProfilesValue("foo,bar");

		Assert.assertEquals("foo,bar", op.getActiveProfilesValue().content());

		Assert.assertEquals(0, op.getSelectedProfiles().size());

		Assert.assertEquals(0, op.getNewLiferayProfiles().size());
	}

	@Test
	public void testSelectProfiles() throws Exception {
		NewLiferayPluginProjectOp op = _newMavenProjectOp();

		Assert.assertEquals(0, op.getSelectedProfiles().size());
	}

	protected NewLiferayPluginProjectOp newProjectOp() {
		return NewLiferayPluginProjectOp.TYPE.instantiate();
	}

	private NewLiferayPluginProjectOp _newMavenProjectOp() {
		NewLiferayPluginProjectOp op = newProjectOp();

		op.setProjectProvider("maven");

		return op;
	}

}