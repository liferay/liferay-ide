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
import com.liferay.blade.upgrade.liferay70.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=jsp,jspf",
	"problem.title=Removed the liferay-ui:trash-undo Tag and Replaced with liferay-trash:undo",
	"problem.section=#removed-the-liferay-uitrash-undo-tag-and-replaced-with-liferay-trashundo",
	"problem.summary=Removed the liferay-ui:trash-undo Tag and Replaced with liferay-trash:undo",
	"problem.tickets=LPS-60779", "auto.correct=jsptag", "implName=DeprecatedTrashUndoTags"
},
	service = {AutoMigrator.class, FileMigrator.class})
public class DeprecatedTrashUndoTags extends JSPTagMigrator {

	public DeprecatedTrashUndoTags() {
		super(new String[0], new String[0], new String[0], new String[0], _TAG_NAMES, _NEW_TAG_NAMES);
	}

	private static final String[] _NEW_TAG_NAMES = {"liferay-trash:undo"};

	private static final String[] _TAG_NAMES = {"liferay-ui:trash-undo"};

}