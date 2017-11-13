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

import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.ProjectCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Gregory Amerson
 */
public class PortletFrameworkPossibleValuesService extends PossibleValuesService {

	@Override
	public boolean ordered() {
		return true;
	}

	@Override
	protected void compute(Set<String> values) {
		values.addAll(_possibleValues);
	}

	@Override
	protected void initPossibleValuesService() {
		super.initPossibleValuesService();

		_possibleValues = new ArrayList<>();

		for (IPortletFramework portletFramework : ProjectCore.getPortletFrameworks()) {
			if (shouldAdd(portletFramework)) {
				_possibleValues.add(portletFramework.getShortName());
			}
		}
	}

	protected boolean shouldAdd(IPortletFramework framework) {
		return !framework.isAdvanced();
	}

	private List<String> _possibleValues;

}