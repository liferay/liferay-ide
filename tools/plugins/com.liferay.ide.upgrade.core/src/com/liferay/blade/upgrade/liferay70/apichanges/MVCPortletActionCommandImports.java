/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.liferay70.ImportStatementMigrator;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.summary=The classes from package com.liferay.util.bridges.mvc in util-bridges.jar were moved to a new package com.liferay.portal.kernel.portlet.bridges.mvc in portal-service.jar.",
		"problem.tickets=LPS-50156",
		"problem.title=Moved MVCPortlet, ActionCommand and ActionCommandCache from util-bridges.jar to portal-service.jar",
		"problem.section=#moved-mvcportlet-actioncommand-and-actioncommandcache-from-util-bridges-jar",
		"auto.correct=import",
		"implName=MVCPortletActionCommandImports"
	},
	service = {
		AutoMigrator.class,
		FileMigrator.class
	}
)
public class MVCPortletActionCommandImports extends ImportStatementMigrator {

	private final static String[] IMPORTS = new String[] {
			"com.liferay.util.bridges.mvc.ActionCommand",
			"com.liferay.util.bridges.mvc.BaseActionCommand",
			"com.liferay.util.bridges.mvc.MVCPortlet"
	};

	private final static String[] IMPORTS_FIXED = new String[] {
			"com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand",
			"com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand",
			"com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet"
	};

	private final static Map<String, String> importFixes;

	static {
		importFixes = new HashMap<>();

		for (int i = 0; i < IMPORTS.length; i++) {
			importFixes.put(IMPORTS[i], IMPORTS_FIXED[i]);
		}
	}
	public MVCPortletActionCommandImports() {
		super(importFixes);
	}

}