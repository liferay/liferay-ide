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

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.PropertiesFileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=properties", "problem.title=Email Signature Properties",
		"problem.summary=Removed Liferay Frontend Editor BBCode Web, Previously Known as Liferay BBCode Editor",
		"problem.tickets=LPS-48334",
		"problem.section=#removed-liferay-frontend-editor-bbcode-web-previously-known-as-liferay-bbco", "version=7.0"
	},
	service = FileMigrator.class
)
public class BBCodeProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("editor.wysiwyg.portal-web.docroot.html.portlet.message_boards.edit_message.bb_code.jsp");
	}

}