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

package com.liferay.ide.kaleo.core.model;

import com.liferay.ide.kaleo.core.model.internal.CurrentAssignmentsDerviedValueService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
public interface Assignable extends Element {

	public ElementType TYPE = new ElementType(Assignable.class);

	public Value<String> getCurrentAssignments();

	public ElementList<ResourceAction> getResourceActions();

	public ElementList<Role> getRoles();

	public ElementHandle<Scriptable> getScriptedAssignment();

	public ElementHandle<User> getUser();

	@Derived
	@Label(standard = "current assignments")
	@ReadOnly
	@Service(impl = CurrentAssignmentsDerviedValueService.class)
	public ValueProperty PROP_CURRENT_ASSIGNMENTS = new ValueProperty(TYPE, "CurrentAssignments");

	@Label(standard = "resource actions")
	@Type(base = ResourceAction.class)
	@XmlListBinding(
		mappings = {@XmlListBinding.Mapping(element = "resource-action", type = ResourceAction.class)},
		path = "assignments/resource-actions"
	)
	public ListProperty PROP_RESOURCE_ACTIONS = new ListProperty(TYPE, "ResourceActions");

	@Label(standard = "roles")
	@Type(base = Role.class)
	@XmlListBinding(
		mappings = {@XmlListBinding.Mapping(element = "role", type = Role.class)}, path = "assignments/roles"
	)
	public ListProperty PROP_ROLES = new ListProperty(TYPE, "Roles");

	@Label(standard = "scripted assignment")
	@Type(base = Scriptable.class)
	@XmlElementBinding(
		mappings = {@XmlElementBinding.Mapping(element = "scripted-assignment", type = Scriptable.class)},
		path = "assignments"
	)
	public ElementProperty PROP_SCRIPTED_ASSIGNMENT = new ElementProperty(TYPE, "ScriptedAssignment");

	@Label(standard = "user")
	@Type(base = User.class)
	@XmlElementBinding(
		mappings = {@XmlElementBinding.Mapping(element = "user", type = User.class)}, path = "assignments"
	)
	public ElementProperty PROP_USER = new ElementProperty(TYPE, "User");

}