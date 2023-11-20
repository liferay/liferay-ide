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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.SapphireContentAccessor;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;

/**
 * @author Simon Jiang
 */
public class TargetPlatformDefaultValueService extends DefaultValueService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		super.dispose();

		PossibleValuesService possibleValuesService = _possibleValuesService();

		possibleValuesService.detach(_listener);
	}

	@Override
	protected String compute() {
		return _defaultValue;
	}

	@Override
	protected void initDefaultValueService() {
		super.initDefaultValueService();

		PossibleValuesService possibleValuesService = _possibleValuesService();

		_listener = new Listener() {

			@Override
			public void handle(Event event) {
				Set<String> values = possibleValuesService.values();

				if (!values.isEmpty()) {
					Iterator<String> iterator = values.iterator();

					_defaultValue = iterator.next();

					refresh();
				}
			}

		};

		possibleValuesService.attach(_listener);
	}

	private PossibleValuesService _possibleValuesService() {
		NewLiferayWorkspaceOp op = context(NewLiferayWorkspaceOp.class);

		Value<Object> property = op.property(NewLiferayWorkspaceOp.PROP_TARGET_PLATFORM);

		return property.service(PossibleValuesService.class);
	}

	private String _defaultValue = null;
	private Listener _listener;

}