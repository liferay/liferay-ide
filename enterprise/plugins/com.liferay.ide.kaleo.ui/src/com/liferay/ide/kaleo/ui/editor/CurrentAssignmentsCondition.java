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

package com.liferay.ide.kaleo.ui.editor;

import com.liferay.ide.kaleo.core.model.Assignable;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireCondition;

/**
 * @author Gregory Amerson
 */
public class CurrentAssignmentsCondition extends SapphireCondition {

	@Override
	protected boolean evaluate() {
		boolean retval = false;

		Assignable assignable = _op();

		boolean hasUser = false;

		if (assignable.getUser().content(false) != null) {
			hasUser = true;
		}

		boolean hasScript = false;

		if (assignable.getScriptedAssignment().content(false) != null) {
			hasScript = true;
		}

		boolean hasRoles = false;

		if (assignable.getRoles().size() > 0) {
			hasRoles = true;
		}

		boolean hasResourceActions = false;

		if (assignable.getResourceActions().size() > 0) {
			hasResourceActions = true;
		}

		if (hasUser) {
			retval = "user".equals(_parameter) || "creator".equals(_parameter);
		}
		else if (hasScript) {
			retval = "script".equals(_parameter);
		}
		else if (hasRoles) {
			retval = "role".equals(_parameter) || "roles".equals(_parameter);
		}
		else if (hasResourceActions) {
			retval = "resources".equals(_parameter);
		}

		return retval;
	}

	@Override
	protected void initCondition(ISapphirePart part, String parameter) {
		super.initCondition(part, parameter);

		_parameter = parameter;

		Assignable assignable = _op();

		Listener assignmentTypeListener = new FilteredListener<PropertyContentEvent>() {

			@Override
			public void handleTypedEvent(PropertyContentEvent event) {
				updateConditionState();
			}

		};

		assignable.attach(assignmentTypeListener, "*");

		updateConditionState();
	}

	private Assignable _op() {
		Element element = getPart().getLocalModelElement();

		return element.nearest(Assignable.class);
	}

	private String _parameter;

}