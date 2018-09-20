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

package com.liferay.ide.project.core.service;

import com.liferay.ide.core.util.WorkspaceConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Joye Luo
 */
public class TargetLiferayVersionPossibleValuesService extends PossibleValuesService {

	@Override
	protected void compute(Set<String> values) {
		List<String> possibleValues = Arrays.asList(WorkspaceConstants.LIFERAY_VERSIONS);

		values.addAll(possibleValues);
	}

}