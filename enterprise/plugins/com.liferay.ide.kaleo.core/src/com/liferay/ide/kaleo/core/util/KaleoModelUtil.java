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

package com.liferay.ide.kaleo.core.util;

import com.liferay.ide.kaleo.core.model.ResourceAction;
import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.RoleType;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.User;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.internal.Point;
import com.liferay.ide.kaleo.core.op.AssignableOp;

import java.lang.reflect.Field;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.annotations.EnumSerialization;

/**
 * @author Gregory Amerson
 */
public class KaleoModelUtil {

	public static final Point DEFAULT_POINT = new Point(-1, -1);

	public static void changeTaskAssignments(Task task, AssignableOp op) {
		if ((task == null) || (op == null)) {
			return;
		}

		ElementHandle<User> user = task.getUser();

		user.clear();

		ElementHandle<Scriptable> scripteAssignment = task.getScriptedAssignment();

		scripteAssignment.clear();

		ElementList<ResourceAction> resourceAction = task.getResourceActions();

		resourceAction.clear();

		ElementList<Role> taskRole = task.getRoles();

		taskRole.clear();

		switch (op.getAssignmentType().content(true)) {
			case CREATOR:
				user.content(true);

				break;

			case USER:
				User content = user.content(true);

				content.copy(op.getImpliedUser());

				break;

			case ROLE:
				final Role newRole = taskRole.insert();

				newRole.copy(op.getImpliedRole());

				break;

			case ROLE_TYPE:
				for (Role role : op.getRoles()) {
					Role newRoleType = taskRole.insert();

					newRoleType.copy(role);

					Value<RoleType> roleType = role.getRoleType();

					newRoleType.setRoleType(roleType.content(true));

					Value<Boolean> autoCreate = role.getAutoCreate();

					if (autoCreate.content() != null) {
						newRoleType.setAutoCreate(autoCreate.content());
					}
				}

				break;

			case SCRIPTED_ASSIGNMENT:

				Scriptable scriptable = scripteAssignment.content(true);

				Scriptable impliedScriptable = op.getImpliedScriptable();

				Value<ScriptLanguageType> scriptLanguageType = impliedScriptable.getScriptLanguage();

				scriptable.setScriptLanguage(scriptLanguageType.content(true));

				scriptable.setScript("/*specify script assignment */");

				break;

			case RESOURCE_ACTIONS:
				for (ResourceAction ra : op.getResourceActions()) {
					ResourceAction newResourceAction = resourceAction.insert();

					newResourceAction.copy(ra);
				}

				break;
		}
	}

	public static String getDefaultValue(Element modelElement, QualifiedName key, Enum<?> defaultValue) {
		String value = null;

		IFile definitionFile = null;

		WorkflowDefinition workflowDefinition = modelElement.nearest(WorkflowDefinition.class);

		if (workflowDefinition == null) {
			workflowDefinition = modelElement.adapt(WorkflowDefinition.class);
		}

		if (workflowDefinition != null) {
			definitionFile = workflowDefinition.adapt(IFile.class);
		}

		if (definitionFile != null) {
			try {
				value = definitionFile.getPersistentProperty(key);
			}
			catch (CoreException ce) {
			}
		}

		if (value == null) {
			value = getEnumSerializationAnnotation(defaultValue);
		}

		return value;
	}

	public static String getEnumSerializationAnnotation(Enum<?> type) {
		try {
			Class<?> typeClass = type.getClass();

			Field field = typeClass.getField(type.name());

			EnumSerialization enumAnnotation = field.getAnnotation(EnumSerialization.class);

			return enumAnnotation.primary();
		}
		catch (Exception e) {
			return null;
		}
	}

}