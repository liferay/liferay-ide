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

package com.liferay.ide.maven.core.tests.legacy;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.core.model.Profile;

import org.eclipse.sapphire.ElementList;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class NewLiferayPluginProjectProfileTests {

	@Test
	public void testProfiles() throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectProvider("maven");

		Assert.assertEquals(_getter.get(op.getActiveProfilesValue()), null);

		op.setActiveProfilesValue("foo,bar");

		Assert.assertEquals("foo,bar", _getter.get(op.getActiveProfilesValue()));

		ElementList<Profile> selectedProfiles = op.getSelectedProfiles();

		Assert.assertEquals(selectedProfiles.size(), 0, selectedProfiles.size());

		ElementList<NewLiferayProfile> liferayProfiles = op.getNewLiferayProfiles();

		Assert.assertEquals(liferayProfiles.size(), 0, liferayProfiles.size());
	}

	@Test
	public void testSelectProfiles() throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectProvider("maven");

		ElementList<Profile> profiles = op.getSelectedProfiles();

		Assert.assertEquals(profiles.size(), 0, profiles.size());
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {
	};

}