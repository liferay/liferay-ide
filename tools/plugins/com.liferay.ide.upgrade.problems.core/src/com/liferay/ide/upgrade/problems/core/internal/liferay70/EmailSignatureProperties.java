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

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.PropertiesFileMigrator;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=properties", "problem.title=Email Signature Properties",
	"problem.summary=Merged Configured Email Signature Field into the Body of Email Messages from Message Boards and Wiki",
	"problem.tickets=LPS-44599",
	"problem.section=#merged-configured-email-signature-field-into-the-body-of-email-messages-fro",
	 "version=7.0"
},
	service = FileMigrator.class)
public class EmailSignatureProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("message.boards.email.message.added.signature");
		properties.add("message.boards.email.message.updated.signature");
		properties.add("wiki.email.page.added.signature");
		properties.add("wiki.email.page.updated.signature");
	}

}