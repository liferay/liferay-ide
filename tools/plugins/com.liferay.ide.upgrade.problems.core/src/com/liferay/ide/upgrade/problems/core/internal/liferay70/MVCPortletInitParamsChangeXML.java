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

import com.liferay.ide.upgrade.plan.tasks.core.SearchResult;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.XMLFile;
import com.liferay.ide.upgrade.problems.core.internal.XMLFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=xml", "problem.title=copy-request-parameters init-param default value change",
	"problem.summary=The copy-request-parameters init parameter's default value is now set to true in all portlets " +
		"that extend MVCPortlet.",
	"problem.tickets=LPS-54798",
	"problem.section=#changed-the-default-value-of-the-copy-request-parameters-init-parameter-for",
	"implName=MVCPortletInitParamsChangeXML", "version=7.0"
},
	service = FileMigrator.class)
public class MVCPortletInitParamsChangeXML extends XMLFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, XMLFile xmlFileChecker) {

		// check if it is portlet.xml file

		if (!"portlet.xml".equals(file.getName())) {
			return Collections.emptyList();
		}

		List<SearchResult> results = new ArrayList<>();

		results.addAll(xmlFileChecker.findElement("portlet-class", "com.liferay.util.bridges.mvc.MVCPortlet"));

		results.addAll(
			xmlFileChecker.findElement("portlet-class", "com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet"));

		results.addAll(xmlFileChecker.findElement("name", "copy-request-parameters"));

		return results;
	}

}