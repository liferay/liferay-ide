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

import com.liferay.ide.kaleo.core.model.Assignable;
import com.liferay.ide.kaleo.core.model.ResourceAction;
import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.User;

import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;

/**
 * @author Gregory Amerson
 */
public class CurrentAssignmentsDerviedValueService extends DerivedValueService {

	@Override
	protected String compute() {
		StringBuilder data = new StringBuilder();

		Assignable assignable = context(Assignable.class);

		ElementHandle<User> assignableUser = assignable.getUser();

		User user = assignableUser.content(false);

		ElementList<Role> roles = assignable.getRoles();

		ElementHandle<Scriptable> scripteAssignment = assignable.getScriptedAssignment();

		Scriptable scriptable = scripteAssignment.content(false);

		ElementList<ResourceAction> resourceActions = assignable.getResourceActions();

		if (user != null) {
			Value<Integer> userId = user.getUserId();

			Value<String> userScreenName = user.getScreenName();

			Value<String> userEmailAddress = user.getEmailAddress();

			if (userId.content() != null) {
				data.append(userId.content() + ", ");
			}
			else if (userScreenName.content() != null) {
				data.append(userScreenName.content() + ", ");
			}
			else if (userEmailAddress.content() != null) {
				data.append(userEmailAddress.content() + ", ");
			}
			else {
				data.append("User: Asset Creator");
			}
		}

		if (!roles.isEmpty()) {
			data.append("Roles: ");

			for (Role role : roles) {
				Value<Integer> roleId = role.getRoleId();

				Value<String> roleName = role.getName();

				if (roleId.content() != null) {
					data.append(roleId.content() + ", ");
				}
				else {
					data.append(roleName.text() + ", ");
				}
			}
		}

		if (scriptable != null) {
			Value<String> script = scriptable.getScript();

			Value<ScriptLanguageType> scriptLanguageType = scriptable.getScriptLanguage();

			if (script.content() != null) {
				data.append("Script language: " + scriptLanguageType.content());
			}
		}

		if (!resourceActions.isEmpty()) {
			data.append("Resource actions: ");

			for (ResourceAction resourceAction : resourceActions) {
				Value<String> reAction = resourceAction.getResourceAction();

				if (reAction.content() != null) {
					data.append(reAction.content() + ", ");
				}
			}
		}

		String dataInfo = data.toString();

		return dataInfo.replaceAll(", $", "");
	}

	@Override
	protected void initDerivedValueService() {
		FilteredListener<PropertyContentEvent> listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			public void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		context(Assignable.class).attach(listener, "*");
	}

}