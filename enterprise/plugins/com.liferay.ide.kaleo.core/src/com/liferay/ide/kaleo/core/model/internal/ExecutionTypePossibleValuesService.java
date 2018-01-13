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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.kaleo.core.model.ExecutionType;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Gregory Amerson
 */
public class ExecutionTypePossibleValuesService extends PossibleValuesService {

	public static final String ON_ASSIGNMENT = KaleoModelUtil.getEnumSerializationAnnotation(
		ExecutionType.ON_ASSIGNMENT);

	public static final String ON_ENTRY = KaleoModelUtil.getEnumSerializationAnnotation(ExecutionType.ON_ENTRY);

	public static final String ON_EXIT = KaleoModelUtil.getEnumSerializationAnnotation(ExecutionType.ON_EXIT);

	@Override
	protected void compute(Set<String> values) {
		values.add(ON_ENTRY);
		values.add(ON_EXIT);

		if (isInTaskHeirarchy()) {
			values.add(ON_ASSIGNMENT);
		}
	}

	protected boolean isInTaskHeirarchy() {
		if (_taskHeirarchy == null) {
			_taskHeirarchy = context().find(Task.class) != null;
		}

		return _taskHeirarchy;
	}

	private Boolean _taskHeirarchy = null;

}