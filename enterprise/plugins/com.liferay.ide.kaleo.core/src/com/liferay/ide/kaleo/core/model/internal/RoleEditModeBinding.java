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

import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.RoleEditMode;
import com.liferay.ide.kaleo.core.model.RoleType;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author Gregory Amerson
 */
public class RoleEditModeBinding extends XmlValueBindingImpl {

	@Override
	public String read() {
		if (_localMode == null) {
			Role role = role();

			Value<Integer> id = role.getRoleId();

			Integer roleId = id.content();

			if (roleId != null) {
				return RoleEditMode.BY_ROLE_ID.toString();
			}

			return RoleEditMode.BY_NAME.toString();
		}
		else {
			return _localMode.toString();
		}
	}

	@Override
	public void write(String value) {
		String modeName = RoleEditMode.BY_NAME.toString();

		if (modeName.equals(value)) {
			_localMode = RoleEditMode.BY_NAME;
			role().setRoleId((String)null);
		}
		else {
			_localMode = RoleEditMode.BY_ROLE_ID;
			role().setName(null);
			role().setRoleType((RoleType)null);
			role().setAutoCreate((Boolean)null);
		}
	}

	protected Role role() {
		return property().nearest(Role.class);
	}

	private RoleEditMode _localMode = null;

}