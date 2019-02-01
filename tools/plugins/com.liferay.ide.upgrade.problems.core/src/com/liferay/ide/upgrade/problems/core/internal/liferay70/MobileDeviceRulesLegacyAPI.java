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

package com.liferay.ide.upgrade.problems.core.internal.liferay70;

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java,jsp,jspf",
	"problem.summary=All Mobile Device Rules APIs previously exposed as Liferay Portal API in 6.2 have been move ou" +
		"t from portal-service into separate OSGi modules",
	"problem.tickets=LPS-57519", "problem.title=Mobile Device Rules APIs migrated to OSGi module",
	"problem.section=#legacy", "implName=MobileDeviceRulesLegacyAPI", "version=7.0"
},
	service = FileMigrator.class)
public class MobileDeviceRulesLegacyAPI extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findServiceAPIs(_SERVICE_API_PREFIXES);
	}

	private static final String[] _SERVICE_API_PREFIXES = {
		"com.liferay.portlet.mobiledevicerules.service.MDRAction",
		"com.liferay.portlet.mobiledevicerules.service.MDRRuleGroupInstance",
		"com.liferay.portlet.mobiledevicerules.service.MDRRuleGroup"
	};

}