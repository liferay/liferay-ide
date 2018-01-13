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

import com.liferay.ide.kaleo.core.model.WorkflowDefinition;

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.Version;

/**
 * @author Gregory Amerson
 */
public class NotificationTransportValuesService extends PossibleValuesService {

	@Override
	protected void compute(Set<String> values) {
		values.add("email");
		values.add("im");
		values.add("private-message");

		Value<Version> schemaVersion = context(WorkflowDefinition.class).getSchemaVersion();

		final Version version = schemaVersion.content();

		if (_v62.compareTo(version) <= 0) {
			values.add("user-notification");
		}
	}

	private static final Version _v62 = new Version("6.2");

}