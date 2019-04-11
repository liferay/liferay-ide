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

package com.liferay.ide.project.core.model;

import com.liferay.ide.project.core.model.internal.LiferayVersionDefaultValueService;
import com.liferay.ide.project.core.model.internal.LiferayVersionPossibleValuesService;
import com.liferay.ide.project.core.model.internal.NewLiferayProfileIdDefaultValueService;
import com.liferay.ide.project.core.model.internal.NewLiferayProfileIdValidationService;
import com.liferay.ide.project.core.model.internal.NewLiferayProfileRuntimeValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;

/**
 * @author Gregory Amerson
 */
public interface NewLiferayProfile extends HasLiferayRuntime, Profile {

	public ElementType TYPE = new ElementType(NewLiferayProfile.class);

	public Value<String> getLiferayVersion();

	public Value<ProfileLocation> getProfileLocation();

	public void setLiferayVersion(String value);

	public void setProfileLocation(ProfileLocation value);

	public void setProfileLocation(String value);

	@Label(standard = "new profile id")
	@Services(
		value = {
			@Service(impl = NewLiferayProfileIdDefaultValueService.class),
			@Service(impl = NewLiferayProfileIdValidationService.class)
		}
	)
	public ValueProperty PROP_ID = new ValueProperty(TYPE, "Id");

	@Label(standard = "liferay version")
	@Required
	@Services(
		value = {
			@Service(
				impl = LiferayVersionPossibleValuesService.class,
				params = {
					@Service.Param(name = "groupId", value = "com.liferay.portal"),
					@Service.Param(name = "artifactId", value = "portal-service")
				}
			),
			@Service(impl = LiferayVersionDefaultValueService.class)
		}
	)
	public ValueProperty PROP_LIFERAY_VERSION = new ValueProperty(TYPE, "LiferayVersion");

	@DefaultValue(text = "projectPom")
	@Label(standard = "profile location")
	@Type(base = ProfileLocation.class)
	public ValueProperty PROP_PROFILE_LOCATION = new ValueProperty(TYPE, "ProfileLocation");

	@Service(impl = NewLiferayProfileRuntimeValidationService.class)
	public ValueProperty PROP_RUNTIME_NAME = new ValueProperty(TYPE, HasLiferayRuntime.PROP_RUNTIME_NAME);

}