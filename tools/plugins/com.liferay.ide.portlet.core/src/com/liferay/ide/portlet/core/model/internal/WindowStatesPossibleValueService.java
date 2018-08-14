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

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.portlet.core.model.CustomWindowState;
import com.liferay.ide.portlet.core.model.PortletApp;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.ElementDisposeEvent;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class WindowStatesPossibleValueService extends PossibleValuesService {

	// provided by Portlet Specification

	@Override
	protected void compute(Set<String> values) {
		if (!_initialized) {
			_readPriorToInit = true;
		}

		values.addAll(_values);
	}

	@Override
	protected void initPossibleValuesService() {
		super.initPossibleValuesService();

		PortletApp portletApp = context(PortletApp.class);

		Listener listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				_refreshValues();
			}

		};

		portletApp.attach(listener, PortletApp.PROP_CUSTOM_WINDOW_STATES.name());

		_refreshValues();

		portletApp.attach(
			new FilteredListener<ElementDisposeEvent>() {

				@Override
				protected void handleTypedEvent(ElementDisposeEvent event) {
					portletApp.detach(listener, PortletApp.PROP_CUSTOM_WINDOW_STATES.name());
				}

			});

		_initialized = true;
	}

	private void _refreshValues() {
		PortletApp portletApp = context(PortletApp.class);

		if ((portletApp != null) && !portletApp.disposed()) {
			Set<String> newValues = new TreeSet<>();

			for (String state : _DEFAULT_STATES) {
				newValues.add(state);
			}

			List<CustomWindowState> customWindowStates = portletApp.getCustomWindowStates();

			for (CustomWindowState iCustomWindowState : customWindowStates) {
				String customWindowState = SapphireUtil.getText(iCustomWindowState.getWindowState());

				if (customWindowState != null) {
					newValues.add(customWindowState);
				}
			}

			if (!_values.equals(newValues)) {
				_values = Collections.unmodifiableSet(newValues);
			}

			if (_initialized || _readPriorToInit) {
				refresh();
			}
		}
	}

	private static final String[] _DEFAULT_STATES = {"maximized", "minimized", "normal"};

	private boolean _initialized;
	private boolean _readPriorToInit;
	private Set<String> _values = Collections.emptySet();

}