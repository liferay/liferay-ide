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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/timer_16x16.png")
public interface TaskTimer extends TimerAction {

	public ElementType TYPE = new ElementType(TaskTimer.class);

	public ElementList<ResourceAction> getResourceActions();

	public ElementList<Role> getRoles();

	public Scriptable getScriptedAssignment();

	public User getUser();

	public Value<Boolean> isBlocking();

	public void setBlocking(Boolean value);

	public void setBlocking(String value);

	@Label(standard = "&blocking")
	@Type(base = Boolean.class)
	@XmlBinding(path = "blocking")
	public ValueProperty PROP_BLOCKING = new ValueProperty(TYPE, "Blocking");

	@Label(standard = "resource actions")
	@Type(base = ResourceAction.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "resource-action", type = ResourceAction.class),
		path = "reassignments/resource-actions"
	)
	public ListProperty PROP_RESOURCE_ACTIONS = new ListProperty(TYPE, "ResourceActions");

	@Label(standard = "roles")
	@Type(base = Role.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "role", type = Role.class), path = "reassignments/roles"
	)
	public ListProperty PROP_ROLES = new ListProperty(TYPE, "Roles");

	@Label(standard = "scripted assignment")
	@Type(base = Scriptable.class)
	@XmlElementBinding(path = "reassignments/scripted-assignment")
	public ImpliedElementProperty PROP_SCRIPTED_ASSIGNMENT = new ImpliedElementProperty(TYPE, "ScriptedAssignment");

	@Label(standard = "user")
	@Type(base = User.class)
	@XmlElementBinding(path = "reassignments/user")
	public ImpliedElementProperty PROP_USER = new ImpliedElementProperty(TYPE, "User");

}