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
import com.liferay.ide.project.core.model.Profile;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ProfilePossibleValuesTests {

	@Test
	public void testActiveProfilesValue() throws Exception {
		NewLiferayPluginProjectOp op = _newMavenProjectOp();

		Profile firstProfile = op.getSelectedProfiles().insert();

		String firstProfileId = "__first_profile__";

		firstProfile.setId(firstProfileId);

		Assert.assertEquals(firstProfileId, op.getActiveProfilesValue().content());

		Profile secondProfile = op.getSelectedProfiles().insert();

		String secondProfileId = "__second_profile__";

		secondProfile.setId(secondProfileId);

		Assert.assertEquals(firstProfileId + ',' + secondProfileId, op.getActiveProfilesValue().content());
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