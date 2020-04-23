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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.project.core.InitProviderPossibleValuesService;

import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Terry Jia
 * @author Seiphon Wang
 */
public class FragmentProjectProviderPossibleValuesService
	extends PossibleValuesService implements InitProviderPossibleValuesService {

	@Override
	public boolean ordered() {
		return true;
	}

	@Override
	protected void compute(Set<String> values) {
		values.addAll(_possibleValues);
	}

	protected void initPossibleValuesService() {
		_possibleValues = initPossibleValues("module-fragment");
	}

	private List<String> _possibleValues;

}