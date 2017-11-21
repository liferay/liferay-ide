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

package com.liferay.ide.kaleo.core.op;

import com.liferay.ide.kaleo.core.model.Assignable;
import com.liferay.ide.kaleo.core.model.AssignmentType;
import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.RoleName;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.User;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;

/**
 * @author Gregory Amerson
 */
public interface AssignableOp extends Assignable {

	public ElementType TYPE = new ElementType(AssignableOp.class);

	public Value<AssignmentType> getAssignmentType();

	public Value<ScriptLanguageType> getDefaultScriptLanguage();

	public Role getImpliedRole();

	public Scriptable getImpliedScriptable();

	public User getImpliedUser();

	public ElementList<RoleName> getRoleNames();

	public void setAssignmentType(AssignmentType value);

	public void setAssignmentType(String value);

	public void setDefaultScriptLanguage(ScriptLanguageType scriptLanguage);

	public void setDefaultScriptLanguage(String scriptLanguage);

	@DefaultValue(text = "CREATOR")
	@Required
	@Type(base = AssignmentType.class)
	public ValueProperty PROP_ASSIGNMENT_TYPE = new ValueProperty(TYPE, "AssignmentType");

	@DefaultValue(text = "groovy")
	@Label(standard = "default &script type")
	@Type(base = ScriptLanguageType.class)
	public ValueProperty PROP_DEFAULT_SCRIPT_LANGUAGE = new ValueProperty(TYPE, "DefaultScriptLanguage");

	@Label(standard = "roles")
	@Type(base = Role.class)
	public ImpliedElementProperty PROP_IMPLIED_ROLE = new ImpliedElementProperty(TYPE, "ImpliedRole");

	@Type(base = Scriptable.class)
	public ImpliedElementProperty PROP_IMPLIED_SCRIPTABLE = new ImpliedElementProperty(TYPE, "ImpliedScriptable");

	@Label(standard = "user")
	@Type(base = User.class)
	public ImpliedElementProperty PROP_IMPLIED_USER = new ImpliedElementProperty(TYPE, "ImpliedUser");

	@Type(base = RoleName.class)
	public ListProperty PROP_ROLE_NAMES = new ListProperty(TYPE, "RoleNames");

}