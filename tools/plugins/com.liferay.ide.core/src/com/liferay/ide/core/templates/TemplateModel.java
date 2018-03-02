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

package com.liferay.ide.core.templates;

import com.liferay.ide.core.util.ListUtil;

import freemarker.template.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class TemplateModel {

	public TemplateModel(
		String bundleId, Configuration config, String id, String resource, String templateFolder,
		TemplateVariable[] vars) {

		_bundleId = bundleId;
		_config = config;
		_id = id;
		_resource = resource;
		_templateFolder = templateFolder;
		_vars = vars;
	}

	public String getBundleId() {
		return _bundleId;
	}

	public Configuration getConfig() {
		return _config;
	}

	public String getId() {
		return _id;
	}

	public String[] getRequiredVarNames() {
		if (ListUtil.isEmpty(_vars)) {
			return new String[0];
		}

		List<String> reqVarNames = new ArrayList<>();

		for (TemplateVariable var : _vars) {
			if (var.isRequired()) {
				reqVarNames.add(var.getName());
			}
		}

		return reqVarNames.toArray(new String[0]);
	}

	public String getResource() {
		return _resource;
	}

	public String getTemplateFolder() {
		return _templateFolder;
	}

	public void setConfig(Configuration config) {
		_config = config;
	}

	private String _bundleId;
	private Configuration _config;
	private String _id;
	private String _resource;
	private String _templateFolder;
	private TemplateVariable[] _vars;

}