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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.project.core.model.NewLiferayProfile;

import java.util.Set;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 */
public class NewLiferayProfileIdValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		NewLiferayProfile newLiferayProfile = _profile();

		String profileId = get(newLiferayProfile.getId());

		if ((profileId == null) || profileId.isEmpty()) {
			retval = Status.createErrorStatus("Profile id can not be empty.");
		}
		else if (profileId.contains(StringPool.SPACE)) {
			retval = Status.createErrorStatus("No spaces are allowed in profile id.");
		}

		if (ListUtil.isEmpty(_existingValues)) {
			return retval;
		}

		for (String val : _existingValues) {
			if ((val != null) && val.equals(profileId)) {
				return Status.createErrorStatus("Profile already exists.");
			}
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		NewLiferayPluginProjectOp op = context(NewLiferayPluginProjectOp.class);

		_existingValues = NewLiferayPluginProjectOpMethods.getPossibleProfileIds(op, false);
	}

	private NewLiferayProfile _profile() {
		return context(NewLiferayProfile.class);
	}

	private Set<String> _existingValues;

}