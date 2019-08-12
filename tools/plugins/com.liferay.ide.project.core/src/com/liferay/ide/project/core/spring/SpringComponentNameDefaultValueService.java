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

package com.liferay.ide.project.core.spring;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Simon Jiang
 */
public class SpringComponentNameDefaultValueService extends DefaultValueService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		NewLiferaySpringProjectOp op = _op();

		if (op != null) {
			SapphireUtil.detachListener(op.property(NewLiferaySpringProjectOp.PROP_PROJECT_NAME), _listener);
		}

		super.dispose();
	}

	@Override
	protected String compute() {
		String retVal = "";

		NewLiferaySpringProjectOp op = _op();

		String projectName = get(op.getProjectName());

		if (projectName != null) {
			String className = _getClassName(projectName);

			if ((className.length() > 7) && className.endsWith("Portlet")) {
				className = className.substring(0, className.length() - 7);
			}

			retVal = className;
		}

		return retVal;
	}

	@Override
	protected void initDefaultValueService() {
		super.initDefaultValueService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferaySpringProjectOp op = _op();

		SapphireUtil.attachListener(op.property(NewLiferaySpringProjectOp.PROP_PROJECT_NAME), _listener);
	}

	private static String _capitalize(String s, char separator) {
		StringBuilder sb = new StringBuilder(s.length());

		sb.append(s);

		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);

			if ((i == 0) || (sb.charAt(i - 1) == separator)) {
				c = Character.toUpperCase(c);
			}

			sb.setCharAt(i, c);
		}

		return sb.toString();
	}

	private String _getCapitalizedName(String name) {
		name = name.replace('-', ' ');
		name = name.replace('.', ' ');

		return _capitalize(name, ' ');
	}

	private String _getClassName(String name) {
		name = _getCapitalizedName(name);

		return _removeChar(name, ' ');
	}

	private NewLiferaySpringProjectOp _op() {
		return context(NewLiferaySpringProjectOp.class);
	}

	private String _removeChar(String s, char c) {
		int y = s.indexOf(c);

		if (y == -1) {
			return s;
		}

		StringBuilder sb = new StringBuilder(s.length());

		int x = 0;

		while (x <= y) {
			sb.append(s.substring(x, y));

			x = y + 1;

			y = s.indexOf(c, x);
		}

		sb.append(s.substring(x));

		return sb.toString();
	}

	private FilteredListener<PropertyContentEvent> _listener;

}