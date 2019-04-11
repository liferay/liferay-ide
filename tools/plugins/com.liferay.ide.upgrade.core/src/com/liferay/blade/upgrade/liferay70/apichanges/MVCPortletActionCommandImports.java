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

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.ImportStatementMigrator;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.summary=The classes from package com.liferay.util.bridges.mvc in util-bridges.jar were moved to a new package com.liferay.portal.kernel.portlet.bridges.mvc in portal-service.jar.",
		"problem.tickets=LPS-50156",
		"problem.title=Moved MVCPortlet, ActionCommand and ActionCommandCache from util-bridges.jar to portal-service.jar",
		"problem.section=#moved-mvcportlet-actioncommand-and-actioncommandcache-from-util-bridges-jar",
		"auto.correct=import", "version=7.0"
	},
	service = {AutoMigrator.class, FileMigrator.class}
)
public class MVCPortletActionCommandImports extends ImportStatementMigrator {

	public MVCPortletActionCommandImports() {
		super(_importFixes);
	}

	private static final String[] _IMPORTS = {
		"com.liferay.util.bridges.mvc.ActionCommand", "com.liferay.util.bridges.mvc.BaseActionCommand",
		"com.liferay.util.bridges.mvc.MVCPortlet"
	};

	private static final String[] _IMPORTS_FIXED = {
		"com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand",
		"com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand",
		"com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet"
	};

	private static final Map<String, String> _importFixes;

	static {
		_importFixes = new HashMap<>();

		for (int i = 0; i < _IMPORTS.length; i++) {
			_importFixes.put(_IMPORTS[i], _IMPORTS_FIXED[i]);
		}
	}

}