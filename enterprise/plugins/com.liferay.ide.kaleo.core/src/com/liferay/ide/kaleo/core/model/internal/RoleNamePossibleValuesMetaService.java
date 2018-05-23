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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.model.Assignable;
import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.RoleName;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.op.AssignableOp;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.services.Service;

/**
 * @author Kuo Zhang
 */
public class RoleNamePossibleValuesMetaService extends Service {

	protected String[] getRoleNames() {
		Set<String> retval = new TreeSet<>();

		for (Entry<String, Integer> roleName : _additionalRoleNames.entrySet()) {
			Integer nameVlue = roleName.getValue();

			if (nameVlue.intValue() > 0) {
				retval.add(roleName.getKey());
			}
		}

		retval.addAll(_originalRoleNames);

		return retval.toArray(new String[0]);
	}

	protected void initIfNecessary(Object object) {
		if (object instanceof WorkflowDefinition) {
			for (WorkflowNode node : ((WorkflowDefinition)object).getDiagramNodes()) {
				Assignable assignable = node.nearest(Assignable.class);

				if (assignable != null) {
					for (Role role : assignable.getRoles()) {
						Value<String> roleName = role.getName();

						String name = roleName.content(false);

						if (!CoreUtil.isNullOrEmpty(name)) {
							_originalRoleNames.add(name);
						}
					}
				}
			}
		}
		else if (object instanceof AssignableOp) {
			ElementList<RoleName> roleNames = ((AssignableOp)object).getRoleNames();

			for (RoleName roleName : roleNames) {
				Value<String> nameValue = roleName.getName();

				String name = nameValue.content(false);

				if (!CoreUtil.isNullOrEmpty(name)) {
					_originalRoleNames.add(name);
				}
			}
		}
	}

	protected void updateRoleNames(String previousRoleName, String currentRoleName) {
		if ((previousRoleName != null) && (currentRoleName != null) && previousRoleName.equals(currentRoleName)) {
			return;
		}

		boolean needsBroadcast = false;

		if (!CoreUtil.isNullOrEmpty(previousRoleName) && !_originalRoleNames.contains(previousRoleName)) {
			Integer preRoleNameValue = _additionalRoleNames.get(previousRoleName);

			int times = _additionalRoleNames.containsKey(previousRoleName) ? preRoleNameValue.intValue() : 0;

			if (times >= 1) {
				_additionalRoleNames.put(previousRoleName, Integer.valueOf(--times));
			}

			needsBroadcast = times == 0;
		}

		if (!CoreUtil.isNullOrEmpty(currentRoleName) && !_originalRoleNames.contains(currentRoleName)) {
			Integer curRoleNameValue = _additionalRoleNames.get(currentRoleName);

			int times = _additionalRoleNames.containsKey(currentRoleName) ? curRoleNameValue.intValue() : 0;

			_additionalRoleNames.put(currentRoleName, Integer.valueOf(++times));

			needsBroadcast = needsBroadcast ? true : (times == 1);
		}

		if (needsBroadcast) {
			broadcast();
		}
	}

	private static final Set<String> _originalRoleNames = new TreeSet<>();

	static {
		_originalRoleNames.add("Administrator");
		_originalRoleNames.add("Organization Administrator");
		_originalRoleNames.add("Organization Content Reviewer");
		_originalRoleNames.add("Organization Owner");
		_originalRoleNames.add("Portal Content Reviewer");
		_originalRoleNames.add("Site Administrator");
		_originalRoleNames.add("Site Content Reviewer");
		_originalRoleNames.add("Site Owner");
	}

	private final Map<String, Integer> _additionalRoleNames = new HashMap<>();

}