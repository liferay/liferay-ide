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

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.op.AssignableOp;

import java.util.Set;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class RoleNamePossibleValuesService extends PossibleValuesService {

	@Override
	public void dispose() {
		_metaService.updateRoleNames(_previousRoleName, StringPool.EMPTY);

		if ((_op() != null) && !_op().disposed()) {
			Value<String> opName = _op().getName();

			opName.detach(_roleNameListener);
		}

		_metaService.detach(_metaRoleNamesListener);

		_previousRoleName = null;

		_currentRoleName = null;

		_roleNameListener = null;

		_metaRoleNamesListener = null;

		_metaService = null;

		super.dispose();
	}

	@Override
	protected void compute(Set<String> values) {
		for (String roleName : _metaService.getRoleNames()) {
			values.add(roleName);
		}
	}

	@Override
	protected void initPossibleValuesService() {
		invalidValueSeverity = Status.Severity.OK;
		_metaService = context().service(RoleNamePossibleValuesMetaService.class);
		_previousRoleName = StringPool.EMPTY;
		_currentRoleName = StringPool.EMPTY;

		_initMetaServiceIfNecessary();

		_metaRoleNamesListener = new FilteredListener<Event>() {

			@Override
			protected void handleTypedEvent(Event event) {
				if (!context(Role.class).disposed()) {
					try {
						refresh();
					}
					catch (Exception e) {
					}
				}
			}

		};

		_roleNameListener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				Property property = event.property();

				Element element = property.element();

				if (!element.disposed()) {
					Role role = element.nearest(Role.class);

					Value<String> roleName = role.getName();

					_currentRoleName = roleName.content();

					_metaService.updateRoleNames(_previousRoleName, _currentRoleName);
					_previousRoleName = _currentRoleName;
				}
			}

		};

		_metaService.attach(_metaRoleNamesListener);

		Value<String> opName = _op().getName();

		opName.attach(_roleNameListener);

		super.initPossibleValuesService();
	}

	private void _initMetaServiceIfNecessary() {
		WorkflowDefinition definition = _op().nearest(WorkflowDefinition.class);

		if (definition != null) {
			_metaService.initIfNecessary(definition);
		}

		AssignableOp assignableOp = _op().nearest(AssignableOp.class);

		if (assignableOp != null) {
			_metaService.initIfNecessary(assignableOp);
		}
	}

	private Role _op() {
		return context(Role.class);
	}

	private String _currentRoleName;
	private Listener _metaRoleNamesListener;
	private RoleNamePossibleValuesMetaService _metaService;
	private String _previousRoleName;
	private Listener _roleNameListener;

}