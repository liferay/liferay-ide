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
import com.liferay.ide.project.core.model.Profile;

import org.eclipse.sapphire.ElementList;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ProfilePossibleValuesTests {

	@Test
	public void testActiveProfilesValue() throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectProvider("maven");

		ElementList<Profile> profiles = op.getSelectedProfiles();

		Profile firstProfile = profiles.insert();

		String firstProfileId = "__first_profile__";

		firstProfile.setId(firstProfileId);

		Assert.assertEquals(firstProfileId, _getter.get(op.getActiveProfilesValue()));

		Profile secondProfile = profiles.insert();

		String secondProfileId = "__second_profile__";

		secondProfile.setId(secondProfileId);

		Assert.assertEquals(firstProfileId + ',' + secondProfileId, _getter.get(op.getActiveProfilesValue()));
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {
	};

}