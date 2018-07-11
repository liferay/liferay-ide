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

package com.liferay.ide.test.core.base.support;

import com.liferay.ide.test.core.base.action.EnvAction;

import org.junit.rules.ExternalResource;

/**
 * @author Terry Jia
 */
public class SupportBase extends ExternalResource {

	public SupportBase() {
		envAction = new EnvAction();
	}

	@Override
	public void after() {
		timestamp = 0;
	}

	public void before() {
		String tt = String.valueOf(System.currentTimeMillis());

		try {
			timestamp = Long.parseLong(tt.substring(6));
		}
		catch (NumberFormatException nfe) {
		}
	}

	public EnvAction envAction;

	protected long timestamp = 0;

}